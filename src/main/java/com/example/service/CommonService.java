package com.example.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CommonService {

    public String processRequest(Map<String, Object> input) {
        // Общая бизнес-логика для всех облаков
        return "Hello from Lambda";
    }

    public String processRequest() {
        return processRequest(Map.of());
    }
}

