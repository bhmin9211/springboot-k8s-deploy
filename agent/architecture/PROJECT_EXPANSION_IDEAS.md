# Project Expansion Ideas

## Why This Document

현재 프로젝트는 이미 다음 재료를 가지고 있다.

- Kubernetes 리소스 조회 API
- 읽기 전용 / 명령형 기능 분리 구조
- Spring Boot + Vue 대시보드
- Helm / Argo CD / GitHub Actions 기반 GitOps 스토리
- 인증 구조 전환 여지
- 헬스체크와 배포용 환경 분리

문제는 기능이 `쿠버네티스 목록 조회기`로 보일 위험이 있다는 점이다.

그래서 확장 방향은 단순히 `리소스를 더 많이 보여주기`보다 아래 중 하나로 가야 한다.

1. 운영 판단을 돕는 화면
2. 배포 변경 이력을 설명하는 화면
3. 보안 / 접근 제어 / 감사 관점을 드러내는 화면
4. 장애 대응 시나리오를 보여주는 화면

이 문서는 현재 코드베이스에서 해볼 만한 확장 아이디어를 정리한 것이다.

## Reading of Current Project

코드 기준으로 확인된 현재 강점:

- `springboot-app/src/main/java/com/example/demo/k8s/controller/KubernetesController.java`
  - pods / deployments / services / namespaces / nodes 조회 가능
  - scale, delete, namespace create/delete 같은 명령형 API도 이미 있음
  - 공개 배포용으로 명령형 기능 차단 플래그도 있음
- `frontend/src/views/Cluster.vue`
  - 리소스 탐색 UI 기본 골격이 이미 있음
- `frontend/src/views/Home.vue`
  - 상태 카드와 overview 페이지가 이미 있음
- `frontend/src/views/About.vue`
  - GitOps 포트폴리오 스토리텔링 페이지가 이미 있음

즉, 지금 필요한 건 `새로운 CRUD`가 아니라 `운영 관점의 제품성`을 붙이는 것이다.

## Best Direction

가장 추천하는 방향은 아래 셋 중 하나다.

1. `Release Intelligence`
2. `Operational Readiness`
3. `Access / Audit Center`

이 셋은 단순 K8s 조회를 넘어서, 포트폴리오가 `운영도구처럼 보이게` 만든다.

## Idea 1. Release Intelligence

### Concept

배포가 언제, 무엇이, 어떤 경로로 바뀌었는지를 한 화면에서 보여주는 영역이다.

현재 프로젝트는 Helm, Argo CD, GitHub Actions, Docker 이미지 태그 변경이라는 서사가 이미 README에 있다.  
그런데 실제 UI에서는 그 흐름이 아직 데이터처럼 보이지 않고 설명문으로만 존재한다.

### What to Build

- 최근 배포 타임라인
- 배포된 이미지 태그 비교
- 이전 버전 대비 변경점
- 어느 namespace / deployment에 반영됐는지 표시
- 배포 성공 / 실패 / 대기 상태
- Git commit SHA, 브랜치, 배포 시각 표시

### Why It Fits

- README의 핵심 메시지와 일치한다.
- 단순 K8s 리소스 조회보다 훨씬 포트폴리오 설득력이 높다.
- GitOps라는 말을 실제 화면 기능으로 바꿀 수 있다.

### Data Source Options

- 1차: 샘플 JSON
- 2차: GitHub Actions workflow run API
- 3차: Argo CD Application 상태 API 또는 Helm release 정보

### Difficulty

- 1차 목업: 낮음
- 실제 GitHub Actions 연동: 중간
- Argo CD 상태 동기화까지: 중간 이상

### Priority

매우 높음

## Idea 2. Operational Readiness Dashboard

### Concept

리소스 목록이 아니라 `지금 운영 가능한 상태인가`를 보여주는 페이지다.

### What to Build

- CrashLoopBackOff / Pending / NotReady 리소스 수
- restartCount 상위 Pod
- replicas mismatch deployment 목록
- namespace별 이상 상태 집계
- readiness / liveness 실패 추정 상태
- 최근 1시간 내 비정상 리소스 요약

