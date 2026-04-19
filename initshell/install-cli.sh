#!/usr/bin/env bash

set -euo pipefail

if [[ "$(uname -s)" != "Darwin" ]]; then
  echo "이 스크립트는 macOS 기준으로 작성되었습니다."
  exit 1
fi

if ! command -v brew >/dev/null 2>&1; then
  echo "Homebrew가 필요합니다. 먼저 https://brew.sh 에서 설치해주세요."
  exit 1
fi

echo "📦 macOS용 CLI 설치를 시작합니다."

brew update
brew install kubectl minikube helm argocd docker-compose

if ! command -v docker >/dev/null 2>&1; then
  echo "⚠️ docker CLI가 없습니다. Docker Desktop 설치가 필요합니다."
  echo "   brew install --cask docker"
fi

echo
echo "✅ 설치 확인"
echo "kubectl: $(kubectl version --client --output=yaml 2>/dev/null | rg 'gitVersion' -m 1 || true)"
echo "minikube: $(minikube version | head -n 1)"
echo "helm: $(helm version --short)"
echo "argocd: $(argocd version --client --short 2>/dev/null || echo 'installed')"
echo "docker-compose: $(docker-compose version --short 2>/dev/null || echo 'not found')"
echo
echo "다음 단계:"
echo "1. ./initshell/setup-all.sh"
echo "2. docker-compose -f compose.local.yml up -d mariadb keycloak keycloak-db"
echo "3. ./initshell/register-argocd.sh"
echo "   빠른 앱 검증만 필요하면: ./initshell/quick-deploy.sh"
