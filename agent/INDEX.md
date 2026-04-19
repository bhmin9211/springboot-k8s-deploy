# Docs Index

## Purpose
이 폴더는 프로젝트 방향, 인증/권한 설계, 그리고 Codex 실행용 작업 문서를 관리한다.

## Structure
- architecture: 구조/정책/마이그레이션 기준 문서
- tasks: 실제 구현 작업 단위

현재 폴더:

- `agent/architecture`
- `agent/tasks`

## Current Direction

지금까지 정리된 핵심 방향은 아래와 같다.

1. 인증은 `Keycloak + Spring Boot BFF` 기준으로 간다.
2. 토큰과 refresh token은 프론트가 아닌 백엔드가 관리한다.
3. 프론트는 `withCredentials` 기반 세션 호출로 전환한다.
4. 역할 모델은 `VIEWER / OPERATOR / ADMIN` 3단계로 간다.
5. 프로젝트 확장은 단순 K8s 목록 증가보다 아래 축을 우선한다.
   - Operational Readiness
   - Release Intelligence
   - Access / Audit Center
6. 현재 포트폴리오 방향은 `운영형 플랫폼 포트폴리오` 메시지를 가장 강하게 가져간다.
7. 로컬 개발은 `compose.local.yml` 기준으로 MariaDB + Keycloak + Backend + Frontend 스택을 사용할 수 있게 정리했다.

## Start Here

가장 먼저 보면 좋은 문서는 아래 3개다.

1. `architecture/upgrade_plan.md`
2. `architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md`
3. `architecture/ROLE_MODEL_IDEAS.md`

이 셋만 읽어도 현재 프로젝트의 포트폴리오 방향, 인증 구조, 권한 모델을 이해할 수 있다.

## Architecture Docs

### 1. Portfolio Upgrade

- `architecture/upgrade_plan.md`
- 내용:
  - 클라우드 / 백엔드 포지션 기준 포트폴리오 정렬
  - 현재 프로젝트의 적합도 분석
  - 화면/README/기술 스토리 업그레이드 방향
  - 구현 우선순위와 단계별 실행 순서

### 2. Keycloak / Session Architecture

- `architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md`
- 내용:
  - JWT 제거 방향
  - Keycloak BFF 구조
  - 세션 / 쿠키 정책
  - 구현 순서
  - 코딩 컨벤션

### 3. Project Expansion Direction

- `architecture/PROJECT_EXPANSION_IDEAS.md`
- 내용:
  - 현재 프로젝트를 단순 K8s 조회기 이상으로 확장하는 방향
  - 추천 축:
    - Operational Readiness
    - Release Intelligence
    - Access / Audit Center
    - Incident / Runbook

### 4. Role Model

- `architecture/ROLE_MODEL_IDEAS.md`
- 내용:
  - `VIEWER / OPERATOR / ADMIN`
  - 역할별 가능 작업
  - 페이지 접근 / API 접근 차등
  - Keycloak role 매핑 방향

## Task Docs

### 001. Operational Readiness

- 파일: `tasks/001-operational-readiness.md`
- 상태: planned
- 목적:
  - Overview를 운영 판단형 화면으로 전환
  - unhealthy pod, restartCount, replica drift, node readiness 추가

### 002. Release Intelligence

- 파일: `tasks/002-release-intelligence.md`
- 상태: planned
- 목적:
  - 배포 이력, 이미지 태그, commit 정보, GitOps 흐름 가시화

### 003. Access / Audit Center

- 파일: `tasks/003-access-audit-center.md`
- 상태: planned
- 목적:
  - 현재 사용자, role, session, audit log를 보여주는 보안/권한 화면 구성

### 004. Keycloak Phase 1

- 파일: `tasks/004-keycloak-phase1.md`
- 상태: in progress
- 목적:
  - 기존 JWT 제거
  - Spring Boot `oauth2Login + session`
  - `/auth/me`
  - axios `withCredentials`

### 005. Role Enforcement Phase 1

- 파일: `tasks/005-role-enforcement-phase1.md`
- 상태: planned
- 목적:
  - `VIEWER / OPERATOR / ADMIN` UI/API 반영
  - role badge
  - 버튼 노출 차등
  - 백엔드 role 검사

## Recommended Reading Order

구조를 이해하려면 아래 순서가 가장 좋다.

1. `architecture/upgrade_plan.md`
2. `architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md`
3. `architecture/ROLE_MODEL_IDEAS.md`
4. `architecture/PROJECT_EXPANSION_IDEAS.md`
5. `tasks/004-keycloak-phase1.md`
6. `tasks/005-role-enforcement-phase1.md`
7. `tasks/001-operational-readiness.md`
8. `tasks/002-release-intelligence.md`
9. `tasks/003-access-audit-center.md`

## Recommended Working Order

실제 구현 순서는 아래가 맞다.

1. `tasks/004-keycloak-phase1.md`
2. `tasks/005-role-enforcement-phase1.md`
3. `tasks/001-operational-readiness.md`
4. `tasks/002-release-intelligence.md`
5. `tasks/003-access-audit-center.md`
6. README / About narrative cleanup

## Current Priority

현재 기준 다음 실행 우선순위는:

1. Keycloak role 매핑과 세션 로그아웃 하드닝 마무리
2. 역할 모델 1차 적용
3. Overview를 운영 판단형 화면으로 고도화
4. Release summary를 첫 화면에 연결

## Status Snapshot

- `004-keycloak-phase1`: in progress
- `005-role-enforcement-phase1`: planned
- `001-operational-readiness`: planned
- `002-release-intelligence`: planned
- `003-access-audit-center`: planned
