#!/bin/bash
# Build script for AWS Lambda PoC

set -e

echo "Building AWS Lambda PoC project..."
echo "=================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven first: brew install maven"
    exit 1
fi

# Clean and build the project
echo "Running Maven build (skipping tests)..."
mvn clean package -DskipTests -Paws

# Check if JAR was created
if [ -f "target/lambda-service.jar" ]; then
    echo ""
    echo "✅ Build successful!"
    echo "JAR file created: target/lambda-service.jar"
    echo "Size: $(du -h target/lambda-service.jar | cut -f1)"
    echo ""
    echo "You can now run Terraform to deploy:"
    echo "  cd terraform/aws"
    echo "  terraform init"
    echo "  terraform plan"
    echo "  terraform apply"
else
    echo "❌ Build failed - JAR file not created"
    exit 1
fi

