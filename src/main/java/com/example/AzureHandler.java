package com.example;

import com.microsoft.azure.functions.*;
import com.example.service.CommonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import java.util.*;

@SpringBootApplication
public class AzureHandler {

    private static ApplicationContext applicationContext;
    private CommonService commonService;

    public AzureHandler() {
        if (applicationContext == null) {
            applicationContext = SpringApplication.run(AzureHandler.class);
        }
        this.commonService = applicationContext.getBean(CommonService.class);
    }

    public HttpResponseMessage run(
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        String responseMessage = commonService.processRequest();
        return request.createResponseBuilder(HttpStatus.OK).body(responseMessage).build();
    }
}
