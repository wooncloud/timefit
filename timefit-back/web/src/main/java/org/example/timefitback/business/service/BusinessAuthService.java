package org.example.timefitback.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.timefitback.business.dto.BusinessAuthDto;
import org.example.timefitback.business.entity.Business;
import org.example.timefitback.business.entity.User;
import org.example.timefitback.business.repository.BusinessRepository;
import org.example.timefitback.business.repository.UserRepository;
import org.example.timefitback.common.exception.BusinessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessAuthService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public BusinessAuthDto.AuthResponse signUp(BusinessAuthDto.SignUpRequest request) {
        log.info("업체 회원가입 시작: email={}, businessName={}", request.getEmail(), request.getBusinessName());

        request.normalizePhoneNumber();
        validateDuplicateData(request);

        // check reference again :(
        try {
            // 사용자 생성
            User user = User.createBusinessUser(
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getName(),
                    request.getPhoneNumber()
            );
            User savedUser = userRepository.save(user);
            log.info("사용자 생성 완료: userId={}", savedUser.getId());

            // 비즈니스 정보 생성
            Business business = Business.createBusiness(
                    savedUser.getId(),
                    request.getBusinessName(),
                    request.getBusinessType(),
                    request.getBusinessNumber(),
                    request.getAddress(),
                    request.getContactPhone(),
                    request.getDescription()
            );
            Business savedBusiness = businessRepository.save(business);
            log.info("업체 정보 생성 완료: businessId={}", savedBusiness.getId());

            return createAuthResponse(savedUser, savedBusiness);

        } catch (Exception e) {
            log.error("업체 회원가입 실패: email={}, error={}", request.getEmail(), e.getMessage());
            throw new BusinessException(BusinessException.BusinessErrorCode.INTERNAL_SERVER_ERROR,
                    "회원가입 처리 중 오류가 발생했습니다");
        }
    }

    @Transactional
    public BusinessAuthDto.AuthResponse signIn(BusinessAuthDto.SignInRequest request) {
        log.info("업체 로그인 시도: email={}", request.getEmail());

        try {
            // 사용자 검증
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.INVALID_CREDENTIALS));

            // 비밀번호 검증
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                log.warn("로그인 실패 - 잘못된 비밀번호: email={}", request.getEmail());
                throw new BusinessException(BusinessException.BusinessErrorCode.INVALID_CREDENTIALS);
            }

            // 업체 정보 조회
            Business business = businessRepository.findByUserId(user.getId())
                    .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.BUSINESS_NOT_FOUND));

            // 마지막 로그인 시간 업데이트
            user.updateLastLoginAt();
            userRepository.save(user);

            log.info("업체 로그인 성공: userId={}, businessId={}", user.getId(), business.getId());

            return createAuthResponse(user, business);

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("업체 로그인 실패: email={}, error={}", request.getEmail(), e.getMessage());
            throw new BusinessException(BusinessException.BusinessErrorCode.INTERNAL_SERVER_ERROR,
                    "로그인 처리 중 오류가 발생했습니다");
        }
    }

    private void validateDuplicateData(BusinessAuthDto.SignUpRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("회원가입 실패 - 이메일 중복: email={}", request.getEmail());
            throw new BusinessException(BusinessException.BusinessErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 사업자번호 중복 검사
        if (businessRepository.existsByBusinessNumber(request.getBusinessNumber())) {
            log.warn("회원가입 실패 - 사업자번호 중복: businessNumber={}", request.getBusinessNumber());
            throw new BusinessException(BusinessException.BusinessErrorCode.BUSINESS_NUMBER_ALREADY_EXISTS);
        }
    }

    private BusinessAuthDto.AuthResponse createAuthResponse(User user, Business business) {
        BusinessAuthDto.BusinessInfo businessInfo = BusinessAuthDto.BusinessInfo.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getBusinessNumber(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                business.isBasicInfoComplete(),
                business.getCreatedAt(),
                business.getUpdatedAt()
        );

        return BusinessAuthDto.AuthResponse.of(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getRole().name(),
                businessInfo,
                user.getLastLoginAt()
        );
    }
}