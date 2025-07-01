package com.example.demo.api.k8s.service;

import com.example.demo.api.k8s.model.*;
import com.example.demo.api.k8s.port.in.KubernetesQueryPort;
import com.example.demo.api.k8s.port.out.KubernetesRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 쿠버네티스 쿼리 서비스 (헥사고날 아키텍처의 핵심 비즈니스 로직)
 * 인바운드 포트를 구현하고 아웃바운드 포트를 통해 외부 시스템과 통신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesQueryService implements KubernetesQueryPort {
    
    private final KubernetesRepositoryPort kubernetesRepository;
    
    @Override
    public List<PodInfo> getAllPods() {
        log.info("모든 Pod 정보를 조회합니다.");
        return kubernetesRepository.findAllPods();
    }
    
    @Override
    public List<PodInfo> getPodsByNamespace(String namespace) {
        log.info("네임스페이스 {}의 Pod 정보를 조회합니다.", namespace);
        return kubernetesRepository.findPodsByNamespace(namespace);
    }
    
    @Override
    public PodInfo getPodByName(String namespace, String name) {
        log.info("Pod {}/{} 정보를 조회합니다.", namespace, name);
        return kubernetesRepository.findPodByName(namespace, name);
    }
    
    @Override
    public List<NodeInfo> getAllNodes() {
        log.info("모든 Node 정보를 조회합니다.");
        return kubernetesRepository.findAllNodes();
    }
    
    @Override
    public NodeInfo getNodeByName(String name) {
        log.info("Node {} 정보를 조회합니다.", name);
        return kubernetesRepository.findNodeByName(name);
    }
    
    @Override
    public List<DeploymentInfo> getAllDeployments() {
        log.info("모든 Deployment 정보를 조회합니다.");
        return kubernetesRepository.findAllDeployments();
    }
    
    @Override
    public List<DeploymentInfo> getDeploymentsByNamespace(String namespace) {
        log.info("네임스페이스 {}의 Deployment 정보를 조회합니다.", namespace);
        return kubernetesRepository.findDeploymentsByNamespace(namespace);
    }
    
    @Override
    public DeploymentInfo getDeploymentByName(String namespace, String name) {
        log.info("Deployment {}/{} 정보를 조회합니다.", namespace, name);
        return kubernetesRepository.findDeploymentByName(namespace, name);
    }
    
    @Override
    public List<ServiceInfo> getAllServices() {
        log.info("모든 Service 정보를 조회합니다.");
        return kubernetesRepository.findAllServices();
    }
    
    @Override
    public List<ServiceInfo> getServicesByNamespace(String namespace) {
        log.info("네임스페이스 {}의 Service 정보를 조회합니다.", namespace);
        return kubernetesRepository.findServicesByNamespace(namespace);
    }
    
    @Override
    public ServiceInfo getServiceByName(String namespace, String name) {
        log.info("Service {}/{} 정보를 조회합니다.", namespace, name);
        return kubernetesRepository.findServiceByName(namespace, name);
    }
    
    @Override
    public List<String> getAllNamespaces() {
        log.info("모든 Namespace 정보를 조회합니다.");
        return kubernetesRepository.findAllNamespaces();
    }
    
    @Override
    public Map<String, Object> getClusterInfo() {
        log.info("클러스터 정보를 조회합니다.");
        return kubernetesRepository.getClusterInfo();
    }
} 