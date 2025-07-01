package com.example.demo.api.k8s.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceInfo {
    private String name;
    private String namespace;
    private String type;
    private String clusterIP;
    private String externalIP;
    private List<String> ports;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String selector;
    private String sessionAffinity;
    private String loadBalancerIP;
} 