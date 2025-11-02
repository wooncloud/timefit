package timefit.auth.dto;

import lombok.Getter;

import java.util.Objects;

public class AuthRequestDto {

    @Getter
    public static class UserSignUp {
        private final String email;
        private final String password;
        private final String name;
        private final String phoneNumber;

        private UserSignUp(String email, String password, String name, String phoneNumber) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phoneNumber = phoneNumber;
        }

        public static UserSignUp of(String email, String password, String name, String phoneNumber) {
            return new UserSignUp(email, password, name, phoneNumber);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            UserSignUp that = (UserSignUp) other;

            return Objects.equals(email, that.email) &&
                    Objects.equals(password, that.password) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(phoneNumber, that.phoneNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, password, name, phoneNumber);
        }
    }

    @Getter
    public static class UserSignIn {
        private final String email;
        private final String password;

        private UserSignIn(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public static UserSignIn of(String email, String password) {
            return new UserSignIn(email, password);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            UserSignIn that = (UserSignIn) other;

            return Objects.equals(email, that.email) &&
                    Objects.equals(password, that.password);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, password);
        }
    }

    @Getter
    public static class CustomerOAuth {
        private final String provider;
        private final String accessToken;
        private final String oauthId;

        private CustomerOAuth(String provider, String accessToken, String oauthId) {
            this.provider = provider;
            this.accessToken = accessToken;
            this.oauthId = oauthId;
        }

        public static CustomerOAuth of(String provider, String accessToken, String oauthId) {
            return new CustomerOAuth(provider, accessToken, oauthId);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            CustomerOAuth that = (CustomerOAuth) other;

            return Objects.equals(provider, that.provider) &&
                    Objects.equals(accessToken, that.accessToken) &&
                    Objects.equals(oauthId, that.oauthId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(provider, accessToken, oauthId);
        }
    }

    @Getter
    public static class Logout {
        private final String temporaryToken;

        private Logout(String temporaryToken) {
            this.temporaryToken = temporaryToken;
        }

        public static Logout of(String temporaryToken) {
            return new Logout(temporaryToken);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Logout logout = (Logout) other;

            return Objects.equals(temporaryToken, logout.temporaryToken);
        }

        @Override
        public int hashCode() {
            return Objects.hash(temporaryToken);
        }
    }


    @Getter
    public static class TokenRefresh {
        private final String refreshToken;

        private TokenRefresh(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public static TokenRefresh of(String refreshToken) {
            return new TokenRefresh(refreshToken);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            TokenRefresh that = (TokenRefresh) other;

            return Objects.equals(refreshToken, that.refreshToken);
        }

        @Override
        public int hashCode() {
            return Objects.hash(refreshToken);
        }
    }

    @Getter
    public static class OAuthUserInfo {
        private final String email;
        private final String name;
        private final String profileImageUrl;

        private OAuthUserInfo(String email, String name, String profileImageUrl) {
            this.email = email;
            this.name = name;
            this.profileImageUrl = profileImageUrl;
        }

        public static OAuthUserInfo of(String email, String name, String profileImageUrl) {
            return new OAuthUserInfo(email, name, profileImageUrl);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            OAuthUserInfo that = (OAuthUserInfo) other;

            return Objects.equals(email, that.email) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(profileImageUrl, that.profileImageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, name, profileImageUrl);
        }
    }
}