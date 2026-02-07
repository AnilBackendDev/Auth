# OTP Insertion Troubleshooting Guide

## Issue
OTP is not being inserted into the `otp` table in the database.

---

## ✅ Code Analysis - Everything Looks Correct

### 1. **OTP Model (Otp.java)**
```java
@Entity
@Table(name = "otp")
@Data
@Builder
public class Otp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name = "mobile_number")
    private String mobileNumber;
    
    @Column(name = "otp_value", nullable = false)
    private String otpValue;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;
    
    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```
✅ Model is correct with @PrePersist for auto-setting createdAt

---

### 2. **OTP Repository (OtpRepository.java)**
```java
public interface OtpRepository extends JpaRepository<Otp, Integer> {
    @Query("SELECT o FROM Otp o WHERE o.mobileNumber = :mobileNumber 
            AND o.isVerified = false ORDER BY o.createdAt DESC LIMIT 1")
    Optional<Otp> findLatestByMobileNumber(@Param("mobileNumber") String mobileNumber);
}
```
✅ Repository extends JpaRepository correctly

---

### 3. **OTP Service (OtpService.java)**
```java
@Transactional
public ApiResponse sendOtp(OtpRequestDto request) {
    String mobileNumber = request.getMobileNumber();
    String otpValue = generateOtp();
    
    Otp otp = Otp.builder()
            .mobileNumber(mobileNumber)
            .otpValue(otpValue)
            .expirationTime(LocalDateTime.now().plusMinutes(5))
            .isVerified(false)
            .build();
    
    log.debug("Saving OTP for mobile: {}, value: {}", mobileNumber, otpValue);
    Otp savedOtp = otpRepository.save(otp);
    log.info("OTP saved to database with ID: {}", savedOtp.getId());
    
    return new ApiResponse("OTP sent successfully");
}
```
✅ Service has @Transactional and saves correctly
✅ Added enhanced logging to verify save operation

---

## 🔍 How to Verify OTP Insertion

### Step 1: Test Send OTP Endpoint

```bash
curl -X POST http://localhost:8081/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210"
  }'
```

### Step 2: Check Application Logs

Look for these log messages:
```
INFO  OtpService : Sending OTP to mobile: 9876543210
DEBUG OtpService : Saving OTP for mobile: 9876543210, value: 123456
INFO  OtpService : OTP saved to database with ID: 1
INFO  OtpService : OTP generated for 9876543210: 123456 (remove this log in production)
```

**If you see "OTP saved to database with ID: X"** → OTP WAS saved successfully!

### Step 3: Query Database IMMEDIATELY

```sql
-- Check if OTP exists
SELECT * FROM otp ORDER BY created_at DESC LIMIT 5;

-- Check specific mobile number
SELECT * FROM otp WHERE mobile_number = '9876543210';

-- Count all OTPs
SELECT COUNT(*) FROM otp;
```

---

## 🐛 Possible Causes if OTP is Not Inserted

### 1. **Transaction Rollback**
**Symptom:** Logs show "OTP saved" but database is empty

**Cause:** Exception thrown after save but before transaction commit

**Fix:** Check logs for exceptions after OTP save
```bash
# Check for errors
grep -i "error\|exception" logs/spring.log
```

---

### 2. **Database Connection Issue**
**Symptom:** No database errors but OTP not inserted

**Check:**
```sql
-- Verify table exists
SHOW TABLES LIKE 'otp';

-- Check table structure
DESC otp;

-- Check permissions
SHOW GRANTS FOR CURRENT_USER();
```

---

### 3. **Flyway Migration Not Run**
**Symptom:** Table doesn't exist or has wrong structure

**Fix:**
```sql
-- Check Flyway history
SELECT * FROM flyway_schema_history;

-- If OTP table is missing, create it manually
CREATE TABLE IF NOT EXISTS otp (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    mobile_number VARCHAR(15),
    otp_value VARCHAR(10) NOT NULL,
    created_at DATETIME,
    expiration_time DATETIME NOT NULL,
    is_verified BOOLEAN DEFAULT FALSE,
    INDEX idx_mobile (mobile_number),
    INDEX idx_user_id (user_id)
);
```

---

### 4. **Transaction Not Committing**
**Symptom:** OTP shows in logs but not in database

**Possible causes:**
- Missing `@Transactional` annotation
- Transaction timeout
- Database auto-commit disabled

**Fix in application.properties:**
```properties
# Ensure auto-commit is enabled
spring.datasource.hikari.auto-commit=true

# Check transaction timeout
spring.transaction.default-timeout=30
```

---

### 5. **Wrong Database Connection**
**Symptom:** Application runs but OTP not in expected database

**Check application.properties:**
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/auth_user
spring.datasource.username=root
spring.datasource.password=rootAdmin
```

**Verify you're querying the correct database:**
```sql
SELECT DATABASE();
```

---

## 📊 Enhanced Logging Added

I've added detailed logging to help debug:

```java
// Before save
log.debug("Saving OTP for mobile: {}, value: {}", mobileNumber, otpValue);

// After save
Otp savedOtp = otpRepository.save(otp);
log.info("OTP saved to database with ID: {}", savedOtp.getId());
```

**What to look for:**
1. If ID is null → OTP not saved
2. If ID has value → OTP WAS saved to database

---

## 🧪 Complete Test Flow

### 1. Send OTP
```bash
curl -X POST http://localhost:8081/api/v1/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"mobileNumber": "9876543210"}'
```

### 2. Check Logs Immediately
```
tail -f logs/spring.log | grep -i otp
```

### 3. Query Database Immediately
```sql
SELECT id, mobile_number, otp_value, created_at, expiration_time, is_verified 
FROM otp 
WHERE mobile_NUMBER = '9876543210'
ORDER BY created_at DESC;
```

### 4. Verify OTP
```bash
# Use the OTP from step 2 logs
curl -X POST http://localhost:8081/api/v1/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobileNumber": "9876543210",
    "otp": "123456"
  }'
```

---

## ✅ Expected Behavior

### Successful OTP Flow:

1. **Send OTP:** Returns "OTP sent successfully to ****3210"
2. **Logs show:** 
   - "OTP saved to database with ID: 1"
   - "OTP generated for 9876543210: 123456"
3. **Database query:** Shows 1 row with the OTP
4. **Verify OTP:** Returns "OTP verified successfully"
5. **Database update:** is_verified changes to TRUE

---

## 🔧 If OTP Still Not Inserting

### Enable SQL Logging
In `application.properties`:
```properties
# Show all SQL statements
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

This will show the EXACT INSERT statement being executed.

### Check for Constraints/Triggers
```sql
-- Check for any triggers on the otp table
SHOW TRIGGERS LIKE 'otp';

-- Check constraints
SELECT * FROM information_schema.TABLE_CONSTRAINTS 
WHERE TABLE_NAME = 'otp';
```

---

## 📞 Next Steps

1. **Test Send OTP** via Swagger or cURL
2. **Check application logs** for the new debug messages
3. **Query database** immediately after sending OTP
4. **Share logs** if issue persists:
   - Application logs (OTP service logs)
   - SQL logs (if enabled)
   - Database query results

---

**The code is correct. If OTP is still not inserting, it's likely a:**
- Database permission issue
- Transaction configuration issue  
- Connection pooling issue
- Or you're querying a different database than the app is using

Check the logs with the new enhanced logging to pinpoint the exact issue!
