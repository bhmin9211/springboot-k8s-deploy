# 🚀 DevOps 자동 배포 포트폴리오

Spring Boot 애플리케이션을 기반으로 한 CI/CD 및 GitOps 자동 배포 파이프라인 구축 예제입니다.

---

## 📌 기술 스택

- **Backend**: Spring Boot 3.1.5 (Java 17)
- **CI**: GitHub Actions
- **Image Registry**: DockerHub (`byunghyukmin/springboot-app`)
- **CD**: ArgoCD (GitOps 방식)
- **Container Orchestration**: Kubernetes (Minikube)
- **Monitoring**: ArgoCD UI
- **Infra**: macOS + Colima + Minikube

---

## 🗺️ 전체 아키텍처

```plantuml
@startuml
actor Developer
Developer --> GitHub : Push code
component "GitHub" as GitHub
component "GitHub Actions" as CI
component "DockerHub\n(byunghyukmin)" as DockerHub
component "ArgoCD\n(GitOps)" as ArgoCD
component "Kubernetes\n(minikube)" as K8s
component "Spring Boot App\n(/health, /message)" as App
GitHub --> CI : Triggers CI
CI --> DockerHub : Build & Push Docker Image
CI --> GitHub : Status Check
GitHub --> ArgoCD : Manifest Update (yaml)
ArgoCD --> K8s : Sync Deployment
K8s --> App : Run Container
@enduml
```

> 👉 또는 [PlantUML 구조도 보기](./devops-architecture.puml)

---

## 🔄 배포 흐름

1. GitHub에 코드 푸시
2. GitHub Actions에서 Docker 이미지 빌드 & DockerHub 푸시
3. ArgoCD가 Git 변경 감지 → K8s에 배포
4. Minikube 내 앱 실행 → `/health`, `/message` 확인

---

## ⚙️ 주요 명령어

```bash
# Docker 이미지 빌드 및 푸시
docker build -t byunghyukmin/springboot-app:latest .
docker push byunghyukmin/springboot-app:latest

# Minikube 서비스 접근
minikube service springboot-service
```

---

## 🧩 트러블슈팅

- `imagepullbackoff`: 이미지 경로가 잘못됐거나 DockerHub에 존재하지 않음
- ArgoCD 로그인 실패: `kubectl port-forward svc/argocd-server -n argocd 8080:443`
- Pod 상태 확인: `kubectl get pods`, `kubectl describe pod <name>`

---

## ✨ 다음 목표 (WIP)

- [ ] Helm Chart로 템플릿화
- [ ] ArgoCD 자동 Sync 설정
- [ ] Vue 프론트엔드 통합 배포
