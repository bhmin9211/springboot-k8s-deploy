package com.example.demo.k8s.service;

import com.example.demo.k8s.client.KubernetesGateway;
import com.example.demo.k8s.dto.DeploymentInfo;
import com.example.demo.k8s.dto.NodeInfo;
import com.example.demo.k8s.dto.PodInfo;
import com.example.demo.k8s.dto.ServiceInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesFacade {

    private final KubernetesGateway kubernetesGateway;

    public List<PodInfo> getAllPods() {
        log.info("모든 Pod 정보를 조회합니다.");
        return kubernetesGateway.findAllPods();
    }

    public List<PodInfo> getPodsByNamespace(String namespace) {
        return kubernetesGateway.findPodsByNamespace(namespace);
    }

    public PodInfo getPodByName(String namespace, String name) {
        return kubernetesGateway.findPodByName(namespace, name);
    }

    public boolean deletePod(String namespace, String name) {
        return kubernetesGateway.deletePod(namespace, name);
    }

    public List<NodeInfo> getAllNodes() {
        return kubernetesGateway.findAllNodes();
    }

    public NodeInfo getNodeByName(String name) {
        return kubernetesGateway.findNodeByName(name);
    }

    public List<DeploymentInfo> getAllDeployments() {
        return kubernetesGateway.findAllDeployments();
    }

    public List<DeploymentInfo> getDeploymentsByNamespace(String namespace) {
        return kubernetesGateway.findDeploymentsByNamespace(namespace);
    }

    public DeploymentInfo getDeploymentByName(String namespace, String name) {
        return kubernetesGateway.findDeploymentByName(namespace, name);
    }

    public boolean scaleDeployment(String namespace, String name, int replicas) {
        return kubernetesGateway.scaleDeployment(namespace, name, replicas);
    }

    public List<ServiceInfo> getAllServices() {
        return kubernetesGateway.findAllServices();
    }

    public List<ServiceInfo> getServicesByNamespace(String namespace) {
        return kubernetesGateway.findServicesByNamespace(namespace);
    }

    public ServiceInfo getServiceByName(String namespace, String name) {
        return kubernetesGateway.findServiceByName(namespace, name);
    }

    public boolean deleteService(String namespace, String name) {
        return kubernetesGateway.deleteService(namespace, name);
    }

    public List<String> getAllNamespaces() {
        return kubernetesGateway.findAllNamespaces();
    }

    public boolean createNamespace(String name) {
        return kubernetesGateway.createNamespace(name);
    }

    public boolean deleteNamespace(String name) {
        return kubernetesGateway.deleteNamespace(name);
    }

    public Map<String, Object> getClusterInfo() {
        return kubernetesGateway.getClusterInfo();
    }
}
