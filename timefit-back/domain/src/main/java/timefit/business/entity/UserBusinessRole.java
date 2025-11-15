package timefit.business.entity;

import timefit.common.entity.BaseEntity;
import timefit.common.entity.BusinessRole;
import timefit.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_business_role",
        uniqueConstraints = @UniqueConstraint(
                name = "unique_user_business",
                columnNames = {"user_id", "business_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBusinessRole extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private Business business;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessRole role = BusinessRole.MEMBER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invited_by")
    private User invitedBy;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt = LocalDateTime.now();

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;



    /**
     * OWNER 권한으로 UserBusinessRole 생성
     */
    public static UserBusinessRole createOwner(User user, Business business, User invitedBy) {
        UserBusinessRole userBusinessRole = new UserBusinessRole();
        userBusinessRole.user = user;
        userBusinessRole.business = business;
        userBusinessRole.role = BusinessRole.OWNER;
        userBusinessRole.invitedBy = invitedBy;
        userBusinessRole.joinedAt = LocalDateTime.now();
        userBusinessRole.isActive = true;
        return userBusinessRole;
    }

    /**
     * MANAGER 권한으로 UserBusinessRole 생성
     */
    public static UserBusinessRole createManager(User user, Business business, User invitedBy) {
        UserBusinessRole userBusinessRole = new UserBusinessRole();
        userBusinessRole.user = user;
        userBusinessRole.business = business;
        userBusinessRole.role = BusinessRole.MANAGER;
        userBusinessRole.invitedBy = invitedBy;
        userBusinessRole.joinedAt = LocalDateTime.now();
        userBusinessRole.isActive = true;
        return userBusinessRole;
    }

    /**
     * MEMBER 권한으로 UserBusinessRole 생성
     */
    public static UserBusinessRole createMember(User user, Business business, User invitedBy) {
        UserBusinessRole userBusinessRole = new UserBusinessRole();
        userBusinessRole.user = user;
        userBusinessRole.business = business;
        userBusinessRole.role = BusinessRole.MEMBER;
        userBusinessRole.invitedBy = invitedBy;
        userBusinessRole.joinedAt = LocalDateTime.now();
        userBusinessRole.isActive = true;
        return userBusinessRole;
    }

    /**
     * 특정 역할로 권한 생성 (범용)
     */
    public static UserBusinessRole createWithRole(User user, Business business, BusinessRole role, User invitedBy) {
        UserBusinessRole userBusinessRole = new UserBusinessRole();
        userBusinessRole.user = user;
        userBusinessRole.business = business;
        userBusinessRole.role = role;
        userBusinessRole.invitedBy = invitedBy;
        userBusinessRole.joinedAt = LocalDateTime.now();
        userBusinessRole.isActive = true;
        return userBusinessRole;
    }

    /**
     * 역할 변경
     */
    public void changeRole(BusinessRole newRole) {
        this.role = newRole;
    }

    /**
     * 권한 비활성화
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * 권한 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 초대자 변경 (권한 이관 시 사용)
     */
    public void changeInviter(User newInviter) {
        this.invitedBy = newInviter;
    }
}