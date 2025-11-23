/**
 * Microservices Architecture Demo
 * 
 * This file demonstrates key microservices concepts:
 * 1. Service Discovery (Eureka Client)
 * 2. API Gateway routing
 * 3. Circuit Breaker pattern
 * 4. Service-to-Service communication
 * 5. Configuration management
 * 6. Distributed tracing concepts
 * 
 * Note: This is a conceptual demonstration. To run actual microservices,
 * you need proper Spring Cloud dependencies and multiple service instances.
 */

package Microservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ============================================
 * 1. USER SERVICE
 * ============================================
 * Demonstrates a microservice with:
 * - Service registration (Eureka)
 * - REST API endpoints
 * - Database per service concept
 */
@SpringBootApplication
@EnableEurekaClient
public class MicroservicesDemo implements CommandLineRunner {
    
    @Autowired
    private DiscoveryClient discoveryClient;
    
    @Autowired
    private UserService userService;
    
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("MICROSERVICES ARCHITECTURE DEMO");
        System.out.println("============================================");
        System.out.println("\nStarting User Service...");
        System.out.println("Port: 8081");
        System.out.println("Service Name: user-service");
        
        SpringApplication.run(MicroservicesDemo.class, args);
    }
    
    @Override
    public void run(String... args) {
        System.out.println("\n============================================");
        System.out.println("USER SERVICE STARTED");
        System.out.println("============================================");
        System.out.println("Service registered with Eureka");
        System.out.println("Available endpoints:");
        System.out.println("  GET  /api/users/{id}");
        System.out.println("  POST /api/users");
        System.out.println("  GET  /api/users");
        
        // Demonstrate service discovery
        System.out.println("\n--- Service Discovery Demo ---");
        discoverServices();
    }
    
    private void discoverServices() {
        List<String> services = discoveryClient.getServices();
        System.out.println("Discovered services: " + services);
        
        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            System.out.println("Service: " + service + " has " + instances.size() + " instances");
        }
    }
    
    /**
     * Load-balanced RestTemplate for service-to-service communication
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

/**
 * ============================================
 * 2. USER CONTROLLER
 * ============================================
 * REST API endpoints for User Service
 */
@RestController
@RequestMapping("/api/users")
class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * GET /api/users/{id}
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        System.out.println("\n[User Service] GET /api/users/" + id);
        System.out.println("[Trace ID: abc123] Processing request");
        
        User user = userService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }
    
    /**
     * POST /api/users
     * Create new user
     */
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        System.out.println("\n[User Service] POST /api/users");
        System.out.println("[Trace ID: abc123] Creating user: " + user.getName());
        
        User created = userService.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * GET /api/users
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        System.out.println("\n[User Service] GET /api/users");
        System.out.println("[Trace ID: abc123] Getting all users");
        
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "UP", "service", "user-service"));
    }
}

/**
 * ============================================
 * 3. USER SERVICE (Business Logic)
 * ============================================
 * Contains business logic for user operations
 */
@Service
class UserService {
    
    // Simulated database (in real app, this would be a repository)
    private final Map<Long, User> userDatabase = new java.util.HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    
    public User getUserById(Long id) {
        System.out.println("[User Service] Business logic: Getting user " + id);
        System.out.println("[User Service] Database query: SELECT * FROM users WHERE id = " + id);
        return userDatabase.get(id);
    }
    
    public User createUser(User user) {
        System.out.println("[User Service] Business logic: Creating user");
        Long id = (long) idGenerator.getAndIncrement();
        user.setId(id);
        userDatabase.put(id, user);
        System.out.println("[User Service] Database query: INSERT INTO users ...");
        return user;
    }
    
    public List<User> getAllUsers() {
        System.out.println("[User Service] Business logic: Getting all users");
        System.out.println("[User Service] Database query: SELECT * FROM users");
        return new java.util.ArrayList<>(userDatabase.values());
    }
}

/**
 * ============================================
 * 4. ORDER SERVICE (Separate Microservice)
 * ============================================
 * Demonstrates service-to-service communication
 * This would typically be a separate application
 */
@RestController
@RequestMapping("/api/orders")
class OrderController {
    
    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * GET /api/orders/{id}
     * Get order by ID (calls User Service)
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable Long id) {
        System.out.println("\n[Order Service] GET /api/orders/" + id);
        System.out.println("[Trace ID: abc123] Processing order request");
        
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Service-to-Service Communication
        // Order Service calls User Service
        System.out.println("[Order Service] Calling User Service to get user details");
        try {
            String userServiceUrl = "http://user-service/api/users/" + order.getUserId();
            System.out.println("[Order Service] Service Discovery: Resolved user-service to: " + userServiceUrl);
            
            User user = restTemplate.getForObject(userServiceUrl, User.class);
            order.setUser(user);
            System.out.println("[Order Service] Successfully retrieved user from User Service");
        } catch (Exception e) {
            System.out.println("[Order Service] Error calling User Service: " + e.getMessage());
            // In real app, would use Circuit Breaker here
        }
        
        return ResponseEntity.ok(order);
    }
    
    /**
     * POST /api/orders
     * Create new order
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        System.out.println("\n[Order Service] POST /api/orders");
        System.out.println("[Trace ID: abc123] Creating order");
        
        Order created = orderService.createOrder(order);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}

/**
 * ============================================
 * 5. ORDER SERVICE (Business Logic)
 * ============================================
 */
