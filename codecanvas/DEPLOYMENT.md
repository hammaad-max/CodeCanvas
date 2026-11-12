# TraceTables — Deployment Guide

How to run TraceTables (repository: CodeCanvas) in production. Most people only need section 5a (Render). Verified locally on 2026-11-08 by running the
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
| `APP_BASE_URL` | recommended | `https://tracetables.onrender.com` | Public address used in password reset emails |
| `MAIL_FROM` | for password reset | `TraceTables <no-reply@yourdomain>` | Sender address (must be verified with your email provider) |
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
Description=TraceTables
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
tracetables.example.com {
    reverse_proxy localhost:8080
}
```
The prod profile trusts `X-Forwarded-Proto`/`X-Forwarded-Host` from the proxy, so redirects use `https://`,
session cookies are `Secure`, and HSTS is sent. **Do not expose port 8080 directly to the internet**; only the
proxy should reach it.

## 5a. Free deployment: Render (website) + Neon (database)
You do **not** install Java or PostgreSQL. Both services are free and need no card:

| Service | Used for | Free plan limits |
|---|---|---|
| **Neon** (neon.tech) | The PostgreSQL database (accounts, scores) | 0.5 GB storage, does **not** expire |
| **Render** (render.com) | The website `tracetables` | Sleeps after ~15 min with no visitors; the next visit takes about a minute |

Render's own free database is deleted after 30 days, so it is not used.

**Step 1: create the database on Neon (about 3 minutes)**
1. Go to neon.tech, click **Sign up**, and sign in with GitHub.
2. Create a project: name `tracetables`, PostgreSQL version 17, region **AWS Asia Pacific (Singapore)**.
3. On the project dashboard click **Connect**. Turn **Connection pooling off** (Flyway needs a direct connection).
4. Note three things from the connection details (the screen may look slightly different):
   - **Host**, e.g. `ep-cool-name-123456.ap-southeast-1.aws.neon.tech`
   - **Role** (user), e.g. `neondb_owner`
   - **Password** (click the eye icon to show it)
   The database name is `neondb` unless you changed it.

**Step 2: create the website on Render (about 10 minutes)**
1. Go to render.com and sign in with GitHub.
2. Click **New → Blueprint** and choose the `CodeCanvas` repository. Render reads `render.yaml` and shows one
   service, `tracetables` (Free).
3. Render asks for the values below. Type them exactly:

| Key | What to enter |
|---|---|
| `DB_URL` | `jdbc:postgresql://YOUR-NEON-HOST/neondb?sslmode=require` (replace YOUR-NEON-HOST with the host from Neon) |
| `DB_USERNAME` | the Neon role, e.g. `neondb_owner` |
| `DB_PASSWORD` | the Neon password |
| `APP_BASE_URL` | leave empty for now |
| `MAIL_FROM`, `SPRING_MAIL_HOST`, `SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD` | leave empty for now (only "Forgot password?" emails need them, see 5c) |

4. Click **Apply**. The first build takes about 5-10 minutes (it runs all the tests). When the service shows
   **Live**, open the address at the top of the page, e.g. `https://tracetables.onrender.com`.
5. Register, take a quiz, and check My Progress. The tables are created in Neon automatically on first start.

**If the build or start fails:** open the `tracetables` service → **Logs**, copy the last lines, and look for
`password authentication failed` (wrong `DB_USERNAME`/`DB_PASSWORD`) or `UnknownHost` (wrong host in `DB_URL`).
Fix the value under **Environment**, then **Manual Deploy → Deploy latest commit**.

**Releasing an update later**
- Push to GitHub, then on `tracetables` click **Manual Deploy → Deploy latest commit**
  (`autoDeploy` is off, so nothing goes live by accident).
- If something breaks: **Events → Rollback** to the previous deploy (see section 8 for database migrations).

**Good to know**
- HTTPS is automatic. A custom domain (e.g. `tracetables.in`) is added under **Settings → Custom Domains**; then set
  `APP_BASE_URL` to that address.
- The free website sleeps when nobody uses it. To stop that later, change it to the Starter plan in
  **Settings → Instance Type** (paid). No code change is needed.
