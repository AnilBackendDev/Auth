# 🚀 Authentication Service - Local Testing Guide

## Overview
This guide will help you set up and test the Authentication Service locally with database migrations and Swagger UI.

---

## ✅ Prerequisites

- ☑️ Java 17 or higher
- ☑️ Maven 3.6+
- ☑️ MySQL 8.0+ (running locally)
- ☑️ Git
- ☑️ Terminal/Command Prompt

---

## 📋 Step-by-Step Setup

### Step 1: Create Local MySQL Database

```bash
# Connect to MySQL
mysql -u root -p

# Create database
CREATE DATABASE auth_auth_local CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# Verify database created
SHOW DATABASES;

# Exit MySQL
EXIT;
```

### Step 2: Update Application Configuration

Navigate to the auth service directory:
```bash
cd /Users/credr/Documents/Og/auth-auth-service
```

**Option A: Use Local Configuration File**
```bash
# Copy the local configuration template
cp /Users/credr/Documents/Og/auth-b2b-backend/application-local.properties \
   src/main/resources/application.properties
```

**Option B: Manual Update**

Edit `src/main/resources/application.properties` and update the database section:

```properties
# Update these values
spring.datasource.url=jdbc:mysql://localhost:3306/auth_auth_local?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# Ensure Flyway is enabled
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

### Step 3: Build the Project

```bash
cd /Users/credr/Documents/Og/auth-auth-service

# Clean and build
./mvnw clean install -DskipTests
```

**Expected Output:**
```
[INFO] BUILD SUCCESS
[INFO] Total time:  XX.XXX s
```

### Step 4: Run Database Migrations

Migrations will run automatically when you start the application. To verify manually:

```bash
# Run Flyway migration
./mvnw flyway:migrate
```

**What gets created:**
- ✅ `user` table
- ✅ `role` table  
- ✅ `permissions` table
- ✅ `role_permission` junction table
- ✅ `token` table
- ✅ `otp` table

**What gets inserted:**
- ✅ 5 default roles
- ✅ 21 permissions with role assignments
- ✅ 1 admin user (admin@auth.com / Admin@123)

### Step 5: Start the Application

```bash
./mvnw spring-boot:run
```

**Expected Output:**
```
====================================================
   Auth Authentication Service Started
   Port: 8081
   Ready to handle authentication requests!
====================================================
```

### Step 6: Verify the Service is Running

**Health Check:**
```bash
curl http://localhost:8081/actuator/health
```

**Expected Response:**
```json
{"status":"UP"}
```

---

## 📖 Access Swagger UI

### Open Swagger Documentation

**Browser:** http://localhost:8081/swagger-ui.html

**API Docs (JSON):** http://localhost:8081/v3/api-docs

### Swagger UI Features

- 📝 Complete API documentation
- 🧪 Try out endpoints directly
- 🔐 JWT token authentication support
- 📋 Request/response examples

---

## 🧪 Testing the API

### Test 1: Login with Default Admin

**Using Swagger UI:**

1. Open http://localhost:8081/swagger-ui.html
2. Find `POST /api/auth/login`
3. Click "Try it out"
4. Enter request body:
```json
{
  "email": "admin@auth.com",
  "password": "Admin@123"
}
```
5. Click "Execute"

**Using cURL:**

```bash
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@auth.com",
    "password": "Admin@123"
  }'
```

**Expected Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "...",
  "user": {
    "id": 1,
    "email": "admin@auth.com",
    "firstName": "Admin",
    "role": "ADMIN"
  }
}
```

**Copy the `token` value for the next tests!**

### Test 2: Get All Roles (Authenticated)

**Using Swagger UI:**

1. Click the "Authorize" button (🔒) at the top of Swagger UI
2. Enter: `Bearer YOUR_TOKEN_HERE`
3. Click "Authorize"
4. Find `GET /api/roles`
5. Click "Try it out" → "Execute"

**Using cURL:**

```bash
curl -X GET "http://localhost:8081/api/roles" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

**Expected Response:**
```json
[
  {"id": 1, "roleName": "ADMIN"},
  {"id": 2, "roleName": "DISTRIBUTOR"},
  {"id": 3, "roleName": "RETAILER"},
  {"id": 4, "roleName": "STOCKIST"},
  {"id": 5, "roleName": "MARKETING_USER"}
]
```

### Test 3: Register New User

**Using cURL:**

```bash
curl -X POST "http://localhost:8081/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "password": "Test@123",
    "mobileNumber": "1234567890",
    "roleId": 2,
    "companyName": "Test Company"
  }'
```

### Test 4: Change Password

```bash
curl -X POST "http://localhost:8081/api/auth/change-password" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "oldPassword": "Admin@123",
    "newPassword": "NewAdmin@123"
  }'
```

---

## 🗄️ Verify Database

### Check Tables Created

```sql
mysql -u root -p auth_auth_local

