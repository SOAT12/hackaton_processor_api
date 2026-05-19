package com.hackaton.processor.domain.entity;

import java.util.List;

public record DiagramAnalysis(
    List<String> components,
    List<String> risks,
    List<String> recommendations
) {}
