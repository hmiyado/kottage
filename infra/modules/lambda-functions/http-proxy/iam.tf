resource "aws_iam_role" "http_proxy" {
  name               = "http_proxy"
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

resource "aws_iam_role_policy" "http_proxy" {
  name   = "lambda_kottage_log_role"
  role   = aws_iam_role.http_proxy.id
  policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        "Effect" : "Allow",
        "Action" : "ec2:CreateNetworkInterface",
        "Resource" : "*"
      },
      {
        "Effect" : "Allow",
        "Action" : "ec2:DescribeNetworkInterfaces",
        "Resource" : "*"
      },
      {
        "Effect" : "Allow",
        "Action" : "ec2:AssignPrivateIpAddresses",
        "Resource" : "*"
      },
      {
        "Effect" : "Allow",
        "Action" : "ec2:UnassignPrivateIpAddresses",
        "Resource" : "*"
      },
      {
        "Effect" : "Allow",
        "Action" : "ec2:DeleteNetworkInterface",
        "Resource" : "*"
      }
    ]
  })
}
