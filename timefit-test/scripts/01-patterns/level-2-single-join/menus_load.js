/**
 * ========================================
 * Load Test - 일상적 부하 성능 확인
 * ========================================
 *
 * 목적: JOIN 1개 포함 쿼리의 평균 부하 성능 측정
 *
 * API: GET /api/business/{businessId}/menu
 * 쿼리:
 *   1. SELECT * FROM menu WHERE business_id = ?
 *   2. SELECT * FROM business_category WHERE id IN (...)
 * JOIN: 1개 (business_category)
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 근거:
 * - VU 100: 일상적 부하 (실제 예상 피크 120명의 83%)
 * - JOIN 1개 추가 시 Level 1 대비 성능 비교
 *   → Level 1 (no-join): p95 ~ 50-80ms
 *   → Level 2 (single-join): p95 ~ 80-150ms (예상)
 *
 * 학습 포인트:
 * - "JOIN 1개 추가하면 얼마나 느려지는가?"
 * - "Level 1 대비 성능 차이는?"
 * - "VU 100에서도 안정적인가?"
 *
 * 실행 주기: 매일/매주 (메뉴 조회 로직 변경 시)
 * 소요 시간: 5분
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const menuDuration = new Trend('menu_query_duration');

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

// Setup: 로그인하여 JWT 토큰 획득
export function setup() {
    console.log('========================================');
    console.log('Level 2: single-join - Load Test');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}/menu');
    console.log('');

    // 로그인
    console.log('🔐 로그인 중...');
    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.test',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    if (loginRes.status !== 200) {
        console.error('❌ 로그인 실패:', loginRes.status);
        throw new Error('로그인 실패');
    }

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공');
    console.log('');
    console.log('목표: VU 100에서 JOIN 1개 쿼리 성능 확인');
    console.log('  - Level 1과 성능 비교');
    console.log('  - p95 < 300ms 달성');
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
        '응답 시간 < 300ms': (r) => r.timings.duration < 300,
    });

    errorRate.add(!success);

    // Think time
    sleep(0.5);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('✅ Load Test 완료');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - Level 1과 비교: 얼마나 느렸나요?');
    console.log('  - VU 100에서도 안정적이었나요?');
    console.log('');
    console.log('다음 단계: npm run test:pattern:l2:stress');
    console.log('');
}