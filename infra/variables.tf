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

variable "ecs_cloudwatch_kottage_api" {
  type    = string
  default = "/ecs/awslogs-kottage-api"
}

variable "main_availability_zones" {
  type    = list(string)
  default = ["us-east-2a", "us-east-2b"]
}
