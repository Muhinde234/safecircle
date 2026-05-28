-- Flyway migration V5: Add moderation columns to chat_message table
ALTER TABLE chat_message ADD COLUMN IF NOT EXISTS is_flagged BOOLEAN DEFAULT FALSE;
ALTER TABLE chat_message ADD COLUMN IF NOT EXISTS moderation_notes TEXT;
