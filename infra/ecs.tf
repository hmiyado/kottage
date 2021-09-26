variable "cloudwatch_kottage_api" {
  type = string
  default = "/ecs/awslogs-kottage-api"
}

resource "aws_ecs_cluster" "kottage_api" {
  name = "kottage_api"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  configuration {
    execute_command_configuration {
      logging    = "OVERRIDE"

      log_configuration {
        s3_bucket_name = aws_s3_bucket.log.bucket
        s3_key_prefix = "cluster_kottage_api"
      }
    }
  }
}
resource "aws_ecs_task_definition" "kottage_api" {
  family                = "kottage_api"
  container_definitions = templatefile("task-definitions/service.json", {
    mysql_user     = aws_db_instance.kottage_db.username
    mysql_password = aws_db_instance.kottage_db.password
    mysql_database = aws_db_instance.kottage_db.name
    mysql_host     = aws_db_instance.kottage_db.endpoint
    awslogs_region = "us-east-2"
    awslogs_group = var.cloudwatch_kottage_api
  })

  cpu    = "256"
  memory = "512"

  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"

  execution_role_arn = aws_iam_role.kottage_task.arn
}

resource "aws_ecs_service" "kottage_api" {
  name            = "kottage_api"
  cluster         = aws_ecs_cluster.kottage_api.id
  task_definition = aws_ecs_task_definition.kottage_api.arn
  launch_type     = "FARGATE"
  desired_count   = 1
  //  iam_role        = aws_iam_role.foo.arn
  depends_on = [aws_security_group_rule.kottage]

  //  ordered_placement_strategy {
  //    type  = "binpack"
  //    field = "cpu"
  //  }
  //
  load_balancer {
    target_group_arn = aws_lb_target_group.lb_target_kottage_api.arn
    container_name   = "kottage_api"
    container_port   = 8080
  }

  //  placement_constraints {
  //    type       = "memberOf"
  //    expression = "attribute:ecs.availability-zone in [us-east-2a, us-east-2b]"
  //  }

  network_configuration {
    subnets         = aws_subnet.private.*.id
    security_groups = [aws_security_group.kottage.id]
  }
}

resource "aws_cloudwatch_log_group" "kottage_api" {
  name = var.cloudwatch_kottage_api
}
