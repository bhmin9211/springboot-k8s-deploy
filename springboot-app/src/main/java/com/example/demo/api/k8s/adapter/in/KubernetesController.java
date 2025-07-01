package com.example.demo.api.k8s.adapter.in;

import com.example.demo.api.k8s.model.*;
import com.example.demo.api.k8s.port.in.KubernetesCommandPort;
import com.example.demo.api.k8s.port.in.KubernetesQueryPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 쿠버네티스 컨트롤러 (헥사고날 아키텍처의 인바운드 어댑터)
 * HTTP 요청을 받아서 포트를 통해 핵심 비즈니스 로직을 호출
 */
@RestController
@RequestMapping("/api/k8s")
@RequiredArgsConstructor
@Slf4j
public class KubernetesController {
    
    private final KubernetesQueryPort kubernetesQueryPort;
    private final KubernetesCommandPort kubernetesCommandPort;
    
    // ========== Pod 관련 API ==========
    
    /**
     * 모든 Pod 조회
     */
    @GetMapping("/pods")
    public ResponseEntity<List<PodInfo>> getAllPods() {
        log.info("모든 Pod 조회 요청");
        List<PodInfo> pods = kubernetesQueryPort.getAllPods();
        return ResponseEntity.ok(pods);
    }
    
    /**
     * 특정 네임스페이스의 Pod 조회
     */
    @GetMapping("/namespaces/{namespace}/pods")
    public ResponseEntity<List<PodInfo>> getPodsByNamespace(@PathVariable String namespace) {
        log.info("네임스페이스 {}의 Pod 조회 요청", namespace);
        List<PodInfo> pods = kubernetesQueryPort.getPodsByNamespace(namespace);
        return ResponseEntity.ok(pods);
    }
    
