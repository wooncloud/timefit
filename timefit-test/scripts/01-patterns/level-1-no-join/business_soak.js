/**
 * ========================================
 * Level 1: no-join - 업체 상세 조회 장시간 테스트 (Soak Test)
 * ========================================
 *
 * 목적: 장시간 부하 시 메모리 누수, 커넥션 누수, 성능 저하 확인
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ?
 * JOIN: 0개 (단순 조회)
 * 권한: 불필요 (공개 API) ← JWT 토큰 불필요!
 *
 * 테스트 시나리오:
 * - Stage 1: VU 50 (2분) - 워밍업
 * - Stage 2: VU 50 (1시간) - 장시간 유지
 * - Stage 3: VU 0 (2분) - 종료
 *
 * 예상 결과:
 * - 시작 시 avg: 20-30ms
 * - 1시간 후 avg: 20-30ms (변화 없음)
 * - 메모리 사용량: 평평한 그래프
 * - 커넥션 풀: 안정적
 *
 * 학습 포인트:
 * - "1시간 후에도 응답 시간이 일정한가?"
 * - "메모리 누수가 있는가?"
 * - "커넥션 풀이 고갈되는가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const businessDuration = new Trend('business_query_duration');

// 테스트 옵션 - Soak Test (1시간)
export const options = {
    stages: [
        { duration: '2m', target: 50 },   // 워밍업: 50 VU
        { duration: '1h', target: 50 },   // 유지: 50 VU (1시간!)
        { duration: '2m', target: 0 },    // 종료
    ],
    thresholds: {
        // 1시간 후에도 p95 < 100ms 유지
        'http_req_duration': ['p(95)<100'],
        // 에러율 1% 미만
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
    console.log('Level 1: no-join - 업체 상세 조회 장시간 테스트');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('권한: 불필요 (공개 API) ← JWT 토큰 불필요!');
    console.log('');
    console.log('테스트 패턴: Soak Test (1시간)');
    console.log('  - Stage 1: VU 50 (2분) - 워밍업');
    console.log('  - Stage 2: VU 50 (1시간) - 장시간 유지');
    console.log('  - Stage 3: VU 0 (2분) - 종료');
    console.log('');
    console.log('목표:');
    console.log('  - p95 < 100ms (1시간 후에도)');
    console.log('  - 에러율 < 1%');
    console.log('  - 메모리/커넥션 누수 없음');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 1시간 후 응답 시간 변화?');
    console.log('  - 메모리 누수 감지');
    console.log('  - 커넥션 풀 고갈 여부');
    console.log('');
    console.log('⏰ 대략 1시간 소요 ');
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
        '응답 시간 < 100ms': (r) => r.timings.duration < 100,
    });

    errorRate.add(!success);

    // Think time - 1초 대기
    sleep(1);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 1 Soak Test 완료 (1시간)');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 시작 vs 종료: 응답 시간 변화?');
    console.log('  - 그래프가 우상향? (메모리 누수)');
    console.log('  - 그래프가 평평? (안정적)');
    console.log('  - p95 그래프: 1시간 동안 안정적?');
    console.log('');
    console.log('다음 분석:');
    console.log('  - k6 Dashboard에서 Duration 그래프 확인');
    console.log('  - Spring Actuator로 메모리 사용량 확인');
    console.log('  - HikariCP 메트릭으로 커넥션 풀 상태 확인');
    console.log('');
    console.log('🎉 Soak Test 완료!');
    console.log('');
}