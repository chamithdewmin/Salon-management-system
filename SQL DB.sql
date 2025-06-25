CREATE DATABASE monthly_budget;
USE monthly_budget;

CREATE TABLE income (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    description VARCHAR(255),
    value DOUBLE NOT NULL
);

CREATE TABLE expense (
    id INT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    description VARCHAR(255),
    value DOUBLE NOT NULL,
    category ENUM('Needs', 'Wants') NOT NULL
);

CREATE TABLE IF NOT EXISTS reminders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    type ENUM('Income', 'Expense') NOT NULL,
    date DATE NOT NULL,
    description VARCHAR(255),
    cost DOUBLE NOT NULL
);

drop table income;
Drop table expense;