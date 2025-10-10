package com.example;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.example.service.CommonService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.util.Map;

@SpringBootApplication
public class Handler implements RequestHandler<Map<String, Object>, String> {

    private static ApplicationContext applicationContext;
    private CommonService commonService;

    public Handler() {
        if (applicationContext == null) {
            applicationContext = SpringApplication.run(Handler.class);
        }
        this.commonService = applicationContext.getBean(CommonService.class);
    }

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("Lambda function invoked with input: " + input);

        return commonService.processRequest(input);
    }

    public static void main(String[] args) {
        SpringApplication.run(Handler.class, args);
    }
}
