/**
 * ========================================
 * Concurrent Booking Test - 동시성 제어 검증
 * ========================================
 *
 * 목적: 같은 슬롯에 동시 예약 시 Optimistic Lock 동작 확인
 *
 * API: POST /api/reservation (단수!)
 * Body:
 *   - businessId, menuId, bookingSlotId
 *   - customerName, customerPhone
 *   - durationMinutes, totalPrice
 *
 * 근거:
 * - 50 VU, 50 iterations: 50명이 동시에 같은 슬롯 예약 시도
 *   → 실제 상황: 인기 시간대 (예: 토요일 오후 3시)
 *   → 1명만 성공, 49명 실패(409 Conflict) 예상
 *
 * - shared-iterations: 모든 VU가 동시에 시작
 *   → 최대한 동시성 극대화
 *   → Connection Pool 경합 강제 발생
 *
 * - maxDuration 30초: 충분한 시간 (실제는 1-2초 내 완료)
 *
 * 예상 결과:
 * - success_count: 1 ✅
 * - conflict_count: 49 ✅
 * - conflicts rate: 98% ✅
 *
 * 실패 시나리오:
 * - success_count: 2+ → Optimistic Lock 실패 (Race Condition)
 * - conflict_count: 0 → 모두 실패 (설정 오류)
 *
 * 실행 주기: 예약 로직 변경 시마다
 * 소요 시간: 30초
 *
 * 업계 표준:
 * - Martin Fowler: "Optimistic Lock은 충돌률 5% 이하일 때 효율적"
 * - 우리 케이스: 충돌률 98%이지만 테스트 목적이므로 정상
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
        concurrent_booking: {
            executor: 'shared-iterations',
            vus: 50,              // 50명 동시
            iterations: 50,       // 총 50번 시도
            maxDuration: '30s',   // 최대 30초
        },
    },
    thresholds: {
        'http_req_duration': ['p(95)<2000'],  // 동시성 테스트는 관대
        'conflicts': ['rate>0.95'],           // 49명 실패 = 정상!
        'success': ['rate<0.05'],             // 1명만 성공
    },
};

// 모든 VU가 같은 슬롯을 예약 시도
const FIXED_BUSINESS_ID = '30000000-0000-0000-0000-000000000001';
const FIXED_MENU_ID = '60000000-0000-0000-0000-000000000001';

export function setup() {
    console.log('========================================');
    console.log('Phase 2: concurrent-booking - 동시 예약 테스트');
    console.log('========================================');
    console.log('');

    // 1. 50명의 고객 계정 로그인 (customer1~customer50)
    console.log('📋 50명의 고객 계정 로그인 중...');
    const customerTokens = [];

    for (let i = 1; i <= 50; i++) {
        const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
            email: `customer${i}@timefit.test`,
            password: 'password123'
        }), {
            headers: { 'Content-Type': 'application/json' }
        });

        if (loginRes.status !== 200) {
            console.error(`❌ customer${i} 로그인 실패: ${loginRes.status}`);
            throw new Error(`customer${i} 로그인 실패`);
        }

        const loginBody = JSON.parse(loginRes.body);
        customerTokens.push(loginBody.data.accessToken);
    }

    console.log(`✅ ${customerTokens.length}명의 고객 로그인 성공`);
    console.log('');

    // 2. 실제 슬롯 조회 (동적으로 가져오기)
    // ⚠️ 중요: 7일 후 날짜 사용 (시간대 이슈 회피 + 과거 날짜 방지)
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 7); // 7일 후
    const dateStr = futureDate.toISOString().split('T')[0];

    const slotsUrl = `${BASE_URL}/api/business/${FIXED_BUSINESS_ID}/booking-slot/menu/${FIXED_MENU_ID}?startDate=${dateStr}&endDate=${dateStr}`;
    const slotsRes = http.get(slotsUrl, {
        headers: { 'Authorization': `Bearer ${customerTokens[0]}` }
    });

    if (slotsRes.status !== 200) {
        console.error('❌ 슬롯 조회 실패:', slotsRes.status);
        throw new Error('슬롯 조회 실패');
    }

    const slotsBody = JSON.parse(slotsRes.body);
    const availableSlots = slotsBody.data.slots.filter(slot => slot.isAvailable);

    if (availableSlots.length === 0) {
        throw new Error('사용 가능한 슬롯이 없습니다. seed 데이터를 확인하세요.');
    }

    const targetSlot = availableSlots[0];
    console.log(`✅ 테스트 슬롯 조회 성공: ${targetSlot.slotId}`);
    console.log(`   날짜: ${targetSlot.slotDate}, 시간: ${targetSlot.startTime}`);
    console.log('');
    console.log('테스트 시나리오:');
    console.log('  - 50명의 서로 다른 고객이 동시에 같은 슬롯 예약');
    console.log('  - 1명만 성공, 49명 실패 예상');
    console.log('  - customer1~customer50 계정 사용');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 동시성 제어 작동?');
    console.log('  - Optimistic Lock 효과?');
    console.log('  - 실패율 98%?');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
        customerTokens: customerTokens,      // 50개 토큰 배열
        slotId: targetSlot.slotId,
        menuId: FIXED_MENU_ID,
        businessId: FIXED_BUSINESS_ID,
    };
}

export default function (data) {
    // 각 VU는 자신의 인덱스에 맞는 토큰 사용 (VU 1 → customer1, VU 2 → customer2, ...)
    const myTokenIndex = __VU - 1; // VU는 1부터 시작하므로 -1
    const myToken = data.customerTokens[myTokenIndex];

    const payload = JSON.stringify({
        businessId: data.businessId,
        menuId: data.menuId,
        bookingSlotId: data.slotId,
        customerName: `고객${__VU}`,
        customerPhone: '01012345678',
        durationMinutes: 60,
        totalPrice: 15000,
        notes: `customer${__VU} 동시 예약 테스트`
    });

    // ✅ 올바른 API 엔드포인트: /api/reservation (단수!)
    const url = `${data.baseUrl}/api/reservation`;
    const res = http.post(url, payload, {
        headers: {
            'Authorization': `Bearer ${myToken}`,
            'Content-Type': 'application/json'
        },
    });

    reservationDuration.add(res.timings.duration);

    // 결과 기록
    if (res.status === 201) {
        successRate.add(true);
        successCount.add(1);
        console.log(`✅ VU ${__VU} (customer${__VU}): 예약 성공!`);
    } else if (res.status === 409) {
        // 409 Conflict = 정상적인 동시성 제어!
        conflictRate.add(true);
        conflictCount.add(1);
    } else {
        errorRate.add(true);
        console.log(`❌ VU ${__VU} (customer${__VU}): 예상치 못한 에러 (${res.status})`);
        if (res.body) {
            console.log(`   응답: ${res.body.substring(0, 200)}`);
        }
    }

    const success = check(res, {
        '성공 또는 충돌': (r) => r.status === 201 || r.status === 409,
    });

    sleep(0.1);
}

export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('✅ Concurrent Booking Test 완료');
    console.log('========================================');
    console.log('');
    console.log('💡 결과 해석:');
    console.log('  - 성공 1명 + 충돌 49명 = 동시성 제어 성공!');
    console.log('  - 충돌율 98% = Optimistic Lock 정상 작동!');
    console.log('  - 성공 2명 이상 = 동시성 제어 실패!');
    console.log('');
    console.log('📊 기대 결과:');
    console.log('  - success_count: 1');
    console.log('  - conflict_count: 49');
    console.log('  - conflicts rate: 98%');
    console.log('');
}