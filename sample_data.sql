-- Sample Data for Stationery Management System
-- This file contains test data compatible with your schema

-- First, let's add some users
-- Note: Passwords are BCrypt encoded. The plaintext passwords are shown in comments
-- Use BCrypt online generator or Spring to generate these hashes

-- Password: "password123" -> $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy

-- MANAGERS
INSERT INTO users (email, full_name, max_purchase_limit, password, role) VALUES
('alice.manager@college.edu', 'Alice Johnson', 5000.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER'),
('bob.manager@college.edu', 'Bob Williams', 5000.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'MANAGER');

-- EMPLOYEES
INSERT INTO users (email, full_name, max_purchase_limit, password, role) VALUES
('charlie.emp@college.edu', 'Charlie Brown', 1000.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYEE'),
('diana.emp@college.edu', 'Diana Prince', 750.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYEE'),
('emma.emp@college.edu', 'Emma Watson', 1200.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYEE'),
('frank.emp@college.edu', 'Frank Miller', 900.00, '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'EMPLOYEE');

-- ITEMS (Stationery supplies)
INSERT INTO items (item_name, stock_quantity, unit_price) VALUES
-- Paper Products
('A4 Printer Paper (500 sheets)', 250, 5.50),
('A3 Printer Paper (500 sheets)', 100, 8.00),
('Sticky Notes 3x3 (100 pack)', 500, 1.10),
('Index Cards (100 pack)', 200, 2.50),
('Legal Pads (Yellow)', 150, 3.25),

-- Writing Instruments
('Ballpoint Pen Black (Box of 12)', 400, 4.50),
('Ballpoint Pen Blue (Box of 12)', 400, 4.50),
('Gel Pen Black (Single)', 1000, 1.25),
('Gel Pen Blue (Single)', 800, 1.25),
('Permanent Marker Black', 300, 1.80),
('Highlighter Yellow', 250, 0.85),
('Highlighter Green', 200, 0.85),
('Mechanical Pencil 0.7mm', 350, 1.95),

-- Office Supplies
('Heavy Duty Stapler', 45, 18.00),
('Staples (5000 count)', 300, 3.50),
('Paper Clips (500 count)', 400, 1.20),
('Binder Clips Assorted (40 pack)', 180, 4.75),
('Rubber Bands (1lb bag)', 120, 2.90),
('Correction Tape', 220, 2.10),
('Glue Stick (40g)', 280, 1.75),

-- Filing & Organization
('File Folders Letter Size (25 pack)', 160, 6.50),
('Binders 1.5 inch (Pack of 4)', 90, 12.00),
('Desk Organizer', 35, 15.50),
('Magazine File Box', 65, 8.75),
('Label Maker Tape', 140, 5.25),

-- Technology
('USB Flash Drive 32GB', 80, 12.00),
('USB Flash Drive 64GB', 50, 18.00),
('Wireless Mouse', 40, 22.50),
('Keyboard Wired', 25, 28.00),
('Laptop Stand', 20, 35.00),

-- Whiteboard Supplies
('Whiteboard Eraser', 100, 2.50),
('Dry Erase Marker Black', 180, 1.40),
('Dry Erase Marker Blue', 150, 1.40),
('Dry Erase Marker Red', 140, 1.40);

-- SAMPLE REQUESTS
-- Note: These use assumed user IDs. Adjust based on your actual user IDs after INSERT

-- Request 1: PENDING (Charlie requesting basic supplies)
INSERT INTO requests (status, superior_email, total_cost, requester_id, created_date) VALUES
('PENDING', 'alice.manager@college.edu', 23.50, 3, CURRENT_TIMESTAMP);

INSERT INTO request_details (quantity, item_id, request_id) VALUES
(2, 1, 1),  -- 2x A4 Paper
(10, 8, 1), -- 10x Gel Pen Black
(1, 14, 1); -- 1x Stapler

-- Request 2: APPROVED (Diana's approved request)
INSERT INTO requests (status, superior_email, total_cost, requester_id, created_date) VALUES
('APPROVED', 'bob.manager@college.edu', 45.75, 4, CURRENT_TIMESTAMP - INTERVAL '2 days');

INSERT INTO request_details (quantity, item_id, request_id) VALUES
(3, 3, 2),  -- 3x Sticky Notes
(5, 11, 2), -- 5x Highlighter Yellow
(2, 22, 2); -- 2x Binders

-- Request 3: REJECTED (Emma's rejected request - over budget)
INSERT INTO requests (status, superior_email, total_cost, requester_id, created_date) VALUES
('REJECTED', 'alice.manager@college.edu', 156.00, 5, CURRENT_TIMESTAMP - INTERVAL '1 day');

INSERT INTO request_details (quantity, item_id, request_id) VALUES
(2, 27, 3), -- 2x Wireless Mouse
(3, 29, 3); -- 3x Laptop Stand

-- Request 4: CANCEL_REQUESTED (Frank wants to cancel approved request)
INSERT INTO requests (status, superior_email, total_cost, requester_id, created_date) VALUES
('CANCEL_REQUESTED', 'bob.manager@college.edu', 62.50, 6, CURRENT_TIMESTAMP - INTERVAL '3 days');

INSERT INTO request_details (quantity, item_id, request_id) VALUES
(5, 1, 4),  -- 5x A4 Paper
(10, 13, 4), -- 10x Mechanical Pencil
(1, 23, 4);  -- 1x Desk Organizer

-- Request 5: PENDING (Emma requesting office supplies)
INSERT INTO requests (status, superior_email, total_cost, requester_id, created_date) VALUES
('PENDING', 'alice.manager@college.edu', 34.25, 5, CURRENT_TIMESTAMP);

INSERT INTO request_details (quantity, item_id, request_id) VALUES
(3, 5, 5),  -- 3x Legal Pads
(5, 10, 5), -- 5x Permanent Marker
(2, 19, 5); -- 2x Correction Tape

-- Request 6: APPROVED (Charlie's second approved request)
INSERT INTO requests (status, superior_email, total_cost, requester_id, created_date) VALUES
('APPROVED', 'alice.manager@college.edu', 28.00, 3, CURRENT_TIMESTAMP - INTERVAL '5 days');

INSERT INTO request_details (quantity, item_id, request_id) VALUES
(1, 25, 6), -- 1x USB 32GB
(4, 30, 6); -- 4x Whiteboard Eraser

-- VERIFICATION QUERIES
-- Use these to check your data

-- Check all users with their roles
-- SELECT id, email, full_name, role, max_purchase_limit FROM users ORDER BY role, email;

-- Check all items with stock
-- SELECT id, item_name, stock_quantity, unit_price FROM items ORDER BY item_name;

-- Check all requests with requester and status
-- SELECT r.id, u.full_name as requester, r.status, r.total_cost, r.superior_email, r.created_date
-- FROM requests r
-- JOIN users u ON r.requester_id = u.id
-- ORDER BY r.created_date DESC;

-- Check request details
-- SELECT rd.id, r.id as request_id, i.item_name, rd.quantity, (rd.quantity * i.unit_price) as line_total
-- FROM request_details rd
-- JOIN requests r ON rd.request_id = r.id
-- JOIN items i ON rd.item_id = i.id
-- ORDER BY r.id, rd.id;

-- LOGIN CREDENTIALS FOR TESTING
-- All users have password: password123
--
-- Managers:
--   alice.manager@college.edu / password123
--   bob.manager@college.edu / password123
--
-- Employees:
--   charlie.emp@college.edu / password123
--   diana.emp@college.edu / password123
--   emma.emp@college.edu / password123
--   frank.emp@college.edu / password123

