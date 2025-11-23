# 📝 Java & Spring Boot Annotations - Complete Tutorial

## 🎓 What is an Annotation?

**Annotation** = Special form of metadata that provides information about code without directly affecting program execution.

Think of annotations as **sticky notes** on your code that tell the compiler, runtime, or frameworks (like Spring) what to do with your code.

### Simple Example:
```java
@Override
public String toString() {
    return "Hello";
}
```

The `@Override` annotation tells the compiler: "Check that this method actually overrides a parent class method"

---

## 🏗️ How Annotations Work

### Structure:
```
@AnnotationName(parameters)
```

### Types of Annotations:
1. **Marker Annotations**: No parameters - `@Override`
2. **Single Value**: One parameter - `@SuppressWarnings("unchecked")`
3. **Full Annotations**: Multiple parameters - `@RequestMapping(path="/users", method=RequestMethod.GET)`

---

## 📚 Categories of Annotations

1. **Built-in Java Annotations** (JDK provided)
2. **Spring Framework Annotations** (Dependency injection, MVC, etc.)
3. **Spring Boot Annotations** (Application configuration)
4. **Custom Annotations** (You create your own)

---

## 1️⃣ BUILT-IN JAVA ANNOTATIONS

### @Override
**Purpose**: Indicates method overrides a parent class method  
**Why use**: Compiler checks if method actually overrides something (catches typos)

```java
class Parent {
    void show() {
        System.out.println("Parent");
    }
}

class Child extends Parent {
    @Override
    void show() {  // ✓ Correct - overrides parent method
        System.out.println("Child");
    }
    
    @Override
    void shw() {  // ✗ Compiler error - method doesn't exist in parent!
        // Typo caught at compile time!
    }
}
```

### @Deprecated
**Purpose**: Marks code as outdated, should not be used  
**Effect**: Compiler shows warning when used

```java
@Deprecated
public void oldMethod() {
    // Old way of doing things
}

@Deprecated(since = "2.0", forRemoval = true)
public void veryOldMethod() {
    // Will be removed in future version
}

// Usage:
oldMethod();  // ⚠️ Shows warning: "method is deprecated"
```

### @SuppressWarnings
**Purpose**: Suppresses compiler warnings  
**Use carefully**: Only when you're sure the warning is safe to ignore

```java
@SuppressWarnings("unchecked")
List list = new ArrayList();  // Suppresses unchecked warning

@SuppressWarnings("deprecation")
void useOldMethod() {
    oldMethod();  // Suppresses deprecation warning
}

@SuppressWarnings({"unchecked", "rawtypes"})  // Multiple warnings
```

### @FunctionalInterface
**Purpose**: Indicates interface has exactly one abstract method (for lambda expressions)

```java
@FunctionalInterface
interface Calculator {
    int calculate(int a, int b);
    // Can have default methods
    default void print() {
        System.out.println("Calculator");
    }
}

// Can use lambda:
Calculator add = (a, b) -> a + b;
```

### @SafeVarargs
**Purpose**: Suppresses warnings for varargs methods with generic types

```java
@SafeVarargs
public static <T> void printItems(T... items) {
    for (T item : items) {
        System.out.println(item);
    }
}
```

### @Target, @Retention, @Documented, @Inherited
**Purpose**: Meta-annotations used to define custom annotations (see Custom Annotations section)

---

## 2️⃣ SPRING CORE ANNOTATIONS (Dependency Injection)

### @Component
**Purpose**: Marks class as a Spring component (bean)  
**Use**: Generic components, utilities

```java
@Component
public class EmailValidator {
    public boolean isValid(String email) {
        return email.contains("@");
    }
}
```

### @Service
**Purpose**: Marks class as a business logic service  
**Same as @Component**, but more semantic

```java
@Service
public class UserService {
    public User createUser(String name) {
        return new User(name);
    }
}
```

### @Repository
**Purpose**: Marks class as data access layer  
**Bonus**: Spring translates data access exceptions

```java
@Repository
public class UserRepository {
    public User findById(int id) {
        // Database access
        return new User();
    }
}
```

### @Controller
**Purpose**: Marks class as Spring MVC controller  
**Use**: Handles HTTP requests (web applications)

```java
@Controller
public class UserController {
    @RequestMapping("/users")
    public String getUsers() {
        return "users";
    }
}
```

### @RestController
**Purpose**: @Controller + @ResponseBody  
**Use**: REST APIs (returns JSON/XML, not views)

