#!/bin/bash
set -e

echo "🔍 Minikube 상태 확인 중..."
if ! minikube status | grep -q "host: Running"; then
  echo "🚀 Minikube 시작 중..."
  minikube start --driver=docker
else
  echo "✅ Minikube는 이미 실행 중입니다."
fi

echo "🔍 ArgoCD 설치 여부 확인 중..."
if ! kubectl get ns argocd &>/dev/null; then
  echo "📦 ArgoCD 설치 중..."
  kubectl create namespace argocd
  kubectl apply -n argocd -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
else
  echo "✅ ArgoCD는 이미 설치되어 있습니다."
fi

if ! lsof -i :8080 &>/dev/null; then
  echo "🌐 포트포워딩 시작 (백그라운드)..."
  nohup kubectl port-forward svc/argocd-server -n argocd 8080:443 > argocd-portforward.log 2>&1 &
else
  echo "🔁 포트포워딩 이미 실행 중"
fi

echo "📄 values.yaml 생성 중..."
./generate-values.sh

echo "📦 Helm 배포 중..."
./deploy-helm.sh

echo "🧩 ArgoCD 앱 등록 중..."
./register-argocd.sh

echo "🎉 전체 자동화 완료!"
