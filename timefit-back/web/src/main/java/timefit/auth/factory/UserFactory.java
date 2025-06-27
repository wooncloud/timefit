package timefit.auth.factory;

import timefit.user.entity.User;
import timefit.user.entity.UserRole;

import java.time.LocalDateTime;

/**
 * User Entity 생성을 담당하는 Factory 클래스
 */
public class UserFactory {

    private UserFactory() {
        // utility class
    }

    /**
     * 업체 사용자 생성 (이메일 회원가입)
     */
    public static User createBusinessUser(String email, String password, String name, String phoneNumber) {
        return User.createBusinessUser(email, password, name, phoneNumber);
    }

    /**
     * OAuth 고객 사용자 생성
     */
    public static User createOAuthUser(String email, String name, String profileImageUrl,
                                       String oauthProvider, String oauthId) {
        return User.createOAuthUser(email, name, profileImageUrl, oauthProvider, oauthId);
    }

    /**
     * 마지막 로그인 시간 업데이트
     */
    public static void updateLastLogin(User user) {
        user.updateLastLogin();
    }

    /**
     * 사용자 정보 업데이트
     */
    public static void updateUserInfo(User user, String name, String phoneNumber, String profileImageUrl) {
        user.updateUserInfo(name, phoneNumber, profileImageUrl);
    }
}