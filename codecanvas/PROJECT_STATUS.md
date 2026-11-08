# CodeCanvas — Project Status

_Last verified: 2026-11-08 · base checkpoint `24c05ab` + Phase 3 (uncommitted)_

## Stack
Java 25 · Spring Boot 4.1.0 (Spring MVC, Thymeleaf, Spring Security, Spring Data JPA, Validation) · Flyway · PostgreSQL 17 · Maven · HTML/CSS/JS inline in templates (+ `static/js/codecanvas-api.js`).

## Phase status
| Phase | Status |
|---|---|
| 1 — Baseline audit | Done |
| 2 — Core functionality | Done (verified; no new fixes needed) |
| 3 — Production foundations | Done and verified locally (see below) |
| 4 — Staging deployment | Not started — needs approval before any deploy |
| 5 — Release checklist | Not started |

## Verified working (tested, not assumed)
| Area | How verified | Result |
|---|---|---|
| Build + tests | `mvnw test` (H2 in PostgreSQL mode, same Flyway migration) | **26/26 pass** |
| PostgreSQL | App started against local PostgreSQL 17 | Flyway V1 applied; Hibernate `validate` passed |
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

## How it works now
- **Accounts:** `/register` (display name, username, password ×2) → `/` login (Spring Security form login, BCrypt via delegating encoder). Usernames are stored lower-case.
- **Sessions:** cookie-only, HttpOnly, SameSite=Lax, 120-minute timeout (longer than the 90-minute exam), session id rotated on login.
- **CSRF:** on for all forms and API writes; pages send the token from `<meta name="_csrf">` via `CodeCanvasApi.send()`.
- **Grading:** `POST /api/questions/{id}/check` gives instant feedback (not stored). `POST /api/quiz/submit` and `POST /api/exam/grade` re-grade on the server and store an `attempts` row. Browser-computed scores are never trusted.
- **Progress:** `GET /api/progress` (own data only), `DELETE /api/progress` (reset). Pages still read `localStorage`, but only as a display cache refreshed from the server on every load and cleared on the login page.
- **Secrets:** `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` from the environment; locally from gitignored `codecanvas/.env` (template: `.env.example`). The app refuses to start without `DB_PASSWORD`.
- **Schema:** Flyway migrations in `src/main/resources/db/migration` (`V1__students_and_attempts.sql`).

## Known gaps / next work
1. **No login rate limiting or account lockout** (brute-force protection) — add before public launch.
2. **No password reset or email verification**; no admin/teacher role.
3. **Old browser-only progress is not imported** — data saved only in `localStorage` before Phase 3 is not migrated (it was client-reported and cannot be trusted).
4. Question bank is still hard-coded in `QuestionRepository` (fine for now; move to DB if content editing is needed).
5. **Dev settings are the defaults** (Thymeleaf cache off, DevTools) — Phase 4 adds a `prod` profile, health check, logging and error pages.
6. CSP still allows `'unsafe-inline'` because all pages use inline scripts/styles.

## Local environment
- PostgreSQL 17 installed via winget, Windows service `postgresql-x64-17`, port 5432.
- Database `codecanvas`, owned by least-privilege role `codecanvas` (no superuser/createdb/createrole).
- Superuser password saved outside the repo at `%USERPROFILE%\.codecanvas\postgres-superuser.env`.
- Git repo root is the parent folder `CodeCanvas/`; `codecanvas - backup/` and stray `tatus --short` are intentionally untracked.
