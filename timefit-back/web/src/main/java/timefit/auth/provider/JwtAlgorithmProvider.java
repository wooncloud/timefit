package timefit.auth.provider;

import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import timefit.config.JwtConfig;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAlgorithmProvider {

    private final JwtConfig jwtConfig;

    /**
     * Access Token용 Algorithm
     *
     * @return RS256 Algorithm
     */
    public Algorithm getAccessTokenAlgorithm() {
        try {
            RSAPublicKey publicKey = loadPublicKey(jwtConfig.getAccess().getPublicKeyPath());
            RSAPrivateKey privateKey = loadPrivateKey(jwtConfig.getAccess().getPrivateKeyPath());

            log.debug("Access Token Algorithm 로드 성공 (RS256)");
            return Algorithm.RSA256(publicKey, privateKey);

        } catch (Exception e) {
            log.error("Access Token Algorithm 로드 실패: {}", e.getMessage());
            throw new RuntimeException("Failed to load Access Token RSA keys", e);
        }
    }

    /**
     * Refresh Token용 Algorithm
     *
     * @return RS512 Algorithm
     */
    public Algorithm getRefreshTokenAlgorithm() {
        try {
            RSAPublicKey publicKey = loadPublicKey(jwtConfig.getRefresh().getPublicKeyPath());
            RSAPrivateKey privateKey = loadPrivateKey(jwtConfig.getRefresh().getPrivateKeyPath());

            log.debug("Refresh Token Algorithm 로드 성공 (RS512)");
            return Algorithm.RSA512(publicKey, privateKey);

        } catch (Exception e) {
            log.error("Refresh Token Algorithm 로드 실패: {}", e.getMessage());
            throw new RuntimeException("Failed to load Refresh Token RSA keys", e);
        }
    }

    /**
     * Private Key 로드 (PKCS#8 형식)
     *
     * @param path Private Key 파일 경로
     * @return RSAPrivateKey
     */
    private RSAPrivateKey loadPrivateKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(path)));

        // PEM 헤더/푸터 제거
        key = key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN RSA PRIVATE KEY-----", "")
                .replace("-----END RSA PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPrivateKey) keyFactory.generatePrivate(spec);
    }

    /**
     * Public Key 로드 (X.509 형식)
     *
     * @param path Public Key 파일 경로
     * @return RSAPublicKey
     */
    private RSAPublicKey loadPublicKey(String path) throws Exception {
        String key = new String(Files.readAllBytes(Paths.get(path)));

        // PEM 헤더/푸터 제거
        key = key.replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(key);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return (RSAPublicKey) keyFactory.generatePublic(spec);
    }
}