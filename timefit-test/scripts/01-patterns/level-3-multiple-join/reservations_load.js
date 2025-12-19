/**
 * ========================================
 * Level 3: multiple-join - 예약 목록 조회 부하 테스트
 * ========================================
 *
 * 목적: 복잡한 JOIN 쿼리의 실전 성능 측정
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
 * 테스트 시나리오:
 * - Stage 1: VU 50 (1분) - 워밍업
 * - Stage 2: VU 100 (3분) - 목표 부하 (Level 1/2보다 낮음!)
 * - Stage 3: VU 0 (30초) - 종료
 *
 * 예상 결과:
 * - avg: 200-300ms (Level 1의 10배!)
 * - p95: < 500ms (업계 표준)
 * - TPS: 50+ (VU 100 기준)
 * - 에러율: < 1%
 *
 * 학습 포인트:
 * - "JOIN 4개면 얼마나 느린가?"
 * - "Level 1(20ms) vs Level 3(250ms) = 12배!"
 * - "실전 쿼리는 이렇게 무겁구나!"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const reservationDuration = new Trend('reservation_query_duration');

export const options = {
    stages: [
        { duration: '30s', target: 50 },   // 워밍업: 50 VU
        { duration: '30s', target: 50 },   // 안정: 50 VU
        { duration: '30s', target: 100 },  // 증가: 100 VU (Level 1/2보다 낮음!)
        { duration: '3m', target: 100 },   // 유지: 100 VU
        { duration: '30s', target: 0 },    // 종료
    ],
    thresholds: {
        // 업계 표준: 복잡한 쿼리는 p95 < 500ms
        'http_req_duration': ['p(95)<500'],
        // 에러율 1% 미만
        'http_req_failed': ['rate<0.01'],
        'errors': ['rate<0.01'],
    },
};

// owner1@timefit.com이 소유한 업체 ID (권한 문제 방지!)
const BUSINESS_ID = '30000000-0000-0000-0000-000000000001';

export function setup() {
    console.log('========================================');
    console.log('Level 3: multiple-join - 예약 목록 조회 부하 테스트');
    console.log('========================================');
    console.log('');

    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.com',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    if (loginRes.status !== 200) {
        throw new Error('로그인 실패');
    }

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공: owner1@timefit.com');
    console.log(`✅ 업체 ID: ${BUSINESS_ID} (owner1 소유)`);
    console.log('');
    console.log('테스트 패턴: Load Test');
    console.log('  - VU 100 (복잡한 쿼리라 낮게 설정)');
    console.log('');
    console.log('목표:');
    console.log('  - p95 < 500ms (업계 표준)');
    console.log('  - TPS: 50+');
    console.log('');
    console.log('학습 목표:');
    console.log('  - JOIN 4개 = 얼마나 느린가?');
    console.log('  - Level 1(20ms) vs Level 3(?)');
    console.log('  - 실전 쿼리의 복잡도 체감');
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
        '응답 시간 < 500ms': (r) => r.timings.duration < 500,
    });

    errorRate.add(!success);

    sleep(0.5);
}

export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 3 Load Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - Level 1(20ms) vs Level 2(60ms) vs Level 3(?ms)');
    console.log('  - JOIN 개수에 따른 성능 차이 체감');
    console.log('  - p95 < 500ms 달성?');
    console.log('');
    console.log('💡 인사이트:');
    console.log('  - 복잡도는 곱셈으로 증가!');
    console.log('  - 쿼리 최적화가 필수!');
    console.log('');
    console.log('다음 단계:');
    console.log('  npm run test:pattern:l3:stress');
    console.log('');
}