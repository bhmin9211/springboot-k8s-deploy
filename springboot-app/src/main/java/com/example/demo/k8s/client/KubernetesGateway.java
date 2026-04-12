package com.example.demo.k8s.client;

import com.example.demo.k8s.client.factory.KubernetesClientFactory;
import com.example.demo.k8s.dto.DeploymentInfo;
import com.example.demo.k8s.dto.NodeInfo;
import com.example.demo.k8s.dto.PodInfo;
import com.example.demo.k8s.dto.ServiceInfo;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeAddress;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Slf4j
public class KubernetesGateway {

    private final KubernetesClient client;

    public KubernetesGateway(KubernetesClientFactory kubernetesClientFactory) {
        this.client = kubernetesClientFactory.createClient();
    }

    public List<PodInfo> findAllPods() {
        try {
            return client.pods().list().getItems().stream()
                    .map(this::toPodInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Pod 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    public List<PodInfo> findPodsByNamespace(String namespace) {
        try {
            return client.pods().inNamespace(namespace).list().getItems().stream()
                    .map(this::toPodInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("네임스페이스 {}의 Pod 조회 중 오류 발생", namespace, e);
            return new ArrayList<>();
        }
    }

    public PodInfo findPodByName(String namespace, String name) {
        try {
            Pod pod = client.pods().inNamespace(namespace).withName(name).get();
            return pod != null ? toPodInfo(pod) : null;
        } catch (Exception e) {
            log.error("Pod {}/{} 조회 중 오류 발생", namespace, name, e);
            return null;
        }
    }

    public boolean deletePod(String namespace, String name) {
        try {
            List<StatusDetails> result = client.pods().inNamespace(namespace).withName(name).delete();
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("Pod {}/{} 삭제 중 오류 발생", namespace, name, e);
            return false;
        }
    }

    public List<NodeInfo> findAllNodes() {
        try {
            return client.nodes().list().getItems().stream()
                    .map(this::toNodeInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Node 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    public NodeInfo findNodeByName(String name) {
        try {
            Node node = client.nodes().withName(name).get();
            return node != null ? toNodeInfo(node) : null;
        } catch (Exception e) {
            log.error("Node {} 조회 중 오류 발생", name, e);
            return null;
        }
    }

    public List<DeploymentInfo> findAllDeployments() {
        try {
            return client.apps().deployments().list().getItems().stream()
                    .map(this::toDeploymentInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Deployment 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    public List<DeploymentInfo> findDeploymentsByNamespace(String namespace) {
        try {
            return client.apps().deployments().inNamespace(namespace).list().getItems().stream()
                    .map(this::toDeploymentInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("네임스페이스 {}의 Deployment 조회 중 오류 발생", namespace, e);
            return new ArrayList<>();
        }
    }

    public DeploymentInfo findDeploymentByName(String namespace, String name) {
        try {
            Deployment deployment = client.apps().deployments().inNamespace(namespace).withName(name).get();
            return deployment != null ? toDeploymentInfo(deployment) : null;
        } catch (Exception e) {
            log.error("Deployment {}/{} 조회 중 오류 발생", namespace, name, e);
            return null;
        }
    }

    public boolean scaleDeployment(String namespace, String name, int replicas) {
        try {
            client.apps().deployments().inNamespace(namespace).withName(name).scale(replicas);
            return true;
        } catch (Exception e) {
            log.error("Deployment {}/{} 스케일링 중 오류 발생", namespace, name, e);
            return false;
        }
    }

    public List<ServiceInfo> findAllServices() {
        try {
            return client.services().list().getItems().stream()
                    .map(this::toServiceInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Service 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    public List<ServiceInfo> findServicesByNamespace(String namespace) {
        try {
            return client.services().inNamespace(namespace).list().getItems().stream()
                    .map(this::toServiceInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("네임스페이스 {}의 Service 조회 중 오류 발생", namespace, e);
            return new ArrayList<>();
        }
    }

    public ServiceInfo findServiceByName(String namespace, String name) {
        try {
            Service service = client.services().inNamespace(namespace).withName(name).get();
            return service != null ? toServiceInfo(service) : null;
        } catch (Exception e) {
            log.error("Service {}/{} 조회 중 오류 발생", namespace, name, e);
            return null;
        }
    }

    public boolean deleteService(String namespace, String name) {
        try {
            List<StatusDetails> result = client.services().inNamespace(namespace).withName(name).delete();
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("Service {}/{} 삭제 중 오류 발생", namespace, name, e);
            return false;
        }
    }

    public List<String> findAllNamespaces() {
        try {
            return client.namespaces().list().getItems().stream()
                    .map(namespace -> namespace.getMetadata().getName())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("모든 Namespace 조회 중 오류 발생", e);
            return new ArrayList<>();
        }
    }

    public boolean createNamespace(String name) {
        try {
            Namespace namespace = new NamespaceBuilder()
                    .withNewMetadata()
                    .withName(name)
                    .endMetadata()
                    .build();
            client.namespaces().resource(namespace).create();
            return true;
        } catch (Exception e) {
            log.error("Namespace {} 생성 중 오류 발생", name, e);
            return false;
        }
    }

    public boolean deleteNamespace(String name) {
        try {
            List<StatusDetails> result = client.namespaces().withName(name).delete();
            return result != null && !result.isEmpty();
        } catch (Exception e) {
            log.error("Namespace {} 삭제 중 오류 발생", name, e);
            return false;
        }
    }

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
            log.error("클러스터 정보 조회 중 오류 발생", e);
            return new HashMap<>();
        }
    }

    private PodInfo toPodInfo(Pod pod) {
        return PodInfo.builder()
                .name(pod.getMetadata().getName())
                .namespace(pod.getMetadata().getNamespace())
                .labels(pod.getMetadata().getLabels())
                .annotations(pod.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(pod.getMetadata().getCreationTimestamp()))
                .status(pod.getStatus() != null ? pod.getStatus().getPhase() : null)
                .phase(pod.getStatus() != null ? pod.getStatus().getPhase() : null)
                .podIP(pod.getStatus() != null ? pod.getStatus().getPodIP() : null)
                .nodeName(pod.getSpec() != null ? pod.getSpec().getNodeName() : null)
                .image(pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && !pod.getStatus().getContainerStatuses().isEmpty()
                        ? pod.getStatus().getContainerStatuses().get(0).getImage() : null)
                .ready(pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && !pod.getStatus().getContainerStatuses().isEmpty()
                        ? (pod.getStatus().getContainerStatuses().get(0).getReady() ? "Ready" : "Not Ready") : null)
                .restartCount(pod.getStatus() != null && pod.getStatus().getContainerStatuses() != null && !pod.getStatus().getContainerStatuses().isEmpty()
                        ? String.valueOf(pod.getStatus().getContainerStatuses().get(0).getRestartCount()) : null)
                .build();
    }

    private NodeInfo toNodeInfo(Node node) {
        NodeInfo.NodeInfoBuilder builder = NodeInfo.builder()
                .name(node.getMetadata().getName())
                .labels(node.getMetadata().getLabels())
                .annotations(node.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(node.getMetadata().getCreationTimestamp()));

        if (node.getStatus() != null && node.getStatus().getNodeInfo() != null) {
            builder.version(node.getStatus().getNodeInfo().getKubeletVersion())
                    .architecture(node.getStatus().getNodeInfo().getArchitecture())
                    .operatingSystem(node.getStatus().getNodeInfo().getOperatingSystem())
                    .kernelVersion(node.getStatus().getNodeInfo().getKernelVersion())
                    .containerRuntime(node.getStatus().getNodeInfo().getContainerRuntimeVersion())
                    .kubeletVersion(node.getStatus().getNodeInfo().getKubeletVersion())
                    .kubeProxyVersion(node.getStatus().getNodeInfo().getKubeProxyVersion());

            if (node.getStatus().getAddresses() != null) {
                for (NodeAddress address : node.getStatus().getAddresses()) {
                    if ("InternalIP".equals(address.getType())) {
                        builder.internalIP(address.getAddress());
                    } else if ("ExternalIP".equals(address.getType())) {
                        builder.externalIP(address.getAddress());
                    }
                }
            }

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

    private DeploymentInfo toDeploymentInfo(Deployment deployment) {
        DeploymentInfo.DeploymentInfoBuilder builder = DeploymentInfo.builder()
                .name(deployment.getMetadata().getName())
                .namespace(deployment.getMetadata().getNamespace())
                .labels(deployment.getMetadata().getLabels())
                .annotations(deployment.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(deployment.getMetadata().getCreationTimestamp()));

        if (deployment.getSpec() != null) {
            builder.replicas(deployment.getSpec().getReplicas())
                    .strategy(deployment.getSpec().getStrategy() != null ? deployment.getSpec().getStrategy().getType() : null);

            if (deployment.getSpec().getTemplate() != null
                    && deployment.getSpec().getTemplate().getSpec() != null
                    && deployment.getSpec().getTemplate().getSpec().getContainers() != null
                    && !deployment.getSpec().getTemplate().getSpec().getContainers().isEmpty()) {
                builder.image(deployment.getSpec().getTemplate().getSpec().getContainers().get(0).getImage());
            }
        }

        if (deployment.getStatus() != null) {
            builder.availableReplicas(deployment.getStatus().getAvailableReplicas())
                    .readyReplicas(deployment.getStatus().getReadyReplicas())
                    .updatedReplicas(deployment.getStatus().getUpdatedReplicas())
                    .status(deployment.getStatus().getReadyReplicas() != null
                            && deployment.getSpec() != null
                            && deployment.getSpec().getReplicas() != null
                            && deployment.getStatus().getReadyReplicas().equals(deployment.getSpec().getReplicas())
                            ? "Ready" : "Progressing");
        }

        return builder.build();
    }

    private ServiceInfo toServiceInfo(Service service) {
        ServiceInfo.ServiceInfoBuilder builder = ServiceInfo.builder()
                .name(service.getMetadata().getName())
                .namespace(service.getMetadata().getNamespace())
                .labels(service.getMetadata().getLabels())
                .annotations(service.getMetadata().getAnnotations())
                .creationTimestamp(parseCreationTimestamp(service.getMetadata().getCreationTimestamp()));

        if (service.getSpec() != null) {
            builder.type(service.getSpec().getType())
                    .clusterIP(service.getSpec().getClusterIP())
                    .externalIP(service.getSpec().getExternalIPs() != null ? String.join(",", service.getSpec().getExternalIPs()) : null)
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

    private LocalDateTime parseCreationTimestamp(String timestamp) {
        if (timestamp == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            log.warn("날짜 파싱 실패: {}", timestamp, e);
            return null;
        }
    }
}
