# SafeCircle Backend — TODO

Branch: `backend` → merge to `master` when stable.
Rule: every feature step ends with tests + a git commit.

---

## What Has Been Done

- [x] Spring Boot project scaffolded (Boot 4.0.6, Java 26, Maven)
- [x] PostgreSQL + Flyway migrations (V1 schema, V2 seed data)
- [x] Multi-profile config (`dev`, `prod`, `application.yaml`)
- [x] Anonymous session creation (`POST /api/v1/sessions/anonymous`) — nickname generation, validation
- [x] Content feed (`GET /api/v1/content`) — in-memory seed, category filter, limit cap
- [x] Rule-based chat (`POST /api/v1/chat/messages`) — keyword matching (HIV, STI, PrEP, clinic)
- [x] Risk assessment (`POST /api/v1/risk/assess`) — 3-tier logic (HIGH/MEDIUM/LOW)
- [x] Clinic locator (`GET /api/v1/clinics`) — in-memory list, district/youthFriendly/service filters
- [x] Anonymous event tracking (`POST /api/v1/events`, `GET /api/v1/events`) — PII sanitization
- [x] Global exception handler (`IllegalArgumentException`, `MethodArgumentNotValidException`)
- [x] JPA entity classes + repositories for all modules
- [x] Unit tests: SessionService, ChatService, ContentService, ClinicService, RiskAssessmentService, EventService
- [x] Controller tests (MockMvc): Session, Chat, Risk, Clinic

---

## What Is NOT Done / Needs Work

### CRITICAL BUGS & ISSUES (fix first)

- [ ] **`SafeCircleBackendApplication.main()` is not `public`** — app cannot start
- [ ] **`SessionService` does NOT persist sessions to DB** — generates UUID in memory, never saves to `anonymous_session` table; all other modules that need a real session FK will fail
- [ ] **`EventService.trackEvent()` creates a blank `new AnonymousSession()`** instead of looking up the real session — will cause FK constraint violation on save
- [ ] **`ContentService` uses hardcoded in-memory list** — ignores the DB and `ContentItemRepository` entirely
- [ ] **`ClinicService` uses hardcoded in-memory list** — ignores `ClinicRepository` entirely
- [ ] **`ChatMessage.role` and `ChatMessage.source` are typed as `Object`** — should be proper enums (`ChatRole`, `ChatSource`)
- [ ] **`CorsConfig` is empty** — CORS is not configured; frontend calls will be blocked
- [ ] **`OpenApiConfig` is empty** — Swagger/OpenAPI docs not set up
- [ ] **`ApiSuccessResponse` is empty** — unused placeholder
- [ ] **`IdGenerator` and `TimeUtils` are empty** — unused placeholders
- [ ] **`EventServiceTest` instantiates `EventService` with `new EventService()`** but the real constructor requires `EventLogRepository` — test will fail to compile/run
- [ ] **`ApiSmokeTest` is empty** — integration smoke test not implemented
- [ ] **`SafeCircleBackendApplicationTests` (`@SpringBootTest`)** will fail without a running DB — needs `@DataJpaTest` or H2 test profile
- [ ] **`.env` contains a real password in plaintext** — must be excluded from git (check `.gitignore`)
- [ ] **`application-prod.yaml` datasource is commented out** — prod profile has no DB config

---

## Step-by-Step TODO Plan

### STEP 1 — Fix critical bugs (no new features)
- [ ] 1.1 Make `SafeCircleBackendApplication.main()` `public`
- [ ] 1.2 Fix `ChatMessage.role` and `ChatMessage.source` — create `ChatRole` and `ChatSource` enums, update the entity
- [ ] 1.3 Fix `CorsConfig` — add `@Configuration` + `WebMvcConfigurer` allowing frontend origin
- [ ] 1.4 Verify `.gitignore` excludes `.env`
- [ ] **Commit:** `fix: critical startup and entity bugs`

### STEP 2 — Wire SessionService to the database
- [ ] 2.1 Inject `AnonymousSessionRepository` into `SessionService`
- [ ] 2.2 Persist the `AnonymousSession` entity on creation, return the real DB-generated UUID as `sessionId`
- [ ] 2.3 Add `getSessionById(UUID)` helper used by other services
- [ ] 2.4 Update `SessionServiceTest` to mock the repository
- [ ] 2.5 Update `SessionControllerTest` if needed
- [ ] **Commit:** `feat(session): persist anonymous sessions to database`

### STEP 3 — Wire ContentService to the database
- [ ] 3.1 Inject `ContentItemRepository` into `ContentService`
- [ ] 3.2 Replace in-memory seed list with real DB queries (filter by `category`, `published=true`, apply `limit`)
- [ ] 3.3 Add `findByPublishedTrueAndCategoryOrderByCreatedAtDesc` query method or `@Query`
- [ ] 3.4 Update `ContentServiceTest` to mock the repository
- [ ] **Commit:** `feat(content): serve content feed from database`

