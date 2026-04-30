# SafeCircle

SafeCircle is an anonymous and inclusive digital platform designed to help young people in Rwanda safely access sexual and reproductive health (SRH) information, ask sensitive questions, and take real-world health action such as visiting youth-friendly clinics.

This repository is currently organized for backend-first development using Spring Boot, while frontend implementation is handled in parallel by a separate teammate.

## Project Vision

Despite increased awareness of SRH topics, many young people still avoid services due to fear, stigma, misinformation, and lack of trust. SafeCircle bridges this gap by turning knowledge into action through private, judgment-free digital support.

## Problem Statement

Young people in urban Rwanda (especially Kigali) can often find SRH information, but usage of SRH services remains low. This disconnect contributes to rising STI trends even where HIV prevalence is declining.

SafeCircle addresses this by providing:
- privacy-first interaction,
- trustworthy guidance,
- and clear pathways to care.

## Core Solution

SafeCircle provides a safe digital journey where youth can:
- consume accurate SRH content in short, accessible formats,
- ask anonymous questions without revealing identity,
- get guided support through sensitive scenarios,
- and discover nearby youth-friendly clinics with clear expectations.

## User Requirements

### Core Users (Youth)

Users must be able to:
- access the platform anonymously,
- browse short educational content,
- ask sensitive questions,
- receive AI or expert-driven support,
- use an emergency-style "I messed up" guidance flow,
- get clinic recommendations,
- understand what to expect at clinics,
- use content via text and audio.

### Inclusion Requirements

The experience should also support:
- voice-based interaction where possible,
- simplified UI patterns,
- low-data usage modes for constrained connectivity.

## System Requirements

### Functional Requirements

1. **Anonymous identity**
   - no mandatory signup,
   - optional nickname,
   - generated anonymous user/session ID.

2. **Content feed**
   - social-style content listing (video/text),
   - content categories (HIV, STI, myths, prevention),
   - optional engagement signals (views, likes).

3. **Anonymous chat**
   - private chat interface,
   - AI and/or rule-based responses,
   - optional anonymous chat history storage.

4. **"I Messed Up" flow**
   - structured Q&A intake (what happened, when, context),
   - decision-tree/risk logic,
   - output with risk level and recommended action.

5. **Clinic locator**
   - list nearby clinics,
   - filters (location, youth-friendly services),
   - clinic details (contact, expectations, service notes).

6. **Anonymous analytics**
   - track viewed content and actions taken,
   - no personally identifying data collection.

7. **Accessibility support**
   - text-to-speech integration,
   - optional voice input support.

### Non-Functional Requirements

- **Security:** HTTPS-only transport, minimal data storage, no required personal identifiers.
- **Privacy:** anonymous IDs/sessions only; no real-name/email dependency.
- **Performance:** lightweight endpoints with target responses under ~500ms for common API calls.
- **Scalability:** modular service boundaries; start as monolith and evolve to microservices.
- **Reliability:** stable behavior under expected load and clear error handling.
- **Accessibility:** mobile-first and low-bandwidth-friendly UX.

## Tech Stack (Current Backend Plan)

- **Backend:** Spring Boot (Java)
- **Build Tool:** Maven
- **API style:** RESTful services
- **Current app port:** `8089` (from `application.yaml`)

> Note: The current scaffold in the existing backend starter project uses Spring Boot `4.0.6` and Java `26`.

## Initial Repository Structure

Top-level structure intended for this stage:

```text
safecircle/
├── safecircle_backend/
├── README.md
└── .gitignore
```

## Team Collaboration

- **Backend owner (you):** APIs, business logic, data model, security/privacy controls, integration points.
- **Frontend owner (friend):** UI/UX, content feed interface, chat screens, guided flows, and client-side accessibility.

## Suggested Backend Milestones

1. Bootstrap core Spring Boot modules (`auth`, `content`, `chat`, `risk-flow`, `clinics`, `analytics`).
2. Implement anonymous session mechanism and privacy-safe request tracking.
3. Expose first REST endpoints for content feed and anonymous Q&A.
4. Build "I messed up" decision flow service.
5. Add clinic locator API and filtering.
6. Add telemetry for anonymous behavior analytics.

## Getting Started (Backend)

When your backend code is inside `safecircle_backend`:

1. Ensure Java and Maven are installed.
2. From the backend folder, run:
   - `mvn clean install`
   - `mvn spring-boot:run`
3. Verify API on configured port (default: `8089`).

## Next Improvements for README (Later)

- Add exact API endpoint documentation.
- Add data model and ERD.
- Add environment setup (`.env` or config profiles).
- Add deployment notes (staging/production).
- Add testing strategy and CI badges.

## License

To be decided by the team.
