# disable because it takes cost to fetch log from S3
#module "lambda" {
#  source = "./modules/lambda-functions/kottage-log"
#
#  aws_account_id         = var.aws_account_id
#  lb_access_log_prefix   = "lb"
#  s3_bucket_arn          = aws_s3_bucket.log.arn
#  s3_bucket_id           = aws_s3_bucket.log.id
#  slack_incoming_webhook = var.slack_incoming_webhook
#}

module "lambda" {
  source = "./modules/lambda-functions/http-proxy"

  kottage_host   = "10.0.0.84"
  subnet_ids     = aws_subnet.public.*.id
  vpc_id         = aws_vpc.kottage_vpc.id
}
