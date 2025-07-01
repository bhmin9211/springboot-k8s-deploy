package com.example.demo.api.k8s.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NodeInfo {
    private String name;
    private String status;
    private String role;
    private String version;
    private String internalIP;
    private String externalIP;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String architecture;
    private String operatingSystem;
    private String kernelVersion;
    private String containerRuntime;
    private String kubeletVersion;
    private String kubeProxyVersion;
} 