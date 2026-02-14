package timefit.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import timefit.auth.dto.AuthRequestDto;
import timefit.auth.dto.AuthResponseDto;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthFacadeService {

    private final AuthCommandService authCommandService;

    // 회원가입
    @Transactional
    public AuthResponseDto.UserSignUp signup(AuthRequestDto.UserSignUp request) {
        log.info("회원가입 처리 위임: email={}", request.email());
        return authCommandService.registerUser(request);
    }

    // 일반 로그인
    @Transactional
    public AuthResponseDto.UserSignIn signin(AuthRequestDto.UserSignIn request) {
        log.info("로그인 처리 위임: email={}", request.email());
        return authCommandService.loginUser(request);
    }

    // OAuth 로그인
    @Transactional
    public AuthResponseDto.CustomerOAuth customerOAuthLogin(AuthRequestDto.CustomerOAuth request) {
        log.info("OAuth 로그인 처리 위임: provider={}", request.provider());
        return authCommandService.loginOAuthUser(request);
    }

    // 로그아웃
    @Transactional
    public void logout(AuthRequestDto.Logout request) {
        log.info("로그아웃 처리");

        if (request.currentToken() != null) {
            authCommandService.logout(request.currentToken());
        }
    }

    // 토큰 갱신
    @Transactional
    public AuthResponseDto.TokenRefresh refreshToken(AuthRequestDto.TokenRefresh request) {
        log.info("토큰 갱신 처리 위임");
        return authCommandService.refreshToken(request);
    }
}