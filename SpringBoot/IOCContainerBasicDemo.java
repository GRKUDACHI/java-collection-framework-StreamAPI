/**
 * ============================================================================
 * SPRING BOOT IOC CONTAINER - BASIC TUTORIAL
 * ============================================================================
 * 
 * This tutorial demonstrates the fundamental concepts of IOC (Inversion of Control)
 * Container in Spring Boot with practical examples.
 * 
 * WHAT IS IOC?
 * ------------
 * IOC (Inversion of Control) is a design pattern where the control of object
 * creation and dependency management is transferred from application code to
 * a framework (Spring Container).
 * 
 * KEY BENEFITS:
 * 1. Loose Coupling - Classes don't create their own dependencies
 * 2. Easy Testing - Can easily mock dependencies
 * 3. Better Maintainability - Changes in one place don't affect others
 * 4. Automatic Dependency Resolution - Spring handles all wiring
 * 
 * HOW IT WORKS:
 * ------------
 * 1. Spring scans for components (@Component, @Service, @Repository, @Controller)
 * 2. Creates bean definitions (metadata about objects)
 * 3. Instantiates beans (creates objects)
 * 4. Resolves and injects dependencies automatically
 * 5. Manages bean lifecycle (creation, initialization, destruction)
 * 
 * TO RUN THIS:
 * -----------
 * Add Spring Boot dependencies to your pom.xml or build.gradle, then run:
 * java IOCContainerBasicDemo
 * 
 * Note: This is a simplified demonstration. In real Spring Boot apps,
 * you'll use @SpringBootApplication and SpringApplication.run()
 */

package SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

// ============================================================================
// EXAMPLE 1: SIMPLE COMPONENT (Bean)
// ============================================================================

/**
 * @Component tells Spring: "Hey Spring, manage this class as a bean!"
 * Spring will create an instance of this class and store it in the IOC container.
 */
@Component
class MessageService {
    public String getMessage() {
        return "Hello from IOC Container!";
    }
}

// ============================================================================
// EXAMPLE 2: SERVICE WITH DEPENDENCY INJECTION
// ============================================================================

/**
 * @Service is a specialization of @Component for business logic.
 * @Autowired tells Spring: "Automatically inject MessageService here"
 * 
 * WITHOUT IOC (Traditional Way):
 * MessageService messageService = new MessageService();
 * GreetingService greetingService = new GreetingService();
 * greetingService.setMessageService(messageService);
 * 
 * WITH IOC (Spring Way):
 * Spring automatically creates both and injects MessageService into GreetingService
 */
@Service
class GreetingService {
    @Autowired
    private MessageService messageService;
    
    public String greet(String name) {
        return messageService.getMessage() + " Welcome, " + name + "!";
    }
}

// ============================================================================
// EXAMPLE 3: CONSTRUCTOR INJECTION (RECOMMENDED APPROACH)
// ============================================================================

/**
 * Constructor injection is PREFERRED because:
 * 1. Ensures all dependencies are provided (can't create object without them)
 * 2. Makes dependencies explicit
 * 3. Easier to test (can pass mock objects)
 * 4. Immutable (can make fields final)
 */
@Service
class UserService {
    private final UserRepository userRepository;
    
    // Spring automatically calls this constructor and injects UserRepository
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("✓ UserService created with UserRepository injected!");
    }
    
    public String getUserInfo(int userId) {
        return userRepository.findById(userId);
    }
}

// ============================================================================
// EXAMPLE 4: REPOSITORY LAYER
// ============================================================================

/**
 * @Repository is for data access layer.
 * Spring also provides exception translation for data access.
 */
@Repository
interface UserRepository {
    String findById(int id);
}

@Component
class InMemoryUserRepository implements UserRepository {
    public String findById(int id) {
        return "User " + id + " from database";
    }
}

// ============================================================================
// EXAMPLE 5: MANUAL BEAN CREATION USING @Configuration
// ============================================================================

/**
 * @Configuration indicates this class contains bean definitions.
 * @Bean methods create beans manually when you need more control.
 */
@Configuration
class AppConfig {
    
    @Bean
    public Calculator calculator() {
        System.out.println("✓ Creating Calculator bean...");
        return new Calculator();
    }
    
    @Bean
    public MathService mathService(Calculator calculator) {
        // Spring automatically passes the Calculator bean to this method
        System.out.println("✓ Creating MathService bean with Calculator injected...");
        return new MathService(calculator);
    }
}

class Calculator {
    public int add(int a, int b) {
        return a + b;
    }
    
    public int multiply(int a, int b) {
        return a * b;
    }
}

class MathService {
    private final Calculator calculator;
    
    public MathService(Calculator calculator) {
        this.calculator = calculator;
    }
    
    public int calculate(int a, int b) {
        return calculator.multiply(calculator.add(a, b), 2);
    }
}

// ============================================================================
// EXAMPLE 6: DEMONSTRATING IOC CONTAINER LIFECYCLE
// ============================================================================

@Component
class LifecycleDemo {
    
    public LifecycleDemo() {
        System.out.println("1️⃣ Constructor called - Bean instance created");
    }
    
    @PostConstruct
    public void init() {
        System.out.println("2️⃣ @PostConstruct called - Bean initialized");
    }
    
    public void doWork() {
        System.out.println("3️⃣ Bean is being used");
    }
    
