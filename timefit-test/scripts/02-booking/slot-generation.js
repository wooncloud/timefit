/**
 * ========================================
 * Phase 2: slot-generation - 슬롯 생성 성능 테스트
 * ========================================
 *
 * 목적: 메뉴 생성 시 자동 슬롯 생성의 성능 측정
 *
 * API: POST /api/business/{businessId}/menu
 * Body:
 *   - serviceName, price, orderType: RESERVATION_BASED
 *   - durationMinutes: 60
 *   - autoGenerateSlots: true
 *   - slotSettings: { startDate, endDate, slotDuration, ... }
 *
 * 시나리오:
 * - 30일간 슬롯 자동 생성 (약 240개 슬롯)
 * - VU 10으로 동시 생성 테스트
 *
 * 학습 포인트:
 * - "대량 INSERT 성능은?"
 * - "트랜잭션 처리 시간은?"
 * - "동시 생성 시 충돌은?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const slotGenerationDuration = new Trend('slot_generation_duration');

export const options = {
    stages: [
        { duration: '30s', target: 5 },   // 워밍업
        { duration: '2m', target: 10 },   // 메인 테스트
        { duration: '30s', target: 0 },   // 종료
    ],
    thresholds: {
        // 슬롯 생성은 무거운 작업: p95 < 3초
        'http_req_duration': ['p(95)<3000'],
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
    console.log('Phase 2: slot-generation - 슬롯 생성 성능');
    console.log('========================================');
    console.log('');
    console.log('⚠️  주의: 이 테스트는 API 스펙 확인 필요');
    console.log('   - autoGenerateSlots 기능 구현 여부 확인');
    console.log('   - BookingSlot 생성 API 별도 존재 확인');
    console.log('');

    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.com',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공');
    console.log('');
    console.log('테스트 시나리오:');
    console.log('  - 메뉴 생성 + 30일 슬롯 자동 생성');
    console.log('  - 약 240개 슬롯 INSERT');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 대량 INSERT 성능은?');
    console.log('  - 트랜잭션 시간은?');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
        accessToken: body.data.accessToken,
    };
}

export default function (data) {
    const businessId = BUSINESS_IDS[Math.floor(Math.random() * BUSINESS_IDS.length)];

    // 오늘부터 30일간 슬롯 생성
    const today = new Date();
    const endDate = new Date(today);
    endDate.setDate(today.getDate() + 30);

    const payload = JSON.stringify({
        businessType: 'BD003',
        categoryName: '헤어',
        serviceName: `테스트 서비스 ${Date.now()}`,
        price: 30000,
        orderType: 'RESERVATION_BASED',
        durationMinutes: 60,
        autoGenerateSlots: true,
        slotSettings: {
            startDate: today.toISOString().split('T')[0],
            endDate: endDate.toISOString().split('T')[0],
            slotDuration: 60,
            daysOfWeek: [1, 2, 3, 4, 5], // 월-금
            dailyStartTime: '09:00',
            dailyEndTime: '18:00'
        }
    });

    const url = `${data.baseUrl}/api/business/${businessId}/menu`;
    const res = http.post(url, payload, {
        headers: {
            'Authorization': `Bearer ${data.accessToken}`,
            'Content-Type': 'application/json'
        },
    });

    slotGenerationDuration.add(res.timings.duration);

    const success = check(res, {
        '상태 코드 201': (r) => r.status === 201,
        '응답 시간 < 3초': (r) => r.timings.duration < 3000,
    });

    errorRate.add(!success);

    sleep(2); // 슬롯 생성은 무거운 작업
}

export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Phase 2: slot-generation 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - p95가 3초 이하?');
    console.log('  - 240개 슬롯 INSERT 시간은?');
    console.log('  - 동시 생성 시 충돌 없었나?');
    console.log('');
}