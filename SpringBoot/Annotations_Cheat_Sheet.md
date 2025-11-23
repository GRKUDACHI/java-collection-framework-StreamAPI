# 📋 Annotations Cheat Sheet - Quick Reference

## 🎯 What is an Annotation?
**Annotation** = Metadata that provides information about code
- Format: `@AnnotationName`
- Tells compiler/runtime/frameworks what to do with your code

---

## 📚 CATEGORY 1: Java Built-in Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Override` | Method overrides parent | `@Override public String toString()` |
| `@Deprecated` | Mark as outdated | `@Deprecated public void oldMethod()` |
| `@SuppressWarnings` | Suppress warnings | `@SuppressWarnings("unchecked")` |
| `@FunctionalInterface` | For lambda expressions | `@FunctionalInterface interface Calc` |

---

## 📚 CATEGORY 2: Spring Bean Creation

| Annotation | Purpose | When to Use |
|------------|---------|-------------|
| `@Component` | Generic Spring component | Utilities, helpers |
| `@Service` | Business logic service | Business operations |
| `@Repository` | Data access layer | Database operations |
| `@Controller` | Web controller | MVC controllers |
| `@RestController` | REST API controller | REST endpoints |

**All create beans, but use semantic names for clarity!**

---

## 📚 CATEGORY 3: Dependency Injection

| Annotation | Purpose | Usage |
|------------|---------|-------|
| `@Autowired` | Auto-inject dependency | Constructor/Field/Setter |
| `@Qualifier("name")` | Choose specific bean | When multiple implementations |
| `@Primary` | Default bean | When multiple implementations |
| `@Value("${prop}")` | Inject property value | Configuration values |

### Best Practice:
```java
// ✅ PREFERRED: Constructor injection
@Service
class UserService {
    private UserRepository repo;
    
    @Autowired
    public UserService(UserRepository repo) {
        this.repo = repo;
    }
}

// ❌ AVOID: Field injection (harder to test)
@Service
class UserService {
    @Autowired
    private UserRepository repo;
}
```

---

## 📚 CATEGORY 4: Bean Configuration

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@Configuration` | Bean definition class | `@Configuration class Config` |
| `@Bean` | Create bean manually | `@Bean public DataSource ds()` |
| `@Scope("singleton")` | Bean scope | Singleton (default) or prototype |
| `@Lazy` | Lazy initialization | Create when accessed, not at startup |
| `@Profile("dev")` | Profile-specific bean | Only in dev environment |

---

## 📚 CATEGORY 5: Bean Lifecycle

| Annotation | Purpose | When Called |
|------------|---------|-------------|
| `@PostConstruct` | After bean creation | After constructor & DI |
| `@PreDestroy` | Before bean destruction | Before application shutdown |

---

## 📚 CATEGORY 6: Spring Boot Main

| Annotation | Purpose | Includes |
|------------|---------|----------|
| `@SpringBootApplication` | Main app annotation | `@Configuration + @EnableAutoConfiguration + @ComponentScan` |
| `@EnableAutoConfiguration` | Auto-configure | Detects dependencies |
| `@ComponentScan` | Scan packages | Find components |

---

## 📚 CATEGORY 7: Web/MVC Annotations

| Annotation | Purpose | HTTP Method |
|------------|---------|-------------|
| `@RequestMapping` | Map HTTP request | Any |
| `@GetMapping` | Handle GET request | GET |
| `@PostMapping` | Handle POST request | POST |
| `@PutMapping` | Handle PUT request | PUT |
| `@DeleteMapping` | Handle DELETE request | DELETE |
| `@PatchMapping` | Handle PATCH request | PATCH |

### Request Binding:

| Annotation | Purpose | Source |
|------------|---------|--------|
| `@PathVariable` | URL path variable | `/users/{id}` → `@PathVariable int id` |
| `@RequestParam` | Query parameter | `?name=John` → `@RequestParam String name` |
| `@RequestBody` | Request body (JSON/XML) | POST body → Java object |
| `@RequestHeader` | HTTP header | `Authorization` header |
| `@CookieValue` | Cookie value | Cookie from request |

---

## 📚 CATEGORY 8: Validation Annotations

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@NotNull` | Not null | `@NotNull private String name` |
| `@NotEmpty` | Not null and not empty | `@NotEmpty private String email` |
| `@NotBlank` | Not null/empty/whitespace | `@NotBlank private String username` |
| `@Min(value)` | Minimum value | `@Min(0) private double price` |
| `@Max(value)` | Maximum value | `@Max(100) private int discount` |
| `@Size(min, max)` | Size constraint | `@Size(min=3, max=50) private String name` |
| `@Email` | Email format | `@Email private String email` |
| `@Pattern(regexp)` | Regex pattern | `@Pattern(regexp = "^[A-Z]{2}")` |
| `@Valid` | Trigger validation | `@Valid @RequestBody User user` |

