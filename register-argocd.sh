#!/bin/bash

APP_NAME="springboot-helm"
REPO_URL="https://github.com/bhmin9211/springboot-k8s-deploy.git"
CHART_PATH="springboot-helm-chart"
DEST_SERVER="https://kubernetes.default.svc"
DEST_NAMESPACE="default"

# 로그인 (자동)
PASSWORD=$(kubectl -n argocd get secret argocd-initial-admin-secret -o jsonpath="{.data.password}" | base64 -d)
argocd login localhost:8080 --username admin --password $PASSWORD --insecure

if argocd app get $APP_NAME &>/dev/null; then
  echo "🧹 기존 ArgoCD 앱 삭제 중..."
  argocd app delete $APP_NAME --yes
fi

echo "🚀 ArgoCD 앱 생성 중..."
argocd app create $APP_NAME \
  --repo $REPO_URL \
  --path $CHART_PATH \
  --dest-server $DEST_SERVER \
  --dest-namespace $DEST_NAMESPACE \
  --sync-policy automated \
  --self-heal \
  --auto-prune

echo "🔄 앱 동기화 중..."
argocd app sync $APP_NAME

echo "✅ ArgoCD 앱 등록 및 동기화 완료!"
