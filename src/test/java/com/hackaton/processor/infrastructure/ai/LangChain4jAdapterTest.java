package com.hackaton.processor.infrastructure.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hackaton.processor.domain.entity.DiagramAnalysis;
import com.hackaton.processor.domain.exception.AiParsingException;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("LangChain4jAdapter Unit Tests")
class LangChain4jAdapterTest {

    @Mock
    private ChatLanguageModel chatModel;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private LangChain4jAdapter langChain4jAdapter;

    @Nested
    @DisplayName("Analyze Method Scenarios")
    class AnalyzeScenarios {

        @Test
        @DisplayName("Should successfully analyze image and return DiagramAnalysis")
        void shouldAnalyzeSuccessfully() throws Exception {
            // Arrange
            byte[] imageData = "image-data".getBytes();
            String jsonResponse = "{\"components\":[\"S3\"], \"risks\":[\"Open\"], \"recommendations\":[\"Close\"]}";
            
            Response<AiMessage> response = Response.from(AiMessage.from(jsonResponse));
            when(chatModel.generate(any(), any(UserMessage.class))).thenReturn(response);

            // Act
            DiagramAnalysis result = langChain4jAdapter.analyze(imageData);

            // Assert
            assertNotNull(result, "Result should not be null");
            assertEquals(1, result.components().size(), "Should have 1 component");
            assertEquals("S3", result.components().get(0), "Component should be S3");
        }

        @Test
        @DisplayName("Should handle markdown in AI response and parse correctly")
        void shouldHandleMarkdownInResponse() {
            // Arrange
            byte[] imageData = "image-data".getBytes();
            String jsonResponse = "```json\n{\"components\":[\"EC2\"], \"risks\":[], \"recommendations\":[]}\n```";
            
            Response<AiMessage> response = Response.from(AiMessage.from(jsonResponse));
            when(chatModel.generate(any(), any(UserMessage.class))).thenReturn(response);

            // Act
            DiagramAnalysis result = langChain4jAdapter.analyze(imageData);

            // Assert
            assertNotNull(result, "Result should not be null after cleaning markdown");
            assertEquals("EC2", result.components().get(0), "Should parse EC2 correctly");
        }

        @Test
        @DisplayName("Should throw AiParsingException when JSON is invalid")
        void shouldThrowAiParsingException() {
            // Arrange
            byte[] imageData = "image-data".getBytes();
            String invalidJson = "invalid-json";
            
            Response<AiMessage> response = Response.from(AiMessage.from(invalidJson));
            when(chatModel.generate(any(), any(UserMessage.class))).thenReturn(response);

            // Act & Assert
            assertThrows(AiParsingException.class, () -> langChain4jAdapter.analyze(imageData), 
                    "Should throw AiParsingException for malformed JSON");
        }
    }
}
