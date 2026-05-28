-- Flyway migration V4: Add audio_url support for text-to-speech accessibility
ALTER TABLE content_item ADD COLUMN IF NOT EXISTS audio_url VARCHAR(255);
