package com.hackaton.processor.domain.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.hackaton.processor.domain.entity.DiagramAnalysis;
import java.util.UUID;

public interface MessagePublisherGateway {
    void publishStatus(UUID diagramId, String status, DiagramAnalysis analysis, String notes) ;
}
