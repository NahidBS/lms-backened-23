-- Insert sample categories
INSERT INTO categories (name, description, created_at, updated_at) VALUES
('Fiction', 'Fictional literature including novels and short stories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Science', 'Scientific books and research publications', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Technology', 'Technology, programming, and computer science books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('History', 'Historical books and biographies', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Philosophy', 'Philosophy and ethics books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Business', 'Business, economics, and management books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Featured', 'Featured books', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample users
INSERT INTO users (username, name, email, password_hash, role, date_of_birth, address, is_active, created_at, updated_at) VALUES
('john_doe', 'John Doe', 'john.doe@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', '1990-05-15', '123 Main St, City, State', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('jane_smith', 'Jane Smith', 'jane.smith@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', '1985-08-22', '456 Oak Ave, City, State', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('bob_johnson', 'Bob Johnson', 'bob.johnson@email.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', 'USER', '1992-03-10', '789 Pine Rd, City, State', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('admin', 'Charlie Wilson', 'charlie.wilson@email.com', '$2b$12$xVgqwB0C3bFkJIaoYpaQWOO8A3tmHSx8U5OIRcJFEx8JHqAiLH3C.', 'ADMIN', '1980-11-15', '654 Maple Dr, City, State', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample books
INSERT INTO books (name, author, isbn, publication_year, total_copies, available_copies, category_id, short_details, about, format, created_at, updated_at, book_cover_url, pdf_file_url, audio_file_url) VALUES
('The Great Gatsby', 'F. Scott Fitzgerald', '978-0-7432-7356-5', 1925, 55, 33, 1, 'A classic American novel', 'A classic American novel set in the Jazz Age', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/commons/7/7a/The_Great_Gatsby_Cover_1925_Retouched.jpg', null, null),
('To Kill a Mockingbird', 'Harper Lee', '978-0-06-112008-4', 1960, 44, 22, 1, 'A novel about racial injustice', 'A novel about racial injustice in the American South', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/commons/4/4f/To_Kill_a_Mockingbird_%28first_edition_cover%29.jpg', null, null),
('A Brief History of Time', 'Stephen Hawking', '978-0-553-38016-3', 1988, 33, 33, 2, 'A popular science book', 'A popular science book about cosmology', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/en/a/a3/BriefHistoryTime.jpg', null, null),
('Clean Code', 'Robert C. Martin', '978-0-13-235088-4', 2008, 66, 44, 3, 'A handbook of agile software', 'A handbook of agile software craftsmanship', 'E_BOOK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://www.oreilly.com/covers/urn:orm:book:9780136083238/400w/', null, null),
('The Art of War', 'Sun Tzu', '978-0-14-044919-4', 500, 22, 12, 4, 'Ancient Chinese military treatise', 'Ancient Chinese military treatise', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/commons/2/2a/Bamboo_book_-_closed_-_UCR.jpg', null, null),
('Thinking, Fast and Slow', 'Daniel Kahneman', '978-0-374-53355-7', 2011, 44, 44, 5, 'A book about behavioral psychology', 'A book about behavioral psychology and decision-making', 'E_BOOK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/en/c/c1/Thinking%2C_Fast_and_Slow.jpg', null, null),
('Good to Great', 'Jim Collins', '978-0-06-662099-2', 2001, 33, 22, 6, 'A management book', 'A management book about company transformation', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/en/0/03/Cover_Good_2_Gr8.jpg', null, null),
('1984', 'George Orwell', '978-0-452-28423-4', 1949, 55, 33, 1, 'A dystopian social science fiction', 'A dystopian social science fiction novel', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/commons/5/51/1984_first_edition_cover.jpg', null, null),
('The Origin of Species', 'Charles Darwin', '978-0-14-043205-9', 1859, 22, 22, 2, 'A work of scientific literature', 'A work of scientific literature on evolution', 'HARD_COPY', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/commons/c/cd/Origin_of_Species_title_page.jpg', null, null),
('Design Patterns', 'Gang of Four', '978-0-201-63361-0', 1994, 44, 33, 3, 'Elements of reusable software', 'Elements of reusable object-oriented software', 'E_BOOK', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://upload.wikimedia.org/wikipedia/en/7/78/Design_Patterns_cover.jpg', null, null),
('Steal Like an Artist', 'Austin Kleon', '978-8-858-122259-2', 2012, 44, 33, 7, 'Steal Like an Artist: 10 Things Nobody Told You About Being Creative is a book on coming up with creative ideas written by Austin Kleon and published in 2012 from Workman Publishing. The book has since then become a New York Times Bestseller.', 'Elements of reusable object-oriented software', 'E_BOOK',
 CURRENT_TIMESTAMP, CURRENT_TIMESTAMP,'https://images-na.ssl-images-amazon.com/images/S/compressed.photo.goodreads.com/books/1430943636i/24693052.jpg', 'https://pdfdrive.com.co/wp-content/pdfh/Steal-LikeanArtist-PDFdrive.pdf', 'https://cdn.pixabay.com/download/audio/2021/11/02/audio_7801c25e41.mp3?filename=a-bird-10226.mp3');


-- Insert sample borrows (some active, some returned)
INSERT INTO borrows (user_id, book_id, borrow_date, due_date, return_date, status, extension_count, created_at, updated_at) VALUES
(1, 1, '2025-08-10', '2025-06-14', '2025-06-24', 'RETURNED', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, '2025-08-15', '2025-08-29', NULL, 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 4, '2025-08-20', '2025-08-03', NULL, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(1, 5, '2025-08-25', '2025-08-08', NULL, 'ACTIVE', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 7, '2025-08-12', '2025-08-26', '2025-08-25', 'RETURNED', 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 1, '2025-08-30', '2025-08-13', NULL, 'ACTIVE', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert admin settings with new structure
INSERT INTO admin_settings (borrow_day_limit, borrow_extend_limit, borrow_book_limit, booking_days_limit, created_at, updated_at) VALUES
(14, 2, 5, 7, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

