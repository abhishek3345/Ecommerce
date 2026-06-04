-- Category table
INSERT INTO category (category_name) VALUES ('Fashion');
INSERT INTO category (category_name) VALUES ('Electronics');
INSERT INTO category (category_name) VALUES ('Books');
INSERT INTO category (category_name) VALUES ('Groceries');
INSERT INTO category (category_name) VALUES ('Medicines');

-- Users table (using 'users' instead of 'user')
INSERT INTO users (username, password) VALUES ('jack', 'pass_word');
INSERT INTO users (username, password) VALUES ('bob', 'pass_word');
INSERT INTO users (username, password) VALUES ('apple', 'pass_word');
INSERT INTO users (username, password) VALUES ('glaxo', 'pass_word');

-- Cart table
INSERT INTO cart (total_amount, user_user_id) VALUES (20, 1);
INSERT INTO cart (total_amount, user_user_id) VALUES (0, 2);

-- User roles table
INSERT INTO user_role (user_id, roles) VALUES (1, 'CONSUMER');
INSERT INTO user_role (user_id, roles) VALUES (2, 'CONSUMER');
INSERT INTO user_role (user_id, roles) VALUES (3, 'SELLER');
INSERT INTO user_role (user_id, roles) VALUES (4, 'SELLER');

-- Products table
INSERT INTO product (price, product_name, category_id, seller_id)
VALUES (29190, 'Apple iPad 10.2 8th Gen WiFi iOS Tablet', 2, 3);
INSERT INTO product (price, product_name, category_id, seller_id)
VALUES (10, 'Crocin pain relief tablet', 5, 4);

-- Cart products table
INSERT INTO cart_product (cart_id, product_id, quantity) VALUES (1, 2, 2);