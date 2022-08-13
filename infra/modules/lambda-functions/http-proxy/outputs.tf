output "lambda_invoke_arn" {
  value = aws_lambda_function.http_proxy.invoke_arn
}

output "lambda_function_name" {
  value = aws_lambda_function.http_proxy.function_name
}
