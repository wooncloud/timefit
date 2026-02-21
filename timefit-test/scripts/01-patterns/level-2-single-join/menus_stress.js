/**
 * ========================================
 * Stress Test - 비즈니스 목표 + 한계 탐색
 * ========================================
 *
 * 목적: JOIN 1개 쿼리의 비즈니스 목표 달성 및 한계 파악
 *
 * API: GET /api/business/{businessId}/menu
 * 쿼리:
 *   1. SELECT * FROM menu WHERE business_id = ?
 *   2. SELECT * FROM business_category WHERE id IN (...)
 * JOIN: 1개
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 학습 포인트:
 * - "JOIN 있으면 한계 VU가 낮아지는가?"
 * - "Level 1 (no-join)과 비교하여 병목이 더 빨리 발생하는가?"
 * - "VU 500에서도 p95 < 300ms 달성 가능한가?"
 *
 * 예상 결과:
 * - VU 100: p95 < 150ms ✅
 * - VU 500: p95 < 300ms ✅ (목표 달성)
 * - VU 750: p95 < 500ms ⚠️
 * - VU 1000: p95 > 1000ms ❌
 *
 * 실행 주기: 월 1회
 * 소요 시간: 15분
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:80';

const errorRate = new Rate('errors');
const menuDuration = new Trend('menu_query_duration');

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

const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001',
    '30000000-0000-0000-0000-000000000002',
    '30000000-0000-0000-0000-000000000003',
];

export function setup() {
    console.log('========================================');
    console.log('Level 2: single-join - Stress Test');
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
    console.log('✅ 로그인 성공');
    console.log('');
    console.log('목표: JOIN 1개 쿼리의 한계 탐색');
    console.log('  - VU 500에서 p95 < 300ms 달성?');
    console.log('  - Level 1과 비교하여 병목 빨리 발생?');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
        accessToken: body.data.accessToken,
    };
}

export default function (data) {
    const businessId = BUSINESS_IDS[Math.floor(Math.random() * BUSINESS_IDS.length)];

    const url = `${data.baseUrl}/api/business/${businessId}/menu`;
    const res = http.get(url, {
        headers: {
            'Authorization': `Bearer ${data.accessToken}`,
        },
    });

    menuDuration.add(res.timings.duration);

    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 본문 존재': (r) => r.body && r.body.length > 0,
    });

    errorRate.add(!success);

    sleep(0.3);
}

export function teardown(data) {
    console.log('');
    console.log('✅ Stress Test 완료');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - VU 500에서 p95 < 300ms 달성했나요?');
    console.log('  - Level 1 대비 병목이 더 빨리 발생했나요?');
    console.log('');
    console.log('다음 단계: npm run test:pattern:l2:spike');
    console.log('');
}