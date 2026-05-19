package com.hackaton.processor.application.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("DTO Unit Tests")
class DtoTest {

    @Test
    @DisplayName("Should test ProcessDiagramInput builder and getters")
    void testProcessDiagramInput() {
        UUID id = UUID.randomUUID();
        ProcessDiagramInput input = ProcessDiagramInput.builder()
                .diagramId(id)
                .fileName("test.png")
                .contentType("image/png")
                .data("data")
                .build();

        assertEquals(id, input.getDiagramId(), "ID should match");
        assertEquals("test.png", input.getFileName(), "Filename should match");
        assertEquals("image/png", input.getContentType(), "Content type should match");
        assertEquals("data", input.getData(), "Data should match");
    }

    @Test
    @DisplayName("Should test UpdateStatusOutput builder and getters")
    void testUpdateStatusOutput() {
        UpdateStatusOutput output = UpdateStatusOutput.builder()
                .diagramId("id")
                .status("PROCESSED")
                .report(UpdateStatusOutput.ReportDTO.builder()
                        .components(List.of("C"))
                        .build())
                .build();

        assertEquals("id", output.getDiagramId(), "ID should match");
        assertEquals("PROCESSED", output.getStatus(), "Status should match");
        assertNotNull(output.getReport(), "Report should not be null");
        assertEquals("C", output.getReport().getComponents().get(0), "Component should match");
    }
}
