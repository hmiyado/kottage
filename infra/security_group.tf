resource "aws_security_group" "kottage_db" {
  name   = "kottage_db"
  vpc_id = aws_vpc.kottage_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "MySql:3306"
    from_port   = 3306
    to_port     = 3306
    protocol    = "tcp"
    cidr_blocks = ["10.0.0.0/16"]
  }

  tags = {
    Name = "kottage_db"
  }
}

resource "aws_security_group" "api_gateway" {
  name   = "api_gateway"
  vpc_id = aws_vpc.kottage_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress = [{
    description = "HTTP:80"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = []
    prefix_list_ids = []
    security_groups = []
    self = false
  }, {
    description = "HTTPS:443"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    ipv6_cidr_blocks = []
    prefix_list_ids = []
    security_groups = []
    self = false
  }]

  tags = {
    Name = "api_gateway"
    Service = "kottage"
  }
}

resource "aws_security_group" "ec2_instance" {
  name   = "ec2_instance"
  vpc_id = aws_vpc.kottage_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress = [
    {
      description      = "HTTP"
      from_port        = var.kottage_port
      to_port          = var.kottage_port
      protocol         = "tcp"
      cidr_blocks      = ["10.0.0.0/16"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]

  tags = {
    Name    = "ec2_instance"
    Service = "kottage"
  }
}

resource "aws_security_group" "ec2_instance_ssh" {
  name   = "ec2_instance_ssh"
  vpc_id = aws_vpc.kottage_vpc.id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress = [
    {
      description      = "SSH:22"
      from_port        = 22
      to_port          = 22
      protocol         = "tcp"
      cidr_blocks      = ["0.0.0.0/0"]
      ipv6_cidr_blocks = []
      prefix_list_ids  = []
      security_groups  = []
      self             = false
    }
  ]

  tags = {
    Name    = "ec2_instance_ssh"
    Service = "kottage"
  }
}
