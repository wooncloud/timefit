package timefit.common.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Controller 메서드 파라미터에 현재 인증된 사용자 ID를 주입하는 어노테이션
 *
 * 사용 예시:
 * public ResponseEntity<?> createMenu(
 *     @PathVariable UUID businessId,
 *     @RequestBody MenuRequest request,
 *     @CurrentUserId UUID currentUserId) { ... }
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserId {
}