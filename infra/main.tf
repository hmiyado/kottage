terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }

  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = "default"
  region  = "us-east-2"
}

resource "aws_vpc" "test" {
  cidr_block = "10.0.0.0/16"
}

variable "availability_zones" {
  type    = list(string)
  default = ["us-east-2a", "us-east-2b"]
}

resource "aws_subnet" "test" {
  count             = 2
  vpc_id            = aws_vpc.test.id
  cidr_block        = "10.0.${count.index + 1}.0/24"
  availability_zone = var.availability_zones[count.index]

  tags = {
    Name = "develop"
  }
}

resource "aws_internet_gateway" "test" {
  vpc_id = aws_vpc.test.id

  tags = {
    Name = "develop"
  }
}

resource "aws_eip" "nat_gw" {
  vpc = true

  depends_on = [aws_internet_gateway.test]
}

resource "aws_nat_gateway" "test" {
  allocation_id = aws_eip.nat_gw.id
  subnet_id     = aws_subnet.test[0].id

  tags = {
    Name = "gw NAT"
  }

  # To ensure proper ordering, it is recommended to add an explicit dependency
  # on the Internet Gateway for the VPC.
  depends_on = [aws_internet_gateway.test]
}

resource "aws_lb" "test" {
  name               = "test-lb-tf"
  internal           = false
  load_balancer_type = "application"
  //  security_groups    = [aws_security_group.lb_sg.id]
  subnets = aws_subnet.test.*.id

  enable_deletion_protection = false

  //  access_logs {
  //    bucket  = aws_s3_bucket.lb_logs.bucket
  //    prefix  = "test-lb"
  //    enabled = true
  //  }

  tags = {
    Environment = "develop"
  }
}

resource "aws_lb_target_group" "test" {
  name     = "tf-example-lb-tg"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.test.id
}

resource "aws_lb_listener" "front_end" {
  load_balancer_arn = aws_lb.test.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.test.arn
  }
}
resource "aws_ecs_cluster" "test_cluster" {
  name = "test-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }
}

resource "aws_ecs_task_definition" "service" {
  family                = "service"
  container_definitions = file("task-definitions/service.json")
}

resource "aws_ecs_service" "kottage" {
  name            = "kottage"
  cluster         = aws_ecs_cluster.test_cluster.id
  task_definition = aws_ecs_task_definition.service.arn
  //  desired_count   = 3
  //  iam_role        = aws_iam_role.foo.arn
  //  depends_on      = [aws_iam_role_policy.foo]

  ordered_placement_strategy {
    type  = "binpack"
    field = "cpu"
  }

  load_balancer {
    target_group_arn = aws_lb_target_group.test.arn
    container_name   = "kottage"
    container_port   = 8080
  }

  placement_constraints {
    type       = "memberOf"
    expression = "attribute:ecs.availability-zone in [us-east-2a, us-east-2b]"
  }
}
