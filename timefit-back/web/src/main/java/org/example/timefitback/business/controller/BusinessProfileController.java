package org.example.timefitback.business.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.timefitback.business.dto.BusinessProfileDto;
import org.example.timefitback.business.service.BusinessProfileService;
import org.example.timefitback.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/business/profile")
@RequiredArgsConstructor
public class BusinessProfileController {

    private final BusinessProfileService businessProfileService;

    /**
     * 업체 정보 조회
     * GET /api/business/profile/{userId}
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<BusinessProfileDto.ProfileResponse>> getBusinessProfile(
            @PathVariable UUID userId) {

        log.info("업체 정보 조회 요청: userId={}", userId);

        BusinessProfileDto.ProfileResponse response = businessProfileService.getBusinessProfile(userId);

        return ResponseEntity.ok(
                ApiResponse.success("업체 정보 조회가 완료되었습니다", response)
        );
    }

    /**
     * 업체 정보 생성
     * POST /api/business/profile/{userId}
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<BusinessProfileDto.ProfileResponse>> createBusinessProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody BusinessProfileDto.UpdateRequest request) {

        log.info("업체 정보 생성 요청: userId={}", userId);

        BusinessProfileDto.ProfileResponse response = businessProfileService.createBusinessProfile(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("업체 정보 생성이 완료되었습니다", response)
        );
    }

    /**
     * 업체 정보 수정
     * PUT /api/business/profile/{userId}
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<BusinessProfileDto.ProfileResponse>> updateBusinessProfile(
            @PathVariable UUID userId,
            @Valid @RequestBody BusinessProfileDto.UpdateRequest request) {

        log.info("업체 정보 수정 요청: userId={}", userId);

        BusinessProfileDto.ProfileResponse response = businessProfileService.updateBusinessProfile(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success("업체 정보 수정이 완료되었습니다", response)
        );
    }
}