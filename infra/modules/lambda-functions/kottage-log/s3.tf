data "archive_file" "kottage_log" {
  type        = "zip"
  source_dir  = "${path.module}/src"
  output_path = "${path.module}/src.zip"
}

resource "aws_s3_bucket_object" "kottage_log" {
  bucket = var.s3_bucket_id

  key    = "kottage_log.zip"
  source = data.archive_file.kottage_log.output_path

  etag = filemd5(data.archive_file.kottage_log.output_path)
}

resource "aws_s3_bucket_notification" "kottage_log" {
  bucket = var.s3_bucket_id

  lambda_function {
    lambda_function_arn = aws_lambda_function.kottage_log.arn
    events              = ["s3:ObjectCreated:Put", "s3:ObjectCreated:Post"]
    filter_prefix       = "${var.lb_access_log_prefix}/AWSLogs/${var.aws_account_id}/elasticloadbalancing"
    filter_suffix       = ".gz"
  }

  depends_on = [aws_lambda_permission.allow_s3]
}
