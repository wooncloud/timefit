/**
 * ========================================
 * Phase 2: concurrent-booking - 동시 예약 테스트
 * ========================================
 *
 * 목적: 같은 슬롯에 동시 예약 시도 시 동시성 제어 확인
 *
 * API: POST /api/reservations
 * Body:
 *   - businessId, menuId, bookingSlotId
 *   - customerName, customerPhone
 *   - durationMinutes, totalPrice
 *
 * 시나리오:
 * - 50명이 동시에 같은 슬롯 예약 시도
 * - 단 1명만 성공해야 함 (Optimistic Lock)
 * - 나머지 49명은 실패 (409 Conflict)
 *
 * 학습 포인트:
 * - "동시성 제어가 작동하는가?"
 * - "실패율이 98%인가? (1명 성공, 49명 실패)"
 * - "Optimistic Lock이 효과적인가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const conflictRate = new Rate('conflicts'); // 409 에러 = 정상!
const successRate = new Rate('success');
const reservationDuration = new Trend('reservation_duration');
const successCount = new Counter('success_count');
const conflictCount = new Counter('conflict_count');

export const options = {
    scenarios: {
        // 50명이 동시에 같은 슬롯 예약 시도
        concurrent_booking: {
            executor: 'shared-iterations',
            vus: 50,              // 50명 동시
            iterations: 50,       // 총 50번 시도
            maxDuration: '30s',   // 최대 30초
        },
    },
    thresholds: {
        // 동시성 테스트이므로 관대한 threshold
        'http_req_duration': ['p(95)<2000'],
        // 49명 실패 = 정상 (98%)
        'conflicts': ['rate>0.95'],
        // 1명만 성공 (2%)
        'success': ['rate<0.05'],
    },
};

// 모든 VU가 같은 슬롯을 예약 시도
const FIXED_BUSINESS_ID = '30000000-0000-0000-0000-000000000001'; // Hair Salon
const FIXED_MENU_ID = '60000000-0000-0000-0000-000000000001'; // Basic Haircut (60min)

export function setup() {
    console.log('========================================');
    console.log('Phase 2: concurrent-booking - 동시 예약 테스트');
    console.log('========================================');

    // 1. 로그인 (올바른 이메일)
    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'customer1@timefit.com',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    const loginBody = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공');

    // 2. 실제 슬롯 조회 (동적으로 가져오기)
    const today = new Date();
    const dateStr = today.toISOString().split('T')[0];

    const slotsUrl = `${BASE_URL}/api/business/${FIXED_BUSINESS_ID}/booking-slot/menu/${FIXED_MENU_ID}?startDate=${dateStr}&endDate=${dateStr}`;
    const slotsRes = http.get(slotsUrl, {
        headers: { 'Authorization': `Bearer ${loginBody.data.accessToken}` }
    });

    const slotsBody = JSON.parse(slotsRes.body);
    const availableSlots = slotsBody.data.slots.filter(slot => slot.isAvailable);

    if (availableSlots.length === 0) {
        throw new Error('사용 가능한 슬롯이 없습니다. seed 데이터를 확인하세요.');
    }

    const targetSlot = availableSlots[0];
    console.log(`✅ 테스트 슬롯 조회 성공: ${targetSlot.id}`);
    console.log(`   날짜: ${targetSlot.slotDate}, 시간: ${targetSlot.startTime}`);
    console.log('');
    console.log('테스트 시나리오:');
    console.log('  - 50명이 동시에 같은 슬롯 예약');
    console.log('  - 1명만 성공, 49명 실패 예상');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 동시성 제어 작동?');
    console.log('  - Optimistic Lock 효과?');
    console.log('  - 실패율 98%?');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
        accessToken: loginBody.data.accessToken,
        slotId: targetSlot.id,
        menuId: FIXED_MENU_ID,
        businessId: FIXED_BUSINESS_ID,
    };
}

export default function (data) {
    const payload = JSON.stringify({
        businessId: data.businessId,
        menuId: data.menuId,
        bookingSlotId: data.slotId,
        customerName: `고객${__VU}`,
        customerPhone: '01012345678',
        durationMinutes: 60,
        totalPrice: 15000, // Basic Haircut 가격
        notes: `VU ${__VU} 동시 예약 테스트`
    });

    const url = `${data.baseUrl}/api/reservations`;
    const res = http.post(url, payload, {
        headers: {
            'Authorization': `Bearer ${data.accessToken}`,
            'Content-Type': 'application/json'
        },
    });

    reservationDuration.add(res.timings.duration);

    // 결과 기록
    if (res.status === 201) {
        successRate.add(true);
        successCount.add(1);
        console.log(`✅ VU ${__VU}: 예약 성공!`);
    } else if (res.status === 409) {
        // 409 Conflict = 정상적인 동시성 제어!
        conflictRate.add(true);
        conflictCount.add(1);
    } else {
        errorRate.add(true);
        console.log(`❌ VU ${__VU}: 예상치 못한 에러 (${res.status})`);
    }

    const success = check(res, {
        '성공 또는 충돌': (r) => r.status === 201 || r.status === 409,
    });

    sleep(0.1);
}

export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Phase 2: concurrent-booking 완료');
    console.log('========================================');
    console.log('');
    console.log('💡 결과 해석:');
    console.log('  - 성공 1명 + 충돌 49명 = 동시성 제어 성공!');
    console.log('  - 충돌율 98% = Optimistic Lock 정상 작동!');
    console.log('  - 성공 2명 이상 = 동시성 제어 실패!');
    console.log('');
}