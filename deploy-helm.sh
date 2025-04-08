#!/bin/bash

VALUES_FILE="generated-values.yaml"
RELEASE_NAME="springboot-helm"
CHART_PATH="./springboot-helm-chart"

if [ ! -f "$VALUES_FILE" ]; then
  echo "❌ values 파일이 없습니다: $VALUES_FILE"
  exit 1
fi

if [ ! -d "$CHART_PATH" ]; then
  echo "❌ Helm Chart 디렉토리가 없습니다: $CHART_PATH"
  exit 1
fi

# 릴리스 상태에 따라 install/upgrade 분기
if helm list -q | grep -q "^$RELEASE_NAME$"; then
  echo "🔄 기존 Helm 릴리스를 업데이트합니다..."
  helm upgrade $RELEASE_NAME $CHART_PATH -f $VALUES_FILE
else
  echo "🚀 Helm 차트를 새로 배포합니다..."
  helm install $RELEASE_NAME $CHART_PATH -f $VALUES_FILE
fi

echo "✅ Helm 배포 완료!"
