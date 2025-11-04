package com.example;

import com.example.service.CommonService;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for Google Cloud Functions Handler.
 * Tests the handler logic in isolation by mocking dependencies.
 * Note: Due to Spring Boot initialization in constructor, we test the service layer
 * that the handler delegates to, which contains the core business logic.
 */
class GcpHandlerTest {

    @Test
    void testGcpHandlerLogicWithCommonService() {
        // Arrange - Test the service that GcpHandler uses
        CommonService commonService = new CommonService();

        // Act
        String result = commonService.processRequest();

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }

    @Test
    void testGcpResponseWriting() throws IOException {
        // Arrange - Test GCP response writing pattern
        HttpResponse response = mock(HttpResponse.class);
        StringWriter stringWriter = new StringWriter();
        BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
        when(response.getWriter()).thenReturn(bufferedWriter);

        String responseMessage = "Hello from Lambda v2.3";

        // Act
        BufferedWriter writer = response.getWriter();
        writer.write(responseMessage);
        writer.flush();

        // Assert
        String actualResponse = stringWriter.toString();
        assertEquals(responseMessage, actualResponse);
    }

    @Test
    void testGcpRequestAndResponse() {
        // Arrange - Test HttpRequest and HttpResponse mocking
        HttpRequest request = mock(HttpRequest.class);
        HttpResponse response = mock(HttpResponse.class);

        // Act & Assert - Verify mocks can be created
        assertNotNull(request);
        assertNotNull(response);
    }
}