SHOW TABLES;
```

**Expected Output:**
```
+---------------------------+
| Tables_in_auth_auth_local |
+---------------------------+
| flyway_schema_history     |
| otp                       |
| permissions               |
| role                      |
| role_permission           |
| token                     |
| user                      |
+---------------------------+
```

### Check Roles Inserted

```sql
SELECT * FROM role;
```

### Check Permissions

```sql
SELECT * FROM permissions;
```

### Check Admin User

```sql
SELECT id, first_name, email_id, role_id FROM user;
```

### Check Flyway Migration History

```sql
SELECT * FROM flyway_schema_history;
```

---

## 📊 Available API Endpoints

### Authentication Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/register` | Register new user | ❌ No |
| POST | `/api/auth/login` | User login | ❌ No |
| POST | `/api/auth/logout` | User logout | ✅ Yes |
| POST | `/api/auth/refresh-token` | Refresh JWT | ✅ Yes |
| POST | `/api/auth/verify-otp` | Verify OTP | ❌ No |
| POST | `/api/auth/resend-otp` | Resend OTP | ❌ No |

### Password Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/auth/forgot-password` | Initiate password reset | ❌ No |
| POST | `/api/auth/reset-password` | Reset password with OTP | ❌ No |
| POST | `/api/auth/change-password` | Change password | ✅ Yes |

### Role Management (Admin Only)

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/roles` | List all roles | ✅ Yes |
| POST | `/api/roles` | Create new role | ✅ Yes (Admin) |
| GET | `/api/roles/{id}` | Get role details | ✅ Yes |
| PUT | `/api/roles/{id}` | Update role | ✅ Yes (Admin) |
| DELETE | `/api/roles/{id}` | Delete role | ✅ Yes (Admin) |

### Monitoring & Health

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/actuator/health` | Health check |
| GET | `/actuator/info` | Application info |
| GET | `/actuator/metrics` | Application metrics |
| GET | `/actuator/flyway` | Database migration history |

---

## 🐛 Troubleshooting

### Issue 1: Database Connection Failed

**Error:**
```
Communications link failure
```

**Solution:**
1. Verify MySQL is running: `brew services list` or `systemctl status mysql`
2. Check credentials in `application.properties`
3. Ensure database exists: `SHOW DATABASES;`
4. Check MySQL is on port 3306: `netstat -an | grep 3306`

### Issue 2: Flyway Migration Failed

**Error:**
```
FlywayException: Migration failed
```

**Solution:**
1. Check migration files syntax
2. Verify database schema exists
3. Drop and recreate database:
```sql
DROP DATABASE auth_auth_local;
CREATE DATABASE auth_auth_local CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```
4. Restart application

### Issue 3: Port 8081 Already in Use

**Error:**
```
Port 8081 was already in use
```

**Solution:**
1. Find and kill process:
```bash
lsof -i :8081
kill -9 <PID>
```
2. Or change port in `application.properties`:
```properties
server.port=8082
```

### Issue 4: Swagger UI Not Loading

**Solution:**
1. Verify Swagger dependency in pom.xml
2. Check application is running
3. Clear browser cache
4. Try: http://localhost:8081/swagger-ui/index.html

### Issue 5: JWT Token Invalid

**Solution:**
1.Check token expiration (default: 30 days)
2. Ensure `jwt.secret-key` matches across services
3. Verify token format: `Bearer <token>`

---

## ✨ Default Credentials

### Admin User
- **Email:** admin@auth.com
- **Password:** Admin@123
- **Role:** ADMIN

⚠️ **IMPORTANT:** Change this password in production!

---

## 📝 Database Schema Details

### User Table
```sql
- id: INT (Primary Key)
- email_id: VARCHAR(255) (Unique)
- password: VARCHAR(255) (BCrypt hashed)
- first_name: VARCHAR(100)
- last_name: VARCHAR(100)
- mobile_number: VARCHAR(20)
- role_id: INT (Foreign Key)
- is_user_verified: ENUM
- company_name: VARCHAR(255)
- gst: VARCHAR(50)
- city, state, address: TEXT
- status: INT
- created_at, updated_at: TIMESTAMP
```

### Role & Permissions
- Many-to-Many relationship via `role_permission` table
- Each role can have multiple permissions
- Permissions are defined in the permissions table

---

## 🎯 Next Steps

1. ✅ Test all authentication endpoints
2. ✅ Create additional test users
3. ✅ Test role-based access control
4. ✅ Integrate with frontend application
5. ✅ Add custom roles and permissions
6. ✅ Configure SMS/Email for OTP (optional)
7. ✅ Set up production database
8. ✅ Deploy to staging/production

---

## 📞 Support

For issues, check:
- Application logs: `logs/application.log`
- MySQL error log
- Flyway migration history: `/actuator/flyway`
- Swagger UI error console

---

**Created:** February 6, 2026  
**Version:** 1.0.0  
**Status:** ✅ Ready for Testing
