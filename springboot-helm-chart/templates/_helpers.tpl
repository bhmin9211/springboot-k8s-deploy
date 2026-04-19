# _helpers.tpl
{{/* Generate a fullname for resources */}}
{{- define "springboot-app.fullname" -}}
{{- printf "%s" .Release.Name -}}
{{- end }}

{{/* Shared labels */}}
{{- define "springboot-app.labels" -}}
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/part-of: kubeops-platform
helm.sh/chart: {{ printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" }}
{{- end }}

{{/* Backend selector labels */}}
{{- define "springboot-app.backendSelectorLabels" -}}
app.kubernetes.io/name: {{ .Chart.Name }}
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: backend
{{- end }}

{{/* Frontend selector labels */}}
{{- define "springboot-app.frontendSelectorLabels" -}}
app.kubernetes.io/name: frontend-service
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/component: frontend
{{- end }}
