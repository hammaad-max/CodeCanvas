# CodeCanvas — Deployment Guide

How to run CodeCanvas on a staging or production server. Verified locally on 2026-11-08 by running the
packaged jar with `SPRING_PROFILES_ACTIVE=prod` against PostgreSQL 17 (see "Verified" at the end).

## 1. What you need
| Component | Version | Notes |
|---|---|---|
| Java runtime | 25 (JRE or JDK) | `java -version` |
| PostgreSQL | 15+ (tested on 17) | Managed service recommended (automated backups) |
| Reverse proxy / load balancer | any | Terminates HTTPS and forwards to the app on port 8080 |

The app is a single self-contained jar: `target/CodeCanvas-0.0.1-SNAPSHOT.jar`.

## 2. Configuration (environment variables)
| Variable | Required | Example | Purpose |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | **yes** | `prod` | Production settings (secure cookies, caching, JSON logs) |
| `DB_URL` | **yes** | `jdbc:postgresql://db-host:5432/codecanvas?sslmode=require` | JDBC URL |
| `DB_USERNAME` | **yes** | `codecanvas` | Database user |
| `DB_PASSWORD` | **yes** | _(from your secret store)_ | The app refuses to start without it |
| `PORT` | no | `8080` | HTTP port (default 8080) |
| `JAVA_TOOL_OPTIONS` | no | `-Xmx512m` | JVM memory limit |

Never commit secrets. Use the hosting platform's secret manager or a root-only environment file.
`.env` files are only for local development (`.env.example` is the template).

## 3. Database setup (once per environment)
Run as a PostgreSQL admin. Use a strong generated password.
```sql
CREATE ROLE codecanvas LOGIN PASSWORD '<generated-password>' NOSUPERUSER NOCREATEDB NOCREATEROLE;
CREATE DATABASE codecanvas OWNER codecanvas ENCODING 'UTF8';
REVOKE ALL ON DATABASE codecanvas FROM PUBLIC;
```
Tables are created and upgraded automatically by **Flyway** on startup
(`src/main/resources/db/migration`). Never edit an applied migration; add a new `V2__…sql` instead.
`flyway clean` is disabled in production.

## 4. Build
```bash
./mvnw clean verify
```
`verify` runs all tests (they use in-memory H2, no database needed) and produces the jar.
Release only from a commit where the tests pass.

## 5. Run
```bash
SPRING_PROFILES_ACTIVE=prod DB_URL=... DB_USERNAME=... DB_PASSWORD=... \
  java -jar CodeCanvas-0.0.1-SNAPSHOT.jar
```
Startup takes about 6 seconds. Check it is up:
```bash
curl -fsS http://localhost:8080/actuator/health
```

### Linux service (systemd) example
`/etc/codecanvas/codecanvas.env` (owner root, mode 600):
```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:postgresql://db-host:5432/codecanvas?sslmode=require
DB_USERNAME=codecanvas
DB_PASSWORD=<secret>
```
`/etc/systemd/system/codecanvas.service`:
```ini
[Unit]
Description=CodeCanvas
After=network-online.target

[Service]
User=codecanvas
EnvironmentFile=/etc/codecanvas/codecanvas.env
ExecStart=/usr/bin/java -Xmx512m -jar /opt/codecanvas/codecanvas.jar
Restart=on-failure
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target
```

### HTTPS reverse proxy (Caddy example — automatic Let's Encrypt certificates)
```
staging.example.com {
    reverse_proxy localhost:8080
}
```
The prod profile trusts `X-Forwarded-Proto`/`X-Forwarded-Host` from the proxy, so redirects use `https://`,
session cookies are `Secure`, and HSTS is sent. **Do not expose port 8080 directly to the internet**; only the
proxy should reach it.

## 6. Health checks and monitoring
| Endpoint | Use | Response |
|---|---|---|
| `GET /actuator/health` | Uptime monitor | `200 {"status":"UP"}` or `503` (includes database check) |
| `GET /actuator/health/liveness` | Restart the process if failing | `200 {"status":"UP"}` |
| `GET /actuator/health/readiness` | Send traffic only when ready (checks DB) | `200 {"status":"UP"}` |

No other actuator endpoints are exposed, and health responses contain no component details.

## 7. Logs
In the prod profile every log line is one JSON object (Elastic Common Schema) on stdout. Useful messages:
| Message | Meaning |
|---|---|
| `login.success username=…` / `login.failure username=… reason=…` | Sign-in audit |
| `student.registered id=… username=…` | New account |
| `attempt.quiz …` / `attempt.exam …` | Graded attempt stored |
| `progress.reset …` | Student reset their progress |
| `Unhandled API error reference=<uuid>` | Unexpected failure; the student sees the same reference id |

Passwords are never logged. Example: `journalctl -u codecanvas -f | grep login.failure`.

## 8. Releasing a new version and rolling back
1. Back up the database (see section 9).
2. Build and test the new jar (`./mvnw clean verify`), keep the previous jar alongside it.
3. Stop the service, swap the jar, start the service, and check `/actuator/health`.
4. Smoke test: register or sign in, take one quiz, open the progress page.

**Rollback:** stop, put the previous jar back, start. The previous jar ignores migrations newer than itself, so
it keeps working as long as the new migration only *added* tables or columns. If a migration renamed or removed
something the old code uses, the old jar fails its schema check at startup: restore the pre-release database
backup, then start the previous jar. For that reason, write migrations so they only add things whenever possible.

## 9. Backups (minimum)
- Prefer a managed PostgreSQL with daily automated backups and point-in-time recovery.
- Self-hosted: `pg_dump -Fc -U codecanvas codecanvas > codecanvas-$(date +%F).dump` daily, copied off the server.
- **Test a restore** into a scratch database before launch and periodically:
  `pg_restore -d codecanvas_restore_test codecanvas-YYYY-MM-DD.dump`.

## 10. Verified locally (2026-11-08)
- `mvnw clean package` → jar without DevTools or H2.
- Ran with `SPRING_PROFILES_ACTIVE=prod` and DB settings **only** from environment variables (no `.env`).
- Flyway validated the schema; health, liveness and readiness returned `UP`.
- Cookies: `Secure; HttpOnly; SameSite=Lax`. With `X-Forwarded-Proto: https`: HSTS header present and
  redirects to `https://`.
- Static JS served with 7-day caching under content-hashed URLs.
- Browser: register → wrong password rejected → sign in → quiz → ICSE mock exam → scores stored → branded 404 page.
- JSON logs contained the audit events above, with no passwords and no errors.

**Not yet verified:** a real staging host, HTTPS certificates, managed database backups and restore.
