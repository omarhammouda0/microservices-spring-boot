-- Insert Products with conflict handling
INSERT INTO products (name, price, created_by, created_date)
VALUES
    ('Laptop Pro X1', 1299.99, 1, NOW()),
    ('Smartphone Ultra', 899.99, 1, NOW()),
    ('Wireless Headphones', 199.99, 2, NOW()),
    ('4K Monitor 27"', 449.99, 1, NOW()),
    ('Mechanical Keyboard', 129.99, 3, NOW()),
    ('Gaming Mouse', 79.99, 2, NOW()),
    ('External SSD 1TB', 159.99, 1, NOW()),
    ('USB-C Hub', 49.99, 3, NOW()),
    ('Webcam HD', 89.99, 2, NOW()),
    ('Laptop Stand', 39.99, 1, NOW())
ON CONFLICT (name) DO NOTHING;

-- Insert Inventory with conflict handling (safe to re-run)
WITH product_quantities AS (
    VALUES
        ('Laptop Pro X1', 50),
        ('Smartphone Ultra', 100),
        ('Wireless Headphones', 200),
        ('4K Monitor 27"', 30),
        ('Mechanical Keyboard', 150),
        ('Gaming Mouse', 180),
        ('External SSD 1TB', 75),
        ('USB-C Hub', 250),
        ('Webcam HD', 60),
        ('Laptop Stand', 120)
)
INSERT INTO inventory (product_id, quantity, created_date, last_modified_date, version)
SELECT
    p.id,
    pq.column2,
    NOW(),
    NOW(),
    0
FROM products p
         JOIN product_quantities pq ON p.name = pq.column1
ON CONFLICT (product_id) DO NOTHING;