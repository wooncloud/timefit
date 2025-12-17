package timefit.businesscategory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.BusinessTypeCode;
import timefit.businesscategory.dto.BusinessCategoryRequestDto;
import timefit.businesscategory.dto.BusinessCategoryResponseDto;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryFacadeService {

    private final BusinessCategoryCommandService commandService;
    private final BusinessCategoryQueryService queryService;

    /**
     * 업체의 모든 카테고리 목록 조회 (활성/비활성 포함)
     *
     * @param businessId 업체 ID
     * @return 카테고리 목록
     */
    public BusinessCategoryResponseDto.CategoryList getCategoryList(UUID businessId) {
        log.debug("Facade: 카테고리 목록 조회 - businessId={}", businessId);
        return queryService.getCategoryList(businessId);
    }

    /**
     * 특정 업종의 카테고리 목록 조회
     *
     * @param businessId 업체 ID
     * @param businessType 업종 코드
     * @return 카테고리 목록
     */
    public BusinessCategoryResponseDto.CategoryList getCategoryListByType(
            UUID businessId,
            BusinessTypeCode businessType) {

        log.debug("Facade: 업종별 카테고리 목록 조회 - businessId={}, businessType={}",
                businessId, businessType);

        return queryService.getCategoryListByType(businessId, businessType);
    }

    /**
     * 카테고리 상세 조회
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @return 카테고리 상세 정보
     */
    public BusinessCategoryResponseDto.Category getCategory(
            UUID businessId,
            UUID categoryId) {

        log.debug("Facade: 카테고리 상세 조회 - businessId={}, categoryId={}",
                businessId, categoryId);

        return queryService.getCategory(businessId, categoryId);
    }

    /**
     * 카테고리 생성
     *
     * @param businessId 업체 ID
     * @param request 생성 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 생성된 카테고리 정보
     */
    @Transactional
    public BusinessCategoryResponseDto.Category createCategory(
            UUID businessId,
            BusinessCategoryRequestDto.CreateCategory request,
            UUID currentUserId) {

        log.debug("Facade: 카테고리 생성 - businessId={}, userId={}",
                businessId, currentUserId);

        return commandService.createCategory(businessId, request, currentUserId);
    }

    /**
     * 카테고리 수정
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @param request 수정 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 수정된 카테고리 정보
     */
    @Transactional
    public BusinessCategoryResponseDto.Category updateCategory(
            UUID businessId,
            UUID categoryId,
            BusinessCategoryRequestDto.UpdateCategory request,
            UUID currentUserId) {

        log.debug("Facade: 카테고리 수정 - businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        return commandService.updateCategory(businessId, categoryId, request, currentUserId);
    }

    /**
     * 카테고리 삭제 (논리 삭제)
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @param currentUserId 현재 사용자 ID
     */
    @Transactional
    public void deleteCategory(
            UUID businessId,
            UUID categoryId,
            UUID currentUserId) {

        log.debug("Facade: 카테고리 삭제 - businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        commandService.deleteCategory(businessId, categoryId, currentUserId);
    }
}