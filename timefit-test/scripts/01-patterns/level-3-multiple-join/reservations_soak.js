/**
 * Level 3: multiple-join - 예약 목록 조회 장시간 테스트
 *
 * API: GET /api/business/{businessId}/reservations
 * JOIN: 3-4개
 *
 * 목표: 메모리 누수, 커넥션 누수, 성능 저하 확인
 * 테스트: VU 50으로 1시간 유지
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const reservationDuration = new Trend('reservation_query_duration');

export const options = {
    stages: [
        { duration: '2m', target: 50 },     // 워밍업
        { duration: '1h', target: 50 },     // 1시간 유지!
        { duration: '2m', target: 0 },      // 종료
    ],
    thresholds: {
        'http_req_duration': ['p(95)<1000'],
        'http_req_failed': ['rate<0.05'],
        'errors': ['rate<0.05'],
    },
};

// owner1@timefit.com이 소유한 업체 ID (권한 문제 방지!)
const BUSINESS_ID = '30000000-0000-0000-0000-000000000001';

export function setup() {
    console.log('Level 3: multiple-join - 장시간 테스트 (1시간)');

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
    console.log('학습 목표:');
    console.log('  - 1시간 후 응답 시간 증가?');
    console.log('  - 메모리/커넥션 누수?');
    console.log('  - 안정적으로 유지?');
    console.log('');
    console.log('⏰ 1시간 소요 - 점심시간 추천!');
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
        '응답 시간 < 1초': (r) => r.timings.duration < 1000,
    });

    errorRate.add(!success);

    sleep(1); // 1초 대기
}

export function teardown(data) {
    console.log('');
    console.log('Level 3 Soak Test 완료 (1시간)');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 시작 vs 종료: 응답 시간 변화?');
    console.log('  - 그래프가 우상향? (메모리 누수)');
    console.log('  - 그래프가 평평? (안정적)');
    console.log('');
    console.log('🎉 Phase 1 완료!');
    console.log('');
    console.log('다음 단계: Phase 2 (예약 도메인 실전)');
    console.log('');
}