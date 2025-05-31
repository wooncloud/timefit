package org.example.timefitback.business.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.timefitback.business.dto.BusinessAuthDto;
import org.example.timefitback.business.service.BusinessAuthService;
import org.example.timefitback.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/business/auth")
@RequiredArgsConstructor
public class BusinessAuthController {

    private final BusinessAuthService businessAuthService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<BusinessAuthDto.AuthResponse>> signUp(
            @Valid @RequestBody BusinessAuthDto.SignUpRequest request) {

        log.info("업체 회원가입 요청: email={}, businessName={}", request.getEmail(), request.getBusinessName());

        BusinessAuthDto.AuthResponse response = businessAuthService.signUp(request);

        return ResponseEntity.ok(
                ApiResponse.success("업체 회원가입이 완료되었습니다", response)
        );
    }

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<BusinessAuthDto.AuthResponse>> signIn(
            @Valid @RequestBody BusinessAuthDto.SignInRequest request) {

        log.info("업체 로그인 요청: email={}", request.getEmail());

        BusinessAuthDto.AuthResponse response = businessAuthService.signIn(request);

        return ResponseEntity.ok(
                ApiResponse.success("로그인이 완료되었습니다", response)
        );
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(
                ApiResponse.success("서비스가 정상 작동 중입니다", "OK")
        );
    }
}