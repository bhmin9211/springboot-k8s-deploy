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
minikube 클러스터에서 Pod 실행
```

---

## 📁 주요 디렉토리 구조

```bash
.
├── .github/workflows/         # GitHub Actions 워크플로우
│   └── ci.yml
├── springboot-app/            # Spring Boot + Dockerfile
├── springboot-helm-chart/     # Helm Chart (deployment/service)
├── README.md
```

---

## 🔄 CI/CD 흐름

1. 코드를 `main` 브랜치에 푸시
2. GitHub Actions가 Docker 이미지 빌드 및 DockerHub에 푸시
3. ArgoCD가 Git 변경을 감지하여 자동 배포
4. Kubernetes 클러스터에서 Pod 기동
5. readinessProbe `/` 경로로 체크

---

## ✅ 서비스 확인 (Minikube)

```bash
minikube service springboot-app-service --url
# 예시 출력: http://127.0.0.1:50351
```

브라우저 또는 curl 로 접근:

```bash
curl http://127.0.0.1:50351/
```

응답:
```
OK
```

---

## 🔧 Helm 사용법

```bash
helm install springboot-app ./springboot-helm-chart
helm upgrade springboot-app ./springboot-helm-chart
helm uninstall springboot-app
```

---

## 💬 프로젝트 목적

> 이 포트폴리오는 Spring Boot 서비스를 CI/CD 파이프라인과 함께 자동 배포하고,  
> GitOps 기반의 실무 DevOps 흐름을 연습하기 위한 개인 프로젝트입니다.

---

## 📌 기여

- Author: [bhmin9211](https://github.com/bhmin9211)
- DockerHub: [byunghyukmin](https://hub.docker.com/u/byunghyukmin)

---

## 🧠 참고

- ArgoCD는 `syncPolicy: automated` 로 설정되어 있어 Git 변경 시 자동 배포됩니다.
- Pod 상태는 `/` 경로 readinessProbe 를 통해 ArgoCD가 판단합니다.
