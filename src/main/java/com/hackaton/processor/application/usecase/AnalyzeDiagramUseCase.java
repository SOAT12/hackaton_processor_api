package com.hackaton.processor.application.usecase;

import com.hackaton.processor.application.dto.ProcessDiagramInput;
import com.hackaton.processor.domain.gateway.ImageAnalysisGateway;
import com.hackaton.processor.domain.gateway.MessagePublisherGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyzeDiagramUseCase {

    private final ImageAnalysisGateway imageAnalysisGateway;
    private final MessagePublisherGateway messagePublisherGateway;

    public void execute(ProcessDiagramInput input) {
        log.info("Iniciando processamento do diagrama ID: {}", input.getDiagramId());

        try {
            byte[] imageBytes = Base64.getDecoder().decode(input.getData());

            var analysis = imageAnalysisGateway.analyze(imageBytes);

            if (analysis.components() == null || analysis.components().isEmpty()) {
                log.warn("IA não identificou componentes no diagrama ID: {}", input.getDiagramId());
                messagePublisherGateway.publishStatus(
                        input.getDiagramId(),
                        "FAILED",
                        null,
                        "Não foi possível identificar componentes no diagrama. Certifique-se de que a imagem é um diagrama de arquitetura legível."
                );
                return;
            }

            messagePublisherGateway.publishStatus(
                    input.getDiagramId(),
                    "PROCESSED",
                    analysis,
                    "Análise de IA concluída com sucesso."
            );

        } catch (Exception e) {
            log.error("Erro ao processar diagrama ID: {}", input.getDiagramId(), e);

            messagePublisherGateway.publishStatus(
                    input.getDiagramId(),
                    "FAILED",
                    null,
                    "Erro no processamento de IA: " + e.getMessage()
            );
        }

    }
}
