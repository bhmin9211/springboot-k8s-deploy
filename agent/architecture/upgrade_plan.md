# Portfolio Upgrade Plan

## Why This Document

이 문서는 현재 프로젝트를 운영형 플랫폼 포트폴리오로 더 설득력 있게 업그레이드하기 위한 기준 문서다.

대상 공고:

- Cloud Engineer
- Backend Developer

현재 프로젝트는 이미 아래 강점을 가지고 있다.

- Spring Boot + Vue 기반 서비스 구조
- Kubernetes 조회/운영 API
- GitHub Actions + Docker + Helm + Argo CD 기반 GitOps 흐름
- Keycloak 기반 세션 인증 전환
- 역할 기반 운영 콘솔로 확장 가능한 구조

즉, 완전히 새 프로젝트를 만드는 것보다 현재 프로젝트를 `운영/플랫폼/백엔드 설계 역량이 보이는 방향`으로 재정렬하는 것이 훨씬 효율적이다.

## Target Position Reading

공고 기준 핵심 키워드는 아래처럼 정리된다.

### 1. Cloud Engineer 포지션에서 강하게 보는 것

- AWS 기반 인프라 구축 / 개발 / 운영
- 모니터링 환경 운영 / 개선
- 신규 서비스 아키텍처 검증 / 운영
- CI/CD 파이프라인 구축 / 운영 / 개선
- Docker 기반 환경 구축 / 검증 / 운영
- Kubernetes 기반 생태계 구축
- 운영 자동화
- Troubleshooting 능력

즉 이 포지션은 단순히 배포를 해봤는지보다,  
`운영 가능한 시스템을 만들고 개선할 수 있는가`를 본다.

### 2. Backend Developer 포지션에서 강하게 보는 것

- Spring Boot / Java 또는 Kotlin
- Web backend 개발 및 운영 경험
- DBMS / SQL / ORM 이해
- Microservice architecture 환경 이해
- 대용량 트래픽 / 대용량 데이터 처리 관점
- 분산 환경에서의 안정성과 유연성
- 장애 원인 분석과 구조적 개선 경험

즉 이 포지션은 단순 REST API 구현보다,  
`운영되는 백엔드를 어떻게 설계하고 보호하고 확장했는가`를 본다.

## Fit Assessment

현재 프로젝트는 두 공고 모두에 연결 가능하지만, 연결 방식은 다르게 잡는 것이 좋다.

### Cloud Engineer 쪽으로 보여줄 때

핵심 메시지:

- Kubernetes 운영 대시보드
- GitOps 파이프라인
- 인증과 권한이 있는 내부 운영 도구
- 공개 환경과 내부 운영 환경의 정책 분리
- 향후 모니터링 / 이상징후 / 감사 로그로 확장 가능한 구조

### Backend Developer 쪽으로 보여줄 때

핵심 메시지:

- Spring Boot 기반 운영형 API 서버
- 세션 기반 인증 아키텍처와 역할 모델 설계
- 운영 명령 API와 조회 API 분리
- 클러스터 리소스 데이터를 다루는 백엔드 설계
- 장애 / 권한 / 감사 / 배포 이력까지 고려한 서비스 구조

## Best Portfolio Positioning

이 프로젝트의 가장 설득력 있는 포지셔닝은 아래 문장이다.

`Spring Boot 기반 운영형 플랫폼 백엔드를 만들고, 이를 Kubernetes / GitOps / 인증 / 권한 모델까지 포함한 내부 도구 형태로 확장한 프로젝트`

이 포지셔닝이 좋은 이유:

- Cloud Engineer 공고의 운영/자동화/배포 역량을 보여줄 수 있다.
- Backend Developer 공고의 Spring Boot/운영/분산 환경 감각도 같이 보여줄 수 있다.
- 단순 CRUD 포트폴리오보다 훨씬 실무형으로 보인다.

## What To Emphasize

지원 포지션 기준으로는 아래 순서대로 강조하는 것이 가장 좋다.

### Priority 1. Operational Readiness

가장 먼저 보여줘야 할 것:

- unhealthy pod 수
- restartCount 상위 워크로드
- replica mismatch
- node readiness
- namespace별 이슈 요약

왜 중요한가:

