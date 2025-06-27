package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.business.repository.BusinessRepository;
import timefit.business.repository.UserBusinessRoleRepository;
import timefit.exception.auth.AuthException;
import timefit.exception.auth.AuthErrorCode;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.factory.UserFactory;
import timefit.auth.factory.BusinessFactory;
import timefit.auth.factory.UserBusinessRoleFactory;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserRegistrationService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final UserBusinessRoleRepository userBusinessRoleRepository;

    /**
     * 업체 사용자 등록 (User + Business + Role 모두 생성)
     */
    @Transactional
    public UserRegistrationResult registerBusinessUser(AuthRequestDto.BusinessSignUp request) {

        // 1. 중복 체크
        validateDuplication(request);

        // 2. User 생성
        User user = UserFactory.createBusinessUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getPhoneNumber()
        );
        User savedUser = userRepository.save(user);

        // 3. Business 생성
        Business business = BusinessFactory.createBusiness(
                request.getBusinessName(),
                request.getBusinessType(),
                request.getBusinessNumber(),
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription()
        );
        Business savedBusiness = businessRepository.save(business);

        // 4. UserBusinessRole 생성 (OWNER 권한)
        UserBusinessRole userBusinessRole = UserBusinessRoleFactory.createOwner(savedUser, savedBusiness);
        UserBusinessRole savedRole = userBusinessRoleRepository.save(userBusinessRole);

        return UserRegistrationResult.of(savedUser, savedBusiness, savedRole);
    }

    /**
     * 중복 검증
     */
    private void validateDuplication(AuthRequestDto.BusinessSignUp request) {
        // 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(AuthErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사업자번호 중복 체크
        if (businessRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            throw new AuthException(AuthErrorCode.BUSINESS_NUMBER_ALREADY_EXISTS);
        }
    }
}