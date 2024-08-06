#!/bin/bash

yum update -y
yum install -y yum-utils device-mapper-persistent-data lvm2 python3-pip git docker

systemctl start docker
systemctl enable docker

if [ ! -f "/usr/local/bin/docker-compose" ]; then
    wget -q https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m) -O /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
fi

git clone https://github.com/mikolajkozlowskiii/tic-tac-toe-game.git