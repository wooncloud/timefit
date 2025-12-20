/**
 * ========================================
 * Level 2: single-join - 메뉴 목록 조회 부하 테스트
 * ========================================
 *
 * 목적: JOIN 1개 포함 쿼리의 성능 측정 및 Level 1과 비교
 *
 * API: GET /api/business/{businessId}/menu
 * 쿼리:
 *   1. SELECT * FROM menu WHERE business_id = ?
 *   2. SELECT * FROM business_category WHERE id IN (...)
 * JOIN: 1개 (business_category)
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 테스트 시나리오:
 * - Stage 1: VU 50 (1분) - 워밍업
 * - Stage 2: VU 200 (3분) - 목표 부하 유지
 * - Stage 3: VU 0 (30초) - 종료
 *
 * 예상 결과:
 * - avg: 60-100ms (Level 1의 3배)
 * - p95: < 200ms
 * - TPS: 100+ (Level 1보다 낮음)
 * - 에러율: < 1%
 *
 * 학습 포인트:
 * - "JOIN 1개 추가하면 얼마나 느려지는가?"
 * - "Level 1 대비 3배 느린가?"
 * - "VU 한계가 낮아지는가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const menuDuration = new Trend('menu_query_duration');

// 테스트 옵션 - Load Test
export const options = {
    stages: [
        { duration: '30s', target: 50 },   // 워밍업: 50 VU
        { duration: '30s', target: 50 },   // 안정: 50 VU
        { duration: '30s', target: 200 },  // 증가: 200 VU
        { duration: '3m', target: 200 },   // 유지: 200 VU (목표 부하)
        { duration: '30s', target: 0 },    // 종료
    ],
    thresholds: {
        // JOIN 1개: p95 < 200ms
        'http_req_duration': ['p(95)<200'],
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

// Setup: 로그인하여 JWT 토큰 획득
export function setup() {
    console.log('========================================');
    console.log('Level 2: single-join - 메뉴 목록 조회 부하 테스트');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}/menu');
    console.log('권한: 인증 필요 (JWT 토큰)');
    console.log('');

    // 로그인
    console.log('🔐 로그인 중...');
    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.com',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    if (loginRes.status !== 200) {
        console.error('❌ 로그인 실패:', loginRes.status);
        throw new Error('로그인 실패');
    }

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공 - 토큰 획득');
    console.log('');
    console.log('테스트 패턴: Load Test');
    console.log('  - Stage 1: VU 50 (1분) - 워밍업');
    console.log('  - Stage 2: VU 200 (3분) - 목표 부하');
    console.log('');
    console.log('목표:');
    console.log('  - p95 < 200ms');
    console.log('  - TPS: 100+');
    console.log('  - 에러율 < 1%');
    console.log('');
    console.log('학습 목표:');
    console.log('  - JOIN 1개 추가 시 성능 비교');
    console.log('  - Level 1 대비 얼마나 느린가?');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
        accessToken: body.data.accessToken,
    };
}

// 메인 테스트 함수
export default function (data) {
    // 랜덤하게 업체 선택
    const businessId = BUSINESS_IDS[Math.floor(Math.random() * BUSINESS_IDS.length)];

    // 메뉴 목록 조회 (인증 필요)
    const url = `${data.baseUrl}/api/business/${businessId}/menu`;
    const res = http.get(url, {
        headers: {
            'Authorization': `Bearer ${data.accessToken}`,
        },
    });

    // 응답 시간 기록
    menuDuration.add(res.timings.duration);

    // 검증
    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 본문 존재': (r) => r.body && r.body.length > 0,
        '응답 시간 < 200ms': (r) => r.timings.duration < 200,
    });

    errorRate.add(!success);

    // Think time
    sleep(0.5);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 2 Load Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - Level 1과 비교: 얼마나 느렸나요?');
    console.log('  - JOIN 1개 추가 = 응답 시간 3배?');
    console.log('  - VU 200에서도 안정적이었나요?');
    console.log('');
    console.log('다음 단계:');
    console.log('  npm run test:pattern:l2:stress');
    console.log('');
}