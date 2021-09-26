resource "aws_s3_bucket" "log" {
  bucket = "kottage-log"
  acl = "public-read-write"

  tags = {
    Name        = "log"
  }
}
