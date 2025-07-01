package com.example.demo.api.k8s.service;

import com.example.demo.api.k8s.port.in.KubernetesCommandPort;
import com.example.demo.api.k8s.port.out.KubernetesRepositoryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 쿠버네티스 명령 서비스 (헥사고날 아키텍처의 핵심 비즈니스 로직)
 * 인바운드 포트를 구현하고 아웃바운드 포트를 통해 외부 시스템과 통신
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KubernetesCommandService implements KubernetesCommandPort {
    
    private final KubernetesRepositoryPort kubernetesRepository;
    
    @Override
    public boolean deletePod(String namespace, String name) {
        log.info("Pod {}/{}를 삭제합니다.", namespace, name);
        return kubernetesRepository.deletePod(namespace, name);
    }
    
    @Override
    public boolean scaleDeployment(String namespace, String name, int replicas) {
        log.info("Deployment {}/{}를 {}개로 스케일링합니다.", namespace, name, replicas);
        return kubernetesRepository.scaleDeployment(namespace, name, replicas);
    }
    
    @Override
    public boolean deleteService(String namespace, String name) {
        log.info("Service {}/{}를 삭제합니다.", namespace, name);
        return kubernetesRepository.deleteService(namespace, name);
    }
    
    @Override
    public boolean createNamespace(String name) {
        log.info("Namespace {}를 생성합니다.", name);
        return kubernetesRepository.createNamespace(name);
    }
    
    @Override
    public boolean deleteNamespace(String name) {
        log.info("Namespace {}를 삭제합니다.", name);
        return kubernetesRepository.deleteNamespace(name);
    }
} 