```java
@RestController
public class UserApiController {
    @GetMapping("/users")
    public List<User> getUsers() {
        return Arrays.asList(new User());
    }
}
```

### @Autowired
**Purpose**: Automatically injects dependency  
**Can use on**: Constructor, field, setter method

```java
@Service
public class OrderService {
    // Field injection
    @Autowired
    private UserRepository userRepository;
    
    // Constructor injection (preferred)
    private PaymentService paymentService;
    
    @Autowired
    public OrderService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    
    // Setter injection
    private EmailService emailService;
    
    @Autowired
    public void setEmailService(EmailService emailService) {
        this.emailService = emailService;
    }
}
```

### @Qualifier
**Purpose**: Specifies which bean to inject when multiple implementations exist

```java
interface PaymentService { }

@Service("creditCard")
class CreditCardService implements PaymentService { }

@Service("paypal")
class PayPalService implements PaymentService { }

@Service
class OrderService {
    @Autowired
    @Qualifier("paypal")  // Choose PayPalService
    private PaymentService paymentService;
}
```

### @Primary
**Purpose**: Marks bean as default when multiple implementations exist

```java
@Service
@Primary  // This will be injected by default
class CreditCardService implements PaymentService { }

@Service
class PayPalService implements PaymentService { }

@Service
class OrderService {
    @Autowired
    private PaymentService paymentService;  // Gets CreditCardService (primary)
}
```

### @Scope
**Purpose**: Defines bean scope (lifecycle)

```java
@Component
@Scope("singleton")  // Default - one instance
class SingletonBean { }

@Component
@Scope("prototype")  // New instance each time
class PrototypeBean { }
```

### @Lazy
**Purpose**: Bean created only when first accessed (not at startup)

```java
@Component
@Lazy
class ExpensiveService {
    // Only created when someone uses it
}
```

### @Value
**Purpose**: Injects values from properties file or environment

```java
@Component
public class AppConfig {
    @Value("${app.name}")
    private String appName;
    
    @Value("${app.version:1.0.0}")  // Default value
    private String version;
    
    @Value("${server.port}")
    private int port;
}
```

---

## 3️⃣ SPRING BOOT ANNOTATIONS

### @SpringBootApplication
**Purpose**: Main application annotation (combines 3 annotations)  
**Includes**: @Configuration + @EnableAutoConfiguration + @ComponentScan

```java
@SpringBootApplication
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

**Equivalent to:**
```java
@Configuration
@EnableAutoConfiguration
@ComponentScan
public class MyApplication { }
```

### @Configuration
**Purpose**: Indicates class contains bean definitions

```java
@Configuration
public class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
```

### @Bean
**Purpose**: Method creates a Spring bean

```java
@Configuration
public class DatabaseConfig {
    @Bean
    public Connection connection() {
        return DriverManager.getConnection("jdbc:mysql://localhost/db");
    }
    
    @Bean
    @Primary  // Can combine with other annotations
    public DataSource primaryDataSource() {
        return new HikariDataSource();
    }
}
```

### @ComponentScan
**Purpose**: Specifies packages to scan for components

```java
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.services", "com.example.repositories"})
public class MyApplication { }
```

### @EnableAutoConfiguration
**Purpose**: Enables Spring Boot auto-configuration  
**Auto-detects**: Classpath dependencies and configures them automatically

### @Profile
**Purpose**: Activates beans only for specific profiles

```java
@Component
@Profile("dev")  // Only in development
class DevDatabaseConfig { }

@Component
@Profile("prod")  // Only in production
class ProdDatabaseConfig { }
```

### @ConditionalOnProperty
**Purpose**: Creates bean only if property matches

```java
@Component
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
class FeatureService { }

@Component
@ConditionalOnProperty(name = "database.type", havingValue = "mysql")
class MySQLConnection { }
```

---

## 4️⃣ SPRING MVC ANNOTATIONS (Web)

### @RequestMapping
**Purpose**: Maps HTTP requests to methods

```java
@Controller
public class UserController {
    @RequestMapping("/users")  // GET /users
    public String getAllUsers() {
        return "users";
    }
    
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public String createUser() {
        return "success";
    }
}
```

### @GetMapping, @PostMapping, @PutMapping, @DeleteMapping
**Purpose**: Shortcuts for @RequestMapping with specific HTTP method

```java
@RestController
public class UserApiController {
    @GetMapping("/users")           // GET /users
    public List<User> getUsers() { }
    