@Service
class OrderService {
    
    private final Map<Long, Order> orderDatabase = new java.util.HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(1);
    
    public Order getOrderById(Long id) {
        System.out.println("[Order Service] Business logic: Getting order " + id);
        System.out.println("[Order Service] Database query: SELECT * FROM orders WHERE id = " + id);
        return orderDatabase.get(id);
    }
    
    public Order createOrder(Order order) {
        System.out.println("[Order Service] Business logic: Creating order");
        Long id = (long) idGenerator.getAndIncrement();
        order.setId(id);
        orderDatabase.put(id, order);
        System.out.println("[Order Service] Database query: INSERT INTO orders ...");
        return order;
    }
}

/**
 * ============================================
 * 6. CIRCUIT BREAKER DEMONSTRATION
 * ============================================
 * Shows how Circuit Breaker prevents cascading failures
 */
@Service
class PaymentService {
    
    private final CircuitBreaker circuitBreaker;
    private int failureCount = 0;
    private String state = "CLOSED"; // CLOSED, OPEN, HALF_OPEN
    
    public PaymentService() {
        // Simulated Circuit Breaker
        this.circuitBreaker = new CircuitBreaker();
    }
    
    /**
     * Process payment with Circuit Breaker protection
     */
    public PaymentResult processPayment(Payment payment) {
        System.out.println("\n[Payment Service] Processing payment: " + payment.getAmount());
        System.out.println("[Payment Service] Circuit Breaker State: " + state);
        
        if ("OPEN".equals(state)) {
            System.out.println("[Payment Service] Circuit is OPEN - Using fallback");
            return fallback(payment);
        }
        
        try {
            // Simulate calling payment service
            PaymentResult result = callPaymentService(payment);
            
            // Reset failure count on success
            if ("HALF_OPEN".equals(state)) {
                state = "CLOSED";
                failureCount = 0;
                System.out.println("[Payment Service] Service recovered - Circuit CLOSED");
            }
            
            return result;
        } catch (Exception e) {
            failureCount++;
            System.out.println("[Payment Service] Payment failed. Failure count: " + failureCount);
            
            if (failureCount >= 5) {
                state = "OPEN";
                System.out.println("[Payment Service] Too many failures - Circuit OPEN");
                System.out.println("[Payment Service] Will use fallback for next 30 seconds");
            }
            
            return fallback(payment);
        }
    }
    
    private PaymentResult callPaymentService(Payment payment) throws Exception {
        // Simulate service call
        System.out.println("[Payment Service] Calling external payment service...");
        
        // Simulate failure (in real app, this would be an actual HTTP call)
        if (Math.random() < 0.3) { // 30% failure rate for demo
            throw new Exception("Payment service unavailable");
        }
        
        return new PaymentResult("SUCCESS", "Payment processed");
    }
    
    private PaymentResult fallback(Payment payment) {
        System.out.println("[Payment Service] Using fallback method");
        return new PaymentResult("PENDING", "Payment queued for later processing");
    }
}

/**
 * Simple Circuit Breaker implementation
 */
class CircuitBreaker {
    // Simplified for demonstration
    // Real implementation would use Resilience4j or Hystrix
}

/**
 * ============================================
 * 7. API GATEWAY DEMONSTRATION
 * ============================================
 * Shows API Gateway routing and filtering
 */
@RestController
@RequestMapping("/gateway")
class ApiGatewayController {
    
    @Autowired
    @LoadBalanced
    private RestTemplate restTemplate;
    
    /**
     * API Gateway route: /gateway/users/{id}
     * Routes to user-service
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<User> routeToUserService(@PathVariable Long id) {
        System.out.println("\n[API Gateway] Routing request to user-service");
        System.out.println("[API Gateway] Original path: /gateway/users/" + id);
        System.out.println("[API Gateway] Routed to: http://user-service/api/users/" + id);
        
        // Authentication check (simulated)
        System.out.println("[API Gateway] Authentication: Valid");
        System.out.println("[API Gateway] Rate limiting: Check passed");
        
        try {
            User user = restTemplate.getForObject(
                "http://user-service/api/users/" + id, 
                User.class
            );
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            System.out.println("[API Gateway] Error routing to user-service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
    
    /**
     * API Gateway route: /gateway/orders/{id}
     * Routes to order-service
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<Order> routeToOrderService(@PathVariable Long id) {
        System.out.println("\n[API Gateway] Routing request to order-service");
        System.out.println("[API Gateway] Original path: /gateway/orders/" + id);
        System.out.println("[API Gateway] Routed to: http://order-service/api/orders/" + id);
        
        try {
            Order order = restTemplate.getForObject(
                "http://order-service/api/orders/" + id, 
                Order.class
            );
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            System.out.println("[API Gateway] Error routing to order-service: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}

/**
 * ============================================
 * 8. CONFIGURATION MANAGEMENT DEMO
 * ============================================
 * Shows centralized configuration concept
 */
@RestController
@RequestMapping("/config")
@org.springframework.cloud.context.config.annotation.RefreshScope
class ConfigController {
    
