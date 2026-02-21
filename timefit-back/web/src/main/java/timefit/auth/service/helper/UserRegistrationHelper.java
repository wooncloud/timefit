package timefit.auth.service.helper;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import timefit.auth.dto.AuthRequestDto;
import timefit.user.entity.User;
import timefit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserRegistrationHelper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerNewUser(AuthRequestDto.UserSignUp request) {

        // 1. 비밀 번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 2. User 생성 (Entity 정적 팩토리)
        User user = User.createUser(
                request.email(),
                encodedPassword,
                request.name(),
                request.phoneNumber()
        );

        return userRepository.save(user);
    }
}
