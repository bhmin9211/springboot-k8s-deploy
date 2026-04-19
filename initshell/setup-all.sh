#!/usr/bin/env bash

set -euo pipefail

PROFILE="${MINIKUBE_PROFILE:-minikube}"
CPUS="${MINIKUBE_CPUS:-4}"
MEMORY="${MINIKUBE_MEMORY:-8192}"
DRIVER="${MINIKUBE_DRIVER:-docker}"

cd "$(dirname "$0")/.."

echo "🚀 minikube 시작"
minikube start -p "$PROFILE" --driver="$DRIVER" --cpus="$CPUS" --memory="$MEMORY"

echo "🔌 ingress addon 활성화"
minikube -p "$PROFILE" addons enable ingress

echo "⏳ ingress controller 준비 대기"
kubectl wait --namespace ingress-nginx \
  --for=condition=ready pod \
  --selector=app.kubernetes.io/component=controller \
  --timeout=180s || true

MINIKUBE_IP="$(minikube -p "$PROFILE" ip)"

echo
echo "✅ minikube 준비 완료"
echo "minikube ip: $MINIKUBE_IP"
echo
echo "hosts 파일 예시:"
echo "$MINIKUBE_IP api.kubeops.local dashboard.kubeops.local"
echo
echo "다음 단계:"
echo "1. docker-compose -f compose.local.yml up -d mariadb keycloak-db keycloak"
echo "2. 기본 GitOps 경로: ./initshell/register-argocd.sh"
echo "3. 빠른 검증 경로: ./initshell/quick-deploy.sh"
