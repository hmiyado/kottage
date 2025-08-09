resource "aws_db_instance" "kottage_db" {
  allocated_storage           = 20
  engine                      = "mysql"
  engine_version              = "8.4.6"
  instance_class              = "db.t3.micro"
  username                    = var.db_user
  password                    = var.db_password
  parameter_group_name        = "default.mysql8.4"
  skip_final_snapshot         = true
  availability_zone           = var.main_availability_zones[0]
  auto_minor_version_upgrade  = true
  allow_major_version_upgrade = true
  apply_immediately           = true
  vpc_security_group_ids = [aws_security_group.kottage_db.id]
  db_subnet_group_name        = aws_db_subnet_group.kottage_public.name
}

resource "aws_db_subnet_group" "kottage_public" {
  name       = "kottage_public"
  subnet_ids = aws_subnet.public.*.id

  tags = {
    Name = "kottage_public"
  }
}
