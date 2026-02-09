/**
 * ========================================
 * Smoke Test - API 기본 동작 확인
 * ========================================
 *
 * 목적: 배포 전 API가 정상 동작하는지 최소 부하로 확인
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ? (단 1개)
 * 권한: 공개 API (토큰 불필요)
 *
 * 근거:
 * - VU 1: 단일 사용자로도 에러 발생하면 배포 불가
 * - 30초: 충분한 반복 횟수 (약 30 requests) 확보
 * - p95 < 300ms: Supabase Free Plan 기준 (네트워크 50ms 포함)
 *
 * 실행 주기: 배포 전 매번 (CI/CD 필수)
 * 소요 시간: 30초
 *
 * 업계 표준:
 * - Google SRE: "1 VU로 API 가용성 확인"
 * - k6: "최소 부하로 기본 기능 검증"
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
        { duration: '30s', target: 1 },
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
    console.log('Level 1: no-join - Smoke Test');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('');
    console.log('목표: API 정상 동작 확인 (VU 1, 30초)');
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

    // Think time
    sleep(1);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('✅ Smoke Test 완료');
    console.log('');
}