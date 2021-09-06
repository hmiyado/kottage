#!/usr/bin/env sh

TAG=miyado/kottage:latest
./gradlew installDist
docker build -t ${TAG} .
docker push ${TAG}
