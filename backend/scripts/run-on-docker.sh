#!/usr/bin/env sh
set -e
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAVA_DOCKER_TAG=$(sh "$SCRIPT_DIR/mise-java-docker-tag.sh")
export JAVA_DOCKER_TAG

./gradlew clean installDist
docker compose build
docker compose up -d
