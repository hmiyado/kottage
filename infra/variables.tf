variable "db_user" {
  type    = string
  default = "user"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "admin_name" {
  type      = string
  sensitive = true
}

variable "admin_password" {
  type      = string
  sensitive = true
}

variable "aws_account_id" {
  type      = string
  sensitive = true
}

variable "slack_incoming_webhook" {
  type      = string
  sensitive = true
}

variable "vercel_deploy_hook" {
  type      = string
  sensitive = true
}

variable "kottage_image" {
  type    = string
  default = "miyado/kottage:latest"
}

variable "main_availability_zones" {
  type    = list(string)
  default = ["us-east-2a", "us-east-2b"]
}
