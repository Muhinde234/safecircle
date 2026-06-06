# SafeCircle — Deployment and Testing Guide

This guide provides comprehensive instructions for running, testing, and deploying the SafeCircle application. It covers both the Spring Boot Java backend (`SafeCircle_backend`) and the Next.js frontend (`safecircle_frontend`), along with details for testing APIs via Swagger UI and Postman.

---

## Table of Contents
1. [Overview](#1-overview)
2. [Local Development Setup](#2-local-development-setup)
   - [Prerequisites](#prerequisites)
   - [Database Configuration](#database-configuration)
   - [Running the Backend](#running-the-backend)
   - [Running the Frontend](#running-the-frontend)
3. [Swagger UI Testing Guide](#3-swagger-ui-testing-guide)
4. [Postman Testing Guide](#4-postman-testing-guide)
   - [Importing & Settings](#importing--settings)
   - [API Reference with Request Payloads](#api-reference-with-request-payloads)
5. [Frontend Routing & Page Guide](#5-frontend-routing--page-guide)
6. [Production Deployment Guide](#6-production-deployment-guide)
   - [Backend Deployment](#backend-deployment)
   - [Frontend Deployment](#frontend-deployment)
7. [Git & GitHub Commands](#7-git--github-commands)

---

## 1. Overview

SafeCircle is a privacy-first digital platform tailored to connect young people in Rwanda with Sexual and Reproductive Health (SRH) services, anonymous guidance, and youth-friendly clinics in Kigali.

*   **Backend Application Port:** `8089` (Base URL: `http://localhost:8089/api/v1`)
*   **Frontend Client Port:** `3000` (URL: `http://localhost:3000`)
*   **Database:** PostgreSQL (H2 Database is used automatically for the `test` profile)

---

## 2. Local Development Setup

### Prerequisites
*   **Java Development Kit (JDK):** Version 26
*   **Node.js:** Version 18.0 or higher (with `npm` package manager)
*   **PostgreSQL:** Version 14 or higher (running locally on port `5432`)
*   **Maven:** Version 3.9+ (or use the provided `./mvnw` wrapper in the backend directory)

### Database Configuration
Ensure a PostgreSQL database named `safecircle` is running locally.

Create a `.env` file in the `SafeCircle_backend/` directory:
```env
SPRING_PROFILES_ACTIVE=dev
DB_URL=jdbc:postgresql://localhost:5432/safecircle
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password
```

### Running the Backend
1. Open a terminal in the `SafeCircle_backend/` directory.
2. Build and run the project using Maven:
   ```bash
   mvn clean spring-boot:run
   ```
3. The Flyway migrations will run automatically on startup to scaffold the schema and insert seed data.
4. Verify the backend is up by opening [http://localhost:8089/actuator/health](http://localhost:8089/actuator/health) (should return `{"status":"UP"}`).

### Running the Frontend
1. Open a terminal in the `safecircle_frontend/` directory.
2. Install dependencies:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
4. Access the frontend app at [http://localhost:3000](http://localhost:3000).

---

## 3. Swagger UI Testing Guide

The Spring Boot backend uses `springdoc-openapi` to automatically generate documentation for all controllers.

*   **Swagger UI URL:** [http://localhost:8089/swagger-ui/index.html](http://localhost:8089/swagger-ui/index.html) (or simply [http://localhost:8089/swagger-ui.html](http://localhost:8089/swagger-ui.html) which redirects)
*   **OpenAPI OpenAPI Docs JSON:** [http://localhost:8089/v3/api-docs](http://localhost:8089/v3/api-docs)

### How to Test in Swagger UI:
1. Open the **Swagger UI** URL in your browser.
2. Expand any tag (e.g., **Session Management**, **Chat Operations**, **Risk Assessment**).
3. Click the **Try it out** button on any endpoint.
4. Populate parameters or JSON request bodies as outlined in the [API Reference](#api-reference-with-request-payloads) section below.
5. Click **Execute** to see the response code, headers, and JSON body directly.

---

## 4. Postman Testing Guide

### Importing & Settings
1. Open Postman.
2. Click **Import** in the top-left corner.
3. Select the **Link** tab and paste the OpenAPI JSON schema link: `http://localhost:8089/v3/api-docs` (with the backend running).
4. Postman will generate a complete collection containing all endpoints, request parameter inputs, and boilerplate bodies.
5. Configure a collection variable or environment variable named `baseUrl` with the value `http://localhost:8089/api/v1`.

### API Reference with Request Payloads

#### **1. Session Management**

*   **POST** `/sessions/anonymous` — Create an anonymous session.
    *   *Headers:* `Content-Type: application/json`
    *   *Body:*
        ```json
        {
          "nickname": "Agasaro"
        }
        ```
    *   *Response Example (201 Created):*
        ```json
        {
          "sessionId": "a823c5d6-8486-4f40-9a3d-3df38e8a6142",
          "nickname": "Agasaro",
          "createdAt": "2026-06-06T00:15:30Z"
        }
        ```

*   **POST** `/sessions/{sessionId}/bookmarks` — Save a clinic or content bookmark.
    *   *Path Variable:* `sessionId` (the UUID from the session creation response)
    *   *Query Parameters:*
        *   `type` (e.g. `CLINIC`, `CONTENT`)
        *   `targetId` (the UUID of the clinic or content item)
    *   *Response:* `201 Created` with empty body.

*   **DELETE** `/sessions/{sessionId}/bookmarks` — Remove a saved bookmark.
    *   *Path Variable:* `sessionId`
    *   *Query Parameters:* `type`, `targetId`
    *   *Response:* `204 No Content` with empty body.

*   **GET** `/sessions/{sessionId}/bookmarks` — Get all bookmarks for a session.
    *   *Path Variable:* `sessionId`
    *   *Response Example (200 OK):*
        ```json
        [
          {
            "bookmarkType": "CLINIC",
            "targetId": "d4f0df4a-86f7-4de1-aec5-6d8cbc730001",
            "createdAt": "2026-06-06T00:20:15Z"
          }
        ]
        ```

#### **2. Clinic Locator**

*   **GET** `/clinics` — Search and filter youth-friendly clinics.
    *   *Query Parameters (All optional):*
        *   `district` (e.g. `Gasabo`, `Nyarugenge`, `Kicukiro`)
        *   `youthFriendly` (`true` / `false`)
        *   `service` (e.g., `Family Planning`)
    *   *Response Example (200 OK):*
        ```json
        [
          {
            "id": "d4f0df4a-86f7-4de1-aec5-6d8cbc730001",
            "name": "Kigali Youth Center Clinic",
            "district": "Gasabo",
            "address": "KG 345 ST 6",
            "youthFriendly": true,
            "contactInfo": "+250780000000",
            "services": ["Family Planning", "STI Testing"],
            "whatToExpect": "Private counseling and STI testing in a non-judgmental space."
          }
        ]
        ```

#### **3. Educational Content**

*   **GET** `/content` — List educational content feed.
    *   *Query Parameters:*
        *   `category` (optional, e.g., `HIV`, `STI`, `MYTHS`)
        *   `limit` (optional integer)
    *   *Response Example (200 OK):*
        ```json
        {
          "items": [
            {
              "id": "d4f0df4a-86f7-4de1-aec5-6d8cbc731001",
              "title": "What is PrEP?",
              "category": "HIV",
              "excerpt": "Daily HIV prevention option.",
              "body": "Pre-Exposure Prophylaxis (PrEP) is a daily pill...",
              "language": "en",
              "isPublished": true,
              "isFeatured": true,
              "audioUrl": "https://safecircle.org/audio/prep-en.mp3"
            }
          ]
        }
        ```

*   **GET** `/content/low-bandwidth` — Light-weight, low-bandwidth text-only feed.
    *   *Query Parameters:* `category`, `limit`
    *   *Response:* Format identical to standard feed but optimizes for payload text size.

#### **4. Risk Assessment ("I Messed Up" flow)**

*   **GET** `/risk/questionnaire` — Fetch the dynamic branching question decision-tree.
    *   *Response Example (200 OK):* Returns nodes containing questions, options, and branches.

*   **POST** `/risk/assess` — Submit user responses and get risk evaluation.
    *   *Headers:* `Content-Type: application/json`
    *   *Body:*
        ```json
        {
          "sessionId": "a823c5d6-8486-4f40-9a3d-3df38e8a6142",
          "eventType": "UNPROTECTED_SEX",
          "hoursSinceEvent": 48,
          "symptomsPresent": false
        }
        ```
    *   *Response Example (200 OK):*
        ```json
        {
          "riskLevel": "HIGH",
          "recommendedAction": "Please visit an emergency clinic for PEP (Post-Exposure Prophylaxis).",
          "urgencyWindow": "Within 24 hours"
        }
        ```

#### **5. Chat Support**

*   **POST** `/chat/messages` — Send a message and get a rule-based reply (persisted in DB).
    *   *Headers:* `Content-Type: application/json`
    *   *Body:*
        ```json
        {
          "sessionId": "a823c5d6-8486-4f40-9a3d-3df38e8a6142",
          "message": "Do I need PEP after unprotected sex?",
          "language": "en"
        }
        ```
    *   *Response Example (200 OK):*
        ```json
        {
          "reply": "It's brave to ask. Testing is the only way to know your status. Would you like to find a clinic?",
          "source": "RULE_BASED",
          "timestamp": "2026-06-06T00:25:00Z"
        }
        ```

*   **GET** `/chat/history` — Fetch chat history for an anonymous session.
    *   *Query Parameters:* `sessionId`
    *   *Response Example (200 OK):* Returns all user and assistant chat logs in chronological order.

#### **6. Content & Chat Moderation**

*   **PUT** `/moderation/chat-messages/{messageId}/flag` — Flag sensitive messages for review.
    *   *Path Variable:* `messageId`
    *   *Body:*
        ```json
        {
          "moderationNotes": "Highly distressed, emergency guideline triggered"
        }
        ```
    *   *Response:* `200 OK` with moderated message payload.

*   **GET** `/moderation/chat-messages/flagged` — List flagged messages.
    *   *Response:* List of flagged chat messages.

#### **7. Telemetry & Analytics**

*   **POST** `/events` — Track anonymous interaction log.
    *   *Body:*
        ```json
        {
          "sessionId": "a823c5d6-8486-4f40-9a3d-3df38e8a6142",
          "eventType": "CONTENT_VIEW",
          "metadata": {
            "contentId": "d4f0df4a-86f7-4de1-aec5-6d8cbc731001"
          }
        }
        ```

---

## 5. Frontend Routing & Page Guide

The Next.js frontend has client-side route handlers built with React/TypeScript under `safecircle_frontend/app`:

| Route Path | Active Component / File | Purpose & Interface |
| :--- | :--- | :--- |
| `/` | `app/page.tsx` | **Welcome Screen:** Language selector (EN/RW), value proposition list, trust banners, get started button. |
| `/explore` | `app/(main)/explore/page.tsx` | **Discovery Feed:** Category filtering, bookmarks shortcut, quick access buttons. |
| `/learn` | `app/(main)/learn/page.tsx` | **Learn Hub:** Full educational content rendering, text-to-speech audio control, and plain-text low-data delivery. |
| `/chat` | `app/(main)/chat/page.tsx` | **Anonymous Chat:** Instant connection to Sira AI companion, streaming bubbles, auto-destruction warning, hotkeys. |
| `/risk` | `app/(main)/risk/page.tsx` | **“I Messed Up” flow:** Dynamic question cards, slide animation transits, diagnostic output and urgent clinic lookup trigger. |
| `/clinics` | `app/(main)/clinics/page.tsx` | **Clinic Locator:** Map navigation, district filtering, quick call dialers, Google Maps navigation route generator. |
| `/dashboard` | `app/(main)/dashboard/page.tsx` | **Interactive Dashboard:** Tracks bookmark counts, test scheduling logs, risk stats. |
| `/profile` | `app/(main)/profile/page.tsx` | **Private Profile:** Anonymously saved clinics, bookmarked reading lists, clear browser storage options. |
| `/privacy` | `app/(main)/privacy/page.tsx` | **Privacy Center:** Detailed policies on zero tracker footprint and data caching. |
| `/peer-support` | `app/(main)/peer-support/page.tsx` | **Peer Support Chat:** Anonymously connect with simulated counselor to share feelings and receive non-judgmental guidance. |

---

## 6. Production Deployment Guide

### Backend Deployment

The backend Spring Boot app compiles to an executable JAR and is intended to run alongside a PostgreSQL instance.

#### **1. Packaging:**
Run from the `SafeCircle_backend/` folder:
```bash
mvn clean package -DskipTests
```
This produces `target/SafeCircle_backend-0.0.1-SNAPSHOT.jar`.

#### **2. Environment Variables Required:**
When deploying to Render, Railway, AWS ECS, or Heroku, configure the following environment properties:
*   `SPRING_PROFILES_ACTIVE`: `prod`
*   `DB_URL`: JDBC PostgreSQL connection string (e.g. `jdbc:postgresql://<host>:<port>/<dbname>`)
*   `DB_USERNAME`: Database login user
*   `DB_PASSWORD`: Database login password
*   `CORS_ALLOWED_ORIGINS`: Production frontend domain (e.g., `https://safecircle.org`)

#### **3. Production Dockerfile Example:**
Place a `Dockerfile` at the root of `SafeCircle_backend/`:
```dockerfile
FROM eclipse-temurin:26-jre-alpine
WORKDIR /app
COPY target/SafeCircle_backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8089
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
```

---

### Frontend Deployment

The Next.js frontend is fully optimized for edge delivery platforms (like Vercel, Netlify, or AWS Amplify).

#### **1. Build Command:**
Run from the `safecircle_frontend/` folder:
```bash
npm run build
```

#### **2. Environment Variables Required:**
Configure these variables in your deployment dashboard:
*   `NEXT_PUBLIC_BACKEND_URL`: URL of the deployed production API backend (e.g., `https://api.safecircle.org/api/v1`)
*   `ANTHROPIC_API_KEY`: API key for the Anthropic Claude integration (used by the chat route handler)

#### **3. Vercel Quick Deploy:**
1. Connect your GitHub repository to **Vercel**.
2. Set the root directory of the project to `safecircle_frontend`.
3. Add the Environment Variables (`NEXT_PUBLIC_BACKEND_URL`, `ANTHROPIC_API_KEY`).
4. Click **Deploy**. Vercel will handle caching, routing, edge optimization, and SSL generation automatically.

---

## 7. Git & GitHub Commands

Whenever you add new endpoints or make changes to components, document them and push the changes to GitHub using the following pipeline:

1. **Stage changes:**
   ```bash
   git add -A
   ```
2. **Commit with a descriptive message:**
   ```bash
   git commit -m "docs: create comprehensive test and deployment guide"
   ```
3. **Push to the master branch:**
   ```bash
   git push origin master
   ```
