# Keycloak Implementation Plan

## 1. Goal

현재 프로젝트의 자체 JWT 로그인 구조를 Keycloak 기반 인증으로 전환한다.

목표는 다음과 같다.

- 메인 페이지와 로그인 진입 화면은 서비스 자체 UI로 유지한다.
- 실제 인증과 권한 관리는 Keycloak에 위임한다.
- 백엔드는 Keycloak과 토큰 교환, 세션 유지, 토큰 갱신을 담당한다.
- 프론트는 토큰 원문을 직접 보관하지 않고 세션 기반으로 동작한다.

이 문서는 구현 방향, 작업 순서, 코드 컨벤션을 먼저 고정하기 위한 초안이다.

## 2. Recommended Architecture

권장 방향은 `Spring Boot BFF`가 Keycloak과 OAuth2 로그인 및 토큰 관리를 담당하고, `Vue`는 백엔드 세션만 사용하는 구조다.

구성 요약:

- 프론트:
  - 커스텀 로그인 화면 제공
  - `Sign in with Keycloak` 버튼 클릭 시 백엔드 로그인 시작 엔드포인트로 이동
  - 로그인 후에는 세션 쿠키만 이용해 API 호출
  - `/login`, `/`, `/cluster` 등 라우트 접근 제어 수행
- 백엔드:
  - 자체 로그인 발급 로직 제거
  - Keycloak authorization code 교환 수행
  - access token, refresh token을 서버 측 세션 또는 저장소에서 관리
  - 필요 시 refresh token으로 access token 갱신
  - 세션 기준으로 `username`, `email`, `roles`를 `/auth/me` 응답으로 매핑

이 방향을 추천하는 이유:

- 현재 Vue + Spring Boot 분리 구조에 가장 자연스럽다.
- 메인 페이지는 Keycloak 테마 없이 유지할 수 있다.
- refresh token이 브라우저 JavaScript에 노출되지 않는다.
- 향후 role 기반 API 보호와 세션 정책 확장이 쉽다.

## 3. Explicit Non-Goals

이번 전환에서 우선 제외할 항목:

- Keycloak 로그인 화면 자체 커스터마이징
- 자체 회원가입 화면 유지
- 브라우저에서 사용자 비밀번호를 직접 받아 Keycloak 토큰 엔드포인트에 전달하는 방식
- 복수 IdP 연동
- 프론트에서 access token 또는 refresh token을 직접 저장하는 구조

## 4. Current State Summary

현재 기준 코드 상태:

- 프론트는 [frontend/src/views/Login.vue](/Users/bhmin/Desktop/project/bhminproject/frontend/src/views/Login.vue)에서 인증 진입 UI를 제공
- Axios는 [frontend/src/axios.js](/Users/bhmin/Desktop/project/bhminproject/frontend/src/axios.js)에서 `withCredentials` 기반 세션 호출을 사용한다
- 라우터는 [frontend/src/router/index.js](/Users/bhmin/Desktop/project/bhminproject/frontend/src/router/index.js)에서 보호 라우트 가드를 점진적으로 적용 중이다
- 백엔드는 [springboot-app/src/main/java/com/example/demo/auth/controller/AuthController.java](/Users/bhmin/Desktop/project/bhminproject/springboot-app/src/main/java/com/example/demo/auth/controller/AuthController.java)와 관련 security 설정에서 세션 기반 인증 흐름을 제공한다
- 보안 설정은 [springboot-app/src/main/java/com/example/demo/config/security/SecurityConfig.java](/Users/bhmin/Desktop/project/bhminproject/springboot-app/src/main/java/com/example/demo/config/security/SecurityConfig.java) 중심으로 관리한다
- 서버 컨텍스트 경로는 [springboot-app/src/main/resources/application.yml](/Users/bhmin/Desktop/project/bhminproject/springboot-app/src/main/resources/application.yml) 기준 `/api`

즉, 남은 핵심은 `Keycloak role 매핑`, `세션 로그아웃 하드닝`, `권한 정책 연결`이다.

## 5. Target Design

### 5.1 Login UX

