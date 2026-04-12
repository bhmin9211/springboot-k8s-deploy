package com.example.demo.k8s.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeploymentInfo {
    private String name;
    private String namespace;
    private String status;
    private Integer replicas;
    private Integer availableReplicas;
    private Integer readyReplicas;
    private Integer updatedReplicas;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String strategy;
    private String image;
    private String selector;
}
