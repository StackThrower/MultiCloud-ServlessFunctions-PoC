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
  filename      = var.filename
  source_code_hash = filebase64sha256(var.filename)
  timeout       = var.timeout
  memory_size   = var.memory_size
  environment {
    variables = var.environment_variables
  }
  tracing_config {
    mode = var.tracing_mode
  }
}

variable "aws_region" { default = "us-east-1" }
variable "function_name" { default = "lambda-service" }
variable "handler" { default = "com.example.Handler::handleRequest" }
variable "runtime" { default = "java17" }
variable "filename" { default = "../target/lambda-service.jar" }
variable "timeout" { default = 30 }
variable "memory_size" { default = 512 }
variable "environment_variables" { default = { SPRING_PROFILES_ACTIVE = "lambda" } }
variable "tracing_mode" { default = "Active" }

