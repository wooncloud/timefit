import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '30s', target: 50 },     // Warm-up
        { duration: '1m', target: 200 },     // Ramp-up
        { duration: '1m', target: 500 },     // High load
        { duration: '1m', target: 1000 },    // Peak load
        { duration: '2m', target: 1000 },    // Sustain peak
        { duration: '30s', target: 0 },      // Cool-down
    ],

    thresholds: {
        http_req_duration: ['p(95)<500', 'p(99)<1000'],
        http_req_failed: ['rate<0.1'],
        http_reqs: ['rate>100'],
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const res = http.get(`${BASE_URL}/api/auth/health`);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response has data': (r) => {
            try {
                return r.json('data') !== null;
            } catch (e) {
                return false;
            }
        },
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}