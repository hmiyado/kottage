resource "aws_ecr_repository" "kottage" {
  name = "kottage"

  image_scanning_configuration {
    scan_on_push = true
  }

  tags = {
    Name    = "kottage"
    Service = "kottage"
  }
}

resource "aws_ecr_lifecycle_policy" "kottage" {
  repository = aws_ecr_repository.kottage.name

  policy = jsonencode({
    rules = [
      {
        rulePriority = 1
        description  = "Keep only the last 10 tagged images"
        selection = {
          tagStatus      = "tagged"
          tagPatternList = ["*"]
          countType      = "imageCountMoreThan"
          countNumber    = 10
        }
        action = {
          type = "expire"
        }
      }
    ]
  })
}
