# TiDB移行 - トラブルシューティング

このドキュメントは、TiDB移行中に発生した問題と解決策をまとめたものです。

## 目次

- [接続エラー](#接続エラー)
- [SSL/TLS関連](#ssltls関連)
- [データ移行関連](#データ移行関連)
- [パフォーマンス問題](#パフォーマンス問題)
- [その他](#その他)

---

## 接続エラー

### エラー: "Cannot connect to MySQL server"

#### 症状
```
java.sql.SQLException: Cannot connect to MySQL server on 'gateway01.us-east-1.prod.aws.tidbcloud.com'
```

#### 考えられる原因
1. ホスト名が間違っている
2. ポート番号が間違っている（4000ではない）
3. ネットワーク接続の問題
4. TiDB側のファイアウォール設定

#### 解決策

**1. 接続情報の確認**
```bash
# 環境変数確認
echo $MYSQL_HOST
echo $MYSQL_PORT
echo $MYSQL_USER

# TiDBに直接接続してテスト
mysql -h gateway01.us-east-1.prod.aws.tidbcloud.com \
      -P 4000 \
      -u '<username>' \
      -p \
      --ssl-mode=REQUIRED
```

**2. ネットワーク確認**
```bash
# ホストへのping
ping gateway01.us-east-1.prod.aws.tidbcloud.com

# ポートへの接続確認
telnet gateway01.us-east-1.prod.aws.tidbcloud.com 4000
# または
nc -zv gateway01.us-east-1.prod.aws.tidbcloud.com 4000
```

**3. TiDB Cloud Consoleで確認**
- ファイアウォール設定でIPアドレスが許可されているか確認
- クラスターが稼働しているか確認

---

## SSL/TLS関連

### エラー: "SSL connection error"

#### 症状
```
javax.net.ssl.SSLHandshakeException: No appropriate protocol
```

#### 考えられる原因
1. `sslMode`設定が間違っている
2. TLS 1.2/1.3がサポートされていない
3. 証明書検証の問題

#### 解決策

**1. sslMode設定確認**
```bash
# 環境変数確認
echo $MYSQL_SSL_MODE
# 期待値: REQUIRED

# application.confの確認
cat backend/src/main/resources/application.conf | grep sslMode
```

**2. JDBC URLの確認**

Migration.ktの接続文字列が正しいか確認:
```kotlin
// 正しい例
val url = "jdbc:mysql://$host:$port/$name?sslMode=REQUIRED&enabledTLSProtocols=TLSv1.2,TLSv1.3"

// 間違い例
val url = "jdbc:mysql://$host:$port/$name?useSSL=false"  // TiDBでは動かない
```

**3. Javaバージョン確認**
```bash
java -version
# Java 8以上でTLS 1.2がサポートされているか確認
```

---

### エラー: "certificate verify failed"

#### 症状
```
SSLException: Certificate verify failed
```

#### 解決策

TiDBはLet's Encrypt証明書を使用。通常はJavaのデフォルト証明書ストアで検証可能。

```bash
# 証明書検証をスキップ（開発環境のみ）
# 本番環境では推奨しない
val url = "jdbc:mysql://$host:$port/$name?sslMode=REQUIRED&verifyServerCertificate=false"
```

---

## データ移行関連

### エラー: "Lost connection to MySQL server during query"

#### 症状
mysqldumpでのインポート中に接続が切れる。

#### 考えられる原因
1. ファイルサイズが大きすぎる
2. `max_allowed_packet`の制限
3. ネットワークタイムアウト

####解決策

**1. max_allowed_packetを増やす**
```sql
-- TiDBに接続して実行
SET GLOBAL max_allowed_packet=1073741824;  -- 1GB
```

**2. ファイルを分割**
```bash
# テーブルごとにエクスポート
mysqldump -h <rds-host> -u <user> -p<password> \
          --single-transaction \
          <database-name> entries > entries.sql

mysqldump -h <rds-host> -u <user> -p<password> \
          --single-transaction \
          <database-name> comments > comments.sql

# テーブルごとにインポート
mysql -h <tidb-host> -P 4000 -u <user> -p<password> \
      --ssl-mode=REQUIRED \
      <database-name> < entries.sql
```

**3. タイムアウト設定**
```bash
# タイムアウトを長く設定
mysql -h <tidb-host> -P 4000 -u <user> -p<password> \
      --ssl-mode=REQUIRED \
      --connect-timeout=3600 \
      --max-allowed-packet=1G \
      <database-name> < backup.sql
```

---

### エラー: "Unknown collation: 'utf8mb4_0900_ai_ci'"

#### 症状
```
ERROR 1273 (HY000): Unknown collation: 'utf8mb4_0900_ai_ci'
```

#### 考えられる原因
TiDBがMySQL 8.0のデフォルト照合順序をサポートしていない。

#### 解決策

**1. バックアップファイルを編集**
```bash
# utf8mb4_0900_ai_ci を utf8mb4_bin に置換
sed -i 's/utf8mb4_0900_ai_ci/utf8mb4_bin/g' backup.sql

# または utf8mb4_general_ci
sed -i 's/utf8mb4_0900_ai_ci/utf8mb4_general_ci/g' backup.sql
```

**2. エクスポート時に指定**
```bash
mysqldump -h <rds-host> -u <user> -p<password> \
          --single-transaction \
          --default-character-set=utf8mb4 \
          --skip-set-charset \
          <database-name> > backup.sql
```

---

### データ不整合: レコード数が一致しない

#### 症状
RDSとTiDBでレコード数が異なる。

#### 確認方法

```sql
-- RDS
SELECT COUNT(*) FROM entries;
SELECT COUNT(*) FROM comments;

-- TiDB
SELECT COUNT(*) FROM entries;
SELECT COUNT(*) FROM comments;
```

#### 解決策

**1. インポート時のエラー確認**
```bash
# インポート時のエラーログを確認
mysql -h <tidb-host> -P 4000 -u <user> -p<password> \
      --ssl-mode=REQUIRED \
      <database-name> < backup.sql 2>&1 | tee import.log

# エラー行を確認
grep -i error import.log
```

**2. 再インポート**
```bash
# TiDBのデータを削除
mysql -h <tidb-host> -P 4000 -u <user> -p<password> \
      --ssl-mode=REQUIRED \
      -e "DROP DATABASE kottage; CREATE DATABASE kottage;"

# 再度インポート
mysql -h <tidb-host> -P 4000 -u <user> -p<password> \
      --ssl-mode=REQUIRED \
      kottage < backup.sql
```

---

## パフォーマンス問題

### レスポンスタイムが遅い

#### 症状
TiDB移行後、APIのレスポンスタイムが増加。

#### 確認方法

```bash
# レスポンスタイム測定
for i in {1..10}; do
  curl -o /dev/null -s -w "Time: %{time_total}s\n" \
    https://<your-domain>/api/v1/entries
done
```

#### 考えられる原因
1. クロスリージョン通信のレイテンシ（us-east-2 → us-east-1）
2. インデックス不足
3. クエリの非効率
4. TiDBのコールドスタート

#### 解決策

**1. レイテンシ測定**
```bash
# EC2からTiDBへのレイテンシ
time mysql -h <tidb-host> -P 4000 -u <user> -p<password> \
           --ssl-mode=REQUIRED \
           -e "SELECT 1"
```

**2. インデックス確認**
```sql
-- TiDB
SHOW INDEX FROM entries;
SHOW INDEX FROM comments;

-- RDS（比較用）
SHOW INDEX FROM entries;
SHOW INDEX FROM comments;
```

**3. スロークエリ確認**
```sql
-- TiDB
SHOW VARIABLES LIKE 'slow_query_log';
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;  -- 1秒以上のクエリをログ

-- スロークエリログ確認
SELECT * FROM INFORMATION_SCHEMA.SLOW_QUERY;
```

**4. クエリ最適化**
```sql
-- EXPLAINでクエリプラン確認
EXPLAIN SELECT * FROM entries WHERE serial_number = 1;

-- 必要に応じてインデックス追加
CREATE INDEX idx_serial_number ON entries(serial_number);
```

**5. 許容範囲の確認**
- +5-10ms程度の増加は許容範囲
- それ以上の場合は、インフラ全体をus-east-1に移行検討

---

### 無料枠超過の警告

#### 症状
TiDB Cloud Consoleで無料枠超過の警告。

#### 確認方法

TiDB Cloud Console → Monitoring:
- Request Units (RU): 5000万RU/月以内か
- Storage: 5GB以内か

#### 解決策

**1. RU消費の最適化**
```sql
-- 不要なクエリの削減
-- インデックスの最適化
-- バッチ処理の導入
```

**2. ストレージの削減**
```sql
-- 古いデータの削除
DELETE FROM comments WHERE created_at < DATE_SUB(NOW(), INTERVAL 1 YEAR);

-- または別DBにアーカイブ
```

**3. 有料プランへの移行検討**
- Scaler Pro: $39/月〜

**4. RDSへのロールバック**
- コスト削減効果がなければRDSに戻す

---

## その他

### Flywayマイグレーションエラー

#### 症状
```
FlywayValidateException: Migration checksum mismatch
```

#### 考えられる原因
マイグレーションスクリプトが変更された。

#### 解決策

**1. マイグレーション履歴確認**
```sql
SELECT * FROM flyway_schema_history ORDER BY installed_rank;
```

**2. リペア（慎重に）**
```bash
# ローカル環境で実行
./gradlew flywayRepair
```

**3. ベースライン設定**
```bash
# 既存DBに対してベースライン設定
./gradlew flywayBaseline
```

---

### 環境変数が反映されない

#### 症状
環境変数を変更したのに、アプリケーションが古い設定を使用。

#### 解決策

**1. 環境変数の確認**
```bash
# Docker Composeの場合
docker-compose config

# 実行中のコンテナの環境変数確認
docker exec kottage env | grep MYSQL
```

**2. キャッシュクリア**
```bash
# Docker Composeの完全リビルド
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

**3. .envファイルの確認**
```bash
# .envファイルが正しく読み込まれているか
cat .env | grep MYSQL
```

---

### ロールバックが必要な場合

#### 手順

```bash
# 1. EC2にSSH接続
ssh -i ~/.ssh/kottage.pem ec2-user@<ec2-ip>

# 2. 環境変数を元に戻す
cp .env.backup_YYYYMMDD_HHMMSS .env

# 3. アプリケーション再起動
docker-compose down
docker-compose up -d

# 4. 動作確認
curl https://<your-domain>/api/v1/health
```

**所要時間**: 1-2分

---

## 問題報告テンプレート

問題が解決しない場合は、以下のテンプレートで報告してください。

```markdown
## 問題の概要
簡潔に説明

## 発生タイミング
Phase X: タスクY実施中

## 症状
- エラーメッセージ:
- ログ出力:
```
エラーログをここに貼り付け
```

## 実行したコマンド
```bash
実行したコマンド
```

## 環境情報
- OS:
- Javaバージョン:
- アプリケーションバージョン:

## 試したこと
1. 対応1: 結果
2. 対応2: 結果

## 質問・相談
具体的な質問
```

---

**最終更新**: 2025-01-16（実施中に随時更新）
