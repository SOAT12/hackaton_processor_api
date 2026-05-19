package com.hackaton.processor.infrastructure.messaging;

import com.hackaton.processor.application.dto.ProcessDiagramInput;
import com.hackaton.processor.application.usecase.AnalyzeDiagramUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SqsDiagramConsumer {

    private final AnalyzeDiagramUseCase analyzeDiagramUseCase;

    @SqsListener(value = "${spring.cloud.aws.sqs.queue-diagram-process}", acknowledgementMode = "ON_SUCCESS")
    public void listen(@Valid ProcessDiagramInput message) {
        log.info("Mensagem consumida da fila diagram-process para o ID: {}", message.getDiagramId());
        try {
            analyzeDiagramUseCase.execute(message);
        } catch (Exception e) {
            log.error("Erro inesperado ao processar diagrama ID {}: {}", message.getDiagramId(), e.getMessage(), e);
        }
    }
}
