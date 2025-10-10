# Multi-Cloud Deployment Guide

## Обзор

Этот проект поддерживает деплой Java Lambda-функции в три облачных провайдера:
- **AWS Lambda**
- **Azure Functions**
- **Google Cloud Functions**

## Архитектура

Проект использует **общий модуль** `CommonService` для бизнес-логики, который разделяется между всеми тремя облаками:

```
src/main/java/com/example/
├── Handler.java          # AWS Lambda handler
├── AzureHandler.java     # Azure Functions handler
├── GcpHandler.java       # Google Cloud Functions handler
└── service/
    └── CommonService.java # Общая бизнес-логика для всех облаков
```

### Как это работает

Каждый handler (AWS, Azure, GCP) инициализирует Spring Boot контекст и использует `CommonService` через dependency injection:

- **CommonService** содержит общую бизнес-логику
- Каждый handler адаптирует запрос/ответ под специфику своего облака
- Spring Boot обеспечивает единообразное управление зависимостями

## Структура проекта

```
├── src/main/java/com/example/
│   ├── Handler.java          # AWS Lambda handler
│   ├── AzureHandler.java     # Azure Functions handler
│   ├── GcpHandler.java       # Google Cloud Functions handler
│   └── service/
│       └── CommonService.java # Общая бизнес-логика
├── terraform/
│   ├── aws/main.tf           # Terraform для AWS
│   ├── azure/main.tf         # Terraform для Azure
│   └── gcp/main.tf           # Terraform для GCP
├── .github/workflows/
│   └── deploy.yml            # GitHub Actions workflow
└── pom.xml                   # Maven конфигурация с профилями
```

## Сборка проекта

### Для AWS Lambda:
```bash
mvn clean package -DskipTests -Paws
```

### Для Azure Functions:
```bash
mvn clean package -DskipTests -Pazure
```

### Для Google Cloud Functions:
```bash
mvn clean package -DskipTests -Pgcp
```

Собранный jar-файл будет находиться в `target/lambda-service.jar`.

## Добавление бизнес-логики

Чтобы добавить новую бизнес-логику, редактируйте **только** класс `CommonService.java`:

```java
@Service
public class CommonService {
    public String processRequest(Map<String, Object> input) {
        // Ваша бизнес-логика здесь
        return "Hello from Lambda";
    }
}
```

Изменения автоматически применятся ко всем трём облакам! 🎉

## Деплой через Terraform

### AWS Lambda

1. Перейдите в директорию AWS Terraform:
```bash
cd terraform/aws
```

2. Инициализируйте Terraform:
```bash
terraform init
```

3. Примените конфигурацию:
```bash
terraform apply
```

**Необходимые переменные окружения:**
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`
- `AWS_REGION` (по умолчанию: us-east-1)

### Azure Functions

1. Перейдите в директорию Azure Terraform:
```bash
cd terraform/azure
```

2. Инициализируйте Terraform:
```bash
terraform init
```

3. Примените конфигурацию:
```bash
terraform apply
```

**Необходимые переменные окружения:**
- `ARM_CLIENT_ID`
- `ARM_CLIENT_SECRET`
- `ARM_SUBSCRIPTION_ID`
- `ARM_TENANT_ID`

### Google Cloud Functions

1. Перейдите в директорию GCP Terraform:
```bash
cd terraform/gcp
```

2. Инициализируйте Terraform:
```bash
terraform init
```

3. Примените конфигурацию:
```bash
terraform apply -var="project=YOUR_GCP_PROJECT_ID"
```

**Необходимые переменные окружения:**
- `GOOGLE_CREDENTIALS` (JSON с credentials)
- `GOOGLE_PROJECT` (ID проекта GCP)

## GitHub Actions Deployment

### Настройка секретов

В настройках вашего GitHub репозитория добавьте следующие секреты:

#### Для AWS:
- `AWS_ACCESS_KEY_ID`
- `AWS_SECRET_ACCESS_KEY`

#### Для Azure:
- `AZURE_CLIENT_ID`
- `AZURE_CLIENT_SECRET`
- `AZURE_SUBSCRIPTION_ID`
- `AZURE_TENANT_ID`

#### Для GCP:
- `GCP_CREDENTIALS` (содержимое JSON файла с ключом сервисного аккаунта)
- `GCP_PROJECT` (ID проекта)

### Запуск деплоя

1. Перейдите в раздел **Actions** вашего GitHub репозитория
2. Выберите workflow **"Deploy Lambda PoC"**
3. Нажмите **"Run workflow"**
4. Выберите целевое облако: `aws`, `azure` или `gcp`
5. Нажмите **"Run workflow"** для запуска

Workflow автоматически:
1. Соберёт проект с нужным профилем Maven
2. Инициализирует Terraform для выбранного облака
3. Задеплоит функцию в выбранное облако

## Различия между облачными провайдерами

### AWS Lambda
- **Handler**: `com.example.Handler::handleRequest`
- **Runtime**: `java17`
- **Формат**: Использует AWS Lambda API (`RequestHandler`)
- **Вызов**: `commonService.processRequest(input)`

### Azure Functions
- **Handler**: `com.example.AzureHandler`
- **Runtime**: Java 17 на Linux
- **Формат**: Использует Azure Functions API (`HttpRequestMessage`, `ExecutionContext`)
- **Вызов**: `commonService.processRequest()`

### Google Cloud Functions
- **Entry Point**: `com.example.GcpHandler`
- **Runtime**: `java17`
- **Формат**: Использует Google Cloud Functions API (`HttpFunction`)
- **Вызов**: `commonService.processRequest()`

## Кастомизация

### Изменение параметров функции

Отредактируйте файлы `terraform/*/main.tf` для изменения:
- Памяти (memory_size)
- Таймаута (timeout)
- Переменных окружения (environment_variables)
- Региона деплоя

### Добавление зависимостей

Добавьте зависимости в `pom.xml` в секцию `<dependencies>`.

### Добавление Spring Boot сервисов

Создавайте новые сервисы в пакете `com.example.service` и используйте их в `CommonService`:

```java
@Service
public class MyNewService {
    public String doSomething() {
        return "Result";
    }
}

@Service
public class CommonService {
    @Autowired
    private MyNewService myNewService;
    
    public String processRequest(Map<String, Object> input) {
        return myNewService.doSomething();
    }
}
```

## Удаление ресурсов

Для удаления задеплоенных ресурсов выполните в соответствующей директории terraform:

```bash
terraform destroy
```

## Troubleshooting

### Проблема: Классы не найдены в jar-файле
**Решение**: Убедитесь, что maven-shade-plugin правильно настроен и включает все классы.

### Проблема: Terraform не может найти credentials
**Решение**: Проверьте, что все необходимые переменные окружения установлены.

### Проблема: Функция не запускается в облаке
**Решение**: Проверьте логи в консоли облачного провайдера:
- AWS: CloudWatch Logs
- Azure: Application Insights / Log Stream
- GCP: Cloud Logging

### Проблема: Spring Boot контекст не инициализируется
**Решение**: Убедитесь, что все классы находятся в пакете `com.example` или его подпакетах.

## Дополнительная информация

- [AWS Lambda Java Documentation](https://docs.aws.amazon.com/lambda/latest/dg/java-handler.html)
- [Azure Functions Java Developer Guide](https://docs.microsoft.com/azure/azure-functions/functions-reference-java)
- [Google Cloud Functions Java Runtime](https://cloud.google.com/functions/docs/concepts/java-runtime)

