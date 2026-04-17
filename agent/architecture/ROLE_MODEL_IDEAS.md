# Role Model Ideas

## Goal

이 프로젝트에서 `관리자 / 사용자`를 단순 로그인 구분이 아니라,  
`무엇을 볼 수 있고 무엇을 실행할 수 있는가`로 나누는 역할 모델을 정의한다.

현재 코드베이스에는 이미 다음 기능이 있다.

- 조회 기능:
  - pods
  - deployments
  - services
  - namespaces
  - nodes
  - cluster info
- 명령형 기능:
  - pod delete
  - deployment scale
  - service delete
  - namespace create/delete
- 공개 환경 차단 플래그:
  - `commands-enabled`

즉, 역할 모델을 붙이기 좋은 상태다.

## Recommended Roles

추천 역할은 2단계보다 3단계가 낫다.

- `VIEWER`
- `OPERATOR`
- `ADMIN`

이유:

- `USER / ADMIN` 2단계만 두면 중간 권한이 없어 설계가 거칠어진다.
- 실제 운영 도구는 보통 조회 전용, 제한적 운영, 전체 관리자 권한이 분리된다.
- 현재 프로젝트의 명령형 API 수준과도 잘 맞는다.

## Role Definitions

### 1. VIEWER

읽기 전용 사용자.

가능한 것:

- Overview 페이지 조회
- Cluster Explorer 조회
- Pipeline / Release 화면 조회
- Health 상태 조회
- 현재 로그인 사용자 정보 조회

불가능한 것:

- pod 삭제
- deployment scale
- service 삭제
- namespace 생성/삭제
- 보안 정책 변경
- 사용자 권한 관리

포지션:

- 공개 포트폴리오 방문자
- 일반 사용자
- 읽기 전용 데모 계정

### 2. OPERATOR

제한된 운영 작업을 수행할 수 있는 사용자.

가능한 것:

- VIEWER 권한 전체
- deployment scale
- pod 재시작 또는 pod delete
- namespace 내부 리소스 수준의 제한적 조작
- 장애 대응용 운영 기능 접근

불가능한 것:

- namespace 삭제
- namespace 생성
- 사용자 권한 변경
- 시스템 전역 정책 변경

포지션:

- 온콜 운영자
- 애플리케이션 운영 담당자
- 제한적 SRE 역할

### 3. ADMIN

전체 관리자 권한을 가진 사용자.

가능한 것:

- OPERATOR 권한 전체
- namespace 생성/삭제
- service 삭제
- 운영 정책 변경
- 사용자 / 역할 관리
- 감사 로그 전체 열람

포지션:

- 시스템 관리자
- 플랫폼 관리자
- 보안/운영 총괄 계정

## Permission Matrix

| Capability | VIEWER | OPERATOR | ADMIN |
| --- | --- | --- | --- |
| Overview 조회 | O | O | O |
| Cluster 리소스 조회 | O | O | O |
| Health 조회 | O | O | O |
| Release / Pipeline 조회 | O | O | O |
| Session / Identity 조회 | O | O | O |
| Pod 삭제 | X | O | O |
| Deployment Scale | X | O | O |
| Service 삭제 | X | 제한적 또는 X | O |
| Namespace 생성 | X | X | O |
| Namespace 삭제 | X | X | O |
| 명령 기능 on/off 정책 보기 | O | O | O |
| 명령 기능 정책 변경 | X | X | O |
| 감사 로그 조회 | 제한적 | 제한적 | O |
| 사용자 역할 관리 | X | X | O |

## Best Mapping for Current Project

현재 프로젝트에 가장 잘 맞는 초기 적용안:

### Public Demo

- 비로그인 또는 공개 방문자
- 사실상 `VIEWER`
- 모든 명령형 기능은 숨김 또는 비활성화

### Authenticated User

- 기본 로그인 사용자
- 기본적으로 `VIEWER`
- 단순 조회와 내 세션 정보만 제공

### Internal Operator

- 별도 Keycloak role을 가진 계정
- `OPERATOR`
- scale, restart, 일부 조작 가능

### Platform Admin

- 최고 권한
- `ADMIN`
- namespace, 정책, 감사 로그까지 가능

## Good Features to Build Around Roles

역할이 들어가면 다음 기능들이 자연스럽게 생긴다.

### 1. Role Badge

상단 바 또는 세션 카드에 다음 표시:

- `Viewer`
- `Operator`
- `Admin`

추가 표시:

- `Read-only environment`
- `Commands disabled in public deployment`

### 2. Conditional Action Buttons

예시:

- VIEWER:
  - `Scale`, `Delete`, `Create Namespace` 버튼 숨김
- OPERATOR:
  - `Scale` 버튼 표시
  - `Delete Pod` 버튼 표시
  - `Delete Namespace` 버튼 숨김
- ADMIN:
  - 모든 버튼 표시

### 3. Role Policy Page

