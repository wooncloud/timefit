package timefit.businesscategory.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessCategory;
import timefit.business.repository.BusinessCategoryRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.businesscategory.dto.BusinessCategoryRequest;
import timefit.businesscategory.dto.BusinessCategoryResponse;
import timefit.businesscategory.service.validator.BusinessCategoryValidator;

import java.util.UUID;

/**
 * BusinessCategory CUD 전담 서비스
 *
 * 담당 기능:
 * - 카테고리 생성
 * - 카테고리 수정
 * - 카테고리 삭제 (논리 삭제)
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCategoryCommandService {

    private final BusinessCategoryRepository businessCategoryRepository;
    private final BusinessValidator businessValidator;
    private final BusinessCategoryValidator businessCategoryValidator;

    /**
     * 카테고리 생성
     *
     * @param businessId 업체 ID
     * @param request 생성 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 생성된 카테고리 응답 DTO
     */
    public BusinessCategoryResponse createCategory(
            UUID businessId,
            BusinessCategoryRequest.CreateCategory request,
            UUID currentUserId) {

        log.info("카테고리 생성 시작: businessId={}, userId={}, categoryName={}",
                businessId, currentUserId, request.getCategoryName());

        // 1. 권한 검증 (OWNER 또는 MANAGER)
        Business business = businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 카테고리명 형식 검증
        businessCategoryValidator.validateCategoryNameFormat(request.getCategoryName());

        // 3. 중복 검증
        businessCategoryValidator.validateNoDuplicateCategory(
                businessId,
                request.getBusinessType(),
                request.getCategoryName()
        );

        // 4. Entity 생성 (정적 팩토리)
        BusinessCategory category = BusinessCategory.create(
                business,
                request.getBusinessType(),
                request.getCategoryName().trim(),
                request.getCategoryNotice()
        );

        // 5. 저장
        BusinessCategory savedCategory = businessCategoryRepository.save(category);

        log.info("카테고리 생성 완료: categoryId={}, categoryName={}",
                savedCategory.getId(), savedCategory.getCategoryName());

        // 6. DTO 변환
        return BusinessCategoryResponse.of(savedCategory);
    }

    /**
     * 카테고리 수정
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @param request 수정 요청 DTO
     * @param currentUserId 현재 사용자 ID
     * @return 수정된 카테고리 응답 DTO
     */
    public BusinessCategoryResponse updateCategory(
            UUID businessId,
            UUID categoryId,
            BusinessCategoryRequest.UpdateCategory request,
            UUID currentUserId) {

        log.info("카테고리 수정 시작: businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 카테고리 조회 및 소속 검증
        BusinessCategory category = businessCategoryValidator.validateExists(categoryId);
        businessCategoryValidator.validateCategoryBelongsToBusiness(category, businessId);

        // 3. 카테고리명 수정 시
        if (request.getCategoryName() != null && !request.getCategoryName().isBlank()) {
            // 형식 검증
            businessCategoryValidator.validateCategoryNameFormat(request.getCategoryName());

            // 중복 검증 (자기 자신 제외)
            businessCategoryValidator.validateNoDuplicateCategoryForUpdate(
                    categoryId,
                    businessId,
                    category.getBusinessType(),
                    request.getCategoryName()
            );

            // 카테고리명 업데이트
            category.updateCategoryName(request.getCategoryName().trim());
            log.info("카테고리명 변경: categoryId={}, newName={}", categoryId, request.getCategoryName());
        }

        // 4. 공지사항 수정
        if (request.getCategoryNotice() != null) {
            category.updateInfo(request.getCategoryNotice());
        }

        // 5. 활성화 상태 변경
        if (request.getIsActive() != null) {
            if (request.getIsActive()) {
                category.activate();
            } else {
                category.deactivate();
            }
            log.info("카테고리 활성화 상태 변경: categoryId={}, isActive={}",
                    categoryId, request.getIsActive());
        }

        log.info("카테고리 수정 완료: categoryId={}", categoryId);

        // 6. DTO 변환 (변경 감지로 저장은 자동)
        return BusinessCategoryResponse.of(category);
    }

    /**
     * 카테고리 삭제 (논리 삭제)
     *
     * @param businessId 업체 ID
     * @param categoryId 카테고리 ID
     * @param currentUserId 현재 사용자 ID
     */
    public void deleteCategory(
            UUID businessId,
            UUID categoryId,
            UUID currentUserId) {

        log.info("카테고리 삭제 시작: businessId={}, categoryId={}, userId={}",
                businessId, categoryId, currentUserId);

        // 1. 권한 검증
        businessValidator.validateBusinessAccess(currentUserId, businessId);

        // 2. 카테고리 조회 및 소속 검증
        BusinessCategory category = businessCategoryValidator.validateExists(categoryId);
        businessCategoryValidator.validateCategoryBelongsToBusiness(category, businessId);

        // 3. 삭제 가능 여부 검증 (활성 메뉴 확인)
        businessCategoryValidator.validateCategoryDeletable(categoryId);

        // 4. 비활성화 (논리 삭제)
        category.deactivate();

        log.info("카테고리 삭제 완료: categoryId={}", categoryId);
    }
}