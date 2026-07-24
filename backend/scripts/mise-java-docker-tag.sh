#!/usr/bin/env sh
# mise.toml (リポジトリルート) の java バージョンから、
# eclipse-temurin の完全な Docker イメージタグ (バージョン部分 + ベースOS部分) を組み立てる。
#
# バージョン部分 (例: 21.0.11_10) は mise.toml から自動抽出できるが、
# ベースOSイメージ (ubi9-minimal / ubi10-minimal 等) は eclipse-temurin 側の
# 都合で Java のメジャーバージョンによって変わることがある
# (例: Java 25 から ubi9-minimal の提供が終了し ubi10-minimal に変更された)。
# これは mise.toml の値からは判断できないため、メジャーバージョンごとに
# 動作確認済みの組み合わせをここで明示的に管理する。
#
# 新しい Java メジャーバージョンに上げるときは、
# https://hub.docker.com/_/eclipse-temurin/tags で実際に存在するタグを確認し、
# 下の case に追記すること (追記しないと即座にエラーで止まる)。
set -e
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MISE_TOML="$SCRIPT_DIR/../../mise.toml"

VERSION_TAG=$(grep -E '^java[[:space:]]*=' "$MISE_TOML" \
  | sed -E 's/.*"temurin-([0-9]+)\.([0-9.]+)\+([0-9]+).*/\1.\2_\3/')
MAJOR_VERSION=$(echo "$VERSION_TAG" | cut -d. -f1)

case "$MAJOR_VERSION" in
  21)
    UBI_VARIANT="ubi9-minimal"
    ;;
  25)
    UBI_VARIANT="ubi10-minimal"
    ;;
  *)
    echo "mise-java-docker-tag.sh: Java メジャーバージョン $MAJOR_VERSION 用の" \
      "eclipse-temurin ベースイメージが未登録です。" \
      "https://hub.docker.com/_/eclipse-temurin/tags で実在するタグを確認し、" \
      "$0 の case 文に追記してください。" >&2
    exit 1
    ;;
esac

echo "${VERSION_TAG}-jdk-${UBI_VARIANT}"
