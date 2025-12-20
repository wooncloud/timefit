/**
 * Level 3: multiple-join - 예약 목록 조회 스트레스 테스트
 *
 * API: GET /api/business/{businessId}/reservations
 * JOIN: 3-4개 (복잡!)
 *
 * 목표: 복잡한 쿼리의 한계 VU 찾기
 * 예상: VU 200 정도가 한계 (Level 1: 800, Level 2: 500)
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const reservationDuration = new Trend('reservation_query_duration');

export const options = {
    stages: [
        { duration: '1m', target: 50 },
        { duration: '2m', target: 100 },
        { duration: '2m', target: 100 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 500 },
        { duration: '1m', target: 500 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        'http_req_duration': ['p(95)<3000'],
        'http_req_failed': ['rate<0.2'],
        'errors': ['rate<0.2'],
    },
};

// owner1@timefit.com이 소유한 업체 ID (권한 문제 방지!)
const BUSINESS_ID = '30000000-0000-0000-0000-000000000001';

export function setup() {
    console.log('Level 3: multiple-join - 스트레스 테스트');

    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.com',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공: owner1@timefit.com');
    console.log(`✅ 업체 ID: ${BUSINESS_ID} (owner1 소유)`);
    console.log('');
    console.log('학습 목표: 복잡한 쿼리의 VU 한계 찾기');
    console.log('예상: Level 1(800) > Level 2(500) > Level 3(200?)');
    console.log('');

    return {
        baseUrl: BASE_URL,
        accessToken: body.data.accessToken,
        businessId: BUSINESS_ID,
    };
}

export default function (data) {
    const businessId = data.businessId;

    const url = `${data.baseUrl}/api/business/${businessId}/reservations`;
    const res = http.get(url, {
        headers: {
            'Authorization': `Bearer ${data.accessToken}`,
        },
    });

    reservationDuration.add(res.timings.duration);

    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
    });

    errorRate.add(!success);

    sleep(0.3);
}

export function teardown(data) {
    console.log('');
    console.log('Level 3 Stress Test 완료');
    console.log('');
    console.log('💡 3가지 Level 비교:');
    console.log('  Level 1 (no-join): VU ?까지 안정');
    console.log('  Level 2 (single-join): VU ?까지 안정');
    console.log('  Level 3 (multiple-join): VU ?까지 안정');
    console.log('');
}