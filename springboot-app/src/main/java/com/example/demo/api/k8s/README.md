# 쿠버네티스 API (헥사고날 아키텍처)

## 개요

이 프로젝트는 쿠버네티스 API를 Java로 구현한 것으로, 헥사고날 아키텍처 패턴을 적용하여 설계되었습니다.

## 아키텍처 구조

### 헥사고날 아키텍처 구성요소

```
┌─────────────────────────────────────────────────────────────┐
│                    인바운드 어댑터 (Adapter In)              │
│  ┌─────────────────────────────────────────────────────┐   │
│  │              KubernetesController                   │   │
│  │              (REST API 엔드포인트)                   │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    포트 (Ports)                             │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │ KubernetesQuery │  │KubernetesCommand│                  │
│  │     Port        │  │     Port        │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    핵심 비즈니스 로직 (Core)                │
│  ┌─────────────────┐  ┌─────────────────┐                  │
│  │KubernetesQuery  │  │KubernetesCommand│                  │
│  │   Service       │  │   Service       │                  │
│  └─────────────────┘  └─────────────────┘                  │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                    포트 (Ports)                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │            KubernetesRepositoryPort                 │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────┐
│                   아웃바운드 어댑터 (Adapter Out)            │
│  ┌─────────────────────────────────────────────────────┐   │
│  │           KubernetesClientAdapter                   │   │
│  │           (실제 쿠버네티스 클라이언트)               │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

## 주요 특징

### 1. Builder 패턴 적용
- 모든 모델 클래스에 `@Builder` 어노테이션 적용
- 객체 생성 시 가독성과 유연성 향상
- 예시:
```java
PodInfo pod = PodInfo.builder()
    .name("my-pod")
    .namespace("default")
    .status("Running")
    .build();
```

### 2. 헥사고날 아키텍처 적용
- **인바운드 포트**: `KubernetesQueryPort`, `KubernetesCommandPort`
- **아웃바운드 포트**: `KubernetesRepositoryPort`
- **핵심 서비스**: `KubernetesQueryService`, `KubernetesCommandService`
- **어댑터**: `KubernetesController`, `KubernetesClientAdapter`

### 3. 의존성 역전 원칙
- 핵심 비즈니스 로직이 외부 시스템에 의존하지 않음
- 포트를 통한 인터페이스 정의
- 테스트 용이성 및 유지보수성 향상

## API 엔드포인트

### Pod 관련 API
- `GET /api/k8s/pods` - 모든 Pod 조회
- `GET /api/k8s/namespaces/{namespace}/pods` - 특정 네임스페이스의 Pod 조회
- `GET /api/k8s/namespaces/{namespace}/pods/{name}` - 특정 Pod 조회
- `DELETE /api/k8s/namespaces/{namespace}/pods/{name}` - Pod 삭제

### Node 관련 API
- `GET /api/k8s/nodes` - 모든 Node 조회
- `GET /api/k8s/nodes/{name}` - 특정 Node 조회

### Deployment 관련 API
- `GET /api/k8s/deployments` - 모든 Deployment 조회
- `GET /api/k8s/namespaces/{namespace}/deployments` - 특정 네임스페이스의 Deployment 조회
- `GET /api/k8s/namespaces/{namespace}/deployments/{name}` - 특정 Deployment 조회
- `PUT /api/k8s/namespaces/{namespace}/deployments/{name}/scale?replicas={count}` - Deployment 스케일링

### Service 관련 API
- `GET /api/k8s/services` - 모든 Service 조회
- `GET /api/k8s/namespaces/{namespace}/services` - 특정 네임스페이스의 Service 조회
- `GET /api/k8s/namespaces/{namespace}/services/{name}` - 특정 Service 조회
- `DELETE /api/k8s/namespaces/{namespace}/services/{name}` - Service 삭제

### Namespace 관련 API
- `GET /api/k8s/namespaces` - 모든 Namespace 조회
- `POST /api/k8s/namespaces?name={name}` - Namespace 생성
- `DELETE /api/k8s/namespaces/{name}` - Namespace 삭제

### 클러스터 정보 API
- `GET /api/k8s/cluster/info` - 클러스터 정보 조회
- `GET /api/k8s/health` - 쿠버네티스 API 연결 상태 확인

## 모델 클래스

### PodInfo
```java
@Builder
public class PodInfo {
    private String name;
    private String namespace;
    private String status;
    private String podIP;
    private String nodeName;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String image;
    private String restartCount;
    private String ready;
    private String phase;
}
```

### NodeInfo
```java
@Builder
public class NodeInfo {
    private String name;
    private String status;
    private String role;
    private String version;
    private String internalIP;
    private String externalIP;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String architecture;
    private String operatingSystem;
    private String kernelVersion;
    private String containerRuntime;
    private String kubeletVersion;
    private String kubeProxyVersion;
}
```

### DeploymentInfo
```java
@Builder
public class DeploymentInfo {
    private String name;
    private String namespace;
    private String status;
    private Integer replicas;
    private Integer availableReplicas;
    private Integer readyReplicas;
    private Integer updatedReplicas;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String strategy;
    private String image;
    private String selector;
}
```

### ServiceInfo
```java
@Builder
public class ServiceInfo {
    private String name;
    private String namespace;
    private String type;
    private String clusterIP;
    private String externalIP;
    private List<String> ports;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String selector;
    private String sessionAffinity;
    private String loadBalancerIP;
}
```

## 사용 예시

### 1. 모든 Pod 조회
```bash
curl -X GET http://localhost:8080/api/k8s/pods
```

### 2. 특정 네임스페이스의 Pod 조회
```bash
curl -X GET http://localhost:8080/api/k8s/namespaces/default/pods
```

### 3. Deployment 스케일링
```bash
curl -X PUT "http://localhost:8080/api/k8s/namespaces/default/deployments/my-app/scale?replicas=3"
```

### 4. 클러스터 정보 조회
```bash
curl -X GET http://localhost:8080/api/k8s/cluster/info
```

## 장점

1. **유지보수성**: 헥사고날 아키텍처로 인한 명확한 책임 분리
2. **테스트 용이성**: 포트를 통한 인터페이스 분리로 단위 테스트 용이
3. **확장성**: 새로운 어댑터 추가 시 기존 코드 변경 없이 확장 가능
4. **가독성**: Builder 패턴으로 인한 객체 생성 코드의 가독성 향상
5. **한글 로깅**: 모든 로그 메시지가 한글로 작성되어 이해하기 쉬움

## 의존성

- Spring Boot 3.1.5
- Fabric8 Kubernetes Client 6.8.1
- Lombok
- MariaDB Driver
- JWT 