- `/login`은 커스텀 페이지로 유지
- 폼 입력 대신 CTA 중심 UI 사용
- 로그인 버튼 클릭 시 Keycloak 로그인 시작
- 로그인 성공 후 `/` 또는 원래 접근하려던 경로로 복귀
- 로그아웃 버튼 클릭 시 Keycloak 로그아웃 후 `/login` 또는 `/` 이동

### 5.2 Token Flow

- 인증 방식은 `Authorization Code` 기반으로 백엔드가 code 교환을 수행한다.
- 프론트는 Keycloak 토큰을 직접 보관하지 않는다.
- 백엔드는 Keycloak에서 받은 access token과 refresh token을 서버 측 세션 또는 저장소에 보관한다.
- 브라우저에는 세션 식별용 `HttpOnly` 쿠키만 내려준다.
- API 호출은 쿠키 기반으로 처리한다.
- 백엔드는 access token 만료 시 refresh token으로 재발급을 시도한다.
- refresh 실패 시 세션을 종료하고 재로그인을 유도한다.

### 5.3 Session and Cookie Policy

- 세션 식별자는 브라우저 쿠키로만 전달한다.
- 쿠키는 `HttpOnly`, 가능하면 `Secure`, 적절한 `SameSite` 정책을 사용한다.
- 프론트는 `withCredentials: true`로 API를 호출한다.
- CSRF 방어 정책은 세션 기반 구조에 맞춰 별도로 정의한다.

### 5.4 API Responsibility

- `/auth/login`, `/auth/register`, `/auth/validate`는 제거하거나 레거시 경로로 격리
- `/auth/login/keycloak` 또는 동등한 로그인 시작 엔드포인트를 제공
- `/auth/callback` 또는 Spring Security 기본 callback 경로를 사용
- `/auth/me`는 현재 로그인 사용자의 식별 정보 반환
- `/auth/logout`은 세션 종료와 Keycloak 로그아웃 흐름을 담당
- 보호 API는 모두 백엔드 세션 인증 기준으로 동작
- 권한 정책이 필요한 엔드포인트는 `ROLE_*` 또는 realm/client roles를 기준으로 분기

## 6. Implementation Order

### Phase 1. Configuration Baseline

- Keycloak realm, client, redirect URI 확정
- 백엔드 OAuth2 client 설정 키 확정
- 세션 쿠키 정책 확정
- 로컬 개발 주소 정리

완료 기준:

- `localhost:3000` 또는 `localhost:5173`에서 백엔드 로그인 시작 엔드포인트로 이동 가능한 상태
- `localhost:8081` 백엔드가 Keycloak authorization/token endpoint를 참조할 수 있는 상태

### Phase 2. Backend Security Migration

- 자체 JWT 관련 필터 및 provider 의존 제거 범위 결정
- Spring Security를 `oauth2Login + session` 기반으로 전환
- authorization code callback과 세션 저장 흐름 구성
- `/auth/me` 엔드포인트를 세션 principal 기반으로 재구성
- 필요 시 access token refresh 처리 지점 정의
- 공개 API와 보호 API 경계 재정의

완료 기준:

- 로그인 세션이 있으면 보호 API 접근 가능
- 세션이 없거나 만료되면 401 반환
- 권한 부족은 403 반환

### Phase 3. Frontend Auth Integration

- 로그인 버튼을 백엔드 로그인 시작 엔드포인트로 연결
- 로그아웃 버튼을 백엔드 로그아웃 엔드포인트로 연결
- Axios 인터셉터에서 Authorization 헤더 제거
- Axios를 쿠키 기반 호출로 전환
- 앱 초기 진입 시 로그인 상태 복원

완료 기준:

- 로그인 후 API 요청에 세션 쿠키가 자동 포함됨
- 세션이 없거나 만료되면 재로그인 유도

### Phase 4. Route Guard and UX

- 보호가 필요한 라우트에 meta 추가
- 전역 라우터 가드 적용
- 상단 바에 사용자명/권한/로그아웃 표시
- 로그인 페이지를 커스텀 진입면 스타일로 정리

완료 기준:

