/**
 * ========================================
 * Slot Generation Test - 대량 INSERT 성능 확인
 * ========================================
 *
 * 목적: 메뉴 생성 시 자동 슬롯 생성(30일, 540개)의 성능 측정
 *
 * API: POST /api/business/{businessId}/menu
 * Body:
 *   - serviceName, price, orderType: RESERVATION_BASED
 *   - durationMinutes: 60
 *   - autoGenerateSlots: true
 *   - slotSettings: { startDate, endDate, slotIntervalMinutes, specificTimeRanges }
 *
 * 근거:
 * - VU 5-10: 슬롯 생성은 무거운 작업 (대량 INSERT)
 *   → 30일 × 18개/일 = 540개 슬롯 INSERT
 *   → 동시에 많이 실행되는 작업 아님 (업체 등록은 드뭄)
 *
 * - 2분 유지: 충분한 반복 횟수 (10-20회)
 *   → 평균 응답 시간의 신뢰도 확보
 *   → DB Transaction Lock 경합 확인
 *
 * - p95 < 5000ms (5초): 대량 INSERT는 느릴 수 있음
 *   → 540개 INSERT = Batch Insert 시 1-3초 예상
 *   → 5초 = 여유 있는 목표 (UX상 허용 가능)
 *
 * 예상 결과:
 * - VU 5, p95 < 2000ms: Batch Insert 효과 우수 ✅
 * - VU 5, p95 < 5000ms: 목표 달성 ✅
 * - VU 10, p95 > 5000ms: Transaction Lock 경합 ⚠️
 *
 * 실행 주기: 슬롯 생성 로직 변경 시
 * 소요 시간: 3분
 *
 * 최적화 포인트:
 * - Batch Insert 사용 (한 번에 100개씩)
 * - Transaction 분리 (메뉴 생성 / 슬롯 생성)
 * - 비동기 처리 고려 (사용자는 메뉴 생성 완료 후 이탈)
 *
 * 업계 표준:
 * - 대량 INSERT: Batch 사용 시 10배 빠름
 * - Transaction: 짧게 유지 (Lock 경합 최소화)
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const slotGenerationDuration = new Trend('slot_generation_duration');

export const options = {
    stages: [
        { duration: '30s', target: 5 },    // Warm up
        { duration: '2m', target: 10 },    // 메인 테스트
        { duration: '30s', target: 0 },    // Cool down
    ],
    thresholds: {
        'http_req_duration': ['p(95)<5000'],  // 5초 (대량 INSERT)
        'http_req_failed': ['rate<0.05'],
        'errors': ['rate<0.05'],
    },
};

// owner1이 소유한 업체만!
const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001'
];

export function setup() {
    console.log('========================================');
    console.log('Phase 2: slot-generation - 슬롯 생성 성능');
    console.log('========================================');
    console.log('');

    const loginRes = http.post(`${BASE_URL}/api/auth/signin`, JSON.stringify({
        email: 'owner1@timefit.com',
        password: 'password123'
    }), {
        headers: { 'Content-Type': 'application/json' }
    });

    if (loginRes.status !== 200) {
        console.error('❌ 로그인 실패:', loginRes.status);
        throw new Error('로그인 실패');
    }

    const body = JSON.parse(loginRes.body);
    console.log('✅ 로그인 성공: owner1@timefit.com');
    console.log(`✅ 업체 ID: ${BUSINESS_IDS[0]}`);
    console.log('');
    console.log('테스트 시나리오:');
    console.log('  - 메뉴 생성 + 30일 슬롯 자동 생성');
    console.log('  - 09:00-18:00, 30분 간격 = 18개/일');
    console.log('  - 30일 × 18개 = 약 540개 슬롯 INSERT');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 대량 INSERT 성능은?');
    console.log('  - 트랜잭션 시간은?');
    console.log('  - 동시 생성 시 충돌은?');
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
        businessType: 'BD008',
        categoryName: 'Hair',
        serviceName: `테스트 서비스 ${Date.now()}`,
        price: 30000,
        description: 'k6 성능 테스트용 메뉴',
        orderType: 'RESERVATION_BASED',
        durationMinutes: 60,
        autoGenerateSlots: true,
        slotSettings: {
            startDate: today.toISOString().split('T')[0],      // "2025-12-20"
            endDate: endDate.toISOString().split('T')[0],      // "2026-01-19"
            slotIntervalMinutes: 30,                            // 슬롯 간격 (15~480분)
            specificTimeRanges: [
                {
                    startTime: '09:00',
                    endTime: '18:00'
                }
            ]
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

    // 응답 검증
    let hasValidData = false;
    let slotCount = 0;

    if (res.status === 201 && res.body) {
        try {
            const body = JSON.parse(res.body);
            if (body.data && body.data.menuId) {
                hasValidData = true;
                // 슬롯 생성 정보가 있다면 확인
                if (body.data.slotGenerationResult) {
                    slotCount = body.data.slotGenerationResult.totalCreated || 0;
                }
            }
        } catch (e) {
            // JSON 파싱 실패
        }
    }

    const success = check(res, {
        '상태 코드 201': (r) => r.status === 201,
        '응답 데이터 유효': () => hasValidData,
        '응답 시간 < 5초': (r) => r.timings.duration < 5000,
    });

    if (!success) {
        console.log(`[ERROR] 슬롯 생성 실패: status=${res.status}, body=${res.body ? res.body.substring(0, 200) : 'empty'}`);
    } else if (slotCount > 0) {
        console.log(`[SUCCESS] 슬롯 생성 성공: ${slotCount}개, ${res.timings.duration}ms`);
    }

    errorRate.add(!success);

    sleep(2); // 슬롯 생성은 무거운 작업
}

export function teardown(data) {
    console.log('');
    console.log('✅ Slot Generation Test 완료');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - p95가 5초 이하?');
    console.log('  - 540개 슬롯 INSERT 시간은?');
    console.log('  - 동시 생성 시 충돌 없었나?');
    console.log('');
    console.log('다음 단계:');
    console.log('  - 생성된 슬롯 확인');
    console.log('  - DB INSERT 성능 분석');
    console.log('  - 트랜잭션 시간 측정');
    console.log('');
}