- Cloud Engineer 공고의 모니터링, 장애 대응, 운영 판단과 직접 연결된다.
- Backend 포지션에서도 운영 경험이 있는 개발자로 보이게 만든다.

### Priority 2. Release Intelligence

반드시 붙이면 좋은 것:

- 최근 배포 타임라인
- Git commit SHA
- 이미지 태그
- 어떤 deployment에 반영됐는지
- GitHub Actions -> Helm -> Argo CD -> Cluster 흐름

왜 중요한가:

- CI/CD 운영/개선 경험을 시각적으로 증명한다.
- 현재 README에 있는 GitOps 스토리를 실제 제품 기능으로 바꿔준다.

### Priority 3. Access / Audit Center

추가하면 포트폴리오 완성도가 크게 오른다.

- 현재 로그인 사용자
- role badge
- 허용된 작업 / 차단된 작업
- 최근 보호 API 호출 로그
- 권한 부족 또는 정책 차단 이벤트

왜 중요한가:

- 단순 대시보드가 아니라 내부 운영 도구로 보이게 만든다.
- Keycloak + Spring Boot 세션 구조의 이유를 설명할 수 있다.

### Priority 4. Troubleshooting Story

문서 또는 UI로 반드시 남겨야 한다.

- 어떤 운영 문제를 가정했는지
- 어떻게 진단하는지
- 어디를 먼저 확인하는지
- 어떤 대응 순서로 해결하는지

왜 중요한가:

- 두 공고 모두 장애 대응과 운영 개선 경험을 높게 본다.
- 기능보다 사고방식을 보여주는 데 유리하다.

## Recommended Build Order

운영형 포트폴리오로 다듬으려면 실제 구현 순서는 아래가 가장 좋다.

1. Overview를 Operational Readiness Dashboard로 업그레이드
2. Release Intelligence 화면 추가
3. Access / Audit Center 화면 추가
4. 역할 기반 UI/API 제어 완성
5. 장애 대응 / 운영 개선 사례를 README와 About에 반영

## Detailed Implementation Sequence

위 우선순위를 실제 구현으로 옮길 때는 아래 순서로 가는 것이 가장 안전하다.

### Phase 0. Baseline Cleanup

목적:

- 현재 동작하는 기능을 유지한 상태로 확장 작업의 기준선을 만든다.

해야 할 일:

- README의 현재 기능/진행 중 기능 구분 정리
- 라우트/화면/백엔드 API 현황 표 정리
- 샘플 데이터로 먼저 구현할 영역과 실제 API 연동 영역 구분
- 공개 환경과 로컬 환경 차이 정리

완료 기준:

- 무엇이 실제 데이터인지, 무엇이 시연용 데이터인지 문서상에서 구분된다.
- 이후 기능 추가 시 기존 메시지가 흐려지지 않는다.

### Phase 1. Operational Readiness First

목적:

- 첫 화면을 소개형 페이지에서 운영 판단형 대시보드로 바꾼다.

구현 순서:

1. Home 화면의 기존 카드 구조를 운영 지표 중심으로 재배치
2. unhealthy pod, restartCount, replica drift, node readiness 계산 로직 추가
3. API / DB / K8s health 상태를 한 영역에서 요약
4. Top Issues와 Unhealthy Workloads 섹션 추가
5. 상태 기준 색상과 라벨을 일관되게 정리

백엔드 작업:

- 기존 K8s 조회 응답에서 운영 지표를 가공하는 집계 로직 추가
- 필요 시 Overview 전용 summary endpoint 추가

프론트 작업:

- `Home.vue`를 소개형 레이아웃에서 운영형 레이아웃으로 전환
- 상태 카드, 이슈 테이블, 우선 확인 항목을 한 화면에 배치

완료 기준:

- 첫 화면에서 운영자가 `지금 문제인지 아닌지`를 5초 안에 판단할 수 있다.
- 단순 리소스 개수보다 이상 상태가 우선 보인다.

### Phase 2. Release Intelligence

목적:

- GitOps 스토리를 설명문이 아니라 데이터와 화면으로 보여준다.

구현 순서:

