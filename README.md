# Splunk‑Lite — SQLite + FTS5 (Single‑Service Spring Boot)

Minimal Splunk-like logs app without Docker or external services. Uses SQLite with FTS5 for full‑text search.

## Quickstart
```bash
mvn -DskipTests clean package
java -jar target/splunk-lite-sqlite-fts-0.0.1-SNAPSHOT.jar
```

## Test it
```bash
# Ingest a log
curl -X POST http://localhost:8080/api/logs -H 'Content-Type: application/json' -d '{
  "source":"orders-api","env":"dev","level":"ERROR",
  "message":"Failed to charge card","ts":"2025-08-23T16:30:10Z",
  "http":{"status":502,"path":"/v1/charge"}
}'

# Search logs (last 24h)
curl -X POST http://localhost:8080/api/search -H 'Content-Type: application/json' -d '{
  "from":"now-24h","to":"now",
  "query":"level:ERROR AND source:"orders-api"",
  "size":50
}'

# Create an alert (runs every 60s)
curl -X POST http://localhost:8080/api/alerts -H 'Content-Type: application/json' -d '{
  "name":"5xx spike",
  "query":{"from":"now-5m","to":"now","query":"level:ERROR AND http:502","size":100},
  "threshold":{"operator":">=","value":1},
  "notify":{"type":"webhook","url":"http://webhook.site/replace-me"}
}'
```

## Notes
- Database file: `data/logs.db`
- Full-text via FTS5 (`logs_fts`) with triggers to sync from `logs`.
- For performance: WAL mode and reasonable PRAGMAs.
