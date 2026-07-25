resource "aws_instance" "kottage" {
  ami = "ami-0741dc526e1106ae5"
  instance_type = "t2.nano"
  availability_zone = var.main_availability_zones[0]
  subnet_id = aws_subnet.public[0].id

  key_name = aws_key_pair.kottage.key_name
  # ec2_instance_ssh はインスタンスに手作業でアタッチされており、config 側に載っていな
  # かったため、apply するたびに Terraform がデタッチしようとする差分が出ていた。
  # Lambda 移行のフェーズ9で EC2 を撤去するまでは SSH での運用作業 (.env の編集、コンテナ
  # の入れ替え、docker logs の確認) が必要なので、実態に合わせて config 側に明示する。
  vpc_security_group_ids = [
    aws_security_group.ec2_instance.id,
    aws_security_group.ec2_instance_ssh.id
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
