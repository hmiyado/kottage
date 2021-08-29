terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 3.27"
    }
  }

  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = "default"
  region = "us-east-2"
}

resource "aws_vpc" "kottage_vpc" {
  cidr_block = "10.0.0.0/16"
}

variable "availability_zones" {
  type = list(string)
  default = [
    "us-east-2a",
    "us-east-2b"]
}

resource "aws_subnet" "public" {
  vpc_id = aws_vpc.kottage_vpc.id
  cidr_block = "10.0.0.0/24"
  availability_zone = var.availability_zones[0]

  tags = {
    Name = "public"
  }
}

resource "aws_subnet" "private" {
  count = 2
  vpc_id = aws_vpc.kottage_vpc.id
  cidr_block = "10.0.${count.index + 1}.0/24"
  availability_zone = var.availability_zones[count.index]

  tags = {
    Name = "private"
  }
}

resource "aws_internet_gateway" "i_gw" {
  vpc_id = aws_vpc.kottage_vpc.id

  tags = {
    Name = "i_gw"
  }
}

resource "aws_eip" "nat_gw" {
  vpc = true

  depends_on = [
    aws_internet_gateway.i_gw]
}

resource "aws_nat_gateway" "nat_gw" {
  allocation_id = aws_eip.nat_gw.id
  subnet_id = aws_subnet.public.id

  tags = {
    Name = "nat_gw"
    Subnet = "public"
  }

  # To ensure proper ordering, it is recommended to add an explicit dependency
  # on the Internet Gateway for the VPC.
  depends_on = [
    aws_internet_gateway.i_gw]
}
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.kottage_vpc.id

  route = [
    {
      cidr_block = "0.0.0.0/0"
      egress_only_gateway_id = ""
      gateway_id = ""
      instance_id = ""
      ipv6_cidr_block = ""
      nat_gateway_id = aws_nat_gateway.nat_gw.id
      network_interface_id = ""
      transit_gateway_id = ""
      vpc_peering_connection_id = ""
      carrier_gateway_id = ""
      destination_prefix_list_id = ""
      vpc_endpoint_id = ""
      local_gateway_id = ""
    }
  ]

  tags = {
    Name = "private"
  }

  depends_on = [aws_nat_gateway.nat_gw]
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.kottage_vpc.id

  route = [
    {
      cidr_block = "0.0.0.0/0"
      egress_only_gateway_id = ""
      gateway_id = aws_internet_gateway.i_gw.id
      instance_id = ""
      ipv6_cidr_block = ""
      nat_gateway_id = ""
      network_interface_id = ""
      transit_gateway_id = ""
      vpc_peering_connection_id = ""
      carrier_gateway_id = ""
      destination_prefix_list_id = ""
      vpc_endpoint_id = ""
      local_gateway_id = ""
    }
  ]

  tags = {
    Name = "public"
  }

  depends_on = [aws_internet_gateway.i_gw]
}

resource "aws_main_route_table_association" "private_route_table" {
  vpc_id         = aws_vpc.kottage_vpc.id
  route_table_id = aws_route_table.private.id
}
resource "aws_route_table_association" "public" {
  subnet_id = aws_subnet.public.id
  route_table_id = aws_route_table.public.id
}
