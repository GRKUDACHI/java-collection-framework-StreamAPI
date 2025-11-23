/**
 * ============================================================================
 * SPRING BOOT IOC CONTAINER - ADVANCED TUTORIAL
 * ============================================================================
 * 
 * This tutorial demonstrates advanced IOC features in Spring Boot:
 * - Bean Scopes (Singleton, Prototype)
 * - @Primary and @Qualifier for Multiple Implementations
 * - Conditional Bean Creation
 * - Lazy Initialization
 * - Profile-based Beans
 * - Custom Bean Post Processors
 * 
 * These features give you fine-grained control over how Spring manages your beans.
 */

package SpringBoot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

// ============================================================================
// ADVANCED FEATURE 1: BEAN SCOPES
// ============================================================================

/**
 * BEAN SCOPES:
 * - singleton (default): One instance for entire application
 * - prototype: New instance every time bean is requested
 * - request: One per HTTP request (web apps)
 * - session: One per HTTP session (web apps)
 */

@Component
@Scope("singleton")  // This is the default
class SingletonBean {
    private static int instanceCount = 0;
    
    public SingletonBean() {
        instanceCount++;
        System.out.println("✓ SingletonBean instance #" + instanceCount + " created");
    }
    
    public int getInstanceCount() {
        return instanceCount;
    }
}

@Component
@Scope("prototype")  // New instance each time
class PrototypeBean {
    private static int instanceCount = 0;
    private int instanceId;
    
    public PrototypeBean() {
        instanceId = ++instanceCount;
        System.out.println("✓ PrototypeBean instance #" + instanceId + " created");
    }
    
    public int getInstanceId() {
        return instanceId;
    }
}

// ============================================================================
// ADVANCED FEATURE 2: MULTIPLE IMPLEMENTATIONS - @Primary and @Qualifier
// ============================================================================

/**
 * When you have multiple implementations of the same interface,
 * Spring needs to know which one to inject.
 */

interface PaymentService {
    String processPayment(double amount);
}

@Service
@Primary  // This will be injected by default when multiple implementations exist
class CreditCardPaymentService implements PaymentService {
    public String processPayment(double amount) {
        return "Processing $" + amount + " via Credit Card";
    }
}

@Service
@Qualifier("paypal")  // Named bean
class PayPalPaymentService implements PaymentService {
    public String processPayment(double amount) {
        return "Processing $" + amount + " via PayPal";
    }
}

@Service
@Qualifier("bitcoin")
class BitcoinPaymentService implements PaymentService {
    public String processPayment(double amount) {
        return "Processing $" + amount + " via Bitcoin";
    }
}

// Service that uses @Primary bean (default)
@Service
class OrderService {
    @Autowired
    private PaymentService paymentService;  // Gets CreditCardPaymentService (@Primary)
    
    public String placeOrder(double amount) {
        return paymentService.processPayment(amount);
    }
}

// Service that uses @Qualifier to specify which bean
@Service
class CryptoOrderService {
    @Autowired
    @Qualifier("bitcoin")  // Explicitly request BitcoinPaymentService
    private PaymentService paymentService;
    
    public String placeCryptoOrder(double amount) {
        return paymentService.processPayment(amount);
    }
}

// ============================================================================
// ADVANCED FEATURE 3: CONDITIONAL BEAN CREATION
// ============================================================================

/**
 * Create beans only when certain conditions are met.
 * Useful for feature flags, environment-specific beans, etc.
 */

interface DatabaseConnection {
    String connect();
}

@Component
@ConditionalOnProperty(name = "database.type", havingValue = "mysql", matchIfMissing = false)
class MySQLConnection implements DatabaseConnection {
    public String connect() {
        return "Connected to MySQL database";
    }
}

@Component
@ConditionalOnProperty(name = "database.type", havingValue = "postgresql")
class PostgreSQLConnection implements DatabaseConnection {
    public String connect() {
        return "Connected to PostgreSQL database";
    }
}

