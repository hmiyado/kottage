resource "aws_apigatewayv2_api" "kottage" {
  name          = "kottage"
  protocol_type = "HTTP"
}

resource "aws_apigatewayv2_vpc_link" "kottage" {
  name               = "kottage"
  security_group_ids = [aws_security_group.api_gateway.id]
  subnet_ids         = aws_subnet.public.*.id
}

resource "aws_apigatewayv2_integration" "kottage" {
  api_id           = aws_apigatewayv2_api.kottage.id
  integration_type = "AWS_PROXY"

  connection_type    = "INTERNET"
  description        = "proxy to app lambda (kottage_app:live)"
  integration_method = "POST"
  # フェーズ8: 本番切替。http_proxy（VPC内）からアプリLambdaのエイリアスへ変更。
  # ロールバックは、この値を module.lambda_http_proxy.lambda_invoke_arn に戻すだけ
  # （module.lambda_http_proxyとEC2は削除せず稼働させたまま残している。
  # 詳しくはmigration-plan.mdフェーズ8「ロールバック手順」を参照）。
  integration_uri        = aws_lambda_alias.kottage_app_live.invoke_arn
  payload_format_version = "2.0"
  # 実測のコールドスタートは6.0秒（1769MB）。旧経路（http_proxy、関数timeout=10秒）に
  # 合わせた10000msのままだと、コールドスタート直後にDB接続などの実処理が乗った場合に
  # 統合タイムアウトが先に切れてしまう余地が大きい。アプリLambda自身のtimeout（29秒、
  # lambda_app.tf）と一致させ、API Gateway側が先に打ち切ることがないようにする
  # （HTTP APIの統合タイムアウト上限は30000ms）。
  timeout_milliseconds = 29000
}

resource "aws_apigatewayv2_stage" "kottage_default" {
  api_id      = aws_apigatewayv2_api.kottage.id
  name        = "$default"
  auto_deploy = true

  tags = {
    Service = "kottage"
  }
}

resource "aws_apigatewayv2_route" "kottage_route" {
  api_id    = aws_apigatewayv2_api.kottage.id
  route_key = "ANY /{proxy+}"
  target    = "integrations/${aws_apigatewayv2_integration.kottage.id}"
}

resource "aws_apigatewayv2_domain_name" "kottage" {
  domain_name = "kottage.miyado.dev"
  domain_name_configuration {
    certificate_arn = aws_acm_certificate.kottage_miyado_dev.arn
    endpoint_type   = "REGIONAL"
    security_policy = "TLS_1_2"
  }

  tags = {
    Service = "kottage"
  }
}

resource "aws_apigatewayv2_api_mapping" "kottage" {
  api_id      = aws_apigatewayv2_api.kottage.id
  domain_name = aws_apigatewayv2_domain_name.kottage.id
  stage       = aws_apigatewayv2_stage.kottage_default.id
}
