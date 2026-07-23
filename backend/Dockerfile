# JAVA_DOCKER_TAG はリポジトリルートの mise.toml から
# scripts/mise-java-docker-tag.sh で抽出した値を渡すこと (docker build --build-arg)。
ARG JAVA_DOCKER_TAG=21.0.11_10
FROM eclipse-temurin:${JAVA_DOCKER_TAG}-jdk-ubi9-minimal
EXPOSE 8080:8080
RUN microdnf install -y findutils
RUN mkdir /app
COPY ./build/install/kottage/ /app/
WORKDIR /app/bin
CMD ["./kottage"]
