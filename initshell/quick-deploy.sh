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

"$(dirname "$0")/build-minikube-images.sh"

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
echo "권장 접속 방식:"
echo "1. 별도 터미널에서 minikube tunnel 실행"
echo "2. /etc/hosts 에 아래를 추가"
echo "   127.0.0.1 $API_HOST $FRONTEND_HOST"
