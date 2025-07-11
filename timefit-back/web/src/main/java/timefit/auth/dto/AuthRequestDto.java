package timefit.auth.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.Objects;

public class AuthRequestDto {

    @Getter
    public static class BusinessSignUp {
        private final String email;
        private final String password;
        private final String name;
        private final String phoneNumber;
        private final String businessName;
        private final String businessType;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;

        private BusinessSignUp(String email, String password, String name, String phoneNumber,
                                String businessName, String businessType, String businessNumber,
                                String address, String contactPhone, String description) {
            this.email = email;
            this.password = password;
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
        }

        public static BusinessSignUp of(String email, String password, String name, String phoneNumber,
                                        String businessName, String businessType, String businessNumber,
                                        String address, String contactPhone, String description) {
            return new BusinessSignUp(email, password, name, phoneNumber, businessName,
                    businessType, businessNumber, address, contactPhone, description);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            BusinessSignUp that = (BusinessSignUp) other;

            return Objects.equals(email, that.email) &&
                    Objects.equals(password, that.password) &&
                    Objects.equals(name, that.name) &&
                    Objects.equals(phoneNumber, that.phoneNumber) &&
                    Objects.equals(businessName, that.businessName) &&
                    Objects.equals(businessType, that.businessType) &&
                    Objects.equals(businessNumber, that.businessNumber) &&
                    Objects.equals(address, that.address) &&
                    Objects.equals(contactPhone, that.contactPhone) &&
                    Objects.equals(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, password, name, phoneNumber, businessName,
                    businessType, businessNumber, address, contactPhone, description);
        }
    }

    @Getter
    public static class BusinessSignIn {
        private final String email;
        private final String password;

        private BusinessSignIn(String email, String password) {
            this.email = email;
            this.password = password;
        }

        public static BusinessSignIn of(String email, String password) {
            return new BusinessSignIn(email, password);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            BusinessSignIn that = (BusinessSignIn) other;

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