package timefit.menu.service.factory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.businesscategory.service.validator.BusinessCategoryValidator;
import timefit.menu.dto.MenuRequestDto;
import timefit.menu.entity.Menu;
import timefit.menu.entity.OrderType;

// 주문 타입에 따른 Menu Entity 생성 팩토리
@Slf4j
@Component
@RequiredArgsConstructor
public class MenuEntityFactory {

    private final BusinessCategoryValidator businessCategoryValidator;

    /**
     * Menu Entity 생성
     * 1. BusinessCategory 조회 및 검증
     * 2. OrderType에 따라 적절한 정적 팩토리 메서드 호출
     */
    public Menu createMenu(Business business, MenuRequestDto.CreateUpdateMenu request) {
        log.debug("Menu Entity 생성 시작: businessId={}, orderType={}",
                business.getId(), request.orderType());

        // 1. BusinessCategory 조회 및 검증
        BusinessCategory businessCategory = businessCategoryValidator.validateAndGetBusinessCategory(
                business.getId(),
                request.businessType(),
                request.categoryName()
        );

        // 2. OrderType에 따라 Menu 생성
        Menu menu;
        if (OrderType.RESERVATION_BASED.equals(request.orderType())) {
            menu = Menu.createReservationBased(
                    business,
                    businessCategory,
                    request.serviceName(),
                    request.price(),
                    request.description(),
                    request.durationMinutes(),
                    request.imageUrl()
            );
            log.debug("예약형 메뉴 생성: serviceName={}", request.serviceName());
        } else {
            menu = Menu.createOnDemandBased(
                    business,
                    businessCategory,
                    request.serviceName(),
                    request.price(),
                    request.description(),
                    request.durationMinutes(),
                    request.imageUrl()
            );
            log.debug("주문형 메뉴 생성: serviceName={}", request.serviceName());
        }

        return menu;
    }
}