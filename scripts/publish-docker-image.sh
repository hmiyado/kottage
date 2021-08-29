#!/usr/bin/env sh

TAG=miyado/kottage:latest
docker build -t ${TAG} .
docker push ${TAG}
