# DevOps 자동화 템플릿 (config.yaml 기반)

이 템플릿은 `config.yaml` 설정 한 번으로 Minikube, ArgoCD, Helm 배포까지 자동 구성하는 환경을 제공합니다.

## 🧾 구성 파일 예시

```yaml
app:
  name: springboot-app
  image: byunghyukmin/springboot-app
  tag: latest
  port: 8080
  path: /
  ingress: springboot.local
  namespace: default
```

## ⚙️ 사용 방법

1. config.yaml에 값을 채워 넣습니다.
2. PowerShell 또는 Bash 스크립트를 실행합니다.
3. 자동으로:
   - minikube 실행 확인
   - Argo CD 설치
   - values.yaml 생성 및 Helm install
   - Argo CD 자동 동기화

## 📂 파일 구성

- `config.yaml` : 앱 설정 파일
- `generate-values.py` : config.yaml을 읽어 Helm values.yaml 생성 (추후 제공)
- `install-argocd.ps1` / `install-argocd.sh` : Minikube 및 ArgoCD 자동 구성
