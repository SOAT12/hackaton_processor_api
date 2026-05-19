package com.hackaton.processor.infrastructure.messaging;

import com.hackaton.processor.application.dto.ProcessDiagramInput;
import com.hackaton.processor.application.usecase.AnalyzeDiagramUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqsDiagramConsumer Unit Tests")
class SqsDiagramConsumerTest {

    @Mock
    private AnalyzeDiagramUseCase analyzeDiagramUseCase;

    @InjectMocks
    private SqsDiagramConsumer sqsDiagramConsumer;

    @Test
    @DisplayName("Should call usecase when a message is received")
    void shouldCallUseCaseOnMessage() {
        // Arrange
        ProcessDiagramInput message = ProcessDiagramInput.builder()
                .diagramId(UUID.randomUUID())
                .data("base64")
                .build();

        // Act
        sqsDiagramConsumer.listen(message);

        // Assert
        verify(analyzeDiagramUseCase, times(1)).execute(message);
    }
}
