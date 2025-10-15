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
    echo "Creating ZIP for GCP deployment..."
    echo "======================"

    # Create lambda-service.zip containing lambda-service.jar at the root
    cd target
    zip -j lambda-service.zip lambda-service.jar
    cd ..
    echo "ZIP file created: target/lambda-service.zip"
    echo "Size: $(du -h target/lambda-service.zip | cut -f1)"
    echo ""
    echo "Preparing Terraform..."
    echo "======================"

    cd terraform/aws

    # Initialize Terraform if needed
    if [ ! -d ".terraform" ]; then
        echo "Initializing Terraform..."
        terraform init
    fi

    # Check if IAM role already exists in AWS and import it
    echo "Checking if IAM role 'lambda_exec_role' exists in AWS..."
    if aws iam get-role --role-name lambda_exec_role &> /dev/null; then
        echo "IAM role exists in AWS. Checking Terraform state..."

        # Check if role is already in Terraform state
        if ! terraform state show aws_iam_role.lambda_exec &> /dev/null; then
            echo "Importing existing IAM role into Terraform state..."
            terraform import aws_iam_role.lambda_exec lambda_exec_role || true
            echo "✅ IAM role imported"
        else
            echo "✅ IAM role already in Terraform state"
        fi
    else
        echo "IAM role doesn't exist in AWS yet. Terraform will create it."
    fi

    # Check if Lambda function exists and import it
    echo "Checking if Lambda function 'lambda-service' exists in AWS..."
    if aws lambda get-function --function-name lambda-service &> /dev/null; then
        echo "Lambda function exists in AWS. Checking Terraform state..."

        if ! terraform state show aws_lambda_function.lambda_service &> /dev/null; then
            echo "Importing existing Lambda function into Terraform state..."
            terraform import aws_lambda_function.lambda_service lambda-service || true
            echo "✅ Lambda function imported"
        else
            echo "✅ Lambda function already in Terraform state"
        fi
    else
        echo "Lambda function doesn't exist in AWS yet. Terraform will create it."
    fi

    cd ../..

    echo ""
    echo "Ready to deploy!"
    echo "================"
    echo "Run the following commands:"
    echo "  cd terraform/aws"
    echo "  terraform plan"
    echo "  terraform apply"
else
    echo "❌ Build failed - JAR file not created"
    exit 1
fi
