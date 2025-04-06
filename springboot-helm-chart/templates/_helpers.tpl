# _helpers.tpl
{{/* Generate a fullname for resources */}}
{{- define "springboot-app.fullname" -}}
{{- printf "%s" .Release.Name -}}
{{- end }}

