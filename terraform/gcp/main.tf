provider "google" {
  project = var.project
  region  = var.region
}

resource "google_storage_bucket" "lambda_bucket" {
  name     = var.bucket_name
  location = var.region
}

resource "google_storage_bucket_object" "lambda_zip" {
  name   = "lambda-service.zip"
  bucket = google_storage_bucket.lambda_bucket.name
  source = var.zip_path
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
  source_archive_object = google_storage_bucket_object.lambda_zip.name
  environment_variables = var.environment_variables
  trigger_http = true
}

variable "project" {default = "multicloudfunction-475213"}
variable "region" { default = "us-central1" }
variable "bucket_name" { default = "lambda-poc-bucket-123456" }
variable "zip_path" { default = "../../target/lambda-service.zip" }
variable "function_name" { default = "lambda-service" }
variable "entry_point" { default = "com.example.GcpHandler" }
variable "memory_size" { default = 512 }
variable "timeout" { default = 30 }
variable "environment_variables" { default = { SPRING_PROFILES_ACTIVE = "lambda" } }