1. 샘플 데이터 기반 릴리즈 타임라인 UI 먼저 구현
2. 이미지 태그, commit SHA, 배포 시각, 대상 deployment 표시
3. GitHub Actions -> Helm -> Argo CD -> Cluster 흐름을 단계형으로 시각화
4. 실제 GitHub Actions 또는 Helm 값 기반 데이터 연동
5. 마지막 배포 결과와 현재 클러스터 상태 연결

백엔드 작업:

- release summary DTO 정의
- workflow run 또는 배포 메타데이터 조회 endpoint 추가

프론트 작업:

- 별도 Release 화면 또는 Home 내 Last Deployment Summary 카드 추가
- 타임라인, 상태 pill, 배포 메타 정보 UI 구성

완료 기준:

- README의 GitOps 설명을 보지 않아도 화면만으로 배포 흐름을 이해할 수 있다.
- 최근 배포가 무엇이었고 어디에 반영됐는지 바로 보인다.

### Phase 3. Access / Audit Center

목적:

- 인증과 권한 구조를 사용자에게 납득 가능하게 보여준다.

구현 순서:

1. 현재 로그인 사용자와 role badge 표시
2. 현재 계정으로 가능한 작업 / 불가능한 작업 표기
3. 공개 환경 차단 정책과 이유 표시
4. 최근 보호 API 호출 또는 감사 로그 UI 추가
5. 권한 부족 / 정책 차단 이벤트 표시

백엔드 작업:

- `/auth/me` 응답 구조를 role 중심으로 확장
- audit log용 샘플 응답 또는 실제 저장소 구조 정의
- 권한 거부 이벤트를 기록할 수 있는 기본 포맷 마련

프론트 작업:

- Topbar 또는 별도 페이지에 사용자/권한 정보 배치
- 현재 role별 액션 가능 여부를 카드 또는 표로 시각화

완료 기준:

- 이 프로젝트가 `로그인 있는 대시보드`가 아니라 `운영 권한이 걸린 내부 도구`로 보인다.
- 세션 기반 인증 구조를 왜 선택했는지 화면에서 자연스럽게 설명된다.

### Phase 4. Role Enforcement

목적:

- 역할 모델을 문서 수준이 아니라 실제 동작으로 연결한다.

구현 순서:

1. `VIEWER / OPERATOR / ADMIN` role 매핑 확정
2. 프론트에서 버튼/화면 노출 차등 적용
3. 백엔드에서 명령형 API role 검사 추가
4. 권한 부족 시 403과 일관된 메시지 반환
5. 허용/거부 결과를 audit log와 연결

백엔드 작업:

- Spring Security role mapping 정리
- 명령형 API별 권한 정책 명시
- 공개 배포 차단 플래그와 role 정책 충돌 시 우선순위 정의

프론트 작업:

- 액션 버튼 조건부 노출
- role badge와 안내 문구 추가
- 권한 없는 기능 클릭 시 안내 UX 정리

완료 기준:

- UI와 API 모두 같은 권한 정책으로 동작한다.
- 문서에 적힌 역할 모델이 실제 제품 동작과 일치한다.

### Phase 5. Troubleshooting Story and Narrative Upgrade

목적:

- 기능 구현을 넘어 운영 사고방식과 개선 경험을 드러낸다.

구현 순서:

1. 대표 장애 시나리오 2~3개 정의
2. 어떤 화면/지표/API로 진단하는지 문서화
3. About 또는 별도 Runbook 영역에 대응 흐름 반영
4. README를 문제 정의 -> 설계 -> 운영 판단 -> 배포 흐름 중심으로 개편
5. 지원서/PDF용 한 줄 소개와 프로젝트 설명 문안 정리

완료 기준:

- 프로젝트 설명이 기술 나열이 아니라 문제 해결 스토리로 전달된다.
- 면접에서 장애 대응 질문이 와도 이 프로젝트로 바로 설명할 수 있다.

## Execution Checklist by Area

빠르게 실행하려면 아래 체크리스트 순서대로 진행하면 된다.

### Frontend

1. `Home.vue` 운영형 대시보드 전환
2. Release summary / timeline UI 추가
3. Access / Audit Center UI 추가
4. role badge / 조건부 버튼 적용
5. About / README와 연결되는 설명 문구 정리

### Backend

1. overview summary용 집계 로직 추가
2. release metadata 응답 구조 추가
3. session user / role 응답 정리
4. role enforcement 추가
5. audit log 기본 구조 추가

