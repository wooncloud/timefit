package timefit.business.dto;

import lombok.Getter;
import timefit.common.entity.BusinessRole;
import java.util.Objects;
import java.util.UUID;

public class BusinessRequestDto {
    /**
     * 업체 정보 생성
     */
    @Getter
    public static class CreateBusiness {
        private final String businessName;
        private final String businessType;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final UUID ownerUserId;

        private CreateBusiness(String businessName, String businessType, String businessNumber,
                                String address, String contactPhone, String description, UUID ownerUserId) {
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.ownerUserId = ownerUserId;
        }

        public static CreateBusiness of(String businessName, String businessType, String businessNumber,
                                        String address, String contactPhone, String description, UUID ownerUserId) {
            return new CreateBusiness(businessName, businessType, businessNumber, address, contactPhone, description, ownerUserId);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            CreateBusiness that = (CreateBusiness) other;

            return Objects.equals(businessName, that.businessName) &&
                    Objects.equals(businessType, that.businessType) &&
                    Objects.equals(businessNumber, that.businessNumber) &&
                    Objects.equals(address, that.address) &&
                    Objects.equals(contactPhone, that.contactPhone) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(ownerUserId, that.ownerUserId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessName, businessType, businessNumber, address, contactPhone, description, ownerUserId);
        }
    }

    /**
     * 업체 정보 수정
     */
    @Getter
    public static class UpdateBusiness {
        private final String businessName;
        private final String businessType;
        private final String businessNumber;
        private final String address;
        private final String contactPhone;
        private final String description;
        private final String logoUrl;

        private UpdateBusiness(String businessName, String businessType,  String businessNumber,
                                String address,
                                String contactPhone, String description, String logoUrl) {
            this.businessName = businessName;
            this.businessType = businessType;
            this.businessNumber = businessNumber;
            this.address = address;
            this.contactPhone = contactPhone;
            this.description = description;
            this.logoUrl = logoUrl;
        }

        public static UpdateBusiness of(String businessName, String businessType, String businessNumber,
                                        String address, String contactPhone, String description, String logoUrl) {
            return new UpdateBusiness(businessName, businessType, businessNumber, address, contactPhone, description, logoUrl);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            UpdateBusiness that = (UpdateBusiness) other;

            return Objects.equals(businessName, that.businessName) &&
                    Objects.equals(businessType, that.businessType) &&
                    Objects.equals(businessNumber, that.businessNumber) &&
                    Objects.equals(address, that.address) &&
                    Objects.equals(contactPhone, that.contactPhone) &&
                    Objects.equals(description, that.description) &&
                    Objects.equals(logoUrl, that.logoUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessName, businessType, businessNumber , address, contactPhone, description, logoUrl);
        }
    }

    /**
     * 업체 정보 조회
     */
    @Getter
    public static class GetBusiness {

    }

    /**
     * 업체 검색 요청
     */
    @Getter
    public static class SearchBusiness {
        private final String keyword;
        private final String businessType;
        private final String region;
        private final Integer page;
        private final Integer size;

        private SearchBusiness(String keyword, String businessType, String region, Integer page, Integer size) {
            this.keyword = keyword;
            this.businessType = businessType;
            this.region = region;
            this.page = page != null ? page : 0;
            this.size = size != null ? size : 20;
        }

        public static SearchBusiness of(String keyword, String businessType, String region, Integer page, Integer size) {
            return new SearchBusiness(keyword, businessType, region, page, size);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            SearchBusiness that = (SearchBusiness) other;

            return Objects.equals(keyword, that.keyword) &&
                    Objects.equals(businessType, that.businessType) &&
                    Objects.equals(region, that.region) &&
                    Objects.equals(page, that.page) &&
                    Objects.equals(size, that.size);
        }

        @Override
        public int hashCode() {
            return Objects.hash(keyword, businessType, region, page, size);
        }
    }

    /**
     * 업체 삭제 확인 요청
     */
    @Getter
    public static class DeleteBusiness {
        private final Boolean confirmDelete;
        private final String deleteReason;

        private DeleteBusiness(Boolean confirmDelete, String deleteReason) {
            this.confirmDelete = confirmDelete;
            this.deleteReason = deleteReason;
        }

        public static DeleteBusiness of(Boolean confirmDelete, String deleteReason) {
            return new DeleteBusiness(confirmDelete, deleteReason);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            DeleteBusiness that = (DeleteBusiness) other;

            return Objects.equals(confirmDelete, that.confirmDelete) &&
                    Objects.equals(deleteReason, that.deleteReason);
        }

        @Override
        public int hashCode() {
            return Objects.hash(confirmDelete, deleteReason);
        }
    }

    /**
     * 구성원 권한 변경 요청
     */
    @Getter
    public static class ChangeRole {
        private final UUID businessId;
        private final UUID targetUserId;
        private final BusinessRole newRole;
        private final UUID requesterUserId;

        private ChangeRole(UUID businessId, UUID targetUserId, BusinessRole newRole, UUID requesterUserId) {
            this.businessId = businessId;
            this.targetUserId = targetUserId;
            this.newRole = newRole;
            this.requesterUserId = requesterUserId;
        }

        public static ChangeRole of(UUID businessId, UUID targetUserId, BusinessRole newRole, UUID requesterUserId) {
            return new ChangeRole(businessId, targetUserId, newRole, requesterUserId);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            ChangeRole that = (ChangeRole) other;

            return Objects.equals(businessId, that.businessId) &&
                    Objects.equals(targetUserId, that.targetUserId) &&
                    Objects.equals(newRole, that.newRole) &&
                    Objects.equals(requesterUserId, that.requesterUserId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(businessId, targetUserId, newRole, requesterUserId);
        }
    }

    /**
     * 사용자 초대 요청
     */
    @Getter
    public static class InviteUser {
        private final String email;
        private final BusinessRole role;
        private final String invitationMessage;

        private InviteUser(String email, BusinessRole role, String invitationMessage) {
            this.email = email;
            this.role = role;
            this.invitationMessage = invitationMessage;
        }

        public static InviteUser of(String email, BusinessRole role, String invitationMessage) {
            return new InviteUser(email, role, invitationMessage);
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            InviteUser that = (InviteUser) other;

            return Objects.equals(email, that.email) &&
                    Objects.equals(role, that.role) &&
                    Objects.equals(invitationMessage, that.invitationMessage);
        }

        @Override
        public int hashCode() {
            return Objects.hash(email, role, invitationMessage);
        }
    }
}