### STEP 4 — Wire ClinicService to the database
- [ ] 4.1 Inject `ClinicRepository` into `ClinicService`
- [ ] 4.2 Replace in-memory list with DB queries; add `ClinicServiceRepository` query for service filter
- [ ] 4.3 Map `Clinic` + `ClinicService` entities to `ClinicResponse` DTO (include services list)
- [ ] 4.4 Update `ClinicServiceTest` to mock the repository
- [ ] **Commit:** `feat(clinic): serve clinic data from database`

### STEP 5 — Fix EventService session lookup
- [ ] 5.1 Inject `AnonymousSessionRepository` into `EventService`
- [ ] 5.2 Look up the real `AnonymousSession` by `sessionId` before saving `EventLog`; throw `IllegalArgumentException` if not found
- [ ] 5.3 Remove `System.out.println` — use proper `@Slf4j` logging
- [ ] 5.4 Fix `EventServiceTest` — inject a mocked `EventLogRepository` and `AnonymousSessionRepository`
- [ ] **Commit:** `fix(events): resolve session FK and add proper logging`

### STEP 6 — Fix ChatService session validation + persistence
- [ ] 6.1 Inject `AnonymousSessionRepository` and `ChatMessageRepository` into `ChatService`
- [ ] 6.2 Validate that the `sessionId` exists in DB before replying
- [ ] 6.3 Persist both the user message and the bot reply as `ChatMessage` rows
- [ ] 6.4 Add `GET /api/v1/chat/history?sessionId=` endpoint to retrieve chat history for a session
- [ ] 6.5 Update `ChatServiceTest` and `ChatControllerTest`
- [ ] **Commit:** `feat(chat): validate session and persist chat messages`

### STEP 7 — Fix RiskAssessmentService session validation + persistence
- [ ] 7.1 Inject `AnonymousSessionRepository` and `RiskAssessmentRepository` into `RiskAssessmentService`
- [ ] 7.2 Add `sessionId` to `RiskAssessmentRequest`
- [ ] 7.3 Validate session exists, then persist the `RiskAssessment` entity
- [ ] 7.4 Update `RiskAssessmentServiceTest` and `RiskControllerTest`
- [ ] **Commit:** `feat(risk): validate session and persist risk assessments`

### STEP 8 — OpenAPI / Swagger setup
- [ ] 8.1 Add `springdoc-openapi-starter-webmvc-ui` dependency to `pom.xml`
- [ ] 8.2 Implement `OpenApiConfig` with project title, version, description
- [ ] 8.3 Verify Swagger UI loads at `/swagger-ui.html`
- [ ] **Commit:** `feat(config): add OpenAPI/Swagger documentation`

### STEP 9 — Test infrastructure
- [ ] 9.1 Add H2 in-memory DB dependency (`test` scope) to `pom.xml`
- [ ] 9.2 Create `src/test/resources/application-test.yaml` with H2 datasource + Flyway enabled
- [ ] 9.3 Fix `SafeCircleBackendApplicationTests` to use `@ActiveProfiles("test")`
- [ ] 9.4 Implement `ApiSmokeTest` as a `@SpringBootTest` integration test hitting all main endpoints
- [ ] **Commit:** `test: add H2 test profile and API smoke tests`

### STEP 10 — Rate limiting & security hardening
- [ ] 10.1 Add Bucket4j or a simple `HandlerInterceptor`-based rate limiter on chat and risk endpoints
- [ ] 10.2 Add `@NotBlank` / `@Size` validation to any remaining unvalidated request fields
- [ ] 10.3 Confirm `application-prod.yaml` datasource is fully configured via env vars
- [ ] 10.4 Add `Content-Security-Policy` and `X-Content-Type-Options` headers via `SecurityFilterChain` or `WebMvcConfigurer`
- [ ] **Commit:** `feat(security): rate limiting and security headers`

### STEP 11 — Cleanup & polish
- [ ] 11.1 Delete empty utility classes (`IdGenerator`, `TimeUtils`) or implement them if needed
- [ ] 11.2 Delete or implement `ApiSuccessResponse` consistently across controllers
- [ ] 11.3 Replace all `System.out.println` with `@Slf4j` logging throughout
- [ ] 11.4 Add `@Tag` and `@Operation` Swagger annotations to all controllers
- [ ] **Commit:** `chore: cleanup empty stubs and standardize logging`

### STEP 12 — Pre-merge checklist
- [ ] 12.1 Run `mvn clean test` — all tests green
- [ ] 12.2 Run app locally, verify all endpoints via Swagger UI
- [ ] 12.3 Open Pull Request: `backend` → `master`
- [ ] 12.4 Review + merge

---

## Notes

- Services currently serving **mock/in-memory data** (Content, Clinic) will silently return stale data even after DB is populated — Steps 3 & 4 fix this.
- The `EventService` bug (Step 5) will cause a **runtime crash** the first time a real event is tracked against a real session — fix before any integration testing.
- `.env` password is committed — rotate the DB password after confirming `.gitignore` is correct.
