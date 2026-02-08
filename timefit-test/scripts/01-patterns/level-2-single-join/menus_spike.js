/**
 * ========================================
 * Spike Test - 트래픽 급증 회복력 확인
 * ========================================
 *
 * 목적: 트래픽 급증 시 시스템 회복력(Resilience) 확인
 *
 * API: GET /api/business/{businessId}/menu
 * 쿼리:
 *   1. SELECT * FROM menu WHERE business_id = ?
 *   2. SELECT * FROM business_category WHERE id IN (...)
 * JOIN: 1개
 * 권한: 인증 필요 (JWT 토큰)
 *
 * 근거:
 * - VU 100 (1분): 정상 부하 (Baseline)
 *
 * - VU 100→500 (30초): 급증! ⚡
 *   → 실제 상황: 마케팅 이벤트 시작, 특정 시간대 예약 몰림
 *   → 30초 = 급격한 변화 (사용자는 기다리지 않음)
 *
 * - VU 500 (1분): 피크 유지
 *   → 목적: 급증 후에도 안정적으로 유지되는가?
 *
 * - VU 500→100 (30초): 급감
 *
 * - VU 100 (1분): 회복 확인 ← 핵심!
 *   → 목적: 원래 성능으로 복귀하는가?
 *
 * 예상 결과:
 * - 정상: p95 ~ 100ms
 * - 급증 시: p95 ~ 500ms (일시적 증가)
 * - 회복 후: p95 ~ 100ms (원래대로 복귀) ✅
 *
 * 실행 주기: 월 1회
 * 소요 시간: 5분
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const menuDuration = new Trend('menu_query_duration');

export const options = {
    stages: [
        { duration: '1m', target: 100 },    // 정상 부하
        { duration: '30s', target: 500 },   // 급증! ⚡
        { duration: '1m', target: 500 },    // 피크 유지
        { duration: '30s', target: 100 },   // 급감
        { duration: '1m', target: 100 },    // 회복 확인 ✅
        { duration: '30s', target: 0 },     // 종료
    ],
    thresholds: {
        'http_req_duration': ['p(95)<1000'],  // 급증 시 일시적 증가 허용
        'http_req_failed': ['rate<0.05'],
        'errors': ['rate<0.05'],
    },
};

const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001',
    '30000000-0000-0000-0000-000000000002',
    '30000000-0000-0000-0000-000000000003',
];

export function setup() {
    console.log('========================================');
    console.log('Level 2: single-join - Spike Test');
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
    console.log('목표: 급증 트래픽 회복력 확인');
    console.log('  - VU 100 (정상) → 500 (급증) → 100 (회복)');
    console.log('  - 회복 후 원래 성능으로 복귀?');
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
    console.log('✅ Spike Test 완료');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 급증 시: V자 그래프 확인');
    console.log('  - 회복 후: 원래 속도로 복귀?');
    console.log('  - 에러: 급증 시 일시적 발생?');
    console.log('');
    console.log('다음 단계: Level 3 (multiple-join) 테스트');
    console.log('');
}