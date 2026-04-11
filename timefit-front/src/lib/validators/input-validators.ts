/**
 * 일반 입력값 보안 검증
 * 도메인에 관계없이 범용으로 사용
 */

/**
 * 스크립트 인젝션 패턴 감지
 * description, notice 등 자유 입력 필드에 적용
 * @returns true면 위험한 입력 (차단 대상)
 */
export function validateScriptInjection(value: string): boolean {
    const SCRIPT_PATTERN = /<script|javascript:|on\w+\s*=/i;
    return SCRIPT_PATTERN.test(value);
}