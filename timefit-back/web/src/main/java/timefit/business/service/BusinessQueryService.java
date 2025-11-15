package timefit.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.business.dto.BusinessResponseDto;
import timefit.business.entity.Business;
import timefit.business.entity.BusinessTypeCode;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepositoryCustom;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.business.service.validator.BusinessValidator;

import java.util.List;
import java.util.UUID;

/**
 * Business Query Service
 * - 조회 전용 로직 처리
 * - memberCount 제거
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusinessQueryService {

    private final UserBusinessRoleRepository userBusinessRoleRepository;
    private final BusinessRepositoryCustom businessRepositoryCustom;
    private final BusinessValidator businessValidator;

    /**
     * 공개 업체 상세 조회
     * 권한: 누구나 조회 가능
     */
    public BusinessResponseDto.PublicBusinessResponse getPublicBusinessDetail(UUID businessId) {
        log.info("공개 업체 상세 조회: businessId={}", businessId);

        Business business = businessValidator.validateBusinessExists(businessId);

        return BusinessResponseDto.PublicBusinessResponse.of(business);
    }

    /**
     * 사업자용 업체 상세 조회
     * 권한: OWNER/MANAGER/MEMBER
     */
    public BusinessResponseDto.BusinessResponse getBusinessProfile(
            UUID businessId,
            UUID currentUserId) {

        log.info("사업자용 업체 상세 조회: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 확인
        UserBusinessRole userRole = businessValidator.validateUserBusinessRole(currentUserId, businessId);

        return BusinessResponseDto.BusinessResponse.of(business, userRole);
    }

    /**
     * 내가 속한 업체 목록 조회
     * 권한: 로그인한 사용자 본인
     */
    public BusinessResponseDto.BusinessListResponse getMyBusinesses(UUID currentUserId) {
        log.info("내 업체 목록 조회: userId={}", currentUserId);

        // 내가 속한 활성 권한 조회
        List<UserBusinessRole> userRoles = userBusinessRoleRepository
                .findByUserIdAndIsActive(currentUserId, true);

        return BusinessResponseDto.BusinessListResponse.of(userRoles);
    }

    /**
     * 구성원 목록 조회
     * 권한: OWNER, MANAGER, MEMBER
     */
    public BusinessResponseDto.MemberListResponse getMembersList(
            UUID businessId,
            UUID currentUserId) {

        log.info("구성원 목록 조회: businessId={}, userId={}", businessId, currentUserId);

        // 1. 업체 존재 확인
        Business business = businessValidator.validateBusinessExists(businessId);

        // 2. 권한 확인
        businessValidator.validateUserBusinessRole(currentUserId, businessId);

        // 3. 활성 구성원 조회
        List<UserBusinessRole> userRoles = userBusinessRoleRepository
                .findByBusinessIdAndIsActiveOrderByJoinedAtAsc(businessId, true);

        return BusinessResponseDto.MemberListResponse.of(business, userRoles);
    }

    public BusinessResponseDto.BusinessListResponse searchBusinesses(
            String keyword,
            BusinessTypeCode businessType,
            String region,
            int page,
            int size) {

        log.info("업체 검색: keyword={}, type={}, region={}", keyword, businessType, region);

        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Business> businessPage = businessRepositoryCustom.searchBusinesses(
                keyword, businessType, region, pageRequest);

        // DTO 변환
        List<BusinessResponseDto.BusinessListResponse.BusinessItem> items =
                businessPage.getContent().stream()
                        .map(business -> new BusinessResponseDto.BusinessListResponse.BusinessItem(
                                business.getId(),
                                business.getBusinessName(),
                                business.getBusinessTypes(),
                                business.getLogoUrl(),
                                null,  // myRole - 검색에서는 null
                                null,  // joinedAt - 검색에서는 null
                                business.getIsActive()
                        ))
                        .toList();

        return new BusinessResponseDto.BusinessListResponse(
                items,
                (int) businessPage.getTotalElements()
        );
    }
}