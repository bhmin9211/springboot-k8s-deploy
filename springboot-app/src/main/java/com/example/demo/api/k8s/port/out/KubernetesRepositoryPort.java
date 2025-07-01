package com.example.demo.api.k8s.port.out;

import com.example.demo.api.k8s.model.*;

import java.util.List;
import java.util.Map;

/**
 * 쿠버네티스 저장소를 위한 아웃바운드 포트 (헥사고날 아키텍처)
 * 외부 시스템과의 인터페이스를 정의
 */
public interface KubernetesRepositoryPort {
    
    // Pod 관련 저장소 작업
    List<PodInfo> findAllPods();
    List<PodInfo> findPodsByNamespace(String namespace);
    PodInfo findPodByName(String namespace, String name);
    boolean deletePod(String namespace, String name);
    
    // Node 관련 저장소 작업
    List<NodeInfo> findAllNodes();
    NodeInfo findNodeByName(String name);
    
    // Deployment 관련 저장소 작업
    List<DeploymentInfo> findAllDeployments();
    List<DeploymentInfo> findDeploymentsByNamespace(String namespace);
    DeploymentInfo findDeploymentByName(String namespace, String name);
    boolean scaleDeployment(String namespace, String name, int replicas);
    
    // Service 관련 저장소 작업
    List<ServiceInfo> findAllServices();
    List<ServiceInfo> findServicesByNamespace(String namespace);
    ServiceInfo findServiceByName(String namespace, String name);
    boolean deleteService(String namespace, String name);
    
    // Namespace 관련 저장소 작업
    List<String> findAllNamespaces();
    boolean createNamespace(String name);
    boolean deleteNamespace(String name);
    
    // 클러스터 정보 저장소 작업
    Map<String, Object> getClusterInfo();
} 