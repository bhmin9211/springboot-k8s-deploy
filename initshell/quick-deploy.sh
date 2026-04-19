#!/usr/bin/env bash

set -euo pipefail

PROFILE="${MINIKUBE_PROFILE:-minikube}"
NAMESPACE="${NAMESPACE:-default}"
BACKEND_IMAGE="${BACKEND_IMAGE:-kubeops-backend:dev}"
FRONTEND_IMAGE="${FRONTEND_IMAGE:-kubeops-frontend:dev}"
API_HOST="${API_HOST:-api.kubeops.local}"
FRONTEND_HOST="${FRONTEND_HOST:-dashboard.kubeops.local}"

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"

cd "$ROOT_DIR"

echo "🐳 minikube docker-env 연결"
eval "$(minikube -p "$PROFILE" docker-env)"

echo "🏗️ backend image build: $BACKEND_IMAGE"
docker build -t "$BACKEND_IMAGE" ./springboot-app

echo "🏗️ frontend image build: $FRONTEND_IMAGE"
docker build \
  -t "$FRONTEND_IMAGE" \
  --build-arg VITE_API_BASE_URL="http://$API_HOST/api" \
  --build-arg VITE_AUTH_LOGIN_PATH="/auth/login/keycloak" \
  --build-arg VITE_AUTH_LOGOUT_PATH="/auth/logout" \
  ./frontend

echo "🚀 Helm 배포"
helm upgrade --install springboot-app ./springboot-helm-chart \
  --namespace "$NAMESPACE" \
  --create-namespace \
  -f ./springboot-helm-chart/minikube-values.yaml \
  --set image.repository="${BACKEND_IMAGE%%:*}" \
  --set image.tag="${BACKEND_IMAGE##*:}" \
  --set frontend.image.repository="${FRONTEND_IMAGE%%:*}" \
  --set frontend.image.tag="${FRONTEND_IMAGE##*:}" \
  --set ingress.host="$API_HOST" \
  --set frontend.ingress.host="$FRONTEND_HOST" \
  --set-string backendEnv[3].value="http://$FRONTEND_HOST" \
  --set-string backendEnv[4].value="false"

echo
echo "✅ minikube 배포 완료"
echo "hosts 파일에 아래를 추가하세요:"
echo "$(minikube -p "$PROFILE" ip) $API_HOST $FRONTEND_HOST"
