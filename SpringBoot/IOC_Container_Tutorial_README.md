# 🎯 Spring Boot IOC Container - Complete Tutorial

## 📖 Table of Contents
1. [What is IOC Container?](#what-is-ioc-container)
2. [Why Do We Need IOC?](#why-do-we-need-ioc)
3. [How IOC Container Works](#how-ioc-container-works)
4. [Core Concepts](#core-concepts)
5. [How Spring Implements IOC](#how-spring-implements-ioc)
6. [Step-by-Step Implementation](#step-by-step-implementation)
7. [Advanced IOC Features](#advanced-ioc-features)

---

## 🎓 What is IOC Container?

**IOC (Inversion of Control)** is a design principle where **control of object creation and lifecycle is transferred to a framework** instead of the application code itself.

### Traditional Approach (Without IOC)
```java
// Developer creates objects manually
UserService userService = new UserService();
UserRepository userRepository = new UserRepository();
userService.setRepository(userRepository);
```

**Problems:**
- ❌ Tight coupling between classes
- ❌ Hard to test (can't easily swap dependencies)
- ❌ Manual object management
- ❌ Difficult to maintain

### IOC Approach (With Spring Boot)
```java
// Spring Container creates and injects objects automatically
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    // Spring automatically injects UserRepository
}
```

**Benefits:**
- ✅ Loose coupling
- ✅ Easy testing (can mock dependencies)
- ✅ Automatic dependency injection
- ✅ Better maintainability

---

## 💡 Why Do We Need IOC?

### Problem Without IOC:
Imagine building a car:
```java
class Car {
    private Engine engine;
    private Wheels wheels;
    private Transmission transmission;
    
    public Car() {
        // Manual creation - tightly coupled!
        this.engine = new Engine();
        this.wheels = new Wheels();
        this.transmission = new Transmission();
    }
}
```

**Issues:**
- If Engine needs FuelPump, you must create it manually
- If you want to test with a MockEngine, it's difficult
- Changing one component requires changing multiple files

### Solution With IOC:
```java
@Component
class Car {
    @Autowired
    private Engine engine;
    @Autowired
    private Wheels wheels;
    @Autowired
    private Transmission transmission;
    // Spring automatically creates and injects all dependencies!
}
```

**Benefits:**
- Spring handles all object creation
- Easy to swap implementations
- Simple testing with mocks
- Clean, maintainable code

---

## 🔧 How IOC Container Works

### The IOC Container Lifecycle:

```
1. Spring Boot Application Starts
        ↓
2. Component Scanning (Finds @Component, @Service, @Repository, @Controller)
        ↓
3. Bean Creation (Creates instances of all beans)
        ↓
4. Dependency Injection (Resolves and injects dependencies)
        ↓
5. Bean Initialization (@PostConstruct methods called)
        ↓
6. Application Ready (All beans available for use)
        ↓
7. On Shutdown - Bean Destruction (@PreDestroy methods called)
```

### Key Components:

1. **ApplicationContext**: The IOC container that manages beans
2. **Bean**: An object managed by Spring
3. **Dependency Injection**: Automatic injection of dependencies
4. **Component Scanning**: Finding and registering components

---

## 🎯 Core Concepts

### 1. **ApplicationContext (The Container)**
The heart of Spring IOC. It:
- Creates and manages beans
- Resolves dependencies
- Provides bean lifecycle management

```java
ApplicationContext context = SpringApplication.run(MyApp.class, args);
UserService service = context.getBean(UserService.class);
```

### 2. **Beans**
Objects managed by Spring container:
- Created when application starts
- Singletons by default (one instance shared)
- Dependencies automatically injected

### 3. **Dependency Injection Types**

#### A. Constructor Injection (Recommended)
```java
@Service
public class UserService {
    private UserRepository repository;
    
    // Spring injects via constructor
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}
```

#### B. Setter Injection
```java
@Service
public class UserService {
    private UserRepository repository;
    
    @Autowired
    public void setRepository(UserRepository repository) {
        this.repository = repository;
    }
}
```

#### C. Field Injection (Less Recommended)
```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
}
```

---

## 🏗️ How Spring Implements IOC

### Internal Working:

#### 1. **Component Scanning**
```java
@SpringBootApplication  // This enables component scanning
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

Spring scans packages and sub-packages for:
- `@Component` - Generic component
- `@Service` - Business logic
- `@Repository` - Data access
- `@Controller` / `@RestController` - Web controllers

#### 2. **Bean Definition**
Spring creates bean definitions (metadata about beans):
```java
BeanDefinition {
    className: "com.example.UserService"
    scope: "singleton"
    dependencies: ["UserRepository"]
    initMethod: "init"
    destroyMethod: "cleanup"
}
```

#### 3. **Bean Factory**
Creates bean instances:
```java
// Simplified internal Spring logic
public <T> T getBean(Class<T> clazz) {
    // 1. Check if bean exists
    if (singletonCache.containsKey(clazz)) {
        return singletonCache.get(clazz);
    }
    
    // 2. Create instance
    T instance = createInstance(clazz);
    
    // 3. Inject dependencies
    injectDependencies(instance);
    
    // 4. Store in cache
    singletonCache.put(clazz, instance);
    
    return instance;
}
```

#### 4. **Dependency Resolution**
Spring builds a dependency graph:
```
UserController
    ↓ depends on
UserService
    ↓ depends on
UserRepository
    ↓ depends on
DataSource
```

Spring creates them in order: DataSource → UserRepository → UserService → UserController

---

## 📝 Step-by-Step Implementation

### Step 1: Create a Simple Bean
```java
@Component
public class HelloService {
    public String greet() {
        return "Hello from IOC Container!";
    }
}
```

### Step 2: Inject into Another Bean
```java
@Service
public class GreetingService {
    @Autowired
    private HelloService helloService;
    
    public String welcome() {
        return helloService.greet();
    }
}
```

### Step 3: Use in Main Application
```java
@SpringBootApplication
public class MyApp implements CommandLineRunner {
    @Autowired
    private GreetingService greetingService;
    
    public static void main(String[] args) {
        SpringApplication.run(MyApp.class, args);
    }
    
    @Override
    public void run(String... args) {
        System.out.println(greetingService.welcome());
    }
}
```

---

## 🚀 Advanced IOC Features

### 1. **Bean Scopes**
```java
@Component
@Scope("singleton")  // Default - one instance
public class SingletonBean { }

@Component
@Scope("prototype")  // New instance each time
public class PrototypeBean { }
```

### 2. **Conditional Beans**
```java
@Configuration
public class AppConfig {
    @Bean
    @ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
    public FeatureService featureService() {
        return new FeatureService();
    }
}
```

### 3. **Primary Beans**
```java
@Repository
@Primary  // This will be injected when multiple implementations exist
public class JpaUserRepository implements UserRepository { }
```

### 4. **Qualifier**
```java
@Service
public class UserService {
    @Autowired
    @Qualifier("jdbcRepository")  // Specify which bean to inject
    private UserRepository repository;
}
```

### 5. **Lazy Initialization**
```java
@Component
@Lazy  // Bean created only when first accessed
public class ExpensiveService { }
```

---

## 📊 IOC Container Architecture

```
┌─────────────────────────────────────┐
│      Spring Application Context     │
│         (IOC Container)             │
├─────────────────────────────────────┤
│  ┌───────────────────────────────┐  │
│  │   Bean Definition Registry    │  │
│  │   (Metadata about beans)      │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │   Singleton Bean Cache        │  │
│  │   (Actual bean instances)     │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │   Dependency Resolver         │  │
│  │   (Resolves dependencies)     │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

## ✅ Key Takeaways

1. **IOC Container** manages object creation and lifecycle
2. **Dependency Injection** automatically wires dependencies
3. **Component Scanning** discovers and registers beans
4. **ApplicationContext** is the IOC container implementation
5. **Beans** are objects managed by Spring
6. **Constructor injection** is preferred over field injection
7. **Singleton** is the default bean scope

---

## 🎓 Next Steps

1. Run the examples in `IOCContainerBasicDemo.java`
2. Study `IOCContainerAdvancedDemo.java` for advanced features
3. Practice creating your own beans and dependency injections
4. Explore `@Configuration` and `@Bean` annotations
5. Learn about `@Autowired`, `@Qualifier`, and `@Primary`

---

**Remember**: IOC makes your code more maintainable, testable, and loosely coupled. It's one of the fundamental concepts that makes Spring Boot powerful! 🚀

