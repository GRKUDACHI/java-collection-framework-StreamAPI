# 🔐 Spring Boot Security & JWT - Complete Tutorial

## 🎯 Table of Contents
1. [Understanding Security Basics](#understanding-security-basics)
2. [Authentication vs Authorization](#authentication-vs-authorization)
3. [Spring Security Overview](#spring-security-overview)
4. [JWT Explained Simply](#jwt-explained-simply)
5. [Step-by-Step Implementation](#step-by-step-implementation)
6. [Complete Project Example](#complete-project-example)
7. [Common Patterns & Best Practices](#common-patterns--best-practices)

---

## 🏠 Understanding Security Basics

### What is Security in Web Applications?

Think of security like a **house with locks**:
- **Authentication** = "Who are you?" (Checking ID at the door)
- **Authorization** = "What can you do?" (Which rooms can you enter?)

### Real-World Analogy:
```
🏠 Your House (Application)
├── 🚪 Front Door (Login Page)
│   └── Need ID Card (Username/Password) → Authentication
├── 🛏️ Bedroom (Admin Panel)
│   └── Only family members → Authorization
└── 🚗 Garage (User Dashboard)
    └── Anyone with key → Authorization
```

---

## 🔑 Authentication vs Authorization

### Authentication (Who are you?)
**Purpose**: Verify the user's identity

**Examples**:
- Login with username/password
- Login with email/OTP
- Login with social media (Google, Facebook)

**Result**: "Yes, you are John Doe" ✅

### Authorization (What can you do?)
**Purpose**: Check what permissions the user has

**Examples**:
- Can John edit this post? (Yes/No)
- Can John delete users? (No, only admins)
- Can John view this file? (Yes)

**Result**: "You can view, but not edit" ✅

### Simple Code Example:
```java
// Authentication
if (username.equals("john") && password.equals("secret123")) {
    // ✅ User is authenticated (we know who they are)
}

// Authorization
if (user.getRole().equals("ADMIN")) {
    // ✅ User is authorized (they can do admin stuff)
    deleteUser(userId);
} else {
    // ❌ Not authorized
    throw new AccessDeniedException();
}
```

---

## 🛡️ Spring Security Overview

### What is Spring Security?

**Spring Security** = A framework that handles security automatically for you

**Without Spring Security**:
```java
// You have to manually check in every method
public void deleteUser(Long id) {
    if (currentUser == null) {
        throw new Exception("Not logged in!");
    }
    if (!currentUser.isAdmin()) {
        throw new Exception("Not authorized!");
    }
    // ... actual delete logic
}
```

**With Spring Security**:
```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) {
    // Spring Security automatically checks authentication & authorization
    // ... just write your business logic
}
```

### Key Components:

1. **SecurityFilterChain** = The security guard that checks every request
2. **AuthenticationManager** = The bouncer that verifies credentials
3. **UserDetailsService** = The database that knows all users
4. **JwtTokenProvider** = The ID card issuer

---

## 🎫 JWT Explained Simply

### What is JWT?

**JWT (JSON Web Token)** = A digital ID card that proves who you are

### Real-World Analogy:

**Traditional Session (Like a Club Wristband)**:
```
1. You show ID at door → Get wristband
2. Show wristband to enter → Server checks wristband database
3. Problem: Server must remember all wristbands (stored in memory/database)
```

**JWT (Like a Driver's License)**:
```
1. You show ID at door → Get a license (JWT token)
2. Show license to enter → Server just reads the license (no database check needed!)
3. License contains: Name, Expiry, Permissions (all in the token itself)
```

### JWT Structure:

A JWT has 3 parts separated by dots (`.`):

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**Breaking it down**:
```
[HEADER]        [PAYLOAD]              [SIGNATURE]
eyJhbGci...     eyJzdWIi...            SflKxwRJ...
     ↓                ↓                       ↓
  Algorithm      User Info              Verification
  (How to        (Who you are)          (Is it real?)
   verify)
```

### JWT Payload Example:
```json
{
  "sub": "john@example.com",      // Subject (user identifier)
  "name": "John Doe",              // User name
  "roles": ["USER", "ADMIN"],      // Permissions
  "iat": 1516239022,              // Issued at (timestamp)
  "exp": 1516242622               // Expires at (timestamp)
}
```

### Why Use JWT?

✅ **Stateless**: Server doesn't need to store sessions  
✅ **Scalable**: Works across multiple servers  
✅ **Portable**: Can be used in mobile apps, web apps, etc.  
✅ **Self-contained**: All info is in the token

---

## 🚀 Step-by-Step Implementation

### Step 1: Add Dependencies

Add to your `pom.xml`:

```xml
<dependencies>
    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- JWT Library -->
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
    
    <!-- For password encoding -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### Step 2: Create JWT Utility Class

**Purpose**: Create and validate JWT tokens

```java
package com.example.security.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    
    // Secret key for signing tokens (in production, use environment variable!)
    @Value("${jwt.secret:MySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLong}")
    private String secret;
    
    @Value("${jwt.expiration:86400000}") // 24 hours in milliseconds
    private Long expiration;
    
    // Get secret key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    // Generate token for user
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return createToken(claims, username);
    }
    
    // Create token with claims
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    // Extract username from token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    // Extract role from token
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
    
    // Extract expiration date
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    // Generic method to extract claim
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    // Get all claims from token
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    // Check if token is expired
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    // Validate token
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }
}
```

### Step 3: Create User Entity

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
    private String role = "USER"; // Default role: USER, ADMIN, etc.
    
    private Boolean enabled = true;
}
```

### Step 4: Create UserDetailsService

**Purpose**: Tells Spring Security how to load users from database

```java
package com.example.security.service;

import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Should be encoded!
                .roles(user.getRole())
                .disabled(!user.getEnabled())
                .build();
    }
}
```

### Step 5: Create JWT Authentication Filter

**Purpose**: Intercepts requests and validates JWT tokens

```java
package com.example.security.filter;

import com.example.security.service.CustomUserDetailsService;
import com.example.security.util.JwtTokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        // Get token from request header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;
        
        // Extract token from "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Remove "Bearer " prefix
            try {
                username = jwtTokenUtil.getUsernameFromToken(token);
            } catch (Exception e) {
                logger.error("JWT Token parsing error: " + e.getMessage());
            }
        }
        
        // If token is valid and user is not already authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            // Validate token
            if (jwtTokenUtil.validateToken(token, username)) {
                // Set authentication in Spring Security context
                UsernamePasswordAuthenticationToken authToken = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        
        // Continue filter chain
        filterChain.doFilter(request, response);
    }
}
```

### Step 6: Create Security Configuration

**Purpose**: Configure Spring Security rules

```java
package com.example.security.config;

import com.example.security.filter.JwtAuthenticationFilter;
import com.example.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    // Password encoder (BCrypt - one-way hashing)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    // Authentication provider
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
    
    // Authentication manager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
    // Security filter chain - Main security configuration
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless JWT (or enable if using cookies)
            .csrf(csrf -> csrf.disable())
            
            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints (no authentication required)
                .requestMatchers("/api/auth/**", "/api/public/**").permitAll()
                
                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                
                // User endpoints (authenticated users)
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            
            // Use stateless session (JWT doesn't need server-side sessions)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            
            // Set authentication provider
            .authenticationProvider(authenticationProvider())
            
            // Add JWT filter before username/password filter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

### Step 7: Create Authentication Controller

```java
package com.example.security.controller;

import com.example.security.dto.LoginRequest;
import com.example.security.dto.LoginResponse;
import com.example.security.dto.RegisterRequest;
import com.example.security.entity.User;
import com.example.security.repository.UserRepository;
import com.example.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    
    // Register new user
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Check if user already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest()
                    .body("Username already exists!");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Encode password!
        user.setRole("USER"); // Default role
        
        userRepository.save(user);
        
        return ResponseEntity.ok("User registered successfully!");
    }
    
    // Login and get JWT token
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getUsername(), 
                    request.getPassword()
                )
            );
            
            // Get user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Generate JWT token
            String token = jwtTokenUtil.generateToken(
                userDetails.getUsername(), 
                userDetails.getAuthorities().iterator().next().getAuthority()
            );
            
            // Return token
            return ResponseEntity.ok(new LoginResponse(token, "Login successful!"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Invalid username or password!");
        }
    }
}
```

### Step 8: Create DTOs (Data Transfer Objects)

```java
// LoginRequest.java
package com.example.security.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}

