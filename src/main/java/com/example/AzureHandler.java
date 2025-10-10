package com.example;

import com.microsoft.azure.functions.*;
import java.util.*;

public class AzureHandler {
    public HttpResponseMessage run(
        HttpRequestMessage<Optional<String>> request,
        final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");
        String responseMessage = "Hello from Azure Function!";
        return request.createResponseBuilder(HttpStatus.OK).body(responseMessage).build();
    }
}
