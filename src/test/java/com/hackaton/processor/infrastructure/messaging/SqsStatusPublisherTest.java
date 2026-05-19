package com.hackaton.processor.infrastructure.messaging;

import com.hackaton.processor.domain.entity.DiagramAnalysis;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("SqsStatusPublisher Unit Tests")
class SqsStatusPublisherTest {

    @Mock
    private SqsTemplate sqsTemplate;

    @InjectMocks
    private SqsStatusPublisher sqsStatusPublisher;

    private final String queueUrl = "http://localhost:4566/queue/status-update";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(sqsStatusPublisher, "queueUrl", queueUrl);
    }

    @Nested
    @DisplayName("PublishStatus Method Scenarios")
    class PublishStatusScenarios {

        @Test
        @DisplayName("Should successfully publish PROCESSED status with report")
        void shouldPublishProcessedStatus() {
            // Arrange
            UUID diagramId = UUID.randomUUID();
            DiagramAnalysis analysis = new DiagramAnalysis(
                    List.of("Comp"), List.of("Risk"), List.of("Rec")
            );

            // Act
            sqsStatusPublisher.publishStatus(diagramId, "PROCESSED", analysis, "Success notes");

            // Assert
            verify(sqsTemplate, times(1)).send(eq(queueUrl), any(Object.class));
        }

        @Test
        @DisplayName("Should successfully publish ERROR status without report")
        void shouldPublishErrorStatus() {
            // Arrange
            UUID diagramId = UUID.randomUUID();

            // Act
            sqsStatusPublisher.publishStatus(diagramId, "ERROR", null, "Error notes");

            // Assert
            verify(sqsTemplate, times(1)).send(eq(queueUrl), any(Object.class));
        }
    }
}
