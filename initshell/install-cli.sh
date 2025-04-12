#!/bin/bash

set -e

OS_TYPE=$(uname -s)

install_helm() {
  echo "🔍 Helm 설치 중..."
  if command -v helm &>/dev/null; then
    echo "✅ Helm 이미 설치됨: $(helm version --short)"
    return
  fi

  case "$OS_TYPE" in
    Linux)
      curl https://raw.githubusercontent.com/helm/helm/main/scripts/get-helm-3 | bash
      ;;
    Darwin)
      brew install helm
      ;;
    MINGW*|MSYS*|CYGWIN*|Windows_NT)
      mkdir -p ~/bin
      curl -sSL https://get.helm.sh/helm-v3.13.2-windows-amd64.zip -o helm.zip
      unzip helm.zip
      mv windows-amd64/helm.exe ~/bin/helm.exe
      rm -rf helm.zip windows-amd64
      ;;
    *)
      echo "❌ 알 수 없는 OS: $OS_TYPE"
      exit 1
      ;;
  esac
}

install_argocd() {
  echo "🔍 ArgoCD CLI 설치 중..."
  if command -v argocd &>/dev/null; then
    echo "✅ ArgoCD CLI 이미 설치됨: $(argocd version --client --short || echo '확인 완료')"
    return
  fi

  case "$OS_TYPE" in
    Linux)
      curl -sSL -o argocd https://github.com/argoproj/argo-cd/releases/latest/download/argocd-linux-amd64
      chmod +x argocd
      sudo mv argocd /usr/local/bin/
      ;;
    Darwin)
      brew install argocd
      ;;
    MINGW*|MSYS*|CYGWIN*|Windows_NT)
      mkdir -p ~/bin
      curl -sSL -o ~/bin/argocd.exe https://github.com/argoproj/argo-cd/releases/latest/download/argocd-windows-amd64.exe
      chmod +x ~/bin/argocd.exe
      ;;
    *)
      echo "❌ 알 수 없는 OS: $OS_TYPE"
      exit 1
      ;;
  esac
}

echo "📦 Helm & ArgoCD 설치 자동화 시작"
install_helm
install_argocd

echo "✅ PATH에 ~/bin 추가 권장 (Git Bash 사용자)"
echo 'export PATH="$HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc || true

echo "🎉 모든 CLI 설치 완료!"