- Neon's free plan keeps a short restore history; for real backups take a weekly copy with `pg_dump` (section 9).
- Tested locally with the same settings: the app starts and uses about 290 MB of memory with a 384 MB limit,
  within the free plan's 512 MB.

## 5b. Your own domain name
`tracetables.onrender.com` works, but your own address (for example `tracetables.in`) looks far more professional
and lets you send email from it. About 20 minutes, plus waiting for DNS (usually minutes, sometimes a few hours).

1. **Buy the domain** at a registrar such as Cloudflare, Namecheap, GoDaddy or Hostinger. Check it is free first.
   `.in` is cheapest for India; `.com` is the most recognised. Turn on **auto-renew**.
2. **Add it in Render:** open the `tracetables` service, then **Settings, Custom Domains, Add Custom Domain**, and type
   your domain (for example `tracetables.in`). Add `www.tracetables.in` too if you like.
3. **Add the DNS records Render shows you.** Render lists exactly what to create: usually an **A record** for the plain
   domain and a **CNAME record** for `www`. In your registrar open **DNS settings**, add them exactly as shown, and
   delete any parking-page records the registrar added for the same names. Copy the values from Render's screen, not
   from this guide, because they can change.
4. **Wait, then press Verify** in Render. When it says verified, Render issues the HTTPS certificate itself.
5. **Tell the app its address:** in Render open **Environment** and set `APP_BASE_URL` to `https://tracetables.in`
   (your real domain). Save and deploy. Password reset emails and link previews then use your domain.
6. **Redirect the old address (optional):** leave `tracetables.onrender.com` working; it simply shows the same site.

**A contact address without publishing your personal email:** the footer's "Report a problem" link and the Privacy and
Terms pages show an address only when `CONTACT_EMAIL` is set in Render. Use an address on your domain such as
`help@tracetables.in`. If you use Cloudflare for DNS, its free **Email Routing** forwards `help@yourdomain` to your
own inbox, so students never see your personal address. Until `CONTACT_EMAIL` is set, no address is shown at all.

## 5c. Email for password reset
"Forgot password?" emails a one-time link (valid 30 minutes, max 3 per account per hour). The free **Brevo** plan
(about 300 emails a day) is plenty. Setting it up properly takes about 20 minutes and needs your own domain (5b), because
email sent "from" a Gmail or Yahoo address through another service is often put in spam.

1. **Create a Brevo account** at brevo.com and finish their sign-up questions.
2. **Add your domain:** in Brevo open **Senders, Domains and Dedicated IPs, Domains, Add a domain**, enter your domain.
3. **Add the DNS records Brevo shows** (a verification TXT record plus DKIM and DMARC records) in your registrar's DNS
   settings, then press **Authenticate** in Brevo. This proves the email really comes from you.
4. **Create the sender:** under **Senders** add `no-reply@yourdomain` (any address on your domain).
5. **Get SMTP details:** open **SMTP and API, SMTP**. Note the **login** and click **Generate a new SMTP key**; copy the
   key straight away because it is shown once.
6. **Enter these in Render, on the `tracetables` service under Environment:**

| Variable | Value |
|---|---|
| `SPRING_MAIL_HOST` | `smtp-relay.brevo.com` |
| `SPRING_MAIL_PORT` | `587` |
| `SPRING_MAIL_USERNAME` | the SMTP login Brevo shows |
| `SPRING_MAIL_PASSWORD` | the SMTP key you generated (secret) |
| `MAIL_FROM` | `TraceTables <no-reply@yourdomain>` |
| `APP_BASE_URL` | `https://yourdomain` |
| `CONTACT_EMAIL` | `help@yourdomain` (shown in the footer and legal pages) |

7. **Save, deploy, and test:** on the live site open **Forgot password?**, enter your own username, and check that the
   email arrives (look in spam the first time). In Render's **Logs** you should see `mail.sent type=password_reset`.
   If you see `mail.failed`, the SMTP key or login is wrong; if you see `mail.not_configured`, `SPRING_MAIL_HOST` or
   `MAIL_FROM` is empty.

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
