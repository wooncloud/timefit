package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.dto.BusinessResponse;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.BusinessRepositoryCustom;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.business.service.validator.BusinessValidator;
import timefit.exception.business.BusinessErrorCode;
import timefit.exception.business.BusinessException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Business 조회 전담 서비스
 * - 모든 조회(Read) 작업 처리
 * - @Transactional(readOnly = true) 적용
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessQueryService {

    private final BusinessRepository businessRepository;
    private final BusinessRepositoryCustom businessRepositoryCustom;
    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final BusinessValidator businessValidator;

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     */
    public List<BusinessResponse.BusinessSummary> getMyBusinesses(UUID userId) {
        log.info("내 업체 목록 조회 시작: userId={}", userId);

        List<UserBusinessRole> userBusinessRoles = userBusinessRoleRepository
                .findByUserIdAndIsActive(userId, true);

        if (userBusinessRoles.isEmpty()) {
            log.info("소속 업체 없음: userId={}", userId);
            return List.of();
        }

        List<BusinessResponse.BusinessSummary> result = userBusinessRoles.stream()
                .map(role -> BusinessResponse.BusinessSummary.of(
                        role.getBusiness(),
                        role.getRole()
                ))
                .collect(Collectors.toList());

        log.info("내 업체 목록 조회 완료: userId={}, count={}", userId, result.size());
        return result;
    }

    /**
     * 업체 상세 정보 조회 (공개 - 인증 불필요)
     * 권한: 누구나 조회 가능
     */
    public BusinessResponse.PublicBusinessDetail getBusinessDetail(UUID businessId) {
        log.info("업체 상세 조회 시작: businessId={}", businessId);

        Business business = businessValidator.validateActiveBusinessExists(businessId);

        BusinessResponse.PublicBusinessDetail response = BusinessResponse.PublicBusinessDetail.of(business);

        log.info("업체 상세 조회 완료: businessId={}, businessName={}",
                businessId, business.getBusinessName());

        return response;
    }

    /**
     * 업체 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER (해당 업체에 속한 사용자만)
     */
    public BusinessResponse.MembersListResult getMembersList(UUID businessId, UUID currentUserId) {
        log.info("구성원 목록 조회 시작: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 사용자 권한 확인 (해당 업체의 구성원인지)
        businessValidator.validateUserBusinessRole(currentUserId, businessId);

        // 3. 활성 구성원 목록 조회
        List<UserBusinessRole> members = userBusinessRoleRepository
                .findByBusinessIdAndIsActive(businessId, true);

        BusinessResponse.MembersListResult response = BusinessResponse.MembersListResult.of(business, members);

        log.info("구성원 목록 조회 완료: businessId={}, memberCount={}",
                businessId, members.size());

        return response;
    }

    /**
     * 업체 검색 (페이징)
     * 권한: 누구나 검색 가능
     */
    public BusinessResponse.BusinessSearchResult searchBusinesses(
            String keyword,
            BusinessTypeCode businessType,
            String region,
            int page,
            int size) {

        log.info("업체 검색 시작: keyword={}, businessType={}, region={}, page={}, size={}",
                keyword, businessType, region, page, size);

        // 페이징 검증
        validateSearchParameters(page, size);

        // 검색
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("businessName").ascending());
        Page<Business> businessPage = businessRepositoryCustom.searchBusinesses(
                keyword, businessType, region, pageRequest);

        if (businessPage.isEmpty()) {
            log.info("검색 결과 없음: keyword={}, businessType={}, region={}", keyword, businessType, region);
            return BusinessResponse.BusinessSearchResult.empty(page, size);
        }

        BusinessResponse.BusinessSearchResult response = BusinessResponse.BusinessSearchResult.of(
                businessPage.getContent(),
                (int) businessPage.getTotalElements(),
                businessPage.getTotalPages(),
                page,
                size
        );

        log.info("업체 검색 완료: totalElements={}, totalPages={}, currentPage={}",
                businessPage.getTotalElements(), businessPage.getTotalPages(), page);

        return response;
    }

    /**
     * 검색 파라미터 검증
     */
    private void validateSearchParameters(int page, int size) {
        if (page < 0) {
            throw new BusinessException(BusinessErrorCode.INVALID_PAGE_NUMBER);
        }
        if (size <= 0 || size > 100) {
            throw new BusinessException(BusinessErrorCode.INVALID_PAGE_SIZE);
        }
    }
}