별도 페이지로 보여줄 만한 콘텐츠:

- 역할별 가능 작업 표
- 현재 내 역할
- 현재 환경에서 막힌 기능
- 왜 막혀 있는지 설명

### 4. Audit Log

역할 모델과 가장 잘 맞는 기능 중 하나다.

기록 예시:

- 누가 로그인했는지
- 누가 scale 요청했는지
- 누가 삭제 요청했는지
- 권한 부족으로 거부된 요청
- 공개 환경 정책 때문에 차단된 요청

이 기능은 포트폴리오 완성도를 크게 올린다.

### 5. Namespaced Access

한 단계 더 가면 역할뿐 아니라 범위까지 나눌 수 있다.

예시:

- 특정 사용자는 `dev` namespace만 조회/운영 가능
- 특정 운영자는 `staging`만 조작 가능
- admin만 전체 namespace 접근 가능

이건 단순 role보다 더 실무적인 설계다.

## Backend Enforcement Rules

중요한 원칙:

- 프론트에서 버튼을 숨기는 것만으로는 충분하지 않다.
- 백엔드가 최종 권한 검사를 반드시 해야 한다.

필수 규칙:

1. 프론트:
   - 역할에 따라 UI 노출을 조정
2. 백엔드:
   - 실제 API 호출 시 role 검사
3. 감사 로그:
   - 허용 / 거부 결과를 기록

즉:

- `UI authorization`
- `API authorization`

둘 다 필요하다.

## Keycloak Mapping Idea

Keycloak 전환 기준으로는 다음처럼 매핑하면 된다.

- `viewer`
- `operator`
- `admin`

백엔드에서는 이를:

- `ROLE_VIEWER`
- `ROLE_OPERATOR`
- `ROLE_ADMIN`

형태로 매핑해 사용할 수 있다.

추천:

- 초기에는 realm role로 단순하게 시작
- 나중에 필요하면 client role 또는 group 기반으로 확장

## Suggested Route Access

현재 프로젝트 라우트 기준 예시:

| Route | VIEWER | OPERATOR | ADMIN |
| --- | --- | --- | --- |
| `/` | O | O | O |
| `/about` | O | O | O |
| `/login` | O | O | O |
| `/cluster` | O | O | O |
| `/pipeline` | O | O | O |
| `/security` | O | O | O |
| `/audit` | X 또는 제한 | O 또는 제한 | O |
| `/admin` | X | X | O |
| `/runbook` | O | O | O |

## Suggested API Access

현재 백엔드 API 기준 예시:

| API | VIEWER | OPERATOR | ADMIN |
| --- | --- | --- | --- |
| `GET /api/k8s/pods` | O | O | O |
| `GET /api/k8s/deployments` | O | O | O |
| `GET /api/k8s/services` | O | O | O |
| `GET /api/k8s/namespaces` | O | O | O |
| `GET /api/k8s/nodes` | O | O | O |
| `GET /api/k8s/cluster/info` | O | O | O |
| `DELETE /api/k8s/namespaces/{namespace}/pods/{name}` | X | O | O |
| `PUT /api/k8s/namespaces/{namespace}/deployments/{name}/scale` | X | O | O |
| `DELETE /api/k8s/namespaces/{namespace}/services/{name}` | X | 제한적 또는 X | O |
| `POST /api/k8s/namespaces` | X | X | O |
| `DELETE /api/k8s/namespaces/{name}` | X | X | O |

## Portfolio Value

이 역할 모델이 들어가면 프로젝트가 단순 조회 앱에서 다음처럼 보이게 된다.

- 공개 시연용 읽기 전용 운영 대시보드
- 내부 운영자용 제한적 제어 콘솔
- 관리자용 보안 / 정책 / 감사 중심 도구

즉, 같은 프로젝트 안에서 `public demo`, `operator console`, `admin console` 세 층을 설명할 수 있다.

이건 포트폴리오 관점에서 꽤 강하다.

## Recommended First Batch

역할 모델을 붙일 때 첫 배치는 작게 가는 게 맞다.

1. `VIEWER`, `OPERATOR`, `ADMIN` 역할 정의
2. 상단 바에 현재 역할 배지 표시
3. 명령형 버튼 노출 차등
4. 백엔드 API role 검사 추가
5. `Role Policy` 또는 `Security` 페이지 추가

그 다음 배치:

1. 감사 로그
2. namespace 범위 제한
3. 관리자 전용 페이지

## Final Recommendation

이 프로젝트에서 `관리자 / 사용자`를 만들 거면, 단순 2단계보다 아래 구성이 가장 좋다.

- `VIEWER`: 조회만 가능
- `OPERATOR`: 제한된 운영 가능
- `ADMIN`: 전체 관리 가능

그리고 이 역할은 단순 메뉴 가리기가 아니라:

- 페이지 접근
- 버튼 노출
- API 권한 검사
- 감사 로그

까지 연결되어야 의미가 있다.
