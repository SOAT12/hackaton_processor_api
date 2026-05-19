package com.hackaton.processor.infrastructure.messaging;

import com.hackaton.processor.application.dto.UpdateStatusOutput;
import com.hackaton.processor.domain.entity.DiagramAnalysis;
import com.hackaton.processor.domain.gateway.MessagePublisherGateway;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsStatusPublisher implements MessagePublisherGateway {

    private final SqsTemplate sqsTemplate;

    @Value("${spring.cloud.aws.sqs.queue-diagram-status-update}")
    private String queueUrl;

    @Override
    public void publishStatus(UUID diagramId, String status, DiagramAnalysis analysis, String notes) {
        log.info("Publicando status {} para o diagrama ID: {}", status, diagramId);

        UpdateStatusOutput.UpdateStatusOutputBuilder builder = UpdateStatusOutput.builder()
                .diagramId(diagramId.toString())
                .titulo("Análise de Arquitetura Gerada")
                .status(status)
                .createdAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .notes(notes);

        if (analysis != null) {
            builder.report(UpdateStatusOutput.ReportDTO.builder()
                    .components(analysis.components())
                    .risks(analysis.risks())
                    .recommendations(analysis.recommendations())
                    .build());
        }

        sqsTemplate.send(queueUrl, builder.build());
    }
}
