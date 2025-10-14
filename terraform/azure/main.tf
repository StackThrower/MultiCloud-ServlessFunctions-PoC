provider "azurerm" {
  features {}
}

resource "azurerm_resource_group" "lambda_rg" {
  name     = var.resource_group_name
  location = var.location
}

resource "azurerm_storage_account" "lambda_sa" {
  name                     = var.storage_account_name
  resource_group_name      = azurerm_resource_group.lambda_rg.name
  location                 = azurerm_resource_group.lambda_rg.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
}

resource "azurerm_app_service_plan" "lambda_plan" {
  name                = var.app_service_plan_name
  location            = azurerm_resource_group.lambda_rg.location
  resource_group_name = azurerm_resource_group.lambda_rg.name
  kind                = "FunctionApp"
  sku {
    tier = "Free"
    size = "F1"
  }
}

resource "azurerm_function_app" "lambda_func" {
  name                       = var.function_app_name
  location                   = azurerm_resource_group.lambda_rg.location
  resource_group_name        = azurerm_resource_group.lambda_rg.name
  app_service_plan_id        = azurerm_app_service_plan.lambda_plan.id
  storage_account_name       = azurerm_storage_account.lambda_sa.name
  storage_account_access_key = azurerm_storage_account.lambda_sa.primary_access_key
  version                    = "~4"
  os_type                    = "linux"
  site_config {
    java_version = "17"
  }
  app_settings = {
    "FUNCTIONS_WORKER_RUNTIME" = "java"
    "SPRING_PROFILES_ACTIVE"   = "lambda"
    "WEBSITE_RUN_FROM_PACKAGE" = var.package_path
  }
}

resource "random_string" "suffix" {
  length  = 6
  special = false
}

variable "resource_group_name" { default = "lambda-rg" }
variable "location" { default = "West Europe" }
variable "storage_account_name" { default = "lambdasa123456" }
variable "app_service_plan_name" { default = "lambda-plan" }
variable "function_app_name" { default = "lambda-function-app" }
variable "package_path" { default = "../target/lambda-service.jar" }
