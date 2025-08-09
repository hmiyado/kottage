resource "aws_lambda_function" "http_proxy" {
  function_name = "http_proxy"
  role          = aws_iam_role.http_proxy.arn

  filename = data.archive_file.http_proxy.output_path
  source_code_hash = data.archive_file.http_proxy.output_base64sha256

  environment {
    variables = {
      KottageHost = var.kottage_host
      KottagePort = var.kottage_port
    }
  }

  runtime = "nodejs22.x"
  handler = "index.handler"
  timeout = 3

  vpc_config {
    security_group_ids = [
      aws_security_group.http_proxy.id
    ]
    subnet_ids         = var.subnet_ids
  }

  tags =  {
    Name = "http-proxy"
    Service = "kottage"
  }
}

data "archive_file" "http_proxy" {
  type        = "zip"
  source_dir  = "${path.module}/src"
  output_path = "${path.module}/src.zip"
}
