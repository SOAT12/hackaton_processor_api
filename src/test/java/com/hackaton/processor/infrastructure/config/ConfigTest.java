package com.hackaton.processor.infrastructure.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import dev.langchain4j.model.chat.ChatLanguageModel;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import io.awspring.cloud.sqs.operations.SqsTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Configuration Smoke Tests")
class ConfigTest {

    @Autowired
    private ChatLanguageModel chatLanguageModel;

    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Autowired
    private SqsTemplate sqsTemplate;

    @Test
    @DisplayName("Should verify if AI beans are correctly loaded")
    void aiBeansLoaded() {
        assertNotNull(chatLanguageModel, "ChatLanguageModel bean should be loaded");
    }

    @Test
    @DisplayName("Should verify if SQS beans are correctly loaded")
    void sqsBeansLoaded() {
        assertNotNull(sqsAsyncClient, "SqsAsyncClient bean should be loaded");
        assertNotNull(sqsTemplate, "SqsTemplate bean should be loaded");
    }
}

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.cloud.aws.credentials.session-token=" // Empty token to test else branch
})
@DisplayName("SQS Config Without Token Test")
class SqsConfigWithoutTokenTest {
    @Autowired
    private SqsAsyncClient sqsAsyncClient;

    @Test
    @DisplayName("Should load SqsAsyncClient without session token")
    void sqsBeansLoadedWithoutToken() {
        assertNotNull(sqsAsyncClient, "SqsAsyncClient bean should be loaded even without token");
    }
}
