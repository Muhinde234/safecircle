-- Flyway migration V3: Add session bookmarks and preferences
CREATE TABLE IF NOT EXISTS session_bookmark (
    session_id UUID NOT NULL,
    bookmark_type VARCHAR(32) NOT NULL, -- 'CLINIC' or 'CONTENT'
    target_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (session_id, bookmark_type, target_id),
    CONSTRAINT fk_session_bookmark_session
        FOREIGN KEY (session_id)
        REFERENCES anonymous_session(id)
        ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_session_bookmark_session
    ON session_bookmark (session_id);
