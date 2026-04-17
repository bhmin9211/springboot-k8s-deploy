# DevOps Portfolio: Spring Boot + Vue GitOps Dashboard

## 프로젝트 개요

이 프로젝트는 `Spring Boot + Vue` 기반의 DevOps 포트폴리오 대시보드입니다.  
단순한 웹 CRUD가 아니라, 인증, 배포, 운영 상태, Kubernetes 리소스 조회, GitOps 파이프라인까지 한 서비스 흐름으로 보여주는 것을 목표로 합니다.

현재 기준 핵심 방향은 다음과 같습니다.

- Keycloak 기반 세션 인증
- Kubernetes 운영 상태 조회
- GitOps 배포 흐름 시각화
- 읽기 전용 공개 포트폴리오 + 내부 운영 콘솔 구조

## 현재 상태

현재 구현된 범위:

- Spring Boot 백엔드 `/api`
- Vue 대시보드 UI
- Kubernetes 리소스 조회 API
- Keycloak 기반 세션 로그인 1차 전환
- Docker / Docker Compose 로컬 실행 환경
- Helm / Argo CD 기반 GitOps 스토리

현재 진행 중인 축:

- `VIEWER / OPERATOR / ADMIN` 역할 모델 적용
- Operational Readiness 화면 강화
- Release Intelligence / Access & Audit Center 추가

## 주요 기능

### 1. 인증

- Keycloak 기반 로그인
- Spring Boot가 OAuth2 login과 세션을 관리하는 BFF 구조
- 프론트는 토큰을 직접 저장하지 않고 `withCredentials` 기반으로 동작
- `/auth/me`, `/auth/status`, `/auth/logout` 제공

### 2. Kubernetes Dashboard

- Pods 조회
- Deployments 조회
- Services 조회
- Namespaces 조회
- Nodes 조회
- Cluster overview 조회

### 3. 운영 정책 분리

- 공개 환경에서는 명령형 기능 비활성화 가능
- Kubernetes 명령 기능 on/off 분리
- 향후 역할별 권한 차등 적용 예정

### 4. GitOps Story

- GitHub Actions
- Docker build/push
- Helm values 업데이트
- Argo CD sync
- Kubernetes 반영

## 기술 스택

- Backend: Java 17, Spring Boot 3, Spring Security, Spring Data JPA, OAuth2 Client
- Frontend: Vue 3, Vite, Vue Router, Axios, Bootstrap
- Auth: Keycloak
- Database: MariaDB
- CI/CD: GitHub Actions
- Container: Docker, Docker Compose
- Kubernetes: Fabric8 Client, Helm, Argo CD

## 인증 아키텍처

현재 인증 구조는 `프론트 토큰 보관` 방식이 아니라 `백엔드 세션 관리` 방식입니다.

- 사용자는 프론트 로그인 버튼을 클릭
- 백엔드가 Keycloak 로그인 흐름 시작
- Keycloak 인증 성공 후 백엔드 callback으로 복귀
- 백엔드가 세션을 유지
- 프론트는 세션 쿠키 기반으로 API 호출

즉:

- Keycloak이 인증 담당
- Spring Boot가 세션/보호 API 담당
- Vue는 커스텀 로그인 UI와 대시보드 담당

## 역할 모델 방향

현재 프로젝트는 아래 3단계 역할 모델을 기준으로 확장 중입니다.

- `VIEWER`: 조회 전용
- `OPERATOR`: 제한적 운영 기능
- `ADMIN`: 전체 관리 권한

이 역할 모델은 이후 다음 항목에 연결됩니다.

- 페이지 접근
- 버튼 노출 차등
- API 권한 검사
- 감사 로그

## 화면 구성

### Overview

- 서비스 소개
- 상태 카드
- 최근 Pod 요약
- 배포 흐름 요약

### Cluster

- namespace / resource type 필터
- Pod / Deployment / Service 탐색
- 선택 리소스 상세 정보

### Login

- Keycloak 로그인 진입 화면
- 세션 기반 인증 안내

### About

- 프로젝트 제작 의도
- 기술 스택
- GitOps 포트폴리오 방향 설명

## 로컬 실행

가장 간단한 실행 방식은 `compose.local.yml` 입니다.

포함 서비스:

- MariaDB
- Keycloak
- Spring Boot Backend
- Vue Frontend

실행:

```bash
docker-compose -f compose.local.yml up --build
```

기본 접속 주소:

- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8081/api`
- Keycloak: `http://localhost:8090`

Keycloak Admin 계정:

- `admin / admin1234`

샘플 로그인 계정:

- `viewer / viewer1234`
- `operator / operator1234`
- `admin / admin1234`

## 환경변수 예시

- Backend: [springboot-app/.env.render.example](/Users/bhmin/Desktop/Project/side/springboot-app/.env.render.example)
- Frontend: [frontend/.env.example](/Users/bhmin/Desktop/Project/side/frontend/.env.example)

중요 설정 예시:

- `SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_KEYCLOAK_ISSUER_URI`
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_ID`
- `SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET`
- `APP_SECURITY_FRONTEND_BASE_URL`
- `VITE_API_BASE_URL`

## 디렉토리 구조

```text
.
├── agent/                     # 아키텍처/작업 문서
├── frontend/                  # Vue 프론트엔드
├── infra/keycloak/import/     # 로컬 Keycloak realm import
├── springboot-app/            # Spring Boot 백엔드
├── springboot-helm-chart/     # Helm Chart
├── mariadb/                   # DB 관련 리소스
├── initshell/                 # 초기 배포 스크립트
├── compose.local.yml          # 로컬 통합 실행
└── README.md
```

## 로드맵

가까운 다음 작업:

1. Role Enforcement Phase 1
2. Operational Readiness Dashboard
3. Release Intelligence
4. Access / Audit Center

관련 문서:

- [agent/INDEX.md](/Users/bhmin/Desktop/Project/side/agent/INDEX.md)
- [agent/architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md](/Users/bhmin/Desktop/Project/side/agent/architecture/KEYCLOAK_IMPLEMENTATION_PLAN.md)
- [agent/architecture/ROLE_MODEL_IDEAS.md](/Users/bhmin/Desktop/Project/side/agent/architecture/ROLE_MODEL_IDEAS.md)
- [agent/architecture/PROJECT_EXPANSION_IDEAS.md](/Users/bhmin/Desktop/Project/side/agent/architecture/PROJECT_EXPANSION_IDEAS.md)

## 공개 배포 방향

목표 배포 구조:

- Frontend: Vercel 또는 Nginx 기반 정적 배포
- Backend: Render 또는 컨테이너 기반 배포
- Auth: 외부 Keycloak
- Database: 외부 MariaDB
- Kubernetes: 읽기 전용 또는 제한적 운영 권한 클러스터

공개 환경 운영 원칙:

- 조회 기능 우선 공개
- 위험한 명령형 기능은 관리자 전용 또는 비활성화
- 인증 토큰은 프론트에 직접 저장하지 않음
- 환경별 설정 분리

## 만든 사람

- GitHub: [bhmin9211](https://github.com/bhmin9211)
- DockerHub: [byunghyukmin](https://hub.docker.com/u/byunghyukmin)

> 이 프로젝트는 DevOps 개인 포트폴리오를 목표로 계속 확장 중인 운영형 대시보드입니다.
