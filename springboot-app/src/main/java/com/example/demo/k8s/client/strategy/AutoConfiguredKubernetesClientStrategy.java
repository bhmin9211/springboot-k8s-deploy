package com.example.demo.k8s.client.strategy;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import org.springframework.stereotype.Component;

@Component
public class AutoConfiguredKubernetesClientStrategy implements KubernetesClientStrategy {

    @Override
    public boolean supports() {
        return true;
    }

    @Override
    public KubernetesClient createClient() {
        return new KubernetesClientBuilder().build();
    }

    @Override
    public String name() {
        return "auto-configured";
    }
}
