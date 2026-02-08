/**
 * ========================================
 * Load Test - 일상적 부하 성능 확인
 * ========================================
 *
 * 목적: 복잡한 JOIN 쿼리(3-4개)의 일상적 부하 성능 측정
 *
 * API: GET /api/business/{businessId}/reservations
 * 쿼리:
 *   1. SELECT * FROM reservation WHERE business_id = ?
 *   2. JOIN booking_slot ON reservation.booking_slot_id = booking_slot.id
 *   3. JOIN menu ON booking_slot.menu_id = menu.id
 *   4. JOIN "user" ON reservation.customer_id = user.id
 * JOIN: 3-4개 (복잡!)
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 근거:
 * - VU 100: 일상적 부하 (실제 예상 피크 120명의 83%)
 * - JOIN 4개 = Level 1(no-join)보다 훨씬 무거움
 *   → Level 1: p95 ~ 50-80ms
 *   → Level 2: p95 ~ 80-150ms
 *   → Level 3: p95 ~ 150-250ms (예상)
 *
 * 학습 포인트:
 * - "JOIN 4개면 얼마나 느린가?"
 * - "Level 1 대비 성능 차이는?"
 * - "실전 쿼리의 복잡도 체감"
 *
 * 실행 주기: 매일/매주 (예약 조회 로직 변경 시)
 * 소요 시간: 5분
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const reservationDuration = new Trend('reservation_query_duration');

export const options = {
    stages: [
        { duration: '1m', target: 100 },   // Ramp up
        { duration: '3m', target: 100 },   // Steady state
        { duration: '1m', target: 0 },     // Ramp down
    ],
    thresholds: {
        'http_req_duration': ['p(95)<300'],
        'http_req_failed': ['rate<0.01'],
        'errors': ['rate<0.01'],
    },
};

// owner1@timefit.test이 소유한 업체 ID (권한 문제 방지!)
const BUSINESS_ID = '30000000-0000-0000-0000-000000000001';

export function setup() {
    console.log('========================================');
    console.log('Level 3: multiple-join - Load Test');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('');

    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.test',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    if (loginRes.status !== 200) {
        throw new Error('로그인 실패');
    }

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공: owner1@timefit.test');
    console.log(`✅ 업체 ID: ${BUSINESS_ID}`);
    console.log('');
    console.log('목표: VU 100에서 JOIN 4개 쿼리 성능 확인');
    console.log('  - Level 1/2와 성능 비교');
    console.log('  - p95 < 300ms 달성');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
        accessToken: body.data.accessToken,
        businessId: BUSINESS_ID,
    };
}

export default function (data) {
    const businessId = data.businessId;

    // 예약 목록 조회 (복잡한 JOIN!)
    const url = `${data.baseUrl}/api/business/${businessId}/reservations`;
    const res = http.get(url, {
        headers: {
            'Authorization': `Bearer ${data.accessToken}`,
        },
    });

    reservationDuration.add(res.timings.duration);

    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 본문 존재': (r) => r.body && r.body.length > 0,
        '응답 시간 < 300ms': (r) => r.timings.duration < 300,
    });

    errorRate.add(!success);

    sleep(0.5);
}

export function teardown(data) {
    console.log('');
    console.log('✅ Load Test 완료');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - Level 1(50ms) vs Level 2(100ms) vs Level 3(?)');
    console.log('  - JOIN 개수에 따른 성능 차이 체감');
    console.log('');
    console.log('다음 단계: npm run test:pattern:l3:stress');
    console.log('');
}