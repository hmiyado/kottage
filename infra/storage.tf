resource "aws_s3_bucket" "log" {
  bucket        = "kottage-log"
  acl           = "private"
  force_destroy = false

  tags = {
    Name = "log"
  }
}