    @PreDestroy
    public void cleanup() {
        System.out.println("4️⃣ @PreDestroy called - Bean being destroyed");
    }
}

// ============================================================================
// MAIN APPLICATION - DEMONSTRATING IOC IN ACTION
// ============================================================================

@SpringBootApplication
public class IOCContainerBasicDemo implements CommandLineRunner {
    
    @Autowired
    private GreetingService greetingService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private MathService mathService;
    
    @Autowired
    private LifecycleDemo lifecycleDemo;
    
    @Autowired
    private ApplicationContext applicationContext;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("🚀 SPRING BOOT IOC CONTAINER - BASIC DEMONSTRATION");
        System.out.println("=".repeat(70));
        System.out.println();
        
        // Spring Boot automatically:
        // 1. Scans for @Component, @Service, @Repository, @Controller
        // 2. Creates beans
        // 3. Resolves dependencies
        // 4. Injects them where needed
        SpringApplication.run(IOCContainerBasicDemo.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("📦 IOC CONTAINER DEMONSTRATION");
        System.out.println("=".repeat(70));
        System.out.println();
        
        // ====================================================================
        // DEMO 1: Dependency Injection in Action
        // ====================================================================
        System.out.println("📌 DEMO 1: Dependency Injection");
        System.out.println("-".repeat(70));
        System.out.println("GreetingService automatically received MessageService!");
        System.out.println("Result: " + greetingService.greet("John"));
        System.out.println();
        
        // ====================================================================
        // DEMO 2: Constructor Injection
        // ====================================================================
        System.out.println("📌 DEMO 2: Constructor Injection");
        System.out.println("-".repeat(70));
        System.out.println("UserService created with UserRepository via constructor!");
        System.out.println("Result: " + userService.getUserInfo(123));
        System.out.println();
        
        // ====================================================================
        // DEMO 3: Manual Bean Creation
        // ====================================================================
        System.out.println("📌 DEMO 3: Manual Bean Creation via @Configuration");
        System.out.println("-".repeat(70));
        System.out.println("MathService uses Calculator bean created manually");
        System.out.println("Result: ((5 + 3) * 2) = " + mathService.calculate(5, 3));
        System.out.println();
        
        // ====================================================================
        // DEMO 4: Accessing Beans from Container
        // ====================================================================
        System.out.println("📌 DEMO 4: Getting Beans from IOC Container");
        System.out.println("-".repeat(70));
        
        // Get bean by type
        GreetingService service1 = applicationContext.getBean(GreetingService.class);
        System.out.println("✓ Got GreetingService from container: " + service1);
        
        // Get bean by name
        GreetingService service2 = (GreetingService) applicationContext.getBean("greetingService");
        System.out.println("✓ Got GreetingService by name: " + service2);
        
        // Check if same instance (Singleton by default)
        System.out.println("✓ Are they the same instance? " + (service1 == service2));
        System.out.println();
        
        // ====================================================================
        // DEMO 5: Bean Lifecycle
        // ====================================================================
        System.out.println("📌 DEMO 5: Bean Lifecycle Methods");
        System.out.println("-".repeat(70));
        lifecycleDemo.doWork();
        System.out.println();
        
        // ====================================================================
        // DEMO 6: Listing All Beans
        // ====================================================================
        System.out.println("📌 DEMO 6: All Beans in IOC Container");
        System.out.println("-".repeat(70));
        String[] beanNames = applicationContext.getBeanDefinitionNames();
        System.out.println("Total beans managed by Spring: " + beanNames.length);
        System.out.println("Some example beans:");
        int count = 0;
        for (String beanName : beanNames) {
            if (!beanName.startsWith("org.springframework")) {
                System.out.println("  • " + beanName + " (" + 
                    applicationContext.getBean(beanName).getClass().getSimpleName() + ")");
                if (++count >= 10) break;
            }
        }
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("✅ IOC CONTAINER DEMONSTRATION COMPLETE!");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("KEY TAKEAWAYS:");
        System.out.println("1. Spring automatically creates and manages objects (beans)");
        System.out.println("2. Dependencies are automatically injected");
        System.out.println("3. Beans are singletons by default (one instance shared)");
        System.out.println("4. Constructor injection is preferred");
        System.out.println("5. Spring manages entire lifecycle of beans");
    }
}

// ============================================================================
// COMPARISON: WITH vs WITHOUT IOC
// ============================================================================

/**
 * WITHOUT IOC (Traditional Approach):
 * 
 * public class TraditionalApp {
 *     public static void main(String[] args) {
 *         // Manual creation - tightly coupled!
 *         MessageService messageService = new MessageService();
 *         GreetingService greetingService = new GreetingService();
 *         greetingService.setMessageService(messageService);
 *         
 *         // Problems:
 *         // - Hard to test (can't easily swap MessageService)
 *         // - Manual management
 *         // - Tight coupling
 *     }
 * }
 * 
 * WITH IOC (Spring Approach):
 * 
 * @SpringBootApplication
 * public class SpringApp {
 *     @Autowired
 *     private GreetingService greetingService;  // Spring injects automatically!
 *     
 *     // Benefits:
 *     // - Easy to test (can mock dependencies)
 *     // - Automatic management
 *     // - Loose coupling
 * }
 */

