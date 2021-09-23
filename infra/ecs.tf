resource "aws_ecs_cluster" "kottage_api" {
  name = "kottage_api"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}
resource "aws_ecs_task_definition" "kottage_api" {
  family                = "kottage_api"
  container_definitions = file("task-definitions/service.json")

  cpu    = "256"
  memory = "512"

  requires_compatibilities = ["FARGATE"]
  network_mode             = "awsvpc"
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
