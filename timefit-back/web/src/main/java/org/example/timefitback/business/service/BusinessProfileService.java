package org.example.timefitback.business.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.timefitback.business.dto.BusinessProfileDto;
import org.example.timefitback.business.entity.Business;
import org.example.timefitback.business.entity.User;
import org.example.timefitback.business.repository.BusinessRepository;
import org.example.timefitback.business.repository.UserRepository;
import org.example.timefitback.common.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessProfileService {

    private final UserRepository userRepository;
    private final BusinessRepository businessRepository;

    // 업체 정보 조회
    @Transactional(readOnly = true)
    public BusinessProfileDto.ProfileResponse getBusinessProfile(UUID userId) {
        log.info("업체 정보 조회: userId={}", userId);

        // 사용자 정보 조회
        User user = userRepository.findBusinessUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.USER_NOT_FOUND));

        // 업체 정보 조회
        Business business = businessRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.BUSINESS_NOT_FOUND));

        return BusinessProfileDto.ProfileResponse.of(
                business.getId(),
                business.getBusinessName(),
                business.getBusinessType(),
                business.getBusinessNumber(),
                business.getAddress(),
                business.getContactPhone(),
                business.getDescription(),
                business.getLogoUrl(),
                business.getCreatedAt(),
                business.getUpdatedAt(),
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getPhoneNumber(),
                user.getProfileImageUrl()
        );
    }

    // 업체 정보 생성 (Create)
    @Transactional
    public BusinessProfileDto.ProfileResponse createBusinessProfile(UUID userId, BusinessProfileDto.UpdateRequest request) {
        log.info("업체 정보 생성: userId={}", userId);

        // 전화번호 정규화
        request.normalizePhoneNumber();

        // 사용자 존재 확인
        User user = userRepository.findBusinessUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.USER_NOT_FOUND));

        // 이미 업체 정보가 있는지 확인
        if (businessRepository.existsByUserId(userId)) {
            throw new BusinessException(BusinessException.BusinessErrorCode.BUSINESS_ALREADY_EXISTS);
        }

        // 사용자 정보 업데이트
        user.updateProfile(request.getUserName(), request.getUserPhoneNumber(), request.getProfileImageUrl());
        userRepository.save(user);

        // 업체 정보 생성
        Business business = Business.createBusiness(
                userId,
                request.getBusinessName(),
                request.getBusinessType(),
                "000-00-00000", // 임시 사업자번호 (별도 API에서 관리)
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription()
        );
        business.updateBusinessInfo(null, null, null, null, null, request.getLogoUrl());

        Business savedBusiness = businessRepository.save(business);

        log.info("업체 정보 생성 완료: businessId={}", savedBusiness.getId());

        return getBusinessProfile(userId);
    }

    // 업체 정보 수정 (Update)
    @Transactional
    public BusinessProfileDto.ProfileResponse updateBusinessProfile(UUID userId, BusinessProfileDto.UpdateRequest request) {
        log.info("업체 정보 수정: userId={}", userId);

        // 전화번호 정규화
        request.normalizePhoneNumber();

        // 사용자 정보 조회 및 수정
        User user = userRepository.findBusinessUserById(userId)
                .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.USER_NOT_FOUND));

        user.updateProfile(request.getUserName(), request.getUserPhoneNumber(), request.getProfileImageUrl());

        // 업체 정보 조회 및 수정
        Business business = businessRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(BusinessException.BusinessErrorCode.BUSINESS_NOT_FOUND));

        business.updateBusinessInfo(
                request.getBusinessName(),
                request.getBusinessType(),
                request.getAddress(),
                request.getContactPhone(),
                request.getDescription(),
                request.getLogoUrl()
        );

        // 변경사항 저장
        userRepository.save(user);
        businessRepository.save(business);

        log.info("업체 정보 수정 완료: businessId={}", business.getId());

        return getBusinessProfile(userId);
    }
}