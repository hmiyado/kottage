#!/usr/bin/env sh

TAG=ghcr.io/hmiyado/kottage:latest
docker build -t ${TAG} .
docker push ${TAG}
