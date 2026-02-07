# 📚 Documentation Index

## Main Documentation Files

| File | Description | Size |
|------|-------------|------|
| **[README.md](README.md)** | ⭐ Complete project documentation | 15 KB |
| **[LOCAL_TESTING_GUIDE.md](LOCAL_TESTING_GUIDE.md)** | 🧪 Step-by-step testing guide | 15 KB |
| **[database-schema.sql](database-schema.sql)** | 🗄️ Complete database schema | 8 KB |

---

## Quick Links

### Getting Started
- [Quick Start Guide](README.md#-quick-start) - Get up and running in 5 minutes
- [Database Setup](README.md#️-database-setup) - Set up your database
- [Configuration](README.md#️-configuration) - Configure the application

### Features
- [Core Features](README.md#-features) - What this service can do
- [API Endpoints](README.md#-api-endpoints) - All available endpoints
- [Authentication Methods](README.md#-authentication-methods) - Different ways to authenticate

### Google OAuth
- [Google OAuth Setup](README.md#-google-oauth-setup) - Complete OAuth integration guide
- [Frontend Integration](README.md#3-frontend-integration-react-example) - React example

### Testing
- [Testing Guide](LOCAL_TESTING_GUIDE.md) - Complete testing scenarios
- [Using Swagger UI](LOCAL_TESTING_GUIDE.md#1-swagger-ui-recommended) - Visual API testing
- [Common Issues](LOCAL_TESTING_GUIDE.md#-common-issues) - Troubleshooting

### Security
- [Security Features](README.md#-security) - Security implementation details
- [Production Deployment](README.md#-production-deployment) - Production checklist

---

## What's in Each File?

### README.md
The main documentation covering:
- ✅ Project overview and features
- ✅ Tech stack
- ✅ Installation and setup
- ✅ Database configuration
- ✅ All API endpoints with examples
- ✅ Google OAuth complete setup
- ✅ Security implementation
- ✅ Configuration guide
- ✅ Troubleshooting
- ✅ Production deployment

### LOCAL_TESTING_GUIDE.md
Detailed testing instructions:
- ✅ Prerequisites and setup
- ✅ Test scenarios for all features
- ✅ Example requests and responses
- ✅ Testing tools (Swagger, Postman, cURL)
- ✅ OAuth testing with HTML tool
- ✅ Common issues and solutions
- ✅ Testing checklist

### database-schema.sql
Single comprehensive SQL file:
- ✅ All table definitions
- ✅ Indexes and constraints
- ✅ Default roles and permissions
- ✅ Default admin user
- ✅ OAuth support columns
- ✅ OTP support columns

---

## Additional Files

### Testing Tools
- **google-oauth-test.html** - Browser-based OAuth testing tool
  - Update Client ID on line ~75
  - Open in browser to test Google Sign-In
  - View tokens and user data

### Configuration Files
- **application.properties** - Main configuration
  - Database settings
  - JWT settings
  - OAuth credentials
  - Server settings

### Migration Files
Located in `src/main/resources/db/migration/`:
- **V1__create_auth_schema.sql** - Initial schema
- **V2__insert_seed_data.sql** - Seed data
- Auto-runs on application startup

---

## Quick Reference

### Default Credentials
- **Email:** admin@auth-service.com
- **Password:** Admin@123
- **Mobile:** 9999999999
- ⚠️ Change in production!

### URLs
- **API:** http://localhost:8081
- **Swagger:** http://localhost:8081/swagger-ui.html
- **Health:** http://localhost:8081/actuator/health

### Database
- **Name:** auth_user
- **Tables:** user, role, permissions, role_permission, token, otp
- **Default Roles:** ADMIN, USER, DISTRIBUTOR, RETAILER, STOCKIST, MARKETING_USER

---

## Need Help?

1. **Start here:** [README.md](README.md)
2. **For testing:** [LOCAL_TESTING_GUIDE.md](LOCAL_TESTING_GUIDE.md)
3. **Check logs:** Console output for errors
4. **Swagger UI:** Interactive API documentation
5. **Database:** Verify table structure and data

---

**Everything you need is in these 3 files! 🎯**
