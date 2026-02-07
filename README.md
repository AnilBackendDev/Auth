# Authentication Service

A comprehensive Spring Boot authentication service with JWT tokens, role-based access control, OTP verification, and Google OAuth2 integration.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [Quick Start](#quick-start)
- [Database Setup](#database-setup)
- [API Endpoints](#api-endpoints)
- [Authentication Methods](#authentication-methods)
- [Google OAuth Setup](#google-oauth-setup)
- [Configuration](#configuration)
- [Security](#security)
- [Testing](#testing)
- [Documentation](#documentation)

---

## ✨ Features

### **Core Authentication**
- ✅ User registration with role assignment
- ✅ Login with email/mobile + password
- ✅ **Mobile login with OTP** (password-less)
- ✅ **Google OAuth2 Login/Signup**
- ✅ JWT token-based authentication
- ✅ Refresh token mechanism
- ✅ Role-based access control (RBAC)
- ✅ Logout with token revocation

### **Password Management**
- ✅ Forgot password (OTP-based)
- ✅ Reset password with OTP verification
- ✅ Update password (authenticated users)

### **OTP Features**
- ✅ Send OTP to mobile number
- ✅ Verify OTP
- ✅ OTP-based login
- ✅ OTP expiration (5 minutes)

### **OAuth2 Support**
- ✅ Google Sign-In integration
- ✅ Automatic account linking
- ✅ New user auto-registration
- ✅ Profile auto-fill from Google

### **Role Management**
- ✅ Create and manage roles
- ✅ Permission system
- ✅ Role-permission mappings
- ✅ 6 default roles (ADMIN, USER, DISTRIBUTOR, RETAILER, STOCKIST, MARKETING_USER)

### **Additional Features**
- ✅ Swagger/OpenAPI documentation
- ✅ Actuator health endpoints
- ✅ Database migrations with Flyway
- ✅ Comprehensive error handling
- ✅ Request validation
- ✅ CORS configuration
- ✅ Notification service ready (SMS/WhatsApp)

---

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3.4.4**
  - Spring Security
  - Spring Data JPA
  - Spring OAuth2 Client
- **MySQL 8.0+**
- **JWT (JSON Web Tokens)**
- **Flyway** - Database migrations
- **Swagger/SpringDoc** - API documentation
- **Lombok** - Reduce boilerplate
- **Maven** - Dependency management

---

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- MySQL 8.0+
- Maven 3.6+

### 1. Clone the Repository
```bash
git clone <repository-url>
cd auth-service
```

### 2. Create Database
```bash
mysql -u root -p
CREATE DATABASE auth_user;
exit
```

### 3. Configure Application
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_user
spring.datasource.username=root
spring.datasource.password=your_password
```

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

### 5. Access Swagger UI
Open: **http://localhost:8081/swagger-ui.html**

### 6. Login with Default Admin
- **Email:** admin@auth-service.com
- **Password:** Admin@123
- ⚠️ **Change this password in production!**

---

## 🗄️ Database Setup

### Option 1: Auto-Migration (Recommended)
Flyway will automatically create tables on startup:
```bash
./mvnw spring-boot:run
# Migrations run automatically
```

### Option 2: Manual Setup
Use the single comprehensive schema file:
```bash
mysql -u root -p auth_user < database-schema.sql
```

### Database Schema

**Tables:**
- `user` - User accounts with OAuth support
- `role` - User roles
- `permissions` - System permissions
- `role_permission` - Role-permission mappings
- `token` - JWT token storage
- `otp` - OTP verification codes

**Key Features:**
- OAuth columns: `oauth_provider`, `oauth_provider_id`
- OTP columns: `mobile_number`, `is_verified`
- Indexes for performance optimization
- Foreign key constraints
- Audit fields (created_at, updated_at)

### Migration Files
Located in `src/main/resources/db/migration/`:
- `V1__create_auth_schema.sql` - Initial schema
- `V2__insert_seed_data.sql` - Default roles, permissions, admin user

---

## 🔌 API Endpoints

### Authentication Endpoints
```
POST   /api/v1/auth/register          - Register new user
POST   /api/v1/auth/authenticate      - Login (email/mobile + password)
POST   /api/v1/auth/refresh-token     - Refresh JWT token
```

**Register Request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "mobileNumber": "9876543210",
  "roleId": 2,
  "password": "Password@123"
}
```

**Login Request:**
```json
{
  "username": "john@example.com",  // or mobile number
  "password": "Password@123",
  "source": "web"
}
```

### OTP Endpoints
```
POST   /api/v1/auth/send-otp          - Send OTP to mobile
POST   /api/v1/auth/verify-otp        - Verify OTP
POST   /api/v1/auth/login-with-otp    - Login with OTP (no password)
```

**Send OTP Request:**
```json
{
  "mobileNumber": "9876543210"
}
```

**Login with OTP Request:**
```json
{
  "mobileNumber": "9876543210",
  "otp": "123456"
}
```

### Password Management
```
POST   /api/v1/auth/forgot-password   - Send reset OTP
POST   /api/v1/auth/reset-password    - Reset password with OTP
POST   /api/v1/auth/update-password   - Update password (authenticated)
```

**Forgot Password Request:**
```json
{
  "mobileNumber": "9876543210"
}
```

**Reset Password Request:**
```json
{
  "identifier": "9876543210",  // mobile or email
  "otp": "123456",
  "newPassword": "NewPassword@123"
}
```

### OAuth2 Endpoints
```
POST   /api/v1/auth/oauth2/google/login    - Google login/signup
POST   /api/v1/auth/oauth2/google/signup   - Google signup with role
GET    /api/v1/auth/oauth2/status           - OAuth status check
```

**Google Login Request:**
```json
{
  "email": "user@gmail.com",
  "firstName": "John",
  "lastName": "Doe",
  "oauthProviderId": "google-user-id",
  "roleId": 2
}
```

### Role Management (Admin Only)
```
GET    /api/roles                     - List all roles
POST   /api/roles                     - Create new role
GET    /api/roles/{id}                - Get role by ID
```

### Health Check
```
GET    /actuator/health               - Application health status
```

---

## 🔐 Authentication Methods

### 1. **Email/Mobile + Password**
Traditional login with username and password.

**Steps:**
1. Register user with email/mobile and password
2. Login with credentials
3. Receive JWT tokens
4. Use access token for API calls
5. Refresh when token expires

### 2. **Mobile + OTP (Password-less)**
Login using mobile number and OTP only.

**Steps:**
1. Send OTP to mobile number
2. Receive OTP (SMS/WhatsApp)
3. Login with mobile and OTP
4. Receive JWT tokens

### 3. **Google OAuth2**
Sign in with Google account.

**Steps:**
1. Click "Sign in with Google" on frontend
2. Authenticate with Google
3. Send Google user data to backend
4. Receive JWT tokens
5. Auto-creates account if new user

---

## 🔑 Google OAuth Setup

### 1. Google Cloud Console Setup

1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create or select a project
3. Navigate to: **APIs & Services** → **Credentials**
4. Click **+ CREATE CREDENTIALS** → **OAuth client ID**
5. **Configure OAuth Consent Screen:**
   - App name: Your App Name
   - User support email: your-email@example.com
   - Developer contact: your-email@example.com
6. **Create OAuth Client ID:**
   - Application type: **Web application**
   - Name: Auth Service Web Client
   - Authorized JavaScript origins: `http://localhost:3000`
   - Authorized redirect URIs: `http://localhost:3000/auth/callback`
7. **Copy Client ID and Client Secret**

### 2. Configure Backend

**Option 1: Environment Variables (Recommended)**
```bash
export GOOGLE_CLIENT_ID="your-client-id-here"
export GOOGLE_CLIENT_SECRET="your-client-secret-here"
```

**Option 2: application.properties**
```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=profile,email
spring.security.oauth2.client.registration.google.redirect-uri={baseUrl}/login/oauth2/code/{registrationId}
```

### 3. Frontend Integration (React Example)

```bash
npm install @react-oauth/google jwt-decode
```

```jsx
import { GoogleOAuthProvider, GoogleLogin } from '@react-oauth/google';
import jwt_decode from 'jwt-decode';

function LoginPage() {
  const handleGoogleLogin = async (credentialResponse) => {
    const decoded = jwt_decode(credentialResponse.credential);
    
    const response = await fetch('http://localhost:8081/api/v1/auth/oauth2/google/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: decoded.email,
        firstName: decoded.given_name,
        lastName: decoded.family_name,
        oauthProviderId: decoded.sub,
        roleId: 2
      })
    });
    
    const data = await response.json();
    if (data.accessToken) {
      localStorage.setItem('accessToken', data.accessToken);
      // Navigate to dashboard
    }
  };

  return (
    <GoogleOAuthProvider clientId="YOUR_GOOGLE_CLIENT_ID">
      <GoogleLogin
        onSuccess={handleGoogleLogin}
        onError={() => console.log('Login Failed')}
      />
    </GoogleOAuthProvider>
  );
}
```

### 4. Test OAuth Integration

Use the provided test file:
```bash
# Update google-oauth-test.html with your Client ID
# Open in browser
open google-oauth-test.html
```

---

## ⚙️ Configuration

### application.properties

**Database Configuration:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_user
spring.datasource.username=root
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=validate
```

**JWT Configuration:**
```properties
jwt.secret=your-secret-key-here-make-it-long-and-secure
jwt.expiration=86400000                    # 24 hours
jwt.refresh-expiration=604800000           # 7 days
```

**Server Configuration:**
```properties
server.port=8081
server.servlet.context-path=/
```

**Actuator:**
```properties
management.endpoints.web.exposure.include=health
management.endpoint.health.show-details=always
```

**Flyway:**
```properties
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

---

## 🔒 Security

### JWT Token Security
- Tokens are stateless and cryptographically signed
- Access token expiry: 24 hours (configurable)
- Refresh token expiry: 7 days (configurable)
- Token revocation on logout
- Secure token storage in database

### Password Security
- BCrypt hashing with salt
- Minimum password length: 6 characters
- Password update requires current password verification
- OTP-based password reset

### OAuth2 Security
- Client secret stored securely (environment variables)
- OAuth tokens verified and validated
- Automatic account linking with existing emails
- Provider-specific user ID stored

### API Security
- Public endpoints: register, login, OTP, OAuth, forgot password
- Protected endpoints: require valid JWT token
- Role-based access control
- CORS configured for allowed origins
- Request validation with Jakarta Validation

### Database Security
- Foreign key constraints
- Indexed columns for performance
- Password never stored in plain text
- Audit trails (created_at, updated_by)

---

## 🧪 Testing

### Using Swagger UI
1. Open: http://localhost:8081/swagger-ui.html
2. Try out endpoints directly
3. View request/response schemas
4. Test authentication flow

### Using cURL

**Register:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Test",
    "lastName": "User",
    "email": "test@example.com",
    "mobileNumber": "9876543210",
    "roleId": 2,
    "password": "Test@123"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/authenticate \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test@example.com",
    "password": "Test@123",
    "source": "web"
  }'
```

**Send OTP:**
```bash
curl -X POST http://localhost:8081/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210"
  }'
```

For complete testing guide, see: **[LOCAL_TESTING_GUIDE.md](LOCAL_TESTING_GUIDE.md)**

---

## 📚 Documentation

### API Documentation
- **Swagger UI:** http://localhost:8081/swagger-ui.html
- **OpenAPI Spec:** http://localhost:8081/v3/api-docs

### Project Documentation
- **LOCAL_TESTING_GUIDE.md** - Complete testing instructions
- **database-schema.sql** - Complete database schema
- **google-oauth-test.html** - OAuth testing tool

---

## 📦 Default Roles

| Role ID | Role Name | Description |
|---------|-----------|-------------|
| 1 | ADMIN | Full system access |
| 2 | USER | Standard user access |
| 3 | DISTRIBUTOR | Business user |
| 4 | RETAILER | Business user |
| 5 | STOCKIST | Can create users |
| 6 | MARKETING_USER | Read-only access |

---

## 🚨 Troubleshooting

### Port Already in Use
```bash
# Find process on port 8081
lsof -ti:8081

# Kill process
kill -9 $(lsof -ti:8081)
```

### Database Connection Issues
- Verify MySQL is running
- Check credentials in application.properties
- Ensure database `auth_user` exists
- Check MySQL port (default: 3306)

### OAuth Not Working
- Verify Google Client ID and Secret
- Check redirect URIs match in Google Console
- Ensure frontend URL is in authorized origins
- Check browser console for CORS errors

### OTP Not Received
- OTP is currently logged in console (for development)
- Integrate SMS/WhatsApp service in production
- Check `OtpService.java` logs

### Token Expired
- Use refresh token endpoint
- Check token expiration settings
- Verify system time is correct

---

## 🔧 Project Structure

```
auth-service/
├── src/main/java/com/auth/service/
│   ├── config/              # Security, JWT, Swagger configs
│   ├── constants/           # Application constants
│   ├── controller/          # REST controllers
│   ├── dto/                 # Data Transfer Objects
│   ├── exception/           # Custom exceptions
│   ├── model/               # JPA entities
│   ├── repository/          # Data access layer
│   ├── service/             # Business logic
│   ├── serviceImpl/         # Service implementations
│   └── utils/               # Utility classes
├── src/main/resources/
│   ├── db/migration/        # Flyway migration files
│   └── application.properties
├── database-schema.sql      # Complete DB schema
├── LOCAL_TESTING_GUIDE.md   # Testing instructions
└── README.md                # This file
```

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 📞 Support

For issues and questions:
- Check [LOCAL_TESTING_GUIDE.md](LOCAL_TESTING_GUIDE.md)
- Review Swagger documentation
- Check application logs
- Verify configuration settings

---

## 🎯 Production Deployment

### Pre-deployment Checklist
- [ ] Change default admin password
- [ ] Use environment variables for secrets
- [ ] Enable HTTPS/SSL
- [ ] Configure production database
- [ ] Set up proper CORS origins
- [ ] Configure email/SMS for OTP
- [ ] Set up monitoring and logging
- [ ] Configure production OAuth credentials
- [ ] Review security settings
- [ ] Set up database backups

### Environment Variables
```bash
GOOGLE_CLIENT_ID=production-client-id
GOOGLE_CLIENT_SECRET=production-secret
JWT_SECRET=very-long-production-secret
DB_PASSWORD=production-db-password
```

---

**Built with ❤️ using Spring Boot**
