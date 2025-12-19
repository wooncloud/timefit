/**
 * ========================================
 * Level 1: no-join - 업체 조회 부하 테스트
 * ========================================
 *
 * 목적: 목표 부하(VU 200)에서 안정적으로 동작하는지 확인
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ? (단 1개)
 * 권한: 공개 API (토큰 불필요)
 *
 * 테스트 시나리오:
 * - Stage 1: VU 50 (1분) - 워밍업
 * - Stage 2: VU 200 (3분) - 목표 부하 유지
 * - Stage 3: VU 0 (30초) - 종료
 *
 * 예상 결과:
 * - avg: < 50ms
 * - p95: < 100ms
 * - TPS: 150+ (VU 200 기준)
 * - 에러율: 0%
 *
 * 학습 포인트:
 * - "VU 200에서도 응답 시간이 평평하게 유지되는가?"
 * - "커넥션 풀 10개로 VU 200 처리 가능한가?"
 * - "목표 TPS(150+) 달성하는가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const businessDuration = new Trend('business_query_duration');

// 테스트 옵션 - Load Test (부하 테스트)
export const options = {
    stages: [
        { duration: '30s', target: 50 },   // 워밍업: 50 VU
        { duration: '30s', target: 50 },   // 안정: 50 VU
        { duration: '30s', target: 200 },  // 증가: 200 VU
        { duration: '3m', target: 200 },   // 유지: 200 VU (목표 부하)
        { duration: '30s', target: 0 },    // 종료
    ],
    thresholds: {
        // 업계 표준: p95 < 100ms (단순 조회)
        'http_req_duration': ['p(95)<100'],
        // 에러율 1% 미만
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
    console.log('Level 1: no-join - 업체 조회 부하 테스트');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('권한: 공개 API (토큰 불필요)');
    console.log('');
    console.log('테스트 패턴: Load Test');
    console.log('  - Stage 1: VU 50 (1분) - 워밍업');
    console.log('  - Stage 2: VU 200 (3분) - 목표 부하');
    console.log('');
    console.log('목표:');
    console.log('  - p95 < 100ms');
    console.log('  - TPS: 150+');
    console.log('  - 에러율 < 1%');
    console.log('');
    console.log('학습 목표:');
    console.log('  - VU 200에서 응답 시간 평평하게 유지?');
    console.log('  - 커넥션 풀 10개로 충분한가?');
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
        '응답 시간 < 100ms': (r) => r.timings.duration < 100,
    });

    errorRate.add(!success);

    // Think time (0.5초로 짧게 - 부하 테스트)
    sleep(0.5);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 1 Load Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - VU 200에서 응답 시간이 평평했나요?');
    console.log('  - TPS가 150+ 달성했나요?');
    console.log('  - 에러가 없었나요?');
    console.log('');
    console.log('다음 단계:');
    console.log('  npm run test:pattern:l1:stress');
    console.log('');
}