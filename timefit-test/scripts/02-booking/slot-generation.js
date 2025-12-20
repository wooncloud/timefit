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
 *   - slotSettings: { startDate, endDate, slotIntervalMinutes, specificTimeRanges }
 *
 * 시나리오:
 * - 30일간 슬롯 자동 생성
 * - 09:00-18:00, 30분 간격 = 하루 18개 슬롯
 * - 30일 × 18개 = 약 540개 슬롯 INSERT
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
        // 슬롯 생성은 무거운 작업: p95 < 5초
        'http_req_duration': ['p(95)<5000'],
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
    console.log('로그인 성공: owner1@timefit.com');
    console.log(`업체 ID: ${BUSINESS_IDS[0]} (owner1 소유)`);
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
    console.log('========================================');
    console.log('Phase 2: slot-generation 완료');
    console.log('========================================');
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