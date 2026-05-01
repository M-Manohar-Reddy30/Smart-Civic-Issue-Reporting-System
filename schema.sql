-- MySQL schema setup for Smart Civic Issue Reporting System
CREATE DATABASE IF NOT EXISTS civic_app;
USE civic_app;

-- Users table for admin authentication (optional, but included for completeness)
CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  email VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255) NOT NULL,
  role VARCHAR(50) NOT NULL DEFAULT 'admin'
);

-- Insert default admin user
INSERT IGNORE INTO users (email, password, role) VALUES ('manoharreddyind@gmail.com', 'Nani@8888', 'admin');

-- Issues table
CREATE TABLE IF NOT EXISTS issues (
  id INT AUTO_INCREMENT PRIMARY KEY,
  category VARCHAR(100) NOT NULL,
  description TEXT NOT NULL,
  location VARCHAR(255) NOT NULL,
  status VARCHAR(50) NOT NULL DEFAULT 'Pending',
  image_path VARCHAR(500),
  proof_image_path VARCHAR(500),
  proof_start_path VARCHAR(500),
  proof_end_path VARCHAR(500),
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Migration: Add new columns if they don't exist
ALTER TABLE issues ADD COLUMN IF NOT EXISTS proof_start_path VARCHAR(500);
ALTER TABLE issues ADD COLUMN IF NOT EXISTS proof_end_path VARCHAR(500);
