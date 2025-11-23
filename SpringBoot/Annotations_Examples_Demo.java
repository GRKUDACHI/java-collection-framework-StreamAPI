/**
 * ============================================================================
 * JAVA & SPRING BOOT ANNOTATIONS - PRACTICAL EXAMPLES
 * ============================================================================
 * 
 * This file demonstrates various annotations with working examples.
 * Each section shows annotations in action with clear explanations.
 * 
 * ANNOTATIONS COVERED:
 * -------------------
 * 1. Java Built-in Annotations
 * 2. Spring Bean Creation Annotations
 * 3. Dependency Injection Annotations
 * 4. Spring Boot Configuration Annotations
 * 5. Web/MVC Annotations
 * 6. Validation Annotations
 * 7. Custom Annotations
 */

package SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.*;
import java.lang.reflect.Method;
import java.util.List;

// ============================================================================
// SECTION 1: JAVA BUILT-IN ANNOTATIONS
// ============================================================================

/**
 * @Override - Ensures method actually overrides parent method
 */
class Parent {
    void display() {
        System.out.println("Parent display");
    }
    
    @Deprecated  // Mark as outdated
    void oldMethod() {
        System.out.println("This method is deprecated");
    }
}

class Child extends Parent {
    @Override
    void display() {  // ✓ Correct - overrides parent
        System.out.println("Child display");
    }
    
    // @Override
    // void display2() { }  // ✗ Compiler error - doesn't override anything!
}

/**
 * @Deprecated - Marks method as outdated
 */
class OldApi {
    @Deprecated
    public void oldWay() {
        System.out.println("Old way - don't use!");
    }
    
    @Deprecated(since = "2.0", forRemoval = true)
    public void veryOldWay() {
        System.out.println("Will be removed!");
    }
}

/**
 * @SuppressWarnings - Suppresses compiler warnings
 */
class WarningDemo {
    @SuppressWarnings("unchecked")
    public void suppressUnchecked() {
        List list = new java.util.ArrayList();  // No unchecked warning
    }
    
    @SuppressWarnings({"deprecation", "unchecked"})
    public void suppressMultiple() {
        OldApi api = new OldApi();
        api.oldWay();  // No deprecation warning
    }
}

/**
 * @FunctionalInterface - For lambda expressions
 */
@FunctionalInterface
interface MathOperation {
    int calculate(int a, int b);
    
    // Can have default methods
    default void printResult(int result) {
        System.out.println("Result: " + result);
    }
}

// ============================================================================
// SECTION 2: SPRING BEAN CREATION ANNOTATIONS
// ============================================================================

/**
 * @Component - Generic Spring component
 */
@Component
class UtilityService {
    public String process(String data) {
        return "Processed: " + data;
    }
}

/**
 * @Service - Business logic service
 */
@Service
class OrderService {
    public void processOrder(String orderId) {
        System.out.println("Processing order: " + orderId);
    }
}

/**
 * @Repository - Data access layer
 */
interface UserRepository {
    String findUser(int id);
}

@Repository
class JpaUserRepository implements UserRepository {
    public String findUser(int id) {
        return "User " + id + " from database";
    }
}

// ============================================================================
// SECTION 3: DEPENDENCY INJECTION ANNOTATIONS
// ============================================================================

/**
 * @Autowired - Field injection
 */
@Service
class PaymentService {
    @Autowired
    private UserRepository userRepository;
    
    public String processPayment(int userId) {
        String user = userRepository.findUser(userId);
        return "Processing payment for " + user;
    }
}

/**
 * @Autowired - Constructor injection (preferred)
 */
@Service
class OrderProcessingService {
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    
    // Spring automatically injects dependencies
    @Autowired
    public OrderProcessingService(UserRepository userRepository, 
                                  PaymentService paymentService) {
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        System.out.println("✓ OrderProcessingService created with dependencies");
    }
}

/**
 * @Qualifier - Specify which bean when multiple exist
 */
interface NotificationService {
    void send(String message);
}

@Service
@Qualifier("email")
class EmailNotificationService implements NotificationService {
    public void send(String message) {
        System.out.println("📧 Email: " + message);
    }
}

