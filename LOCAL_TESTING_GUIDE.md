# Local Testing Guide

Complete guide for testing the Authentication Service locally with step-by-step instructions, example requests, and expected responses.

---

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Initial Setup](#initial-setup)
- [Test Scenarios](#test-scenarios)
  - [1. User Registration](#1-user-registration)
  - [2. Login with Password](#2-login-with-password)
  - [3. Login with OTP](#3-login-with-otp)
  - [4. Google OAuth Login](#4-google-oauth-login)
  - [5. Password Management](#5-password-management)
  - [6. Token Refresh](#6-token-refresh)
  - [7. Role Management](#7-role-management)
- [Testing Tools](#testing-tools)
- [Common Issues](#common-issues)

---

## ✅ Prerequisites

Before testing, ensure you have:

1. **Java 17+** installed
2. **MySQL 8.0+** running
3. **Database created:**
   ```bash
   mysql -u root -p
   CREATE DATABASE auth_user;
   ```
4. **Application** configured in `application.properties`
5. **Application running:** `./mvnw spring-boot:run`

---

## 🚀 Initial Setup

### Step 1: Start the Application

```bash
cd /path/to/auth-service
./mvnw spring-boot:run
```

**Verify startup:**
- Check console for: `Started AuthServiceApplication`
- Application runs on: **http://localhost:8081**

### Step 2: Verify Database

Tables should be auto-created by Flyway:
```bash
mysql -u root -p auth_user
SHOW TABLES;
```

**Expected tables:**
- user
- role
- permissions
- role_permission
- token
- otp

### Step 3: Access Swagger UI

Open browser: **http://localhost:8081/swagger-ui.html**

You should see all API endpoints documented.

### Step 4: Verify Default Admin

Default admin user is created automatically:
- **Email:** admin@auth-service.com
- **Password:** Admin@123
- **Mobile:** 9999999999
- **Role:** ADMIN

---

## 🧪 Test Scenarios

### 1. User Registration

#### Test Case 1.1: Register New User (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "mobileNumber": "9876543210",
    "roleId": 2,
    "password": "John@123",
    "source": "web"
  }'
```

**Expected Response (201 Created):**
```json
{
  "message": "User registered successfully"
}
```

**Verify in Database:**
```sql
SELECT * FROM user WHERE email_id = 'john.doe@example.com';
```

#### Test Case 1.2: Register Duplicate Email (Failure)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "john.doe@example.com",
    "mobileNumber": "9876543211",
    "roleId": 2,
    "password": "Jane@123"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "message": "User with email john.doe@example.com already exists."
}
```

#### Test Case 1.3: Invalid Mobile Number (Failure)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "email": "test@example.com",
    "mobileNumber": "12345",
    "roleId": 2,
    "password": "Test@123"
  }'
```

**Expected Response (400 Bad Request):**
```json
{
  "message": "Mobile number must be 10 digits."
}
```

---

### 2. Login with Password

#### Test Case 2.1: Login with Email (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe@example.com",
    "password": "John@123",
    "source": "web"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "Login successful",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "BEARER",
  "userId": 2,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

**Save the accessToken** for subsequent authenticated requests.

#### Test Case 2.2: Login with Mobile Number (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "9876543210",
    "password": "John@123",
    "source": "mobile"
  }'
```

**Expected Response:** Same as Test Case 2.1

#### Test Case 2.3: Login with Wrong Password (Failure)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john.doe@example.com",
    "password": "WrongPassword",
    "source": "web"
  }'
```

**Expected Response (401 Unauthorized):**
```json
{
  "message": "Invalid credentials"
}
```

---

### 3. Login with OTP

#### Test Case 3.1: Send OTP (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "OTP sent successfully to ****3210"
}
```

**Check Console Logs:**
Look for: `OTP generated for 9876543210: 123456`

**Verify in Database:**
```sql
SELECT * FROM otp WHERE mobile_number = '9876543210' ORDER BY created_at DESC LIMIT 1;
```

#### Test Case 3.2: Verify OTP (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210",
    "otp": "123456"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "OTP verified successfully"
}
```

#### Test Case 3.3: Login with OTP (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login-with-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210",
    "otp": "123456"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "Login successful",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "BEARER",
  "userId": 2,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

#### Test Case 3.4: Expired OTP (Failure)

Wait for 5 minutes, then:

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/login-with-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210",
    "otp": "123456"
  }'
```

**Expected Response (401 Unauthorized):**
```json
{
  "message": "OTP has expired. Please request a new one."
}
```

---

### 4. Google OAuth Login

#### Test Case 4.1: Test OAuth with HTML File

1. **Update google-oauth-test.html:**
   - Open file
   - Replace `YOUR_GOOGLE_CLIENT_ID` with your actual Google Client ID (line ~75)

2. **Open in Browser:**
   ```bash
   open google-oauth-test.html
   ```

3. **Click "Sign in with Google"**

4. **Select Google Account**

5. **View Response:**
   - Access Token displayed
   - Refresh Token displayed
   - User info displayed

#### Test Case 4.2: OAuth Login via API (Success)

**Request:**
```bash
curl -X POST 'http://localhost:8081/api/v1/auth/oauth2/google/login?source=web' \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@gmail.com",
    "firstName": "Test",
    "lastName": "User",
    "oauthProviderId": "google-123456789",
    "roleId": 2
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "Login successful",
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "BEARER",
  "userId": 3,
  "email": "testuser@gmail.com",
  "firstName": "Test",
  "lastName": "User"
}
```

**Verify in Database:**
```sql
SELECT * FROM user WHERE oauth_provider = 'GOOGLE' AND oauth_provider_id = 'google-123456789';
```

---

### 5. Password Management

#### Test Case 5.1: Forgot Password - Send OTP (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "OTP sent to your registered mobile number"
}
```

**Check Console for OTP**

#### Test Case 5.2: Reset Password with OTP (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "identifier": "9876543210",
    "otp": "123456",
    "newPassword": "NewPassword@123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "Password reset successfully. Please login with your new password."
}
```

**Verify:** Login with new password

#### Test Case 5.3: Update Password (Authenticated) (Success)

First, login to get access token, then:

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/update-password \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -d '{
    "currentPassword": "NewPassword@123",
    "newPassword": "AnotherPassword@123"
  }'
```

**Expected Response (200 OK):**
```json
{
  "message": "Password updated successfully. Please login again."
}
```

---

### 6. Token Refresh

#### Test Case 6.1: Refresh Access Token (Success)

**Request:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/refresh-token \
  -H "Authorization: Bearer YOUR_REFRESH_TOKEN"
```

**Expected Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "message": "success",
  "userId": 2,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe"
}
```

---

### 7. Role Management

#### Test Case 7.1: Get All Roles (Success)

**Request:**
```bash
curl -X GET 'http://localhost:8081/api/roles?page=0&count=10' \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "roleName": "ADMIN",
    "status": 1
  },
  {
    "id": 2,
    "roleName": "USER",
    "status": 1
  },
  {
    "id": 3,
    "roleName": "DISTRIBUTOR",
    "status": 1
  }
]
```

#### Test Case 7.2: Create New Role (Admin Only)

**Request:**
```bash
curl -X POST http://localhost:8081/api/roles \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer ADMIN_ACCESS_TOKEN" \
  -d '{
    "roleName": "MANAGER",
    "status": 1
  }'
```

**Expected Response (200 OK):**
```json
{
  "id": 7,
  "roleName": "MANAGER",
  "status": 1
}
```

---

## 🔧 Testing Tools

### 1. Swagger UI (Recommended)

**URL:** http://localhost:8081/swagger-ui.html

**Steps:**
1. Open Swagger UI
2. Find endpoint (e.g., /api/v1/auth/register)
3. Click "Try it out"
4. Fill in request body
5. Click "Execute"
6. View response

**Advantages:**
- Visual interface
- Request/response schemas
- Easy authentication (Authorize button)
- No command-line needed

### 2. Postman

**Import Collection:**
Create a new collection with these endpoints and save.

**Environment Variables:**
- `baseUrl`: http://localhost:8081
- `accessToken`: (set after login)
- `refreshToken`: (set after login)

**Authentication:**
- Go to Authorization tab
- Type: Bearer Token
- Token: {{accessToken}}

### 3. cURL (Command Line)

All examples in this guide use cURL.

**Save token to variable:**
```bash
TOKEN=$(curl -s -X POST http://localhost:8081/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{"username":"john.doe@example.com","password":"John@123","source":"web"}' \
  | jq -r '.accessToken')

echo $TOKEN
```

**Use token:**
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8081/api/roles?page=0&count=10
```

### 4. Browser DevTools

For OAuth testing:
1. Open `google-oauth-test.html`
2. Open DevTools (F12)
3. Go to Console tab
4. Click "Sign in with Google"
5. View requests in Network tab
6. Check console for responses

---

## ❗ Common Issues

### Issue 1: Port Already in Use

**Error:**
```
Web server failed to start. Port 8081 was already in use.
```

**Solution:**
```bash
# Find process
lsof -ti:8081

# Kill process
kill -9 $(lsof -ti:8081)

# Restart application
./mvnw spring-boot:run
```

### Issue 2: Database Connection Failed

**Error:**
```
Cannot create PoolableConnectionFactory
```

**Solution:**
1. Verify MySQL is running: `mysql -u root -p`
2. Check database exists: `SHOW DATABASES;`
3. Verify credentials in `application.properties`
4. Check MySQL port: default 3306

### Issue 3: Flyway Migration Failed

**Error:**
```
FlywayException: Validate failed
```

**Solution:**
```sql
-- Reset Flyway history
DELETE FROM flyway_schema_history WHERE version > 2;

-- Or drop and recreate database
DROP DATABASE auth_user;
CREATE DATABASE auth_user;
```

Restart application to re-run migrations.

### Issue 4: OTP Not Found

**Error:**
```
No OTP found for this mobile number
```

**Solution:**
1. Send OTP first: `POST /api/v1/auth/send-otp`
2. Check console logs for OTP value
3. Use OTP within 5 minutes
4. Check database: `SELECT * FROM otp WHERE mobile_number = '9876543210';`

### Issue 5: Invalid Token

**Error:**
```
JWT token is expired or invalid
```

**Solution:**
1. Check token expiration (default: 24 hours)
2. Use refresh token endpoint
3. Re-login to get new tokens
4. Verify token is passed in Authorization header: `Bearer <token>`

### Issue 6: OAuth Redirect Mismatch

**Error:**
```
redirect_uri_mismatch
```

**Solution:**
1. Go to Google Cloud Console
2. Check "Authorized redirect URIs"
3. Add: `http://localhost:3000/auth/callback`
4. Add: `http://localhost:8081/login/oauth2/code/google`
5. Save and wait 5 minutes for changes to propagate

### Issue 7: CORS Error

**Error:**
```
Access to fetch has been blocked by CORS policy
```

**Solution:**
Update `SecurityConfiguration.java`:
```java
configuration.setAllowedOrigins(List.of(
    "http://localhost:3000",
    "http://localhost:8081",
    "http://your-frontend-url"
));
```

---

## ✅ Testing Checklist

Use this checklist to verify all features:

- [ ] Application starts successfully
- [ ] Database tables created
- [ ] Swagger UI accessible
- [ ] Register new user
- [ ] Login with email + password
- [ ] Login with mobile + password
- [ ] Send OTP
- [ ] Verify OTP
- [ ] Login with OTP
- [ ] Google OAuth login (if configured)
- [ ] Forgot password
- [ ] Reset password with OTP
- [ ] Update password (authenticated)
- [ ] Refresh token
- [ ] Get all roles
- [ ] Create new role (admin)
- [ ] Health check endpoint
- [ ] Token expiration
- [ ] Invalid credentials error
- [ ] Duplicate user error

---

## 📊 Expected Database State

After running all tests, verify:

```sql
-- Should have multiple users
SELECT COUNT(*) FROM user;  -- At least 3 (admin + your test users)

-- Should have 6 default roles
SELECT COUNT(*) FROM role;  -- 6

-- Should have permissions
SELECT COUNT(*) FROM permissions;  -- ~20

-- Should have tokens for logged-in users
SELECT COUNT(*) FROM token;  -- Multiple

-- Should have OTP records
SELECT * FROM otp ORDER BY created_at DESC LIMIT 5;
```

---

## 🎯 Next Steps

After successful local testing:

1. **Security Review:**
   - Change default admin password
   - Review security configurations
   - Test authorization rules

2. **Integration Testing:**
   - Test with real SMS/WhatsApp service
   - Configure Google OAuth for production
   - Set up monitoring

3. **Performance Testing:**
   - Test with multiple concurrent users
   - Check database query performance
   - Monitor memory/CPU usage

4. **Deploy:**
   - Configure production environment
   - Set up CI/CD pipeline
   - Deploy to staging environment

---

**Happy Testing! 🚀**

For issues or questions, check the main README.md or application logs.
