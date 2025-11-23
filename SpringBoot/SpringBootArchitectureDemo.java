/**
 * Spring Boot Architecture Demo
 * 
 * This file demonstrates the key concepts of Spring Boot architecture:
 * 1. Application startup flow
 * 2. Component scanning
 * 3. Dependency injection
 * 4. Bean lifecycle
 * 5. Request flow (conceptual)
 * 
 * Note: This is a conceptual demonstration. To run a real Spring Boot app,
 * you need proper project structure with pom.xml or build.gradle
 */

package SpringBoot;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * ============================================
 * 1. MAIN APPLICATION CLASS
 * ============================================
 * This is the entry point of Spring Boot application
 * @SpringBootApplication combines:
 * - @Configuration: Marks as configuration class
 * - @EnableAutoConfiguration: Enables auto-configuration
 * - @ComponentScan: Scans for components
 */
@SpringBootApplication
public class SpringBootArchitectureDemo implements CommandLineRunner {
    
    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private UserService userService;
    
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("SPRING BOOT APPLICATION STARTUP FLOW");
        System.out.println("============================================");
        System.out.println("\nStep 1: JVM starts");
        System.out.println("Step 2: main() method called");
        System.out.println("Step 3: SpringApplication.run() invoked");
        System.out.println("\n--- Starting Spring Boot Application ---\n");
        
        // This single line does everything:
        // 1. Creates ApplicationContext
        // 2. Scans for components
        // 3. Creates beans
        // 4. Injects dependencies
        // 5. Starts embedded server (if web app)
        SpringApplication.run(SpringBootArchitectureDemo.class, args);
    }
    
    /**
     * CommandLineRunner runs after application context is fully initialized
     * This demonstrates the application is ready
     */
    @Override
    public void run(String... args) {
        System.out.println("\n============================================");
        System.out.println("APPLICATION READY - CommandLineRunner");
        System.out.println("============================================");
        System.out.println("All beans are created and ready!");
        System.out.println("Total beans in context: " + applicationContext.getBeanDefinitionCount());
        
        // Demonstrate dependency injection working
        System.out.println("\n--- Testing Dependency Injection ---");
        User user = userService.findUserById(1L);
        System.out.println("User found: " + user);
        
        System.out.println("\n--- Application is running ---");
        System.out.println("For web apps, server would be listening on port 8080");
    }
}

/**
 * ============================================
 * 2. REPOSITORY LAYER (Data Access)
 * ============================================
 * @Repository marks this as data access component
 * Spring automatically creates a bean and manages its lifecycle
 */
@Repository
class UserRepository implements BeanNameAware, InitializingBean {
    
    private String beanName;
    
    /**
     * Step 1: Constructor called during bean instantiation
     */
    public UserRepository() {
        System.out.println("\n[UserRepository] Step 1: Constructor called");
    }
    
    /**
     * Step 2: BeanNameAware.setBeanName() called
     */
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("[UserRepository] Step 2: Bean name set: " + name);
    }
    
    /**
     * Step 3: InitializingBean.afterPropertiesSet() called
     */
    @Override
    public void afterPropertiesSet() {
        System.out.println("[UserRepository] Step 3: afterPropertiesSet() called");
    }
    
    /**
     * Step 4: @PostConstruct method called
     */
    @PostConstruct
    public void init() {
        System.out.println("[UserRepository] Step 4: @PostConstruct init() called");
    }
    
    /**
     * Called during application shutdown
     */
    @PreDestroy
    public void cleanup() {
        System.out.println("[UserRepository] @PreDestroy cleanup() called");
    }
    
    // Simulated database operation
    public User findById(Long id) {
        System.out.println("[UserRepository] Executing: SELECT * FROM users WHERE id = " + id);
        return new User(id, "John Doe", "john@example.com");
    }
    
    public void save(User user) {
        System.out.println("[UserRepository] Executing: INSERT INTO users ...");
    }
}

/**
 * ============================================
 * 3. SERVICE LAYER (Business Logic)
 * ============================================
 * @Service marks this as business logic component
 * @Autowired injects UserRepository automatically
 */
@Service
class UserService implements ApplicationContextAware {
    
    // Field injection (less preferred, but shown for demonstration)
    @Autowired
    private UserRepository userRepository;
    
    private ApplicationContext applicationContext;
    
