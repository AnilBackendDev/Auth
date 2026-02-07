-- =============================================
-- AUTH SERVICE - COMPLETE DATABASE SCHEMA
-- =============================================
-- This file contains all tables, indexes, and seed data needed for the authentication service
-- Features: User Auth, Roles, Permissions, JWT Tokens, OTP, OAuth2

-- =============================================
-- 1. CREATE TABLES
-- =============================================

-- Create role table first (referenced by user table)
CREATE TABLE IF NOT EXISTS role (
    id INT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(100) NOT NULL UNIQUE,
    status INT DEFAULT 1,
    created_by INT,
    updated_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_role_name (role_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create user table with OAuth support
CREATE TABLE IF NOT EXISTS user (
    id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    email_id VARCHAR(255) UNIQUE,
    password VARCHAR(255),
    mobile_number VARCHAR(20),
    is_user_verified ENUM('PENDING', 'VERIFIED', 'REJECTED') NOT NULL DEFAULT 'PENDING',
    role_id INT,
    status INT DEFAULT 1,
    
    -- OAuth fields
    oauth_provider VARCHAR(50) DEFAULT NULL COMMENT 'OAuth provider (e.g., GOOGLE)',
    oauth_provider_id VARCHAR(255) DEFAULT NULL COMMENT 'Unique ID from OAuth provider',
    
    -- Optional business fields
    company_name VARCHAR(255),
    gst VARCHAR(50),
    address TEXT,
    city VARCHAR(100),
    state VARCHAR(100),
    reason TEXT,
    alternative_mobile_number VARCHAR(20),
    is_updated INT DEFAULT 0,
    
    -- Audit fields
    created_by INT,
    updated_by INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes
    INDEX idx_email (email_id),
    INDEX idx_mobile (mobile_number),
    INDEX idx_role (role_id),
    INDEX idx_oauth_provider_id (oauth_provider, oauth_provider_id),
    INDEX idx_email_oauth (email_id, oauth_provider),
    
    -- Foreign key
    CONSTRAINT fk_user_role FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create permissions table
CREATE TABLE IF NOT EXISTS permissions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create role_permission junction table
CREATE TABLE IF NOT EXISTS role_permission (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
    PRIMARY KEY (role_id, permission_id),
    FOREIGN KEY (role_id) REFERENCES role(id) ON DELETE CASCADE,
    FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create token table for JWT management
CREATE TABLE IF NOT EXISTS token (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token TEXT NOT NULL,
    token_type ENUM('BEARER') DEFAULT 'BEARER',
    revoked BOOLEAN DEFAULT FALSE,
    expired BOOLEAN DEFAULT FALSE,
    user_id INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Create OTP table with mobile number support
CREATE TABLE IF NOT EXISTS otp (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    mobile_number VARCHAR(20) DEFAULT NULL,
    otp_value VARCHAR(10) NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    expiration_time TIMESTAMP NOT NULL,
    INDEX idx_user_id (user_id),
    INDEX idx_mobile (mobile_number),
    INDEX idx_verified (is_verified),
    INDEX idx_expiration (expiration_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 2. INSERT SEED DATA
-- =============================================

-- Insert default roles
INSERT INTO role (role_name, status, created_by, updated_by) VALUES
('ADMIN', 1, 0, 0),
('USER', 1, 0, 0),
('DISTRIBUTOR', 1, 0, 0),
('RETAILER', 1, 0, 0),
('STOCKIST', 1, 0, 0),
('MARKETING_USER', 1, 0, 0)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- Insert default permissions
INSERT INTO permissions (name) VALUES
-- User Management
('USER_READ'),
('USER_CREATE'),
('USER_UPDATE'),
('USER_DELETE'),

-- Role Management
('ROLE_READ'),
('ROLE_CREATE'),
('ROLE_UPDATE'),
('ROLE_DELETE'),

-- Authentication
('AUTH_LOGIN'),
('AUTH_REGISTER'),
('AUTH_LOGOUT'),
('AUTH_REFRESH_TOKEN'),

-- Password Management
('PASSWORD_CHANGE'),
('PASSWORD_RESET'),
('PASSWORD_FORGOT'),

-- OTP
('OTP_GENERATE'),
('OTP_VERIFY'),

-- Admin
('ADMIN_ACCESS'),
('ADMIN_DASHBOARD'),

-- Profile
('READ_PROFILE'),
('UPDATE_PROFILE')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- =============================================
-- 3. ASSIGN PERMISSIONS TO ROLES
-- =============================================

-- ADMIN: All permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- USER: Basic permissions
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'USER'
AND p.name IN (
    'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_REFRESH_TOKEN',
    'PASSWORD_CHANGE', 'PASSWORD_RESET', 'PASSWORD_FORGOT',
    'READ_PROFILE', 'UPDATE_PROFILE',
    'OTP_GENERATE', 'OTP_VERIFY'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);


-- =============================================
-- 4. CREATE DEFAULT ADMIN USER
-- =============================================
-- Password: Admin@123 (BCrypt hashed)
-- IMPORTANT: Change this password in production!

INSERT INTO user (
    first_name,
    last_name,
    email_id,
    password,
    mobile_number,
    is_user_verified,
    role_id,
    status,
    created_by,
    updated_by
)
SELECT
    'Admin',
    'User',
    'admin@auth-service.com',
    '$2a$10$eImiTXuWVpfIPo3vLJvBQeEP2HqgWw/6YG0qJ7qvOaYmKmPnc/lKS',
    '9999999999',
    'VERIFIED',
    r.id,
    1,
    0,
    0
FROM role r
WHERE r.role_name = 'ADMIN'
LIMIT 1
ON DUPLICATE KEY UPDATE email_id = VALUES(email_id);

-- =============================================
-- SCHEMA SETUP COMPLETE
-- =============================================
