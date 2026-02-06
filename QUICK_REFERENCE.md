# 🚀 Auth Service - Quick Reference Card

## 📍 **Locations**
```
Main Repo:  /Users/credr/Documents/Og/auth-service-backend
Auth Service: /Users/credr/Documents/Og/auth-service
```

## ⚡ **Quick Commands**

### Start Service
```bash
cd /Users/credr/Documents/Og/auth-service
./mvnw spring-boot:run
```

### Build
```bash
./mvnw clean install -DskipTests
```

### Create Local Database
```bash
mysql -u root -p
CREATE DATABASE auth_service_local;
EXIT;
```

## 🔗 **URLs**
```
Swagger UI:  http://localhost:8081/swagger-ui.html
API Docs:    http://localhost:8081/v3/api-docs
Health:      http://localhost:8081/actuator/health
Flyway:      http://localhost:8081/actuator/flyway
```

## 🔑 **Default Credentials**
```
Email:    admin@auth-service.com
Password: Admin@123
Role:     ADMIN
```

## 🧪 **Test Login (cURL)**
```bash
curl -X POST "http://localhost:8081/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@auth-service.com","password":"Admin@123"}'
```

## 📖 **Documentation Files**
```
📘 LOCAL_TESTING_GUIDE.md          ← START HERE!
📗 SWAGGER_AND_DB_COMPLETE.md      ← Latest updates
📙 SEPARATION_COMPLETE.md          ← Architecture
📕 AUTH_SERVICE_QUICKSTART.md      ← Quick ref
```

## 🗄️ **Database**
```
Tables: user, role, permissions, role_permission, token, otp
Roles: ADMIN, DISTRIBUTOR, RETAILER, STOCKIST, MARKETING_USER
Permissions: 21 total
Migration Scripts: V1 (schema), V2 (seed data)
```

## 🔧 **Configuration**
```properties
# src/main/resources/application.properties
server.port=8081
spring.datasource.url=jdbc:mysql://localhost:3306/auth_service_local
spring.flyway.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html
```

## ✅ **Checklist**
- [ ] MySQL running locally
- [ ] Database created: `auth_service_local`
- [ ] Updated `application.properties` with DB password
- [ ] Built project: `./mvnw clean install`
- [ ] Started service: `./mvnw spring-boot:run`
- [ ] Swagger UI accessible
- [ ] Login tested with admin user
- [ ] JWT token received

## 🐛 **Troubleshooting**
```bash
# Port in use
lsof -i :8081
kill -9 <PID>

# Database connection
mysql -u root -p
SHOW DATABASES;

# Flyway issues
DROP DATABASE auth_service_local;
CREATE DATABASE auth_service_local;
```

## 📊 **API Endpoints Summary**
```
Auth:     POST /api/auth/login, /register, /logout
Password: POST /api/auth/change-password, /forgot-password
Roles:    GET/POST/PUT/DELETE /api/roles
Health:   GET /actuator/health
```

---
**Status:** ✅ Ready to Test  
**Version:** 1.0.0  
**Date:** Feb 6, 2026