// RegisterRequest.java
package com.example.security.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}

// LoginResponse.java
package com.example.security.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String message;
}
```

### Step 9: Create Protected Controller

```java
package com.example.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    // Any authenticated user can access
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        return ResponseEntity.ok("Hello " + authentication.getName() + "! This is your profile.");
    }
    
    // Only users with USER or ADMIN role
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> getDashboard() {
        return ResponseEntity.ok("Welcome to Dashboard!");
    }
}

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    // Only ADMIN role can access
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok("List of all users (Admin only)");
    }
    
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return ResponseEntity.ok("User " + id + " deleted (Admin only)");
    }
}
```

---

## 📦 Complete Project Example

### Project Structure:
```
src/main/java/com/example/security/
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── UserController.java
│   └── AdminController.java
├── dto/
│   ├── LoginRequest.java
│   ├── RegisterRequest.java
│   └── LoginResponse.java
├── entity/
│   └── User.java
├── filter/
│   └── JwtAuthenticationFilter.java
├── repository/
│   └── UserRepository.java
├── service/
│   └── CustomUserDetailsService.java
└── util/
    └── JwtTokenUtil.java
```

### application.properties:
```properties
# JWT Configuration
jwt.secret=MySecretKeyForJWTTokenGenerationThatIsAtLeast256BitsLongForSecurity
jwt.expiration=86400000

# Database (example with H2)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

---

