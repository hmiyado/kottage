#!/usr/bin/env sh

TAG=miyado/kottage:latest
./gradlew clean installDist
docker build -t ${TAG} .
docker push ${TAG}
