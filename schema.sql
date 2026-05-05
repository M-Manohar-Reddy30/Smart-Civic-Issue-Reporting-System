-- MySQL schema setup for Smart Civic Issue Reporting System
CREATE DATABASE IF NOT EXISTS civic_app;
USE civic_app;

-- Updated Users table for Authentication & Gamification
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role ENUM('CITIZEN', 'ADMIN') NOT NULL DEFAULT 'CITIZEN',
  points INT DEFAULT 0,
  issues_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert default admin user (Note: Password will be hashed in Phase 1 setup)
-- The password 'Nani@8888' is placeholder here; we will handle hashing in code.
INSERT IGNORE INTO users (name, email, password, role) 
VALUES ('Admin', 'manoharreddyind@gmail.com', 'Nani@8888', 'ADMIN');

-- Updated Issues table with user linkage
CREATE TABLE IF NOT EXISTS issues (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT, -- Link to reporting user (nullable initially for safety)
  category VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  location VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'Pending',
  image_path VARCHAR(500),
  proof_start_path VARCHAR(500),
  proof_end_path VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Migration: Ensure columns exist
ALTER TABLE issues ADD COLUMN IF NOT EXISTS user_id INT;
ALTER TABLE users ADD COLUMN IF NOT EXISTS name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS points INT DEFAULT 0;
ALTER TABLE users ADD COLUMN IF NOT EXISTS issues_count INT DEFAULT 0;