---

## 📚 CATEGORY 9: Transaction Management

| Annotation | Purpose | Usage |
|------------|---------|-------|
| `@Transactional` | Manage transaction | Service methods |
| `@Transactional(readOnly=true)` | Read-only transaction | Query operations |

---

## 📚 CATEGORY 10: Conditional Beans

| Annotation | Purpose | Example |
|------------|---------|---------|
| `@ConditionalOnProperty` | Create if property exists | `@ConditionalOnProperty(name="feature.enabled")` |
| `@ConditionalOnClass` | Create if class exists | `@ConditionalOnClass(DataSource.class)` |
| `@ConditionalOnMissingBean` | Create if bean missing | `@ConditionalOnMissingBean(DataSource.class)` |

---

## 📚 CATEGORY 11: Scheduling

| Annotation | Purpose | Usage |
|------------|---------|-------|
| `@Scheduled` | Schedule method execution | `@Scheduled(fixedRate = 1000)` |
| `@EnableScheduling` | Enable scheduling | On main class |

---

## 📚 CATEGORY 12: Custom Annotations

### Creating Your Own:

```java
// Step 1: Define
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value() default "";
    int count() default 1;
}

// Step 2: Use
@MyAnnotation(value = "test", count = 5)
public void myMethod() { }

// Step 3: Process (with reflection or AOP)
```

### Meta-Annotations (for creating annotations):

| Annotation | Purpose |
|------------|---------|
| `@Target` | Where can be used (TYPE, METHOD, FIELD) |
| `@Retention` | How long kept (SOURCE, CLASS, RUNTIME) |
| `@Documented` | Include in JavaDoc |
| `@Inherited` | Inherited by subclasses |

---

## 🎯 Common Patterns

### Pattern 1: Layered Architecture
```java
@RestController
class UserController {
    @Autowired
    private UserService service;  // Business logic
}

@Service
class UserService {
    @Autowired
    private UserRepository repo;  // Data access
}

@Repository
class UserRepository { }
```

### Pattern 2: Multiple Implementations
```java
interface PaymentService { }

@Service
@Primary
class CreditCardService implements PaymentService { }

@Service
@Qualifier("paypal")
class PayPalService implements PaymentService { }

@Service
class OrderService {
    @Autowired
    private PaymentService service;  // Gets CreditCardService
    
    @Autowired
    @Qualifier("paypal")
    private PaymentService paypalService;  // Gets PayPalService
}
```

### Pattern 3: Configuration Properties
```java
@Component
@ConfigurationProperties(prefix = "app")
class AppProperties {
    private String name;
    private String version;
    // Getters/setters required
}
```

---

## ⚠️ Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Missing `@Component` | Bean not found | Add annotation |
| `@Autowired` on interface | No implementation | Create implementation class |
| Multiple implementations without `@Primary`/`@Qualifier` | Ambiguous dependency | Add `@Primary` or `@Qualifier` |
| `@Transactional` on controller | Wrong layer | Move to service |
| Missing `@Valid` | Validation not triggered | Add `@Valid` to parameter |

---

## 🔍 Quick Lookup

### "I want to..."
- **Create a Spring bean** → `@Component`, `@Service`, `@Repository`
- **Inject a dependency** → `@Autowired`
- **Choose between multiple implementations** → `@Primary` or `@Qualifier`
- **Handle HTTP request** → `@GetMapping`, `@PostMapping`
- **Get URL parameter** → `@PathVariable`
- **Get query parameter** → `@RequestParam`
- **Get request body** → `@RequestBody`
- **Validate data** → `@NotNull`, `@Valid`
- **Manage transaction** → `@Transactional`
- **Create bean conditionally** → `@ConditionalOnProperty`
- **Schedule a task** → `@Scheduled`

---

## 💡 Memory Tricks

- **@Component** = Generic **C**omponent
- **@Service** = Business **S**ervice
- **@Repository** = Data **R**epository
- **@Autowired** = **A**uto-inject
- **@Primary** = **P**rimary/default choice
- **@Qualifier** = **Q**ualify/choose specific one

---

**Print this cheat sheet and keep it handy!** 🚀