@Service
@Qualifier("sms")
class SmsNotificationService implements NotificationService {
    public void send(String message) {
        System.out.println("📱 SMS: " + message);
    }
}

@Service
class NotificationManager {
    @Autowired
    @Qualifier("email")  // Choose EmailNotificationService
    private NotificationService notificationService;
    
    public void notify(String message) {
        notificationService.send(message);
    }
}

/**
 * @Primary - Default bean when multiple exist
 */
@Service
@Primary  // This will be injected by default
class DefaultShippingService implements ShippingService {
    public void ship(String item) {
        System.out.println("📦 Shipping: " + item);
    }
}

interface ShippingService {
    void ship(String item);
}

@Service
class ExpressShippingService implements ShippingService {
    public void ship(String item) {
        System.out.println("🚀 Express shipping: " + item);
    }
}

@Service
class OrderFulfillmentService {
    @Autowired
    private ShippingService shippingService;  // Gets DefaultShippingService (@Primary)
    
    public void fulfill(String item) {
        shippingService.ship(item);
    }
}

/**
 * @Value - Inject from properties
 */
@Component
class AppConfig {
    @Value("${app.name:MyApp}")  // Default value if property not found
    private String appName;
    
    @Value("${app.version}")
    private String version;
    
    public String getInfo() {
        return appName + " v" + version;
    }
}

// ============================================================================
// SECTION 4: BEAN SCOPE ANNOTATIONS
// ============================================================================

/**
 * @Scope - Singleton (default)
 */
@Component
@Scope("singleton")
class SingletonCounter {
    private static int count = 0;
    
    public SingletonCounter() {
        count++;
        System.out.println("SingletonCounter instance #" + count + " created");
    }
    
    public int getInstanceNumber() {
        return count;
    }
}

/**
 * @Scope - Prototype (new instance each time)
 */
@Component
@Scope("prototype")
class PrototypeCounter {
    private static int totalInstances = 0;
    private int instanceId;
    
    public PrototypeCounter() {
        instanceId = ++totalInstances;
        System.out.println("PrototypeCounter instance #" + instanceId + " created");
    }
    
    public int getInstanceId() {
        return instanceId;
    }
}

// ============================================================================
// SECTION 5: BEAN LIFECYCLE ANNOTATIONS
// ============================================================================

/**
 * @PostConstruct and @PreDestroy
 */
@Component
class LifecycleDemoBean {
    public LifecycleDemoBean() {
        System.out.println("  1️⃣ Constructor called");
    }
    
    @PostConstruct
    public void initialize() {
        System.out.println("  2️⃣ @PostConstruct called - Bean initialized");
    }
    
    public void doWork() {
        System.out.println("  3️⃣ Bean is working");
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("  4️⃣ @PreDestroy called - Cleaning up");
    }
}

// ============================================================================
// SECTION 6: @Configuration AND @Bean
// ============================================================================

/**
 * @Configuration - Class contains bean definitions
 * @Bean - Method creates a bean
 */
@Configuration
class DatabaseConfig {
    
    @Bean
    public DataSourceConfig dataSource() {
        System.out.println("✓ Creating DataSourceConfig bean");
        return new DataSourceConfig("jdbc:mysql://localhost/db");
    }
    
    @Bean
    @Primary  // Can combine with other annotations
    public ConnectionManager primaryConnection(DataSourceConfig dataSource) {
        System.out.println("✓ Creating ConnectionManager with DataSourceConfig");
        return new ConnectionManager(dataSource);
    }
}

// Simple classes for demo
class DataSourceConfig {
    private String url;
    
    public DataSourceConfig(String url) {
        this.url = url;
    }
    
    public String getUrl() {
        return url;
    }
}

class ConnectionManager {
    private DataSourceConfig dataSource;
    
    public ConnectionManager(DataSourceConfig dataSource) {
        this.dataSource = dataSource;
    }
    
    public String connect() {
        return "Connected to: " + dataSource.getUrl();
    }
}

// ============================================================================
// SECTION 7: CUSTOM ANNOTATIONS
// ============================================================================

/**
 * Creating a custom annotation
 */

