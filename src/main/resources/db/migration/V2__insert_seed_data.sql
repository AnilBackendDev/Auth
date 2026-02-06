-- Authentication Service - Seed Data
-- Inserts default roles and permissions

-- Insert default roles
INSERT INTO role (role_name, status, created_by, updated_by) VALUES
('ADMIN', 1, 0, 0),
('DISTRIBUTOR', 1, 0, 0),
('RETAILER', 1, 0, 0),
('STOCKIST', 1, 0, 0),
('MARKETING_USER', 1, 0, 0)
ON DUPLICATE KEY UPDATE role_name = VALUES(role_name);

-- Insert default permissions
INSERT INTO permissions (name) VALUES
-- User Management Permissions
('USER_READ'),
('USER_CREATE'),
('USER_UPDATE'),
('USER_DELETE'),

-- Role Management Permissions
('ROLE_READ'),
('ROLE_CREATE'),
('ROLE_UPDATE'),
('ROLE_DELETE'),

-- Authentication Permissions
('AUTH_LOGIN'),
('AUTH_REGISTER'),
('AUTH_LOGOUT'),
('AUTH_REFRESH_TOKEN'),

-- Password Management Permissions
('PASSWORD_CHANGE'),
('PASSWORD_RESET'),
('PASSWORD_FORGOT'),

-- OTP Permissions
('OTP_GENERATE'),
('OTP_VERIFY'),

-- Admin Permissions
('ADMIN_ACCESS'),
('ADMIN_DASHBOARD'),

-- General Permissions
('READ_PROFILE'),
('UPDATE_PROFILE')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- Assign permissions to ADMIN role
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'ADMIN'
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- Assign permissions to DISTRIBUTOR role
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'DISTRIBUTOR'
AND p.name IN (
    'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_REFRESH_TOKEN',
    'PASSWORD_CHANGE', 'PASSWORD_RESET', 'PASSWORD_FORGOT',
    'READ_PROFILE', 'UPDATE_PROFILE',
    'OTP_GENERATE', 'OTP_VERIFY'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- Assign permissions to RETAILER role
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'RETAILER'
AND p.name IN (
    'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_REFRESH_TOKEN',
    'PASSWORD_CHANGE', 'PASSWORD_RESET', 'PASSWORD_FORGOT',
    'READ_PROFILE', 'UPDATE_PROFILE',
    'OTP_GENERATE', 'OTP_VERIFY'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- Assign permissions to STOCKIST role
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'STOCKIST'
AND p.name IN (
    'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_REFRESH_TOKEN',
    'PASSWORD_CHANGE', 'PASSWORD_RESET', 'PASSWORD_FORGOT',
    'READ_PROFILE', 'UPDATE_PROFILE',
    'OTP_GENERATE', 'OTP_VERIFY',
    'USER_READ', 'USER_CREATE'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- Assign permissions to MARKETING_USER role
INSERT INTO role_permission (role_id, permission_id)
SELECT r.id, p.id
FROM role r
CROSS JOIN permissions p
WHERE r.role_name = 'MARKETING_USER'
AND p.name IN (
    'AUTH_LOGIN', 'AUTH_LOGOUT', 'AUTH_REFRESH_TOKEN',
    'PASSWORD_CHANGE',
    'READ_PROFILE'
)
ON DUPLICATE KEY UPDATE role_id = VALUES(role_id);

-- Create default admin user (password: Admin@123 - BCrypt hashed)
-- Note: You should change this password in production!
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
    '$2a$10$eImiTXuWVpfIPo3vLJvBQeEP2HqgWw/6YG0qJ7qvOaYmKmPnc/lKS', -- Admin@123
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
