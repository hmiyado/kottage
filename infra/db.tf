resource "aws_db_instance" "kottage_db" {
  allocated_storage          = 20
  engine                     = "mysql"
  instance_class             = "db.t2.micro"
  name                       = "kottage"
  username                   = var.db_user
  password                   = var.db_password
  parameter_group_name       = "default.mysql8.0"
  skip_final_snapshot        = true
  availability_zone          = var.main_availability_zones[0]
  auto_minor_version_upgrade = true
  vpc_security_group_ids     = [aws_security_group.kottage_db.id]
  db_subnet_group_name       = aws_db_subnet_group.kottage.name
}

resource "aws_db_subnet_group" "kottage" {
  name       = "kottage"
  subnet_ids = aws_subnet.private.*.id

  tags = {
    Name = "kottage"
  }
}
