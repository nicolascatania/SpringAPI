CREATE DATABASE products;

Use products;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT
);

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL,
    image_url VARCHAR(255),
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
INSERT INTO categories (id, name, description) VALUES
(1, 'Electronics', 'Devices, gadgets, and equipment such as phones, laptops, and televisions.'),
(2, 'Books', 'Printed or digital materials for reading, covering various genres and subjects.'),
(3, 'Clothing', 'Apparel for men, women, and children, including casual and formal wear.'),
(4, 'Home & Kitchen', 'Products for household use, including appliances, utensils, and furniture.'),
(5, 'Beauty', 'Cosmetics, skincare products, and personal care items.'),
(6, 'Sports', 'Equipment and apparel related to sports and outdoor activities.'),
(7, 'Toys', 'Playthings for children, including educational and recreational toys.'),
(8, 'Automotive', 'Car parts, accessories, and tools for vehicles.'),
(9, 'Health', 'Products related to health and wellness, including supplements and medical supplies.'),
(10, 'Grocery', 'Food items and household essentials available for daily needs.'),
(11, 'Music', 'Instrumens, Cds, etc');