### Why It Fits

- 현재 `PodInfo`, `DeploymentInfo`, `ServiceInfo`, `NodeInfo`로 이미 필요한 데이터 일부를 갖고 있다.
- 단순한 list view를 `판단형 dashboard`로 바꿔준다.
- 운영 관점이 들어오면 이 프로젝트가 훨씬 성숙해 보인다.

### Good UI Blocks

- `Top Issues`
- `Unhealthy Workloads`
- `Replica Drift`
- `Node Pressure Summary`

### Difficulty

낮음 ~ 중간

### Priority

매우 높음

## Idea 3. Access / Audit Center

### Concept

Keycloak 연동 방향과 잘 맞는 확장이다.  
이 프로젝트를 단순 클러스터 조회기가 아니라 `운영 권한이 걸린 내부 도구`처럼 보이게 만든다.

### What to Build

- 현재 로그인 사용자
- role / realm role / client role 표시
- 어떤 기능이 현재 권한으로 가능한지
- read-only / admin / operator 모드 표시
- 최근 로그인 시각
- 최근 보호 API 호출 기록
- 명령형 API 차단 상태와 이유 표시

### Why It Fits

- 지금 이미 인증 구조가 있고 Keycloak 전환을 논의 중이다.
- `왜 로그인해야 하는가`를 사용자에게 설명할 수 있다.
- 공개 포트폴리오에서도 read-only 정책을 설득력 있게 보여줄 수 있다.

### Extra Value

- 감사 로그처럼 보이는 UI만 있어도 완성도가 급상승한다.
- 실제 운영도구 감성이 난다.

### Difficulty

- 1차 UI: 낮음
- 실제 감사 로그 저장: 중간

### Priority

높음

## Idea 4. Incident Mode / Runbook Page

### Concept

문제가 생겼을 때 어디를 봐야 하는지, 어떤 순서로 대응해야 하는지를 보여주는 페이지다.

### What to Build

- 장애 유형별 runbook 카드
- `API Down`, `DB Unhealthy`, `K8s Unreachable` 같은 시나리오
- 관련 지표 / 리소스 링크
- 대응 순서 체크리스트
- 최근 incident history

### Why It Fits

- 헬스체크 API와 연계하기 쉽다.
- 단순 조회보다 훨씬 실무적인 콘텐츠다.
- SRE/운영 경험을 간접적으로 보여줄 수 있다.

### Difficulty

- 1차 목업: 낮음
- 실제 알림/이력 연동: 중간

### Priority

높음

## Idea 5. GitOps Drift / Sync View

### Concept

`Git에 선언된 상태`와 `클러스터의 실제 상태`가 얼마나 일치하는지 보여주는 화면이다.

### What to Build

- Argo CD sync status
- out-of-sync 리소스 목록
- 어떤 값이 drift 되었는지 요약
- 마지막 sync 시각
- 자동 sync / 수동 sync 여부

### Why It Fits

- 이 프로젝트가 GitOps를 전면에 내세우는 이상, 매우 잘 맞는다.
- `그냥 배포한다`가 아니라 `선언형 운영을 이해한다`는 인상을 준다.

### Difficulty

- Argo CD API 연동이 필요하면 중간 이상
- 샘플 기반 시연은 낮음

### Priority

중간 이상

## Idea 6. Safe Ops Simulator

### Concept

실제 명령형 API가 공개 환경에서 막혀 있어도, `스케일`, `재시작`, `삭제`가 어떤 영향을 주는지 시뮬레이션 형태로 보여주는 기능이다.

### What to Build

- deployment scale preview
- namespace 삭제 시 영향 받는 리소스 preview
- service 삭제 시 연결 영향도 표시
- 운영 명령이 차단된 이유와 정책 설명

### Why It Fits

- 현재 백엔드는 이미 scale/delete/create API가 있다.
- 공개 환경에서는 막고, 로컬/관리자 환경에서만 실제 실행되게 설계하기 좋다.
- 면접이나 포트폴리오 시연에 강하다.