- 비로그인 상태에서 보호 페이지 접근 시 `/login` 또는 Keycloak 로그인으로 이동
- 로그인 후 원래 페이지로 복귀

### Phase 5. Cleanup

- 더 이상 사용하지 않는 DTO, service, filter 제거
- README 및 env 샘플 업데이트
- 예외 응답 형식 통일

완료 기준:

- 자체 JWT 관련 죽은 코드 제거
- 문서와 실제 설정 키가 일치

## 7. Coding Conventions

### 7.1 Naming

인증 관련 용어는 아래처럼 통일한다.

- `auth`: 로그인 상태와 인증 흐름
- `identity`: 사용자 프로필 정보
- `session`: 현재 세션 상태
- `token`: 서버 내부에서만 다루는 Keycloak access token 또는 refresh token

피해야 할 이름:

- `jwtToken`, `userInfo`, `loginData`처럼 범위가 불명확한 이름
- 프론트 코드에서 `accessToken`, `refreshToken`을 직접 상태명으로 노출하는 방식

권장 이름:

- `userProfile`
- `isAuthenticated`
- `authState`
- `identityService`
- `sessionUser`
- `sessionStatus`

### 7.2 Environment Variables

프론트와 백엔드 모두 Keycloak 관련 설정은 접두사를 통일한다.

프론트 예시:

- `VITE_API_BASE_URL`
- `VITE_AUTH_LOGIN_PATH`
- `VITE_AUTH_LOGOUT_PATH`

백엔드 예시:

