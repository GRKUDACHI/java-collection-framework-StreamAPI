/**
 * ============================================================================
 * SIMPLE IOC CONTAINER IMPLEMENTATION - UNDERSTANDING THE INTERNALS
 * ============================================================================
 * 
 * This is a SIMPLIFIED implementation showing how IOC Container works internally.
 * This is NOT Spring's actual code - it's a minimal example to understand concepts.
 * 
 * WHAT THIS DEMONSTRATES:
 * ----------------------
 * 1. How Spring stores bean definitions
 * 2. How Spring creates bean instances
 * 3. How Spring resolves dependencies
 * 4. How Spring injects dependencies
 * 5. Singleton pattern for beans
 * 
 * KEY CONCEPTS:
 * ------------
 * - Bean Registry: Stores bean definitions and instances
 * - Component Scanning: Finding classes with @Component
 * - Dependency Resolution: Building dependency graph
 * - Dependency Injection: Injecting dependencies into beans
 * 
 * IMPORTANT: This is educational. Real Spring Framework is much more complex!
 */

package SpringBoot;

import java.lang.annotation.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

// ============================================================================
// ANNOTATIONS (Simplified versions of Spring annotations)
// ============================================================================

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Component {
    String value() default "";
}

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.CONSTRUCTOR})
@interface Autowired {
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@interface Service {
}

// ============================================================================
// BEAN DEFINITION - Metadata about a bean
// ============================================================================

class BeanDefinition {
    private String beanName;
    private Class<?> beanClass;
    private Object beanInstance;
    private boolean singleton;
    
    public BeanDefinition(String beanName, Class<?> beanClass, boolean singleton) {
        this.beanName = beanName;
        this.beanClass = beanClass;
        this.singleton = singleton;
    }
    
    public String getBeanName() { return beanName; }
    public Class<?> getBeanClass() { return beanClass; }
    public Object getBeanInstance() { return beanInstance; }
    public void setBeanInstance(Object instance) { this.beanInstance = instance; }
    public boolean isSingleton() { return singleton; }
}

// ============================================================================
// SIMPLE IOC CONTAINER - Core Implementation
// ============================================================================

/**
 * This is a simplified version of Spring's ApplicationContext.
 * It demonstrates the core concepts of how IOC Container works.
 */
class SimpleIOCContainer {
    
    // Store bean definitions
    private Map<String, BeanDefinition> beanDefinitions = new HashMap<>();
    
    // Cache for singleton beans (like Spring does)
    private Map<String, Object> singletonBeans = new HashMap<>();
    
    // Track beans currently being created (for circular dependency detection)
    private Set<String> beansInCreation = new HashSet<>();
    
    /**
     * STEP 1: Scan for components (like Spring does)
     * In real Spring, this scans packages specified in @ComponentScan
     */
    public void scanComponents(Class<?>... componentClasses) {
        System.out.println("🔍 Scanning for components...");
        
        for (Class<?> clazz : componentClasses) {
            // Check if class has @Component or @Service annotation
            if (clazz.isAnnotationPresent(Component.class) || 
                clazz.isAnnotationPresent(Service.class)) {
                
                String beanName = getBeanName(clazz);
                
                // Create bean definition
                BeanDefinition beanDef = new BeanDefinition(beanName, clazz, true); // singleton by default
                beanDefinitions.put(beanName, beanDef);
                
                System.out.println("  ✓ Found component: " + beanName + " (" + clazz.getSimpleName() + ")");
            }
        }
    }
    
    /**
     * STEP 2: Initialize all beans (like Spring does on startup)
     */
    public void initializeBeans() {
        System.out.println();
        System.out.println("🚀 Initializing beans...");
        
        // Create all singleton beans
        for (String beanName : beanDefinitions.keySet()) {
            BeanDefinition beanDef = beanDefinitions.get(beanName);
            if (beanDef.isSingleton()) {
                getBean(beanName);  // This will create the bean if not exists
            }
        }
    }
    
