# LoopLab — Project Status

_Last verified: 2026-11-09 · LoopLab update in `195fcd0`; Render set to paid plans in Singapore (`looplab` + `looplab-db`)_

The site is now called **LoopLab** (repository and Java package names are unchanged).

## Stack
Java 25 · Spring Boot 4.1.0 (Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Validation) · Flyway · PostgreSQL 17 · Maven · HTML/CSS/JS inline in templates (+ `static/js/codecanvas-api.js`).

## Phase status
| Phase | Status |
|---|---|
| 1 — Baseline audit | Done |
| 2 — Core functionality | Done (verified; no new fixes needed) |
| 3 — Production foundations | Done and verified locally (see below) |
| 4 — Deployment | Render, paid plans, Singapore (`render.yaml`). **Not deployed yet**: needs the Render account and Blueprint apply. See `DEPLOYMENT.md` §5a |
| 5 — Release checklist | Not started |
| Content and account update | ICSE rebuilt (8 lessons), ISC added (8 lessons, 80 questions, mock exam), LoopLab branding, show-password, forgot password, login lockout. Verified locally (tests + browser); committed in `195fcd0` |

## Verified working (tested, not assumed)
| Area | How verified | Result |
|---|---|---|
| Build + tests | `mvnw clean verify` (H2 in PostgreSQL mode, same Flyway migrations) | **50/50 pass** |
| Production mode | Packaged jar, `prod` profile, DB config from env vars only | Starts in ~6 s; JSON logs; health/liveness/readiness `UP` |
| Health check | Real HTTP tests + curl | Public status only; all other actuator endpoints blocked |
| Error handling | Real HTTP tests + browser | Branded 404/403/500 page; API errors are short JSON, unexpected ones get a reference id |
| HTTPS readiness | curl with `X-Forwarded-Proto: https` | `Secure` cookies, HSTS, `https://` redirects |
| Audit logging | Prod log inspected | Registration, login success/failure, attempts, resets logged; no passwords |
| PostgreSQL | App started against local PostgreSQL 17 | Flyway V1–V3 applied; Hibernate `validate` passed |
| Registration | Browser + tests | Account created; duplicate username (any case) rejected; field validation; CSRF required |
| Passwords | `students` table inspected | `{bcrypt}$2a$10…` hashes only, never plain text |
| Login / logout | Browser + tests | Right password → dashboard; wrong → error; logout ends session, API then 401 |
| Access control | Tests | Pages redirect anonymous users to `/`; `/api/**` returns 401 |
| Quiz | Browser (incl. retry-missed) | Server re-grades, stores attempt; page score = stored score |
| Mock exam | Browser + tests | Only the server-issued 40 questions accepted; graded once (re-submit → 409); stored |
| Progress | Browser + tests | Dashboard/progress read from server; per-student isolation; reset deletes only own attempts |
| Answer protection | Tests | Question/exam APIs never include `correctAnswer`/`explanation` |
| Security headers | Tests + browser | CSP, `X-Frame-Options: DENY`, `nosniff`, referrer policy; Google Fonts still load |
| Pages | Browser while signed in | All pages load with correct titles, no new console errors; design unchanged |
| Lessons | Generator compiles and runs every example program with Java 25 | All 16 ICSE/ISC "Expected Console Output" sections match the real output |
| Question bank | Tests + Java run of every code-output question | 240 questions (80 AP CS, 80 ICSE, 80 ISC); 10 per unit; answers match an option; no duplicate options |
| ICSE / ISC | Browser + tests | Hubs share the AP CS hub design; lesson pages, quizzes, mock exams, weak-topic review, progress tabs |
| Login lockout | Tests | 5 wrong passwords in 15 min locks that username for 15 min; success or reset clears it |
| Forgot password | Browser (link from dev log) + tests | One-time 30-minute link, hashed in DB, max 3/hour, same answer for unknown accounts |
| Phone layout | Browser at 375 px | New pages fit; dashboard nav fixed. Exam start screen has a 2 px overflow that also exists on the unchanged AP exam |

## How it works now
- **Accounts:** `/register` (display name, username, email, password ×2) → `/` login (Spring Security form login, BCrypt via delegating encoder). Usernames and emails are stored lower-case. `/account` changes email and password; accounts created before email was required see an "Add email" reminder on the dashboard.
- **Forgot password:** `/forgot-password` emails a one-time link to `/reset-password` (SMTP settings in `DEPLOYMENT.md` §5b). Only a SHA-256 hash of the token is stored.
- **Brute-force protection:** 5 wrong passwords for a username within 15 minutes lock it for 15 minutes (in memory, per app instance).
- **Courses:** AP CS (`/apcs`), ICSE (`/icse`, Class 9–10) and ISC (`/isc`, Class 11–12), each with 8 lesson pages, 10-question quizzes per unit and a 40-question mock exam. Attempts are stored with curriculum `APCS`, `ICSE` or `ISC`.
- **Sessions:** cookie-only, HttpOnly, SameSite=Lax, 120-minute timeout (longer than the 90-minute exam), session id rotated on login.
- **CSRF:** on for all forms and API writes; pages send the token from `<meta name="_csrf">` via `CodeCanvasApi.send()`.
- **Grading:** `POST /api/questions/{id}/check` gives instant feedback (not stored). `POST /api/quiz/submit` and `POST /api/exam/grade` re-grade on the server and store an `attempts` row. Browser-computed scores are never trusted.
- **Progress:** `GET /api/progress` (own data only), `DELETE /api/progress` (reset). Pages still read `localStorage`, but only as a display cache refreshed from the server on every load and cleared on the login page.
- **Secrets:** `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` from the environment; locally from gitignored `codecanvas/.env` (template: `.env.example`). The app refuses to start without `DB_PASSWORD`.
- **Schema:** Flyway migrations in `src/main/resources/db/migration`: V1 students and attempts, V2 email and password reset tokens, V3 ISC curriculum.

## Known gaps / next work
1. Login lockout is per username and in memory: someone who knows a username can lock it for 15 minutes, and counters reset when the app restarts. Per-IP limiting needs the real client IP verified on the host first.
2. **Email addresses are not verified**, and real email sending is untested until SMTP settings are configured; no admin/teacher role.
3. **Old browser-only progress is not imported** — data saved only in `localStorage` before Phase 3 is not migrated (it was client-reported and cannot be trusted).
4. Question bank is still hard-coded in `QuestionRepository` (fine for now; move to DB if content editing is needed).
5. **Production requires `SPRING_PROFILES_ACTIVE=prod`** — without it the app runs with development defaults (template cache off, non-Secure cookies).
6. CSP still allows `'unsafe-inline'` because all pages use inline scripts/styles.

## Local environment
- PostgreSQL 17 installed via winget, Windows service `postgresql-x64-17`, port 5432.
- Database `codecanvas`, owned by least-privilege role `codecanvas` (no superuser/createdb/createrole).
- Superuser password saved outside the repo at `%USERPROFILE%\.codecanvas\postgres-superuser.env`.
- Git repo root is the parent folder `CodeCanvas/`; `codecanvas - backup/` and stray `tatus --short` are intentionally untracked.
