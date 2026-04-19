#!/usr/bin/env bash

set -euo pipefail

cd "$(dirname "$0")/.."

APP_NAME="${ARGOCD_APP_NAME:-springboot-app-local}"
REPO_URL="${ARGOCD_REPO_URL:-https://github.com/bhmin9211/springboot-k8s-deploy.git}"
CHART_PATH="springboot-helm-chart"
VALUES_FILE="minikube-values.yaml"
DEST_SERVER="https://kubernetes.default.svc"
DEST_NAMESPACE="${ARGOCD_DEST_NAMESPACE:-default}"
ARGOCD_SERVER="${ARGOCD_SERVER:-localhost:8080}"

echo "📦 Argo CD 설치 확인"
if ! kubectl get ns argocd >/dev/null 2>&1; then
  kubectl create namespace argocd
  kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
fi

if ! lsof -i :8080 >/dev/null 2>&1; then
  echo "🔌 argocd-server 포트포워딩 시작"
  nohup kubectl port-forward svc/argocd-server -n argocd 8080:443 > initshell/argocd-portforward.log 2>&1 &
  sleep 5
fi

echo "🛠️ 로컬 minikube 이미지 준비"
"$(dirname "$0")/quick-deploy.sh"

ARGOCD_PASSWORD="$(kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath='{.data.password}' | base64 -d)"
argocd login "$ARGOCD_SERVER" --username admin --password "$ARGOCD_PASSWORD" --insecure

echo "🧩 Argo CD 앱 등록"
argocd app create "$APP_NAME" \
  --repo "$REPO_URL" \
  --path "$CHART_PATH" \
  --values "$VALUES_FILE" \
  --dest-server "$DEST_SERVER" \
  --dest-namespace "$DEST_NAMESPACE" \
  --sync-policy automated \
  --self-heal \
  --auto-prune \
  --upsert

argocd app sync "$APP_NAME"

echo "✅ Argo CD 앱 등록 완료: $APP_NAME"
echo "기본 GitOps 시연 경로가 준비되었습니다."