    /**
     * Constructor injection (preferred method)
     * Spring automatically injects UserRepository
     */
    public UserService(UserRepository userRepository) {
        System.out.println("\n[UserService] Step 1: Constructor called");
        System.out.println("[UserService] UserRepository injected via constructor");
        // Note: In real code, you'd use constructor parameter, not field injection
    }
    
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.applicationContext = context;
        System.out.println("[UserService] Step 2: ApplicationContext set");
    }
    
    @PostConstruct
    public void init() {
        System.out.println("[UserService] Step 3: @PostConstruct init() called");
    }
    
    // Business logic method
    public User findUserById(Long id) {
        System.out.println("\n[UserService] Business logic: Finding user with id: " + id);
        // Delegates to repository layer
        return userRepository.findById(id);
    }
    
    public void createUser(User user) {
        System.out.println("[UserService] Business logic: Creating user");
        userRepository.save(user);
    }
}

/**
 * ============================================
 * 4. CONTROLLER LAYER (Web/API)
 * ============================================
 * @RestController = @Controller + @ResponseBody
 * Handles HTTP requests
 * 
 * Note: This won't work without spring-boot-starter-web dependency
 * This is shown for conceptual understanding
 */
@RestController
class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * HTTP Request Flow:
     * 1. Client sends: GET /api/users/1
     * 2. DispatcherServlet receives request
     * 3. HandlerMapping finds this method
     * 4. HandlerInterceptor.preHandle() runs
     * 5. This method executes
     * 6. Response converted to JSON
     * 7. Sent back to client
     */
    @GetMapping("/api/users/{id}")
    public User getUser(Long id) {
        System.out.println("\n[UserController] Handling GET /api/users/" + id);
        return userService.findUserById(id);
    }
    
    @GetMapping("/api/users")
    public String getAllUsers() {
        System.out.println("[UserController] Handling GET /api/users");
        return "List of users";
    }
}

/**
 * ============================================
 * 5. DOMAIN MODEL (Entity)
 * ============================================
 */
class User {
    private Long id;
    private String name;
    private String email;
    
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

/**
 * ============================================
 * 6. DEMONSTRATION OF BEAN LIFECYCLE
 * ============================================
 */
@Component
class LifecycleDemoBean implements 
    BeanNameAware,
    ApplicationContextAware,
    InitializingBean {
    
    private String beanName;
    private ApplicationContext context;
    
    public LifecycleDemoBean() {
        System.out.println("\n[LifecycleDemoBean] 1. Constructor called");
    }
    
    @Override
    public void setBeanName(String name) {
        this.beanName = name;
        System.out.println("[LifecycleDemoBean] 2. setBeanName(): " + name);
    }
    
    @Override
    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
        System.out.println("[LifecycleDemoBean] 3. setApplicationContext() called");
    }
    
    @PostConstruct
    public void postConstruct() {
        System.out.println("[LifecycleDemoBean] 4. @PostConstruct called");
    }
    
    @Override
    public void afterPropertiesSet() {
        System.out.println("[LifecycleDemoBean] 5. afterPropertiesSet() called");
    }
    
    @PreDestroy
    public void preDestroy() {
        System.out.println("[LifecycleDemoBean] 6. @PreDestroy called (on shutdown)");
    }
}

/**
 * ============================================
 * ARCHITECTURE SUMMARY
 * ============================================
 * 
 * SPRING BOOT ARCHITECTURE LAYERS:
 * 
 * 1. CONTROLLER LAYER (@RestController, @Controller)
 *    - Handles HTTP requests
 *    - Maps URLs to methods
 *    - Returns responses
 * 
 * 2. SERVICE LAYER (@Service)
 *    - Contains business logic
 *    - Orchestrates operations
 *    - Transactional boundaries
 * 
 * 3. REPOSITORY LAYER (@Repository)
 *    - Data access
 *    - Database operations
 *    - Entity management
 * 
 * 4. DOMAIN MODEL
 *    - Entities/POJOs
 *    - Business objects
 * 
 * DEPENDENCY INJECTION FLOW:
 * UserController → UserService → UserRepository
 * 
 * BEAN CREATION ORDER:
 * 1. Repository (no dependencies)
 * 2. Service (depends on Repository)
 * 3. Controller (depends on Service)
 * 
 * REQUEST FLOW (for web apps):
 * HTTP Request → DispatcherServlet → HandlerMapping → 
 * Controller → Service → Repository → Database → 
 * Response (JSON/XML) → Client
 * 
 * STARTUP FLOW:
 * main() → SpringApplication.run() → 
 * Create ApplicationContext → 
 * Component Scanning → 
 * Bean Creation → 
 * Dependency Injection → 
 * @PostConstruct → 
 * Start Server → 
 * Application Ready
 */


