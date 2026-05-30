# Payment Settlement Operations Dashboard 업그레이드 계획

## 1. 현재 포지셔닝

현재 프로젝트는 기존 `Spring Boot + Vue + Keycloak + Kubernetes/GitOps 운영 대시보드` 구조를 기반으로, 지원 공통 타겟에 맞춰 **결제·정산 운영 백오피스 시스템**으로 확장한다.

핵심 메시지는 다음과 같다.

> Java/Spring 기반으로 결제·정산성 업무 데이터를 안전하게 처리하고, 운영자가 승인·대사·재처리·이력 관리를 수행할 수 있는 백오피스 시스템을 설계·구현할 수 있다.

이 프로젝트는 단순 CRUD가 아니라 실제 업무시스템에서 자주 발생하는 문제를 다룬다.

- 중복 요청 방지
- 상태 전이 관리
- 운영자 승인/반려
- 상태 변경 이력
- 감사로그
- 정산 데이터 생성
- 내부/외부 정산 대사
- 실패 건 재처리
- Role 기반 권한 관리
- 운영 환경 배포 구성

---


## 2. 현재 구현 완료

### Backend

- `payment` 도메인 패키지 추가
- 결제 요청 등록 API 추가
- `idempotencyKey` 기반 중복 요청 방지
- DB Unique Key 기준 중복 저장 방지 SQL 추가
- 요청 상태 enum 추가
- 상태 전이 정책 클래스 추가
- 승인 API 추가
- 반려 API 추가
- 처리 시작/성공/실패 API 추가
- 실패 건 재처리 API 추가
- 상태 변경 이력 저장
- 상태 변경 이력 조회 API 추가
- `audit` 도메인 패키지 추가
- 감사로그 저장
- 감사로그 조회 API 추가
- `settlement` 도메인 패키지 추가
- 정산 생성/조회 API 추가
- `reconciliation` 도메인 패키지 추가
- 정산 대사/불일치 조회 API 추가
- README 포지셔닝 변경

### Files

- `springboot-app/src/main/java/com/example/demo/payment/**`
- `springboot-app/src/main/java/com/example/demo/audit/**`
- `springboot-app/src/main/java/com/example/demo/settlement/**`
- `springboot-app/src/main/java/com/example/demo/reconciliation/**`
- `springboot-app/src/main/resources/sql/payment_settlement.sql`
- `README.md`

### Verification

```bash
cd springboot-app
./gradlew compileJava
```

결과: `BUILD SUCCESSFUL`

---

## 4. 현재 API

서버 context path는 `/api`이다.

| Method | URL | 설명 | 권한 |
|---|---|---|---|
| POST | `/api/payment-requests` | 결제 요청 등록 | 공개/외부 시스템 연동 |
| GET | `/api/payment-requests` | 결제 요청 목록 조회 | VIEWER 이상 |
| GET | `/api/payment-requests/{id}` | 결제 요청 상세 조회 | VIEWER 이상 |
| POST | `/api/payment-requests/{id}/approve` | 결제 요청 승인 | OPERATOR 이상 |
| POST | `/api/payment-requests/{id}/reject` | 결제 요청 반려 | OPERATOR 이상 |
| POST | `/api/payment-requests/{id}/process` | 결제 요청 처리 시작 | OPERATOR 이상 |
| POST | `/api/payment-requests/{id}/success` | 결제 요청 성공 처리 | OPERATOR 이상 |
| POST | `/api/payment-requests/{id}/fail` | 결제 요청 실패 처리 | OPERATOR 이상 |
| POST | `/api/payment-requests/{id}/retry` | 실패 건 재처리 | OPERATOR 이상 |
| GET | `/api/payment-requests/{id}/histories` | 상태 변경 이력 조회 | VIEWER 이상 |
| GET | `/api/audit-logs` | 감사로그 조회 | VIEWER 이상 |
| GET | `/api/audit-logs?targetType=PAYMENT_REQUEST&targetId=1` | 대상별 감사로그 조회 | VIEWER 이상 |
| POST | `/api/settlements/generate` | 정산 데이터 생성 | OPERATOR 이상 |
| GET | `/api/settlements` | 정산 목록 조회 | VIEWER 이상 |
| GET | `/api/settlements/{id}` | 정산 상세 조회 | VIEWER 이상 |
| POST | `/api/reconciliations` | 정산 대사 실행 | OPERATOR 이상 |
| GET | `/api/reconciliations` | 대사 결과 조회 | VIEWER 이상 |
| GET | `/api/reconciliations/mismatches` | 대사 불일치 조회 | VIEWER 이상 |

