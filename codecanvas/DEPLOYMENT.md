# LoopLab — Deployment Guide

How to run LoopLab (repository: CodeCanvas) in production. Most people only need section 5a (Render). Verified locally on 2026-11-08 by running the
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
| `DB_URL` | yes, or the three below | `jdbc:postgresql://db-host:5432/codecanvas?sslmode=require` | Full JDBC URL |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | instead of `DB_URL` | `db-host` / `5432` / `codecanvas` | Used when the platform provides separate values (Render) |
| `DB_USERNAME` | **yes** | `codecanvas` | Database user |
| `DB_PASSWORD` | **yes** | _(from your secret store)_ | The app refuses to start without it |
| `APP_BASE_URL` | recommended | `https://looplab.onrender.com` | Public address used in password reset emails |
| `MAIL_FROM` | for password reset | `LoopLab <no-reply@yourdomain>` | Sender address (must be verified with your email provider) |
| `SPRING_MAIL_HOST` / `SPRING_MAIL_PORT` | for password reset | `smtp-relay.brevo.com` / `587` | SMTP server |
| `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` | for password reset | _(from your email provider)_ | SMTP login (store the password as a secret) |
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
Description=LoopLab
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
looplab.example.com {
    reverse_proxy localhost:8080
}
```
The prod profile trusts `X-Forwarded-Proto`/`X-Forwarded-Host` from the proxy, so redirects use `https://`,
session cookies are `Secure`, and HSTS is sent. **Do not expose port 8080 directly to the internet**; only the
proxy should reach it.

## 5a. Deploying on Render (recommended; this is how LoopLab is hosted)
You do **not** install PostgreSQL or Java yourself. Render builds the app from GitHub and runs both the website and
the PostgreSQL database for you. Everything Render needs is already in the repository:
`render.yaml` (repo root) describes the website and database, and `codecanvas/Dockerfile` builds and tests the app.

What `render.yaml` creates (check current prices at render.com/pricing):

| Name | What it is | Plan | Region |
|---|---|---|---|
| `looplab` | The website | Starter (paid, never sleeps) | Singapore |
| `looplab-db` | PostgreSQL 17 database | Basic-256MB (paid, daily backups) | Singapore |

**First deployment**
1. Sign up at render.com with **Sign in with GitHub**.
2. Add a payment card: **Account → Billing**.
3. **New → Blueprint** → choose the `CodeCanvas` repository → Render shows `looplab` and `looplab-db`.
4. Render asks for `APP_BASE_URL` and the email settings. Leave them empty for now if you like (only
   "Forgot password?" emails need them; see 5b).
5. Click **Apply**. The first build takes about 5-10 minutes (it runs all tests). The site is then live at
   `https://looplab.onrender.com` (the exact address is shown on the `looplab` service page).
6. Open the site, register, take a quiz, and check My Progress.

**Releasing an update later**
- Push to GitHub, then on the `looplab` service click **Manual Deploy → Deploy latest commit**
  (`autoDeploy` is off so nothing goes live by accident).
- If something breaks: **Events → Rollback** to the previous deploy (see section 8 for database migrations).

**Good to know**
- HTTPS is automatic. A custom domain (e.g. `looplab.in`) is added under **Settings → Custom Domains**; then set
  `APP_BASE_URL` to that address.
- If the site feels slow with many students online at once, change the `looplab` plan to a larger one in
  **Settings → Instance Type**. No code change is needed.
- Database backups: **looplab-db → Recovery** (restore to an earlier point in time).

## 5b. Email for password reset
"Forgot password?" emails a one-time link (valid 30 minutes, max 3 per account per hour). Any SMTP provider works;
a free option that needs no domain is **Brevo**: create an account, verify your sender email address, then create an
SMTP key under *SMTP & API*. Set:

| Variable | Value |
|---|---|
| `SPRING_MAIL_HOST` | `smtp-relay.brevo.com` |
| `SPRING_MAIL_PORT` | `587` |
| `SPRING_MAIL_USERNAME` | your Brevo SMTP login |
| `SPRING_MAIL_PASSWORD` | your Brevo SMTP key (secret) |
| `MAIL_FROM` | the verified sender, e.g. `LoopLab <you@example.com>` |
| `APP_BASE_URL` | the site's public `https://` address |

Without `SPRING_MAIL_HOST`, no email is sent: in development the link is printed in the log instead, and in
production a `mail.not_configured` warning is logged (the link is never logged in production).

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
| `login.locked username=…` | 5 wrong passwords in 15 minutes; that username is locked for 15 minutes |
| `password_reset.requested …` / `password_reset.completed …` | Forgot-password flow |
| `mail.sent` / `mail.failed` / `mail.not_configured` | Outcome of sending a reset email |
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

- Same jar started with `PORT=10000` and `DB_HOST`/`DB_PORT`/`DB_NAME` instead of `DB_URL` (as Render supplies them):
  connected, Flyway validated, readiness `UP`, login page 200.

**Not yet verified:** sending a real email over SMTP (no email account configured yet; the flow was tested with the
link taken from the dev log), the Docker image build (Docker is not installed locally; Render builds it on first deploy),
the live Render deployment, HTTPS certificates, managed database backups and restore.
