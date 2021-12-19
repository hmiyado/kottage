module "lambda" {
  source = "./modules/lambda-functions/kottage-log"

  aws_account_id         = var.aws_account_id
  lb_access_log_prefix   = "lb"
  s3_bucket_arn          = aws_s3_bucket.log.arn
  s3_bucket_id           = aws_s3_bucket.log.id
  slack_incoming_webhook = var.slack_incoming_webhook
}
