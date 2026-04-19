# Init Shell Guide

이 폴더는 macOS 기준 로컬 개발과 `minikube + Argo CD` 기반 GitOps 시연 흐름을 빠르게 준비하기 위한 스크립트를 모아둔 곳이다.

## 결론

이 프로젝트에서는 `compose.local.yml`을 삭제하지 않는 것이 맞다.

이유:

- `compose.local.yml`은 Keycloak, MariaDB, Backend, Frontend 기능 개발을 가장 빠르게 확인할 수 있다.
- `minikube + Argo CD`는 GitOps 시연, ingress, Helm values, 배포 자동화 구조를 보여주기에 가장 적합하다.
- 즉 `compose = 개발용`, `minikube + Argo CD = 기본 시연용`, `직접 Helm 배포 = 빠른 검증용`으로 나누는 편이 가장 실무적이다.

## 권장 흐름

기본 경로는 `Argo CD를 포함한 GitOps 흐름`이다.

### 1. CLI 설치

```bash
./initshell/install-cli.sh
```

설치 대상:

- `kubectl`
- `minikube`
- `helm`
- `argocd`

## 2. minikube 클러스터 시작

```bash
./initshell/setup-all.sh
```

이 스크립트는 아래를 수행한다.

- minikube 시작
- ingress addon 활성화
- `api.kubeops.local`, `dashboard.kubeops.local`용 hosts 안내 출력

## 3. 로컬 인프라 실행

minikube에서 앱만 검증하고, Keycloak/MariaDB는 Mac 호스트에서 compose로 띄우는 방식을 권장한다.

```bash
docker-compose -f compose.local.yml up -d mariadb keycloak-db keycloak
```

백엔드는 `host.minikube.internal`을 통해 호스트의 Keycloak/MariaDB에 접근한다.

## 4. 기본 GitOps 경로: Argo CD 등록

```bash
./initshell/register-argocd.sh
```

이 스크립트는 아래를 수행한다.

- Argo CD 설치 확인
- `argocd-server` 포트포워딩
- 내부적으로 `quick-deploy.sh`를 실행해 minikube용 이미지를 준비
- 현재 repo의 Helm chart를 Argo CD app으로 등록
- Argo CD sync 실행

## 5. 빠른 검증 경로: 직접 Helm 배포

```bash
./initshell/quick-deploy.sh
```

이 경로는 코드 수정 후 빠르게 앱만 확인할 때 사용한다.

## 스크립트 설명

### `install-cli.sh`

- macOS 기준 CLI 설치

### `setup-all.sh`

- minikube 시작과 ingress 준비

### `register-argocd.sh`

- 기본 GitOps 시연 경로

### `quick-deploy.sh`

- local image build + Helm 직접 배포
