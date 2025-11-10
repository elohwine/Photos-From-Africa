-- Sample data for Photos From Africa
-- This file will be executed automatically by Spring Boot on startup

-- Ensure unique email constraint exists to support ON CONFLICT (email)
CREATE UNIQUE INDEX IF NOT EXISTS users_email_ux ON users (email);

-- Insert sample users idempotently (skip if email exists)
INSERT INTO users (first_name, last_name, email, phone_no, password, address, role)
SELECT 'Admin', 'User', 'admin@photosafrica.com', '+254700000001', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Nairobi, Kenya', 'admin'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@photosafrica.com');

INSERT INTO users (first_name, last_name, email, phone_no, password, address, role)
SELECT 'John', 'Kamau', 'artist@photosafrica.com', '+254700000002', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Mombasa, Kenya', 'artist'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'artist@photosafrica.com');

INSERT INTO users (first_name, last_name, email, phone_no, password, address, role)
SELECT 'Jane', 'Wanjiku', 'buyer@photosafrica.com', '+254700000003', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Kisumu, Kenya', 'user'
WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'buyer@photosafrica.com');

-- Insert sample photos owned by the artist user; idempotent inserts using NOT EXISTS
INSERT INTO photo (title, description, category, label, price, likes, img_url, owner_id)
SELECT 'African Sunset Over Savanna', 'A breathtaking view of the African sunset casting golden hues over the endless savanna plains.', 'oilPainting', 'Unsold', 15000, 24, 'https://placehold.co/800x600/jpg?text=African+Sunset', u.id
FROM users u
WHERE u.email = 'artist@photosafrica.com'
AND NOT EXISTS (
	SELECT 1 FROM photo p WHERE p.title = 'African Sunset Over Savanna' AND p.owner_id = u.id
);

INSERT INTO photo (title, description, category, label, price, likes, img_url, owner_id)
SELECT 'Wildlife Portrait - Majestic Lion', 'A powerful portrait capturing the essence of the king of the jungle in his natural habitat.', 'watercolorPainting', 'Unsold', 22000, 18, 'https://placehold.co/800x600/jpg?text=Majestic+Lion', u.id
FROM users u
WHERE u.email = 'artist@photosafrica.com'
AND NOT EXISTS (
	SELECT 1 FROM photo p WHERE p.title = 'Wildlife Portrait - Majestic Lion' AND p.owner_id = u.id
);

INSERT INTO photo (title, description, category, label, price, likes, img_url, owner_id)
SELECT 'Traditional Tribal Art', 'Contemporary interpretation of traditional African tribal patterns and symbolism.', 'hatching', 'Unsold', 12000, 31, 'https://placehold.co/800x600/jpg?text=Tribal+Art', u.id
FROM users u
WHERE u.email = 'artist@photosafrica.com'
AND NOT EXISTS (
	SELECT 1 FROM photo p WHERE p.title = 'Traditional Tribal Art' AND p.owner_id = u.id
);

INSERT INTO photo (title, description, category, label, price, likes, img_url, owner_id)
SELECT 'Serengeti Landscape', 'Vast expanse of the Serengeti captured during the great migration season.', 'oilPainting', 'Unsold', 28000, 15, 'https://placehold.co/800x600/jpg?text=Serengeti+Landscape', u.id
FROM users u
WHERE u.email = 'artist@photosafrica.com'
AND NOT EXISTS (
	SELECT 1 FROM photo p WHERE p.title = 'Serengeti Landscape' AND p.owner_id = u.id
);

INSERT INTO photo (title, description, category, label, price, likes, img_url, owner_id)
SELECT 'Abstract African Heritage', 'Modern abstract piece celebrating African heritage and culture through bold colors.', 'watercolor', 'Unsold', 18000, 27, 'https://placehold.co/800x600/jpg?text=Abstract+Africa', u.id
FROM users u
WHERE u.email = 'artist@photosafrica.com'
AND NOT EXISTS (
	SELECT 1 FROM photo p WHERE p.title = 'Abstract African Heritage' AND p.owner_id = u.id
);

-- Reset sequences to continue from where we left off
-- No sequence reset needed; IDs auto-increment correctly

-- Update existing rows (if previously inserted) to use external placeholder images
-- Align categories with template filters (oilPainting, watercolorPainting, hatching)
UPDATE photo SET category = 'oilPainting'
WHERE title IN ('African Sunset Over Savanna','Serengeti Landscape');

UPDATE photo SET category = 'watercolorPainting'
WHERE title IN ('Wildlife Portrait - Majestic Lion','Abstract African Heritage');

UPDATE photo SET img_url = 'https://placehold.co/800x600/jpg?text=African+Sunset'
WHERE title = 'African Sunset Over Savanna' AND owner_id = (SELECT id FROM users WHERE email = 'artist@photosafrica.com');

UPDATE photo SET img_url = 'https://placehold.co/800x600/jpg?text=Majestic+Lion'
WHERE title = 'Wildlife Portrait - Majestic Lion' AND owner_id = (SELECT id FROM users WHERE email = 'artist@photosafrica.com');

UPDATE photo SET img_url = 'https://placehold.co/800x600/jpg?text=Tribal+Art'
WHERE title = 'Traditional Tribal Art' AND owner_id = (SELECT id FROM users WHERE email = 'artist@photosafrica.com');

UPDATE photo SET img_url = 'https://placehold.co/800x600/jpg?text=Serengeti+Landscape'
WHERE title = 'Serengeti Landscape' AND owner_id = (SELECT id FROM users WHERE email = 'artist@photosafrica.com');

UPDATE photo SET img_url = 'https://placehold.co/800x600/jpg?text=Abstract+Africa'
WHERE title = 'Abstract African Heritage' AND owner_id = (SELECT id FROM users WHERE email = 'artist@photosafrica.com');

-- Normalize any legacy rows that still reference local static image paths
UPDATE photo
SET img_url = 'https://placehold.co/800x600/jpg?text=Photo'
WHERE img_url LIKE '/img/%';
