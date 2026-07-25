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
