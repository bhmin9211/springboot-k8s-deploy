#!/usr/bin/env bash

set -euo pipefail

PROFILE="${MINIKUBE_PROFILE:-minikube}"
BACKEND_IMAGE="${BACKEND_IMAGE:-kubeops-backend:dev}"
FRONTEND_IMAGE="${FRONTEND_IMAGE:-kubeops-frontend:dev}"
API_HOST="${API_HOST:-api.kubeops.local}"

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

echo "✅ minikube 이미지 준비 완료"
