import { check } from 'k6';
import http from "k6/http";
import { URL } from 'https://jslib.k6.io/url/1.0.0/index.js';

export const options = {
    stages: [
        { duration: '10s', target: 20 },
        { duration: '50s', target: 20 },
    ],
};

export default function () {
    const url = new URL('http://localhost:8080/v1/users/all');
    url.searchParams.append('page', '-1');
    const res = http.get(url.toString());

    check(res, {
        'status is 400': (r) => r.status === 400,
    });
}