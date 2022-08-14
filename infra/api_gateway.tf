resource "aws_apigatewayv2_api" "kottage" {
  name = "kottage"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_vpc_link" "kottage" {
  name        = "kottage"
  security_group_ids = [aws_security_group.api_gatewway.id]
  subnet_ids = aws_subnet.public.*.id
}

resource "aws_apigatewayv2_integration" "kottage" {
  api_id           = aws_apigatewayv2_api.kottage.id
  integration_type = "AWS_PROXY"

  connection_type           = "INTERNET"
  description               = "proxy to lambda http-proxy"
  integration_method        = "POST"
  integration_uri           = module.lambda_http_proxy.lambda_invoke_arn
  payload_format_version = "2.0"
  timeout_milliseconds = 3000
}

resource "aws_apigatewayv2_stage" "kottage_default" {
  api_id = aws_apigatewayv2_api.kottage.id
  name   = "$default"
  auto_deploy = true

  tags = {
    Service = "kottage"
  }
}

resource "aws_apigatewayv2_route" "kottage_route" {
  api_id           = aws_apigatewayv2_api.kottage.id
  route_key = "ANY /{proxy+}"
  target = "integrations/${aws_apigatewayv2_integration.kottage.id}"
}
