# Splunk‑Lite — SQLite + FTS5 (Single‑Service Spring Boot)

Minimal Splunk-like logs app without Docker or external services. Uses SQLite with FTS5 for full‑text search.

## Quickstart
```bash
mvn -DskipTests clean package
java -jar target/splunk-lite-sqlite-fts-0.0.1-SNAPSHOT.jar
```

## Email testing (alerts)

To enable email notifications for alerts, configure SMTP properties in `src/main/resources/application.properties`.

Example (Gmail app password / real SMTP):

```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

For local testing without real SMTP, you can run MailHog (or similar) and point the app at it:

```
# run MailHog locally (separate terminal)
# download MailHog and run the binary, or use Docker: docker run -p 1025:1025 -p 8025:8025 mailhog/mailhog

spring.mail.host=localhost
spring.mail.port=1025
```

Restart the backend after changing properties. Create an alert via the UI or POST `/api/alerts` with `notify.to` set to comma-separated emails; when the alert fires the app will send email(s).

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
  ```markdown
  # Splunk‑Lite — Logs demo (Spring Boot backend + React frontend)

  Small Splunk-like app for ingesting, searching and alerting on logs. This repository contains two pieces:

  - Backend: Spring Boot (Java) application exposing /api endpoints and persisting logs to a MySQL (or local) datasource.
  - Frontend: React app (in `frontend/`) providing UI for ingest, search, alerts and file upload.

  ## Prerequisites
  - Java 17+ / JDK 21
  - Maven
  - Node.js (16+) and npm
  - A running MySQL instance (or adjust `src/main/resources/application.properties` to use your DB)

  ---

  ## Backend — build & run

  From the repository root:

  Development (run from sources):
  ```powershell
  mvn -DskipTests spring-boot:run
  ```

  Build a jar and run it:
  ```powershell
  mvn -DskipTests clean package
  java -jar target/splunk-lite-sqlite-fts-0.0.1-SNAPSHOT.jar
  ```

  Notes:
  - The backend listens on port 8080 by default. If that port is in use change `server.port` in `application.properties`.
  - Important files: `src/main/resources/schema.sql` (creates `logs` table), `src/main/java/com/example/splunklite/api/UploadController.java` (file upload), `svc/Indexer.java` (persists logs), `svc/Searcher.java` (search endpoint).

  ---

  ## Frontend — build & run

  Change into the frontend directory and install dependencies, then start the dev server:
  ```powershell
  cd frontend
  npm install
  npm start
  ```

  - The React dev server runs on port 3000 by default and proxies API calls to the backend (see `package.json` proxy). If you run frontend and backend locally, start the backend first.

  To build a production bundle:
  ```powershell
  cd frontend
  npm run build
  ```
  The static build will be created in `frontend/build`.

  ---

  ## Run both (development)

  1. Start the backend (repo root): `mvn -DskipTests spring-boot:run`
  2. Start the frontend (separate terminal): `cd frontend; npm start`
  3. Open http://localhost:3000 in your browser.

  This setup uses the CRA dev server proxy so API calls from the UI are forwarded to the backend.

  ---

  ## Useful API examples

  - Ingest a single log (JSON):
  ```powershell
  curl -X POST http://localhost:8080/api/logs -H "Content-Type: application/json" -d '{"source":"orders-api","env":"dev","level":"ERROR","message":"Failed to charge card","ts":1692814210000}'
  ```

  - Search logs (wide time window):
  ```powershell
  curl -X POST http://localhost:8080/api/search -H "Content-Type: application/json" -d '{"from":"1970-01-01T00:00:00Z","to":"now","size":50}'
  ```

  - Upload a file of logs (each line JSON or CSV/TSV):
  ```powershell
  curl -F file=@mylogs.txt http://localhost:8080/api/upload
  ```

  The upload endpoint returns JSON with `{ count: <n>, errors: [...] }` on success.

  ---

  ## Troubleshooting

  - If the React dev server shows a `Proxy error` when uploading, ensure the backend is running on port 8080 and reachable (try `http://localhost:8080/actuator/health` or `curl http://localhost:8080/api/search`).
  - If the app cannot connect to the database, verify `application.properties` datasource values and that the user has privileges to create the schema (the app runs `schema.sql` on startup).

  ---

  ## Notes
  - Database table `logs` schema lives in `src/main/resources/schema.sql`.
  - Uploaded logs are parsed line-by-line: JSON preferred; fallback to CSV/TSV `source,env,level,message,ts` (ts optional, epoch millis).

  ``` 

