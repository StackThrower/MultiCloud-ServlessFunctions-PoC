package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class HandlerTest {

    private Handler handler;

    @Mock
    private Context context;

    @Mock
    private LambdaLogger logger;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        handler = new Handler();
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void testHandleRequest() {
        // Arrange
        Map<String, Object> input = new HashMap<>();
        input.put("key", "value");

        // Act
        String result = handler.handleRequest(input, context);

        // Assert
        assertEquals("Hello from Lambda", result);
    }
}
