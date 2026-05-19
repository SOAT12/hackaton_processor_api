package com.hackaton.processor.domain.gateway;

import com.hackaton.processor.domain.entity.DiagramAnalysis;

public interface ImageAnalysisGateway {
    DiagramAnalysis analyze(byte[] imageData);
}
