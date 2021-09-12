resource "aws_security_group" "kottage" {
  name        = "kottage-security-group"
  vpc_id      = aws_vpc.kottage_vpc.id

  # use to pull images from DockerHub
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "develop"
  }
}

resource "aws_security_group_rule" "kottage" {
  security_group_id = aws_security_group.kottage.id

  type = "ingress"

  from_port = 0
  to_port   = 65535
  protocol  = "tcp"

  cidr_blocks = ["10.0.0.0/16"]
}

resource "aws_security_group" "lb" {
  name        = "lb"
  vpc_id      = aws_vpc.kottage_vpc.id

  egress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "develop"
  }
}
