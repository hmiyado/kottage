#!/usr/bin/env sh
# mise.toml (リポジトリルート) の java バージョンから、
# eclipse-temurin の Docker イメージタグ (例: 21.0.11_10) を抽出する。
# 例: java = "temurin-21.0.11+10.0.LTS" -> 21.0.11_10
set -e
SCRIPT_DIR=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
MISE_TOML="$SCRIPT_DIR/../../mise.toml"

grep -E '^java[[:space:]]*=' "$MISE_TOML" \
  | sed -E 's/.*"temurin-([0-9.]+)\+([0-9]+).*/\1_\2/'
