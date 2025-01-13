import http from 'k6/http';
import { check, sleep } from 'k6';

const token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjQzMzNiZmJiLTA3MWUtNDk1YS1iZDU5LTZiNWM2N2E2MjdiMCIsImVtYWlsIjoiZGtrb20uZm9udHlzQGdtYWlsLmNvbSIsInVzZXJuYW1lIjoiVXNlcm5hbWUtNDMzM2JmYmItMDcxZS00OTVhLWJkNTktNmI1YzY3YTYyN2IwIiwic3ViIjoiZGtrb20uZm9udHlzQGdtYWlsLmNvbSIsImlhdCI6MTczNjcwNjU3MiwiZXhwIjo2NDA2MzI2MzYwMH0.Vt0SL1OlropyiMge0RNbGakr-5uE9w-AIHvPl8cTjkLFYtrvPpOgain1wqeXyXGyKd2FTt6pnz0-0ndEV_lMG3uGCM_x7wfrKHKNVkbWnnMuT9pwwafhU4Y21ByBWx9bln9g5Z0VtWhLZhFy2_Qgg4XoeLcC4j8HbatER0JYvExP9qBljD751B7yQ8zkzqeJPVv7DTlnYPruUMY97S1ebRn2kS3WM_dOc42umoNO8gvEFVBmVgXYzd5C6DrU_FyYytu-Tsjc8kGb00oNek_oNY1p_ETdaN5W-xEAYrOz9poJ8xAJhIhDkx45-r3pR0k-eAbMwsG6jiXCgGDkJa7M8Q"

export let options = {
    stages: [
        { duration: '5s', target: 10 }, // Ramp-up to 10 users
        // { duration: '3m', target: 10 }, // Stay at 10 users
        // { duration: '1m', target: 0 },  // Ramp-down
    ],
};

export default function () {
    let headers = {
        'Authorization': `Bearer ${token}`, // Add the Bearer token here
        'Content-Type': 'application/json', // Adjust headers as needed
    };

    let res = http.post('https://stagingapi.dkkom.com/message-service/message/db74487d-486c-4bb2-981c-a83370a5730b', {
        "channelId": "db74487d-486c-4bb2-981c-a83370a5730b",
        "content": "new message witrh some data on it sdfsdfsdfsdf"
    }
    ,  { headers: headers });
    check(res, { 'status is 200': (r) => r.status === 200 });
    sleep(1);
}