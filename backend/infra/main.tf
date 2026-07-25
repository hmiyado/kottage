terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }

  required_version = ">= 1.5"
}

provider "aws" {
  profile = "default"
  region  = "us-east-2"
}

# フェーズ9: EC2/EIP撤去に伴いVPCを廃止した。
#
# VPCの中身はEC2本体（ec2.tf）、http_proxy Lambda（旧lambda.tf）、
# aws_apigatewayv2_vpc_link（旧api_gateway.tf）だけであり、いずれもこのフェーズで
# 削除した。アプリLambda（lambda_app.tf の aws_lambda_function.kottage_app）は
# vpc_config を持たない非VPC構成のため影響を受けない。TiDB Serverlessへの接続も
# NAT Gatewayなしのインターネットegressで完結しており、VPCへの依存はない。
# 詳細は docs/projects/lambda/migration-plan.md フェーズ9を参照。
