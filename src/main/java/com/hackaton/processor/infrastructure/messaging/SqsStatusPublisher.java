package com.hackaton.processor.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.processor.application.dto.UpdateStatusOutput;
import com.hackaton.processor.domain.entity.DiagramAnalysis;
import com.hackaton.processor.domain.gateway.MessagePublisherGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsStatusPublisher implements MessagePublisherGateway {

    private final SqsAsyncClient sqsAsyncClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.cloud.aws.sqs.queue-diagram-status-update}")
    private String queueUrl;

    @Override
    public void publishStatus(UUID diagramId, String status, DiagramAnalysis analysis, String notes) {
        log.info("Publicando status {} para o diagrama ID: {}", status, diagramId);

        UpdateStatusOutput.UpdateStatusOutputBuilder builder = UpdateStatusOutput.builder()
                .diagramId(diagramId.toString())
                .title("Análise de Arquitetura Gerada")
                .status(status)
                .createdAt(LocalDateTime.now())
                .notes(notes);

        if (analysis != null) {
            builder.report(UpdateStatusOutput.ReportDTO.builder()
                    .components(analysis.components())
                    .risks(analysis.risks())
                    .recommendations(analysis.recommendations())
                    .build());
        }

        try{
            String messageBody = objectMapper.writeValueAsString(builder.build());

            sqsAsyncClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(messageBody)
                    .build());
        } catch (JsonProcessingException e) {
            log.error("Erro ao enviar mensagem para o SQS, diagrama ID: {}", diagramId, e);
            throw new RuntimeException("Falha na integração com sistema de mensageria", e);
        }

    }
}
