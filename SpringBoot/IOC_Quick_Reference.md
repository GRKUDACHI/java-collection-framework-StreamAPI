# 🚀 Spring Boot IOC Container - Quick Reference Guide

## 📋 Core Concepts Cheat Sheet

### What is IOC?
**Inversion of Control** = Framework manages object creation and dependencies instead of your code.

### Key Components

| Component | Purpose | Example |
|-----------|---------|---------|
| **ApplicationContext** | The IOC Container | Manages all beans |
| **Bean** | Object managed by Spring | Any class with @Component/@Service/@Repository |
| **Dependency Injection** | Automatic wiring of dependencies | @Autowired |
| **Component Scanning** | Finding and registering beans | @ComponentScan |

---

## 🎯 Common Annotations

### Bean Creation
```java
@Component          // Generic component
@Service            // Business logic service
@Repository         // Data access layer
@Controller         // Web controller
@RestController     // REST API controller
```

### Dependency Injection
```java
@Autowired          // Inject dependency (constructor/field/setter)
@Qualifier("name")  // Specify which bean when multiple exist
@Primary            // Default bean when multiple implementations
```

### Bean Configuration
```java
@Configuration      // Class contains bean definitions
@Bean               // Method creates a bean
@Scope("singleton") // Bean scope (singleton/prototype)
@Lazy               // Create bean only when accessed
@Profile("dev")     // Create bean only for specific profile
```

### Conditional Beans
```java
@ConditionalOnProperty(name = "prop", havingValue = "value")
```

---

## 💡 Dependency Injection Methods

### 1. Constructor Injection (Recommended ✅)
```java
@Service
class UserService {
    private UserRepository repo;
    
    public UserService(UserRepository repo) {  // Spring injects here
        this.repo = repo;
    }
}
```

### 2. Field Injection (Not Recommended ❌)
```java
@Service
class UserService {
    @Autowired
    private UserRepository repo;  // Works, but harder to test
}
```

### 3. Setter Injection
```java
@Service
class UserService {
    private UserRepository repo;
    
    @Autowired
    public void setRepo(UserRepository repo) {
        this.repo = repo;
    }
}
```

---

## 🔄 Bean Lifecycle

```
1. Component Scanning
   ↓
2. Bean Definition Creation
   ↓
3. Bean Instantiation (Constructor)
   ↓
4. Dependency Injection
   ↓
5. @PostConstruct
   ↓
6. Bean Ready
   ↓
7. @PreDestroy (on shutdown)
```

---

## 🎨 Bean Scopes

| Scope | Description | Use Case |
|-------|-------------|----------|
| **singleton** (default) | One instance for entire app | Services, repositories |
| **prototype** | New instance each time | Stateful beans |
| **request** | One per HTTP request | Web controllers |
| **session** | One per HTTP session | User-specific data |

---

## 🔧 Multiple Implementations

### Problem: Multiple implementations of same interface
```java
interface PaymentService { }

@Service
class CreditCardService implements PaymentService { }

@Service
class PayPalService implements PaymentService { }  // Which one to inject?
```

### Solution 1: @Primary (Default)
```java
@Service
@Primary
class CreditCardService implements PaymentService { }
// This will be injected by default
```

### Solution 2: @Qualifier (Specific)
```java
@Service
class OrderService {
    @Autowired
    @Qualifier("payPalService")  // Explicitly choose
    private PaymentService paymentService;
}
```

---

## 📝 Common Patterns

### Pattern 1: Layered Architecture
```java
@RestController
class UserController {
    @Autowired
    private UserService userService;  // → injects UserService
}

@Service
class UserService {
    @Autowired
    private UserRepository userRepository;  // → injects UserRepository
}

@Repository
class UserRepository {
    // Data access logic
}
```

### Pattern 2: Configuration Class
```java
@Configuration
class AppConfig {
    @Bean
    public DataSource dataSource() {
        return new HikariDataSource();
    }
}
```

### Pattern 3: Conditional Beans
```java
@Component
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
class FeatureService {
    // Only created if property is set
}
```

---

## ⚠️ Common Mistakes

| Mistake | Problem | Fix |
|---------|---------|-----|
| Missing @Component/@Service | Bean not found | Add annotation |
| Circular dependency | StackOverflowError | Refactor design |
| @Autowired on interface | No implementation | Create implementation |
| Multiple implementations without @Primary/@Qualifier | Ambiguous bean | Add @Primary or @Qualifier |

---

## 🔍 Debugging Tips

### See All Beans
```java
@Autowired
ApplicationContext context;

public void showBeans() {
    String[] beans = context.getBeanDefinitionNames();
    Arrays.stream(beans).forEach(System.out::println);
}
```

### Check Bean Scope
```java
Object bean1 = context.getBean(MyService.class);
Object bean2 = context.getBean(MyService.class);
System.out.println(bean1 == bean2);  // true for singleton
```

### Verify Dependency Injection
```java
// Add logging in constructor
public UserService(UserRepository repo) {
    System.out.println("UserRepository injected: " + repo);
    this.repo = repo;
}
```

---

## 🎓 Learning Path

1. ✅ **Start**: Understand basic @Component and @Autowired
2. ✅ **Next**: Learn constructor vs field injection
3. ✅ **Then**: Explore @Primary and @Qualifier
4. ✅ **Advanced**: Bean scopes, conditional beans, profiles
5. ✅ **Expert**: BeanPostProcessor, custom annotations

---

## 📚 File Reference

- **README**: `IOC_Container_Tutorial_README.md` - Complete tutorial
- **Basic Demo**: `IOCContainerBasicDemo.java` - Basic examples
- **Advanced Demo**: `IOCContainerAdvancedDemo.java` - Advanced features
- **Implementation**: `SimpleIOCContainerImplementation.java` - How it works internally

---

## 💬 Quick Q&A

**Q: When should I use @Component vs @Service?**
A: Use @Service for business logic, @Component for generic utilities. Both work the same, but @Service is more semantic.

**Q: Constructor vs Field injection?**
A: Always prefer constructor injection. It's testable, immutable, and fails fast.

**Q: What if I have multiple implementations?**
A: Use @Primary for default, or @Qualifier for specific selection.

**Q: How does Spring find my beans?**
A: Component scanning starts from @SpringBootApplication package and scans sub-packages.

**Q: Can I create beans manually?**
A: Yes, use @Configuration and @Bean methods.

---

**Remember**: IOC Container makes your code loosely coupled, testable, and maintainable! 🚀