### Documentation

1. README 상단 메시지 재작성
2. About 페이지 설명 문안 정리
3. 장애 대응 시나리오 문서 추가
4. 포트폴리오 PDF/지원서용 설명 축약본 작성

## Recommended Short-Term Sprint

가장 추천하는 1차 스프린트는 아래 범위다.

### Sprint 1

- Home 운영형 대시보드 전환
- unhealthy / restart / replica drift / node readiness 표시
- Last Deployment Summary 카드 추가
- README 첫 문단 개편

왜 이 구성이 좋은가:

- 가장 적은 수정으로 인상이 가장 크게 바뀐다.
- Cloud Engineer와 Backend Developer 양쪽에 동시에 어필된다.

### Sprint 2

- Release Intelligence 전용 화면 추가
- Access / Audit Center 초안 추가
- role badge와 권한 차등 UI 반영

### Sprint 3

- 백엔드 role enforcement 강화
- audit log 연결
- runbook / troubleshooting narrative 정리

## Concrete Screen Upgrades

### 1. Home / Overview

현재 Home은 소개형 성격이 강하다.

이 방향 기준으로는 아래 카드가 들어가야 한다.

- Unhealthy Pods
- Replica Drift
- Restart Hotspots
- Node Readiness
- API / DB / K8s Health
- Last Deployment Summary

이 화면은 `예쁜 메인`보다 `운영자가 첫 화면에서 판단 가능한 대시보드`여야 한다.

### 2. Cluster View

현재도 기본 탐색기는 좋다. 다만 아래가 추가되면 훨씬 강해진다.

- 상태별 필터
- restartCount 정렬
- ready/desired replica 비교 강조
- 최근 생성 리소스 하이라이트
- namespace risk summary

핵심은 `리스트 조회`를 `문제 탐색 도구`로 바꾸는 것이다.

### 3. About Page

현재 About은 프로젝트 소개 중심이다.

포트폴리오 설명 기준으로는 아래 구조가 더 좋다.

- 왜 이 프로젝트를 만들었는지
- 어떤 운영 문제를 해결하려 했는지
- 인증/권한을 왜 세션 기반으로 바꿨는지
- GitOps 배포 흐름을 왜 시각화하려는지
- 어떤 식으로 확장 중인지

즉 기술 나열보다 `문제 정의 -> 설계 선택 -> 운영 관점`이 보여야 한다.

## Backend Strengthening Points

백엔드 개발자 포지션까지 노리려면 아래 내용이 프로젝트 안에서 더 드러나야 한다.

### 1. API 책임 분리

- 조회 API
- 명령형 API
- 인증 API
- 세션/권한 API
- 감사 로그 API

이 분리가 README와 코드 구조에서 명확히 보여야 한다.

### 2. Failure Handling

- 401 / 403 / 5xx 응답 정책
- 외부 시스템 장애 시 fallback 또는 에러 메시지
- Kubernetes 연결 실패 시 graceful degradation

이런 부분이 있으면 `운영형 백엔드` 느낌이 강해진다.

### 3. Data Model / DTO Narrative

포트폴리오 설명에서 아래를 보여주면 좋다.

- 어떤 리소스를 어떤 DTO로 표준화했는지
- UI에 필요한 데이터를 백엔드에서 어떻게 가공했는지
- 원본 K8s 응답을 그대로 노출하지 않고 어떤 기준으로 정리했는지

### 4. Security Narrative

- 왜 프론트 토큰 저장을 피했는지
- 왜 Spring Boot BFF + session 구조를 선택했는지
- 왜 role 기반으로 운영 기능을 제한하는지

이건 Backend Developer 공고에서 아주 좋은 설명 포인트다.

## Cloud Engineer Strengthening Points

Cloud Engineer 쪽으로는 아래 메시지가 더 강하게 드러나야 한다.

### 1. Delivery Pipeline as Product Feature

README에 있는 배포 파이프라인을 설명문으로 두지 말고, 화면에서 보이게 만들어야 한다.

- latest image tag
- last workflow run
- release commit
- sync status
- deployment target

### 2. Observability Mindset

모니터링 도구 연동이 아직 없더라도 아래 관점은 보여야 한다.

