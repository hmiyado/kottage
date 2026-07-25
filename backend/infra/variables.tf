variable "db_user" {
  type    = string
  default = "user"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "aws_account_id" {
  type      = string
  sensitive = true
}

variable "main_availability_zones" {
  type    = list(string)
  default = ["us-east-2a", "us-east-2b"]
}

variable "kottage_port" {
  type = string
}

variable "create_github_oidc_provider" {
  type        = bool
  default     = true
  description = "Whether to create the token.actions.githubusercontent.com OIDC provider. Set to false if it already exists in this AWS account (an account can only have one provider per issuer URL)."
}
