package com.hackaton.processor.domain.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("AiParsingException Unit Tests")
class AiParsingExceptionTest {

    @Test
    @DisplayName("Should create exception with message and cause")
    void shouldCreateException() {
        // Arrange
        String message = "test message";
        Throwable cause = new RuntimeException("cause");

        // Act
        AiParsingException exception = new AiParsingException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage(), "Message should match");
        assertEquals(cause, exception.getCause(), "Cause should match");
    }
}
