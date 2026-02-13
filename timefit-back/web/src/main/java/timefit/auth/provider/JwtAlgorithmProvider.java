package timefit.auth.provider;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import timefit.config.JwtConfig;

@Component
@RequiredArgsConstructor
public class JwtAlgorithmProvider {

    private final JwtConfig jwtConfig;

    /**
     * JWT 서명/검증용 Algorithm 객체 반환
     * @return JWT Algorithm 객체
     */
    public Algorithm getAlgorithm() {
        return Algorithm.HMAC512(jwtConfig.getSecretKey());
    }
}