---

## 5. 현재 데이터 모델

### PAYMENT_REQUESTS

- `ID`
- `IDEMPOTENCY_KEY`
- `REQUEST_NO`
- `AMOUNT`
- `REQUESTED_BY`
- `APPROVED_BY`
- `APPROVED_AT`
- `REJECTED_BY`
- `REJECTED_AT`
- `REJECT_REASON`
- `FAILURE_REASON`
- `RETRY_COUNT`
- `STATUS`
- `CREATED_AT`
- `UPDATED_AT`

### PAYMENT_STATUS_HISTORIES

- `ID`
- `PAYMENT_REQUEST_ID`
- `FROM_STATUS`
- `TO_STATUS`
- `REASON`
- `CHANGED_BY`
- `CREATED_AT`

### AUDIT_LOGS

- `ID`
- `ACTOR_ID`
- `ACTION_TYPE`
- `TARGET_TYPE`
- `TARGET_ID`
- `BEFORE_VALUE`
- `AFTER_VALUE`
- `IP_ADDRESS`
- `CREATED_AT`

### SETTLEMENTS

- `ID`
- `PAYMENT_REQUEST_ID`
- `GROSS_AMOUNT`
- `FEE_AMOUNT`
- `SETTLEMENT_AMOUNT`
- `SETTLEMENT_STATUS`
- `SETTLEMENT_DUE_DATE`
- `CREATED_AT`
- `UPDATED_AT`

### RECONCILIATION_RESULTS

- `ID`
- `SETTLEMENT_ID`
- `EXTERNAL_TRANSACTION_ID`
- `INTERNAL_AMOUNT`
- `EXTERNAL_AMOUNT`
- `INTERNAL_STATUS`
- `EXTERNAL_STATUS`
- `RESULT_TYPE`
- `MISMATCH_REASON`
- `CREATED_AT`

---

## 6. 상태 전이 기준

```text
REQUESTED -> APPROVED
REQUESTED -> REJECTED
APPROVED -> PROCESSING
PROCESSING -> SUCCESS
PROCESSING -> FAILED
FAILED -> RETRYING
RETRYING -> SUCCESS
RETRYING -> FAILED
SUCCESS -> SETTLED
```

현재 구현된 액션은 다음과 같다.

- `REQUESTED -> APPROVED`
- `REQUESTED -> REJECTED`
- `APPROVED -> PROCESSING`
- `PROCESSING -> SUCCESS`
- `PROCESSING -> FAILED`
- `FAILED -> RETRYING`
- `RETRYING -> SUCCESS`
- `RETRYING -> FAILED`
- `SUCCESS -> SETTLED`

---

## 7. 구현 완료된 Phase

## Phase 1. 정산 데이터 생성

목표: 승인 이후 성공 처리된 거래를 기준으로 정산 대상 데이터를 생성한다.

### 구현 항목

- 완료: `settlement` 도메인 패키지 추가
- 완료: `Settlement` 엔티티 추가
- 완료: `SettlementStatus` enum 추가
- 완료: `SettlementRepository` 추가
- 완료: `SettlementService` 추가
- 완료: `SettlementController` 추가
- 완료: `POST /api/settlements/generate`
- 완료: `GET /api/settlements`
- 완료: `GET /api/settlements/{id}`

### 정책

- `SUCCESS` 상태의 결제 요청만 정산 생성 가능
- 동일 결제 요청에 대해 정산 데이터 중복 생성 방지
- 수수료와 정산금액 계산 로직 명시
- 정산 생성 시 감사로그 기록

### 어필 포인트

> 성공 처리된 거래만 정산 대상으로 생성하고, 중복 정산 생성을 막아 정산 데이터의 정합성을 확보했습니다.

---

## Phase 2. 처리/실패/재처리 흐름

목표: 외부 API 장애나 일시 오류 상황을 가정해 실패 건을 안전하게 재처리할 수 있게 한다.

### 구현 항목

