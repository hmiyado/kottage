#!/usr/bin/env sh
set -e
./gradlew clean installDist
docker-compose build
docker-compose up -d
