package timefit.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import timefit.business.entity.BusinessTypeCode;
import timefit.menu.entity.OrderType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "메뉴 응답")
public class MenuResponseDto {

    /**
     * 메뉴 상세 정보
     * - 단일 메뉴 조회, 생성/수정 응답에 사용
     */
    @Schema(description = "메뉴 상세")
    public record Menu(
            @Schema(
                    description = "메뉴 ID",
                    example = "10000000-0000-0000-0000-000000000001"
            )
            UUID menuId,

            @Schema(
                    description = "업체 ID",
                    example = "30000000-0000-0000-0000-000000000001"
            )
            UUID businessId,

            @Schema(
                    description = "서비스명",
                    example = "헤어 컷"
            )
            String serviceName,

            @Schema(
                    description = "카테고리 ID",
                    example = "60000000-0000-0000-0000-000000000001"
            )
            UUID businessCategoryId,

            @Schema(
                    description = "업종 코드",
                    example = "BD008",
                    allowableValues = {"BD000", "BD001", "BD002", "BD003", "BD004", "BD005", "BD006", "BD007", "BD008", "BD009", "BD010", "BD011", "BD012", "BD013"}
            )
            BusinessTypeCode businessType,

            @Schema(
                    description = "카테고리명",
                    example = "헤어"
            )
            String categoryName,

            @Schema(
                    description = "가격",
                    example = "30000"
            )
            Integer price,

            @Schema(
                    description = "서비스 설명",
                    example = "기본 헤어 컷 서비스입니다",
                    nullable = true
            )
            String description,

            @Schema(
                    description = "서비스 유형",
                    example = "RESERVATION_BASED",
                    allowableValues = {"RESERVATION_BASED", "ONDEMAND_BASED"}
            )
            OrderType orderType,

            @Schema(
                    description = "소요 시간 (분 단위)",
                    example = "60",
                    nullable = true
            )
            Integer durationMinutes,

            @Schema(
                    description = "이미지 URL",
                    example = "https://example.com/image.jpg",
                    nullable = true
            )
            String imageUrl,

            @Schema(
                    description = "활성화 상태 (true: 활성, false: 비활성)",
                    example = "true"
            )
            Boolean isActive,

            @Schema(
                    description = "생성 일시",
                    example = "2025-11-23T10:00:00"
            )
            LocalDateTime createdAt,

            @Schema(
                    description = "최종 수정 일시",
                    example = "2025-11-23T15:30:00"
            )
            LocalDateTime updatedAt
    ) {
        // Entity → DTO 변환
        public static Menu from(timefit.menu.entity.Menu menu) {
            return new Menu(
                    menu.getId(),
                    menu.getBusiness().getId(),
                    menu.getServiceName(),
                    menu.getBusinessCategory().getId(),
                    menu.getBusinessCategory().getBusinessType(),
                    menu.getBusinessCategory().getCategoryName(),
                    menu.getPrice(),
                    menu.getDescription(),
                    menu.getOrderType(),
                    menu.getDurationMinutes(),
                    menu.getImageUrl(),
                    menu.getIsActive(),
                    menu.getCreatedAt(),
                    menu.getUpdatedAt()
            );
        }
    }

    /**
     * 메뉴 목록 응답
     * - Menu를 재사용하여 DTO 개수 최소화
     */
    @Schema(description = "메뉴 목록")
    public record MenuList(
            @Schema(description = "메뉴 배열")
            List<Menu> menus,

            @Schema(
                    description = "총 메뉴 개수",
                    example = "5"
            )
            Integer totalCount
    ) {
        // Entity List → DTO 변환
        public static MenuList of(List<timefit.menu.entity.Menu> menuEntities) {
            List<Menu> menus = menuEntities.stream()
                    .map(Menu::from)
                    .toList();

            return new MenuList(menus, menus.size());
        }
    }

    /**
     * 메뉴 삭제 결과
     */
    @Schema(description = "메뉴 삭제 결과")
    public record DeleteResult(
            @Schema(description = "삭제된 메뉴 ID", example = "4bc7b5db-47dd-4d23-9e0f-f5b820ae1e43")
            UUID menuId,

            @Schema(description = "삭제된 메뉴명", example = "헤어컷")
            String menuName,

            @Schema(description = "결과 메시지", example = "메뉴가 성공적으로 삭제되었습니다")
            String message,

            @Schema(description = "삭제 시간", example = "2026-01-21T02:31:38")
            LocalDateTime deletedAt
    ) {
        public static DeleteResult of(UUID menuId, String menuName, String message) {
            return new DeleteResult(
                    menuId,
                    menuName,
                    message,
                    LocalDateTime.now()
            );
        }
    }
}