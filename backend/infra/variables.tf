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

# フェーズ7: アプリLambda（lambda_app.tf）用の変数。
# 値そのものは sensitive.tfvars で与える。ここに定義する変数のうち機密なものは
# sensitive = true にしている（terraform plan/apply の出力・stateに平文で残さないため）。

variable "app_image_tag" {
  type        = string
  description = "aws_ecr_repository.kottage にpushされたイメージのタグ（delivery.ymlが生成するバージョン文字列）。\"latest\"固定にしない。デフォルト値は持たせず、apply毎に明示させる。"
}

variable "mysql_database" {
  type      = string
  sensitive = true
}

variable "mysql_host" {
  type      = string
  sensitive = true
}

variable "mysql_port" {
  type = string
  # TiDB Serverlessの標準ポート（docs/projects/tidb/migration-plan.md参照）。
  default = "4000"
}

variable "mysql_user" {
  type      = string
  sensitive = true
}

variable "mysql_password" {
  type      = string
  sensitive = true
}

variable "mysql_ssl_mode" {
  type = string
  # TiDB ServerlessはTLS必須。
  default = "REQUIRED"
}

variable "session_sign_key" {
  type        = string
  sensitive   = true
  description = "EC2の.envに設定済みのSESSION_SIGN_KEYと完全に同一の値を渡すこと。異なると本番切替時に全ユーザーがログアウトされる。"
}

variable "admin_name" {
  type      = string
  sensitive = true
}

variable "admin_password" {
  type      = string
  sensitive = true
}

variable "oidc_google_client_id" {
  type = string
}

variable "oidc_google_client_secret" {
  type      = string
  sensitive = true
}

variable "oidc_google_callback_url" {
  type        = string
  description = "GoogleのOAuthクライアント設定に登録済みのリダイレクトURIと一致させること。検証用のLambda Function URLで試す場合は、そのURL向けのコールバックをGoogle Cloud Console側にも追加登録する必要がある。"
}

variable "oidc_google_default_redirect_url" {
  type = string
}

variable "vercel_deploy_hook" {
  type      = string
  sensitive = true
}
