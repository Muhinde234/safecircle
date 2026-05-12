-- SafeCircle MVP core schema
-- PostgreSQL + Flyway
-- Creates normalized tables for sessions, content, clinics, chat, risk assessments, and events.

-- UUID generation helper.
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- ===== Enum types =====
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'chat_role') THEN
        CREATE TYPE chat_role AS ENUM ('USER', 'ASSISTANT');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'chat_source') THEN
        CREATE TYPE chat_source AS ENUM ('RULE_BASED', 'AI', 'EXPERT');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'risk_level') THEN
        CREATE TYPE risk_level AS ENUM ('LOW', 'MEDIUM', 'HIGH');
    END IF;
END
$$;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'event_type') THEN
        CREATE TYPE event_type AS ENUM (
            'CONTENT_VIEW',
            'CONTENT_OPENED',
            'RISK_ASSESSMENT_DONE',
            'CLINIC_OPENED',
            'CHAT_SENT',
            'SESSION_CREATED'
        );
    END IF;
END
$$;

-- ===== Core identity/session =====
CREATE TABLE IF NOT EXISTS anonymous_session (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nickname            VARCHAR(64) NOT NULL,
    language            VARCHAR(8) NOT NULL DEFAULT 'en',
    is_private_session  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_anonymous_session_created_at
    ON anonymous_session (created_at DESC);

-- ===== Content =====
CREATE TABLE IF NOT EXISTS content_item (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title               VARCHAR(255) NOT NULL,
    summary             TEXT,
    body                TEXT,
    content_type        VARCHAR(32) NOT NULL DEFAULT 'TEXT',
    category            VARCHAR(64) NOT NULL,
    language            VARCHAR(8) NOT NULL DEFAULT 'en',
    published           BOOLEAN NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_content_type
        CHECK (content_type IN ('TEXT', 'AUDIO', 'VIDEO'))
);

CREATE INDEX IF NOT EXISTS idx_content_item_category
    ON content_item (category);

CREATE INDEX IF NOT EXISTS idx_content_item_published_created
    ON content_item (published, created_at DESC);

-- ===== Clinics =====
CREATE TABLE IF NOT EXISTS clinic (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(255) NOT NULL,
    district            VARCHAR(120) NOT NULL,
    address             VARCHAR(255) NOT NULL,
    contact_info        VARCHAR(120) NOT NULL,
    youth_friendly      BOOLEAN NOT NULL DEFAULT FALSE,
    no_judgment         BOOLEAN NOT NULL DEFAULT FALSE,
    anonymous_visits    BOOLEAN NOT NULL DEFAULT FALSE,
    what_to_expect      TEXT NOT NULL,
    latitude            DOUBLE PRECISION,
    longitude           DOUBLE PRECISION,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_clinic_district
    ON clinic (district);

CREATE INDEX IF NOT EXISTS idx_clinic_youth_friendly
    ON clinic (youth_friendly);

CREATE TABLE IF NOT EXISTS clinic_service (
    clinic_id            UUID NOT NULL,
    service_name         VARCHAR(120) NOT NULL,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (clinic_id, service_name),
    CONSTRAINT fk_clinic_service_clinic
        FOREIGN KEY (clinic_id)
        REFERENCES clinic(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_clinic_service_name
    ON clinic_service (service_name);

-- ===== Chat =====
CREATE TABLE IF NOT EXISTS chat_message (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id          UUID NOT NULL,
    role                chat_role NOT NULL,
    source              chat_source,
    message_text        TEXT NOT NULL,
    language            VARCHAR(8) NOT NULL DEFAULT 'en',
    metadata            JSONB,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_chat_message_session
        FOREIGN KEY (session_id)
        REFERENCES anonymous_session(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_chat_message_session_created
    ON chat_message (session_id, created_at DESC);

-- ===== Risk assessments =====
CREATE TABLE IF NOT EXISTS risk_assessment (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id          UUID NOT NULL,
    event_type_label    VARCHAR(120) NOT NULL,
    hours_since_event   INTEGER NOT NULL CHECK (hours_since_event >= 0),
    symptoms_present    BOOLEAN NOT NULL DEFAULT FALSE,
    risk_level          risk_level NOT NULL,
    recommended_action  TEXT NOT NULL,
    urgency_window      VARCHAR(120) NOT NULL,
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_risk_assessment_session
        FOREIGN KEY (session_id)
        REFERENCES anonymous_session(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_risk_assessment_session_created
    ON risk_assessment (session_id, created_at DESC);

CREATE INDEX IF NOT EXISTS idx_risk_assessment_level
    ON risk_assessment (risk_level);

-- ===== Event tracking =====
CREATE TABLE IF NOT EXISTS event_log (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id          UUID NOT NULL,
    event_type          event_type NOT NULL,
    status              VARCHAR(32) NOT NULL DEFAULT 'RECORDED',
    metadata            JSONB,
    recorded_at         TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_event_log_session
        FOREIGN KEY (session_id)
        REFERENCES anonymous_session(id)
        ON DELETE CASCADE,
    CONSTRAINT chk_event_status
        CHECK (status IN ('RECORDED', 'REJECTED'))
);

CREATE INDEX IF NOT EXISTS idx_event_log_recorded_at
    ON event_log (recorded_at DESC);

CREATE INDEX IF NOT EXISTS idx_event_log_session
    ON event_log (session_id);

CREATE INDEX IF NOT EXISTS idx_event_log_event_type
    ON event_log (event_type);
