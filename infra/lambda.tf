module "lambda_http_proxy" {
  source = "./modules/lambda-functions/http-proxy"

  kottage_host   = "10.0.0.84"
  subnet_ids     = aws_subnet.public.*.id
  vpc_id         = aws_vpc.kottage_vpc.id
}

resource "aws_lambda_permission" "api_gateway" {
  action        = "lambda:InvokeFunction"
  function_name = module.lambda_http_proxy.lambda_function_name
  principal     = "apigateway.amazonaws.com"
  source_arn = "${aws_apigatewayv2_api.kottage.execution_arn}/*/*"
}