    /**
     * 특정 Pod 조회
     */
    @GetMapping("/namespaces/{namespace}/pods/{name}")
    public ResponseEntity<PodInfo> getPodByName(@PathVariable String namespace, @PathVariable String name) {
        log.info("Pod {}/{} 조회 요청", namespace, name);
        PodInfo pod = kubernetesQueryPort.getPodByName(namespace, name);
        if (pod != null) {
            return ResponseEntity.ok(pod);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Pod 삭제
     */
    @DeleteMapping("/namespaces/{namespace}/pods/{name}")
    public ResponseEntity<String> deletePod(@PathVariable String namespace, @PathVariable String name) {
        log.info("Pod {}/{} 삭제 요청", namespace, name);
        boolean success = kubernetesCommandPort.deletePod(namespace, name);
        if (success) {
            return ResponseEntity.ok("Pod가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Pod 삭제에 실패했습니다.");
        }
    }
    
    // ========== Node 관련 API ==========
    
    /**
     * 모든 Node 조회
     */
    @GetMapping("/nodes")
    public ResponseEntity<List<NodeInfo>> getAllNodes() {
        log.info("모든 Node 조회 요청");
        List<NodeInfo> nodes = kubernetesQueryPort.getAllNodes();
        return ResponseEntity.ok(nodes);
    }
    
    /**
     * 특정 Node 조회
     */
    @GetMapping("/nodes/{name}")
    public ResponseEntity<NodeInfo> getNodeByName(@PathVariable String name) {
        log.info("Node {} 조회 요청", name);
        NodeInfo node = kubernetesQueryPort.getNodeByName(name);
        if (node != null) {
            return ResponseEntity.ok(node);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // ========== Deployment 관련 API ==========
    
    /**
     * 모든 Deployment 조회
     */
    @GetMapping("/deployments")
    public ResponseEntity<List<DeploymentInfo>> getAllDeployments() {
        log.info("모든 Deployment 조회 요청");
        List<DeploymentInfo> deployments = kubernetesQueryPort.getAllDeployments();
        return ResponseEntity.ok(deployments);
    }
    
    /**
     * 특정 네임스페이스의 Deployment 조회
     */
    @GetMapping("/namespaces/{namespace}/deployments")
    public ResponseEntity<List<DeploymentInfo>> getDeploymentsByNamespace(@PathVariable String namespace) {
        log.info("네임스페이스 {}의 Deployment 조회 요청", namespace);
        List<DeploymentInfo> deployments = kubernetesQueryPort.getDeploymentsByNamespace(namespace);
        return ResponseEntity.ok(deployments);
    }
    
    /**
     * 특정 Deployment 조회
     */
    @GetMapping("/namespaces/{namespace}/deployments/{name}")
    public ResponseEntity<DeploymentInfo> getDeploymentByName(@PathVariable String namespace, @PathVariable String name) {
        log.info("Deployment {}/{} 조회 요청", namespace, name);
        DeploymentInfo deployment = kubernetesQueryPort.getDeploymentByName(namespace, name);
        if (deployment != null) {
            return ResponseEntity.ok(deployment);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Deployment 스케일링
     */
    @PutMapping("/namespaces/{namespace}/deployments/{name}/scale")
    public ResponseEntity<String> scaleDeployment(
            @PathVariable String namespace, 
            @PathVariable String name, 
            @RequestParam int replicas) {
        log.info("Deployment {}/{}를 {}개로 스케일링 요청", namespace, name, replicas);
        boolean success = kubernetesCommandPort.scaleDeployment(namespace, name, replicas);
        if (success) {
            return ResponseEntity.ok("Deployment가 성공적으로 스케일링되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Deployment 스케일링에 실패했습니다.");
        }
    }
    
    // ========== Service 관련 API ==========
    
    /**
     * 모든 Service 조회
     */
    @GetMapping("/services")
    public ResponseEntity<List<ServiceInfo>> getAllServices() {
        log.info("모든 Service 조회 요청");
        List<ServiceInfo> services = kubernetesQueryPort.getAllServices();
        return ResponseEntity.ok(services);
    }
    
    /**
     * 특정 네임스페이스의 Service 조회
     */
    @GetMapping("/namespaces/{namespace}/services")
    public ResponseEntity<List<ServiceInfo>> getServicesByNamespace(@PathVariable String namespace) {
        log.info("네임스페이스 {}의 Service 조회 요청", namespace);
        List<ServiceInfo> services = kubernetesQueryPort.getServicesByNamespace(namespace);
        return ResponseEntity.ok(services);
    }
    
    /**
     * 특정 Service 조회
     */
    @GetMapping("/namespaces/{namespace}/services/{name}")
    public ResponseEntity<ServiceInfo> getServiceByName(@PathVariable String namespace, @PathVariable String name) {
        log.info("Service {}/{} 조회 요청", namespace, name);
        ServiceInfo service = kubernetesQueryPort.getServiceByName(namespace, name);
        if (service != null) {
            return ResponseEntity.ok(service);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Service 삭제
     */
    @DeleteMapping("/namespaces/{namespace}/services/{name}")
    public ResponseEntity<String> deleteService(@PathVariable String namespace, @PathVariable String name) {
        log.info("Service {}/{} 삭제 요청", namespace, name);
        boolean success = kubernetesCommandPort.deleteService(namespace, name);
        if (success) {
            return ResponseEntity.ok("Service가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Service 삭제에 실패했습니다.");
        }
    }
    
    // ========== Namespace 관련 API ==========
    
    /**
     * 모든 Namespace 조회
     */
    @GetMapping("/namespaces")
    public ResponseEntity<List<String>> getAllNamespaces() {
        log.info("모든 Namespace 조회 요청");
        List<String> namespaces = kubernetesQueryPort.getAllNamespaces();
        return ResponseEntity.ok(namespaces);
    }
    
    /**
     * Namespace 생성
     */
    @PostMapping("/namespaces")
    public ResponseEntity<String> createNamespace(@RequestParam String name) {
        log.info("Namespace {} 생성 요청", name);
        boolean success = kubernetesCommandPort.createNamespace(name);
        if (success) {
            return ResponseEntity.ok("Namespace가 성공적으로 생성되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Namespace 생성에 실패했습니다.");
        }
    }
    
    /**
     * Namespace 삭제
     */
    @DeleteMapping("/namespaces/{name}")
    public ResponseEntity<String> deleteNamespace(@PathVariable String name) {
        log.info("Namespace {} 삭제 요청", name);
        boolean success = kubernetesCommandPort.deleteNamespace(name);
        if (success) {
            return ResponseEntity.ok("Namespace가 성공적으로 삭제되었습니다.");
        } else {
            return ResponseEntity.badRequest().body("Namespace 삭제에 실패했습니다.");
        }
    }
    
    // ========== 클러스터 정보 API ==========
    
    /**
     * 클러스터 정보 조회
     */
    @GetMapping("/cluster/info")
    public ResponseEntity<Map<String, Object>> getClusterInfo() {
        log.info("클러스터 정보 조회 요청");
        Map<String, Object> clusterInfo = kubernetesQueryPort.getClusterInfo();
        return ResponseEntity.ok(clusterInfo);
    }
    
    // ========== 헬스체크 API ==========
    
    /**
     * 쿠버네티스 API 연결 상태 확인
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        log.info("쿠버네티스 API 헬스체크 요청");
        try {
            Map<String, Object> clusterInfo = kubernetesQueryPort.getClusterInfo();
            if (clusterInfo.containsKey("apiServer")) {
                return ResponseEntity.ok("쿠버네티스 API 연결 상태: 정상");
            } else {
                return ResponseEntity.status(503).body("쿠버네티스 API 연결 상태: 비정상");
            }
        } catch (Exception e) {
            log.error("쿠버네티스 API 헬스체크 중 오류 발생: ", e);
            return ResponseEntity.status(503).body("쿠버네티스 API 연결 상태: 오류");
        }
    }
} 