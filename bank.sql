create database peoplegobanks;

use peoplegobanks;

CREATE TABLE accounts (
    accountNumber VARCHAR(20) PRIMARY KEY,
    username VARCHAR(50),
    password VARCHAR(50),
    accountHolderName VARCHAR(100),
    accountType VARCHAR(20),
    balance DECIMAL(10, 2),
    minBalance DECIMAL(10, 2),
    gender VARCHAR(10),
    profileImage LONGBLOB,
    pan VARCHAR(10),
    aadhaar VARCHAR(12)
);


CREATE TABLE user_validation (
    username VARCHAR(50) PRIMARY KEY,
    pan VARCHAR(10),
    aadhaar VARCHAR(12)
);

CREATE TABLE transactions (
    transactionId INT AUTO_INCREMENT PRIMARY KEY,
    accountNumber VARCHAR(20),
    transactionType VARCHAR(20),
    amount DECIMAL(10, 2),
    transactionDate TIMESTAMP
);

CREATE TABLE transactions (
    transactionID INT AUTO_INCREMENT PRIMARY KEY,
    accountNumber VARCHAR(20),
    transactionType VARCHAR(20),
    amount DOUBLE,
    transactionDate TIMESTAMP,
    FOREIGN KEY (accountNumber) REFERENCES accounts(accountNumber)
);

CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    accountHolderName VARCHAR(255) NOT NULL,
    accountNumber VARCHAR(255) NOT NULL,
    suggestions TEXT NOT NULL,
    submissionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contact (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    subject VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    submissionDate TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Select all records from the accounts table
SELECT * FROM accounts;
drop table accounts;
-- Select all records from the user_validation table
SELECT * FROM user_validation;
drop table user_validation ;
-- Select all records from the transactions table
SELECT * FROM transactions;
drop table transactions;
-- Select all records from the feedback table
SELECT * FROM feedback;
drop table feedback;

-- Select all records from the contact table
SELECT * FROM contact;
drop table contact;

drop database peoplegobanks;


