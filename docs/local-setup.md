# 로컬 실행 예제 값

이 문서의 계정과 비밀번호는 로컬 데모용 예제입니다. 외부에서 접근 가능한 환경에 그대로 두지 않습니다.

| 용도 | 예제 값 |
|---|---|
| MariaDB | database `k8s_dashboard`, user `root`, password `1234` |
| Keycloak 관리자 | `admin` / `admin1234` |
| Keycloak DB | `keycloak` / `keycloak` |
| OAuth client | `kubeops-web` / `kubeops-local-secret` |
| 화면 로그인 | `viewer` / `viewer1234`, `operator` / `operator1234`, `admin` / `admin1234` |

`sql/user.sql`과 `sql/render-init.sql`의 `USERS` 테이블은 현재 로그인에 사용하지 않습니다. 화면 로그인은 Keycloak 사용자입니다.

## 의존 서비스

```bash
docker compose -f compose.local.yml up -d mariadb keycloak-db keycloak
```

Keycloak은 `infra/keycloak/import/kubeops-local-realm.json`을 가져옵니다. 주소는 `http://localhost:8090`입니다.

## 백엔드

호스트에서 현재 코드를 실행할 때:

```bash
cd springboot-app
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_ID=kubeops-web
export SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_KEYCLOAK_CLIENT_SECRET=kubeops-local-secret
./gradlew bootRun --args='--spring.profiles.active=local'
```

API 주소는 `http://localhost:8081/api`입니다.

MariaDB를 Compose 없이 준비했다면 `springboot-app/src/main/resources/sql/payment_settlement.sql`을 적용합니다. 이미 만들어진 데이터베이스에는 파일 하단의 `VERSION` 컬럼 추가가 필요합니다.

## 프론트

```bash
cd frontend
npm install
npm run dev
```

개발 서버는 `http://localhost:3000`입니다.

Compose로 백엔드와 프론트 컨테이너까지 함께 올리려면 `docker compose -f compose.local.yml up -d`를 사용합니다.
