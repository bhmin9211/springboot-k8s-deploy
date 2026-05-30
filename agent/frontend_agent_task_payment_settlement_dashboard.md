# Agent 작업 지시서: Payment Settlement Operations Dashboard Frontend

## 1. 목표

기존 `Payment Settlement Operations Dashboard` 프로젝트에 Vue 기반 관리자 화면을 최소 범위로 추가한다.

목표는 프론트엔드 완성도가 아니라, 백엔드 API의 운영 흐름을 실제 화면에서 확인할 수 있게 만드는 것이다.

핵심 메시지:

> Java/Spring 기반 결제·정산 운영 백오피스 시스템에서 운영자가 요청 조회, 승인/반려, 처리/실패/재처리, 정산 생성, 대사 결과 확인을 수행할 수 있다.

---

## 2. 구현 원칙

- 토큰을 적게 사용하기 위해 불필요한 설명, 과도한 컴포넌트 분리, 복잡한 상태관리는 하지 않는다.
- 화면은 최소 4개만 만든다.
- 디자인보다 API 호출 흐름과 운영 업무 흐름을 우선한다.
- Vue 3 + Vite + Axios + Vue Router 기준으로 구현한다.
- Pinia, Vuex, 복잡한 layout, 디자인 시스템은 사용하지 않는다.
- Keycloak 로그인 화면은 이번 작업 범위에서 제외한다.
- 기존 인증 구조가 있으면 그대로 사용하고, 프론트에서는 우선 API 호출이 가능하도록 구현한다.
- 실패/로딩/빈 목록 상태만 최소 처리한다.
- 백엔드 API 스펙과 맞지 않는 부분이 있으면 프론트 코드를 백엔드 API에 맞춘다.

---

## 3. 사용 기술

```text
Vue 3
Vite
Vue Router
Axios
```

사용하지 않을 것:

```text
Pinia
Vuex
Element Plus
복잡한 컴포넌트 라이브러리
차트 라이브러리
프론트 테스트
```

---

## 4. 대상 API

서버 context path는 `/api`이다.

| Method | URL | 설명 |
|---|---|---|
| GET | `/api/payment-requests` | 결제 요청 목록 조회 |
| GET | `/api/payment-requests/{id}` | 결제 요청 상세 조회 |
| POST | `/api/payment-requests/{id}/approve` | 결제 요청 승인 |
| POST | `/api/payment-requests/{id}/reject` | 결제 요청 반려 |
| POST | `/api/payment-requests/{id}/process` | 결제 요청 처리 시작 |
| POST | `/api/payment-requests/{id}/success` | 결제 요청 성공 처리 |
| POST | `/api/payment-requests/{id}/fail` | 결제 요청 실패 처리 |
| POST | `/api/payment-requests/{id}/retry` | 실패 건 재처리 |
| GET | `/api/payment-requests/{id}/histories` | 상태 변경 이력 조회 |
| POST | `/api/settlements/generate` | 정산 데이터 생성 |
| GET | `/api/settlements` | 정산 목록 조회 |
| GET | `/api/settlements/{id}` | 정산 상세 조회 |
| POST | `/api/reconciliations` | 정산 대사 실행 |
| GET | `/api/reconciliations` | 대사 결과 조회 |
| GET | `/api/reconciliations/mismatches` | 대사 불일치 조회 |
| GET | `/api/audit-logs` | 감사로그 조회 |

---

## 5. 만들 화면

### 5.1 PaymentRequestListView

파일:

```text
frontend/src/views/PaymentRequestListView.vue
```

기능:

- 결제 요청 목록 조회
- 상태 필터
- 요청번호 또는 ID 표시
- 금액 표시
- 상태 표시
- 요청자 표시
- 생성일 표시
- 상세 화면 이동 버튼

필수 UI:

```text
상태 필터 select
조회 버튼
결제 요청 목록 table
상세 버튼
로딩 표시
빈 목록 표시
에러 메시지 표시
```

---

### 5.2 PaymentRequestDetailView

파일:

```text
frontend/src/views/PaymentRequestDetailView.vue
```

기능:

- 결제 요청 상세 조회
- 상태 변경 액션 실행
- 상태 변경 이력 조회
- 감사로그 조회

필수 액션 버튼:

```text
승인
반려
처리 시작
성공 처리
실패 처리
재처리
목록으로
```

반려 요청 body 예시:

```json
{
  "reason": "요청 금액과 증빙 금액이 일치하지 않습니다."
}
```

실패 요청 body 예시:

```json
{
  "reason": "외부 API 처리 중 일시 오류가 발생했습니다."
}
```

상세 화면 표시 항목:

