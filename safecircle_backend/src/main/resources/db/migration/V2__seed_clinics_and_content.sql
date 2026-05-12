-- Optional starter seed data for local/dev MVP.
-- Safe to keep minimal and editable by your team.

-- ===== Seed clinics =====
INSERT INTO clinic (id, name, district, address, contact_info, youth_friendly, no_judgment, anonymous_visits, what_to_expect)
VALUES
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730001', 'Kigali Youth Center Clinic', 'Gasabo', 'KG 345 ST 6', '+250780000000', TRUE, TRUE, TRUE, 'Private counseling and STI testing in a non-judgmental space.'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730002', 'Nyamirambo Wellness Center', 'Nyarugenge', 'KN 345 ST 6', '+250780000001', TRUE, TRUE, TRUE, 'Confidential consultation and family planning support.'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730003', 'Kicukiro Health Facility', 'Kicukiro', 'KK 345 ST 6', '+250780000002', FALSE, TRUE, FALSE, 'General SRH information and STI screening.')
ON CONFLICT (id) DO NOTHING;

INSERT INTO clinic_service (clinic_id, service_name)
VALUES
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730001', 'Internal Medicine'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730001', 'Family Planning'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730002', 'Dentistry'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730002', 'Family Planning'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730003', 'Ophthalmology'),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc730003', 'Family Planning')
ON CONFLICT (clinic_id, service_name) DO NOTHING;

-- ===== Seed content =====
INSERT INTO content_item (id, title, summary, body, content_type, category, language, published)
VALUES
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc731001', 'What is PrEP?', 'Daily HIV prevention option.', 'PrEP can reduce HIV risk significantly when taken consistently.', 'TEXT', 'HIV', 'en', TRUE),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc731002', 'Common STI myths', 'Myths and facts.', 'Many STIs have no symptoms. Regular testing is important.', 'TEXT', 'STI', 'en', TRUE),
    ('d4f0df4a-86f7-4de1-aec5-6d8cbc731003', 'Emergency next steps', 'If something happened recently.', 'Act quickly and seek care within 24-72 hours when needed.', 'TEXT', 'PREVENTION', 'en', TRUE)
ON CONFLICT (id) DO NOTHING;
