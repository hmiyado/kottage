# フェーズ7: アプリ本体のLambda（package_type = "Image"）。
#
# 本番の aws_apigatewayv2_api.kottage / aws_apigatewayv2_integration.kottage には一切
# 触れない。この Lambda を本番経路に繋ぐのはフェーズ8。
#
# フェーズ7の検証用エンドポイントはここでは定義しない。使い捨てのリソースをリポジトリと
# stateに残さないため、検証時に aws_lambda_function_url を未コミットの .tf で一時的に
# 作成し、検証後にファイルを消して apply することで Terraform に片付けさせる。
# 詳細は migration-plan.md の 7.1 を参照。
#
# 環境変数の完全な一覧と根拠は docs/projects/lambda/migration-plan.md のフェーズ7を参照。

resource "aws_cloudwatch_log_group" "kottage_app" {
  # Lambdaのデフォルトのロググループ命名規則 (/aws/lambda/<function_name>) に合わせる。
  # 先にTerraformで作成しておくことで、Lambdaに自動作成させた場合の無期限保持を避ける。
  name              = "/aws/lambda/kottage_app"
  retention_in_days = 14

  tags = {
    Name    = "kottage_app"
    Service = "kottage"
  }
}

resource "aws_iam_role" "kottage_app" {
  name = "kottage_app"
  assume_role_policy = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "",
        "Effect" : "Allow",
        "Principal" : {
          "Service" : [
            "lambda.amazonaws.com"
          ]
        },
        "Action" : "sts:AssumeRole"
      }
    ]
  })

  tags = {
    Name    = "kottage_app"
    Service = "kottage"
  }
}

resource "aws_iam_role_policy" "kottage_app_logs" {
  name = "kottage_app_logs"
  role = aws_iam_role.kottage_app.id
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        # 明示作成したロググループのみに絞る（logs:CreateLogGroupは付与しない。
        # 自動作成に頼らないことが、このリソースをTerraformで作る目的そのものであるため）。
        Resource = "${aws_cloudwatch_log_group.kottage_app.arn}:*"
      }
    ]
  })
}

resource "aws_lambda_function" "kottage_app" {
  function_name = "kottage_app"
  role          = aws_iam_role.kottage_app.arn

  package_type = "Image"
  # イメージタグは常に変数で指定する。"latest"に固定すると、今どのビルドがデプロイ
  # されているかTerraformの差分から追えなくなるため。
  # Lambdaはマニフェストリスト（イメージインデックス）を解決できず、単一アーキテクチャの
  # マニフェストを直接指す必要がある。buildxのマルチアーキビルドが作るタグはインデックスを
  # 指すため、タグではなくarm64マニフェストのダイジェストを渡す。
  # "sha256:..." が渡されたら "@" で、通常のタグなら ":" で連結する。
  image_uri = "${aws_ecr_repository.kottage.repository_url}${startswith(var.app_image_tag, "sha256:") ? "@" : ":"}${var.app_image_tag}"

  # フェーズ4でarm64マルチアーキ対応済み。x86_64より約2割安い。
  architectures = ["arm64"]

  # 1769MBで1vCPU相当。実測（7.5節）ではコールドスタートが1024MBの9.8秒に対して6.0秒まで
  # 短縮し、2048MBに増やしても6.0秒のままで頭打ちだった。無料枠に収まる範囲なので、
  # 頭打ちになる手前の最小値であるこの値を採る。
  memory_size = 1769

  # API Gateway HTTP APIの統合タイムアウト上限が30秒のため、それを下回る値にする。
  timeout = 29

  # 【重要】vpc_configは付けない。非VPC LambdaにすることでENIを持たせず、
  # インターネットegressが無料になり、TiDBへNAT Gatewayなしで到達できる。
  # これが本移行の設計の要（docs/projects/lambda/README.md「移行後のアーキテクチャ」参照）。

  environment {
    variables = {
      DEVELOPMENT = "false"
      # Lambda Web AdapterのAWS_LWA_PORT（Dockerfile）と一致させる。
      PORT = "8080"
      # ECRのイメージタグをそのままバージョン表示に流用する。
      VERSION = var.app_image_tag

      # TiDB Serverless接続情報。値はsensitive.tfvarsから注入する。
      MYSQL_DATABASE = var.mysql_database
      MYSQL_HOST     = var.mysql_host
      MYSQL_PORT     = var.mysql_port
      MYSQL_USER     = var.mysql_user
      MYSQL_PASSWORD = var.mysql_password
      MYSQL_SSL_MODE = var.mysql_ssl_mode

      # フェーズ3で起動時処理から分離済み。コールドスタート毎のFlyway実行と、
      # 複数実行環境からの同時マイグレーションを避ける。
      RUN_MIGRATION_ON_STARTUP = "false"

      # 【重要】EC2の.envに設定されている値と完全に同一でなければならない。
      # 異なるとフェーズ8の切替時点で全ユーザーがログアウトされる。
      SESSION_SIGN_KEY = var.session_sign_key

      ADMIN_NAME     = var.admin_name
      ADMIN_PASSWORD = var.admin_password

      OIDC_GOOGLE_CLIENT_ID            = var.oidc_google_client_id
      OIDC_GOOGLE_CLIENT_SECRET        = var.oidc_google_client_secret
      OIDC_GOOGLE_CALLBACK_URL         = var.oidc_google_callback_url
      OIDC_GOOGLE_DEFAULT_REDIRECT_URL = var.oidc_google_default_redirect_url

      VERCEL_DEPLOY_HOOK = var.vercel_deploy_hook
    }
  }

  depends_on = [
    aws_cloudwatch_log_group.kottage_app,
    aws_iam_role_policy.kottage_app_logs,
  ]

  tags = {
    Name    = "kottage_app"
    Service = "kottage"
  }
}