    @PostMapping("/users")         // POST /users
    public User createUser() { }
    
    @PutMapping("/users/{id}")     // PUT /users/123
    public User updateUser(@PathVariable int id) { }
    
    @DeleteMapping("/users/{id}")  // DELETE /users/123
    public void deleteUser(@PathVariable int id) { }
}
```

### @PathVariable
**Purpose**: Binds URL path variable to method parameter

```java
@GetMapping("/users/{id}")
public User getUser(@PathVariable int id) {
    return userService.findById(id);
}

@GetMapping("/users/{userId}/orders/{orderId}")
public Order getOrder(
    @PathVariable int userId,
    @PathVariable int orderId
) { }
```

### @RequestParam
**Purpose**: Binds query parameter to method parameter

```java
@GetMapping("/users")
public List<User> searchUsers(
    @RequestParam String name,
    @RequestParam(required = false) String email,
    @RequestParam(defaultValue = "10") int limit
) {
    // GET /users?name=John&email=john@example.com&limit=20
}
```

### @RequestHeader
**Purpose**: Binds HTTP header to method parameter

```java
@GetMapping("/users")
public List<User> getUsers(
    @RequestHeader("Authorization") String auth,
    @RequestHeader(value = "User-Agent", defaultValue = "unknown") String userAgent
) { }
```

### @RequestBody
**Purpose**: Binds HTTP request body (JSON/XML) to Java object

```java
@PostMapping("/users")
public User createUser(@RequestBody User user) {
    // JSON in request body is converted to User object
    return userService.save(user);
}
```

### @ResponseBody
**Purpose**: Converts return value to HTTP response body (JSON/XML)

```java
@GetMapping("/users")
@ResponseBody
public List<User> getUsers() {
    return userService.findAll();  // Converted to JSON
}
```

### @CrossOrigin
**Purpose**: Enables CORS (Cross-Origin Resource Sharing)

```java
@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ApiController { }

// Or on method level:
@CrossOrigin
@GetMapping("/users")
public List<User> getUsers() { }
```

---

## 5️⃣ VALIDATION ANNOTATIONS (Bean Validation)

### @NotNull, @NotEmpty, @NotBlank
**Purpose**: Validates field is not null/empty/blank

```java
public class User {
    @NotNull
    private String username;
    
    @NotEmpty  // Not null and not empty
    private String email;
    
    @NotBlank  // Not null, not empty, and not just whitespace
    private String name;
}
```

### @Min, @Max, @Size
**Purpose**: Validates numeric and size constraints

```java
public class Product {
    @Min(0)
    private double price;
    
    @Max(100)
    private int discount;
    
    @Size(min = 3, max = 50)
    private String name;
    
    @Size(min = 1, max = 10)
    private List<String> tags;
}
```

### @Email, @Pattern
**Purpose**: Validates email format and regex patterns

```java
public class User {
    @Email
    private String email;
    
    @Pattern(regexp = "^[A-Z]{2}\\d{10}$")  // 2 letters + 10 digits
    private String accountNumber;
}
```

### @Valid
**Purpose**: Triggers validation of nested objects

```java
@PostMapping("/users")
public User createUser(@Valid @RequestBody User user) {
    // User object is validated before method executes
    return userService.save(user);
}
```

---

## 6️⃣ TRANSACTION ANNOTATIONS

### @Transactional
**Purpose**: Manages database transactions

```java
@Service
@Transactional  // All methods in class are transactional
public class OrderService {
    
    public void createOrder() {
        // Automatic transaction management
    }
    
    @Transactional(readOnly = true)  // Read-only transaction
    public Order findOrder(int id) {
        return orderRepository.findById(id);
    }
    
    @Transactional(rollbackFor = Exception.class)  // Rollback on any exception
    public void riskyOperation() { }
}
```

---

## 7️⃣ SCHEDULING ANNOTATIONS

### @Scheduled
**Purpose**: Executes method at fixed intervals

```java
@Component
public class ScheduledTasks {
    @Scheduled(fixedRate = 1000)  // Every 1 second
    public void task1() {
        System.out.println("Task executed");
    }
    
    @Scheduled(fixedDelay = 5000)  // 5 seconds after previous finishes
    public void task2() { }
    
