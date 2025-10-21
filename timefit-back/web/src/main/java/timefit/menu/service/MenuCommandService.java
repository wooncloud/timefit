package timefit.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.service.validator.BusinessValidator;
import timefit.menu.dto.MenuRequest;
import timefit.menu.dto.MenuResponse;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;
import timefit.menu.repository.MenuRepository;
import timefit.menu.service.validator.MenuValidator;

import java.util.UUID;

/**
 * Menu CUD 전담 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuCommandService {

    private final MenuRepository menuRepository;
    private final BusinessValidator businessValidator;
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

        // 1. 권한 검증 (BusinessValidator 사용)
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu Entity 생성 (정적 팩토리)
        Menu menu;
        if (OrderType.RESERVATION_BASED.equals(request.getOrderType())) {
            menu = Menu.createReservationBased(
                    business,
                    request.getServiceName(),
                    request.getCategory(),
                    request.getPrice(),
                    request.getDescription(),
                    request.getDurationMinutes(),  // ✅ 추가
                    request.getImageUrl()
            );
        } else {
            menu = Menu.createOnDemandBased(
                    business,
                    request.getServiceName(),
                    request.getCategory(),
                    request.getPrice(),
                    request.getDescription(),
                    request.getDurationMinutes(),  // ✅ 추가
                    request.getImageUrl()
            );
        }

        // 3. 저장
        Menu savedMenu = menuRepository.save(menu);

        log.info("메뉴 생성 완료: menuId={}, serviceName={}",
                savedMenu.getId(), savedMenu.getServiceName());

        // 4. DTO 변환
        return MenuResponse.of(savedMenu);
    }

    /**
     * 메뉴 수정
     */
    public MenuResponse updateMenu(
            UUID businessId,
            UUID menuId,
            MenuRequest.UpdateMenu request,
            UUID currentUserId) {

        log.info("메뉴 수정 시작: businessId={}, menuId={}, userId={}",
                businessId, menuId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. Menu 조회 및 검증 (MenuValidator 사용)
        Menu menu = menuValidator.validateMenuOfBusiness(menuId, businessId);

        // 3. Menu 수정
        if (request.getServiceName() != null ||
                request.getCategory() != null ||
                request.getPrice() != null ||
                request.getDescription() != null) {

            menu.updateBasicInfo(
                    request.getServiceName(),
                    request.getCategory(),
                    request.getPrice(),
                    request.getDescription()
            );
        }

        // ✅ 소요 시간 수정 (별도 메서드)
        if (request.getDurationMinutes() != null) {
            menu.updateDuration(request.getDurationMinutes());
        }

        // ✅ 이미지 URL 수정 (별도 메서드)
        if (request.getImageUrl() != null) {
            menu.updateImageUrl(request.getImageUrl());
        }

        log.info("메뉴 수정 완료: menuId={}", menuId);

        // 4. DTO 변환 (save 불필요, 영속성 컨텍스트가 관리)
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
        menu.updateActiveStatus(false);

        log.info("메뉴 삭제 완료: menuId={}", menuId);
    }
}