### Difficulty

중간

### Priority

중간 이상

## Idea 7. Environment Compare

### Concept

dev / stage / prod 또는 local / public 환경을 비교하는 화면이다.

### What to Build

- 환경별 이미지 태그
- replica 수 차이
- ingress / service 차이
- 공개 환경과 로컬 환경의 권한 차이
- 어떤 기능이 disabled 되었는지 비교

### Why It Fits

- Helm values 분리와 연결하기 좋다.
- “운영 환경을 고려한 설계”를 보여주기에 좋다.

### Difficulty

- 1차는 정적 데이터로 쉬움
- 실제 환경 비교 자동화는 중간

### Priority

중간

## Idea 8. Cost / Capacity Lite

### Concept

정교한 FinOps까지는 아니고, 현재 클러스터 규모와 과다/과소 운영 징후를 보여주는 가벼운 화면이다.

### What to Build

- node 수
- pod 밀도
- namespace별 리소스 수
- 미사용 service 추정
- replicas 0 또는 orphan 리소스 추정

### Why It Fits

- 현재도 node / deployment / service / namespace 데이터가 있다.
- K8s를 `운영 비용과 효율` 시점으로 보여줄 수 있다.

### Difficulty

- 낮음 ~ 중간

### Priority

중간

## Ideas That Look Fancy But Are Lower ROI Right Now

현재 단계에서 우선순위가 낮은 것:

- 복잡한 멀티클러스터 관리
- Prometheus / Grafana 전체 통합을 바로 구현
- 서비스 메시 연동
- 복잡한 실시간 웹소켓 이벤트 스트리밍
- AI Ops 같은 큰 이름의 기능

이유:

- 현재 프로젝트 메시지와 구현 범위를 흐릴 수 있다.
- 포트폴리오에서 중요한 건 기술 수집보다 일관된 스토리다.

## Recommended Roadmap

### Track A. 가장 추천

1. `Operational Readiness Dashboard`
2. `Release Intelligence`
3. `Access / Audit Center`

이 조합이 가장 좋다.  
왜냐하면 현재 프로젝트의 축인 `운영`, `배포`, `인증`을 각각 하나씩 강화하기 때문이다.

### Track B. 좀 더 차별화

1. `Incident Mode / Runbook`
2. `GitOps Drift / Sync View`
3. `Safe Ops Simulator`

이 조합은 실무 냄새가 강하고, 단순 대시보드보다 훨씬 기억에 남는다.

## Concrete Page Ideas

현재 라우트 구조에 맞춰 붙이면:

- `/`
  - Overview + Top Issues + Release Timeline + Session Summary
- `/cluster`
  - 기존 리소스 explorer + unhealthy only 필터 + drift badge
- `/pipeline`
  - GitHub Actions / Helm / Argo CD 흐름 + 최근 배포 이력
- `/security`
  - Keycloak 사용자, 권한, 세션 상태, read-only 정책
- `/runbook`
  - 장애 대응 가이드, 헬스 상태, 진단 링크

## Best Next Move

지금 시점에서 가장 효율 좋은 다음 작업은 아래다.

1. `Release Intelligence` 페이지 추가
2. `Overview`에 `Operational Readiness` 카드 추가
3. Keycloak 전환과 함께 `Access / Audit Center` 추가

이렇게 가면 이 프로젝트는 더 이상 `Kubernetes 조회 데모`가 아니라:

- 배포 흐름을 설명하고
- 운영 상태를 판단하게 해주고
- 권한 모델까지 보여주는

조금 더 제품다운 DevOps 포트폴리오가 된다.

## Short Recommendation

단순 K8s를 넘고 싶다면, 제일 좋은 축은 이 셋이다.

- `운영 판단`
- `배포 변경 추적`
- `권한 / 감사`

이 셋은 지금 코드베이스와 가장 잘 맞고, 포트폴리오 가치도 가장 크다.
