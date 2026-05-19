package com.hackaton.processor.domain.exception;

public class AiParsingException extends RuntimeException {
    public AiParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
