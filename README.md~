# DevOps Portfolio: Spring Boot + Vue 자동 배포 환경 구축

## 📄 프로젝트 개요
Spring Boot 백엔드와 Vue 프론트엔드를 기반으로, GitHub Actions, Docker, Helm, Argo CD, Kubernetes를 활용한 CI/CD 자동화 및 GitOps 배포 파이프라인을 구성했습니다.

---

## 🚀 사용 기술 스택
- **Backend**: Java 17, Spring Boot
- **Frontend**: Vue 3, Vite
- **CI/CD**: GitHub Actions
- **Containerization**: Docker, DockerHub
- **Kubernetes**: Minikube (로컬 클러스터)
- **배포 자동화**: Helm, Argo CD

---

## 🔄 전체 흐름도
```mermaid
graph TD
A[코드 푸시] --> B[GitHub Actions 실행]
B --> C[Docker Build & Push - :latest, :SHA tag]
C --> D[Helm values.yaml 이미지 태그 업데이트]
D --> E[Git Push - tag 변경 반영]
E --> F[ArgoCD 자동 Sync & 배포]
```

---

## 🏙️ 서비스 아키텍처
- Spring Boot 앱: `/api/hello` 제공
- Vue 프론트엔드: API 호출하여 메시지 출력
- Ingress: `springboot.local` 도메인에서 전체 접근 가능
- Readiness Probe로 헬스체크

---

## 📁 디렉토리 구조 (중요 부분)
```
.
├── .github/workflows/ci.yml       # GitHub Actions CI 설정
├── springboot-app/                # Spring Boot 애플리케이션
├── frontend/                      # Vue 프론트엔드 앱
├── springboot-helm-chart/        # Helm chart (Spring Boot)
├── README.md
```

---

## 🛠 GitHub Actions 핵심 설정 요약
- main 브랜치 푸시 시 CI 트리거
- `byunghyukmin/springboot-app:{latest, SHA}` 이미지 생성
- SHA 값을 Helm values.yaml 에 반영 후 자동 커밋/푸시

---

## ✨ ArgoCD 설정
- Application 생성: `springboot-helm` 
- Git 저장소에서 Helm chart path 추적: `/springboot-helm-chart`
- 자동 Sync & Auto-prune 활성화

---

## 🌐 접속 방법 (로컬 테스트)
- `/etc/hosts`에 다음 추가:
```
127.0.0.1 springboot.local
```
- 브라우저 접속: `http://springboot.local`

---

## 🔧 향후 개선 계획
- 프론트엔드(Vue) CI/CD 자동화 추가
- TLS(HTTPS) Ingress 적용
- HPA, 리소스 제한 설정 강화
- GitOps 배포로 QA/Prod 분리

---

## 📖 참고 명령어
```bash
kubectl get all
kubectl describe pod <pod-name>
kubectl port-forward svc/springboot-service 8080:80
curl http://localhost:8080
```

---

## 😊 만든 사람
- GitHub: [bhmin9211](https://github.com/bhmin9211)
- DockerHub: [byunghyukmin](https://hub.docker.com/u/byunghyukmin)

---

> 이 프로젝트는 DevOps  개인 포트폴리오입니다. 개선 의견은 언제든 환영합니다 🙌
