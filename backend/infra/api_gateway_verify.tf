# フェーズ7: 検証用のHTTP API。
#
# 本番の aws_apigatewayv2_api.kottage（api_gateway.tf）には一切触れず、完全に別の
# API Gatewayとしてここに作成する。カスタムドメインは使わず execute-api のURLで検証する。

resource "aws_apigatewayv2_api" "kottage_verify" {
  name          = "kottage-verify"
  protocol_type = "HTTP"

  tags = {
    Name    = "kottage-verify"
    Service = "kottage"
  }
}

resource "aws_apigatewayv2_integration" "kottage_verify" {
  api_id           = aws_apigatewayv2_api.kottage_verify.id
  integration_type = "AWS_PROXY"

  integration_method     = "POST"
  integration_uri        = aws_lambda_function.kottage_app.invoke_arn
  payload_format_version = "2.0"
  # Lambda本体のtimeout（29秒）と揃える。HTTP APIの統合タイムアウト上限は30秒。
  timeout_milliseconds = 29000
}

resource "aws_apigatewayv2_stage" "kottage_verify_default" {
  api_id      = aws_apigatewayv2_api.kottage_verify.id
  name        = "$default"
  auto_deploy = true

  tags = {
    Service = "kottage"
  }
}

resource "aws_apigatewayv2_route" "kottage_verify" {
  api_id    = aws_apigatewayv2_api.kottage_verify.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.kottage_verify.id}"
}

resource "aws_lambda_permission" "kottage_verify_api_gateway" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.kottage_app.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.kottage_verify.execution_arn}/*/*"
}