@Component
@ConditionalOnProperty(name = "database.type", havingValue = "mongodb")
class MongoDBConnection implements DatabaseConnection {
    public String connect() {
        return "Connected to MongoDB";
    }
}

// ============================================================================
// ADVANCED FEATURE 4: LAZY INITIALIZATION
// ============================================================================

/**
 * @Lazy: Bean is created only when first accessed, not at application startup.
 * Useful for expensive beans that might not always be used.
 */

@Component
@Lazy
class ExpensiveService {
    public ExpensiveService() {
        System.out.println("⚠️ Creating expensive service... (This happens only when first accessed)");
        // Simulate expensive initialization
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    public String doExpensiveWork() {
        return "Expensive work completed!";
    }
}

// ============================================================================
// ADVANCED FEATURE 5: PROFILE-BASED BEANS
// ============================================================================

/**
 * Create beans based on active profiles (dev, test, prod).
 * Useful for environment-specific configurations.
 */

interface EmailService {
    String sendEmail(String to, String subject);
}

@Component
@Profile("dev")  // Only created when "dev" profile is active
class MockEmailService implements EmailService {
    public String sendEmail(String to, String subject) {
        return "[MOCK] Email sent to " + to + ": " + subject;
    }
}

@Component
@Profile("prod")  // Only created when "prod" profile is active
class ProductionEmailService implements EmailService {
    public String sendEmail(String to, String subject) {
        return "[PROD] Email sent to " + to + ": " + subject;
    }
}

// ============================================================================
// ADVANCED FEATURE 6: BEAN POST PROCESSOR
// ============================================================================

/**
 * BeanPostProcessor allows you to intercept bean creation.
 * You can modify beans before/after initialization.
 */

@Component
@Order(1)  // Order of execution (lower number = earlier)
class LoggingBeanPostProcessor implements BeanPostProcessor {
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        System.out.println("  🔍 Before init: " + beanName + " (" + bean.getClass().getSimpleName() + ")");
        return bean;
    }
    
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("  ✅ After init: " + beanName + " (" + bean.getClass().getSimpleName() + ")");
        return bean;
    }
}

// ============================================================================
// ADVANCED FEATURE 7: CONFIGURATION PROPERTIES
// ============================================================================

/**
 * Inject external configuration into beans.
 */

@Configuration
@ConfigurationProperties(prefix = "app")
class AppConfig {
    private String name;
    private String version;
    private boolean enabled;
    
    // Getters and setters required
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public String getInfo() {
        return "App: " + name + " v" + version + " (enabled: " + enabled + ")";
    }
}

// ============================================================================
// MAIN APPLICATION - DEMONSTRATING ADVANCED FEATURES
// ============================================================================

@SpringBootApplication
public class IOCContainerAdvancedDemo implements CommandLineRunner {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private SingletonBean singletonBean1;
    
    @Autowired
    private SingletonBean singletonBean2;  // Same instance as singletonBean1
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CryptoOrderService cryptoOrderService;
    
    @Autowired(required = false)  // Optional - won't fail if bean doesn't exist
    private ExpensiveService expensiveService;
    
    @Autowired(required = false)
    private EmailService emailService;
    
    @Autowired(required = false)
    private DatabaseConnection databaseConnection;
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("🚀 SPRING BOOT IOC CONTAINER - ADVANCED FEATURES");
        System.out.println("=".repeat(70));
        System.out.println();
        
