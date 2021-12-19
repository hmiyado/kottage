variable "aws_account_id" {
  type = string
}

variable "lb_access_log_prefix" {
  type = string
}

variable "s3_bucket_id" {
  type = string
}

variable "s3_bucket_arn" {
  type = string
}

variable "slack_incoming_webhook" {
  type      = string
  sensitive = true
}
