package com.example.demo.k8s.client.strategy;

import io.fabric8.kubernetes.client.KubernetesClient;

public interface KubernetesClientStrategy {
    boolean supports();
    KubernetesClient createClient();
    String name();
}