```text
ID
요청번호
멱등키
금액
상태
요청자
승인자
승인일시
반려자
반려일시
반려사유
실패사유
재처리횟수
생성일시
수정일시
```

상태 이력 table:

```text
이전 상태
변경 상태
사유
변경자
변경일시
```

감사로그 table:

```text
액션
대상 타입
대상 ID
행위자
변경 전
변경 후
IP
생성일시
```

---

### 5.3 SettlementListView

파일:

```text
frontend/src/views/SettlementListView.vue
```

기능:

- 정산 목록 조회
- 정산 생성 API 실행
- 정산 상태 표시

필수 UI:

```text
정산 생성 버튼
정산 목록 table
로딩 표시
빈 목록 표시
에러 메시지 표시
```

정산 목록 컬럼:

```text
ID
결제요청 ID
총 금액
수수료
정산 금액
정산 상태
정산 예정일
생성일시
수정일시
```

정산 생성 버튼 동작:

```text
POST /api/settlements/generate 호출
성공 시 목록 재조회
실패 시 에러 메시지 표시
```

---

### 5.4 ReconciliationListView

파일:

```text
frontend/src/views/ReconciliationListView.vue
```

기능:

- 대사 결과 조회
- 불일치 결과만 조회
- 대사 실행 API 호출

필수 UI:

```text
전체 조회 버튼
불일치만 보기 버튼
대사 실행 버튼
대사 결과 table
로딩 표시
빈 목록 표시
에러 메시지 표시
```

대사 결과 컬럼:

```text
ID
정산 ID
외부 거래 ID
내부 금액
외부 금액
내부 상태
외부 상태
결과 타입
불일치 사유
생성일시
```

대사 실행 버튼 동작:

```text
POST /api/reconciliations 호출
성공 시 목록 재조회
실패 시 에러 메시지 표시
```

---

## 6. 파일 구조

아래 구조로 만든다.

```text
frontend/src
├── api
│   ├── http.js
│   ├── paymentRequestApi.js
│   ├── settlementApi.js
│   ├── reconciliationApi.js
│   └── auditLogApi.js
├── views
│   ├── PaymentRequestListView.vue
│   ├── PaymentRequestDetailView.vue
│   ├── SettlementListView.vue
│   └── ReconciliationListView.vue
├── router
│   └── index.js
├── App.vue
└── main.js
```

---

## 7. API 모듈 작성 기준

### 7.1 http.js

```js
import axios from "axios";

export const http = axios.create({
  baseURL: "/api",
  timeout: 10000,
});
```

### 7.2 paymentRequestApi.js

```js
import { http } from "./http";

export const getPaymentRequests = (params) =>
  http.get("/payment-requests", { params });

export const getPaymentRequest = (id) =>
  http.get(`/payment-requests/${id}`);

export const approvePaymentRequest = (id, body = {}) =>
  http.post(`/payment-requests/${id}/approve`, body);

export const rejectPaymentRequest = (id, body) =>
  http.post(`/payment-requests/${id}/reject`, body);

export const processPaymentRequest = (id) =>
  http.post(`/payment-requests/${id}/process`);

export const successPaymentRequest = (id) =>
  http.post(`/payment-requests/${id}/success`);

export const failPaymentRequest = (id, body) =>
  http.post(`/payment-requests/${id}/fail`, body);

export const retryPaymentRequest = (id) =>
  http.post(`/payment-requests/${id}/retry`);

export const getPaymentRequestHistories = (id) =>
  http.get(`/payment-requests/${id}/histories`);
```

### 7.3 settlementApi.js

```js
import { http } from "./http";

export const generateSettlements = () =>
  http.post("/settlements/generate");

export const getSettlements = (params) =>
  http.get("/settlements", { params });

export const getSettlement = (id) =>
  http.get(`/settlements/${id}`);
```

### 7.4 reconciliationApi.js

```js
import { http } from "./http";

export const runReconciliation = (body = {}) =>
  http.post("/reconciliations", body);

export const getReconciliations = (params) =>
  http.get("/reconciliations", { params });

export const getMismatchReconciliations = (params) =>
  http.get("/reconciliations/mismatches", { params });
```

### 7.5 auditLogApi.js

```js
import { http } from "./http";

export const getAuditLogs = (params) =>
  http.get("/audit-logs", { params });
```

---

## 8. 라우터