## 🧪 Testing Your Implementation

### 1. Register a User:
```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "username": "john",
  "email": "john@example.com",
  "password": "password123"
}
```

### 2. Login and Get Token:
```bash
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

### 3. Access Protected Endpoint:
```bash
GET http://localhost:8080/api/user/profile
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 4. Access Admin Endpoint (will fail for regular user):
```bash
GET http://localhost:8080/api/admin/users
Authorization: Bearer <token>
```

---

## 🎯 Common Patterns & Best Practices

### 1. Password Encoding
**Always encode passwords! Never store plain text.**

```java
@Autowired
private PasswordEncoder passwordEncoder;

// When saving user
user.setPassword(passwordEncoder.encode(plainPassword));

// When checking password (Spring Security does this automatically)
// But if you need to check manually:
boolean matches = passwordEncoder.matches(plainPassword, encodedPassword);
```

### 2. Role-Based Access Control

```java
// Method-level security
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public void viewProfile() { }

@PreAuthorize("hasAuthority('READ_PRIVILEGE')")
public void readData() { }
```

### 3. Custom Security Expressions

```java
@PreAuthorize("hasRole('USER') and #userId == authentication.principal.id")
public void updateOwnProfile(Long userId) {
    // User can only update their own profile
}
```

### 4. Exception Handling

```java
@RestControllerAdvice
public class SecurityExceptionHandler {
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(403)
                .body("Access Denied: " + e.getMessage());
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException e) {
        return ResponseEntity.status(401)
                .body("Authentication Failed: " + e.getMessage());
    }
}
```

### 5. Refresh Token Pattern (Advanced)

For better security, use short-lived access tokens + long-lived refresh tokens:

```java
// Generate access token (15 minutes)
String accessToken = jwtTokenUtil.generateToken(username, role, 900000);

// Generate refresh token (7 days)
String refreshToken = jwtTokenUtil.generateToken(username, role, 604800000);

// Store refresh token in database
// When access token expires, use refresh token to get new access token
```

### 6. CORS Configuration

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

---

## 🚨 Security Best Practices

### ✅ DO:
1. **Always encode passwords** (use BCrypt)
2. **Use HTTPS** in production
3. **Store JWT secret** in environment variables
4. **Set token expiration** (15-30 minutes for access tokens)
5. **Validate tokens** on every request
6. **Use strong secrets** (at least 256 bits)
7. **Implement refresh tokens** for better security
8. **Log security events** (failed logins, etc.)

### ❌ DON'T:
1. **Never store passwords in plain text**
2. **Don't put sensitive data in JWT** (tokens can be decoded)
3. **Don't use weak secrets** (like "secret123")
4. **Don't ignore token expiration**
5. **Don't expose user details in error messages**
6. **Don't allow unlimited login attempts** (implement rate limiting)

---

## 📚 Key Takeaways

1. **Authentication** = "Who are you?" → Login process
2. **Authorization** = "What can you do?" → Permission checking
3. **JWT** = Digital ID card that contains user info
4. **Spring Security** = Framework that handles security automatically
5. **Filter Chain** = Security checks that run before your controllers
6. **Password Encoding** = One-way hashing (BCrypt) - never store plain text!

---

## 🔄 Flow Diagram

```
1. User Registration
   User → POST /api/auth/register → Save to DB (password encoded)

2. User Login
   User → POST /api/auth/login → 
   Spring Security validates credentials →
   Generate JWT token →
   Return token to user

3. Accessing Protected Resource
   User → GET /api/user/profile + Bearer Token →
   JwtAuthenticationFilter extracts token →
   Validates token →
   Sets authentication in SecurityContext →
   Controller executes →
   Returns response

4. Authorization Check
   User → GET /api/admin/users + Bearer Token →
   JwtAuthenticationFilter validates token →
   @PreAuthorize checks role →
   If ADMIN: Allow access
   If not ADMIN: Return 403 Forbidden
```

---

## 💡 Real-World Implementation Tips

### 1. Environment Variables
```properties
# application.properties
jwt.secret=${JWT_SECRET:defaultSecretKey}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

### 2. User Repository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
}
```

### 3. Response Wrapper
```java
@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }
    
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
```

---

## 🎓 Summary

You now understand:
- ✅ What authentication and authorization are
- ✅ How Spring Security works
- ✅ What JWT is and why we use it
- ✅ How to implement JWT authentication
- ✅ How to protect endpoints
- ✅ Best practices for security

**Next Steps:**
1. Implement this in your project
2. Test with Postman/Thunder Client
3. Add refresh token mechanism
4. Implement password reset functionality
5. Add email verification

**Remember**: Security is an ongoing process. Always keep your dependencies updated and follow security best practices!

---

## 📖 Additional Resources

- Spring Security Documentation: https://spring.io/projects/spring-security
- JWT.io: https://jwt.io (Decode and test JWT tokens)
- OWASP Top 10: https://owasp.org/www-project-top-ten/

---

**Happy Coding! 🚀**

