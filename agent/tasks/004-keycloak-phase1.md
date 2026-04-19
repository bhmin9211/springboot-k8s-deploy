# 004. Keycloak Phase 1

## Goal
기존 JWT 인증을 제거하고 Keycloak 기반 서버 세션 인증으로 전환한다.

## Reference Docs

- `../architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md`
- `../architecture/ROLE_MODEL_IDEAS.md`
- `../architecture/upgrade_plan.md`

## Scope
- Spring Security `oauth2Login + session`
- `/auth/me`
- `/auth/logout`
- axios `withCredentials`
- 기존 `localStorage.jwtToken` 제거
- 프론트 로그인 버튼을 백엔드 로그인 시작 엔드포인트로 연결

## Done Criteria
- 로그인 후 세션 기반 호출 가능
- 프론트가 토큰 문자열을 직접 다루지 않음
- `/auth/me`에서 현재 세션 사용자 조회 가능
- 비로그인 상태에서 보호 API 접근 시 401 처리 가능

## Notes

- 토큰과 refresh token은 백엔드가 관리한다.
- 프론트는 세션 쿠키만 사용한다.
- 공개 페이지와 보호 페이지 경계를 함께 정리해야 한다.

## Progress
- [x] backend oauth2Login/session skeleton added
- [x] frontend withCredentials/session bootstrap added
- [x] login page switched to backend Keycloak redirect
- [x] legacy JWT classes cleanup
- [x] local docker/keycloak stack added for session auth testing
- [ ] Keycloak role mapping and session logout hardening