```js
import { createRouter, createWebHistory } from "vue-router";
import PaymentRequestListView from "../views/PaymentRequestListView.vue";
import PaymentRequestDetailView from "../views/PaymentRequestDetailView.vue";
import SettlementListView from "../views/SettlementListView.vue";
import ReconciliationListView from "../views/ReconciliationListView.vue";

const routes = [
  { path: "/", redirect: "/payment-requests" },
  { path: "/payment-requests", component: PaymentRequestListView },
  { path: "/payment-requests/:id", component: PaymentRequestDetailView, props: true },
  { path: "/settlements", component: SettlementListView },
  { path: "/reconciliations", component: ReconciliationListView },
];

export const router = createRouter({
  history: createWebHistory(),
  routes,
});
```

---

## 9. App.vue 기준

```vue
<template>
  <div class="app">
    <header class="header">
      <h1>Payment Settlement Operations Dashboard</h1>
      <nav>
        <RouterLink to="/payment-requests">Payment Requests</RouterLink>
        <RouterLink to="/settlements">Settlements</RouterLink>
        <RouterLink to="/reconciliations">Reconciliations</RouterLink>
      </nav>
    </header>

    <main class="main">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app {
  font-family: Arial, sans-serif;
  padding: 24px;
}

.header {
  margin-bottom: 24px;
}

nav {
  display: flex;
  gap: 12px;
}

.main {
  margin-top: 16px;
}
</style>
```

---

## 10. 구현 시 주의사항

- 백엔드 응답이 `data.content` 형태인지 `data` 배열인지 확인하고 맞춰 처리한다.
- API 실패 시 `error.response?.data?.message || error.message` 형태로 에러를 표시한다.
- 날짜 포맷 변환은 복잡하게 하지 말고 원문 그대로 표시한다.
- 상태별 버튼 노출은 처음에는 단순 조건문으로 처리한다.
- 권한별 버튼 노출은 실제 인증 연동 전까지는 생략하거나 주석 처리한다.
- CSS는 최소로만 작성한다.
- 공통 컴포넌트 분리는 하지 않는다.
- 먼저 동작하게 만들고, 중복 제거는 나중에 한다.

---

## 11. 상태별 버튼 노출 기준

```text
REQUESTED:
- 승인
- 반려

APPROVED:
- 처리 시작

PROCESSING:
- 성공 처리
- 실패 처리

FAILED:
- 재처리

RETRYING:
- 성공 처리
- 실패 처리

SUCCESS:
- 정산 생성은 정산 목록 화면에서 수행

SETTLED:
- 액션 없음

REJECTED:
- 액션 없음
```

---

## 12. 완료 기준

아래가 되면 완료로 본다.

- 결제 요청 목록이 조회된다.
- 결제 요청 상세로 이동할 수 있다.
- 상세 화면에서 승인/반려/처리/성공/실패/재처리가 가능하다.
- 상태 변경 후 상세 정보와 이력이 갱신된다.
- 감사로그가 상세 화면에 표시된다.
- 정산 목록이 조회된다.
- 정산 생성 버튼이 동작한다.
- 대사 결과 목록이 조회된다.
- 불일치 결과만 조회할 수 있다.
- 대사 실행 버튼이 동작한다.
- `npm run dev`로 프론트가 실행된다.
- 백엔드와 프론트가 함께 실행될 때 주요 흐름을 화면에서 확인할 수 있다.

현재 구현 상태:

- 완료: 결제 요청 목록 조회
- 완료: 결제 요청 상세 이동
- 완료: 승인/반려/처리/성공/실패/재처리 액션
- 완료: 상태 변경 이력 표시
- 완료: 감사로그 표시
- 완료: 정산 목록 조회
- 완료: 정산 생성
- 완료: 대사 결과 목록 조회
- 완료: 불일치 결과 조회
- 완료: 대사 실행
- 완료: `npm run build` 검증

---

## 13. README에 추가할 문구

```md
## Frontend

프론트엔드는 백엔드 API의 운영 흐름을 확인하기 위한 Vue 기반 관리자 화면입니다.

화려한 UI보다 운영자가 실제로 수행하는 업무 흐름을 보여주는 것을 목표로 합니다.

### 주요 화면

| 화면 | 설명 |
|---|---|
| 결제 요청 목록 | 요청 목록 조회, 상태별 필터링, 상세 이동 |
| 결제 요청 상세 | 승인, 반려, 처리 시작, 성공/실패 처리, 재처리, 상태 이력, 감사로그 확인 |
| 정산 목록 | 성공 거래 기준 정산 데이터 조회 및 생성 |
| 대사 결과 | 내부/외부 정산 결과 비교 및 불일치 건 조회 |

### 구현 기준

- Vue 3 + Vite + Axios 사용
- 백엔드 API 호출 흐름 중심으로 구현
- 상태 변경 액션은 버튼으로 실행
- 실패/로딩/빈 목록 상태만 최소 처리
- 디자인보다 운영 업무 흐름을 우선
```
