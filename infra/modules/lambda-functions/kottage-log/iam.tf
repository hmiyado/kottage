resource "aws_iam_role" "lambda_kottage_log" {
  name               = "lambda_kottage_log"
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
}

resource "aws_iam_role_policy" "lambda_kottage_log" {
  name   = "lambda_kottage_log_role"
  role   = aws_iam_role.lambda_kottage_log.id
  policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        "Effect" : "Allow",
        "Action" : "logs:CreateLogGroup",
        "Resource" : "arn:aws:logs:us-east-2:${var.aws_account_id}:*"
      },
      {
        "Effect" : "Allow",
        "Action" : [
          "logs:CreateLogStream",
          "logs:PutLogEvents"
        ],
        "Resource" : [
          "arn:aws:logs:us-east-2:${var.aws_account_id}:log-group:/aws/lambda/kottage-log:*"
        ]
      },
      {
        "Effect" : "Allow",
        "Action" : [
          "s3:Get*",
        ],
        "Resource" : "${var.s3_bucket_arn}/lb/AWSLogs/${var.aws_account_id}/*"
      }
    ]
  })
}
