#!/bin/bash

cd "$(dirname "$0")"

# 설정
RELEASE_NAME="springboot-app"
APP_NAME="springboot-app"
CHART_PATH="../springboot-helm-chart"
VALUES_FILE="$CHART_PATH/values.yaml"
REPO_URL="https://github.com/bhmin9211/springboot-k8s-deploy.git"
DEST_SERVER="https://kubernetes.default.svc"
DEST_NAMESPACE="default"
ARGOCD_SERVER="localhost:8080"
NEW_PASSWORD="admin123!@#"

### [1/4] Argo CD 설치 확인
echo "✅ [1/4] Argo CD 설치 확인 중..."
if ! kubectl get ns argocd &>/dev/null; then
  echo "📦 Argo CD 설치 중..."
  kubectl create namespace argocd
  kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
  echo "✅ Argo CD 설치 완료!"
else
  echo "✅ 이미 설치됨"
fi

### [2/4] 포트포워딩
echo "🔌 [2/4] 포트포워딩 상태 확인 중..."
EXISTING_PID=$(lsof -ti tcp:8080)
if [ -z "$EXISTING_PID" ]; then
  echo "▶ 포트포워딩 시작 (8080)..."
  kubectl port-forward svc/argocd-server -n argocd 8080:443 >/dev/null 2>&1 &
  sleep 5
else
  echo "✅ 포트 8080 사용 중 (PID: $EXISTING_PID)"
fi

### [3/4] 로그인 및 비밀번호 변경
echo "🔐 [3/4] ArgoCD 로그인 중..."
ARGOCD_PASSWORD=$(kubectl -n argocd get secret argocd-initial-admin-secret \
  -o jsonpath="{.data.password}" | base64 -d)

argocd login $ARGOCD_SERVER --username admin --password "$ARGOCD_PASSWORD" --insecure

echo "🔑 비밀번호 변경 시도 중..."
if argocd account update-password --current-password "$ARGOCD_PASSWORD" --new-password "$NEW_PASSWORD"; then
  echo "🔓 재로그인 (새 비밀번호 사용)..."
  argocd login $ARGOCD_SERVER --username admin --password "$NEW_PASSWORD" --insecure
else
  echo "⚠️ 비밀번호 변경 실패 또는 이미 변경됨. 기존 비밀번호로 로그인 유지"
fi

### [4/4] Helm 배포
echo "📦 Helm 배포 중..."
if [ ! -f "$VALUES_FILE" ]; then
  echo "❌ values 파일이 없습니다: $VALUES_FILE"
  exit 1
fi

if [ ! -d "$CHART_PATH" ]; then
  echo "❌ Helm Chart 디렉토리가 없습니다: $CHART_PATH"
  exit 1
fi

if helm list -q | grep -q "^$RELEASE_NAME$"; then
  echo "🔄 기존 Helm 릴리스를 업데이트합니다..."
  helm upgrade "$RELEASE_NAME" "$CHART_PATH" -f "$VALUES_FILE"
else
  echo "🚀 Helm 차트를 새로 배포합니다..."
  helm install "$RELEASE_NAME" "$CHART_PATH" -f "$VALUES_FILE"
fi
echo "✅ Helm 배포 완료!"

### ArgoCD 앱 등록 및 동기화
echo "🚀 ArgoCD 앱 등록 및 동기화 중..."
if argocd app get $APP_NAME &>/dev/null; then
  echo "🧹 기존 앱 삭제..."
  argocd app delete $APP_NAME --yes
  sleep 3
fi

argocd app create $APP_NAME \
  --repo $REPO_URL \
  --path $(basename $CHART_PATH) \
  --dest-server $DEST_SERVER \
  --dest-namespace $DEST_NAMESPACE \
  --sync-policy automated \
  --self-heal \
  --auto-prune \
  --upsert

argocd app sync $APP_NAME

echo "🎉 완료! ArgoCD 앱이 등록되고 최신 상태로 동기화되었습니다."