    @org.springframework.beans.factory.annotation.Value("${app.message:Default Message}")
    private String message;
    
    @org.springframework.beans.factory.annotation.Value("${app.max-users:100}")
    private int maxUsers;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        System.out.println("\n[Config Client] Getting configuration from Config Server");
        System.out.println("[Config Client] Message: " + message);
        System.out.println("[Config Client] Max Users: " + maxUsers);
        
        return ResponseEntity.ok(Map.of(
            "message", message,
            "maxUsers", maxUsers,
            "source", "config-server"
        ));
    }
}

/**
 * ============================================
 * 9. DISTRIBUTED TRACING DEMO
 * ============================================
 * Shows how trace IDs flow across services
 */
@Component
class TracingFilter implements WebFilter {
    
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String traceId = exchange.getRequest().getHeaders().getFirst("X-Trace-Id");
        if (traceId == null) {
            traceId = generateTraceId();
        }
        
        System.out.println("[Tracing] Trace ID: " + traceId);
        System.out.println("[Tracing] Request: " + exchange.getRequest().getPath());
        
        // Add trace ID to response headers
        exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);
        
        return chain.filter(exchange);
    }
    
    private String generateTraceId() {
        return "trace-" + System.currentTimeMillis();
    }
}

/**
 * ============================================
 * 10. DOMAIN MODELS
 * ============================================
 */
class User {
    private Long id;
    private String name;
    private String email;
    
    public User() {}
    
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

class Order {
    private Long id;
    private Long userId;
    private String product;
    private Double amount;
    private User user; // Populated from User Service
    
    public Order() {}
    
    public Order(Long id, Long userId, String product, Double amount) {
        this.id = id;
        this.userId = userId;
        this.product = product;
        this.amount = amount;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getProduct() { return product; }
    public void setProduct(String product) { this.product = product; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    @Override
    public String toString() {
        return "Order{id=" + id + ", userId=" + userId + ", product='" + product + 
               "', amount=" + amount + "}";
    }
}

class Payment {
    private Long orderId;
    private Double amount;
    private String method;
    
    public Payment() {}
    
    public Payment(Long orderId, Double amount, String method) {
        this.orderId = orderId;
        this.amount = amount;
        this.method = method;
    }
    
    // Getters and setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }
}

class PaymentResult {
    private String status;
    private String message;
    
    public PaymentResult() {}
    
    public PaymentResult(String status, String message) {
        this.status = status;
        this.message = message;
    }
    
    // Getters and setters
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    @Override
    public String toString() {
        return "PaymentResult{status='" + status + "', message='" + message + "'}";
    }
}

/**
 * ============================================
 * MICROSERVICES ARCHITECTURE SUMMARY
 * ============================================
 * 
 * KEY CONCEPTS DEMONSTRATED:
 * 
 * 1. SERVICE DISCOVERY
 *    - Services register with Eureka
 *    - Services discover each other dynamically
 *    - Load-balanced service calls
 * 
 * 2. API GATEWAY
 *    - Single entry point for clients
 *    - Routes requests to appropriate services
 *    - Handles authentication, rate limiting
 * 
 * 3. CIRCUIT BREAKER
 *    - Prevents cascading failures
 *    - States: CLOSED, OPEN, HALF_OPEN
 *    - Fallback mechanisms
 * 
 * 4. SERVICE-TO-SERVICE COMMUNICATION
 *    - REST API calls between services
 *    - Load-balanced RestTemplate
 *    - Service discovery integration
 * 
 * 5. CONFIGURATION MANAGEMENT
 *    - Centralized configuration
 *    - Dynamic configuration refresh
 *    - Environment-specific configs
 * 
 * 6. DISTRIBUTED TRACING
 *    - Trace IDs across services
 *    - Request correlation
 *    - Performance monitoring
 * 
 * 7. DATABASE PER SERVICE
 *    - Each service owns its database
 *    - Service independence
 *    - Data isolation
 * 
 * ARCHITECTURE FLOW:
 * 
 * Client → API Gateway → Service Discovery → Microservice
 *                                              ↓
 *                                         Database
 * 
 * SERVICE COMMUNICATION:
 * Order Service → Service Discovery → User Service
 * 
 * FAULT TOLERANCE:
 * Payment Service (Down) → Circuit Breaker → Fallback
 * 
 * CONFIGURATION:
 * Config Client → Config Server → Git Repository
 * 
 * TRACING:
 * Request → Trace ID → All Services → Zipkin/Jaeger
 */

