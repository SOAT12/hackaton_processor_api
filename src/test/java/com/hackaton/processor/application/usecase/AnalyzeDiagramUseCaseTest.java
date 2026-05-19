package com.hackaton.processor.application.usecase;

import com.hackaton.processor.application.dto.ProcessDiagramInput;
import com.hackaton.processor.domain.entity.DiagramAnalysis;
import com.hackaton.processor.domain.gateway.ImageAnalysisGateway;
import com.hackaton.processor.domain.gateway.MessagePublisherGateway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyzeDiagramUseCase Unit Tests")
class AnalyzeDiagramUseCaseTest {

    @Mock
    private ImageAnalysisGateway imageAnalysisGateway;

    @Mock
    private MessagePublisherGateway messagePublisherGateway;

    @InjectMocks
    private AnalyzeDiagramUseCase analyzeDiagramUseCase;

    @Nested
    @DisplayName("Execute Method Scenarios")
    class ExecuteScenarios {

        @Test
        @DisplayName("Should successfully analyze diagram and publish processed status")
        void shouldAnalyzeAndPublishSuccess() {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            ProcessDiagramInput input = ProcessDiagramInput.builder()
                    .diagramId(diagramId)
                    .data("YmFzZTY0ZGF0YQ==") // "base64data"
                    .build();
            DiagramAnalysis analysis = new DiagramAnalysis(
                    List.of("Component 1"),
                    List.of("Risk 1"),
                    List.of("Recommendation 1")
            );

            when(imageAnalysisGateway.analyze(any(byte[].class))).thenReturn(analysis);

            // Act
            analyzeDiagramUseCase.execute(input);

            // Assert
            verify(imageAnalysisGateway, times(1)).analyze(any(byte[].class));
            verify(messagePublisherGateway, times(1)).publishStatus(
                    eq(diagramId),
                    eq("PROCESSED"),
                    eq(analysis),
                    contains("sucesso")
            );
        }

        @Test
        @DisplayName("Should publish error status when IA returns no components")
        void shouldPublishErrorWhenNoComponents() {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            ProcessDiagramInput input = ProcessDiagramInput.builder()
                    .diagramId(diagramId)
                    .data("YmFzZTY0ZGF0YQ==")
                    .build();
            DiagramAnalysis analysis = new DiagramAnalysis(List.of(), List.of(), List.of());

            when(imageAnalysisGateway.analyze(any(byte[].class))).thenReturn(analysis);

            // Act
            analyzeDiagramUseCase.execute(input);

            // Assert
            verify(messagePublisherGateway, times(1)).publishStatus(
                    eq(diagramId),
                    eq("ERROR"),
                    isNull(),
                    contains("Não foi possível identificar componentes")
            );
        }

        @Test
        @DisplayName("Should publish error status when IA returns null components list")
        void shouldPublishErrorWhenComponentsIsNull() {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            ProcessDiagramInput input = ProcessDiagramInput.builder()
                    .diagramId(diagramId)
                    .data("YmFzZTY0ZGF0YQ==")
                    .build();
            DiagramAnalysis analysis = new DiagramAnalysis(null, List.of(), List.of());

            when(imageAnalysisGateway.analyze(any(byte[].class))).thenReturn(analysis);

            // Act
            analyzeDiagramUseCase.execute(input);

            // Assert
            verify(messagePublisherGateway, times(1)).publishStatus(
                    eq(diagramId),
                    eq("ERROR"),
                    isNull(),
                    contains("Não foi possível identificar componentes")
            );
        }

        @Test
        @DisplayName("Should publish error status when an exception occurs")
        void shouldPublishErrorOnException() {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            ProcessDiagramInput input = ProcessDiagramInput.builder()
                    .diagramId(diagramId)
                    .data("YmFzZTY0ZGF0YQ==")
                    .build();

            when(imageAnalysisGateway.analyze(any(byte[].class))).thenThrow(new RuntimeException("IA Failure"));

            // Act
            analyzeDiagramUseCase.execute(input);

            // Assert
            verify(messagePublisherGateway, times(1)).publishStatus(
                    eq(diagramId),
                    eq("ERROR"),
                    isNull(),
                    contains("Erro no processamento de IA: IA Failure")
            );
        }
    }
}
