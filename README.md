# 🚀 springboot-k8s-deploy

Spring Boot 애플리케이션을 **CI/CD 자동화 파이프라인**으로 구성한 DevOps 포트폴리오입니다.  
Docker, GitHub Actions, Helm, ArgoCD, Kubernetes(Minikube) 기반으로 배포 전체 흐름을 구축하였습니다.

---

## 🛠 기술 스택

| 구분 | 기술 |
|------|------|
| 백엔드 | Spring Boot 3.x |
| 빌드 | Gradle |
| 컨테이너 | Docker |
| CI | GitHub Actions |
| 이미지 저장소 | DockerHub (`byunghyukmin/springboot-app`) |
| 배포 자동화 | Helm + ArgoCD (GitOps) |
| 클러스터 | Minikube (macOS + Colima 기반) |
| 라우팅 | Ingress (`springboot.local`) |

---

## ⚙️ 전체 아키텍처

```plaintext
GitHub Push
  ↓
GitHub Actions → Docker 이미지 빌드 & DockerHub 푸시
  ↓
ArgoCD → Git 변경 감지 → 자동 동기화
  ↓
Kubernetes → Helm Chart 기반 배포
  ↓
Ingress → http://springboot.local 접속
```

---

## 📁 주요 디렉토리 구조

```bash
.
├── .github/workflows/         # GitHub Actions 워크플로우
│   └── ci.yml
├── springboot-app/            # Spring Boot + Dockerfile
├── springboot-helm-chart/     # Helm Chart (deployment, service, ingress 등)
├── README.md
```

---

## 🔄 CI/CD 흐름

1. 코드를 `main` 브랜치에 푸시
2. GitHub Actions가 Docker 이미지 빌드 및 DockerHub에 푸시
3. ArgoCD가 Git 변경을 감지하여 자동 배포
4. Kubernetes 클러스터에서 Helm Chart 기반 Pod 배포
5. readinessProbe를 통해 헬스 체크 수행
6. Ingress를 통해 로컬 도메인으로 접속

---

## ✅ 로컬 서비스 확인 (Ingress)

1. `/etc/hosts` 파일에 도메인 등록:

```
127.0.0.1 springboot.local
```

2. 브라우저 또는 curl 접속:

```bash
curl http://springboot.local/
```

> 응답 예시: `OK`

---

## 🛠 Helm 명령어

```bash
helm install springboot-app ./springboot-helm-chart
helm upgrade springboot-app ./springboot-helm-chart
helm uninstall springboot-app
```

---

## 📌 프로젝트 목적

> 이 포트폴리오는 Spring Boot 서비스를 CI/CD 파이프라인과 함께 자동 배포하고,  
> GitOps 기반의 실무 DevOps 흐름을 연습하기 위한 개인 프로젝트입니다.

---

## 🙌 기여자

- Author: [bhmin9211](https://github.com/bhmin9211)
- DockerHub: [byunghyukmin](https://hub.docker.com/u/byunghyukmin)

---

## 🔍 참고 사항

- ArgoCD는 `syncPolicy: automated` 로 설정되어 있어 Git 변경 시 자동 배포됩니다.
- readinessProbe는 기본 `/` 경로로 구성되어 있으며, `/actuator/health`로도 쉽게 전환 가능합니다.
- Ingress는 `springboot.local` 로 설정되어 있으며, 로컬 테스트 시 `/etc/hosts` 등록 필요합니다.
