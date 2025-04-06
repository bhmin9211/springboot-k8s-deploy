# 🚀 DevOps 자동 배포 포트폴리오

Spring Boot 애플리케이션을 GitHub Actions, DockerHub, ArgoCD, Kubernetes를 이용하여 자동 배포하는 DevOps 파이프라인 구축 예제입니다.

---

## 🛠 기술 스택

| 구분         | 기술                                                                 |
|--------------|----------------------------------------------------------------------|
| Backend      | Spring Boot 3.1.5 (Java 17)                                           |
| CI           | GitHub Actions                                                       |
| Image Registry | DockerHub (byunghyukmin/springboot-app)                            |
| CD           | ArgoCD (GitOps 방식)                                                  |
| Container    | Kubernetes (Minikube)                                                 |
| Infra        | macOS + Colima + Minikube                                             |

---

## ⚙️ 아키텍처 흐름

```plaintext
개발자 → GitHub Push
           ↓
GitHub Actions (CI) → DockerHub 이미지 Push
           ↓
ArgoCD → Git Repository 감지 → YAML 배포 적용
           ↓
Kubernetes → Deployment/Service 생성 → Pod 기동
```

---

## 📁 디렉토리 구조

```bash
.
├── .github/workflows/docker-ci.yml         # GitHub Actions 워크플로우
├── springboot-app/                         # Spring Boot 애플리케이션
├── springboot-deployment.yaml             # K8s 배포 매니페스트
├── devops-architecture.puml               # 전체 흐름 PlantUML 다이어그램
└── README.md
```

---

## 🔁 자동화 흐름

1. 코드를 GitHub `main` 브랜치에 Push
2. GitHub Actions가 자동으로 Docker 이미지 빌드 및 DockerHub Push
3. ArgoCD가 Git 변경 감지 → 자동 Sync (또는 수동 Sync)
4. K8s에 최신 버전 배포
5. Pod 기동, Readiness 체크(`/`) 완료 시 배포 완료

---

## ✅ Health 체크

- Readiness Probe 경로: `/`
- 간단한 `RootController` 추가로 대응
- `/` 경로에 대해 `200 OK` 응답하면 ArgoCD에서 `Healthy` 판정

---

## 💡 개선 포인트 (빌드 속도 포함)

- **Gradle 캐시 활용:** `--build-cache`, `.gradle` 캐시 mount로 속도 향상 가능
- **multi-platform build 시간 단축:** QEMU 없이 `linux/amd64` only 가능
- **Helm 도입:** `yaml` 분리 → chart 구조화로 유지보수성 향상
- **ArgoCD Autosync 활성화:** 자동 배포 전체 흐름 완성




