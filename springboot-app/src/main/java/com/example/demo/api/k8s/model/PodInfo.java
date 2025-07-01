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
public class PodInfo {
    private String name;
    private String namespace;
    private String status;
    private String podIP;
    private String nodeName;
    private LocalDateTime creationTimestamp;
    private Map<String, String> labels;
    private Map<String, String> annotations;
    private String image;
    private String restartCount;
    private String ready;
    private String phase;
} 