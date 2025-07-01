package com.example.demo.api.k8s.adapter.out;

import com.example.demo.api.k8s.model.*;
import com.example.demo.api.k8s.port.out.KubernetesRepositoryPort;
import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.api.model.StatusDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 쿠버네티스 클라이언트 어댑터 (헥사고날 아키텍처의 아웃바운드 어댑터)
 * 실제 쿠버네티스 API와 통신하는 구현체
 */
@Component
@Slf4j
public class KubernetesClientAdapter implements KubernetesRepositoryPort {
    
    private final KubernetesClient client;
    
    public KubernetesClientAdapter() {
        this.client = new KubernetesClientBuilder().build();
    }
    
    // Pod 관련 구현
    @Override
    public List<PodInfo> findAllPods() {
        try {
            return client.pods().list().getItems().stream()
                    .map(this::convertToPodInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Pod 조회 중 오류 발생: ", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<PodInfo> findPodsByNamespace(String namespace) {
        try {
            return client.pods().inNamespace(namespace).list().getItems().stream()
                    .map(this::convertToPodInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("네임스페이스 {}의 Pod 조회 중 오류 발생: ", namespace, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public PodInfo findPodByName(String namespace, String name) {
        try {
            Pod pod = client.pods().inNamespace(namespace).withName(name).get();
            return pod != null ? convertToPodInfo(pod) : null;
        } catch (Exception e) {
            log.error("Pod {}/{} 조회 중 오류 발생: ", namespace, name, e);
            return null;
        }
    }
    
    @Override
    public boolean deletePod(String namespace, String name) {
        try {
            List<StatusDetails> result = client.pods().inNamespace(namespace).withName(name).delete();
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("Pod {}/{} 삭제 중 오류 발생: ", namespace, name, e);
            return false;
        }
    }
    
    // Node 관련 구현
    @Override
    public List<NodeInfo> findAllNodes() {
        try {
            return client.nodes().list().getItems().stream()
                    .map(this::convertToNodeInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Node 조회 중 오류 발생: ", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public NodeInfo findNodeByName(String name) {
        try {
            Node node = client.nodes().withName(name).get();
            return node != null ? convertToNodeInfo(node) : null;
        } catch (Exception e) {
            log.error("Node {} 조회 중 오류 발생: ", name, e);
            return null;
        }
    }
    
    // Deployment 관련 구현
    @Override
    public List<DeploymentInfo> findAllDeployments() {
        try {
            return client.apps().deployments().list().getItems().stream()
                    .map(this::convertToDeploymentInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Deployment 조회 중 오류 발생: ", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<DeploymentInfo> findDeploymentsByNamespace(String namespace) {
        try {
            return client.apps().deployments().inNamespace(namespace).list().getItems().stream()
                    .map(this::convertToDeploymentInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("네임스페이스 {}의 Deployment 조회 중 오류 발생: ", namespace, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public DeploymentInfo findDeploymentByName(String namespace, String name) {
        try {
            Deployment deployment = client.apps().deployments().inNamespace(namespace).withName(name).get();
            return deployment != null ? convertToDeploymentInfo(deployment) : null;
        } catch (Exception e) {
            log.error("Deployment {}/{} 조회 중 오류 발생: ", namespace, name, e);
            return null;
        }
    }
    
    @Override
    public boolean scaleDeployment(String namespace, String name, int replicas) {
        try {
            client.apps().deployments().inNamespace(namespace).withName(name).scale(replicas);
            return true;
        } catch (Exception e) {
            log.error("Deployment {}/{} 스케일링 중 오류 발생: ", namespace, name, e);
            return false;
        }
    }
    
    // Service 관련 구현
    @Override
    public List<ServiceInfo> findAllServices() {
        try {
            return client.services().list().getItems().stream()
                    .map(this::convertToServiceInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Service 조회 중 오류 발생: ", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<ServiceInfo> findServicesByNamespace(String namespace) {
        try {
            return client.services().inNamespace(namespace).list().getItems().stream()
                    .map(this::convertToServiceInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("네임스페이스 {}의 Service 조회 중 오류 발생: ", namespace, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public ServiceInfo findServiceByName(String namespace, String name) {
        try {
            Service service = client.services().inNamespace(namespace).withName(name).get();
            return service != null ? convertToServiceInfo(service) : null;
        } catch (Exception e) {
            log.error("Service {}/{} 조회 중 오류 발생: ", namespace, name, e);
            return null;
        }
    }
    
    @Override
    public boolean deleteService(String namespace, String name) {
        try {
            List<StatusDetails> result = client.services().inNamespace(namespace).withName(name).delete();
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("Service {}/{} 삭제 중 오류 발생: ", namespace, name, e);
            return false;
        }
    }
    
    // Namespace 관련 구현
    @Override
    public List<String> findAllNamespaces() {
        try {
            return client.namespaces().list().getItems().stream()
                    .map(namespace -> namespace.getMetadata().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Namespace 조회 중 오류 발생: ", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean createNamespace(String name) {
        try {
            Namespace namespace = new NamespaceBuilder()
                    .withNewMetadata()
                    .withName(name)
                    .endMetadata()
                    .build();
            client.namespaces().create(namespace);
            return true;
        } catch (Exception e) {
            log.error("Namespace {} 생성 중 오류 발생: ", name, e);
            return false;
        }
    }
    
    @Override
    public boolean deleteNamespace(String name) {
        try {
            List<StatusDetails> result = client.namespaces().withName(name).delete();
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("Namespace {} 삭제 중 오류 발생: ", name, e);
            return false;
        }
    }
    
    // 클러스터 정보 구현
    @Override
    public Map<String, Object> getClusterInfo() {
        try {
            Map<String, Object> clusterInfo = new HashMap<>();
            
            clusterInfo.put("apiServer", client.getMasterUrl().toString());
            clusterInfo.put("namespaceCount", findAllNamespaces().size());
            clusterInfo.put("podCount", findAllPods().size());
            clusterInfo.put("nodeCount", findAllNodes().size());
            clusterInfo.put("deploymentCount", findAllDeployments().size());
            clusterInfo.put("serviceCount", findAllServices().size());
            
            return clusterInfo;
        } catch (Exception e) {
            log.error("클러스터 정보 조회 중 오류 발생: ", e);
            return new HashMap<>();
        }
    }
    
    // 변환 메서드들 (Builder 패턴 사용)
    private PodInfo convertToPodInfo(Pod pod) {
        return PodInfo.builder()
                .name(pod.getMetadata().getName())
                .namespace(pod.getMetadata().getNamespace())
                .labels(pod.getMetadata().getLabels())
                .annotations(pod.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(pod.getMetadata().getCreationTimestamp()))
                .phase(pod.getStatus() != null ? pod.getStatus().getPhase() : null)
                .podIP(pod.getStatus() != null ? pod.getStatus().getPodIP() : null)
                .nodeName(pod.getStatus() != null ? pod.getStatus().getHostIP() : null)
                .image(pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && !pod.getStatus().getContainerStatuses().isEmpty() ?
                        pod.getStatus().getContainerStatuses().get(0).getImage() : null)
                .ready(pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && !pod.getStatus().getContainerStatuses().isEmpty() ?
                        (pod.getStatus().getContainerStatuses().get(0).getReady() ? "Ready" : "Not Ready") : null)
                .restartCount(pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && !pod.getStatus().getContainerStatuses().isEmpty() ?
                        String.valueOf(pod.getStatus().getContainerStatuses().get(0).getRestartCount()) : null)
                .build();
    }
    
    private NodeInfo convertToNodeInfo(Node node) {
        NodeInfo.NodeInfoBuilder builder = NodeInfo.builder()
                .name(node.getMetadata().getName())
                .labels(node.getMetadata().getLabels())
                .annotations(node.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(node.getMetadata().getCreationTimestamp()));
        
        if (node.getStatus() != null) {
            builder.version(node.getStatus().getNodeInfo().getKubeletVersion())
                    .architecture(node.getStatus().getNodeInfo().getArchitecture())
                    .operatingSystem(node.getStatus().getNodeInfo().getOperatingSystem())
                    .kernelVersion(node.getStatus().getNodeInfo().getKernelVersion())
                    .containerRuntime(node.getStatus().getNodeInfo().getContainerRuntimeVersion())
                    .kubeletVersion(node.getStatus().getNodeInfo().getKubeletVersion())
                    .kubeProxyVersion(node.getStatus().getNodeInfo().getKubeProxyVersion());
            
            // Node addresses
            if (node.getStatus().getAddresses() != null) {
                for (NodeAddress address : node.getStatus().getAddresses()) {
                    if ("InternalIP".equals(address.getType())) {
                        builder.internalIP(address.getAddress());
                    } else if ("ExternalIP".equals(address.getType())) {
                        builder.externalIP(address.getAddress());
                    }
                }
            }
            
            // Node conditions
            if (node.getStatus().getConditions() != null) {
                for (NodeCondition condition : node.getStatus().getConditions()) {
                    if ("Ready".equals(condition.getType())) {
                        builder.status(condition.getStatus());
                        break;
                    }
                }
            }
        }
        
        return builder.build();
    }
    
    private DeploymentInfo convertToDeploymentInfo(Deployment deployment) {
        DeploymentInfo.DeploymentInfoBuilder builder = DeploymentInfo.builder()
                .name(deployment.getMetadata().getName())
                .namespace(deployment.getMetadata().getNamespace())
                .labels(deployment.getMetadata().getLabels())
                .annotations(deployment.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(deployment.getMetadata().getCreationTimestamp()));
        
        if (deployment.getSpec() != null) {
            builder.replicas(deployment.getSpec().getReplicas())
                    .strategy(deployment.getSpec().getStrategy() != null ? deployment.getSpec().getStrategy().getType() : null);
            
            if (deployment.getSpec().getTemplate().getSpec().getContainers() != null && 
                !deployment.getSpec().getTemplate().getSpec().getContainers().isEmpty()) {
                builder.image(deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
            }
        }
        
        if (deployment.getStatus() != null) {
            builder.availableReplicas(deployment.getStatus().getAvailableReplicas())
                    .readyReplicas(deployment.getStatus().getReadyReplicas())
                    .updatedReplicas(deployment.getStatus().getUpdatedReplicas());
        }
        
        return builder.build();
    }
    
    private ServiceInfo convertToServiceInfo(Service service) {
        ServiceInfo.ServiceInfoBuilder builder = ServiceInfo.builder()
                .name(service.getMetadata().getName())
                .namespace(service.getMetadata().getNamespace())
                .labels(service.getMetadata().getLabels())
                .annotations(service.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(service.getMetadata().getCreationTimestamp()));
        
        if (service.getSpec() != null) {
            builder.type(service.getSpec().getType())
                    .clusterIP(service.getSpec().getClusterIP())
                    .externalIP(service.getSpec().getExternalIPs() != null ? 
                            String.join(",", service.getSpec().getExternalIPs()) : null)
                    .sessionAffinity(service.getSpec().getSessionAffinity())
                    .loadBalancerIP(service.getSpec().getLoadBalancerIP());
            
            if (service.getSpec().getSelector() != null) {
                builder.selector(service.getSpec().getSelector().toString());
            }
            
            if (service.getSpec().getPorts() != null) {
                builder.ports(service.getSpec().getPorts().stream()
                        .map(port -> port.getPort() + ":" + port.getTargetPort().getIntVal())
                        .collect(Collectors.toList()));
            }
        }
        
        return builder.build();
    }
    
    // 날짜 파싱 헬퍼 메서드
    private LocalDateTime parseCreationTimestamp(String timestamp) {
        if (timestamp == null) {
            return null;
        }
        try {
            // ISO 8601 형식의 날짜 문자열을 파싱
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", timestamp, e);
            return null;
        }
    }
} 