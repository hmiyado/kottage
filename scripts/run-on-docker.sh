#!/usr/bin/env sh
./gradlew clean installDist
docker-compose build
docker-compose up -d
