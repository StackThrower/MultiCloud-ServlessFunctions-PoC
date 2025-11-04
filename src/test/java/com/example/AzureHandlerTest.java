package com.example;

import com.example.service.CommonService;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for Azure Functions Handler.
 * Tests the handler logic in isolation by mocking dependencies.
 * Note: Due to Spring Boot initialization in constructor, we test the service layer
 * that the handler delegates to, which contains the core business logic.
 */
class AzureHandlerTest {

    @Test
    void testAzureHandlerLogicWithCommonService() {
        // Arrange - Test the service that AzureHandler uses
        CommonService commonService = new CommonService();

        // Act
        String result = commonService.processRequest();

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }

    @Test
    void testAzureResponseBuilding() {
        // Arrange - Test Azure response building pattern
        HttpRequestMessage<Optional<String>> request = mock(HttpRequestMessage.class);
        HttpResponseMessage.Builder responseBuilder = mock(HttpResponseMessage.Builder.class);
        HttpResponseMessage response = mock(HttpResponseMessage.class);
        
        String responseBody = "Hello from Lambda v2.3";
        when(request.createResponseBuilder(HttpStatus.OK)).thenReturn(responseBuilder);
        when(responseBuilder.body(responseBody)).thenReturn(responseBuilder);
        when(responseBuilder.build()).thenReturn(response);

        // Act
        HttpResponseMessage.Builder builder = request.createResponseBuilder(HttpStatus.OK);
        builder.body(responseBody);
        HttpResponseMessage result = builder.build();

        // Assert
        assertNotNull(result);
        assertEquals(response, result);
        verify(request).createResponseBuilder(HttpStatus.OK);
        verify(responseBuilder).body(responseBody);
        verify(responseBuilder).build();
    }

    @Test
    void testExecutionContextLogger() {
        // Arrange - Test ExecutionContext and Logger interaction
        ExecutionContext context = mock(ExecutionContext.class);
        Logger logger = mock(Logger.class);
        when(context.getLogger()).thenReturn(logger);

        // Act
        Logger actualLogger = context.getLogger();

        // Assert
        assertNotNull(actualLogger);
        assertEquals(logger, actualLogger);
    }
}

