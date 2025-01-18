import http from 'k6/http';
import { check, sleep } from 'k6';

const token = __ENV.JWT_TOKEN;

export let options = {
    scenarios: {
        ramp_up: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 500 }, // Ramp-up to 10 users
                { duration: '1m', target: 1000 }, // Ramp-up to 10 users
                { duration: '1m', target: 2000 }, // Gradually increase to 50 users
                { duration: '1m', target: 2000 }, // Steady-state at 50 users
                { duration: '2m', target: 5000 }, // Gradually increase to 50 users
                { duration: '1m', target: 0 }, // Ramp-down to 0 users
            ],
            gracefulRampDown: '30s',
        },
    },
    thresholds: {
        http_req_duration: ['p(70)<6000'], // 95% of requests should be below 500ms
        http_req_failed: ['rate<0.01'],   // Failure rate should be less than 1%
    },
};

export default function () {
    let headers = {
        'Authorization': `Bearer ${token}`, // Add the Bearer token here
        'Content-Type': 'application/json', // Adjust headers as needed
    };

    let res = http.get('https://stagingapi.dkkom.com/message/db74487d-486c-4bb2-981c-a83370a5730b?pageState=', { headers: headers });

    if (res.status !== 200) {
        console.error(`Request failed with status: ${res.status}`);
        console.error(`Response body: ${res.body}`);
    }


    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(1);
}