/**
 * ========================================
 * Level 1: no-join - 업체 조회 스트레스 테스트
 * ========================================
 *
 * 목적: 시스템 한계점을 찾아 병목 지점 파악
 *
 * API: GET /api/business/{businessId}
 * 쿼리: SELECT * FROM business WHERE id = ? (단 1개)
 * 권한: 공개 API (토큰 불필요)
 *
 * 테스트 시나리오:
 * - Stage 1: VU 50 (1분) - 워밍업
 * - Stage 2: VU 50→200 (2분) - 점진적 증가
 * - Stage 3: VU 200 (2분) - 안정
 * - Stage 4: VU 200→500 (2분) - 고부하
 * - Stage 5: VU 500 (2분) - 고부하 유지
 * - Stage 6: VU 500→1000 (2분) - 한계 테스트
 * - Stage 7: VU 1000 (1분) - 최대 부하
 * - Stage 8: VU 0 (30초) - 종료
 *
 * 예상 결과:
 * - VU 200까지: p95 < 100ms (안정)
 * - VU 500까지: p95 < 200ms (증가 시작)
 * - VU 800+: p95 급증 (커넥션 풀 병목)
 *
 * 학습 포인트:
 * - "어느 VU부터 응답 시간이 증가하는가?"
 * - "커넥션 풀 10개의 한계는?"
 * - "병목 지점이 언제 나타나는가?"
 */

import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

// 환경 변수
const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

// 커스텀 메트릭
const errorRate = new Rate('errors');
const businessDuration = new Trend('business_query_duration');

// 테스트 옵션 - Stress Test (스트레스 테스트)
export const options = {
    stages: [
        { duration: '1m', target: 50 },    // 워밍업: 50 VU
        { duration: '2m', target: 200 },   // 점진적 증가: 200 VU
        { duration: '2m', target: 200 },   // 안정: 200 VU
        { duration: '2m', target: 500 },   // 고부하: 500 VU
        { duration: '2m', target: 500 },   // 고부하 유지: 500 VU
        { duration: '2m', target: 1000 },  // 한계 테스트: 1000 VU
        { duration: '1m', target: 1000 },  // 최대 부하 유지: 1000 VU
        { duration: '30s', target: 0 },    // 종료
    ],
    thresholds: {
        // 관대한 threshold (한계 테스트이므로)
        'http_req_duration': ['p(95)<3000'],
        // 에러율 20% 미만 (스트레스 테스트이므로 관대)
        'http_req_failed': ['rate<0.2'],
        'errors': ['rate<0.2'],
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
    console.log('Level 1: no-join - 업체 조회 스트레스 테스트');
    console.log('========================================');
    console.log(`Target URL: ${BASE_URL}`);
    console.log('API: GET /api/business/{businessId}');
    console.log('권한: 공개 API (토큰 불필요)');
    console.log('');
    console.log('테스트 패턴: Stress Test');
    console.log('  - Stage 1: VU 50 (1분) - 워밍업');
    console.log('  - Stage 2: VU 50→200 (2분) - 점진적 증가');
    console.log('  - Stage 3: VU 200 (2분) - 안정');
    console.log('  - Stage 4: VU 200→500 (2분) - 고부하');
    console.log('  - Stage 5: VU 500 (2분) - 고부하 유지');
    console.log('  - Stage 6: VU 500→1000 (2분) - 한계 테스트');
    console.log('  - Stage 7: VU 1000 (1분) - 최대 부하');
    console.log('');
    console.log('학습 목표:');
    console.log('  - 어느 VU부터 응답 시간 증가?');
    console.log('  - 커넥션 풀 10개의 한계는?');
    console.log('  - 병목 지점이 언제 나타나는가?');
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

    // Think time (0.3초로 짧게 - 스트레스 테스트)
    sleep(0.3);
}

// Teardown: 테스트 종료 후 정보 출력
export function teardown(data) {
    console.log('');
    console.log('========================================');
    console.log('Level 1 Stress Test 완료');
    console.log('========================================');
    console.log('');
    console.log('분석 포인트:');
    console.log('  - 그래프에서 어느 VU부터 응답 시간 급증?');
    console.log('  - VU 200: 안정적이었나요?');
    console.log('  - VU 500: 증가 시작?');
    console.log('  - VU 800+: 병목 발생?');
    console.log('');
    console.log('💡 인사이트:');
    console.log('  - 병목 지점 = 커넥션 풀 한계!');
    console.log('  - 쿼리 1개짜리도 VU가 많으면 느려진다');
    console.log('');
    console.log('다음 단계:');
    console.log('  Level 2: single-join 테스트');
    console.log('  npm run test:pattern:l2:load');
    console.log('');
}