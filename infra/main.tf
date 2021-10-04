terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 3.27"
    }
  }

  required_version = ">= 0.14.9"
}

provider "aws" {
  profile = "default"
  region  = "us-east-2"
}

resource "aws_vpc" "kottage_vpc" {
  cidr_block = "10.0.0.0/16"
}

resource "aws_subnet" "public" {
  count             = 2
  vpc_id            = aws_vpc.kottage_vpc.id
  cidr_block        = "10.0.${count.index * 2}.0/24"
  availability_zone = var.main_availability_zones[count.index]

  tags = {
    Name  = "public"
    Count = count.index
  }
}

resource "aws_subnet" "private" {
  count             = 2
  vpc_id            = aws_vpc.kottage_vpc.id
  cidr_block        = "10.0.${count.index * 2 + 1}.0/24"
  availability_zone = var.main_availability_zones[count.index]

  tags = {
    Name  = "private"
    Count = count.index
  }
}

resource "aws_internet_gateway" "i_gw" {
  vpc_id = aws_vpc.kottage_vpc.id

  tags = {
    Name = "i_gw"
  }
}

resource "aws_eip" "nat_gw" {
  vpc   = true

  tags = {
    Name = "nat_gw_eip"
  }

  depends_on = [
  aws_internet_gateway.i_gw]
}

resource "aws_nat_gateway" "nat_gw" {
  allocation_id = aws_eip.nat_gw.id
  subnet_id     = aws_subnet.public[0].id

  tags = {
    Name   = "nat_gw"
    Subnet = "public0"
  }

  # To ensure proper ordering, it is recommended to add an explicit dependency
  # on the Internet Gateway for the VPC.
  depends_on = [
  aws_internet_gateway.i_gw]
}
resource "aws_route_table" "private" {
  vpc_id = aws_vpc.kottage_vpc.id

  route {
    cidr_block     = "0.0.0.0/0"
    nat_gateway_id = aws_nat_gateway.nat_gw.id
  }

  tags = {
    Name = "private"
  }

  depends_on = [aws_nat_gateway.nat_gw]
}

resource "aws_route_table" "public" {
  vpc_id = aws_vpc.kottage_vpc.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.i_gw.id
  }

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
  count = 2
  subnet_id      = aws_subnet.public[count.index].id
  route_table_id = aws_route_table.public.id
}
