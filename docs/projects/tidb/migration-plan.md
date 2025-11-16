# TiDB Serverless移行 - 詳細計画と仕様

## 目次

- [全体方針](#全体方針)
- [フェーズ0: 事前調査](#フェーズ0-事前調査)
- [フェーズ1: アプリケーションコード修正](#フェーズ1-アプリケーションコード修正)
- [フェーズ2: TiDB Serverlessセットアップ](#フェーズ2-tidb-serverlessセットアップ)
- [フェーズ3: ローカル環境でTiDB接続テスト](#フェーズ3-ローカル環境でtidb接続テスト)
- [フェーズ4: データ移行準備](#フェーズ4-データ移行準備)
- [フェーズ5: TiDBへデータインポート](#フェーズ5-tidbへデータインポート)
- [フェーズ6: 本番切り替え](#フェーズ6-本番切り替え)
- [フェーズ7: 監視・検証](#フェーズ7-監視検証)
- [フェーズ8: クリーンアップ](#フェーズ8-クリーンアップ)
- [リスクとロールバック戦略](#リスクとロールバック戦略)
- [タイムライン](#タイムライン)

---

## 全体方針

### 目標
- **ダウンタイム**: アプリケーション再起動の数秒のみ
- **リスク管理**: 各フェーズで検証、問題があれば即座にロールバック
- **インフラ**: 既存のus-east-2環境はそのまま、DBのみTiDB (us-east-1)に切り替え
- **データ保全**: 切り替え前に完全バックアップ取得

### 戦略: 段階的移行（戦略A）

```text
現状: EC2 (us-east-2) → RDS (us-east-2)
移行後: EC2 (us-east-2) → TiDB (us-east-1)
```

**メリット**:
- ✅ ダウンタイム最小（アプリ再起動のみ）
- ✅ 問題あればすぐRDSにロールバック可能
- ✅ リスク低い
- ⚠️ クロスリージョン通信で若干レイテンシ増（+5-10ms程度、実用上は許容範囲）

---

## フェーズ0: 事前調査

**所要時間**: 30分
**ダウンタイム**: なし

### 0.1 現状の把握

#### タスク
- [ ] 現在のRDSのデータサイズ確認
- [ ] 現在のクエリパターン確認（ログ分析）
- [ ] 月間アクセス数の見積もり（TiDB無料枠に収まるか確認）

#### 実行コマンド

```sql
-- RDSに接続してデータサイズ確認
SELECT
  table_schema AS 'Database',
  SUM(data_length + index_length) / 1024 / 1024 AS 'Size (MB)'
FROM information_schema.TABLES
GROUP BY table_schema;

-- 各テーブルのサイズ確認
SELECT
  table_name AS 'Table',
  ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.TABLES
WHERE table_schema = '<database_name>'
ORDER BY (data_length + index_length) DESC;

-- レコード数確認
SELECT COUNT(*) FROM entries;
SELECT COUNT(*) FROM comments;
SELECT COUNT(*) FROM users;
```

### 0.2 TiDB互換性確認

#### タスク
- [ ] 使用中のMySQL構文がTiDBで動作するか確認
- [ ] Flywayマイグレーションスクリプトの互換性確認
- [ ] Exposed ORMの動作確認

#### 確認ポイント
- TiDB ServerlessはMySQL 5.7/8.0互換
- `backend/src/main/resources/db/migration/` のFlywayスクリプト確認
- 外部キー制約、トリガー、ストアドプロシージャの使用状況確認

### 成功条件
- データサイズが5GB未満
- 月間クエリ数が5000万RU未満の見込み
- 互換性問題なし

---

## フェーズ1: アプリケーションコード修正

**所要時間**: 2時間
**ダウンタイム**: なし

### 1.1 DatabaseConfiguration修正

**ファイル**: `backend/src/main/kotlin/com/github/hmiyado/kottage/application/configuration/DatabaseConfiguration.kt`

**変更内容**:
```kotlin
data class MySql(
    val name: String,
    val host: String,
    val port: Int = 3306,              // 追加: デフォルト3306
    val user: String,
    val password: String,
    val sslMode: String = "DISABLED",  // 追加: デフォルトDISABLED
) : DatabaseConfiguration()
```

**変更理由**:
- TiDBはポート4000を使用
- TiDBはTLS接続必須
- 環境変数で柔軟に設定できるようにする

### 1.2 application.conf修正

**ファイル**: `backend/src/main/resources/application.conf`

**変更内容**:
```hocon
database {
  mysql {
    name = ""
    name = ${?MYSQL_DATABASE}
    host = ""
    host = ${?MYSQL_HOST}
    port = 3306                    # 追加: デフォルト3306
    port = ${?MYSQL_PORT}          # 追加: 環境変数で上書き可能
    user = ""
    user = ${?MYSQL_USER}
    password = ""
    password = ${?MYSQL_PASSWORD}
    sslMode = "DISABLED"           # 追加: デフォルトDISABLED
    sslMode = ${?MYSQL_SSL_MODE}   # 追加: 環境変数で上書き可能
  }
}
```

### 1.3 DatabaseProperties修正

**ファイル**: `backend/src/main/kotlin/com/github/hmiyado/kottage/application/configuration/DatabaseConfiguration.kt`

**変更内容**:
```kotlin
private data class DatabaseProperties(
    val name: String,
    val host: String,
    val port: Int,      // 追加
    val user: String,
    val password: String,
    val sslMode: String, // 追加
) {
    companion object {
        fun from(config: ApplicationConfig?): DatabaseProperties? {
            config ?: return null
            val name = config.propertyOrNull("name")?.getString()?.getNotBlankStringOrNull() ?: return null
            val host = config.propertyOrNull("host")?.getString()?.getNotBlankStringOrNull() ?: return null
            val port = config.propertyOrNull("port")?.getString()?.toIntOrNull() ?: 3306  // 追加
            val user = config.propertyOrNull("user")?.getString()?.getNotBlankStringOrNull() ?: return null
            val password = config.propertyOrNull("password")?.getString()?.getNotBlankStringOrNull() ?: return null
            val sslMode = config.propertyOrNull("sslMode")?.getString() ?: "DISABLED"  // 追加
            return DatabaseProperties(name, host, port, user, password, sslMode)
        }
        // ...
    }
}
```

### 1.4 Migration.kt修正

**ファイル**: `backend/src/main/kotlin/com/github/hmiyado/kottage/repository/Migration.kt`

**変更内容**:
```kotlin
private fun DatabaseConfiguration.MySql.init(): Flyway {
    // SSL設定をsslModeに応じて動的に変更
    val sslParams = when (sslMode.uppercase()) {
        "REQUIRED" -> "sslMode=REQUIRED&enabledTLSProtocols=TLSv1.2,TLSv1.3"
        "DISABLED" -> "useSSL=false&allowPublicKeyRetrieval=true"
        else -> "useSSL=false&allowPublicKeyRetrieval=true"
    }

    // ポート番号を動的に使用
    val url = "jdbc:mysql://$host:$port/$name?$sslParams"

    val flyway = Flyway
        .configure()
        .baselineOnMigrate(true)
        .dataSource(url, user, password)
        .load()

    Database.connect(
        url = url,
        driver = "com.mysql.cj.jdbc.Driver",
        user = user,
        password = password,
    )

    return flyway
}
```

### 1.5 コンパイルとテスト

```bash
# ビルド確認
./gradlew build

# ユニットテスト実行
./gradlew test -x karate:test

# ローカル環境で動作確認（既存のMySQL/RDSで）
docker-compose up -d
./gradlew run

# ヘルスチェック
curl http://localhost:8080/api/v1/health
```

### 成功条件
- コードがビルドできる
- 既存のローカル環境（Docker Compose + MySQL）で動作する（後方互換性）
- すべてのユニットテストがパス

---

## フェーズ2: TiDB Serverlessセットアップ

**所要時間**: 30分
**ダウンタイム**: なし

### 2.1 アカウント作成

#### アカウント作成手順

1. <https://tidbcloud.com> にアクセス
2. アカウント作成（GitHubまたはGoogleアカウント連携可能）
3. 組織名設定

### 2.2 クラスター作成

#### クラスター作成手順

1. "Create Cluster" をクリック
2. プラン選択: **Serverless** を選択
3. リージョン選択: **AWS us-east-1 (N. Virginia)** を選択
4. クラスター名: `kottage-prod`
5. "Create" をクリック

### 2.3 データベース作成

#### データベース作成手順

1. クラスターが作成されたら、"Connect" をクリック
2. データベース名を設定: `kottage`
3. ユーザー名とパスワードを生成または設定

### 2.4 接続情報取得

#### 取得する情報
- **ホスト名**: 例 `gateway01.us-east-1.prod.aws.tidbcloud.com`
- **ポート番号**: `4000`
- **ユーザー名**: 例 `2Axxx.root`
- **パスワード**: 生成されたパスワード
- **データベース名**: `kottage`

#### 保存先
接続情報は安全な場所に保存:
- パスワードマネージャー
- AWS Secrets Manager（推奨）
- 1Password等

### 2.5 接続テスト

```bash
# ローカルのmysqlクライアントからTiDBに接続
mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p \
      --ssl-mode=REQUIRED

# 接続後、テスト
mysql> SELECT VERSION();
mysql> SHOW DATABASES;
mysql> USE kottage;
```

### 成功条件
- TiDBクラスターが正常に作成される
- ローカルから接続できる
- データベースが作成される

---

## フェーズ3: ローカル環境でTiDB接続テスト

**所要時間**: 1時間
**ダウンタイム**: なし

### 3.1 環境変数設定

#### .env.tidb ファイル作成（ローカルテスト用）

```bash
# backend/.env.tidb
MYSQL_HOST=gateway01.us-east-1.prod.aws.tidbcloud.com
MYSQL_PORT=4000
MYSQL_DATABASE=kottage
MYSQL_USER=<your-username>
MYSQL_PASSWORD=<your-password>
MYSQL_SSL_MODE=REQUIRED

# その他の環境変数（既存の.envからコピー）
REDIS_HOST=localhost
ADMIN_NAME=admin
ADMIN_PASSWORD=admin
# ... etc
```

### 3.2 環境変数読み込み

```bash
# .env.tidb を読み込んでアプリケーション起動
cd backend
export $(cat .env.tidb | xargs)
./gradlew run
```

### 3.3 マイグレーション実行確認

#### ログ確認
```text
[main] INFO  Application - database is successfully connected to mysql
[main] INFO  cli - [SUCCESS]: null -> 1.0
```

Flywayマイグレーションが正常に実行されることを確認。

### 3.4 CRUD操作テスト

```bash
# ヘルスチェック
curl http://localhost:8080/api/v1/health

# エントリー一覧取得
curl http://localhost:8080/api/v1/entries

# エントリー作成（要認証）
curl -X POST http://localhost:8080/api/v1/entries \
  -H "Content-Type: application/json" \
  -u admin:admin \
  -d '{
    "title": "TiDB Test",
    "body": "Testing TiDB connection",
    "description": "Test"
  }'
```

### 3.5 E2Eテスト実行

```bash
# Karateテスト実行
./gradlew karate:test
```

### 3.6 接続情報確認

```sql
-- TiDBに接続して確認
mysql> SHOW TABLES;
mysql> SELECT * FROM flyway_schema_history;
mysql> SELECT COUNT(*) FROM entries;
```

### 成功条件
- Flywayマイグレーションが成功
- CRUD操作が正常動作
- E2Eテストがパス
- TiDBに正しくデータが保存される

### ロールバック
環境変数を元に戻すだけ:
```bash
# 元の設定で起動
docker-compose up -d
./gradlew run
```

---

## フェーズ4: データ移行準備

**所要時間**: 1時間
**ダウンタイム**: なし

### 4.1 RDSからデータエクスポート

#### 手順

```bash
# EC2にSSH接続
ssh -i ~/.ssh/kottage.pem ec2-user@<ec2-public-ip>

# mysqldumpがインストールされているか確認
which mysqldump

# なければインストール
sudo yum install -y mysql

# RDSエンドポイント確認（環境変数または設定ファイルから）
echo $MYSQL_HOST

# バックアップディレクトリ作成
mkdir -p ~/db-backup
cd ~/db-backup

# mysqldumpでエクスポート
mysqldump -h <rds-endpoint> \
          -P 3306 \
          -u <username> \
          -p<password> \
          --single-transaction \
          --routines \
          --triggers \
          --set-gtid-purged=OFF \
          <database-name> > backup_$(date +%Y%m%d_%H%M%S).sql

# 実行例
mysqldump -h kottage-db.xxxxx.us-east-2.rds.amazonaws.com \
          -P 3306 \
          -u admin \
          -pYourPassword \
          --single-transaction \
          --routines \
          --triggers \
          --set-gtid-purged=OFF \
          kottage > backup_20250116_120000.sql
```

#### オプション説明
- `--single-transaction`: トランザクション内でダンプ（InnoDB向け）
- `--routines`: ストアドプロシージャ・ファンクションを含める
- `--triggers`: トリガーを含める
- `--set-gtid-purged=OFF`: GTID関連の警告を回避

### 4.2 バックアップファイル検証

```bash
# ファイルサイズ確認
ls -lh backup_*.sql

# 行数確認
wc -l backup_*.sql

# 先頭100行確認（構造確認）
head -n 100 backup_*.sql

# SQLファイルの整合性確認（構文エラーがないか）
mysql -h <rds-endpoint> \
      -u <username> \
      -p<password> \
      --execute="SET SQL_MODE='NO_AUTO_VALUE_ON_ZERO';" < backup_*.sql
# エラーがなければOK（実際には実行されない）
```

### 4.3 S3にバックアップ保存

```bash
# AWS CLIインストール確認
aws --version

# S3にアップロード
aws s3 cp backup_*.sql s3://kottage-log/db-backup/

# アップロード確認
aws s3 ls s3://kottage-log/db-backup/
```

### 4.4 ローカルにもコピー（推奨）

```bash
# ローカルマシンにダウンロード
scp -i ~/.ssh/kottage.pem \
    ec2-user@<ec2-ip>:~/db-backup/backup_*.sql \
    ~/Downloads/
```

### 成功条件
- バックアップファイルが正常に作成される
- ファイルサイズが妥当（空ではない）
- S3に保存される
- 構文エラーがない

---

## フェーズ5: TiDBへデータインポート

**所要時間**: 30分
**ダウンタイム**: なし

### 5.1 バックアップファイルをTiDBにインポート

#### ローカルからインポート（推奨）

```bash
# ローカルマシンから実行
cd ~/Downloads

# TiDBにインポート
mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p<password> \
      --ssl-mode=REQUIRED \
      kottage < backup_20250116_120000.sql

# 時間がかかる場合はプログレス表示
pv backup_20250116_120000.sql | \
  mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
        -P 4000 \
        -u '<username>' \
        -p<password> \
        --ssl-mode=REQUIRED \
        kottage
```

#### EC2からインポート（代替案）

```bash
# EC2上で実行
cd ~/db-backup

mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p<password> \
      --ssl-mode=REQUIRED \
      kottage < backup_20250116_120000.sql
```

### 5.2 データ整合性確認

```sql
-- TiDBに接続
mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p \
      --ssl-mode=REQUIRED

-- データベース選択
USE kottage;

-- テーブル一覧確認
SHOW TABLES;

-- 各テーブルのレコード数確認（RDSと比較）
SELECT COUNT(*) AS entries_count FROM entries;
SELECT COUNT(*) AS comments_count FROM comments;
SELECT COUNT(*) AS users_count FROM users;
SELECT COUNT(*) AS passwords_count FROM passwords;
SELECT COUNT(*) AS admins_count FROM admins;

-- サンプルデータ確認
SELECT * FROM entries ORDER BY serial_number DESC LIMIT 5;
SELECT * FROM users LIMIT 5;

-- インデックス確認
SHOW INDEX FROM entries;
SHOW INDEX FROM comments;
```

### 5.3 Flywayマイグレーション状態確認

```sql
-- Flywayマイグレーション履歴確認
SELECT * FROM flyway_schema_history ORDER BY installed_rank;

-- 最新のマイグレーションバージョン確認
SELECT version, description, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 1;
```

### 5.4 アプリケーションからの接続テスト

```bash
# ローカル環境でTiDB接続テスト（再確認）
export MYSQL_HOST=gateway01.us-east-1.prod.aws.tidbcloud.com
export MYSQL_PORT=4000
export MYSQL_SSL_MODE=REQUIRED
# ... 他の環境変数

./gradlew run

# APIテスト
curl http://localhost:8080/api/v1/health
curl http://localhost:8080/api/v1/entries
```

### 成功条件
- 全データが正常にインポートされる
- レコード数がRDSと一致
- Flywayマイグレーション状態が正常
- アプリケーションから接続できる
- データが正しく取得できる

### トラブルシューティング

#### エラー: "Lost connection to MySQL server during query"
→ ファイルが大きい場合、`max_allowed_packet` を増やす:
```sql
SET GLOBAL max_allowed_packet=1073741824; -- 1GB
```

#### エラー: "Unknown collation"
→ TiDBでサポートされていない照合順序の可能性。バックアップファイルを編集して削除。

---

## フェーズ6: 本番切り替え

**所要時間**: 10分
**ダウンタイム**: 数秒（アプリケーション再起動のみ）

### 6.1 切り替え前チェックリスト

#### 確認事項
- [ ] TiDBにデータが正常にインポートされている
- [ ] ローカル環境でTiDB接続テスト完了
- [ ] レコード数がRDSと一致
- [ ] バックアップがS3に保存されている
- [ ] ロールバック手順が確認されている
- [ ] メンテナンス通知（必要に応じて）

### 6.2 本番EC2の環境変数更新

#### 手順

```bash
# EC2にSSH接続
ssh -i ~/.ssh/kottage.pem ec2-user@<ec2-public-ip>

# 現在の環境変数バックアップ
cp .env .env.backup_$(date +%Y%m%d_%H%M%S)

# 環境変数ファイル編集
vi .env
# または
nano .env
```

#### 変更内容

```bash
# .env

# === TiDB接続設定 ===
MYSQL_HOST=gateway01.us-east-1.prod.aws.tidbcloud.com
MYSQL_PORT=4000
MYSQL_DATABASE=kottage
MYSQL_USER=<your-tidb-username>
MYSQL_PASSWORD=<your-tidb-password>
MYSQL_SSL_MODE=REQUIRED

# === その他の設定はそのまま ===
REDIS_HOST=localhost
ADMIN_NAME=admin
ADMIN_PASSWORD=<your-admin-password>
# ... etc
```

#### 設定確認

```bash
# 環境変数が正しく設定されているか確認
cat .env | grep MYSQL
```

### 6.3 アプリケーション再起動

#### Dockerの場合

```bash
# 現在のコンテナ停止
docker-compose down

# 新しい環境変数で起動
docker-compose up -d

# ログ確認
docker-compose logs -f web
```

#### systemdの場合

```bash
# サービス再起動
sudo systemctl restart kottage

# ステータス確認
sudo systemctl status kottage

# ログ確認
sudo journalctl -u kottage -f
```

### 6.4 即座に動作確認

#### ヘルスチェック

```bash
# ヘルスチェックAPI
curl https://<your-domain>/api/v1/health
# 期待結果: {"status":"ok"}

# またはローカルから
curl http://<ec2-public-ip>:8080/api/v1/health
```

#### データ取得確認

```bash
# エントリー一覧取得
curl https://<your-domain>/api/v1/entries

# 特定のエントリー取得
curl https://<your-domain>/api/v1/entries/1
```

#### ログ確認

```bash
# アプリケーションログでエラーがないか確認
docker logs kottage --tail 100

# データベース接続成功のログを確認
# 期待するログ: "database is successfully connected to mysql"
```

#### ブラウザ確認

1. https://<your-domain> にアクセス
2. ページが正常に表示されるか確認
3. 記事一覧が表示されるか確認
4. 記事詳細が表示されるか確認

### 6.5 データ書き込みテスト

```bash
# コメント投稿テスト（ブラウザから）
# または
curl -X POST https://<your-domain>/api/v1/entries/1/comments \
  -H "Content-Type: application/json" \
  -d '{
    "body": "TiDB migration test",
    "author": "test-user"
  }'

# TiDBで確認
mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p \
      --ssl-mode=REQUIRED \
      -e "USE kottage; SELECT * FROM comments ORDER BY id DESC LIMIT 1;"
```

### 成功条件
- アプリケーションが正常起動
- ヘルスチェックがOK
- データが正常に表示される
- 読み書き操作が正常動作
- エラーログがない

### 6.6 ロールバック手順（問題があった場合）

#### 即座にロールバック

```bash
# 環境変数を元のRDS設定に戻す
cp .env.backup_YYYYMMDD_HHMMSS .env

# または手動で編集
vi .env
```

```bash
# .env（ロールバック時）
MYSQL_HOST=<rds-endpoint>.us-east-2.rds.amazonaws.com
MYSQL_PORT=3306
MYSQL_DATABASE=kottage
MYSQL_USER=<rds-username>
MYSQL_PASSWORD=<rds-password>
MYSQL_SSL_MODE=DISABLED
```

```bash
# アプリケーション再起動
docker-compose down
docker-compose up -d

# 動作確認
curl https://<your-domain>/api/v1/health
```

**ロールバック所要時間**: 1-2分

---

## フェーズ7: 監視・検証

**所要時間**: 1週間
**ダウンタイム**: なし

### 7.1 監視項目

#### 毎日確認
- [ ] アプリケーションエラーログ
- [ ] TiDB接続エラーの有無
- [ ] レスポンスタイム
- [ ] データ整合性

#### 定期確認（2-3日ごと）
- [ ] TiDB使用量（RU消費、ストレージ）
- [ ] パフォーマンスメトリクス

### 7.2 ログ監視

```bash
# アプリケーションログ
docker logs kottage --tail 100 -f

# エラーログのみ抽出
docker logs kottage 2>&1 | grep -i error

# データベース接続関連ログ
docker logs kottage 2>&1 | grep -i "database\|mysql"
```

### 7.3 TiDB使用量確認

#### TiDB Cloud Consoleで確認

1. <https://tidbcloud.com> にログイン
2. クラスター詳細画面
3. "Monitoring" タブ
   - Request Units (RU) 消費量
   - ストレージ使用量
   - クエリパフォーマンス

#### 無料枠チェック
- RU消費: 5000万RU/月以内か
- ストレージ: 5GB以内か

### 7.4 検証テスト

#### 機能テスト
- [ ] 記事投稿
- [ ] 記事編集
- [ ] 記事削除
- [ ] コメント投稿
- [ ] ユーザー認証（Google OIDC）
- [ ] 管理者機能

#### ブラウザテスト
1. トップページ表示
2. 記事一覧表示
3. 記事詳細表示
4. コメント投稿
5. 管理画面（ログイン）

### 7.5 パフォーマンス比較

#### レスポンスタイム測定

```bash
# エントリー一覧取得のレスポンスタイム
for i in {1..10}; do
  curl -o /dev/null -s -w "Time: %{time_total}s\n" \
    https://<your-domain>/api/v1/entries
done

# 平均レスポンスタイムを計算
```

#### クロスリージョンレイテンシ確認

```bash
# EC2からTiDBへのレイテンシ
# EC2上で実行
time mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
           -P 4000 \
           -u '<username>' \
           -p<password> \
           --ssl-mode=REQUIRED \
           -e "SELECT 1"
```

**期待値**: +5-10ms程度の増加は許容範囲

### 7.6 データ整合性チェック

```bash
# 定期的にレコード数確認
# TiDB
mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p \
      --ssl-mode=REQUIRED \
      -e "USE kottage;
          SELECT 'entries' AS table_name, COUNT(*) AS count FROM entries
          UNION ALL
          SELECT 'comments', COUNT(*) FROM comments
          UNION ALL
          SELECT 'users', COUNT(*) FROM users;"

# RDS（比較用、削除前のみ）
mysql -h <rds-endpoint> \
      -P 3306 \
      -u <username> \
      -p<password> \
      -e "USE kottage;
          SELECT 'entries' AS table_name, COUNT(*) AS count FROM entries
          UNION ALL
          SELECT 'comments', COUNT(*) FROM comments
          UNION ALL
          SELECT 'users', COUNT(*) FROM users;"
```

### 7.7 問題発生時の対応

#### エラー発生
→ ログを確認し、TiDB接続エラーの場合はRDSへロールバック

#### パフォーマンス劣化
→ レスポンスタイムを測定し、許容範囲外ならRDSへロールバック

#### データ不整合
→ 即座にRDSへロールバック、原因調査

### 成功条件
- 1週間エラーなし
- パフォーマンス劣化なし（または許容範囲内）
- データ不整合なし
- 無料枠に収まっている

---

## フェーズ8: クリーンアップ

**所要時間**: 30分
**ダウンタイム**: なし

### 8.1 最終確認

#### チェックリスト
- [ ] TiDBで1週間問題なく稼働
- [ ] パフォーマンスが許容範囲
- [ ] データ整合性OK
- [ ] エラーなし

### 8.2 RDS削除（Terraform）

#### 手順

```bash
cd backend/infra

# 現在のTerraform状態確認
terraform show | grep aws_db_instance

# db.tfファイルをバックアップ
cp db.tf db.tf.backup

# db.tfを編集（RDSリソースを削除またはコメントアウト）
vi db.tf
```

#### db.tf変更内容

```hcl
# backend/infra/db.tf

# コメントアウトまたは削除
# resource "aws_db_instance" "kottage_db" {
#   allocated_storage           = 20
#   engine                      = "mysql"
#   engine_version              = "8.4.6"
#   instance_class              = "db.t3.micro"
#   username                    = var.db_user
#   password                    = var.db_password
#   parameter_group_name        = "default.mysql8.4"
#   skip_final_snapshot         = true
#   availability_zone           = var.main_availability_zones[0]
#   auto_minor_version_upgrade  = true
#   allow_major_version_upgrade = true
#   apply_immediately           = true
#   vpc_security_group_ids      = [aws_security_group.kottage_db.id]
#   db_subnet_group_name        = aws_db_subnet_group.kottage_public.name
# }

# resource "aws_db_subnet_group" "kottage_public" {
#   name       = "kottage_public"
#   subnet_ids = aws_subnet.public.*.id
#
#   tags = {
#     Name = "kottage_public"
#   }
# }
```

#### security_group.tf変更内容

```hcl
# backend/infra/security_group.tf

# kottage_db セキュリティグループを削除
# resource "aws_security_group" "kottage_db" {
#   ...
# }
```

### 8.3 Terraform変更適用

```bash
# 変更内容確認
terraform plan

# 削除されるリソース確認
# 期待結果:
# - aws_db_instance.kottage_db will be destroyed
# - aws_db_subnet_group.kottage_public will be destroyed
# - aws_security_group.kottage_db will be destroyed

# 適用（慎重に）
terraform apply

# 確認プロンプトで "yes" を入力
```

### 8.4 RDS削除確認

```bash
# AWS CLIで確認
aws rds describe-db-instances --region us-east-2

# kottage_dbが削除されていることを確認
```

### 8.5 コスト確認

#### AWS Billing & Cost Managementで確認
1. AWS Consoleにログイン
2. Billing & Cost Management → Bills
3. RDSの料金が$0になっていることを確認（翌月）

#### 期待されるコスト削減
- 月額: $14.95削減
- 年額: $179削減

### 8.6 ドキュメント更新

#### 更新すべきドキュメント
- [ ] README.md: RDSからTiDBへの変更を反映
- [ ] デプロイ手順: 環境変数設定を更新
- [ ] インフラ構成図: TiDB使用を明記

### 成功条件
- RDSが削除される
- Terraformの状態が正常
- 請求額が減少している（翌月確認）
- アプリケーションが正常動作（影響なし）

---

## リスクとロールバック戦略

### リスク1: TiDB接続エラー

**発生タイミング**: フェーズ3、フェーズ6

**症状**:
- アプリケーション起動失敗
- "Cannot connect to MySQL server" エラー

**原因**:
- SSL/TLS設定ミス
- ポート番号間違い
- 接続情報間違い
- ネットワーク問題

**対策**:
- フェーズ3でローカルテスト徹底
- 接続文字列を慎重に確認
- TiDB Cloudのファイアウォール設定確認

**ロールバック**:
```bash
# 環境変数を元に戻して再起動（1分以内）
cp .env.backup .env
docker-compose down && docker-compose up -d
```

---

### リスク2: データ移行失敗

**発生タイミング**: フェーズ5

**症状**:
- インポート中にエラー
- レコード数が一致しない
- データが破損

**原因**:
- バックアップファイル破損
- TiDB互換性問題
- ネットワーク切断

**対策**:
- フェーズ4でバックアップ検証
- 複数バックアップ取得
- S3に保存

**ロールバック**:
- TiDBのデータを削除して再インポート
- RDSはそのまま残っているので影響なし

---

### リスク3: パフォーマンス劣化

**発生タイミング**: フェーズ7（監視期間）

**症状**:
- レスポンスタイムが遅い
- クエリが遅い
- ユーザー体験が悪化

**原因**:
- クロスリージョン通信のレイテンシ
- TiDBのクエリ最適化不足
- インデックス不足

**対策**:
- フェーズ7で1週間監視
- パフォーマンスメトリクス測定
- 許容範囲を事前に定義

**ロールバック**:
```bash
# RDSに戻す（フェーズ8実行前なら即座に可能）
cp .env.backup .env
docker-compose down && docker-compose up -d
```

---

### リスク4: 無料枠超過

**発生タイミング**: フェーズ7（運用中）

**症状**:
- TiDBから課金通知
- 無料枠を超えた

**原因**:
- アクセス数が予想以上
- クエリが非効率
- データサイズが5GB超過

**対策**:
- フェーズ0でデータサイズ・アクセス数見積もり
- フェーズ7で使用量監視
- アラート設定

**対応**:
1. クエリ最適化
2. データ削減（古いデータのアーカイブ）
3. 有料プランへ移行検討
4. またはRDSに戻す

---

### リスク5: クロスリージョン通信のレイテンシ

**発生タイミング**: フェーズ6以降

**症状**:
- レスポンスタイムが+5-10ms増加

**原因**:
- us-east-2 (EC2) → us-east-1 (TiDB)のネットワーク遅延

**対策**:
- フェーズ7でパフォーマンス測定
- 許容範囲内か確認

**影響**:
- 実用上は許容範囲の可能性が高い
- ユーザー体験への影響は小さい

**対応**:
1. 許容範囲なら継続
2. 問題あれば後日インフラ全体をus-east-1に移行検討
3. またはRDSに戻す

---

## タイムライン

### 見積もり

| フェーズ | 所要時間 | ダウンタイム | 担当 |
|---------|---------|------------|------|
| 0. 事前調査 | 30分 | なし | 宮戸 |
| 1. コード修正 | 2時間 | なし | 蔵人川 |
| 2. TiDBセットアップ | 30分 | なし | 宮戸 |
| 3. ローカルテスト | 1時間 | なし | 蔵人川 + 宮戸 |
| 4. データ移行準備 | 1時間 | なし | 宮戸 |
| 5. データインポート | 30分 | なし | 宮戸 |
| 6. 本番切り替え | 10分 | **数秒** | 宮戸 + 蔵人川 |
| 7. 監視・検証 | 1週間 | なし | 宮戸 + 蔵人川 |
| 8. クリーンアップ | 30分 | なし | 蔵人川 |

**合計作業時間**: 約6時間（監視期間除く）
**ダウンタイム**: アプリ再起動の数秒のみ

### 推奨スケジュール

#### 準備期間（フェーズ0-5）
- **日時**: 平日の日中
- **期間**: 2-3日間
- **作業**: コード修正、TiDBセットアップ、データ移行準備

#### 本番切り替え（フェーズ6）
- **日時**: 金曜夜または土曜（トラフィックが少ない時間帯）
- **時間帯**: 深夜0時-2時（推奨）
- **作業時間**: 10分
- **ダウンタイム**: 数秒

#### 監視期間（フェーズ7）
- **期間**: 1週間
- **作業**: 毎日ログ確認、定期的なメトリクス確認

#### クリーンアップ（フェーズ8）
- **日時**: 監視期間終了後の平日
- **作業時間**: 30分

---

## まとめ

### 移行のポイント

1. **段階的移行**: リスクを最小化
2. **ロールバック容易**: RDSを1週間残す
3. **ダウンタイム最小**: アプリ再起動のみ
4. **コスト削減**: 月$14.95（年$179）削減

### 次のステップ

1. このドキュメントをレビュー
2. フェーズ0（事前調査）を実施
3. フェーズ1（コード修正）を開始

### 連絡事項

- 問題があればすぐに連絡
- ロールバックは躊躇なく実施
- パフォーマンス劣化を感じたら報告
