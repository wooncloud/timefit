package timefit.auth.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import timefit.business.entity.Business;
import timefit.business.entity.UserBusinessRole;
import timefit.user.entity.User;

@Getter
@RequiredArgsConstructor
public class UserRegistrationResult {

    private final User user;
//    private final Business business;
//    private final UserBusinessRole userBusinessRole;

    public static UserRegistrationResult of(User user) {
        return new UserRegistrationResult(user);
    }
}