// Step 1: Define the annotation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface LogExecutionTime {
    String value() default "";
    boolean detailed() default false;
}

// Step 2: Use the annotation
@Service
class TaskService {
    @LogExecutionTime("Simple Task")
    public void simpleTask() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Task completed");
    }
    
    @LogExecutionTime(value = "Complex Task", detailed = true)
    public void complexTask() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Complex task completed");
    }
}

// Step 3: Process the annotation (simplified - real version uses AOP)
class AnnotationProcessor {
    public static void processAnnotations(Object obj) throws Exception {
        Class<?> clazz = obj.getClass();
        Method[] methods = clazz.getMethods();
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(LogExecutionTime.class)) {
                LogExecutionTime annotation = method.getAnnotation(LogExecutionTime.class);
                String taskName = annotation.value();
                boolean detailed = annotation.detailed();
                
                System.out.println("📊 Starting: " + taskName);
                long start = System.currentTimeMillis();
                
                method.invoke(obj);
                
                long time = System.currentTimeMillis() - start;
                if (detailed) {
                    System.out.println("  ⏱️ Detailed info - Method: " + method.getName());
                    System.out.println("  ⏱️ Execution time: " + time + "ms");
                } else {
                    System.out.println("  ⏱️ Time: " + time + "ms");
                }
            }
        }
    }
}

// ============================================================================
// MAIN APPLICATION - DEMONSTRATING ALL ANNOTATIONS
// ============================================================================

@SpringBootApplication
public class Annotations_Examples_Demo implements CommandLineRunner {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private UtilityService utilityService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderProcessingService orderProcessingService;
    
    @Autowired
    private NotificationManager notificationManager;
    
    @Autowired
    private OrderFulfillmentService orderFulfillmentService;
    
    @Autowired
    private SingletonCounter singleton1;
    
    @Autowired
    private SingletonCounter singleton2;
    
    @Autowired
    private LifecycleDemoBean lifecycleBean;
    
    @Autowired
    private ConnectionManager connectionManager;
    
    @Autowired
    private TaskService taskService;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("📝 JAVA & SPRING BOOT ANNOTATIONS - EXAMPLES");
        System.out.println("=".repeat(70));
        System.out.println();
        
