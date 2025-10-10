provider "aws" {
  region = var.aws_region
}

resource "aws_iam_role" "lambda_exec" {
  name = "lambda_exec_role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [{
      Action = "sts:AssumeRole"
      Effect = "Allow"
      Principal = {
        Service = "lambda.amazonaws.com"
      }
    }]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_policy" {
  role       = aws_iam_role.lambda_exec.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_lambda_function" "lambda_service" {
  function_name = var.function_name
  role          = aws_iam_role.lambda_exec.arn
  handler       = var.handler
  runtime       = var.runtime
  filename      = "${path.module}/../../target/lambda-service.jar"
  source_code_hash = filebase64sha256("${path.module}/../../target/lambda-service.jar")
  timeout       = var.timeout
  memory_size   = var.memory_size
  environment {
    variables = var.environment_variables
  }
  tracing_config {
    mode = var.tracing_mode
  }
}

resource "aws_lambda_function_url" "lambda_service_url" {
  function_name      = aws_lambda_function.lambda_service.function_name
  authorization_type = "NONE"

  cors {
    allow_credentials = true
    allow_origins     = ["*"]
    allow_methods     = ["*"]
    allow_headers     = ["date", "keep-alive", "content-type"]
    expose_headers    = ["keep-alive", "date"]
    max_age           = 86400
  }
}

output "lambda_function_url" {
  description = "The URL to invoke the Lambda function"
  value       = aws_lambda_function_url.lambda_service_url.function_url
}

variable "aws_region" { default = "us-east-1" }
variable "function_name" { default = "lambda-service" }
variable "handler" { default = "com.example.Handler::handleRequest" }
variable "runtime" { default = "java17" }
variable "timeout" { default = 30 }
variable "memory_size" { default = 512 }
variable "environment_variables" { default = { SPRING_PROFILES_ACTIVE = "lambda" } }
variable "tracing_mode" { default = "Active" }
