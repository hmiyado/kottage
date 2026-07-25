# GitHub Actions OIDC federation so CI can push images to ECR without
# long-lived AWS credentials.
#
# NOTE: an AWS account can only have one OIDC provider per issuer URL.
# Before running `terraform apply`, check whether
# token.actions.githubusercontent.com is already registered, e.g.:
#   aws iam list-open-id-connect-providers
# or, if a local state file exists:
#   jq '.resources[] | select(.type=="aws_iam_openid_connect_provider")' terraform.tfstate
# If it already exists, set create_github_oidc_provider = false in
# sensitive.tfvars (or via -var) and either import the existing resource
# or leave it managed outside this module.
locals {
  github_oidc_provider_arn = (
    var.create_github_oidc_provider
    ? aws_iam_openid_connect_provider.github_actions[0].arn
    : "arn:aws:iam::${var.aws_account_id}:oidc-provider/token.actions.githubusercontent.com"
  )
}

resource "aws_iam_openid_connect_provider" "github_actions" {
  count = var.create_github_oidc_provider ? 1 : 0

  url            = "https://token.actions.githubusercontent.com"
  client_id_list = ["sts.amazonaws.com"]

  # GitHub's OIDC thumbprint (DigiCert Global Root CA). AWS no longer relies
  # on this value for validation (it verifies against its own trusted CA
  # store since 2023), but the argument is still required by the resource.
  thumbprint_list = ["6938fd4d98bab03faadb97b34396831e3780aea1"]

  tags = {
    Name    = "github-actions-oidc"
    Service = "kottage"
  }
}

resource "aws_iam_role" "github_actions_ecr_push" {
  name = "kottage_github_actions_ecr_push"
  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Principal = {
          Federated = local.github_oidc_provider_arn
        },
        Action = "sts:AssumeRoleWithWebIdentity",
        Condition = {
          StringEquals = {
            "token.actions.githubusercontent.com:aud" = "sts.amazonaws.com"
          },
          StringLike = {
            "token.actions.githubusercontent.com:sub" = "repo:hmiyado/kottage:*"
          }
        }
      }
    ]
  })

  tags = {
    Name    = "kottage_github_actions_ecr_push"
    Service = "kottage"
  }
}

resource "aws_iam_role_policy" "github_actions_ecr_push" {
  name = "ecr_push"
  role = aws_iam_role.github_actions_ecr_push.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "ecr:GetAuthorizationToken"
        ],
        Resource = "*"
      },
      {
        Effect = "Allow",
        Action = [
          "ecr:BatchCheckLayerAvailability",
          "ecr:InitiateLayerUpload",
          "ecr:UploadLayerPart",
          "ecr:CompleteLayerUpload",
          "ecr:PutImage",
          "ecr:BatchGetImage"
        ],
        Resource = aws_ecr_repository.kottage.arn
      }
    ]
  })
}

# フェーズ8: イメージ更新の主体をTerraformからCIへ移すため（migration-plan.mdの8.1）、
# delivery.ymlがECR pushの後に「アプリLambdaのコード更新 → バージョン発行 →
# エイリアス切替」まで行う。そのための権限をこのロールに追加する。
#
# 【命名について】このロール/リソースアドレスは "github_actions_ecr_push" /
# "ecr_push" だが、フェーズ8以降はECR pushだけでなくLambdaデプロイの権限も持つため
# 実態と乖離している。ただしIAMロールのnameはForceNew（変更すると再作成になり、
# 信頼ポリシーの再確立とGitHub Actions側のAWS_GITHUB_ACTIONS_ROLE_ARN変数の更新が
# 必要になる）ため、本PRでは名前を変更せず、権限だけをこのロールに追加する形にした。
# リネームする場合は、ロールバック中でない安定期に別PRとして計画すること。
resource "aws_iam_role_policy" "github_actions_lambda_deploy" {
  name = "lambda_deploy"
  role = aws_iam_role.github_actions_ecr_push.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          # `aws lambda update-function-code` でイメージを差し替える。
          "lambda:UpdateFunctionCode",
          # `aws lambda wait function-updated` が使うポーリング先。waiterの実装
          # バージョンにより GetFunctionConfiguration / GetFunction のどちらを
          # 呼ぶか変わりうるため両方を許可する（読み取り専用で実害はない）。
          "lambda:GetFunctionConfiguration",
          "lambda:GetFunction",
          # `aws lambda publish-version` で新バージョンを発行する。
          "lambda:PublishVersion",
          # `aws lambda update-alias` で "live" エイリアスの向き先を切り替える。
          "lambda:UpdateAlias"
        ],
        # kottage_app関数のみに限定する（無出力・有出力ARNの両方をカバー）。
        # 他のLambda関数（http_proxy等）には一切権限を与えない。
        Resource = [
          aws_lambda_function.kottage_app.arn,
          "${aws_lambda_function.kottage_app.arn}:*"
        ]
      }
    ]
  })
}