- 완료: `POST /api/payment-requests/{id}/process`
- 완료: `POST /api/payment-requests/{id}/success`
- 완료: `POST /api/payment-requests/{id}/fail`
- 완료: `POST /api/payment-requests/{id}/retry`
- 완료: 실패 원인 저장
- 완료: 재처리 횟수 증가
- 완료: 재처리 가능 횟수 제한
- 완료: 상태 이력 저장
- 완료: 감사로그 저장

### 정책

- `APPROVED -> PROCESSING`
- `PROCESSING -> SUCCESS`
- `PROCESSING -> FAILED`
- `FAILED -> RETRYING`
- `RETRYING -> SUCCESS`
- `RETRYING -> FAILED`

### 어필 포인트

> 실패를 단순 예외로 끝내지 않고, 실패 원인과 재처리 횟수를 관리해 운영자가 안전하게 재처리할 수 있도록 설계했습니다.

---

## Phase 3. 정산 대사

목표: 내부 정산 데이터와 외부 정산 결과를 비교하고 불일치 건을 운영자가 확인할 수 있게 한다.

### 구현 항목

- 완료: `reconciliation` 도메인 패키지 추가
- 완료: `ReconciliationResult` 엔티티 추가
- 완료: `ReconciliationResultType` enum 추가
- 완료: `POST /api/reconciliations`
- 완료: `GET /api/reconciliations`
- 완료: `GET /api/reconciliations/mismatches`
- 완료: 외부 정산 결과 입력 DTO 추가
- 완료: 내부/외부 금액 비교
- 완료: 내부/외부 상태 비교
- 완료: 내부 누락/외부 누락 케이스 저장

### 대사 결과

- `MATCHED`
- `AMOUNT_MISMATCH`
- `STATUS_MISMATCH`
- `EXTERNAL_MISSING`
- `INTERNAL_MISSING`

### 어필 포인트

> 단순 정산 생성에 그치지 않고 외부 결과와 내부 데이터를 비교해 불일치를 탐지하고, 운영자가 조치할 수 있는 구조를 만들었습니다.

---

## Phase 4. 프론트엔드 관리자 화면

목표: 백엔드 핵심 API가 실제 운영 화면에서 어떻게 쓰이는지 보여준다.

### 사용 기술

- Vue 3
- Vite
- Axios
- Vue Router

### 주요 화면

| 화면 | 설명 |
|---|---|
| 요청 목록 | 결제 요청 목록 조회, 상태별 필터링 |
| 요청 상세 | 승인/반려, 상태 이력, 감사로그 확인 |
| 정산 목록 | 정산 대상 및 정산 상태 조회 |
| 대사 결과 | 내부/외부 불일치 결과 조회 |
| 감사로그 | 운영자 행위 이력 조회 |

### 구현 기준

- 완료: 화려한 화면보다 운영 업무 흐름 중심
- 완료: 결제 요청 목록/상세 화면 구현
- 완료: 승인/반려/처리/성공/실패/재처리 액션 버튼 구현
- 완료: 상태 이력과 감사로그 표시
- 완료: 정산 목록 및 정산 생성 화면 구현
- 완료: 대사 결과 및 불일치 조회 화면 구현
- 완료: 실패, 빈 목록, 로딩 상태 표시
- 완료: `npm run build` 검증

---

## 8. 다음 구현 우선순위

## Phase 5. 문서와 테스트

목표: 포트폴리오 검토자가 코드를 실행하지 않아도 설계 의도를 이해할 수 있게 한다.

### 구현 항목

- README API 명세 보강
- ERD 최신화
- 상태 전이 다이어그램 추가
- 정산 생성 시퀀스 다이어그램 추가
- 대사 처리 시퀀스 다이어그램 추가
- 서비스 단위 테스트 추가
- Controller slice 테스트 추가

### 테스트 우선순위

- 동일 `idempotencyKey` 재요청 시 기존 요청 반환
- 허용되지 않은 상태 전이 실패
- 승인 시 상태 이력 저장
- 반려 시 감사로그 저장
- 정산 중복 생성 방지
- 대사 불일치 결과 저장

---

## 9. 구현하지 않을 것

이번 포트폴리오에서는 아래 방향을 피한다.

- 단순 게시판 CRUD
- Todo API
- 쇼핑몰 장바구니 중심 프로젝트
- AI Agent 서비스
- 화면 구현에 과도한 시간 투자
- Kafka/MSA를 억지로 크게 구현
- Kubernetes 배포 데모만 강조
- 기술만 많고 업무 흐름이 없는 프로젝트

---