- 무엇을 unhealthy로 볼 것인지
- 어떤 임계값을 문제로 판단하는지
- 어떤 순서로 운영자가 보게 할 것인지

즉 Prometheus를 붙였는지보다  
`운영 지표를 어떻게 해석하는 사람인지`가 보이는 것이 중요하다.

### 3. Automation Story

아래 중 최소 하나는 문서/코드/UI에 녹이면 좋다.

- 명령형 기능 공개 환경 차단 정책
- 환경별 설정 분리
- 자동 태그 반영
- GitOps sync 흐름
- 샘플 incident 대응 절차

## README Upgrade Direction

README는 지금도 나쁘지 않지만, 현재 방향 기준으로는 아래 방향으로 바꾸는 것이 더 좋다.

### Replace This

- DevOps 포트폴리오 대시보드

### With Something Like This

- Kubernetes 운영 상태와 GitOps 배포 흐름을 시각화하는 Spring Boot 기반 운영형 플랫폼 포트폴리오

또한 README 상단에는 아래 섹션이 빨리 보여야 한다.

1. 문제 정의
2. 내가 설계한 구조
3. 운영 관점 핵심 기능
4. 장애 대응 / 권한 제어 / 배포 흐름
5. 앞으로의 확장 계획

## Portfolio Narrative Template

실제 지원서나 포트폴리오 PDF에서는 아래 흐름으로 설명하는 것이 좋다.

### 한 줄 소개

Spring Boot와 Vue로 만든 Kubernetes 운영형 대시보드로,  
GitOps 배포 흐름과 세션 기반 인증, 역할 기반 운영 제어를 함께 설계한 프로젝트

### 내가 해결한 문제

- 단순 리소스 조회기가 아니라 운영자가 판단 가능한 화면이 필요했다.
- 공개 포트폴리오에서도 인증/권한 구조를 안전하게 설명할 필요가 있었다.
- GitHub Actions, Helm, Argo CD 흐름을 말이 아니라 화면으로 보여주고 싶었다.

### 내가 한 핵심 설계

- Spring Boot BFF + Keycloak session 구조
- 조회 API와 명령형 API의 분리
- 공개 환경과 내부 운영 환경 정책 분리
- GitOps 릴리즈 흐름의 시각화 기반 설계

### 내가 강조할 성과

- 운영 관점 대시보드로 구조 전환
- 배포 파이프라인 가시성 확보
- 권한 기반 내부 도구 구조 설계
- Kubernetes / Docker / CI/CD / Spring Boot를 하나의 제품 흐름으로 통합

## Gaps To Close Before Applying

지금 기준 아쉬운 점도 분명하다.

### Gap 1. 운영 지표가 아직 약함

단순 개수 조회를 넘어서 이상 상태 중심 정보가 더 필요하다.

### Gap 2. 릴리즈 이력 화면이 아직 없음

GitOps를 강점으로 내세우려면 실제 릴리즈 타임라인이 필요하다.

### Gap 3. 감사/권한 흐름의 사용자 가시성이 부족함

역할 모델은 좋지만, 화면에서 바로 납득되는 수준까지는 더 가야 한다.

### Gap 4. 장애 대응 경험이 문서화되어 있지 않음

문제 해결 과정을 설명하는 운영 시나리오가 있으면 훨씬 강해진다.

## Best Next Action

가장 먼저 할 일은 아래 하나다.

`Home 화면을 운영 판단형 Dashboard로 바꾸고, Release Summary 카드 하나를 추가한다.`

이 한 단계만 해도:

- Cloud Engineer 포지션 적합도가 크게 올라가고
- Backend 포지션에서도 운영형 백엔드 감각이 살아나며
- 포트폴리오 첫인상이 훨씬 좋아진다.

## Final Recommendation

현재 지원 전략 기준으로 이 프로젝트는  
`클라우드/백엔드 중간 지점에 있는 운영형 플랫폼 포트폴리오`로 밀어가는 것이 가장 좋다.

즉:

- Cloud Engineer로는 운영/배포/자동화/관측성
- Backend Developer로는 Spring Boot/세션 인증/권한/API 설계/운영 안정성

이 두 축을 동시에 설명할 수 있게 만드는 방향이 가장 설득력 있다.
