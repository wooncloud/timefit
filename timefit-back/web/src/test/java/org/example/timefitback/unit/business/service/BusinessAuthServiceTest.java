package org.example.timefitback.unit.business.service;

import org.example.timefitback.business.dto.BusinessAuthDto;
import org.example.timefitback.business.entity.Business;
import org.example.timefitback.business.entity.User;
import org.example.timefitback.business.repository.BusinessRepository;
import org.example.timefitback.business.repository.UserRepository;
import org.example.timefitback.business.service.BusinessAuthService;
import org.example.timefitback.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("업체 인증 서비스 단위 테스트")
class BusinessAuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BusinessRepository businessRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BusinessAuthService businessAuthService;

    private BusinessAuthDto.SignUpRequest signUpRequest;
    private BusinessAuthDto.SignInRequest signInRequest;
    private User testUser;
    private Business testBusiness;

    @BeforeEach
    void setUp() {
        signUpRequest = new BusinessAuthDto.SignUpRequest(
                "test@example.com",
                "Password123!",
                "테스트업체",
                "01012345678",
                "테스트 미용실",
                "미용실",
                "123-45-67890",
                "서울시 강남구",
                "0212345678",
                "테스트 설명"
        );

        signInRequest = new BusinessAuthDto.SignInRequest(
                "test@example.com",
                "Password123!"
        );

        testUser = User.createBusinessUser(
                "test@example.com",
                "encodedPassword",
                "테스트업체",
                "01012345678"
        );
        testUser.setId(UUID.randomUUID());

        testBusiness = Business.createBusiness(
                testUser.getId(),
                "테스트 미용실",
                "미용실",
                "123-45-67890",
                "서울시 강남구",
                "0212345678",
                "테스트 설명"
        );
        testBusiness.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("회원가입 성공")
    void signUp_Success() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(businessRepository.existsByBusinessNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(businessRepository.save(any(Business.class))).thenReturn(testBusiness);

        // when
        BusinessAuthDto.AuthResponse response = businessAuthService.signUp(signUpRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getName()).isEqualTo("테스트업체");
        assertThat(response.getBusiness().getBusinessName()).isEqualTo("테스트 미용실");

        verify(userRepository).save(any(User.class));
        verify(businessRepository).save(any(Business.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signUp_EmailAlreadyExists() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> businessAuthService.signUp(signUpRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 존재하는 이메일입니다");

        verify(userRepository, never()).save(any());
        verify(businessRepository, never()).save(any());
    }

    @Test
    @DisplayName("회원가입 실패 - 사업자번호 중복")
    void signUp_BusinessNumberAlreadyExists() {
        // given
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(businessRepository.existsByBusinessNumber(anyString())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> businessAuthService.signUp(signUpRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이미 등록된 사업자번호입니다");

        verify(userRepository, never()).save(any());
        verify(businessRepository, never()).save(any());
    }

    @Test
    @DisplayName("로그인 성공")
    void signIn_Success() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(businessRepository.findByUserId(any(UUID.class))).thenReturn(Optional.of(testBusiness));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // when
        BusinessAuthDto.AuthResponse response = businessAuthService.signIn(signInRequest);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getBusiness().getBusinessName()).isEqualTo("테스트 미용실");

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 실패 - 사용자 없음")
    void signIn_UserNotFound() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> businessAuthService.signIn(signInRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void signIn_InvalidPassword() {
        // given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> businessAuthService.signIn(signInRequest))
                .isInstanceOf(BusinessException.class)
                .hasMessage("이메일 또는 비밀번호가 올바르지 않습니다");
    }

    @Test
    @DisplayName("전화번호 정규화 테스트")
    void normalizePhoneNumber_Test() {
        // given
        BusinessAuthDto.SignUpRequest request = new BusinessAuthDto.SignUpRequest(
                "test@example.com",
                "Password123!",
                "테스트업체",
                "010-1234-5678",  // 하이픈 포함
                "테스트 미용실",
                "미용실",
                "123-45-67890",
                "서울시 강남구",
                "02-1234-5678",   // 하이픈 포함
                "테스트 설명"
        );

        // when
        request.normalizePhoneNumber();

        // then
        assertThat(request.getPhoneNumber()).isEqualTo("01012345678");
        assertThat(request.getContactPhone()).isEqualTo("0212345678");
    }
}