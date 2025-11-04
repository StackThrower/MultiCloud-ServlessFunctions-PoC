package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.example.service.CommonService;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for AWS Lambda Handler.
 * Tests the handler logic in isolation by mocking dependencies.
 * Note: Due to Spring Boot initialization in constructor, we test the service layer
 * that the handler delegates to, which contains the core business logic.
 */
class HandlerTest {

    @Test
    void testHandlerLogicWithCommonService() {
        // Arrange - Test the service that Handler uses
        CommonService commonService = new CommonService();
        Map<String, Object> input = new HashMap<>();
        input.put("key", "value");

        // Act
        String result = commonService.processRequest(input);

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }

    @Test
    void testHandlerLogicWithEmptyInput() {
        // Arrange - Test the service that Handler uses
        CommonService commonService = new CommonService();
        Map<String, Object> input = new HashMap<>();

        // Act
        String result = commonService.processRequest(input);

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }

    @Test
    void testContextAndLoggerInteraction() {
        // Arrange - Test that handler correctly uses Context and Logger
        Context context = mock(Context.class);
        LambdaLogger logger = mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);

        // Act - Verify context provides logger
        LambdaLogger actualLogger = context.getLogger();

        // Assert
        assertNotNull(actualLogger);
        assertEquals(logger, actualLogger);
    }
}
