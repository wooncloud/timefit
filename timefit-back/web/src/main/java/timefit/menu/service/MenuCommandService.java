package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.business.service.validator.BusinessCategoryValidator;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.businesscategory.BusinessCategoryErrorCode;
import timefit.exception.businesscategory.BusinessCategoryException;
import timefit.menu.dto.MenuRequest;
import timefit.menu.dto.MenuResponse;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;
import timefit.menu.service.validator.MenuValidator;

import java.util.UUID;

/**
 * Menu CUD 전담 서비스
 * - 생성, 수정, 삭제 (논리 삭제)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCommandService {

    private final MenuRepository menuRepository;
    private final BusinessValidator businessValidator;
    private final BusinessCategoryValidator businessCategoryValidator;
    private final MenuValidator menuValidator;

    /**
     * 메뉴 생성
     */
    public MenuResponse createMenu(
            UUID businessId,
            MenuRequest.CreateMenu request,
            UUID currentUserId) {

        log.info("메뉴 생성 시작: businessId={}, userId={}, serviceName={}",
                businessId, currentUserId, request.getServiceName());

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. BusinessCategory 조회 및 검증
        BusinessCategory businessCategory = getBusinessCategory(
                business,
                request.getBusinessType(),
                request.getCategoryCode()
        );

        // 3. Menu Entity 생성 (정적 팩토리)
        Menu menu;
        if (OrderType.RESERVATION_BASED.equals(request.getOrderType())) {
            menu = Menu.createReservationBased(
                    business,
                    businessCategory,
                    request.getServiceName(),
                    request.getPrice(),
                    request.getDescription(),
                    request.getDurationMinutes(),
                    request.getImageUrl()
            );
        } else {
            menu = Menu.createOnDemandBased(
                    business,
                    businessCategory,
                    request.getServiceName(),
                    request.getPrice(),
                    request.getDescription(),
                    request.getDurationMinutes(),
                    request.getImageUrl()
            );
        }

        // 4. 저장
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 생성 완료: menuId={}, serviceName={}", savedMenu.getId(), savedMenu.getServiceName());

        return MenuResponse.from(savedMenu);
    }

    /**
     * BusinessCategory 조회 및 검증
     * - BusinessCategory는 미리 존재해야 함
     * - 없으면 예외 발생
     *
     * @param business 업체
     * @param businessType 대분류 (업종)
     * @param categoryCode 중분류 (서비스 카테고리)
     * @return BusinessCategory (DB에 존재하는 것만 반환)
     * @throws BusinessCategoryException 카테고리가 존재하지 않을 경우
     */
    private BusinessCategory getBusinessCategory(
            Business business,
            BusinessTypeCode businessType,
            ServiceCategoryCode categoryCode) {

        return businessCategoryValidator.validateAndGetBusinessCategory(
                business.getId(),
                businessType,
                categoryCode
        );
    }

    /**
     * 메뉴 수정
     * - businessType, categoryCode 수정 시 BusinessCategory 변경
     */
    public MenuResponse updateMenu(
            UUID businessId,
            UUID menuId,
            MenuRequest.UpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 수정 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. businessType 또는 categoryCode 변경 시 BusinessCategory 변경
        if (request.getBusinessType() != null || request.getCategoryCode() != null) {

            // 현재 BusinessCategory 정보
            BusinessCategory currentCategory = menu.getBusinessCategory();
            BusinessTypeCode targetBusinessType = request.getBusinessType() != null ?
                    request.getBusinessType() : currentCategory.getBusinessType();
            ServiceCategoryCode targetCategoryCode = request.getCategoryCode() != null ?
                    request.getCategoryCode() : currentCategory.getCategoryCode();

            // businessType 검증
            if (!business.getBusinessTypes().contains(targetBusinessType)) {
                throw new BusinessCategoryException(
                        BusinessCategoryErrorCode.CATEGORY_NOT_FOUND);
            }

            // categoryCode 검증
            businessCategoryValidator.validateCategoryCodeBelongsToBusinessType(
                    targetBusinessType,
                    targetCategoryCode
            );

            // BusinessCategory 찾기 또는 생성
            BusinessCategory newCategory = getBusinessCategory(
                    business,
                    targetBusinessType,
                    targetCategoryCode
            );

            // Menu의 BusinessCategory 변경
            menu.updateBusinessCategory(newCategory);
        }

        // 4. 기본 정보 수정
        if (request.getServiceName() != null ||
                request.getPrice() != null ||
                request.getDescription() != null) {

            menu.updateBasicInfo(
                    request.getServiceName(),
                    request.getPrice(),
                    request.getDescription()
            );
        }

        // 5. 소요 시간 수정
        if (request.getDurationMinutes() != null) {
            menu.updateDuration(request.getDurationMinutes());
        }

        // 6. 이미지 URL 수정
        if (request.getImageUrl() != null) {
            menu.updateImageUrl(request.getImageUrl());
        }

        // 7. 활성/비활성 상태 변경 (추가됨)
        if (request.getIsActive() != null &&
                !request.getIsActive().equals(menu.getIsActive())) {
            if (request.getIsActive()) {
                menu.activate();
            } else {
                menu.deactivate();
            }
        }

        log.info("메뉴 수정 완료: menuId={}", menuId);

        // 8. DTO 변환 ( from() 사용, save 불필요 - 영속성 컨텍스트가 관리)
        return MenuResponse.from(menu);
    }

    /**
     * 메뉴 활성/비활성 토글
     * 현재 상태의 반대로 전환
     *
     * @param businessId 업체 ID
     * @param menuId 메뉴 ID
     * @param currentUserId 현재 사용자 ID
     * @return MenuResponse
     */
    public MenuResponse toggleMenuActive(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        log.info("메뉴 활성상태 토글 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. 활성상태 토글
        if (menu.getIsActive()) {
            menu.deactivate();
            log.info("메뉴 비활성화: menuId={}", menuId);
        } else {
            menu.activate();
            log.info("메뉴 활성화: menuId={}", menuId);
        }

        log.info("메뉴 활성상태 토글 완료: menuId={}, isActive={}", menuId, menu.getIsActive());

        // 4. DTO 변환
        return MenuResponse.from(menu);
    }

    /**
     * 메뉴 삭제 (논리 삭제)
     */
    public void deleteMenu(
            UUID businessId,
            UUID menuId,
            UUID currentUserId) {

        log.info("메뉴 삭제 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 검증
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. 비활성화 (논리 삭제)
        menu.deactivate();

        log.info("메뉴 삭제 완료: menuId={}", menuId);
    }
}