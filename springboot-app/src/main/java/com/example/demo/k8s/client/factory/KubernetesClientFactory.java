package com.example.demo.k8s.client.factory;

import com.example.demo.k8s.client.strategy.KubernetesClientStrategy;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class KubernetesClientFactory {

    private final List<KubernetesClientStrategy> strategies;

    public KubernetesClientFactory(List<KubernetesClientStrategy> strategies) {
        this.strategies = strategies;
    }

    public KubernetesClient createClient() {
        return strategies.stream()
                .filter(KubernetesClientStrategy::supports)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("사용 가능한 KubernetesClientStrategy가 없습니다."))
                .createClient();
    }
}
