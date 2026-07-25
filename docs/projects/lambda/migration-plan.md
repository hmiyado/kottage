# Lambda移行 - 詳細計画と仕様

## 目次

- [全体方針](#全体方針)
- [事前調査の結果](#事前調査の結果)
- [フェーズ1: 死んだRedisコードの削除](#フェーズ1-死んだredisコードの削除)
- [フェーズ2: セッションOAuth-stateのステートレス化](#フェーズ2-セッションoauth-stateのステートレス化)
- [フェーズ3: DBマイグレーションを起動時処理から分離](#フェーズ3-dbマイグレーションを起動時処理から分離)
- [フェーズ4: コンテナイメージの最適化](#フェーズ4-コンテナイメージの最適化)
- [フェーズ5: ECRリポジトリとGitHub-Actions-OIDC連携](#フェーズ5-ecrリポジトリとgithub-actions-oidc連携)
- [フェーズ6: Lambda-Web-Adapterの組み込み](#フェーズ6-lambda-web-adapterの組み込み)
- [フェーズ7: Lambda関数とテスト用API-Gatewayの構築計測](#フェーズ7-lambda関数とテスト用api-gatewayの構築計測)
- [フェーズ8: 本番切り替え](#フェーズ8-本番切り替え)
- [フェーズ9: EC2EIP撤去](#フェーズ9-ec2eip撤去)
- [リスクとロールバック戦略](#リスクとロールバック戦略)

---

## 全体方針

### 目標
- **ダウンタイム**: なし（API Gatewayの統合先切り替えのみ）
- **リスク管理**: アプリ側の変更はすべて現行EC2上で本番検証してから移行する
- **不可逆性の後置**: EC2/EIPの削除は最終フェーズ。それまでは常にEC2へ即時ロールバックできる

### 戦略: アプリのステートレス化を先行させる

```text
現状:   API GW → Lambda http_proxy → EC2 (Ktor, プロセス内に状態)
中間:   API GW → Lambda http_proxy → EC2 (Ktor, ステートレス)  ← ここまでEC2上で検証
移行後: API GW → Lambda (Ktor, ステートレス)
```

**メリット**:
- ステートレス化の退行（ログイン不可、OAuth失敗）をEC2上で先に検出できる
- Lambda切り替え時に変わるのは「実行環境」だけになり、切り分けが容易
- 同じコンテナイメージがEC2でもLambdaでも動くため、イメージ変更も事前検証できる

---

## 事前調査の結果

計画の前提となる調査結果を記録する。

### Redisは使われていない

| 箇所 | 状態 |
|------|------|
| `AuthenticationModule.kt:18` | `SessionStorageRedis(...)` がコメントアウトされ `SessionStorageMemory()` が使用中 |
| `ApplicationConfigurationModule.kt:32-35` | `JedisPool` はKoinに登録済みだが、Koinの `single` は遅延生成であり `createdAtStart` もない。唯一の利用者 `SessionStorageRedis` が無効なため**一度もインスタンス化されない** |
| `libs.versions.toml:42,73` | `redis = { module = "redis.clients:jedis", ... }` は正常な定義で、`kottage` bundleにも含まれている。つまりjedisはクラスパスに載っており `SessionStorageRedis.kt` はコンパイルされている。**依存は存在するが実行時に到達しない**という状態 |
| `docker-compose.yml` | `redis` コンテナは起動するが誰も接続していない |

→ **ElastiCacheは不要。当初想定した最大の障壁は存在しなかった。**

### プロセス内に状態を持つ箇所（Lambdaでの障壁）

| # | 箇所 | 内容 | Lambdaでの影響 |
|---|------|------|---------------|
| 1 | `Sessions.kt:19` | `UserSession` が `SessionStorageMemory` | 実行環境が増減するとログアウトされる |
| 2 | `Sessions.kt:31` | `ClientSession` が `SessionStorageMemory` | 同上 |
| 3 | `Sessions.kt:43` | `CsrfTokenSession` が `SessionStorageMemory` | CSRF検証が失敗する |
| 4 | `AuthenticationModule.kt:20` | `pre-oauth-states` が `mutableMapOf` | **OAuth開始とコールバックが別環境に着弾するとサインイン失敗**。最も確実に壊れる |
| 5 | `AuthenticationModule.kt:23` | `StatelessHmacNonceManager` の鍵が `generateNonce()` によるプロセス毎のランダム値 | 環境間でnonce検証が不一致になる |

なお `SessionStorage` はKoinの `factory` として登録されているため、`get()` 3回でそれぞれ別インスタンスが生成されている。セッション種別ごとに独立したストアになっている。

### マイグレーションが起動時に走る

`Application.kt:34` の `initializeDatabase(get())` により、Flywayマイグレーションがアプリ起動時に実行される（`repository/Migration.kt`）。Lambdaではコールドスタート毎に実行されることになり、同時実行時の競合とコールドスタート時間の増大を招く。

### その他の確認事項

- `RequestHook` の `MutableList`（`RequestHook.kt:55`）は設定時に構築されるもので、リクエスト間の状態ではない → 問題なし
- `ktor.hooks` の `requestTo` は `VERCEL_DEPLOY_HOOK` への外部HTTPリクエスト → 非VPC Lambdaから問題なく到達する
- CORS許可ホストは `miyado.dev` / `www.miyado.dev`、APIは `kottage.miyado.dev` → 同一登録可能ドメインなので `SameSite=Strict` は維持できる
- `aws_apigatewayv2_vpc_link`（`api_gateway.tf:6`）は統合が `AWS_PROXY`/`INTERNET` のため未使用。HTTP APIのVPC Linkは無料だが不要なリソース

---

## フェーズ1: 死んだRedisコードの削除

**ブランチ**: `feature/phase1-lambda-remove-redis`
**リスク**: 極小（挙動変更なし）
**依存**: なし（即着手可）

### タスク

- [ ] `SessionStorageRedis.kt` を削除
- [ ] `RedisConfiguration.kt` を削除
- [ ] `ApplicationConfigurationModule.kt` から `RedisConfiguration` / `JedisPool` のKoin登録と関連importを削除
- [ ] `AuthenticationModule.kt:18` のコメントアウトされた行を削除
- [ ] `application.conf` の `redis { ... }` ブロックを削除
- [ ] `libs.versions.toml` の `redis` 定義（42行目）と `kottage` bundleからの参照（73行目）を削除
      → jedisがクラスパスから外れるため、`SessionStorageRedis.kt` の削除と同時に行う必要がある
- [ ] `docker-compose.yml` から `redis` サービスと `web` の `depends_on: redis` を削除
- [ ] `.ci.env` から `REDIS_HOST` を削除（`.env`, `.db-env` にも存在すれば同様）

### 成功条件

- [ ] `./gradlew build` が通る
- [ ] 既存のテストがすべてパスする
- [ ] `docker compose up` でローカル環境が起動する
- [ ] 挙動の変更が一切ないこと（`SessionStorageMemory` のまま）

---

## フェーズ2: セッション/OAuth stateのステートレス化

**ブランチ**: `feature/phase2-lambda-stateless-session`
**リスク**: **高**（認証機能全体に影響）
**依存**: フェーズ1（`AuthenticationModule.kt` / `application.conf` で競合するためrebase前提）

このフェーズが本プロジェクトの技術的な核心。**現行EC2にデプロイして本番検証まで行う。**

### 2.1 署名鍵の導入と保管

**現状**: セッション用の署名鍵は**存在しない**。サーバー側メモリ保持のため、
cookieにはKtorが生成した不透明なIDしか入っておらず、署名の必要がなかった。

唯一存在するHMAC鍵は `StatelessHmacNonceManager(key = generateNonce().toByteArray())`
（`AuthenticationModule.kt:23`）だが、これは**プロセス起動ごとにランダム生成**され
メモリ上にのみ存在する。副作用として**アプリ再起動を跨いだOAuthフローは現在も失敗する**
（既存の軽微なバグ）。2.6の固定化で解消される。

タスク:

- [ ] 環境変数 `SESSION_SIGN_KEY`（16進文字列）を追加し `application.conf` に配線
- [ ] 起動時に未設定・短すぎる場合はfail fastさせる（本番で無防備な鍵が使われるのを防ぐ）
- [ ] ローカル開発用のデフォルト値を `.ci.env` 等に用意

#### 鍵のライフサイクル

| 項目 | 内容 |
|------|------|
| 生成 | `openssl rand -hex 32`（256bit。HmacSHA256に対応） |
| source of truth | `backend/infra/sensitive.tfvars`（gitignore済み・git追跡外を確認済み） |
| フェーズ2〜8（EC2稼働中） | EC2上の `.env` に手動配置（DB認証情報と同じ運用。gitignore済み） |
| フェーズ7以降（Lambda） | Terraformが `sensitive.tfvars` から読み、Lambda環境変数に設定 |
| 緊急時の全セッション失効 | 鍵をローテーションする（全トークンが即座に無効化される） |

> **移行期の必須条件**: フェーズ2でEC2の `.env` に置いた鍵と、フェーズ7でLambda環境変数に
> 設定する鍵は**同一の値でなければならない**。異なるとフェーズ8の切り替え時点で
> 全ユーザーがログアウトされる。

#### 決定: AWS Secrets Managerは使わない

Secrets Managerは1シークレットあたり**$0.40/月**かかり、本プロジェクトの削減額$3.65/月の
11%を鍵1本で消費してしまう。Lambda環境変数は既定でKMS暗号化されて保存されるため、
個人ブログの要件には十分であり、既存の `sensitive.tfvars` 運用とも一貫する。
無料の代替としてSSM Parameter StoreのSecureString（標準パラメータ）も選択肢に残す。

### 2.2 3種のセッションをステートレス化

`Sessions.kt` の3つの定義から `storage` 引数を外し、署名変換を付与する。

- [ ] `UserSession`（cookie）
- [ ] `ClientSession`（cookie）
- [ ] `CsrfTokenSession`（header）

```kotlin
cookie<UserSession>("user_session") {
    cookie.httpOnly = false
    cookie.extensions["SameSite"] = "Strict"
    cookie.maxAgeInSeconds = sessionExpiration.seconds
    transform(SessionTransportTransformerMessageAuthentication(signKey))
}
```

- [ ] `AuthenticationModule.kt` の `factory<SessionStorage>` 登録を削除

**cookieサイズ**: 3種いずれも数十バイト程度であり4KB上限に対して余裕がある。

#### 決定: 暗号化はせずMACのみとする

セッションの中身は以下の通りで、いずれも秘匿性を必要としない。

```kotlin
data class UserSession(val id: Long = 0)                                // ユーザーIDのみ
data class ClientSession(val token: String)
data class CsrfTokenSession(val token: String, val clientSession: ClientSession)
```

- `UserSession.id` はAPIが通常返す値であり秘密ではない
- CSRFトークンは `X-CSRF-TOKEN` ヘッダでクライアントに渡している値であり、隠す意味がない
- 暗号化すると鍵が2本（暗号鍵・署名鍵）になり管理コストだけが増える

なお `com.auth0:java-jwt` は既に依存に含まれるが、これはGoogleのOIDC IDトークン検証用
（`service/oauth/OauthGoogleService.kt`）であり自前セッションとは無関係。**JWTは導入しない。**
Ktorのセッション機構を維持する方が変更範囲が小さい。

### 2.3 有効期限の署名（ステートレス化で新たに開く穴への対処）

Ktorの `SessionTransportTransformerMessageAuthentication` はペイロードに署名するだけで、
**有効期限を持たない**。期限はcookieの `maxAge` に依存するが、これはクライアント側の値であり
サーバーは強制できない。結果として、一度発行された正当なトークンは**無期限に再利用可能**になる。

現状の `SessionStorageMemory` はサーバー側にエントリを持つためこの穴は存在しない。
つまりステートレス化によって**新たに開く穴**であり、必ず対処する。

- [ ] `UserSession` に有効期限フィールドを追加し、**署名対象のペイロードに含める**

```kotlin
data class UserSession(val id: Long = 0, val expiresAt: Long = 0)
```

- [ ] サーバー側で署名検証後に `expiresAt` を検証し、期限切れなら未認証として扱う
- [ ] **絶対期限**とする（サインイン時に7日後を刻み、以後更新しない）
      — 現状の `sessionExpiration = Duration.ofDays(7)` の挙動に一致させる。
      スライディング期限は漏洩トークンを延命させるため採用しない

**この対処で閉じる穴**:

| 脅威 | 対処後 |
|------|--------|
| 期限を改ざんして延長する | ✅ 防げる（payloadが変わりMACが不一致になる） |
| 期限切れトークンを無期限に再利用する | ✅ 防げる（署名済みexpiresAtで判定） |
| 有効期間内に漏洩したトークンを使われる | ❌ 原理的に防げない（トークンが正当なため） |
| サインアウトで即座に無効化する | ❌ 原理的に防げない |

下2つはステートレス方式（JWTを含む）に共通のトレードオフであり、
失効リストを持たない限り解決しない。**緊急時の全セッション失効手段として
`SESSION_SIGN_KEY` のローテーションを用意する**（鍵を変えれば全トークンが無効化される）。

### 2.4 cookie属性の修正（Secure / httpOnly）

現状 `Sessions.kt` の3定義すべてで `cookie.secure = false` / `cookie.httpOnly = false` になっている。
ステートレス化によりcookieは「不透明なセッションID」から「署名済みの資格情報そのもの」に変わり、
盗難時の影響が構造的に大きくなる（`expiresAt` まで有効で失効不能）。したがって同時に修正する。

- [ ] Production時に `cookie.secure = true` にする
- [ ] `cookie.httpOnly = true` にする
- [ ] 既存のTODOコメント（`Sessions.kt:25-27` 等）を削除する

#### `secure = true` が安全である根拠

既存のTODOコメントは「lb -> app is http なのでsecureにできない」としているが、**これは誤診である**。
`Secure` 属性はブラウザ向けの指示であり、効くのは**ブラウザ ↔ API Gatewayの区間のみ**。
この区間はACM証明書によるHTTPS（TLS 1.2必須）であり、内部のHTTP区間はブラウザから見えない。

```text
ブラウザ ──HTTPS(ACM)──> API Gateway ──HTTP──> Lambda ──HTTP──> EC2
         ↑ Secureが効くのはここだけ    ↑ ブラウザはこの区間を認識しない
```

#### `httpOnly = true` が安全である根拠

フロントエンドを調査した結果、**JavaScriptはcookieを一切読んでいない**。

| 確認項目 | 結果 |
|---------|------|
| `user_session` / `client_session` / `document.cookie` の参照 | **0件** |
| CSRFトークンの取得元 | レスポンスヘッダ（`frontend/src/repository/kottageClient.ts:36`）。cookieではないため `httpOnly` の影響を受けない |
| cookieの送信方法 | `credentials: 'include'` によりブラウザが自動送信（同 `:93`） |

- [ ] 変更後、実ブラウザでサインイン・記事投稿・CSRF再試行が動作することを確認する

### 2.5 OAuth stateのステートレス化

- [ ] `PreOauthState`（`redirectUrl`, `userId`, `nonce` の3フィールド）をシリアライズしHMAC署名したトークンを、OAuthの `state` パラメータ自体に埋め込む
- [ ] コールバック側で署名検証してデコードする
- [ ] `pre-oauth-states` のKoin登録（`AuthenticationModule.kt:20`）を削除
- [ ] `RouteModule.kt:28`, `Application.kt:59`, `RoutingTest.kt:83` の参照を更新
- [ ] stateトークンに有効期限を含め、リプレイを防ぐ

### 2.6 nonce鍵の固定化

- [ ] `StatelessHmacNonceManager` の `key` を `SESSION_SIGN_KEY` 由来の固定値に変更

### 成功条件

- [ ] `./gradlew build` とテストがパスする
- [ ] ローカルでサインイン → リロード → サインアウトが正常動作
- [ ] ローカルでGoogle OAuthサインインが成功する
- [ ] CSRFトークンを要求するエンドポイント（POST/PATCH/DELETE）が動作する
- [ ] **アプリを再起動してもセッションが維持される**（ステートレス化できた証明）
- [ ] 本番EC2にデプロイし、実ブラウザでサインイン・OAuth・記事投稿を確認

### 補足: 既存ユーザーへの影響

現状 `SessionStorageMemory` のためデプロイごとに全セッションが失われている。
したがってステートレス化による「一度のログアウト」は退行ではなく、むしろ改善となる。

---

## フェーズ3: DBマイグレーションを起動時処理から分離

**ブランチ**: `feature/phase3-lambda-separate-migration`
**リスク**: 中（デプロイ順序に依存が生まれる）
**依存**: なし（フェーズ2と並行可能）

### タスク

- [ ] `CliEntrypoint.kt` に `migrate` サブコマンドを追加し、マイグレーションのみ実行して終了できるようにする
- [ ] `Application.kt:34` の `initializeDatabase(get())` を環境変数 `RUN_MIGRATION_ON_STARTUP` でガードする
      （ローカル開発の利便性を保つためデフォルトは `true`、本番Lambdaでは `false`）
- [ ] `delivery.yml` にデプロイ前のマイグレーション実行ステップを追加
- [ ] マイグレーション失敗時はデプロイを中止する

### 成功条件

- [ ] `migrate` サブコマンドが単体で成功する
- [ ] `RUN_MIGRATION_ON_STARTUP=false` でアプリが起動し、マイグレーションを実行しない
- [ ] `RUN_MIGRATION_ON_STARTUP` 未設定時は従来通り起動時に実行される
- [ ] 既存のテストがパスする

---

## フェーズ4: コンテナイメージの最適化

**ブランチ**: `feature/phase4-lambda-slim-image`
**リスク**: 低
**依存**: なし（即着手可）

現状の `miyado/kottage:latest` は展開後511MB。`installDist` の出力を動かすだけならJDKは不要。

### タスク

- [ ] ベースイメージを `jdk` から `jre` に変更（`mise-java-docker-tag.sh` が組み立てるタグの見直しを含む）
- [ ] `microdnf install findutils` が起動スクリプトに本当に必要か確認し、不要なら削除
- [ ] 不要な `EXPOSE 8080:8080` の記述を整理
- [ ] **arm64（Graviton）へ移行する**（承認済みの決定事項。Lambda料金が2割安くなる）
      — `eclipse-temurin` のarm64対応タグを使用し、`mise-java-docker-tag.sh` の出力を確認する
      — 現行EC2は `t2.nano`（x86_64）のため、arm64イメージはEC2では動作しない。
        したがって**フェーズ4以降のイメージ検証はEC2では行えず、ローカル（Apple Silicon）と
        フェーズ7のLambda上で行う**。フェーズ6の「EC2で事前検証する」前提はarm64採用により
        成立しなくなる点に注意
      — もしくはマルチアーキテクチャイメージ（buildxで amd64/arm64 両対応）としてpushし、
        EC2での検証経路を維持する。**こちらを推奨**

### 成功条件

- [ ] イメージサイズが有意に縮小する（目標: 300MB以下）
- [ ] `docker compose up` でアプリが正常に起動する
- [ ] `curl /api/v1/health` が成功する
- [ ] 本番EC2で正常動作する

---

## フェーズ5: ECRリポジトリとGitHub Actions OIDC連携

**ブランチ**: `feature/phase5-lambda-ecr`
**リスク**: 低（追加のみ。既存のDocker Hub経路は維持）
**依存**: なし（即着手可）

### タスク

#### Terraform

- [ ] `aws_ecr_repository` を追加（`ecr.tf`）
- [ ] `aws_ecr_lifecycle_policy` で直近10世代のみ保持
- [ ] GitHub Actions用の `aws_iam_openid_connect_provider`（`token.actions.githubusercontent.com`）を追加
- [ ] ECR push権限を持つIAMロールを追加。信頼ポリシーは `hmiyado/kottage` リポジトリに限定する

#### CI

- [ ] `delivery.yml` に `aws-actions/configure-aws-credentials`（OIDC）と `aws-actions/amazon-ecr-login` を追加
- [ ] **Docker HubとECRの両方にpushする**（移行期間中はEC2がDocker Hubから取得し続けるため）
- [ ] `permissions: id-token: write` をジョブに付与

### 成功条件

- [ ] `terraform apply` が成功しECRリポジトリが作成される
- [ ] `delivery.yml` の実行でECRに `:latest` と `:${version}` がpushされる
- [ ] 既存のDocker Hub pushが引き続き成功する
- [ ] ライフサイクルポリシーが設定されていることをコンソールで確認

---

## フェーズ6: Lambda Web Adapterの組み込み

**ブランチ**: `feature/phase6-lambda-web-adapter`
**リスク**: 低（Lambda外では拡張が無視されるため）
**依存**: フェーズ4（`Dockerfile` で競合）、フェーズ5（push先が必要）

### 設計の要点

Lambda Web Adapterは `/opt/extensions/` に置かれるLambda拡張であり、
**Lambdaランタイム以外の環境では起動されない**。したがって同じイメージがEC2でもLambdaでも動く。
これによりイメージ変更を本番EC2で事前検証できる。

### タスク

- [ ] `Dockerfile` にアダプタのCOPYと環境変数を追加

```dockerfile
COPY --from=public.ecr.aws/awsguru/aws-lambda-adapter:0.9.1 /lambda-adapter /opt/extensions/lambda-adapter
ENV AWS_LWA_PORT=8080
ENV AWS_LWA_READINESS_CHECK_PATH=/api/v1/health
ENV AWS_LWA_ASYNC_INIT=true
```

- [ ] アダプタの最新バージョンを確認して固定する（`0.9.1` は暫定値）
- [ ] **arm64版のアダプタを使う**（フェーズ4でarm64採用を決定済み）
- [ ] `AWS_LWA_ASYNC_INIT=true` によりJVMの初期化が10秒のINIT枠を超えても継続できることを確認

### 成功条件

- [ ] イメージがビルドできる
- [ ] **同じイメージが `docker compose up` で従来通り起動する**（EC2での動作を壊していない証明）
- [ ] 本番EC2にデプロイして正常動作する
- [ ] ECRにpushされる

---

## フェーズ7: Lambda関数とテスト用API Gatewayの構築＋計測

**ブランチ**: `feature/phase7-lambda-function`
**リスク**: 低（本番経路には一切触れない）
**依存**: フェーズ2, 3, 5, 6

> **訂正（2026-07-25）**: 当初「依存: フェーズ5, 6」としていたが不正確だった。
> フェーズ2（セッション/OAuth stateのステートレス化）とフェーズ3
> （マイグレーションの起動時処理からの分離）も実質的な依存である。
>
> - **フェーズ2への依存**: 7.6の機能検証（サインイン・Google OAuth・CSRF）が
>   意味を持つのは、セッション/OAuth stateがステートレス化された後だけである。
>   ステートレス化前のコードをLambda上で動かすと、実行環境が複数並走・入れ替わる
>   ことで確実に失敗し、検証にならない
> - **フェーズ3への依存**: 7.5のコールドスタート計測は、起動時にFlyway
>   マイグレーションが走らない（`RUN_MIGRATION_ON_STARTUP=false`）状態で行わないと
>   数値が汚染される。フェーズ3なしでは計測結果がマイグレーション込みの時間になり、
>   フェーズ8以降での実測と乖離する
>
> フェーズ5・6への依存（ECRリポジトリ・Lambda Web Adapter組み込み済みのイメージが
> 必要）は従来通り。

本番のAPI Gatewayとは**別のHTTP API**を作り、そこで完全に検証する。

### 7.1 Terraform

- [x] `aws_lambda_function`（`package_type = "Image"`, `image_uri` はECR）を追加。**`vpc_config` は付けない**
- [x] メモリは1024MBから開始し、計測結果で調整
- [x] `timeout` は29秒以下（API Gateway HTTP APIの統合タイムアウト上限が30秒）
- [x] 環境変数を設定: DB接続情報、`SESSION_SIGN_KEY`、OAuth関連、`VERCEL_DEPLOY_HOOK`、`RUN_MIGRATION_ON_STARTUP=false`、`DEVELOPMENT=false`
- [x] `aws_cloudwatch_log_group` を明示的に作成し保持期間を設定（14日程度）
実装は `backend/infra/lambda_app.tf`（Lambda本体・ロググループ・IAMロール）。
環境変数の完全な一覧は本フェーズの7.7節を参照。
**計測・`terraform apply`は未実施**（人間が実施する）。

#### 検証用エンドポイントはリポジトリに入れない

当初は検証用の `aws_apigatewayv2_api` を別途定義する計画だったが、取りやめた。
API Gatewayは1つ作るのに5リソース（api / integration / stage / route / lambda_permission）
必要で、フェーズ8で本番切り替えをしたら不要になる。**削除するためだけのPRがもう1本必要**に
なるうえ、リポジトリに本番構成ではないものが混ざって読み手を混乱させる。

一方で、**手作業でコンソールから作るのも避ける**。stateに存在しないリソースが
できると孤児化する。実際にこのプロジェクトでは、state を失った状態でapplyされた結果
API Gateway・ACM証明書・VPC linkが二重に作られ、2021〜2022年世代のリソースが
長期間放置されていた（本フェーズ着手時に import と削除で解消済み）。

したがって「**コミットはしないがTerraformでは管理する**」形をとる。

```text
1. 検証用の .tf を作る（コミットしない）
2. terraform apply → 検証
3. ファイルを削除して terraform apply → Terraformが片付ける
```

`terraform.tfstate` はローカルファイルでgitignore済みなので、これで完結し孤児も残らない。

##### 検証手段の使い分け

| 検証内容 | 手段 | 理由 |
|---|---|---|
| **コールドスタート計測** | `aws lambda invoke` に API GW v2形式の合成ペイロードを渡す | HTTPエンドポイント自体が不要。API Gatewayのオーバーヘッドを除いた**純粋なLambdaのコールドスタート**が測れるため、判断ゲートの数値として正確 |
| **機能検証**（cookie / CORS / OAuth） | `aws_lambda_function_url` を一時的に作成 | HTTPが必要。ただしAPI Gateway 5リソースではなく**1リソースで足りる**。無料で、ペイロード形式は API GW HTTP API と同じ2.0なのでLambda Web Adapterの挙動は変わらない。安定したURLが付くのでOAuthのコールバックURLとしてGoogle Cloud Consoleに登録できる |

### 7.2 マイグレーション実行の設計（フェーズ3で先送りした分）

フェーズ3のPRでは `migrate` サブコマンドの追加までに留め、CI/CDからどう呼び出すかは
本フェーズに先送りしていた。以下の流れを採用する。

```text
1. イメージをビルドしてECRにpush
2. マイグレーション用LambdaをOIDCで invoke（migrateサブコマンドを実行するイメージ）
3. マイグレーション成功を確認したら、アプリLambdaをPublishVersion し、
   エイリアス（例: kottage_app:live）の向き先を新バージョンに切り替える
```

この設計のメリット:

- **GitHub SecretsにDB認証情報を置かずに済む**。マイグレーションはAWS内（Lambda）で
  実行され、DBはAWS内からのみ到達可能なまま維持できる。CI側はLambdaを起動する
  IAM権限（`lambda:InvokeFunction`）だけ持てばよい
- **ダウンタイムの窓がマイグレーション実行時間＋数秒に縮まる**。現行EC2の
  `docker compose down && up`（起動時マイグレーション込み）よりも短くなる

このマイグレーション呼び出し用Lambda・エイリアス・関連IAMロールのTerraformは
**未実装**（本PRのスコープ外）。フェーズ8着手前に実装する。

### 7.3 expand/contractの規律（マイグレーションとデプロイの非アトミック性）

マイグレーションの適用とアプリのデプロイは、DBと実行環境が別システムである以上、
原理的に同一トランザクションにできない。現行のEC2構成は、これを
「ダウンタイムで実質的にアトミックに見せる」ことで回避している。
`docker compose down` でアプリを止めてからマイグレーション込みで起動し直すため、
利用者からは新スキーマと新コードが同時に切り替わる瞬間しか見えない。

Lambdaではこの「止めてから入れ替える」動作を再現できない。7.2の設計を採用しても、
マイグレーション実行からエイリアス切り替えまでの間、旧バージョンのLambdaが新スキーマに
対して稼働し続ける時間が生まれる（切り替え後に旧バージョンへロールバックした場合は、
逆に新コードが旧スキーマに対して稼働する時間が生まれる）。

したがって**expand/contractの規律**が本質的な対策になる。「マイグレーションNは、
アプリN-1とアプリNの両方から見て後方互換であること」を必須要件とする。具体的には:

- カラム削除・NOT NULL化・型変更などの破壊的な変更は、複数段階
  （expand → 移行 → contract）に分けて行う
- 1回のデプロイに含めるマイグレーションは「追加のみ」（カラム追加・テーブル追加・
  インデックス追加）を基本とする
- 破壊的な変更がどうしても必要な場合は、旧コードが新スキーマを読んでも壊れないことを
  レビュー時に明示的に確認する

### 7.4 DB接続のfreeze/thaw対策の検討

> **調査の結果、当初の想定と異なることが判明した。**
> `backend/src/main/kotlin/com/github/hmiyado/kottage/repository/Database.kt` の
> `DatabaseConfiguration.MySql.connect()` はExposedの
> `Database.connect(url, driver, user, password)` オーバーロードをそのまま
> 呼んでいるだけで、**HikariCPは導入されていない**。したがって「HikariCPの
> 最大プールサイズを1〜2に絞る」という当初のタスクは、そのままでは適用対象が
> 存在しない。
>
> このオーバーロードでのExposedの接続は、`transaction { }` ブロックの実行のたびに
> `DriverManager.getConnection()` で素のJDBC接続を確立し、ブロックの終了時に
> 切断する（コネクションプーリングを行わない）。つまり**Lambdaの呼び出しをまたいで
> 保持され続けるDBコネクションは存在しない**。freeze中にTiDB側がアイドル接続を
> 切断しても、次のリクエストは新しいTCP接続を張り直すだけなので、当初懸念していた
> 「freezeで壊れた古い接続を掴んで失敗する」という故障モードは原理的に発生しない。
>
> 代わりに実在するトレードオフは、**リクエスト毎にTCPハンドシェイク＋TLS
> ネゴシエーション（`sslMode=REQUIRED`）が発生するレイテンシコスト**である。
> これは7.5のp50/p95計測にそのまま現れるはずなので、そこで許容範囲かどうかを
> 判断する。
>
> **判断**: 現時点でのKotlinコードの変更は不要と判断する。計測の結果レイテンシが
> 問題になった場合は、以下を将来のタスクとして検討する（本PRのスコープ外、
> 実装しない）:
>
> - `HikariDataSource` を導入し、Exposedの `Database.connect(datasource = ...)`
>   オーバーロードに切り替える
> - `maximumPoolSize` を1〜2程度に絞る（Lambdaの同時実行数の分だけ実行環境が
>   増える点に注意。1環境あたりのプールサイズであり全体の上限ではない）
> - `idleTimeout` をfreezeが起きやすい間隔より短く設定し、freeze中に無効化された
>   接続をプールに残さないようにする
> - `connectionTestQuery` 相当のバリデーションを有効化し、borrow時に死んだ接続を
>   検知する
>
> これを採用する場合、プーリングを持ち込むこと自体が新たなfreeze/thawリスク
> （プール内の接続がfreeze中に切断される）を持ち込む点に注意し、上記設定と
> セットで導入すること。

### 7.5 計測（判断ゲート）

- [x] コールドスタート時間を計測（メモリ1024 / 1769 / 2048MBで比較）
- [x] ウォーム時のレイテンシを計測
- [x] 実行時間から月間コストを試算し無料枠に収まることを確認

**判断: Lambdaで進める。メモリは1769MBを採用する。**

計測は `aws lambda invoke` にAPI Gateway v2形式の合成ペイロードを渡して実施し、
数値はCloudWatch Logsの `REPORT` 行（`Init Duration` / `Duration`）から取得した。
API Gatewayを経由しないため、Lambda自身のコールドスタートを分離して測れている。

#### コールドスタート

| メモリ | Init Duration | ウォーム時 Duration（DBアクセスなし） |
|---|---|---|
| 1024MB | 9,823 ms | 10 ms |
| **1769MB** | **6,033 ms** | **6 ms** |
| 2048MB | 5,989 ms | 6 ms |

1769MBで1vCPU相当に達するため、それ以上メモリを積んでも改善しない。
**約6秒がこのアプリのコールドスタートの下限**と考えてよい。

なお1024MBの初回は `Duration 2,762ms + Init 9,823ms` で12.5秒課金された。
これは `AWS_LWA_ASYNC_INIT=true` がINITフェーズの10秒制限を超えた初期化を
最初のinvokeに持ち越した結果であり、設計通りの挙動。

#### ウォーム時レイテンシ（DBアクセスあり）と現行構成との比較

> **【訂正】この節の当初の記述は誤っていた。** 「Lambdaの方が半分以下に速い」と
> 判断ゲートの決め手として記録していたが、**Lambdaはサーバ側Duration、EC2は
> end-to-endという、単位の異なる値を比較していた**。フェーズ8の本番切替後に
> 同一条件で測り直した結果を以下に記す。当初の結論は成立しない。

`/api/v1/entries`（COUNT + SELECT、180件）で計測。

##### 同一条件（end-to-end、同一計測地点）での比較

| 経路 | 中央値 |
|---|---|
| 切替前EC2（API GW → プロキシLambda → EC2 → TiDB） | 1,328 ms |
| 切替後Lambda（API GW → Lambda → TiDB） | 1,293 ms |

**差は約35ms。誤差の範囲であり、体感できる改善ではない。**

##### 内訳

end-to-endの値には計測地点（日本）からus-east-2までのRTTと、リクエスト毎の
TLSハンドシェイクが含まれる。この分が約540msあり、**両経路が等しく負っている**。

| 区間 | 値 |
|------|-----|
| 計測環境（ネットワーク往復 + TLS） | 約540 ms |
| Lambdaのサーバ側Duration（CloudWatch REPORT） | 554〜778 ms |
| EC2のサーバ側（end-to-endからの逆算） | 約790 ms |

サーバ側だけを見れば改善はあるが（約790ms → 約570ms）、**end-to-endでは
ネットワーク往復が支配的で、利用者から見た差はほぼ無い**。

##### 判断への影響

本移行の主目的はコスト削減（$8.6/月）であり、そこは変わらず達成される。
レイテンシは「改善する」ではなく「悪化しない」が正しい評価。判断ゲートの
結論自体は変わらないが、**根拠として挙げた「半減」は誤り**だった。

##### 教訓

サーバ側の実行時間と、利用者から見たend-to-endのレイテンシは別物であり、
比較するなら計測地点と含まれる区間を揃えなければならない。CloudWatchの
`Duration` はLambda内部の実行時間のみで、API Gatewayの処理もネットワークも
含まない。

#### 接続プール不在のコスト（移行後の最適化テーマ）

同じウォーム状態でも、DBアクセスなし（6 ms）とDBアクセスあり（570 ms）で
2桁の差がある。7.4の通り接続プールが無く、`transaction {}` ごとにTCP+TLS
ハンドシェイクとMySQL認証をクロスリージョン（us-east-2 → us-east-1）で
張り直しているため。`/entries` はCOUNTとSELECTで2トランザクション走るため、
1接続あたり約285msと見積もられる。

**これは現行EC2でも同じコストを払っており、Lambda固有の問題ではない**ので
移行の判断材料からは除外する。一方でLambdaは実行環境が再利用されるため
接続プールが有効に効く。移行後の最適化テーマとして価値が高い。

#### コスト試算

無料枠は月100万リクエスト + 40万GB秒。1769MB（1.73GB）・ウォーム6msで
1リクエストあたり約0.0104 GB秒。**40万GB秒は約3,800万リクエスト相当**にあたり、
コールドスタートを多めに見積もっても無料枠に十分収まる。

#### トレードオフの整理

| 指標 | 評価 |
|---|---|
| **削減額** | ✅ **$8.6/月（年$103）。これが移行の主目的** |
| コスト | ✅ 無料枠内 |
| 運用負荷 | ✅ EC2の死活監視（`ec2-monitoring-checklist.md`）が不要になる |
| ウォーム時レイテンシ | ➖ ほぼ同等（1,328 → 1,293 ms）。**改善ではない**（訂正済み。上記参照） |
| コールドスタート | ⚠️ 唯一の劣化点。影響を受けるのは「しばらくアクセスが無かった後の最初の1リクエスト」のみ |

**判断はコスト削減と運用負荷の解消で決まる。** レイテンシは当初「半減する」と
記録していたが誤りで、正しくは「悪化しない」。案B（Lightsail）はコールドスタートが
無い代わりに削減幅が小さく（$3.5〜5/月が残る）、EC2の運用も続く。
したがってLambdaで進める。

### 7.6 機能検証

- [ ] `/api/v1/health` が成功する
- [ ] 記事一覧・記事詳細の取得
- [ ] サインイン → セッション維持 → サインアウト
- [ ] Google OAuthサインイン（**フェーズ2の成果が効く箇所**）
- [ ] CSRFトークンを要する更新系操作
- [ ] `requestHook` によるVercelデプロイフックの発火

### 7.7 環境変数の完全な一覧

`backend/src/main/resources/application.conf` から抽出した、アプリが読む環境変数の
全18種類。フェーズ9でEC2を削除すると`.env`/`.db-env`が失われるため、ここに記録する。

| 環境変数 | application.confのデフォルト | Lambdaでの扱い |
|---|---|---|
| `DEVELOPMENT` | `true` | `false` を明示 |
| `PORT` | `8080` | `8080`（Lambda Web Adapterの`AWS_LWA_PORT`と一致させる） |
| `VERSION` | `""` | ECRイメージタグ（`var.app_image_tag`）を流用する |
| `MYSQL_DATABASE` | `""` | TiDB接続情報。`sensitive.tfvars`の`mysql_database`から |
| `MYSQL_HOST` | `""` | 同上。`mysql_host`から |
| `MYSQL_PORT` | `3306` | 同上。`mysql_port`（Terraform側のデフォルトは`4000`） |
| `MYSQL_USER` | `""` | 同上。`mysql_user`から |
| `MYSQL_PASSWORD` | `""` | 同上。`mysql_password`から |
| `MYSQL_SSL_MODE` | `DISABLED` | 同上。`mysql_ssl_mode`（Terraform側のデフォルトは`REQUIRED`） |
| `RUN_MIGRATION_ON_STARTUP` | `true` | `false`を明示（フェーズ3で分離済み。コールドスタート毎のFlyway実行を避ける） |
| `SESSION_SIGN_KEY` | `""` | `sensitive.tfvars`の`session_sign_key`から。**EC2の`.env`と同一の値でなければ切替時に全ユーザーがログアウトされる** |
| `ADMIN_NAME` | `admin` | `sensitive.tfvars`の`admin_name`から |
| `ADMIN_PASSWORD` | `admin` | `sensitive.tfvars`の`admin_password`から。デフォルトのままだと危険 |
| `OIDC_GOOGLE_CLIENT_ID` | `""` | `sensitive.tfvars`の`oidc_google_client_id`から |
| `OIDC_GOOGLE_CLIENT_SECRET` | `""` | 同上。`oidc_google_client_secret`から |
| `OIDC_GOOGLE_CALLBACK_URL` | `http://localhost:8080/oauth/google/callback` | 同上。`oidc_google_callback_url`から。**検証用のLambda Function URLで試す場合は、そのURL向けのコールバックをGoogle Cloud Console側にも登録する必要がある** |
| `OIDC_GOOGLE_DEFAULT_REDIRECT_URL` | `http://localhost:3000` | 同上。`oidc_google_default_redirect_url`から |
| `VERCEL_DEPLOY_HOOK` | (hooksの`requestTo`既定値) | `sensitive.tfvars`の`vercel_deploy_hook`から |

### 7.8 現在の本番EC2構成の記録

本番EC2の設定はリポジトリにもインフラコードにも存在せず、EC2の箱の中の`.env`と
手書きの`~/docker-compose.yml`にしか存在しなかった。過去にインスタンスを失った際に
OAuth設定を復旧できず、**本番のGoogle OAuthは現在壊れている**
（`OIDC_GOOGLE_*` が未設定で、authorize URLに`client_id`が入らない）。
フェーズ9でEC2を削除すると同じ情報が再び失われるため、ここに記録する。

#### `~/docker-compose.yml`（EC2上。リポジトリの`backend/docker-compose.yml`とは別物）

リポジトリの`backend/docker-compose.yml`は`build:`とローカルMySQLを含む
ローカル開発用であり、本番EC2上のファイルとは異なる。本番の実際の内容:

```yaml
services:
  web:
    image: miyado/kottage:latest
    container_name: kottage
    ports:
      - "8080:8080"
    env_file:
      - .env
      - .db-env
    depends_on:
      - redis
    healthcheck:
      test: ["CMD", "curl", "-f", "http://0.0.0.0:8080/api/v1/health"]
      interval: "5m"
      timeout: "10s"
      retries: 3
  redis:
    image: redis:latest
    container_name: kottage_redis
    restart: always
```

`redis`はアプリから一切参照されていない（フェーズ1で確認済み）ため、t2.nanoの
限られたメモリを無駄に消費しているだけである。**撤去候補**として記録する
（フェーズ9のEC2削除と同時に自然消滅するが、EC2稼働中に単独で撤去してもよい）。

#### 本番がDevelopmentモードで動作していた事実

`DEVELOPMENT`環境変数がEC2の`.env`に設定されていないため、アプリは
`ktor.development = true`（デフォルト）のまま本番稼働していた。この結果:

- `cookie.secure`が付かない（フェーズ2で修正対象と特定した`cookie.secure = false`は
  「本番でも常にfalseだった」ことの帰結でもある）
- CORS許可オリジンが`localhost:3000`のみになり、本来ブラウザ側から拒否されるべき
  設定のまま動いていた

Lambdaでは`DEVELOPMENT=false`を明示することでこれを是正する
（`backend/infra/lambda_app.tf`）。

### 成功条件 / 判断基準

- [ ] 上記すべての機能検証がパスする
- [ ] **コールドスタートが許容範囲内である**
- [ ] 月間コストが無料枠に収まる見込みである

> **判断ゲート**: コールドスタートが許容できない場合、ここで**案B（Lightsail移行、$3.5-5/月）へ切り替える**判断を行う。
> Provisioned Concurrencyの採用は月$4-5かかり削減額を打ち消すため、選択肢に含めない。
> このフェーズまでの成果（フェーズ1〜4）は案Bでも無駄にならない。

---

## フェーズ8: 本番切り替え

**ブランチ**: `feature/phase8-lambda-cutover`
**リスク**: **高**（本番経路の変更）
**依存**: フェーズ7の判断ゲート通過

### タスク

- [ ] 本番 `aws_apigatewayv2_integration.kottage` の統合先を `http_proxy` からアプリLambdaへ変更
- [ ] **イメージの更新をTerraformからCIへ移す**（下記8.1）
- [ ] `terraform apply` 前に `terraform plan` の差分を確認し、変更が統合先のみであることを検証
- [ ] 適用後、実ブラウザで全機能を確認
- [ ] **EC2とhttp_proxy Lambdaは削除せず稼働させたまま残す**
- [ ] CloudWatch Logsでエラーを監視

### 8.1 イメージ更新の主体をCIに移す

フェーズ7時点では `image_uri` をTerraformの変数（`app_image_tag`）で指定しており、
**デプロイのたびに変数を書き換えて `terraform apply` する**運用になっている。
これは実運用に耐えないため、フェーズ8で改める。

#### `latest` 固定にはできない

一見すると `image_uri` を `:latest` にすればTerraformを触らずに済みそうだが、成立しない。

**Lambdaはイメージタグを CreateFunction / UpdateFunctionCode の時点でダイジェストに
解決して固定する。** その後 `latest` に新しいイメージをpushしても、稼働中の関数は
一切変わらない。Terraform側も `image_uri` の文字列が変わらないため差分ゼロで、
`apply` しても何も起きない。

結果として「CIが `latest` をpushしたのにLambdaは古いイメージのまま」という状態になる。
`/api/v1/health` の `version` で**気づくことはできる**が、**直す手段が無い**
（毎回手で `aws lambda update-function-code` を叩くことになる）。ロールバックも同様。

#### 採る形: Terraformは定義、CIはデプロイ

```text
CI: イメージをECRにpush
  → aws lambda update-function-code --image-uri <ecr>:<version>
  → PublishVersion + エイリアス切り替え
```

Terraform側は初期値だけ持ち、以後の更新はCIに委ねる:

```hcl
lifecycle {
  ignore_changes = [image_uri]
}
```

これにより:

- デプロイのたびにTerraformを触らなくてよい
- Lambdaが確実に新しいイメージへ入れ替わる（`latest` では起きない）
- version付きタグなのでECR側からも何が動いているか追える
- ロールバックはエイリアスを前のバージョンに戻すだけで済む

エイリアス切り替えは新規invokeに即座に反映されるため、7.2で述べたマイグレーションとの
組み合わせ（マイグレーション用Lambdaのinvoke → PublishVersion → エイリアス切り替え）で
**窓をマイグレーション実行時間＋数秒に縮められる**。8.1と7.2は同じ仕組みの両面であり、
まとめて実装する。

### 成功条件

- [ ] `kottage.miyado.dev` 経由で全機能が動作する
- [ ] フロントエンド（Vercel）からのCORSリクエストが成功する
- [ ] エラー率がゼロである

### ロールバック手順

統合先を `http_proxy` に戻して `terraform apply` するだけ。所要1〜2分。
EC2は稼働し続けているため即座に復旧できる。

### 監視期間

**最低1週間**は現状を維持して安定性を確認する。TiDB移行時と同じ方針。

---

## フェーズ9: EC2/EIP撤去

**ブランチ**: `feature/phase9-lambda-teardown`
**リスク**: 中（Terraformで再作成可能だが実質不可逆）
**依存**: フェーズ8の監視期間完了

**このフェーズで初めてpublic IPv4の課金が停止する。**

### タスク

#### Terraform（削除対象）

- [ ] `aws_instance.kottage`
- [ ] `aws_eip.kottage`, `aws_eip_association.kottage`
- [ ] `aws_key_pair.kottage`
- [ ] `aws_security_group.ec2_instance`, `aws_security_group.ec2_instance_ssh`
- [ ] `module.lambda_http_proxy`（および `aws_lambda_permission.api_gateway` の関連定義）
- [ ] `aws_apigatewayv2_vpc_link.kottage`（未使用）
- [ ] `aws_security_group.api_gateway`
- [ ] VPC関連（`aws_vpc`, `aws_subnet.public`, `aws_internet_gateway`, `aws_route_table`, `aws_main_route_table_association`）
      — **VPC内に他のリソースが残っていないことを事前に確認する**
- [ ] 未使用の `db.tf` / `security_group.tf` のコメントアウト済みブロックとバックアップファイルも整理する

#### CI

- [ ] `delivery.yml` からDocker Hubへのpushを削除
- [ ] GitHub Secretsの `DOCKER_USERNAME` / `DOCKER_PASSWORD` を削除
- [ ] `docker-compose.yml` は本番用途を終えローカル開発専用になることを明記

#### 運用

- [ ] `ec2-monitoring-checklist.md` を「EC2廃止により不要」として整理またはアーカイブ
- [ ] SSH秘密鍵の廃棄

### 成功条件

- [ ] `terraform apply` が成功する
- [ ] `aws ec2 describe-instances` で対象インスタンスが存在しない
- [ ] `aws ec2 describe-addresses` でEIPが存在しない
- [ ] `kottage.miyado.dev` の全機能が引き続き動作する
- [ ] **翌月の請求でEC2・EIP・EBSの項目が消えていることを確認する**

---

## リスクとロールバック戦略

### フェーズ別のロールバック

| フェーズ | ロールバック手段 | 所要時間 |
|---------|----------------|---------|
| 1〜4, 6 | PRのrevert。EC2に再デプロイ | 10分 |
| 2 | 同上。ただし署名鍵の扱いに注意（セッションは失効する） | 10分 |
| 5 | 追加のみのため実質不要 | - |
| 7 | 本番に影響しないため不要 | - |
| 8 | API Gatewayの統合先を戻す | 1〜2分 |
| 9 | `terraform apply` でEC2を再作成し、統合先を戻す | 30分〜1時間 |

### 想定される問題と対応

#### コールドスタートが遅い
- フェーズ7の判断ゲートで検出する
- メモリ増加（1769MBで1 vCPU相当）で改善する場合がある
- 改善しない場合は案B（Lightsail）へ方針転換する

#### OAuthサインインが失敗する
- フェーズ2の本番EC2検証で先に検出できる設計にしている
- state署名の検証ロジックと有効期限を確認する

#### 数分放置後の最初のリクエストがDB接続エラーになる
- フェーズ7.4で調査済み: 現状HikariCP等のプーリングを使っておらず、Exposedの
  `Database.connect()` はtransactionのたびに素のJDBC接続を張り直すため、
  freeze中に切断された古い接続を掴んで失敗するという故障モードは原理的に起きない
- 実際に発生した場合は、想定外の場所でコネクションが使い回されていないか
  （例: Flyway側やドライバの内部キャッシュ）を疑って調査する

#### cookieが送信されない
- `SameSite=Strict` と `secure` フラグの組み合わせを確認する
- 現状 `cookie.secure = false` だが、API Gateway経由ではブラウザ側はHTTPSになるため `true` への変更が本来は正しい
- ただしこれは挙動変更を伴うため、フェーズ2のレビューで判断する

---

**最終更新**: 2026-07-25
**作成者**: 黒川
