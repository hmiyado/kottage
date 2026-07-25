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

# Lambda Web Adapter: /opt/extensions/ 配下のLambda拡張はLambdaランタイム以外の
# 環境 (EC2/docker-compose) では起動されないため、このCOPYを追加しても既存の
# EC2上での動作には影響しない。同じイメージがEC2でもLambdaでも動く。
# public.ecr.aws/awsguru/aws-lambda-adapter はマルチアーキ (amd64/arm64) 対応の
# イメージであり、`docker buildx build --platform` のターゲットプラットフォームに
# 応じて COPY --from が自動的に対応アーキテクチャのバイナリを解決する。
# バージョンは https://github.com/awslabs/aws-lambda-web-adapter/releases の
# 最新安定版 v1.0.1 (2026-05-28リリース) に固定。
COPY --from=public.ecr.aws/awsguru/aws-lambda-adapter:1.0.1 /lambda-adapter /opt/extensions/lambda-adapter
# ktor.deployment.port (application.conf) に合わせるポート。
ENV AWS_LWA_PORT=8080
# 既存のdocker-compose healthcheckと同じヘルスチェックパスを流用する。
ENV AWS_LWA_READINESS_CHECK_PATH=/api/v1/health
# JVMの初期化がLambdaのINITフェーズ (10秒制限) を超えても、最初のinvokeに
# 処理を持ち越せるようにする。
ENV AWS_LWA_ASYNC_INIT=true

WORKDIR /app/bin
CMD ["./kottage"]
