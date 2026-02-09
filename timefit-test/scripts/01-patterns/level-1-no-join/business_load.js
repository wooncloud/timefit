/**
 * ========================================
 * Load Test - 일상적 부하 성능 확인
 * ========================================
 *
 * 목적: 평균 부하에서 쾌적한 성능 유지 여부 확인
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ? (단 1개)
 * 권한: 공개 API (토큰 불필요)
 *
 * 근거:
 * - VU 100: 실제 예상 피크(120명)의 83% 부하
 *   → Little's Law: 200개 업체 × 피크 시간대 35% = 120명
 *   → 100 VU = 일상적인 부하 (Baseline)
 *
 * - 1m ramp up: JVM JIT 컴파일, DB Connection Pool 워밍업
 * - 3m steady state: 충분한 샘플 수집 (최소 180 requests)
 * - 1m ramp down: Connection 정리, Graceful Shutdown
 *
 * - p95 < 300ms: Supabase Free Plan 현실적 목표
 *   → 로컬 DB(20ms) + 네트워크(50ms) + Shared CPU 버퍼(50ms)
 *   → 조회 5회 × 300ms = 1.5초 (Jakob Nielsen 3초 이내 ✅)
 *
 * 실행 주기: 매일/매주 (성능 회귀 감지용)
 * 소요 시간: 5분
 *
 * 성능 목표:
 * - VU 100에서 p95 < 100ms 달성 시: "쾌적함" ✅
 * - VU 100에서 p95 < 300ms: "목표 달성" ✅
 * - VU 100에서 p95 > 300ms: "성능 개선 필요" ⚠️
 *
 * 업계 표준:
 * - Google SRE: "평균 트래픽의 80-120% 수준으로 자주 실행"
 * - k6: "Load Test는 CI/CD에 통합하여 빠른 피드백"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:80';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const businessDuration = new Trend('business_query_duration');

// 테스트 옵션
export const options = {
    stages: [
        { duration: '1m', target: 100 },   // Ramp up: 점진적 증가
        { duration: '3m', target: 100 },   // Steady state: 안정 상태
        { duration: '1m', target: 0 },     // Ramp down: 정상 종료
    ],
    thresholds: {
        'http_req_duration': ['p(95)<300'],
        'http_req_failed': ['rate<0.01'],
        'errors': ['rate<0.01'],
    },
};

// 테스트용 고정 ID (seed-minimal.sql 기준)
const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001', // 헤어샵
    '30000000-0000-0000-0000-000000000002', // 네일샵
    '30000000-0000-0000-0000-000000000003', // 카페
];

// Setup: 테스트 시작 전 정보 출력
export function setup() {
    console.log('========================================');
    console.log('Level 1: no-join - Load Test');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('');
    console.log('목표: VU 100에서 일상적 부하 성능 확인');
    console.log('  - p95 < 100ms: 쾌적함 ✅');
    console.log('  - p95 < 300ms: 목표 달성 ✅');
    console.log('========================================');
    console.log('');

    return { baseUrl: BASE_URL };
}

// 메인 테스트 함수
export default function (data) {
    // 랜덤하게 업체 선택
    const businessId = BUSINESS_IDS[Math.floor(Math.random() * BUSINESS_IDS.length)];

    // 업체 정보 조회 (공개 API)
    const url = `${data.baseUrl}/api/business/${businessId}`;
    const res = http.get(url);

    // 응답 시간 기록
    businessDuration.add(res.timings.duration);

    // 검증
    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 본문 존재': (r) => r.body && r.body.length > 0,
        '응답 시간 < 300ms': (r) => r.timings.duration < 300,
    });

    errorRate.add(!success);

    // Think time (0.5초 - 일상적 부하)
    sleep(0.5);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('✅ Load Test 완료');
    console.log('');
    console.log('다음 단계: npm run test:pattern:l1:stress');
    console.log('');
}