- `SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_ISSUER_URI`
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_ID`
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET`
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_SCOPE`

로컬 커스텀 키를 따로 둘 경우 예시:

- `APP_SECURITY_ALLOWED_ORIGINS`
- `APP_SECURITY_PUBLIC_PATHS`
- `APP_SECURITY_SESSION_COOKIE_NAME`

### 7.3 Frontend Structure

권장 폴더 예시:

- `src/auth/`
- `src/auth/session.js`
- `src/auth/guards.js`
- `src/services/api.js`
- `src/stores/auth.js` 또는 동등한 상태 모듈

규칙:

- UI 컴포넌트에서 Keycloak SDK를 직접 사용하지 않는다.
- 인증 초기화, 로그인, 로그아웃, 세션 조회는 `auth` 레이어로 모은다.
- Axios 인터셉터는 `withCredentials`와 공통 에러 처리만 담당한다.
- 컴포넌트가 `localStorage` 또는 토큰 문자열을 직접 다루지 않는다.

### 7.4 Backend Structure

권장 패키지 방향:

- `config/security`
- `auth/controller`
- `auth/service`
- `auth/dto`

규칙:

- 컨트롤러는 principal을 받아 응답 DTO로 변환만 수행
- role 추출, claim 매핑, 권한 변환, 토큰 갱신은 service 또는 converter에서 수행
- SecurityConfig에서 공개/보호 경로를 명시적으로 선언
- Keycloak claim 이름을 여기저기서 직접 꺼내지 말고 한 mapper에서 통일
- refresh token은 컨트롤러나 일반 서비스 레이어에 흩뿌리지 않고 세션 관리 레이어에 집중

### 7.5 Response Convention

인증 관련 응답은 형태를 최대한 일정하게 유지한다.

성공 예시:

```json
{
  "username": "admin",
  "email": "admin@example.com",
  "roles": ["USER", "ADMIN"]
}
```

에러 예시:

```json
{
  "code": "AUTH_UNAUTHORIZED",
  "message": "Authentication is required."
}
```

규칙:

- 401: 인증 실패 또는 세션 없음/세션 만료
- 403: 인증은 되었지만 권한 부족
- 500: 인증 정책 오류와 일반 서버 오류를 섞지 않는다

### 7.6 Browser Storage Policy

브라우저는 Keycloak access token 또는 refresh token을 직접 저장하지 않는다.

다만 규칙은 명확히 둔다.

- `localStorage`에 인증 토큰을 저장하지 않는다
- 인증 상태 확인은 `/auth/me` 또는 `/auth/status` 응답 기준으로 처리한다
- 세션 식별은 쿠키에만 맡긴다
- 프론트는 인증 데이터 캐시가 필요해도 사용자 프로필 정도만 메모리 상태로 유지한다

향후 개선 후보:

- 세션 저장소 분리
- 다중 인스턴스 환경의 세션 공유
- 세션 만료 및 갱신 정책 고도화

### 7.7 Refresh Token Policy

- refresh token은 백엔드만 접근 가능해야 한다.
- refresh token은 서버 세션 또는 별도 서버 저장소에만 저장한다.
- 프론트는 refresh token 존재 여부를 알 필요가 없다.
- API 요청 실패 시 무조건 refresh를 반복하지 않고, 백엔드가 만료 정책을 통제한다.
- refresh 실패 시 세션을 폐기하고 로그인 재시작으로 전환한다.

## 8. Endpoint Migration Table

| Current | Target | Action |
| --- | --- | --- |
| `POST /api/auth/login` | 제거 | 백엔드 OAuth2 로그인 시작 엔드포인트로 대체 |
| `POST /api/auth/register` | 제거 또는 별도 관리 | Keycloak 사용자 관리 정책에 따름 |
| `POST /api/auth/validate` | 제거 | 서버 세션 인증으로 대체 |
| `GET /api/auth/me` | 유지 | 세션 principal 기반으로 재작성 |
| `GET /api/auth/login/keycloak` | 추가 | Keycloak 로그인 시작 |
| `POST /api/auth/logout` 또는 `GET /api/auth/logout` | 추가 | 세션 종료 및 로그아웃 흐름 처리 |
| `GET /api/auth/status` | 선택 | 세션 상태 빠른 확인용 |

## 9. Route Policy

초기 권장 정책:

- `/`: 공개 또는 반공개
- `/about`: 공개
- `/login`: 공개
- `/cluster`: 인증 필요

선택 정책:

- 홈 화면 일부 데이터는 공개
- 상세 클러스터 정보만 로그인 사용자에게 노출

## 10. Testing Checklist

최소 확인 시나리오:

- 비로그인 상태에서 `/login` 진입 가능
- 비로그인 상태에서 보호 페이지 접근 시 로그인 유도
- 로그인 성공 후 원래 페이지로 복귀
- 로그인 후 `/api/auth/me` 정상 응답
- 로그인 후 보호 API 호출 성공
- 세션 없이 호출 시 401
- 권한 없는 사용자 호출 시 403
- 로그아웃 후 보호 API 재호출 시 실패
- access token 만료 후에도 refresh를 통해 세션이 유지되는지 확인
- refresh 실패 시 세션 종료 후 재로그인 유도 확인

## 11. Recommended First Coding Batch

첫 배치에서는 범위를 작게 잡는다.

1. Keycloak client와 백엔드 OAuth2 client 설정 확정
2. Spring Boot `oauth2Login + session` 전환
3. `/auth/me`와 `/auth/logout` 구성
4. Vue 로그인 버튼과 로그아웃 연결
5. Axios를 쿠키 기반 호출로 교체
6. `/cluster` 라우트 가드 적용

이후 2차 배치에서 UI 정리와 죽은 코드 제거를 진행한다.

## 12. Decision Log

현재 권장 결정:

- Keycloak 테마를 메인 페이지에 직접 노출하지 않는다.
- 로그인 진입면은 커스텀 UI로 구현한다.
- 인증 프로토콜은 백엔드가 처리하는 `Authorization Code` 흐름을 사용한다.
- 백엔드는 Keycloak 토큰을 관리하는 BFF 성격으로 전환한다.
- refresh token은 서버 세션 또는 서버 저장소에서만 관리한다.
- `/auth/me`는 유지하되 구현을 세션 principal 기반으로 바꾼다.

남은 결정:

- realm role과 client role 중 어느 권한 모델을 우선 사용할지
- 홈 화면을 완전 공개로 둘지, 일부 데이터부터 인증 요구로 둘지
- 세션 저장소를 인메모리로 시작할지 외부 저장소로 둘지
- 로그아웃 시 Keycloak SSO 세션까지 항상 같이 종료할지 정책 확정