        SpringApplication.run(Annotations_Examples_Demo.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("🚀 ANNOTATION DEMONSTRATIONS");
        System.out.println("=".repeat(70));
        System.out.println();
        
        // ====================================================================
        // DEMO 1: Java Built-in Annotations
        // ====================================================================
        System.out.println("📌 DEMO 1: Java Built-in Annotations");
        System.out.println("-".repeat(70));
        
        Child child = new Child();
        child.display();  // Uses @Override
        
        System.out.println("✓ @Override annotation ensures method overrides parent");
        System.out.println();
        
        // ====================================================================
        // DEMO 2: Bean Creation Annotations
        // ====================================================================
        System.out.println("📌 DEMO 2: Bean Creation (@Component, @Service, @Repository)");
        System.out.println("-".repeat(70));
        System.out.println("UtilityService: " + utilityService.process("data"));
        orderService.processOrder("ORD-123");
        System.out.println();
        
        // ====================================================================
        // DEMO 3: Dependency Injection
        // ====================================================================
        System.out.println("📌 DEMO 3: Dependency Injection (@Autowired)");
        System.out.println("-".repeat(70));
        System.out.println(paymentService.processPayment(123));
        System.out.println();
        
        // ====================================================================
        // DEMO 4: @Qualifier
        // ====================================================================
        System.out.println("📌 DEMO 4: Multiple Implementations (@Qualifier)");
        System.out.println("-".repeat(70));
        notificationManager.notify("Order confirmed");
        System.out.println();
        
        // ====================================================================
        // DEMO 5: @Primary
        // ====================================================================
        System.out.println("📌 DEMO 5: Default Bean (@Primary)");
        System.out.println("-".repeat(70));
        orderFulfillmentService.fulfill("Laptop");
        System.out.println();
        
        // ====================================================================
        // DEMO 6: Bean Scopes
        // ====================================================================
        System.out.println("📌 DEMO 6: Bean Scopes (Singleton vs Prototype)");
        System.out.println("-".repeat(70));
        System.out.println("Singleton (same instance):");
        System.out.println("  Instance count 1: " + singleton1.getInstanceNumber());
        System.out.println("  Instance count 2: " + singleton2.getInstanceNumber());
        System.out.println("  Same instance? " + (singleton1 == singleton2));
        
        PrototypeCounter proto1 = applicationContext.getBean(PrototypeCounter.class);
        PrototypeCounter proto2 = applicationContext.getBean(PrototypeCounter.class);
        System.out.println();
        System.out.println("Prototype (new instance each time):");
        System.out.println("  Proto1 ID: " + proto1.getInstanceId());
        System.out.println("  Proto2 ID: " + proto2.getInstanceId());
        System.out.println("  Same instance? " + (proto1 == proto2));
        System.out.println();
        
        // ====================================================================
        // DEMO 7: Bean Lifecycle
        // ====================================================================
        System.out.println("📌 DEMO 7: Bean Lifecycle (@PostConstruct)");
        System.out.println("-".repeat(70));
        lifecycleBean.doWork();
        System.out.println();
        
        // ====================================================================
        // DEMO 8: @Configuration and @Bean
        // ====================================================================
        System.out.println("📌 DEMO 8: Manual Bean Creation (@Configuration, @Bean)");
        System.out.println("-".repeat(70));
        System.out.println(connectionManager.connect());
        System.out.println();
        
        // ====================================================================
        // DEMO 9: Custom Annotations
        // ====================================================================
        System.out.println("📌 DEMO 9: Custom Annotations");
        System.out.println("-".repeat(70));
        try {
            AnnotationProcessor.processAnnotations(taskService);
        } catch (Exception e) {
            System.out.println("Error processing annotations: " + e.getMessage());
        }
        System.out.println();
        
        // ====================================================================
        // DEMO 10: Listing All Annotations
        // ====================================================================
        System.out.println("📌 DEMO 10: Reflection - Reading Annotations");
        System.out.println("-".repeat(70));
        
        Class<?> serviceClass = OrderService.class;
        if (serviceClass.isAnnotationPresent(Service.class)) {
            System.out.println("✓ OrderService has @Service annotation");
        }
        
        Method[] methods = TaskService.class.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(LogExecutionTime.class)) {
                LogExecutionTime ann = method.getAnnotation(LogExecutionTime.class);
                System.out.println("✓ Method '" + method.getName() + "' has @LogExecutionTime");
                System.out.println("  Value: " + ann.value());
                System.out.println("  Detailed: " + ann.detailed());
            }
        }
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("✅ ANNOTATION DEMONSTRATIONS COMPLETE!");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("KEY TAKEAWAYS:");
        System.out.println("1. Annotations provide metadata about code");
        System.out.println("2. Spring uses annotations to create and manage beans");
        System.out.println("3. @Autowired automatically injects dependencies");
        System.out.println("4. @Primary and @Qualifier handle multiple implementations");
        System.out.println("5. Bean scopes control instance creation");
        System.out.println("6. You can create custom annotations for your needs");
        System.out.println("7. Annotations are processed at compile-time or runtime");
    }
}

// ============================================================================
// ANNOTATION REFERENCE GUIDE
// ============================================================================

/**
 * QUICK REFERENCE:
 * 
 * BEAN CREATION:
 * @Component      - Generic component
 * @Service        - Business logic
 * @Repository     - Data access
 * @Controller     - Web controller
 * @RestController - REST API
 * 
 * DEPENDENCY INJECTION:
 * @Autowired      - Auto-inject dependency
 * @Qualifier      - Specify which bean
 * @Primary        - Default bean
 * @Value           - Inject property value
 * 
 * CONFIGURATION:
 * @Configuration  - Bean definition class
 * @Bean           - Create bean manually
 * @Scope          - Bean scope
 * @Lazy           - Lazy initialization
 * 
 * LIFECYCLE:
 * @PostConstruct  - After bean creation
 * @PreDestroy     - Before bean destruction
 * 
 * JAVA BUILT-IN:
 * @Override       - Method override
 * @Deprecated     - Mark as outdated
 * @SuppressWarnings - Suppress warnings
 */

