/**
 * ========================================
 * Stress Test - 비즈니스 목표 + 한계 탐색
 * ========================================
 *
 * 목적: 비즈니스 목표(500명) 달성 확인 및 시스템 한계 파악
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ? (단 1개)
 * 권한: 공개 API (토큰 불필요)
 *
 * 근거:
 * - VU 100 (2분): Warm up
 *   → JVM JIT 컴파일 완료 (최소 3-5회 실행)
 *   → DB Cache 워밍업 (자주 조회되는 데이터)
 *
 * - VU 500 (5분): 비즈니스 목표 부하 ← 핵심!
 *   → 실제 피크 120명 × 안전계수 4배 = 480명 → 500명
 *   → 안전계수 = 트래픽 변동성(×2) + 마케팅 이벤트(×2)
 *   → 5분 = 충분한 GC 사이클 (최소 3회) + 안정 상태 확인
 *   → 목표: p95 < 300ms (Supabase Free Plan 한계)
 *
 * - VU 750 (2분): 1.5배 부하 (여유 확인)
 *   → 예상: p95 < 500ms (Connection Pool 경합 시작)
 *   → 목적: 확장 여유 50% 확보 여부 확인
 *
 * - VU 1000 (5분): 2배 부하 (한계 탐색) ← 확장 계획 검증
 *   → 예상: p95 > 1000ms (Supabase 60 Connection Limit 병목)
 *   → 목적: 병목 지점 파악 (Connection Pool? CPU? Network?)
 *   → 5분 = 장시간 고부하 시 메모리 누수, GC Pause 확인
 *
 * 예상 결과:
 * - VU 100: p95 < 100ms ✅ (여유)
 * - VU 500: p95 < 300ms ✅ (목표 달성!) ← 면접 포인트
 * - VU 750: p95 < 500ms ⚠️ (Connection Pool 경합)
 * - VU 1000: p95 > 1000ms ❌ (Supabase Free Plan 한계)
 *
 * 실행 주기: 월 1회 (정기 성능 점검)
 * 소요 시간: 15분
 *
 * 면접 포인트:
 * "VU 500에서 p95 < 300ms 달성으로 비즈니스 목표 검증 완료,
 *  VU 1000까지 테스트하여 확장 계획(Supabase Pro 업그레이드) 수립"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:80';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const businessDuration = new Trend('business_query_duration');

// 테스트 옵션
export const options = {
    stages: [
        { duration: '2m', target: 100 },   // Warm up: JVM JIT + DB Cache
        { duration: '5m', target: 500 },   // 목표 부하: 비즈니스 목표 검증 ✅
        { duration: '2m', target: 750 },   // 1.5배 부하: 확장 여유 확인
        { duration: '5m', target: 1000 },  // 2배 부하: 한계 탐색 + 병목 파악
        { duration: '1m', target: 0 },     // Cool down: 정상 종료
    ],
    thresholds: {
        'http_req_duration': ['p(95)<3000'],  // 관대한 threshold (한계 테스트)
        'http_req_failed': ['rate<0.1'],      // 에러율 10% 허용
        'errors': ['rate<0.1'],
    },
};

// 테스트용 고정 ID (seed-minimal.sql 기준)
const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001', // 헤어샵
    '30000000-0000-0000-0000-000000000002', // 네일샵
    '30000000-0000-0000-0000-000000000003', // 카페
];

// Setup: 테스트 시작 전 정보 출력
export function setup() {
    console.log('========================================');
    console.log('Level 1: no-join - Stress Test');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('');
    console.log('목표: 비즈니스 목표(500 VU) + 한계 탐색(1000 VU)');
    console.log('  - VU 500: p95 < 300ms (목표 달성)');
    console.log('  - VU 1000: 병목 지점 파악');
    console.log('');
    console.log('⏱️  소요 시간: 약 15분');
    console.log('========================================');
    console.log('');

    return { baseUrl: BASE_URL };
}

// 메인 테스트 함수
export default function (data) {
    // 랜덤하게 업체 선택
    const businessId = BUSINESS_IDS[Math.floor(Math.random() * BUSINESS_IDS.length)];

    // 업체 정보 조회 (공개 API)
    const url = `${data.baseUrl}/api/business/${businessId}`;
    const res = http.get(url);

    // 응답 시간 기록
    businessDuration.add(res.timings.duration);

    // 검증
    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 본문 존재': (r) => r.body && r.body.length > 0,
    });

    errorRate.add(!success);

    // Think time (0.3초 - 짧게 유지하여 부하 증가)
    sleep(0.3);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('✅ Stress Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - VU 500에서 p95 < 300ms 달성했나요?');
    console.log('  - 어느 VU부터 응답 시간 급증했나요?');
    console.log('  - 병목 지점은? (Connection Pool? CPU?)');
    console.log('');
    console.log('다음 단계: Level 2 (single-join) 테스트');
    console.log('');
}