package com.example.demo.k8s.controller;

import com.example.demo.config.app.KubernetesProperties;
import com.example.demo.k8s.dto.DeploymentInfo;
import com.example.demo.k8s.dto.NodeInfo;
import com.example.demo.k8s.dto.PodInfo;
import com.example.demo.k8s.dto.ServiceInfo;
import com.example.demo.k8s.service.KubernetesFacade;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/k8s")
@RequiredArgsConstructor
@Slf4j
public class KubernetesController {

    private final KubernetesFacade kubernetesFacade;
    private final KubernetesProperties kubernetesProperties;

    @GetMapping("/pods")
    public ResponseEntity<List<PodInfo>> getAllPods() {
        return ResponseEntity.ok(kubernetesFacade.getAllPods());
    }

    @GetMapping("/namespaces/{namespace}/pods")
    public ResponseEntity<List<PodInfo>> getPodsByNamespace(@PathVariable String namespace) {
        return ResponseEntity.ok(kubernetesFacade.getPodsByNamespace(namespace));
    }

    @GetMapping("/namespaces/{namespace}/pods/{name}")
    public ResponseEntity<PodInfo> getPodByName(@PathVariable String namespace, @PathVariable String name) {
        PodInfo pod = kubernetesFacade.getPodByName(namespace, name);
        return pod != null ? ResponseEntity.ok(pod) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/namespaces/{namespace}/pods/{name}")
    public ResponseEntity<String> deletePod(@PathVariable String namespace, @PathVariable String name) {
        ResponseEntity<String> blocked = commandBlockedResponse();
        if (blocked != null) {
            return blocked;
        }
        return kubernetesFacade.deletePod(namespace, name)
                ? ResponseEntity.ok("Pod가 성공적으로 삭제되었습니다.")
                : ResponseEntity.badRequest().body("Pod 삭제에 실패했습니다.");
    }

    @GetMapping("/nodes")
    public ResponseEntity<List<NodeInfo>> getAllNodes() {
        return ResponseEntity.ok(kubernetesFacade.getAllNodes());
    }

    @GetMapping("/nodes/{name}")
    public ResponseEntity<NodeInfo> getNodeByName(@PathVariable String name) {
        NodeInfo node = kubernetesFacade.getNodeByName(name);
        return node != null ? ResponseEntity.ok(node) : ResponseEntity.notFound().build();
    }

    @GetMapping("/deployments")
    public ResponseEntity<List<DeploymentInfo>> getAllDeployments() {
        return ResponseEntity.ok(kubernetesFacade.getAllDeployments());
    }

    @GetMapping("/namespaces/{namespace}/deployments")
    public ResponseEntity<List<DeploymentInfo>> getDeploymentsByNamespace(@PathVariable String namespace) {
        return ResponseEntity.ok(kubernetesFacade.getDeploymentsByNamespace(namespace));
    }

    @GetMapping("/namespaces/{namespace}/deployments/{name}")
    public ResponseEntity<DeploymentInfo> getDeploymentByName(@PathVariable String namespace, @PathVariable String name) {
        DeploymentInfo deployment = kubernetesFacade.getDeploymentByName(namespace, name);
        return deployment != null ? ResponseEntity.ok(deployment) : ResponseEntity.notFound().build();
    }

    @PutMapping("/namespaces/{namespace}/deployments/{name}/scale")
    public ResponseEntity<String> scaleDeployment(@PathVariable String namespace,
                                                  @PathVariable String name,
                                                  @RequestParam int replicas) {
        ResponseEntity<String> blocked = commandBlockedResponse();
        if (blocked != null) {
            return blocked;
        }
        return kubernetesFacade.scaleDeployment(namespace, name, replicas)
                ? ResponseEntity.ok("Deployment가 성공적으로 스케일링되었습니다.")
                : ResponseEntity.badRequest().body("Deployment 스케일링에 실패했습니다.");
    }

    @GetMapping("/services")
    public ResponseEntity<List<ServiceInfo>> getAllServices() {
        return ResponseEntity.ok(kubernetesFacade.getAllServices());
    }

    @GetMapping("/namespaces/{namespace}/services")
    public ResponseEntity<List<ServiceInfo>> getServicesByNamespace(@PathVariable String namespace) {
        return ResponseEntity.ok(kubernetesFacade.getServicesByNamespace(namespace));
    }

    @GetMapping("/namespaces/{namespace}/services/{name}")
    public ResponseEntity<ServiceInfo> getServiceByName(@PathVariable String namespace, @PathVariable String name) {
        ServiceInfo service = kubernetesFacade.getServiceByName(namespace, name);
        return service != null ? ResponseEntity.ok(service) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/namespaces/{namespace}/services/{name}")
    public ResponseEntity<String> deleteService(@PathVariable String namespace, @PathVariable String name) {
        ResponseEntity<String> blocked = commandBlockedResponse();
        if (blocked != null) {
            return blocked;
        }
        return kubernetesFacade.deleteService(namespace, name)
                ? ResponseEntity.ok("Service가 성공적으로 삭제되었습니다.")
                : ResponseEntity.badRequest().body("Service 삭제에 실패했습니다.");
    }

    @GetMapping("/namespaces")
    public ResponseEntity<List<String>> getAllNamespaces() {
        return ResponseEntity.ok(kubernetesFacade.getAllNamespaces());
    }

    @PostMapping("/namespaces")
    public ResponseEntity<String> createNamespace(@RequestParam String name) {
        ResponseEntity<String> blocked = commandBlockedResponse();
        if (blocked != null) {
            return blocked;
        }
        return kubernetesFacade.createNamespace(name)
                ? ResponseEntity.ok("Namespace가 성공적으로 생성되었습니다.")
                : ResponseEntity.badRequest().body("Namespace 생성에 실패했습니다.");
    }

    @DeleteMapping("/namespaces/{name}")
    public ResponseEntity<String> deleteNamespace(@PathVariable String name) {
        ResponseEntity<String> blocked = commandBlockedResponse();
        if (blocked != null) {
            return blocked;
        }
        return kubernetesFacade.deleteNamespace(name)
                ? ResponseEntity.ok("Namespace가 성공적으로 삭제되었습니다.")
                : ResponseEntity.badRequest().body("Namespace 삭제에 실패했습니다.");
    }

    @GetMapping("/cluster/info")
    public ResponseEntity<Map<String, Object>> getClusterInfo() {
        return ResponseEntity.ok(kubernetesFacade.getClusterInfo());
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        try {
            Map<String, Object> clusterInfo = kubernetesFacade.getClusterInfo();
            return clusterInfo.containsKey("apiServer")
                    ? ResponseEntity.ok("쿠버네티스 API 연결 상태: 정상")
                    : ResponseEntity.status(503).body("쿠버네티스 API 연결 상태: 비정상");
        } catch (Exception e) {
            log.error("쿠버네티스 API 헬스체크 중 오류 발생", e);
            return ResponseEntity.status(503).body("쿠버네티스 API 연결 상태: 오류");
        }
    }

    private ResponseEntity<String> commandBlockedResponse() {
        if (kubernetesProperties.isCommandsEnabled()) {
            return null;
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("현재 배포 환경에서는 Kubernetes 명령 기능이 비활성화되어 있습니다.");
    }
}
