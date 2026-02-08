/**
 * ========================================
 * Stress Test - 비즈니스 목표 + 한계 탐색
 * ========================================
 *
 * 목적: 복잡한 JOIN 쿼리의 비즈니스 목표 달성 및 한계 파악
 *
 * API: GET /api/business/{businessId}/reservations
 * JOIN: 3-4개 (복잡!)
 *
 * 학습 포인트:
 * - "복잡한 쿼리의 VU 한계는?"
 * - "Level 1: 800+ > Level 2: 500+ > Level 3: ?"
 * - "VU 500에서도 p95 < 300ms 달성 가능한가?"
 *
 * 예상 결과:
 * - VU 100: p95 < 200ms ✅
 * - VU 500: p95 < 300ms ✅ (목표 달성)
 * - VU 750: p95 < 600ms ⚠️
 * - VU 1000: p95 > 1000ms ❌
 *
 * 실행 주기: 월 1회
 * 소요 시간: 15분
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const reservationDuration = new Trend('reservation_query_duration');

export const options = {
    stages: [
        { duration: '2m', target: 100 },   // Warm up
        { duration: '5m', target: 500 },   // 목표 부하
        { duration: '2m', target: 750 },   // 1.5배 부하
        { duration: '5m', target: 1000 },  // 2배 부하
        { duration: '1m', target: 0 },     // Cool down
    ],
    thresholds: {
        'http_req_duration': ['p(95)<3000'],
        'http_req_failed': ['rate<0.1'],
        'errors': ['rate<0.1'],
    },
};

// owner1@timefit.test이 소유한 업체 ID (권한 문제 방지!)
const BUSINESS_ID = '30000000-0000-0000-0000-000000000001';

export function setup() {
    console.log('========================================');
    console.log('Level 3: multiple-join - Stress Test');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('');

    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.test',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공: owner1@timefit.test');
    console.log(`✅ 업체 ID: ${BUSINESS_ID}`);
    console.log('');
    console.log('목표: 복잡한 쿼리의 VU 한계 찾기');
    console.log('  - Level 1/2/3 비교');
    console.log('  - VU 500에서 p95 < 300ms 달성?');
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
    console.log('✅ Stress Test 완료');
    console.log('');
    console.log('💡 3가지 Level 비교:');
    console.log('  Level 1 (no-join): VU ?까지 안정');
    console.log('  Level 2 (single-join): VU ?까지 안정');
    console.log('  Level 3 (multiple-join): VU ?까지 안정');
    console.log('');
}