-- 1. USER DATA
INSERT INTO USER (user_id, email, name, role, max_purchase_limit, password_hash) VALUES
                                                                                     ('U001', 'alice.mgr@college.edu', 'Alice Johnson (Mgr)', 'MANAGER', 5000.00, '$2a$10$HASHED_PASSWORD_MGR'),
                                                                                     ('U002', 'bob.mgr@college.edu', 'Bob Williams (Mgr)', 'MANAGER', 5000.00, '$2a$10$HASHED_PASSWORD_MGR2'),
                                                                                     ('U003', 'charlie.emp@college.edu', 'Charlie Brown (Emp)', 'EMPLOYEE', 1000.00, '$2a$10$HASHED_PASSWORD_EMP'),
                                                                                     ('U004', 'diana.emp@college.edu', 'Diana Prince (Emp)', 'EMPLOYEE', 750.00, '$2a$10$HASHED_PASSWORD_EMP2');

-- 2. ITEM DATA
INSERT INTO ITEM (item_id, name, description, stock_quantity, unit_price) VALUES
                                                                              ('I101', 'A4 Printer Paper', '80gsm, 500 sheets/ream', 250, 5.50),
                                                                              ('I102', 'Gel Pen (Black)', 'Retractable 0.5mm tip', 1200, 1.25),
                                                                              ('I103', 'Heavy Duty Stapler', 'Full-strip desktop model', 15, 18.00),
                                                                              ('I104', 'Correction Tape', 'Ergonomic side-action dispenser', 5, 3.50),
                                                                              ('I105', 'Large Manila Envelopes', 'C4 size, box of 100', 0, 15.00);

-- 3. REQUEST DATA
INSERT INTO REQUEST (request_id, requester_id, superior_email, request_date, status, total_cost) VALUES
                                                                                                     ('R001', 'U003', 'alice.mgr@college.edu', '2024-10-20', 'APPROVED', 13.00),
                                                                                                     ('R002', 'U004', 'bob.mgr@college.edu', '2024-10-22', 'PENDING', 34.00),
                                                                                                     ('R003', 'U003', 'alice.mgr@college.edu', '2024-10-25', 'REJECTED', 22.50),
                                                                                                     ('R004', 'U004', 'alice.mgr@college.edu', '2024-10-28', 'CANCEL_REQUESTED', 53.50),
                                                                                                     ('R005', 'U003', 'bob.mgr@college.edu', '2024-11-01', 'PENDING', 20.00);

-- 4. REQUEST_DETAIL DATA
INSERT INTO REQUEST_DETAIL (detail_id, request_id, item_id, quantity, unit_price_at_time) VALUES
                                                                                              ('D101', 'R001', 'I102', 10, 1.25),
                                                                                              ('D102', 'R001', 'I101', 1, 5.50),
                                                                                              ('D103', 'R002', 'I103', 1, 18.00),
                                                                                              ('D104', 'R002', 'I104', 4, 3.50),
                                                                                              ('D105', 'R003', 'I101', 4, 5.50),
                                                                                              ('D106', 'R004', 'I101', 5, 5.50),
                                                                                              ('D107', 'R004', 'I104', 10, 3.50),
                                                                                              ('D108', 'R005', 'I103', 1, 18.00),
                                                                                              ('D109', 'R005', 'I102', 1, 1.25);