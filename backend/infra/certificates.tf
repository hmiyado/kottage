resource "aws_acm_certificate" "kottage_miyado_dev" {
  domain_name = "kottage.miyado.dev"

  options {
    certificate_transparency_logging_preference = "ENABLED"
  }

  subject_alternative_names = ["*.kottage.miyado.dev"]
  validation_method         = "DNS"
}
