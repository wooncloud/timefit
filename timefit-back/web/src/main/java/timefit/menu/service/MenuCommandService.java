package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.ServiceCategoryCode;
import timefit.business.repository.BusinessCategoryRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCommandService {

    private final MenuRepository menuRepository;
    private final BusinessCategoryRepository businessCategoryRepository;
    private final BusinessValidator businessValidator;
    private final BusinessCategoryValidator businessCategoryValidator;
    private final MenuValidator menuValidator;

    /**
     * 메뉴 생성
     * 1. businessType 검증 (Business.businessTypes에 포함)
     * 2. categoryCode 검증 (businessType에 속하는지)
     * 3. BusinessCategory 자동 생성/조회
     * 4. Menu 생성 시 BusinessCategory 전달
     */
    public MenuResponse createMenu(
            UUID businessId,
            MenuRequest.CreateMenu request,
            UUID currentUserId) {

        log.info("메뉴 생성 시작: businessId={}, userId={}, serviceName={}",
                businessId, currentUserId, request.getServiceName());

        // 1. 권한 검증 및 Business 조회
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. businessType 검증: Business.businessTypes에 포함되는지 확인
        if (!business.getBusinessTypes().contains(request.getBusinessType())) {
            throw new BusinessCategoryException(
                    BusinessCategoryErrorCode.CATEGORY_NOT_FOUND);
        }

        // 3. categoryCode 검증: businessType에 속하는지 확인
        businessCategoryValidator.validateCategoryCodeBelongsToBusinessType(
                request.getBusinessType(),
                request.getCategoryCode()
        );

        // 4. BusinessCategory 찾기 또는 자동 생성 (핵심!)
        BusinessCategory businessCategory = getBusinessCategory(
                business,
                request.getBusinessType(),
                request.getCategoryCode()
        );

        // 5. Menu Entity 생성 (businessCategory 전달)
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

        // 6. 저장
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 생성 완료: menuId={}, serviceName={}, businessCategoryId={}",
                savedMenu.getId(), savedMenu.getServiceName(), businessCategory.getId());

        // 7. DTO 변환
        return MenuResponse.of(savedMenu);
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

        log.info("메뉴 수정 완료: menuId={}", menuId);

        // 7. DTO 변환 (save 불필요, 영속성 컨텍스트가 관리)
        return MenuResponse.of(menu);
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