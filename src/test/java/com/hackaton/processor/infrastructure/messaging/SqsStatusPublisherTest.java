package com.hackaton.processor.infrastructure.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.processor.domain.entity.DiagramAnalysis;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqsStatusPublisher Unit Tests")
class SqsStatusPublisherTest {

    @Mock
    private SqsAsyncClient sqsAsyncClient;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private SqsStatusPublisher sqsStatusPublisher;

    private final String queueUrl = "http://localhost:4566/queue/status-update";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sqsStatusPublisher, "queueUrl", queueUrl);
    }

    @Nested
    @DisplayName("Tests for publishStatus method")
    class PublishStatusTests {

        @Test
        @DisplayName("Should successfully publish PROCESSED status with full report")
        void shouldPublishProcessedStatusWithReport() throws JsonProcessingException {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            DiagramAnalysis analysis = new DiagramAnalysis(
                    List.of("Component A"), 
                    List.of("Risk B"), 
                    List.of("Recommendation C")
            );
            String status = "PROCESSED";
            String notes = "Analysis completed successfully";
            String expectedJson = "{\"diagramId\":\"" + diagramId + "\", \"status\":\"PROCESSED\"}";

            when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

            // Act
            sqsStatusPublisher.publishStatus(diagramId, status, analysis, notes);

            // Assert
            verify(objectMapper, times(1)).writeValueAsString(any());
            verify(sqsAsyncClient, times(1)).sendMessage(argThat((SendMessageRequest request) -> 
                request.queueUrl().equals(queueUrl) && request.messageBody().equals(expectedJson)
            ));
        }

        @Test
        @DisplayName("Should successfully publish ERROR status without report")
        void shouldPublishErrorStatusWithoutReport() throws JsonProcessingException {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            String status = "ERROR";
            String notes = "Failed to process diagram";
            String expectedJson = "{\"diagramId\":\"" + diagramId + "\", \"status\":\"ERROR\"}";

            when(objectMapper.writeValueAsString(any())).thenReturn(expectedJson);

            // Act
            sqsStatusPublisher.publishStatus(diagramId, status, null, notes);

            // Assert
            verify(objectMapper, times(1)).writeValueAsString(any());
            verify(sqsAsyncClient, times(1)).sendMessage(argThat((SendMessageRequest request) -> 
                request.queueUrl().equals(queueUrl) && request.messageBody().equals(expectedJson)
            ));
        }

        @Test
        @DisplayName("Should throw RuntimeException when JSON processing fails")
        void shouldThrowExceptionWhenJsonProcessingFails() throws JsonProcessingException {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            when(objectMapper.writeValueAsString(any())).thenThrow(new MockJsonProcessingException("JSON Error"));

            // Act & Assert
            RuntimeException exception = assertThrows(RuntimeException.class, () -> 
                sqsStatusPublisher.publishStatus(diagramId, "ERROR", null, "Notes")
            , "Should throw RuntimeException on JSON failure");

            assertEquals("Falha na integração com sistema de mensageria", exception.getMessage(), "Exception message should match");
            verify(sqsAsyncClient, never()).sendMessage(any(SendMessageRequest.class));
        }
    }

    // Helper class to mock JsonProcessingException
    private static class MockJsonProcessingException extends JsonProcessingException {
        protected MockJsonProcessingException(String msg) {
            super(msg);
        }
    }
}
