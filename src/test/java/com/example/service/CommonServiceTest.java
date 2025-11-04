package com.example.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CommonServiceTest {

    private CommonService commonService;

    @BeforeEach
    void setUp() {
        commonService = new CommonService();
    }

    @Test
    void testProcessRequestWithInput() {
        // Arrange
        Map<String, Object> input = new HashMap<>();
        input.put("key", "value");

        // Act
        String result = commonService.processRequest(input);

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }

    @Test
    void testProcessRequestWithEmptyInput() {
        // Arrange
        Map<String, Object> input = new HashMap<>();

        // Act
        String result = commonService.processRequest(input);

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }

    @Test
    void testProcessRequestNoArgs() {
        // Act
        String result = commonService.processRequest();

        // Assert
        assertNotNull(result);
        assertEquals("Hello from Lambda v2.3", result);
    }
}