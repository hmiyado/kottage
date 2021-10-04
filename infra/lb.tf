resource "aws_lb" "lb" {
  name               = "lb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.lb.id]
  subnets            = aws_subnet.public.*.id

  enable_deletion_protection = true

  access_logs {
    bucket  = aws_s3_bucket.log.bucket
    prefix  = "lb"
    enabled = true
  }
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

resource "aws_lb_listener" "https" {
  load_balancer_arn = aws_lb.lb.arn
  port              = "443"
  protocol          = "HTTPS"
  ssl_policy        = "ELBSecurityPolicy-2016-08"
  certificate_arn   = aws_acm_certificate.kottage_miyado_dev.arn

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.lb_target_kottage_api.arn
  }
}
