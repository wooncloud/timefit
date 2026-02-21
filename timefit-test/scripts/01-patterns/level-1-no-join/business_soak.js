/**
 * ========================================
 * Soak Test - 장시간 안정성 확인
 * ========================================
 *
 * 목적: 장시간 부하 시 메모리 누수, Connection 누수, 성능 저하 확인
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ?
 * JOIN: 0개 (단순 조회)
 * 권한: 불필요 (공개 API)
 *
 * 근거:
 * - VU 100: 일상적 부하 수준 (피크의 83%)
 *   → 500 VU 같은 고부하는 장시간 유지 불가능
 *   → 100 VU = 실제 운영 환경과 유사
 *
 * - 2분 Warm up: JVM JIT 컴파일, DB Connection Pool 안정화
 *
 * - 1시간 유지 ← 핵심!
 *   → 목적: 시간에 따른 성능 변화 감지
 *   → 메모리 누수: Heap 사용량이 우상향하는가?
 *   → Connection 누수: Pool이 점진적으로 고갈되는가?
 *   → GC Pause: Old GC가 빈번해지는가?
 *
 * - 2분 Cool down: Connection 정리, 최종 상태 확인
 *
 * 예상 결과:
 * - 시작 시: p95 ~ 80-100ms
 * - 1시간 후: p95 ~ 80-100ms (변화 없음) ✅
 * - 그래프: 평평한 수평선 (안정적)
 *
 * 실패 시나리오:
 * - 그래프 우상향: 메모리 누수 의심
 * - p95 점진적 증가: Connection Pool 고갈
 * - 주기적 스파이크: Old GC 빈번
 *
 * 실행 주기: 분기 1회 (배포 전 중요 변경 시)
 * 소요 시간: 1시간 5분
 *
 * 모니터링 포인트:
 * - k6 Dashboard: Duration 그래프 (평평한가?)
 * - Spring Actuator: Heap 사용량 (증가하는가?)
 * - HikariCP Metrics: Active Connections (고갈되는가?)
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
        { duration: '2m', target: 100 },   // Warm up: 안정화
        { duration: '1h', target: 100 },   // 1시간 유지! ← 핵심
        { duration: '2m', target: 0 },     // Cool down: 종료
    ],
    thresholds: {
        'http_req_duration': ['p(95)<300'],  // 1시간 후에도 목표 유지
        'http_req_failed': ['rate<0.01'],
        'errors': ['rate<0.01'],
    },
};

// 테스트용 고정 ID (seed-minimal.sql 기준)
const BUSINESS_IDS = [
    '30000000-0000-0000-0000-000000000001', // 헤어샵
    '30000000-0000-0000-0000-000000000002', // 네일샵
    '30000000-0000-0000-0000-000000000003', // 카페
];

// Setup: 테스트 정보 출력
export function setup() {
    console.log('========================================');
    console.log('Level 1: no-join - Soak Test (1시간)');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('');
    console.log('목표: VU 100으로 1시간 동안 안정성 확인');
    console.log('  - 메모리 누수 감지');
    console.log('  - Connection Pool 고갈 여부');
    console.log('  - 1시간 후에도 p95 < 300ms 유지');
    console.log('');
    console.log('⏰ 대략 1시간 5분 소요');
    console.log('========================================');
    console.log('');

    return {
        baseUrl: BASE_URL,
    };
}

// 메인 테스트 함수
export default function (data) {
    // 랜덤하게 업체 선택
    const businessId = BUSINESS_IDS[Math.floor(Math.random() * BUSINESS_IDS.length)];

    // 업체 상세 조회 (인증 불필요 - 공개 API)
    const url = `${data.baseUrl}/api/business/${businessId}`;
    const res = http.get(url);

    // 응답 시간 기록
    businessDuration.add(res.timings.duration);

    // 응답 데이터 검증
    let hasValidData = false;

    if (res.status === 200 && res.body) {
        try {
            const body = JSON.parse(res.body);

            // ResponseData 구조: { data: { businessId, businessName, ... } }
            if (body.data && body.data.businessId) {
                hasValidData = true;
            }
        } catch (e) {
            // JSON 파싱 실패
        }
    }

    // 검증
    const success = check(res, {
        '상태 코드 200': (r) => r.status === 200,
        '응답 데이터 유효': () => hasValidData,
        '응답 시간 < 300ms': (r) => r.timings.duration < 300,
    });

    errorRate.add(!success);

    // Think time - 1초 대기
    sleep(1);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('✅ Soak Test 완료 (1시간)');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 시작 vs 종료: 응답 시간 변화?');
    console.log('  - 그래프가 우상향? (메모리 누수)');
    console.log('  - 그래프가 평평? (안정적)');
    console.log('  - p95 그래프: 1시간 동안 안정적?');
    console.log('');
    console.log('🎉 장시간 안정성 테스트 완료!');
    console.log('');
}