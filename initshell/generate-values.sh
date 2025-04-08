#!/bin/bash

CONFIG_FILE="config.yaml"
OUTPUT_FILE="generated-values.yaml"

if [ ! -f "$CONFIG_FILE" ]; then
  echo "❌ config.yaml 파일이 없습니다."
  exit 1
fi

# 필요한 값 추출
name=$(grep 'name:' $CONFIG_FILE | awk '{print $2}')
image=$(grep 'image:' $CONFIG_FILE | awk '{print $2}')
tag=$(grep 'tag:' $CONFIG_FILE | awk '{print $2}')
port=$(grep 'port:' $CONFIG_FILE | awk '{print $2}')
path=$(grep 'path:' $CONFIG_FILE | awk '{print $2}')
ingress=$(grep 'ingress:' $CONFIG_FILE | awk '{print $2}')

# values.yaml 생성
cat <<EOF > $OUTPUT_FILE
replicaCount: 1

image:
  repository: $image
  tag: $tag
  pullPolicy: IfNotPresent

service:
  name: ${name}-service
  type: LoadBalancer
  port: 80
  targetPort: $port

readinessProbe:
  enabled: true
  path: $path
  port: $port
  initialDelaySeconds: 2
  periodSeconds: 3
  timeoutSeconds: 1
  failureThreshold: 3
  successThreshold: 1

resources: {{}}

ingress:
  enabled: true
  host: $ingress
EOF

echo "✅ $OUTPUT_FILE 생성 완료"
