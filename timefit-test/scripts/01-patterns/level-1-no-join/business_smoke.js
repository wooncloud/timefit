/**
 * ========================================
 * Level 1: no-join - 업체 조회 연기 테스트
 * ========================================
 *
 * 목적: 가장 단순한 쿼리(JOIN 없음)로 시스템 기본 동작 확인
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ? (단 1개)
 * 권한: 공개 API (토큰 불필요)
 *
 * 테스트 시나리오:
 * - VU 1: 단일 사용자로 1분간 최소 부하 테스트
 * - 목적: 시스템이 정상 작동하는지 확인
 *
 * 예상 결과:
 * - avg: < 50ms (인덱스 조회는 매우 빠름)
 * - p95: < 100ms
 * - 에러율: 0%
 *
 * 학습 포인트:
 * - "단순 조회는 얼마나 빠른가?"
 * - "인덱스 효과가 얼마나 큰가?"
 * - "시스템이 정상 작동하는가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const businessDuration = new Trend('business_query_duration');

// 테스트 옵션 - Smoke Test (연기 테스트)
export const options = {
    stages: [
        { duration: '30s', target: 1 },  // 30초간 1 VU 유지
        { duration: '30s', target: 1 },  // 30초간 1 VU 유지
    ],
    thresholds: {
        // 업계 표준: p95 < 100ms (단순 조회)
        'http_req_duration': ['p(95)<100'],
        // 에러율 0% (연기 테스트는 에러 없어야 함)
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
    console.log('Level 1: no-join - 업체 조회 연기 테스트');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('권한: 공개 API (토큰 불필요)');
    console.log('');
    console.log('테스트 패턴: Smoke Test');
    console.log('  - VU 1 (1분)');
    console.log('');
    console.log('목표:');
    console.log('  - p95 < 100ms');
    console.log('  - 에러율 < 1%');
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

    // Think time (사용자가 다음 요청까지 대기하는 시간)
    sleep(1);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 1 Smoke Test 완료');
    console.log('========================================');
    console.log('');
    console.log('다음 단계:');
    console.log('  npm run test:pattern:l1:load');
    console.log('');
}