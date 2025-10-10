provider "google" {
  project = var.project
  region  = var.region
}

resource "google_storage_bucket" "lambda_bucket" {
  name     = var.bucket_name
  location = var.region
}

resource "google_storage_bucket_object" "lambda_jar" {
  name   = "lambda-service.jar"
  bucket = google_storage_bucket.lambda_bucket.name
  source = var.jar_path
}

resource "google_cloudfunctions_function" "lambda_func" {
  name        = var.function_name
  description = "AWS Lambda PoC with Spring Boot and Java 17"
  runtime     = "java17"
  entry_point = var.entry_point
  region      = var.region
  available_memory_mb   = var.memory_size
  timeout     = var.timeout
  source_archive_bucket = google_storage_bucket.lambda_bucket.name
  source_archive_object = google_storage_bucket_object.lambda_jar.name
  environment_variables = var.environment_variables
  trigger_http = true
}

variable "project" {}
variable "region" { default = "us-central1" }
variable "bucket_name" { default = "lambda-poc-bucket-123456" }
variable "jar_path" { default = "../../target/lambda-service.jar" }
variable "function_name" { default = "lambda-service" }
variable "entry_point" { default = "com.example.GcpHandler" }
variable "memory_size" { default = 512 }
variable "timeout" { default = 30 }
variable "environment_variables" { default = { SPRING_PROFILES_ACTIVE = "lambda" } }
