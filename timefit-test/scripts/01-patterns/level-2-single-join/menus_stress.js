/**
 * ========================================
 * Level 2: single-join - 메뉴 목록 조회 스트레스 테스트
 * ========================================
 *
 * 목적: JOIN 1개 쿼리의 한계점 파악
 *
 * API: GET /api/business/{businessId}/menu
 * 쿼리:
 *   1. SELECT * FROM menu WHERE business_id = ?
 *   2. SELECT * FROM business_category WHERE id IN (...)
 * JOIN: 1개
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 테스트 시나리오:
 * - VU 50 → 200 → 500 → 1000 점진적 증가
 *
 * 예상 결과:
 * - VU 200까지: p95 < 200ms
 * - VU 500: p95 < 500ms (증가 시작)
 * - VU 800+: 급증 (Level 1보다 빨리 한계)
 *
 * 학습 포인트:
 * - "JOIN 있으면 한계 VU가 낮아지는가?"
 * - "Level 1은 VU 800, Level 2는?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const menuDuration = new Trend('menu_query_duration');

export const options = {
    stages: [
        { duration: '1m', target: 50 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 200 },
        { duration: '2m', target: 500 },
        { duration: '2m', target: 500 },
        { duration: '2m', target: 1000 },
        { duration: '1m', target: 1000 },
        { duration: '30s', target: 0 },
    ],
    thresholds: {
        'http_req_duration': ['p(95)<3000'],
        'http_req_failed': ['rate<0.2'],
        'errors': ['rate<0.2'],
    },
};

const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001',
    '30000000-0000-0000-0000-000000000002',
    '30000000-0000-0000-0000-000000000003',
];

export function setup() {
    console.log('========================================');
    console.log('Level 2: single-join - 메뉴 목록 조회 스트레스 테스트');
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
    console.log('✅ 로그인 성공');
    console.log('');
    console.log('학습 목표:');
    console.log('  - JOIN으로 한계 VU가 낮아지는가?');
    console.log('  - Level 1: VU 800 vs Level 2: ?');
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
    console.log('========================================');
    console.log('Level 2 Stress Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 어느 VU부터 응답 시간 급증?');
    console.log('  - Level 1보다 한계가 낮았나요?');
    console.log('');
    console.log('다음 단계:');
    console.log('  npm run test:pattern:l2:spike');
    console.log('');
}