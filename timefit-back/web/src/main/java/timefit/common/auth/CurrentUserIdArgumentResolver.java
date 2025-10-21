package timefit.common.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import timefit.exception.auth.AuthErrorCode;
import timefit.exception.auth.AuthException;

import java.util.UUID;

/**
 * @CurrentUserId 어노테이션이 붙은 파라미터에 현재 사용자 ID를 주입하는 Resolver
 * JwtAuthFilter 에서 이미 request.setAttribute("userId")로 설정한 값을 재사용
 */
@Slf4j
@Component
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    // @CurrentUserId 어노테이션이 붙은 UUID 타입 파라미터를 지원
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentUserId.class);
        boolean isUUIDType = UUID.class.isAssignableFrom(parameter.getParameterType());

        return hasAnnotation && isUUIDType;
    }

    // Filter 에서 설정한 userId를 추출하여 반환
    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // JwtAuthFilter에서 이미 setAttribute("userId")로 설정한 값 가져오기
        Object userId = request.getAttribute("userId");

        if (userId == null) {
            log.error("request에 userId 속성이 없습니다. JwtAuthFilter가 제대로 동작하지 않았을 수 있습니다.");
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        if (!(userId instanceof UUID)) {
            log.error("userId 타입이 UUID가 아닙니다: {}", userId.getClass().getName());
            throw new AuthException(AuthErrorCode.TOKEN_INVALID);
        }

        log.debug("CurrentUserId resolved from request attribute: {}", userId);
        return userId;
    }
}