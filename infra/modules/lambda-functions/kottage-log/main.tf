resource "aws_lambda_function" "kottage_log" {
  function_name = "kottage_log"
  role          = aws_iam_role.lambda_kottage_log.arn

  s3_bucket        = var.s3_bucket_id
  s3_key           = aws_s3_bucket_object.kottage_log.key
  source_code_hash = data.archive_file.kottage_log.output_base64sha256

  environment {
    variables = {
      SlackIncomingWebhook = var.slack_incoming_webhook
    }
  }

  runtime = "nodejs14.x"
  handler = "index.handler"
  timeout = 3
}

resource "aws_lambda_permission" "allow_s3" {
  statement_id  = "AllowExecutionFromCloudWatch"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.kottage_log.function_name
  principal     = "s3.amazonaws.com"
  source_arn    = var.s3_bucket_arn
}

resource "aws_cloudwatch_log_group" "kottage_log" {
  name = "/aws/lambda/${aws_lambda_function.kottage_log.function_name}"

  retention_in_days = 14
}
