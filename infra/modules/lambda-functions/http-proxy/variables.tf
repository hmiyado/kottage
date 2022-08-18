variable "kottage_host" {
  type = string
}

variable "kottage_port" {
  type = string
}

variable "vpc_id" {
  type = string
}

variable "subnet_ids" {
  type = list(string)
}
