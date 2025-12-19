/**
 * ========================================
 * Level 2: single-join - 메뉴 목록 조회 급증 테스트
 * ========================================
 *
 * 목적: 트래픽 급증 시 시스템 회복력 확인
 *
 * API: GET /api/business/{businessId}/menu
 * 쿼리:
 *   1. SELECT * FROM menu WHERE business_id = ?
 *   2. SELECT * FROM business_category WHERE id IN (...)
 * JOIN: 1개
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 테스트 시나리오:
 * - Stage 1: VU 100 (1분) - 정상 부하
 * - Stage 2: VU 100→2000 (10초) - 급증! ⚡
 * - Stage 3: VU 2000 (1분) - 피크 유지
 * - Stage 4: VU 2000→100 (10초) - 급감
 * - Stage 5: VU 100 (1분) - 회복 확인
 *
 * 예상 결과:
 * - 정상: avg 100ms
 * - 급증 시: avg 500-1000ms
 * - 회복 후: avg 100ms로 복귀
 *
 * 학습 포인트:
 * - "급증 시 얼마나 느려지는가?"
 * - "회복 속도는?"
 * - "에러가 발생하는가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const menuDuration = new Trend('menu_query_duration');

export const options = {
    stages: [
        { duration: '1m', target: 100 },   // 정상 부하
        { duration: '10s', target: 2000 }, // 급증! ⚡
        { duration: '1m', target: 2000 },  // 피크 유지
        { duration: '10s', target: 100 },  // 급감
        { duration: '1m', target: 100 },   // 회복 확인
        { duration: '30s', target: 0 },    // 종료
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
    console.log('Level 2: single-join - 메뉴 목록 조회 급증 테스트');
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
    console.log('테스트 패턴: Spike Test');
    console.log('  - VU 100 (정상) → 2000 (급증) → 100 (회복)');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 급증 시 응답 시간 얼마나 증가?');
    console.log('  - 회복 속도는?');
    console.log('  - 에러 발생하는가?');
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
    });

    errorRate.add(!success);

    sleep(0.3);
}

export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 2 Spike Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 급증 시: 응답 시간 V자 그래프?');
    console.log('  - 회복 후: 원래 속도로 복귀?');
    console.log('  - 에러: 급증 시 일시적 발생?');
    console.log('');
    console.log('다음 단계:');
    console.log('  Level 3: multiple-join 테스트');
    console.log('  npm run test:pattern:l3:load');
    console.log('');
}