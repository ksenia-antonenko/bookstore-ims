-- AUTHORS
INSERT INTO author (id, first_name, last_name, created_at, updated_at, created_by, updated_by)
VALUES
    (10,'Terry','Pratchett', now(), now(), 'seed','seed'),
    (11,'Neil','Gaiman',     now(), now(), 'seed','seed'),
    (12,'Joe','Abercrombie', now(), now(), 'seed','seed'),
    (13,'Joshua','Bloch',    now(), now(), 'seed','seed'),
    (14,'Martin','Fowler',   now(), now(), 'seed','seed')
ON CONFLICT DO NOTHING;

-- GENRES
INSERT INTO genre (id, name, created_at, updated_at, created_by, updated_by)
VALUES
    (20,'Fantasy',     now(), now(), 'seed','seed'),
    (21,'Sci-Fi',      now(), now(), 'seed','seed'),
    (22,'Programming', now(), now(), 'seed','seed')
ON CONFLICT DO NOTHING;

-- BOOKS
INSERT INTO book (id, title, price, quantity, rating, created_at, updated_at, created_by, updated_by)
VALUES
    (100,'Good Omens',          12.50, 5, 4.8, now(), now(), 'seed','seed'),
    (101,'Guards! Guards!',     10.99, 7, 4.6, now(), now(), 'seed','seed'),
    (102,'The Blade Itself',    15.99, 4, 4.5, now(), now(), 'seed','seed'),
    (103,'Effective Java',      39.99, 3, 4.9, now(), now(), 'seed','seed'),
    (104,'Refactoring',         44.00, 2, 4.8, now(), now(), 'seed','seed'),
    (105,'American Gods',       11.99, 6, 4.4, now(), now(), 'seed','seed'),
    (106,'Neverwhere',           9.49, 8, 4.3, now(), now(), 'seed','seed')
ON CONFLICT DO NOTHING;

-- BOOK ↔ AUTHOR (many-to-many)
INSERT INTO book_author (book_id, author_id) VALUES
                                                 (100,10),(100,11),           -- Good Omens: Pratchett + Gaiman
                                                 (101,10),                    -- Guards! Guards!: Pratchett
                                                 (102,12),                    -- The Blade Itself: Abercrombie
                                                 (103,13),                    -- Effective Java: Bloch
                                                 (104,14),                    -- Refactoring: Fowler
                                                 (105,11), (106,11)           -- American Gods, Neverwhere: Gaiman
ON CONFLICT DO NOTHING;

-- BOOK ↔ GENRE (many-to-many)
INSERT INTO book_genre (book_id, genre_id) VALUES
                                               (100,20), (101,20), (102,20),    -- Fantasy
                                               (105,20), (106,20),              -- Fantasy
                                               (103,22), (104,22)               -- Programming
ON CONFLICT DO NOTHING;

-- Keep sequences in sync if using serial/identity
SELECT setval(pg_get_serial_sequence('author','id'), COALESCE((SELECT MAX(id) FROM author),0));
SELECT setval(pg_get_serial_sequence('genre','id'),  COALESCE((SELECT MAX(id) FROM genre),0));
SELECT setval(pg_get_serial_sequence('book','id'),   COALESCE((SELECT MAX(id) FROM book),0));