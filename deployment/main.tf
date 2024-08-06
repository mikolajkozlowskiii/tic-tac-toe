provider "aws" {
  region  = "us-east-1"
  profile = "default"
}

resource "aws_instance" "tic_tac_toe_game2" {
  ami = "ami-04e5276ebb8451442"
  instance_type = "t2.micro"
  key_name = "tic_tac_toe_key_pair_2"
  associate_public_ip_address = true
  vpc_security_group_ids = [aws_security_group.security_group_tic_tac_toe_game2.id]

  tags = {
    Name = "Tic Tac Toe 2"
  }

  user_data = "${file("install-app.sh")}"
}

resource "aws_vpc" "vpc_tic_tac_toe_game2" {
  cidr_block           = "10.0.0.0/16"
  enable_dns_support   = true
  enable_dns_hostnames = true
  tags = {
    Name = "Vpc Tic Tac Toe 2"
  }
}
resource "aws_subnet" "subnet_tic_tac_toe_game2" {
  vpc_id = aws_vpc.vpc_tic_tac_toe_game2.id
  cidr_block = "10.0.1.0/24"

  tags = {
    Name = "Subnet Tic Tac Toe 2"
  }
}

resource "aws_internet_gateway" "internet_gateway_tic_tac_toe_game2" {
  vpc_id = aws_vpc.vpc_tic_tac_toe_game2.id

  tags = {
    Name = "Gateway Tic Tac Toe 2"
  }
}

resource "aws_route_table" "route_table_tic_tac_toe_game2" {
  vpc_id = aws_vpc.vpc_tic_tac_toe_game2.id

  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.internet_gateway_tic_tac_toe_game2.id
  }

  tags = {
    Name = "Route table Tic-tac-toe 2"
  }

}

resource "aws_route_table_association" "subnet_tic_tac_toe_game2" {
  subnet_id      = aws_subnet.subnet_tic_tac_toe_game2.id
  route_table_id = aws_route_table.route_table_tic_tac_toe_game2.id
}

resource "aws_security_group" "security_group_tic_tac_toe_game2" {
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    description = "SSH"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    description = "Backend"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    description = "Frontend"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
