DROP DATABASE IF EXISTS camping_rental;
CREATE DATABASE camping_rental;
USE camping_rental;

-- 1. Divisions table (Moved up so workers/customers can see it)
CREATE TABLE divisions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Categories table
CREATE TABLE categories (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT
);

-- 3. Users table
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Customers table (Now correctly references users and divisions)
CREATE TABLE customers (
    user_id INT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    phone VARCHAR(20),
    shift VARCHAR(50),
    division_id INT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (division_id) REFERENCES divisions(id) ON DELETE SET NULL
);

-- 5. Workers table
CREATE TABLE workers (
    user_id INT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    shift VARCHAR(50),
    division_id INT,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (division_id) REFERENCES divisions(id) ON DELETE SET NULL
);

-- 6. Equipments table
CREATE TABLE equipments (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    brand VARCHAR(100),
    available_stock INT DEFAULT 0,
    price_per_day INT NOT NULL,
    `condition` VARCHAR(50) DEFAULT 'GOOD',
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

-- 7. Invoices table (returned changed to BOOLEAN)
CREATE TABLE invoices (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    worker_id INT,
    total_amount INT,
    payment_status ENUM('PENDING', 'PAID', 'CANCELLED') DEFAULT 'PENDING',
    rent_date DATE,
    expected_return_date DATE,
    returned BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES customers(user_id),
    FOREIGN KEY (worker_id) REFERENCES workers(user_id)
);

-- 8. Invoice details table
CREATE TABLE invoice_details (
    invoice_id INT,
    equipments_id INT,
    quantity INT,
    time_period_in_day INT,
    amount INT,
    PRIMARY KEY (invoice_id, equipments_id),
    FOREIGN KEY (invoice_id) REFERENCES invoices(id) ON DELETE CASCADE,
    FOREIGN KEY (equipments_id) REFERENCES equipments(id)
);

-- 9. Returns table
CREATE TABLE returns (
    id INT PRIMARY KEY AUTO_INCREMENT,
    return_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    worker_id INT,
    invoice_id INT,
    FOREIGN KEY (worker_id) REFERENCES workers(user_id),
    FOREIGN KEY (invoice_id) REFERENCES invoices(id)
);

-- 10. Returns detail table
CREATE TABLE returns_detail (
    id INT PRIMARY KEY AUTO_INCREMENT,
    returns_id INT,
    equipment_id INT,
    quantity_returned INT DEFAULT 0,
    quantity_lost INT DEFAULT 0,
    quantity_damaged INT DEFAULT 0,
    FOREIGN KEY (returns_id) REFERENCES returns(id) ON DELETE CASCADE,
    FOREIGN KEY (equipment_id) REFERENCES equipments(id)
);

-- 11. Penalties table (name changed to detail to match your ERD)
CREATE TABLE penalties (
    id INT PRIMARY KEY AUTO_INCREMENT,
    returns_detail_id INT,
    name VARCHAR(255),
    fine INT,
    FOREIGN KEY (returns_detail_id) REFERENCES returns_detail(id) ON DELETE CASCADE
);

-- ==========================================
-- INSERT DATA (In exact order of dependency)
-- ==========================================

-- Base independent data
INSERT INTO divisions (id, name) VALUES (1, 'Administration'), (2, 'Operations'), (3, 'Maintenance');

INSERT INTO categories (id, name, description) VALUES 
(1, 'Tents', 'Camping tents of various sizes'),
(2, 'Sleeping Bags', 'Warm sleeping bags for all seasons'),
(3, 'Cooking Gear', 'Stoves, pots, and cooking utensils'),
(4, 'Hiking Equipment', 'Backpacks, trekking poles, and accessories');

INSERT INTO users (id, username, password) VALUES 
(1, 'admin', 'admin123'),
(2, 'worker1', 'worker123'),
(3, 'customer1', 'cust123');

-- Dependent personnel data (Requires users and divisions to exist first)
INSERT INTO workers (user_id, name, phone, shift, division_id) VALUES
(2, 'John Worker', '08123456789', 'Morning', 1);

INSERT INTO customers (user_id, full_name, email, phone, shift, division_id) VALUES
(3, 'John Doe', 'john@example.com', '08987654321', 'Flexible', 2);

-- Dependent stock data
INSERT INTO equipments (id, category_id, name, brand, available_stock, price_per_day) VALUES
(1, 1, '4-Person Tent', 'Coleman', 10, 50000),
(2, 1, '2-Person Tent', 'North Face', 15, 35000),
(3, 2, 'Winter Sleeping Bag', 'Mountain Hardware', 20, 25000),
(4, 3, 'Portable Stove', 'Primus', 8, 30000);