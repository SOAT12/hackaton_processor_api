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
    @DisplayName("Should test UpdateStatusOutput and ReportDTO fully")
    void testUpdateStatusOutputFull() {
        UpdateStatusOutput.ReportDTO report = UpdateStatusOutput.ReportDTO.builder()
                .components(List.of("C"))
                .risks(List.of("R"))
                .recommendations(List.of("Rec"))
                .build();
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        UpdateStatusOutput output = UpdateStatusOutput.builder()
                .diagramId("id")
                .title("title")
                .status("PROCESSED")
                .report(report)
                .createdAt(now)
                .notes("notes")
                .build();

        assertEquals("id", output.getDiagramId());
        assertEquals("title", output.getTitle());
        assertEquals("PROCESSED", output.getStatus());
        assertEquals(report, output.getReport());
        assertEquals(now, output.getCreatedAt());
        assertEquals("notes", output.getNotes());
        
        // ReportDTO internal coverage
        assertEquals(List.of("C"), report.getComponents());
        assertEquals(List.of("R"), report.getRisks());
        assertEquals(List.of("Rec"), report.getRecommendations());
        
        assertNotNull(report.toString());
        UpdateStatusOutput.ReportDTO report2 = UpdateStatusOutput.ReportDTO.builder()
                .components(List.of("C"))
                .risks(List.of("R"))
                .recommendations(List.of("Rec"))
                .build();
        assertEquals(report, report2);
        assertEquals(report.hashCode(), report2.hashCode());
    }

    @Test
    @DisplayName("Should test equals, hashCode and toString for DTOs and Records")
    void testEqualsHashCodeToString() {
        UUID id = UUID.randomUUID();
        ProcessDiagramInput input1 = ProcessDiagramInput.builder().diagramId(id).build();
        ProcessDiagramInput input2 = ProcessDiagramInput.builder().diagramId(id).build();
        
        assertEquals(input1, input2, "Equals should work");
        assertEquals(input1.hashCode(), input2.hashCode(), "HashCode should work");
        assertNotNull(input1.toString(), "ToString should work");

        com.hackaton.processor.domain.entity.DiagramAnalysis analysis1 = 
            new com.hackaton.processor.domain.entity.DiagramAnalysis(List.of("A"), List.of("B"), List.of("C"));
        com.hackaton.processor.domain.entity.DiagramAnalysis analysis2 = 
            new com.hackaton.processor.domain.entity.DiagramAnalysis(List.of("A"), List.of("B"), List.of("C"));

        assertEquals(analysis1, analysis2, "Record equals should work");
        assertEquals(analysis1.hashCode(), analysis2.hashCode(), "Record hashCode should work");
        assertNotNull(analysis1.toString(), "Record toString should work");
    }
}
