# AWS Lambda PoC with Spring Boot and Java 17

This project demonstrates a serverless AWS Lambda function built with Spring Boot and Java 17.

## Project Structure

```
├── pom.xml                           # Maven configuration with AWS Lambda dependencies
├── aws-lambda-config.json            # AWS Lambda deployment configuration
├── src/
│   ├── main/
│   │   ├── java/com/example/
│   │   │   └── Handler.java          # Lambda function handler
│   │   └── resources/
│   │       ├── application.properties # Spring Boot configuration
│   │       └── log4j2.xml            # Logging configuration
│   └── test/java/com/example/
│       └── HandlerTest.java          # Unit tests
```

## Dependencies

- **aws-lambda-java-core**: Core AWS Lambda runtime
- **aws-lambda-java-events**: AWS Lambda event types
- **aws-lambda-java-log4j2**: Log4j2 integration for Lambda
- **Spring Boot 3.1.5**: Application framework

## Building the Project

To build the shaded JAR for deployment:

```bash
mvn clean package
```

This will create `lambda-service.jar` in the `target/` directory.

## AWS Lambda Configuration

- **Handler**: `com.example.Handler::handleRequest`
- **Runtime**: Java 17
- **Memory**: 512 MB
- **Timeout**: 30 seconds

## Deployment

1. Build the project: `mvn clean package`
2. Upload `target/lambda-service.jar` to AWS Lambda
3. Configure the function using settings from `aws-lambda-config.json`

## Testing

Run unit tests:

```bash
mvn test
```

## Local Testing

The Handler class includes a main method for local Spring Boot execution:

```bash
mvn spring-boot:run
```
