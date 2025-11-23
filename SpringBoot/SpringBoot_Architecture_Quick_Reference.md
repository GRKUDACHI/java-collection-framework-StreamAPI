# 🚀 Spring Boot Architecture - Quick Reference

## 📋 Core Architecture Layers

```
┌─────────────────────────────────────┐
│   Controller Layer (@RestController)│  ← HTTP Requests
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│   Service Layer (@Service)          │  ← Business Logic
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│   Repository Layer (@Repository)    │  ← Data Access
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│   Database / External Systems       │
└─────────────────────────────────────┘
```

## 🔄 Application Startup Flow

```
1. main() → SpringApplication.run()
2. Create ApplicationContext
3. Component Scanning (@ComponentScan)
4. Auto-Configuration (@EnableAutoConfiguration)
5. Bean Creation & Dependency Injection
6. @PostConstruct methods
7. Start Embedded Server
8. Application Ready
```

## 🌐 HTTP Request Flow

```
Client Request
    ↓
Embedded Server (Tomcat)
    ↓
DispatcherServlet
    ↓
HandlerMapping (finds controller method)
    ↓
HandlerInterceptor.preHandle()
    ↓
Controller Method (@GetMapping, @PostMapping)
    ↓
Service Layer (@Service)
    ↓
Repository Layer (@Repository)
    ↓
Database
    ↓
Response (JSON/XML) → Client
```

## 🎯 Key Annotations

| Annotation | Purpose | Layer |
|------------|---------|-------|
| `@SpringBootApplication` | Main application class | Application |
| `@RestController` | REST API controller | Controller |
| `@Controller` | Web controller | Controller |
| `@Service` | Business logic | Service |
| `@Repository` | Data access | Repository |
| `@Component` | Generic component | Any |
| `@Autowired` | Dependency injection | Any |
| `@Configuration` | Configuration class | Config |
| `@Bean` | Bean definition method | Config |

## 🔧 Core Components

### 1. SpringApplication
```java
SpringApplication.run(MyApp.class, args);
```
- Bootstraps application
- Creates ApplicationContext
- Starts embedded server

### 2. ApplicationContext
- IOC Container
- Manages beans
- Handles dependency injection

### 3. DispatcherServlet
- Central servlet
- Routes HTTP requests
- Handles responses

### 4. Auto-Configuration
- Automatically configures based on classpath
- Checks `META-INF/spring.factories`
- Uses `@ConditionalOn*` annotations

## 📦 Starter Dependencies

| Starter | Includes |
|---------|----------|
| `spring-boot-starter-web` | Spring MVC, Tomcat, Jackson |
| `spring-boot-starter-data-jpa` | Hibernate, Spring Data JPA |
| `spring-boot-starter-security` | Spring Security |
| `spring-boot-starter-test` | JUnit, Mockito, AssertJ |

## 🔄 Bean Lifecycle (Simplified)

```
1. Constructor
2. Dependency Injection (@Autowired)
3. BeanNameAware.setBeanName()
4. ApplicationContextAware.setApplicationContext()
5. @PostConstruct
6. InitializingBean.afterPropertiesSet()
7. Bean Ready
8. @PreDestroy (on shutdown)
```

## 🎯 Dependency Injection Types

### Constructor Injection (Preferred)
```java
@Service
public class UserService {
    private UserRepository repo;
    
    public UserService(UserRepository repo) {
        this.repo = repo;
    }
}
```

### Field Injection
```java
@Service
public class UserService {
    @Autowired
    private UserRepository repo;
}
```

### Setter Injection
```java
@Service
public class UserService {
    private UserRepository repo;
    
    @Autowired
    public void setRepo(UserRepository repo) {
        this.repo = repo;
    }
}
```

## 🏗️ Application Structure

```
src/main/java/
└── com/example/
    ├── MyApplication.java          (@SpringBootApplication)
    ├── controller/
    │   └── UserController.java     (@RestController)
    ├── service/
    │   └── UserService.java        (@Service)
    ├── repository/
    │   └── UserRepository.java     (@Repository)
    └── model/
        └── User.java               (Entity/POJO)
```

## ⚙️ Auto-Configuration Conditions

| Condition | Meaning |
|-----------|---------|
| `@ConditionalOnClass` | If class exists on classpath |
| `@ConditionalOnBean` | If bean exists |
| `@ConditionalOnProperty` | If property is set |
| `@ConditionalOnWebApplication` | If it's a web app |
| `@ConditionalOnMissingBean` | If bean doesn't exist |

## 🔍 Component Scanning

```java
@SpringBootApplication
// Scans current package and sub-packages
// Finds: @Component, @Service, @Repository, @Controller
```

**Scans for:**
- `@Component`
- `@Service`
- `@Repository`
- `@Controller`
- `@RestController`
- `@Configuration`

## 📝 Request Mapping

```java
@GetMapping("/users")           // GET /users
@PostMapping("/users")          // POST /users
@PutMapping("/users/{id}")      // PUT /users/123
@DeleteMapping("/users/{id}")   // DELETE /users/123
```

## 🔗 Parameter Binding

```java
@GetMapping("/users/{id}")
public User getUser(
    @PathVariable Long id,                    // From URL
    @RequestParam String name,                // From query ?name=John
    @RequestHeader("Authorization") String auth, // From header
    @RequestBody User user                    // From body (POST/PUT)
) { }
```

## 🎯 Best Practices

1. ✅ Use constructor injection
2. ✅ Keep controllers thin
3. ✅ Put business logic in services
4. ✅ Use @Transactional on services
5. ✅ Follow package structure
6. ✅ Leverage auto-configuration
7. ✅ Use profiles for environments
8. ❌ Don't use field injection (if possible)
9. ❌ Don't put business logic in controllers
10. ❌ Don't configure what Spring Boot auto-configures

## 📊 Request Processing Steps

```
1. Request arrives at embedded server
2. DispatcherServlet receives
3. HandlerMapping finds controller method
4. HandlerInterceptor.preHandle()
5. Parameter binding (@PathVariable, @RequestParam)
6. Controller method executes
7. Service layer called
8. Repository layer called
9. Database query executed
10. Response converted to JSON/XML
11. HandlerInterceptor.postHandle()
12. Response sent to client
```

## 🚀 Quick Start Template

```java
@SpringBootApplication
public class MyApp {
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
}

@RestController
class MyController {
    @Autowired
    private MyService service;
    
    @GetMapping("/hello")
    public String hello() {
        return service.getMessage();
    }
}

@Service
class MyService {
    public String getMessage() {
        return "Hello Spring Boot!";
    }
}
```

## 🔑 Key Concepts

- **IOC (Inversion of Control)**: Spring manages object creation
- **Dependency Injection**: Automatic wiring of dependencies
- **Auto-Configuration**: Automatic setup based on classpath
- **Convention over Configuration**: Sensible defaults
- **Component Scanning**: Automatic discovery of components
- **Embedded Server**: Built-in web server

## 📚 Related Files

- `SpringBoot_Architecture_Complete_Tutorial.md` - Full detailed tutorial
- `IOC_Container_Tutorial_README.md` - IOC Container details
- `Annotations_Complete_Tutorial.md` - All annotations explained

---

**Quick Tip**: Spring Boot's power comes from auto-configuration. Add a starter dependency, and Spring Boot automatically configures everything for you! 🎯