    @Scheduled(cron = "0 0 12 * * ?")  // Every day at noon
    public void dailyTask() { }
}
```

### @EnableScheduling
**Purpose**: Enables scheduled task execution

```java
@SpringBootApplication
@EnableScheduling
public class MyApplication { }
```

---

## 8️⃣ ASPECT ORIENTED PROGRAMMING (AOP)

### @Aspect
**Purpose**: Marks class as aspect (cross-cutting concern)

```java
@Aspect
@Component
public class LoggingAspect {
    @Before("execution(* com.example.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Before: " + joinPoint.getSignature());
    }
}
```

### @Before, @After, @Around
**Purpose**: Defines advice (code that runs before/after/around methods)

---

## 9️⃣ CUSTOM ANNOTATIONS

### Creating Your Own Annotations

```java
// Step 1: Define annotation
@Target(ElementType.METHOD)  // Can be used on methods
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime
public @interface LogExecutionTime {
    String value() default "";  // Optional parameter
}

// Step 2: Use annotation
@Service
public class UserService {
    @LogExecutionTime("Finding user")
    public User findUser(int id) {
        return userRepository.findById(id);
    }
}

// Step 3: Process annotation (with AOP)
@Aspect
@Component
public class LoggingAspect {
    @Around("@annotation(LogExecutionTime)")
    public Object logTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long time = System.currentTimeMillis() - start;
        System.out.println("Execution time: " + time + "ms");
        return result;
    }
}
```

### Meta-Annotations Explained

#### @Target
**Purpose**: Specifies where annotation can be used

```java
@Target(ElementType.TYPE)        // Classes, interfaces
@Target(ElementType.METHOD)      // Methods
@Target(ElementType.FIELD)       // Fields
@Target(ElementType.PARAMETER)   // Method parameters
@Target(ElementType.CONSTRUCTOR) // Constructors
@Target({ElementType.TYPE, ElementType.METHOD})  // Multiple
```

#### @Retention
**Purpose**: Specifies how long annotation is kept

```java
@Retention(RetentionPolicy.SOURCE)   // Discarded by compiler
@Retention(RetentionPolicy.CLASS)    // In .class file, not at runtime
@Retention(RetentionPolicy.RUNTIME)  // Available at runtime (for reflection)
```

#### @Documented
**Purpose**: Annotation appears in JavaDoc

```java
@Documented
public @interface ApiDocumentation { }
```

#### @Inherited
**Purpose**: Annotation inherited by subclasses

```java
@Inherited
public @interface InheritedAnnotation { }

@InheritedAnnotation
class Parent { }

class Child extends Parent { }  // Also has @InheritedAnnotation
```

---

## 📊 Annotation Summary Table

| Category | Key Annotations | Purpose |
|----------|----------------|---------|
| **Java Built-in** | @Override, @Deprecated, @SuppressWarnings | Compiler hints |
| **Bean Creation** | @Component, @Service, @Repository, @Controller | Create Spring beans |
| **Dependency Injection** | @Autowired, @Qualifier, @Primary, @Value | Inject dependencies |
| **Configuration** | @Configuration, @Bean, @ComponentScan | Configure Spring |
| **Web MVC** | @RestController, @GetMapping, @PostMapping | Handle HTTP requests |
| **Request/Response** | @PathVariable, @RequestParam, @RequestBody | Bind request data |
| **Validation** | @NotNull, @Valid, @Email, @Size | Validate data |
| **Transaction** | @Transactional | Manage transactions |
| **Scheduling** | @Scheduled, @EnableScheduling | Schedule tasks |
| **Profile** | @Profile, @ConditionalOnProperty | Conditional beans |

---

## 🎯 Best Practices

1. **Use @Service for business logic**, @Repository for data access
2. **Prefer constructor injection** over field injection
3. **Use @Qualifier** when multiple implementations exist
4. **Add @Valid** to request body parameters
5. **Use @Transactional** on service methods, not controllers
6. **Document custom annotations** clearly
7. **Keep annotation parameters meaningful**

---

## 💡 Quick Reference

```java
// Bean Creation
@Component, @Service, @Repository, @Controller, @RestController

// Dependency Injection
@Autowired, @Qualifier("name"), @Primary, @Value("${prop}")

// Configuration
@Configuration, @Bean, @ComponentScan, @SpringBootApplication

// Web
@GetMapping, @PostMapping, @PathVariable, @RequestParam, @RequestBody

// Validation
@NotNull, @Valid, @Email, @Size(min=1, max=100)

// Transaction
@Transactional

// Profile
@Profile("dev"), @ConditionalOnProperty(name="feature.enabled")
```

---

**Remember**: Annotations are metadata - they tell frameworks what to do, but don't change your code logic directly! 🚀

