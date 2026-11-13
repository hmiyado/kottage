# actions.lock 生成・検証記録

- 対象: `hmiyado/kottage`
- 基点コミット: `2df41b4396eed7034a62a589e7ad43db16bca8b2`
- 記録日時 (UTC): `2026-11-13T04:01:08.123571+00:00`
- ツール: `gh actions-lock v0.4.1`
- 認証: GitHub App トークン (`GH_TOKEN`、値は記録しない)

以下は対象リポジトリのルートで実行。各コマンドの終了コードは `0`。生成・オンライン検証の標準出力と標準エラーを同ディレクトリの JSON / log ファイルに保存した。

```sh
export GH_REPO=hmiyado/kottage
export GH_ACTIONS_LOCK_REPO_ROOT="$PWD"
gh actions-lock --no-interactive --no-narrow --json > docs/actions-lock/generate.json 2> docs/actions-lock/generate.log
gh actions-lock --verify --json > docs/actions-lock/verify.json 2> docs/actions-lock/verify.log
gh actions-lock --verify-local
```

初回生成も `gh actions-lock --no-interactive --no-narrow` で終了コード `0`。続けて上記コマンドで生成結果を記録した。生成・オンライン検証とも `valid: true`、全5ワークフローが `valid: true`。直接13件・推移的1件の依存を収録。ローカル検証も全5ワークフローの収録を確認した。

既存ワークフローの内容は基点コミットから変更していない。警告は既存の SHA 直接指定 (`sha-as-ref`) と、super-linter が使うコンテナの digest 未指定 (`container-image`)。これらは JSON に残しており、検証エラーはない。build / test / lint は後段のゲートで判定する。

検証対象ファイルの SHA-256:

```text
9f0fdb2febaf4f32fba4cdc7734ba9ae6dcec09f53b700ba4a7c63d13c891a4a  .github/workflows/actions.lock
fe9178f5aa66e40b1a18a1fda695b2239fc7e44d59486622da7a62727fa7c7bc  .github/workflows/ci.yml
18f870ee6e08c55813a77e003ce296786afb66d4c5b8fbe675e9609ed6b7e6a2  .github/workflows/claude-fix-renovate.yml
dc7034d09efb52d28f1c3d822ae11143cc48ff212b322d627ae25800df9079c2  .github/workflows/claude-issue-dev.yml
a2113d574950c52dfba12c078e997f7bbccd30e819836a2490e0172b14563650  .github/workflows/claude.yml
3e2ffac79cf35e6331175ac240caae77ca443e1746f824b9b83a866664e9205e  .github/workflows/delivery.yml
```
