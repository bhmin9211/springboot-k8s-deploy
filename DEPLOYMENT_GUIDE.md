# Deployment Guide

## 목표 구조
- Frontend: Vercel 또는 Netlify
- Backend: Render Web Service
- Database: 외부 MariaDB
- Kubernetes: 외부 접근 가능한 클러스터

## 1. 백엔드 배포 준비
- Render에 Spring Boot 서비스를 생성합니다.
- 환경변수는 [springboot-app/.env.render.example](/Users/bhmin/Desktop/project/bhminproject/springboot-app/.env.render.example) 를 기준으로 설정합니다.
- 실행 명령 예시:

```bash
./gradlew bootJar
```

## 2. 외부 DB 초기화
- MariaDB를 준비한 뒤 데이터베이스를 생성합니다.
- [render-init.sql](/Users/bhmin/Desktop/project/bhminproject/springboot-app/src/main/resources/sql/render-init.sql) 을 실행합니다.
- 초기 테스트 계정:
  - username: `minbh`
  - password: `test1234`

예시:

```bash
mysql -h <db-host> -u <db-user> -p <db-name> < springboot-app/src/main/resources/sql/render-init.sql
```

## 3. Kubernetes 읽기 전용 권한 준비
- 공개 서비스에서는 명령형 API 대신 조회 중심 구성을 권장합니다.
- 클러스터에 [render-k8s-readonly-rbac.yaml](/Users/bhmin/Desktop/project/bhminproject/deploy/render-k8s-readonly-rbac.yaml) 을 적용합니다.

```bash
kubectl apply -f deploy/render-k8s-readonly-rbac.yaml
```

- 이후 ServiceAccount 토큰 또는 kubeconfig를 Render에 안전하게 주입합니다.
- 현재 코드는 Fabric8 기본 설정을 사용하므로 `KUBECONFIG` 기반 연결 구성이 가장 간단합니다.

## 4. 프론트엔드 배포 준비
- 프론트엔드 환경변수는 [frontend/.env.example](/Users/bhmin/Desktop/project/bhminproject/frontend/.env.example) 를 기준으로 설정합니다.
- `VITE_API_BASE_URL` 값은 Render 백엔드 주소를 사용합니다.

예시:

```text
VITE_API_BASE_URL=https://your-render-service.onrender.com/api
```

## 5. Render 권장 설정
- Runtime: Java 17
- Build Command:

```bash
./gradlew build -x test
```

- Start Command:

```bash
java -jar build/libs/*.jar
```

## 6. 현재 프로젝트 상태
- `gradlew` 추가 완료
- `./gradlew test` 성공
- `./gradlew build -x test` 성공
- 공개 배포용 환경변수화 완료
- Kubernetes 명령형 API 비활성화 옵션 추가 완료
