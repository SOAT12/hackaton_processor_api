package com.hackaton.processor.application.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UpdateStatusOutput {
    private String diagramId;
    private String titulo;
    private String status;
    private ReportDTO report;
    private LocalDateTime createdAt;
    private String notes;

    @Data
    @Builder
    public static class ReportDTO {
        private List<String> components;
        private List<String> risks;
        private List<String> recommendations;
    }
}
