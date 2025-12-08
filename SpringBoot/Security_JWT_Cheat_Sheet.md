# 🔐 Spring Boot Security & JWT - Quick Reference Cheat Sheet

## 🎯 Core Concepts (One-Liners)

| Concept | Simple Explanation |
|---------|-------------------|
| **Authentication** | "Who are you?" - Verifying user identity (login) |
| **Authorization** | "What can you do?" - Checking permissions |
| **JWT** | Digital ID card that contains user info (stateless) |
| **Filter Chain** | Security checks that run before your controllers |
| **Password Encoding** | One-way hashing (BCrypt) - never store plain text! |

---

## 🔑 Key Annotations

```java
@EnableWebSecurity          // Enable Spring Security
@EnableMethodSecurity        // Enable method-level security
@PreAuthorize("hasRole('ADMIN')")  // Check role before method execution
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // Multiple roles
```

---

## 📦 Essential Dependencies

```xml
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
```

---

## ⚙️ Security Configuration Pattern

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
            .csrf(csrf -> csrf.disable())  // Disable for JWT
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()  // Public
                .requestMatchers("/api/admin/**").hasRole("ADMIN")  // Admin only
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")  // Users
                .anyRequest().authenticated()  // All others need auth
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // JWT is stateless
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## 🎫 JWT Token Flow

```
1. User Login
   → POST /api/auth/login
   → Spring Security validates credentials
   → Generate JWT token
   → Return token

2. Access Protected Resource
   → GET /api/user/profile
   → Header: Authorization: Bearer <token>
   → JwtAuthenticationFilter extracts token
   → Validates token
   → Sets authentication in SecurityContext
   → Controller executes
```

---

## 🔐 Password Encoding

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Encode password (when saving)
user.setPassword(passwordEncoder.encode(plainPassword));

// Check password (Spring Security does this automatically)
// Manual check:
passwordEncoder.matches(plainPassword, encodedPassword);
```

---

## 🛡️ Method-Level Security

```java
// Only ADMIN can access
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }

// USER or ADMIN can access
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public void viewProfile() { }

// Custom expression
@PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
public void updateOwnProfile(Long userId) { }
```

---

## 📡 API Endpoints Pattern

### Public Endpoints (No Auth)
```java
@PostMapping("/api/auth/register")
@PostMapping("/api/auth/login")
```

### User Endpoints (Authenticated)
```java
@GetMapping("/api/user/profile")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
```

### Admin Endpoints (Admin Only)
```java
@GetMapping("/api/admin/users")
@PreAuthorize("hasRole('ADMIN')")
```

---

## 🧪 Testing with cURL/Postman

### Register
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

### Login
```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

### Access Protected
```bash
GET http://localhost:8080/api/user/profile
Authorization: Bearer <token>
```

---

## 🔧 JWT Token Utility Methods

```java
// Generate token
String token = jwtTokenUtil.generateToken(username, role);

// Extract username
String username = jwtTokenUtil.getUsernameFromToken(token);

// Extract role
String role = jwtTokenUtil.getRoleFromToken(token);

// Validate token
Boolean isValid = jwtTokenUtil.validateToken(token, username);
```

---

## 🚨 Common Issues & Quick Fixes

| Issue | Solution |
|-------|----------|
| 401 Unauthorized | Check if token is in header: `Authorization: Bearer <token>` |
| 403 Forbidden | User doesn't have required role |
| Token expired | Increase `jwt.expiration` or implement refresh tokens |
| Password mismatch | Ensure using same PasswordEncoder for encoding/validation |
| CORS errors | Add CORS configuration in SecurityConfig |

---

## ✅ Security Checklist

- [ ] Passwords are encoded (BCrypt)
- [ ] JWT secret is in environment variable (not hardcoded)
- [ ] Token expiration is set (15-30 min for access tokens)
- [ ] HTTPS is used in production
- [ ] Roles are properly configured
- [ ] Public endpoints are marked with `permitAll()`
- [ ] Protected endpoints have proper authorization
- [ ] Error messages don't expose sensitive info

---

## 📊 Request Flow Diagram

```
Client Request
    ↓
SecurityFilterChain
    ↓
JwtAuthenticationFilter (extracts & validates token)
    ↓
SecurityContext (sets authentication)
    ↓
Controller Method
    ↓
@PreAuthorize (checks authorization)
    ↓
Method Execution
    ↓
Response
```

---

## 🎓 Key Files & Their Purpose

| File | Purpose |
|------|---------|
| `SecurityConfig.java` | Main security configuration |
| `JwtTokenUtil.java` | Create & validate JWT tokens |
| `JwtAuthenticationFilter.java` | Intercept requests & validate tokens |
| `CustomUserDetailsService.java` | Load users from database |
| `AuthController.java` | Handle login/register |
| `UserController.java` | Protected user endpoints |
| `AdminController.java` | Protected admin endpoints |

---

## 💡 Pro Tips

1. **Always encode passwords** - Never store plain text
2. **Use environment variables** for JWT secret in production
3. **Set short expiration** for access tokens (15-30 min)
4. **Implement refresh tokens** for better security
5. **Log security events** (failed logins, access denials)
6. **Use HTTPS** in production
7. **Validate input** on all endpoints
8. **Don't expose sensitive data** in error messages

---

## 🔄 Quick Reference: Complete Flow

```
Registration:
User → POST /register → Encode password → Save to DB

Login:
User → POST /login → Validate credentials → Generate JWT → Return token

Access Resource:
User → GET /api/user/profile + Bearer token → 
Filter validates token → Set authentication → 
Check authorization → Execute method → Return response
```

---

## 📚 File Structure Quick View

```
src/main/java/com/example/security/
├── config/
│   └── SecurityConfig.java          ← Security rules
├── controller/
│   ├── AuthController.java          ← Login/Register
│   ├── UserController.java          ← User endpoints
│   └── AdminController.java         ← Admin endpoints
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── LoginResponse.java
├── entity/
│   └── User.java                    ← User model
├── filter/
│   └── JwtAuthenticationFilter.java ← Token validation
├── repository/
│   └── UserRepository.java          ← Database access
├── service/
│   └── CustomUserDetailsService.java ← Load users
└── util/
    └── JwtTokenUtil.java            ← JWT operations
```

---

**Keep this cheat sheet handy while implementing! 🚀**

