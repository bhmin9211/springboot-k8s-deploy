package com.example.demo.api.k8s.port.in;

import com.example.demo.api.k8s.model.*;

import java.util.List;
import java.util.Map;

/**
 * 쿠버네티스 조회를 위한 인바운드 포트 (헥사고날 아키텍처)
 * 애플리케이션의 핵심 비즈니스 로직을 정의
 */
public interface KubernetesQueryPort {
    
    // Pod 관련 쿼리
    List<PodInfo> getAllPods();
    List<PodInfo> getPodsByNamespace(String namespace);
    PodInfo getPodByName(String namespace, String name);
    
    // Node 관련 쿼리
    List<NodeInfo> getAllNodes();
    NodeInfo getNodeByName(String name);
    
    // Deployment 관련 쿼리
    List<DeploymentInfo> getAllDeployments();
    List<DeploymentInfo> getDeploymentsByNamespace(String namespace);
    DeploymentInfo getDeploymentByName(String namespace, String name);
    
    // Service 관련 쿼리
    List<ServiceInfo> getAllServices();
    List<ServiceInfo> getServicesByNamespace(String namespace);
    ServiceInfo getServiceByName(String namespace, String name);
    
    // Namespace 관련 쿼리
    List<String> getAllNamespaces();
    
    // 클러스터 정보 쿼리
    Map<String, Object> getClusterInfo();
} 