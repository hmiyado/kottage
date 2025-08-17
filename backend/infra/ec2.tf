resource "aws_instance" "kottage" {
  ami = "ami-051dfed8f67f095f5"
  instance_type = "t2.nano"
  availability_zone = var.main_availability_zones[0]
  subnet_id = aws_subnet.public[0].id

  key_name = aws_key_pair.kottage.key_name
  vpc_security_group_ids = [
    aws_security_group.ec2_instance.id
  ]

  tags = {
    Name = "kottage"
    Service = "kottage"
  }
}

resource "aws_eip" "kottage" {
  domain = "vpc"
}

resource "aws_eip_association" "kottage" {
  instance_id = aws_instance.kottage.id
  allocation_id = aws_eip.kottage.id
}

resource "aws_key_pair" "kottage" {
  key_name   = "kottage"
  public_key = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIMEv8tEv+6AHQO1S6Mbp70SVjz7SdQgErmz2I/ZQHBeM 10195648+hmiyado@users.noreply.github.com"
}
