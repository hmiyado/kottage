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
  # var.app_image_tagはフェーズ7時点の初期値としてのみ使う。フェーズ8以降は
  # image_uriの実際の更新をCIに委ねるため（下記lifecycle.ignore_changesと
  # migration-plan.mdの8.1を参照）、apply毎にこの変数を書き換える運用は終わる。
  # "latest"は使えない。LambdaはUpdateFunctionCode時点でタグをダイジェストに
  # 解決して固定するため、後から"latest"に新イメージをpushしても稼働中の関数は
  # 変わらず、Terraformの差分にも表れない（詳細はmigration-plan.mdの8.1）。
  # Lambdaはマニフェストリスト（イメージインデックス）を解決できず、単一アーキテクチャの
  # マニフェストを直接指す必要がある。buildxのマルチアーキビルドが作るタグはインデックスを
  # 指すため、タグではなくarm64マニフェストのダイジェストを渡す。
  # "sha256:..." が渡されたら "@" で、通常のタグなら ":" で連結する。
  image_uri = "${aws_ecr_repository.kottage.repository_url}${startswith(var.app_image_tag, "sha256:") ? "@" : ":"}${var.app_image_tag}"

  # フェーズ8: デプロイのたびにTerraformを触らずに済むよう、image_uriの以後の更新は
  # CI（delivery.yml）の `aws lambda update-function-code` に委ねる。Terraformは
  # 初期値を与えるだけにし、CIが更新した後の差分を無視する。
  lifecycle {
    ignore_changes = [image_uri]
  }

  # フェーズ8: バージョンを発行し、aws_lambda_alias経由でAPI Gatewayから
  # 呼び出す（下記）。ロールバックは「エイリアスを前のバージョンに戻す」だけで
  # 完結するようにするための変更。
  publish = true

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

# フェーズ8: API Gatewayはこのエイリアスを呼び出す（本番切替はapi_gateway.tfの
# aws_apigatewayv2_integration.kottageのintegration_uriを参照）。デプロイ後の
# ロールバックは、このエイリアスのfunction_versionを前のバージョンに戻すだけで
# 完結する（terraform applyを介さず `aws lambda update-alias` で即時に行える）。
#
# function_versionはCI（delivery.yml）の `aws lambda publish-version` /
# `aws lambda update-alias` が継続的に更新する。Terraformが管理するのは
# 初期値（最初にpublishされたバージョン）だけであり、以後はignore_changesで
# 差分を無視する。ignore_changesが無いと、CIが切り替えたバージョンをTerraformが
# 次のapplyで元の値に巻き戻してしまう。
resource "aws_lambda_alias" "kottage_app_live" {
  name             = "live"
  function_name    = aws_lambda_function.kottage_app.function_name
  function_version = aws_lambda_function.kottage_app.version

  lifecycle {
    ignore_changes = [function_version]
  }
}

# 既存の aws_lambda_permission.api_gateway（lambda.tf）はhttp_proxy向けのため、
# アプリLambdaのエイリアスを起動する権限は別途必要。qualifierでエイリアス経由の
# 呼び出しのみを許可する。
resource "aws_lambda_permission" "api_gateway_kottage_app" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_alias.kottage_app_live.function_name
  qualifier     = aws_lambda_alias.kottage_app_live.name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.kottage.execution_arn}/*/*"
}
