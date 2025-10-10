package com.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.example.service.CommonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.io.BufferedWriter;
import java.io.IOException;

@SpringBootApplication
public class GcpHandler implements HttpFunction {

    private static ApplicationContext applicationContext;
    private CommonService commonService;

    public GcpHandler() {
        if (applicationContext == null) {
            applicationContext = SpringApplication.run(GcpHandler.class);
        }
        this.commonService = applicationContext.getBean(CommonService.class);
    }

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        BufferedWriter writer = response.getWriter();
        String responseMessage = commonService.processRequest();
        writer.write(responseMessage);
    }
}
