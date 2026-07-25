# JAVA_DOCKER_TAG はリポジトリルートの mise.toml から
# scripts/mise-java-docker-tag.sh で組み立てた値 (バージョン + ベースOS) を
# 渡すこと (docker build --build-arg)。
#
# installDist の出力 (起動スクリプト + jar) を実行するだけなのでコンパイラ等を含む
# JDK は不要。JRE タグを使いイメージサイズを削減する。
#
# eclipse-temurin の各タグは linux/amd64 と linux/arm64 の両方を含むマルチアーキ
# イメージなので、`docker buildx build --platform linux/amd64,linux/arm64` で
# そのままマルチアーキビルドできる。
ARG JAVA_DOCKER_TAG=21.0.11_10-jre-ubi9-minimal
FROM eclipse-temurin:${JAVA_DOCKER_TAG}
EXPOSE 8080
# findutils (find/xargs) はベースイメージに標準で含まれているため追加インストール不要。
# installDist が生成する起動スクリプト (build/install/kottage/bin/kottage) は
# xargs を使用するが、findutils を明示インストールしなくても既に利用可能。
RUN mkdir /app
COPY ./build/install/kottage/ /app/
WORKDIR /app/bin
CMD ["./kottage"]
