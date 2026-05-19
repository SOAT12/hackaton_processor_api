package com.hackaton.processor.infrastructure.ai;

import com.hackaton.processor.domain.entity.DiagramAnalysis;
import com.hackaton.processor.domain.gateway.ImageAnalysisGateway;
import com.hackaton.processor.domain.exception.AiParsingException;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Base64;

@Slf4j
@Component
@RequiredArgsConstructor
public class LangChain4jAdapter implements ImageAnalysisGateway {

    private final ChatLanguageModel chatModel;
    private final ObjectMapper objectMapper;

    private static final String SYSTEM_PROMPT = """
            Você é um Arquiteto de Software Sênior e Especialista em Cloud. Analise a imagem do diagrama de arquitetura anexo.
            Sua resposta não pode conter formatação markdown de código (como ```json) ou texto explicativo inicial. 
            Você DEVE retornar um objeto JSON estrito com exatamente as seguintes chaves:
            1. 'components': um array de strings listando os serviços/componentes identificados na imagem.
            2. 'risks': um array de strings descrevendo vulnerabilidades, pontos únicos de falha ou gargalos.
            3. 'recommendations': um array de strings com sugestões técnicas de melhoria.
            Responda apenas com o JSON.
            """;

    @Override
    public DiagramAnalysis analyze(byte[] imageData) {
        log.info("Enviando imagem para análise via LangChain4j...");

        String base64Image = Base64.getEncoder().encodeToString(imageData);

        SystemMessage systemMessage = SystemMessage.from(SYSTEM_PROMPT);
        
        UserMessage userMessage = UserMessage.from(
                TextContent.from("Analise este diagrama e extraia os dados estruturados."),
                ImageContent.from(base64Image, "image/png")
        );

        AiMessage response = chatModel.generate(systemMessage, userMessage).content();
        
        String content = response.text();
        log.debug("Resposta bruta da IA: {}", content);

        try {
            content = content.replaceAll("```json", "").replaceAll("```", "").trim();
            return objectMapper.readValue(content, DiagramAnalysis.class);
        } catch (Exception e) {
            log.error("Erro ao converter resposta da IA para DiagramAnalysis: {}", content, e);
            throw new AiParsingException("Falha no parsing da resposta da IA", e);
        }
    }
}
