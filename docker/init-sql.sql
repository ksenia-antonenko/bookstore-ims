-- Authors
INSERT INTO author (id, first_name, last_name, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'Terry', 'Pratchett', now(), now(), 'system', 'system'),
    (2, 'Joe', 'Abercrombie', now(), now(), 'system', 'system'),
    (3, 'Neil', 'Gaiman', now(), now(), 'system', 'system'),
    (4, 'Joshua', 'Bloch', now(), now(), 'system', 'system'),
    (5, 'Martin', 'Fowler', now(), now(), 'system', 'system')
ON CONFLICT DO NOTHING;

-- Genres
INSERT INTO genre (id, name, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'Fantasy', now(), now(), 'system', 'system'),
    (2, 'Urban Fantasy', now(), now(), 'system', 'system'),
    (3, 'Satire', now(), now(), 'system', 'system'),
    (4, 'Grimdark', now(), now(), 'system', 'system'),
    (5, 'Programming', now(), now(), 'system', 'system'),
    (6, 'Software Design', now(), now(), 'system', 'system')
ON CONFLICT DO NOTHING;

-- Books
INSERT INTO book (id, title, price, quantity, rating, created_at, updated_at, created_by, updated_by)
VALUES
    (1, 'Mort', 9.99, 10, 4.8, now(), now(), 'system', 'system'),
    (2, 'Small Gods', 9.99, 7, 4.7, now(), now(), 'system', 'system'),
    (3, 'The Blade Itself', 12.50, 5, 4.5, now(), now(), 'system', 'system'),
    (4, 'American Gods', 11.95, 8, 4.6, now(), now(), 'system', 'system'),
    (5, 'Good Omens', 10.95, 6, 4.9, now(), now(), 'system', 'system'),
    (6, 'Effective Java', 39.99, 15, 4.9, now(), now(), 'system', 'system'),
    (7, 'Refactoring', 44.99, 12, 4.8, now(), now(), 'system', 'system'),
    (8, 'Patterns of Enterprise Application Architecture', 49.99, 10, 4.7, now(), now(), 'system', 'system')
ON CONFLICT DO NOTHING;

-- Book ↔ Author
INSERT INTO book_author (book_id, author_id) VALUES
                                                 (1, 1),  -- Mort → Pratchett
                                                 (2, 1),  -- Small Gods → Pratchett
                                                 (3, 2),  -- The Blade Itself → Abercrombie
                                                 (4, 3),  -- American Gods → Gaiman
                                                 (5, 1), (5, 3),  -- Good Omens → Pratchett + Gaiman
                                                 (6, 4),  -- Effective Java → Bloch
                                                 (7, 5),  -- Refactoring → Fowler
                                                 (8, 5)   -- P of EAA → Fowler
ON CONFLICT DO NOTHING;

-- Book ↔ Genre
INSERT INTO book_genre (book_id, genre_id) VALUES
                                               (1, 1), (1, 3),        -- Mort → Fantasy, Satire
                                               (2, 1), (2, 3),        -- Small Gods → Fantasy, Satire
                                               (3, 1), (3, 4),        -- The Blade Itself → Fantasy, Grimdark
                                               (4, 2),                -- American Gods → Urban Fantasy
                                               (5, 1), (5, 2), (5, 3),-- Good Omens → Fantasy, Urban Fantasy, Satire
                                               (6, 5),                -- Effective Java → Programming
                                               (7, 6),                -- Refactoring → Software Design
                                               (8, 6)                 -- P of EAA → Software Design
ON CONFLICT DO NOTHING;

SELECT setval(pg_get_serial_sequence('book','id'),
              COALESCE((SELECT MAX(id) FROM book), 0));
SELECT setval(pg_get_serial_sequence('author','id'),
              COALESCE((SELECT MAX(id) FROM author), 0));
SELECT setval(pg_get_serial_sequence('genre','id'),
              COALESCE((SELECT MAX(id) FROM genre), 0));