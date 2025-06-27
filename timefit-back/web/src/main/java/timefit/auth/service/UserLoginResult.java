package timefit.auth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.User;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class UserLoginResult {

    private final User user;
    private final List<Business> businesses;
    private final List<UserBusinessRole> userBusinessRoles;
    private final boolean isFirstLogin;

    public static UserLoginResult ofBusinessUser(User user, List<UserBusinessRole> userBusinessRoles) {
        List<Business> businesses = userBusinessRoles.stream()
                .map(UserBusinessRole::getBusiness)
                .toList();

        return new UserLoginResult(user, businesses, userBusinessRoles, false);
    }

    public static UserLoginResult ofOAuthUser(User user, boolean isFirstLogin) {
        return new UserLoginResult(user, List.of(), List.of(), isFirstLogin);
    }
}