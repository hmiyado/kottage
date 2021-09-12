resource "aws_lb" "lb" {
  name               = "lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.lb.id]
  subnets            = aws_subnet.public.*.id

  enable_deletion_protection = false

  //  access_logs {
  //    bucket  = aws_s3_bucket.lb_logs.bucket
  //    prefix  = "test-lb"
  //    enabled = true
  //  }
}

resource "aws_lb_target_group" "lb_target_kottage_api" {
  name        = "lb-target-group"
  port        = 8080
  protocol    = "HTTP"
  target_type = "ip"
  vpc_id      = aws_vpc.kottage_vpc.id

  health_check {
    path     = "/health"
    port     = 8080
    protocol = "HTTP"
  }
}

resource "aws_lb_listener" "lb_listener" {
  load_balancer_arn = aws_lb.lb.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.lb_target_kottage_api.arn
  }
}
