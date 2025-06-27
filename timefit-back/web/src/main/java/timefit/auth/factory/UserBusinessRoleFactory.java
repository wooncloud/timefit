package timefit.auth.factory;

import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.common.entity.BusinessRole;
import timefit.user.entity.User;

import java.time.LocalDateTime;

/**
 * UserBusinessRole Entity 생성을 담당하는 Factory 클래스
 */
public class UserBusinessRoleFactory {

    private UserBusinessRoleFactory() {
        // utility class
    }

    /**
     * OWNER 권한으로 UserBusinessRole 생성
     */
    public static UserBusinessRole createOwner(User user, Business business) {
        return UserBusinessRole.createOwner(user, business);
    }

    /**
     * MANAGER 권한으로 UserBusinessRole 생성
     */
    public static UserBusinessRole createManager(User user, Business business, User invitedBy) {
        return UserBusinessRole.createManager(user, business, invitedBy);
    }

    /**
     * MEMBER 권한으로 UserBusinessRole 생성
     */
    public static UserBusinessRole createMember(User user, Business business, User invitedBy) {
        return UserBusinessRole.createMember(user, business, invitedBy);
    }

    /**
     * 역할 변경
     */
    public static void changeRole(UserBusinessRole userBusinessRole, BusinessRole newRole) {
        userBusinessRole.changeRole(newRole);
    }

    /**
     * 권한 비활성화
     */
    public static void deactivate(UserBusinessRole userBusinessRole) {
        userBusinessRole.deactivate();
    }

    /**
     * 권한 활성화
     */
    public static void activate(UserBusinessRole userBusinessRole) {
        userBusinessRole.activate();
    }

    /**
     * 초대자 변경 (권한 이관 시 사용)
     */
    public static void changeInviter(UserBusinessRole userBusinessRole, User newInviter) {
        userBusinessRole.changeInviter(newInviter);
    }

    /**
     * 특정 역할로 권한 생성 (범용)
     */
    public static UserBusinessRole createWithRole(User user, Business business, BusinessRole role, User invitedBy) {
        return UserBusinessRole.createWithRole(user, business, role, invitedBy);
    }
}