package com.example.demo.api.k8s.port.in;

/**
 * 쿠버네티스 명령을 위한 인바운드 포트 (헥사고날 아키텍처)
 * 애플리케이션의 핵심 비즈니스 로직을 정의
 */
public interface KubernetesCommandPort {
    
    // Pod 관련 명령
    boolean deletePod(String namespace, String name);
    
    // Deployment 관련 명령
    boolean scaleDeployment(String namespace, String name, int replicas);
    
    // Service 관련 명령
    boolean deleteService(String namespace, String name);
    
    // Namespace 관련 명령
    boolean createNamespace(String name);
    boolean deleteNamespace(String name);
} 