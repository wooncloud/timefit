/**
 * ========================================
 * Phase 2: query-performance - 슬롯 조회 성능 테스트
 * ========================================
 *
 * 목적: 날짜별 슬롯 조회 쿼리의 성능 측정
 *
 * API: GET /api/business/{businessId}/booking-slot/menu/{menuId}?startDate={date}&endDate={date}
 *
 * 쿼리:
 *   SELECT * FROM booking_slot
 *   WHERE business_id = ? AND menu_id = ?
 *   AND slot_date BETWEEN ? AND ? AND is_available = true
 *   ORDER BY slot_date, start_time
 *
 * 시나리오:
 * - VU 100으로 고부하 조회
 * - 날짜를 랜덤하게 변경하여 조회
 *
 * 학습 포인트:
 * - "인덱스가 제대로 작동하는가?"
 * - "캐싱이 필요한가?"
 * - "조회 성능이 충분한가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

const errorRate = new Rate('errors');
const queryDuration = new Trend('query_duration');

export const options = {
    stages: [
        { duration: '30s', target: 50 },   // 워밍업
        { duration: '2m', target: 100 },   // 고부하
        { duration: '30s', target: 0 },    // 종료
    ],
    thresholds: {
        // 슬롯 조회는 빨라야 함: p95 < 200ms
        'http_req_duration': ['p(95)<200'],
        'http_req_failed': ['rate<0.01'],
        'errors': ['rate<0.01'],
    },
};

const BUSINESS_MENUS = [
    // Hair Salon
    { businessId: '30000000-0000-0000-0000-000000000001', menuId: '60000000-0000-0000-0000-000000000001' },
    { businessId: '30000000-0000-0000-0000-000000000001', menuId: '60000000-0000-0000-0000-000000000009' },
    // Nail Shop
    { businessId: '30000000-0000-0000-0000-000000000002', menuId: '60000000-0000-0000-0000-000000000011' },
    { businessId: '30000000-0000-0000-0000-000000000002', menuId: '60000000-0000-0000-0000-000000000015' },
];

export function setup() {
    console.log('========================================');
    console.log('Phase 2: query-performance - 슬롯 조회 성능');
    console.log('========================================');
    console.log('');
    console.log('테스트 시나리오:');
    console.log('  - VU 100으로 슬롯 조회');
    console.log('  - 날짜 랜덤 변경');
    console.log('  - 여러 업체/메뉴 랜덤 조회');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 인덱스 효과는?');
    console.log('  - p95 < 200ms 달성?');
    console.log('  - 캐싱이 필요한가?');
    console.log('========================================');
    console.log('');

    return { baseUrl: BASE_URL };
}

export default function (data) {
    // 랜덤하게 업체/메뉴 선택
    const target = BUSINESS_MENUS[Math.floor(Math.random() * BUSINESS_MENUS.length)];

    // 오늘부터 7일 중 랜덤 날짜
    const today = new Date();
    const randomDays = Math.floor(Math.random() * 7);
    const targetDate = new Date(today);
    targetDate.setDate(today.getDate() + randomDays);
    const dateStr = targetDate.toISOString().split('T')[0];

    // API 호출 (startDate, endDate 동일 = 하루 조회)
    const url = `${data.baseUrl}/api/business/${target.businessId}/booking-slot/menu/${target.menuId}?startDate=${dateStr}&endDate=${dateStr}`;
    const res = http.get(url);

    queryDuration.add(res.timings.duration);

    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 시간 < 200ms': (r) => r.timings.duration < 200,
        '슬롯 데이터 존재': (r) => {
            try {
                const body = JSON.parse(r.body);
                return body.data && Array.isArray(body.data.slots);
            } catch (e) {
                return false;
            }
        }
    });

    errorRate.add(!success);

    sleep(0.3);
}

export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Phase 2: query-performance 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - p95 < 200ms 달성?');
    console.log('  - 인덱스가 잘 작동했나?');
    console.log('  - VU 100에서도 안정적?');
    console.log('');
    console.log('💡 개선 방안:');
    console.log('  - Redis 캐싱 도입 검토');
    console.log('  - 복합 인덱스 최적화');
    console.log('');
}