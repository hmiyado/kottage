#!/usr/bin/env sh
set -e

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAVA_DOCKER_TAG=$(sh "$SCRIPT_DIR/mise-java-docker-tag.sh")

TAG=miyado/kottage:latest
./gradlew clean installDist
docker build --build-arg JAVA_DOCKER_TAG="$JAVA_DOCKER_TAG" -t ${TAG} .
docker push ${TAG}
