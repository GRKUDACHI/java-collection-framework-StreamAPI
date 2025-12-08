# 🚀 Spring Boot Security & JWT - Quick Implementation Guide

## 📋 Prerequisites Checklist

- [ ] Java 17 or higher
- [ ] Spring Boot 3.x
- [ ] Maven or Gradle
- [ ] Database (H2 for testing, PostgreSQL/MySQL for production)

---

## ⚡ Quick Start (5 Steps)

### Step 1: Add Dependencies to `pom.xml`

```xml
<dependencies>
    <!-- Spring Boot Starter Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- JPA & Database -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok (optional but recommended) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### Step 2: Configure `application.properties`

```properties
# Server
server.port=8080

# JWT Configuration
jwt.secret=MySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForSecurityPurposes
jwt.expiration=86400000

# Database (H2 for testing)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Step 3: Create Entity Classes

**User.java**
```java
package com.example.security.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String role = "USER";
    
    private Boolean enabled = true;
}
```

### Step 4: Create Repository

**UserRepository.java**
```java
package com.example.security.repository;

import com.example.security.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
```

### Step 5: Copy All Files from Complete Tutorial

Follow the tutorial to create:
- JwtTokenUtil.java
- CustomUserDetailsService.java
- JwtAuthenticationFilter.java
- SecurityConfig.java
- AuthController.java
- UserController.java
- AdminController.java
- DTOs (LoginRequest, RegisterRequest, LoginResponse)

---

## 🧪 Testing with Postman/Thunder Client

### 1. Register User
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

### 2. Login
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "message": "Login successful!"
}
```

### 3. Access Protected Endpoint
```
GET http://localhost:8080/api/user/profile
Authorization: Bearer <paste_token_here>
```

---

## 🎯 Common Issues & Solutions

### Issue 1: "Access Denied" even with valid token
**Solution**: Check if role name includes "ROLE_" prefix. Spring Security adds it automatically.

### Issue 2: Token expired
**Solution**: Increase `jwt.expiration` in application.properties or implement refresh tokens.

### Issue 3: CORS errors
**Solution**: Add CORS configuration in SecurityConfig.

### Issue 4: Password encoding mismatch
**Solution**: Ensure you're using the same PasswordEncoder for encoding and validation.

---

## 📝 Next Steps

1. ✅ Test all endpoints
2. ✅ Add refresh token mechanism
3. ✅ Implement password reset
4. ✅ Add email verification
5. ✅ Add logging and monitoring
6. ✅ Deploy to production with proper secrets management

---

**Ready to implement? Follow the complete tutorial for detailed code!**