        SpringApplication.run(IOCContainerAdvancedDemo.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println();
        System.out.println("=".repeat(70));
        System.out.println("📦 ADVANCED IOC CONTAINER FEATURES");
        System.out.println("=".repeat(70));
        System.out.println();
        
        // ====================================================================
        // DEMO 1: Bean Scopes
        // ====================================================================
        System.out.println("📌 DEMO 1: Bean Scopes (Singleton vs Prototype)");
        System.out.println("-".repeat(70));
        System.out.println("Singleton beans (same instance):");
        System.out.println("  Bean1 instance count: " + singletonBean1.getInstanceCount());
        System.out.println("  Bean2 instance count: " + singletonBean2.getInstanceCount());
        System.out.println("  Same instance? " + (singletonBean1 == singletonBean2));
        System.out.println();
        
        System.out.println("Prototype beans (new instance each time):");
        PrototypeBean proto1 = applicationContext.getBean(PrototypeBean.class);
        PrototypeBean proto2 = applicationContext.getBean(PrototypeBean.class);
        System.out.println("  Proto1 ID: " + proto1.getInstanceId());
        System.out.println("  Proto2 ID: " + proto2.getInstanceId());
        System.out.println("  Same instance? " + (proto1 == proto2));
        System.out.println();
        
        // ====================================================================
        // DEMO 2: @Primary and @Qualifier
        // ====================================================================
        System.out.println("📌 DEMO 2: Multiple Implementations (@Primary vs @Qualifier)");
        System.out.println("-".repeat(70));
        System.out.println("OrderService uses @Primary: " + orderService.placeOrder(100.0));
        System.out.println("CryptoOrderService uses @Qualifier: " + cryptoOrderService.placeCryptoOrder(100.0));
        System.out.println();
        
        // ====================================================================
        // DEMO 3: Lazy Initialization
        // ====================================================================
        System.out.println("📌 DEMO 3: Lazy Initialization");
        System.out.println("-".repeat(70));
        System.out.println("ExpensiveService is @Lazy - not created yet.");
        if (expensiveService != null) {
            System.out.println("Now accessing it...");
            System.out.println(expensiveService.doExpensiveWork());
        } else {
            System.out.println("ExpensiveService not available (not lazy-loaded yet)");
        }
        System.out.println();
        
        // ====================================================================
        // DEMO 4: Conditional Beans
        // ====================================================================
        System.out.println("📌 DEMO 4: Conditional Bean Creation");
        System.out.println("-".repeat(70));
        if (databaseConnection != null) {
            System.out.println("Database connection: " + databaseConnection.connect());
        } else {
            System.out.println("No database connection bean found (check application.properties for database.type)");
        }
        System.out.println();
        
        // ====================================================================
        // DEMO 5: Profile-based Beans
        // ====================================================================
        System.out.println("📌 DEMO 5: Profile-based Beans");
        System.out.println("-".repeat(70));
        String[] activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if (activeProfiles.length > 0) {
            System.out.println("Active profiles: " + String.join(", ", activeProfiles));
        } else {
            System.out.println("No active profiles (default)");
        }
        if (emailService != null) {
            System.out.println("Email service: " + emailService.sendEmail("user@example.com", "Hello"));
        } else {
            System.out.println("No email service bean found (check active profiles: dev or prod)");
        }
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("✅ ADVANCED IOC FEATURES DEMONSTRATION COMPLETE!");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("KEY TAKEAWAYS:");
        System.out.println("1. Use @Scope to control bean lifecycle (singleton/prototype)");
        System.out.println("2. Use @Primary for default bean when multiple exist");
        System.out.println("3. Use @Qualifier to specify which bean to inject");
        System.out.println("4. Use @Lazy for expensive beans not needed at startup");
        System.out.println("5. Use @ConditionalOnProperty for feature flags");
        System.out.println("6. Use @Profile for environment-specific beans");
        System.out.println("7. BeanPostProcessor allows intercepting bean creation");
    }
}

// ============================================================================
// USAGE NOTES
// ============================================================================

/**
 * TO USE CONDITIONAL BEANS:
 * Add to application.properties:
 *   database.type=mysql
 * 
 * TO USE PROFILE-BASED BEANS:
 * Run with: --spring.profiles.active=dev
 * Or add to application.properties: spring.profiles.active=dev
 * 
 * TO USE CONFIGURATION PROPERTIES:
 * Add to application.properties:
 *   app.name=MyApp
 *   app.version=1.0.0
 *   app.enabled=true
 */