    /**
     * STEP 3: Get bean (core IOC method - like Spring's getBean())
     */
    public <T> T getBean(Class<T> clazz) {
        String beanName = getBeanName(clazz);
        return (T) getBean(beanName);
    }
    
    public Object getBean(String beanName) {
        BeanDefinition beanDef = beanDefinitions.get(beanName);
        if (beanDef == null) {
            throw new RuntimeException("Bean not found: " + beanName);
        }
        
        // If singleton and already created, return cached instance
        if (beanDef.isSingleton() && singletonBeans.containsKey(beanName)) {
            return singletonBeans.get(beanName);
        }
        
        // Create new instance
        return createBean(beanName, beanDef);
    }
    
    /**
     * STEP 4: Create bean instance
     * This is where the magic happens - creating object and injecting dependencies
     */
    private Object createBean(String beanName, BeanDefinition beanDef) {
        // Detect circular dependencies
        if (beansInCreation.contains(beanName)) {
            throw new RuntimeException("Circular dependency detected: " + beanName);
        }
        
        beansInCreation.add(beanName);
        
        try {
            Class<?> clazz = beanDef.getBeanClass();
            
            System.out.println("  📦 Creating bean: " + beanName);
            
            // Find constructor with @Autowired or default constructor
            Constructor<?> constructor = findAutowiredConstructor(clazz);
            if (constructor == null) {
                constructor = clazz.getDeclaredConstructor();
            }
            
            constructor.setAccessible(true);
            
            // Get constructor parameters (dependencies)
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] paramValues = new Object[paramTypes.length];
            
            // Resolve each dependency
            for (int i = 0; i < paramTypes.length; i++) {
                paramValues[i] = getBean(paramTypes[i]);
                System.out.println("    ↳ Injecting dependency: " + paramTypes[i].getSimpleName());
            }
            
            // Create instance
            Object instance = constructor.newInstance(paramValues);
            
            // Inject field dependencies
            injectFieldDependencies(instance);
            
            // Cache singleton
            if (beanDef.isSingleton()) {
                singletonBeans.put(beanName, instance);
            }
            
            beansInCreation.remove(beanName);
            return instance;
            
        } catch (Exception e) {
            beansInCreation.remove(beanName);
            throw new RuntimeException("Failed to create bean: " + beanName, e);
        }
    }
    
    /**
     * STEP 5: Find constructor for dependency injection
     */
    private Constructor<?> findAutowiredConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }
        // If no @Autowired constructor, check if there's a constructor with parameters
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() > 0) {
                return constructor;
            }
        }
        return null;
    }
    
    /**
     * STEP 6: Inject field dependencies (for @Autowired fields)
     */
    private void injectFieldDependencies(Object instance) {
        Class<?> clazz = instance.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                
                try {
                    Object dependency = getBean(field.getType());
                    field.set(instance, dependency);
                    System.out.println("    ↳ Field injection: " + field.getName() + " = " + 
                                      field.getType().getSimpleName());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to inject field: " + field.getName(), e);
                }
            }
        }
    }
    
    /**
     * Generate bean name from class
     */
    private String getBeanName(Class<?> clazz) {
        String className = clazz.getSimpleName();
        // Convert MyService to myService (camelCase)
        return Character.toLowerCase(className.charAt(0)) + className.substring(1);
    }
    
    /**
     * Get all bean names (for debugging)
     */
    public Set<String> getBeanNames() {
        return beanDefinitions.keySet();
    }
}

// ============================================================================
// EXAMPLE CLASSES TO DEMONSTRATE IOC
// ============================================================================

@Component
class UserRepository {
    public UserRepository() {
        System.out.println("    ✓ UserRepository constructor called");
    }
    
    public String findUser(int id) {
        return "User " + id;
    }
}

@Service
class UserService {
    private UserRepository userRepository;
    
    // Constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("    ✓ UserService constructor called with UserRepository injected");
    }
    
    public String getUserInfo(int id) {
        return userRepository.findUser(id);
    }
}

@Service
class OrderService {
    @Autowired
    private UserService userService;  // Field injection
    
    public OrderService() {
        System.out.println("    ✓ OrderService constructor called");
    }
    
    public String getOrderForUser(int userId) {
        return "Order for " + userService.getUserInfo(userId);
    }
}

// ============================================================================
// DEMONSTRATION
// ============================================================================

public class SimpleIOCContainerImplementation {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(70));
        System.out.println("🎓 SIMPLE IOC CONTAINER IMPLEMENTATION");
        System.out.println("   (Educational - Shows how Spring IOC works internally)");
        System.out.println("=".repeat(70));
        System.out.println();
        
        // Create our simple IOC container
        SimpleIOCContainer container = new SimpleIOCContainer();
        
        // Step 1: Scan for components (Spring does this automatically)
        System.out.println("STEP 1: Component Scanning");
        System.out.println("-".repeat(70));
        container.scanComponents(UserRepository.class, UserService.class, OrderService.class);
        
        // Step 2: Initialize all beans (Spring does this on startup)
        System.out.println();
        System.out.println("STEP 2: Bean Initialization");
        System.out.println("-".repeat(70));
        container.initializeBeans();
        
        // Step 3: Use the beans
        System.out.println();
        System.out.println("STEP 3: Using Beans");
        System.out.println("-".repeat(70));
        
        UserService userService = container.getBean(UserService.class);
        System.out.println("✓ Got UserService from container");
        System.out.println("Result: " + userService.getUserInfo(123));
        System.out.println();
        
        OrderService orderService = container.getBean(OrderService.class);
        System.out.println("✓ Got OrderService from container");
        System.out.println("Result: " + orderService.getOrderForUser(456));
        System.out.println();
        
        // Step 4: Demonstrate singleton pattern
        System.out.println("STEP 4: Singleton Pattern Demonstration");
        System.out.println("-".repeat(70));
        UserService userService1 = container.getBean(UserService.class);
        UserService userService2 = container.getBean(UserService.class);
        System.out.println("userService1 == userService2? " + (userService1 == userService2));
        System.out.println("✓ Same instance returned (singleton pattern)");
        System.out.println();
        
        // Step 5: List all beans
        System.out.println("STEP 5: All Managed Beans");
        System.out.println("-".repeat(70));
        System.out.println("Beans managed by container: " + container.getBeanNames());
        System.out.println();
        
        System.out.println("=".repeat(70));
        System.out.println("✅ IOC CONTAINER DEMONSTRATION COMPLETE!");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("KEY LEARNINGS:");
        System.out.println("1. Container scans for @Component/@Service annotations");
        System.out.println("2. Container creates bean definitions (metadata)");
        System.out.println("3. Container creates bean instances when needed");
        System.out.println("4. Container resolves dependencies automatically");
        System.out.println("5. Container injects dependencies via constructor/fields");
        System.out.println("6. Singleton beans are cached and reused");
        System.out.println();
        System.out.println("NOTE: Real Spring Framework is much more sophisticated!");
        System.out.println("- Handles circular dependencies");
        System.out.println("- Supports multiple bean scopes");
        System.out.println("- Has BeanPostProcessors for customization");
        System.out.println("- Supports AOP, transaction management, etc.");
    }
}

// ============================================================================
// COMPARISON: Simple Implementation vs Spring Framework
// ============================================================================

/**
 * OUR SIMPLE IMPLEMENTATION:
 * - Basic component scanning
 * - Simple dependency injection
 * - Singleton pattern only
 * - No circular dependency handling (basic detection only)
 * 
 * SPRING FRAMEWORK:
 * - Advanced component scanning with filters
 * - Multiple dependency injection methods
 * - Multiple bean scopes (singleton, prototype, request, session)
 * - Advanced circular dependency resolution
 * - BeanPostProcessors for customization
 * - AOP (Aspect-Oriented Programming) support
 * - Transaction management
 * - Event handling
 * - Profile support
 * - Conditional bean creation
 * - Much more!
 */

