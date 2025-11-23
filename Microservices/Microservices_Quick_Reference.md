# 🚀 Microservices - Quick Reference Guide

## 📋 Core Concepts

### What are Microservices?
- Small, independent services
- Organized by business capabilities
- Independently deployable
- Own their own databases
- Communicate via APIs

### Key Principles
1. **Single Responsibility**: One service, one business capability
2. **Database per Service**: Each service owns its database
3. **Decentralized Governance**: Teams choose their tech stack
4. **Fault Isolation**: Failures don't cascade
5. **Independent Deployment**: Deploy services separately

---

## 🏗️ Architecture Components

### 1. API Gateway
**Purpose**: Single entry point for all clients

**Responsibilities:**
- Routing
- Authentication
- Rate limiting
- Load balancing
- Protocol translation

**Tools**: Spring Cloud Gateway, Zuul, Kong

### 2. Service Discovery
**Purpose**: Services find each other dynamically

**Types:**
- **Client-Side**: Client queries registry
- **Server-Side**: Load balancer queries registry

**Tools**: Eureka, Consul, Zookeeper

### 3. Circuit Breaker
**Purpose**: Prevents cascading failures

**States:**
- **Closed**: Normal operation
- **Open**: Blocking calls, using fallback
- **Half-Open**: Testing recovery

**Tools**: Resilience4j, Hystrix

### 4. Configuration Server
**Purpose**: Centralized configuration management

**Tools**: Spring Cloud Config Server

### 5. Message Queue
**Purpose**: Asynchronous communication

**Tools**: RabbitMQ, Kafka, ActiveMQ

### 6. Distributed Tracing
**Purpose**: Track requests across services

**Tools**: Zipkin, Jaeger, Spring Cloud Sleuth

---

## 🎨 Design Patterns

### 1. API Gateway Pattern
```
Client → API Gateway → Services
```

### 2. Service Discovery Pattern
```
Service → Register → Eureka
Service → Discover → Eureka → Other Services
```

### 3. Circuit Breaker Pattern
```
Service A → Service B (Normal)
Service A → Fallback (When B fails)
```

### 4. Database per Service
```
User Service → User DB
Order Service → Order DB
```

### 5. Saga Pattern
**Choreography**: Services coordinate via events
**Orchestration**: Central orchestrator coordinates

### 6. CQRS (Command Query Responsibility Segregation)
- Separate read and write models
- Optimize for different operations

### 7. Event Sourcing
- Store events instead of current state
- Replay events to get current state

### 8. Bulkhead Pattern
- Isolate resources (thread pools, connections)
- Prevent cascading failures

---

## ☁️ Spring Cloud Components

### Spring Cloud Gateway
```java
@SpringBootApplication
public class GatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
```

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**
```

### Eureka Server
```java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

### Eureka Client
```java
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

### Config Server
```java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
```

### Circuit Breaker (Resilience4j)
```java
@CircuitBreaker(name = "userService", fallbackMethod = "fallback")
public User getUser(Long id) {
    return restTemplate.getForObject("/users/" + id, User.class);
}

public User fallback(Long id, Exception ex) {
    return new User(id, "Default User");
}
```

### OpenFeign Client
```java
@FeignClient(name = "user-service")
public interface UserServiceClient {
    @GetMapping("/users/{id}")
    User getUser(@PathVariable Long id);
}
```

---

## 📡 Communication Patterns

### Synchronous (REST)
```java
@RestController
public class OrderController {
    @Autowired
    private RestTemplate restTemplate;
    
    public User getUser(Long id) {
        return restTemplate.getForObject(
            "http://user-service/users/" + id, 
            User.class
        );
    }
}
```

**Use When:**
- Real-time response needed
- Simple request-response
- Immediate consistency required

### Asynchronous (Message Queue)
```java
// Publisher
@Service
public class OrderService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void createOrder(Order order) {
        orderRepository.save(order);
        rabbitTemplate.convertAndSend("order.exchange", 
                                     "order.created", order);
    }
}

// Subscriber
@Component
public class PaymentService {
    @RabbitListener(queues = "order.created.queue")
    public void handleOrderCreated(Order order) {
        processPayment(order);
    }
}
```

**Use When:**
- Long-running operations
- Event-driven architecture
- Loose coupling needed

---

## 💾 Data Management

### Database per Service
```
User Service → PostgreSQL
Order Service → MySQL
Product Service → MongoDB
```

### Handling Consistency
1. **Eventual Consistency**: Accept eventual consistency
2. **Saga Pattern**: Manage distributed transactions
3. **Event-Driven Updates**: Services update via events

### Saga Types
- **Choreography**: Services coordinate via events
- **Orchestration**: Central orchestrator coordinates

---

## 🔍 Service Discovery

### Eureka Server Configuration
```yaml
server:
  port: 8761

eureka:
  instance:
    hostname: localhost
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Eureka Client Configuration
```yaml
spring:
  application:
    name: user-service

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
```

### Service Discovery in Code
```java
@Autowired
private DiscoveryClient discoveryClient;

public User getUser(Long userId) {
    List<ServiceInstance> instances = 
        discoveryClient.getInstances("user-service");
    ServiceInstance instance = instances.get(0);
    String url = "http://" + instance.getHost() + ":" + 
                 instance.getPort() + "/users/" + userId;
    return restTemplate.getForObject(url, User.class);
}
```

---

## ⚡ Circuit Breaker

### Resilience4j Configuration
```java
@Service
public class PaymentService {
    private final CircuitBreaker circuitBreaker;
    
    public PaymentService() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
            .failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slidingWindowSize(10)
            .build();
        
        this.circuitBreaker = CircuitBreaker.of("paymentService", config);
    }
    
    public PaymentResult processPayment(Payment payment) {
        return circuitBreaker.executeSupplier(() -> {
            return restTemplate.postForObject(
                "http://payment-service/process", 
                payment, 
                PaymentResult.class
            );
        });
    }
}
```

### Circuit Breaker States
- **Closed**: Normal, calls pass through
- **Open**: Failing, calls blocked, fallback used
- **Half-Open**: Testing if service recovered

---

## ⚙️ Configuration Management

### Config Server Setup
```yaml
server:
  port: 8888

spring:
  cloud:
    config:
      server:
        git:
          uri: https://github.com/config-repo
```

### Config Client Setup
```yaml
spring:
  application:
    name: user-service
  cloud:
    config:
      uri: http://localhost:8888
```

### Using Configuration
```java
@RestController
@RefreshScope
public class ConfigController {
    @Value("${app.message}")
    private String message;
    
    @GetMapping("/config")
    public String getConfig() {
        return message;
    }
}
```

---

## 🔍 Distributed Tracing

### Spring Cloud Sleuth
Automatically adds trace IDs to logs.

**Log Format:**
```
[service-name,trace-id,span-id] Log message
```

### Zipkin Integration
```yaml
spring:
  zipkin:
    base-url: http://localhost:9411
  sleuth:
    sampler:
      probability: 1.0
```

---

## ⚠️ Common Challenges

### 1. Network Latency
**Solution**: Async communication, caching, bulk operations

### 2. Data Consistency
**Solution**: Eventual consistency, Saga pattern, events

### 3. Service Communication
**Solution**: Circuit breakers, retries, timeouts, queues

### 4. Testing
**Solution**: Unit tests, integration tests, contract tests

### 5. Deployment
**Solution**: Docker, Kubernetes, CI/CD, blue-green deployment

### 6. Monitoring
**Solution**: Centralized logging, tracing, metrics, alerts

---

## ✅ Best Practices

### Service Design
- ✅ Single responsibility
- ✅ Right size (not too small/large)
- ✅ Stateless services
- ✅ Idempotent operations

### Communication
- ✅ Prefer async when possible
- ✅ API versioning
- ✅ Backward compatibility
- ✅ Always set timeouts

### Data Management
- ✅ Database per service
- ✅ Accept eventual consistency
- ✅ Use Saga for transactions
- ✅ Consider CQRS

### Security
- ✅ Centralized authentication
- ✅ Service-level authorization
- ✅ Encrypt data
- ✅ Secrets management

### Monitoring
- ✅ Health checks
- ✅ Structured logging
- ✅ Metrics collection
- ✅ Distributed tracing

---

## 🎤 Top Interview Questions

### Q1: What are Microservices?
**A**: Small, independent services organized by business capabilities, independently deployable.

### Q2: Microservices vs Monolith?
**A**: Monolith = single unit, shared DB. Microservices = multiple units, DB per service.

### Q3: What is Service Discovery?
**A**: Pattern where services register and discover each other dynamically (Eureka, Consul).

### Q4: What is API Gateway?
**A**: Single entry point handling routing, auth, rate limiting, load balancing.

### Q5: What is Circuit Breaker?
**A**: Prevents cascading failures by blocking calls to failing services.

### Q6: How handle transactions?
**A**: Saga pattern (choreography/orchestration), eventual consistency.

### Q7: Database per Service?
**A**: Each service owns its database for independence and loose coupling.

### Q8: Communication patterns?
**A**: Synchronous (REST) for real-time, Asynchronous (MQ) for decoupling.

### Q9: Distributed Tracing?
**A**: Track requests across services (Zipkin, Jaeger, Sleuth).

### Q10: When use Microservices?
**A**: Large app, multiple teams, independent scaling, technology diversity.

---

## 📊 Comparison Table

| Aspect | Monolith | Microservices |
|--------|----------|---------------|
| Deployment | Single unit | Independent |
| Scaling | Scale all | Scale individual |
| Technology | Single stack | Multiple stacks |
| Database | Shared | Per service |
| Coupling | Tight | Loose |
| Complexity | Lower | Higher |
| Testing | Easier | Complex |

---

## 🔧 Quick Setup Commands

### Eureka Server
```bash
# Add dependency
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>

# Annotate main class
@EnableEurekaServer
```

### Eureka Client
```bash
# Add dependency
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>

# Annotate main class
@EnableEurekaClient
```

### Spring Cloud Gateway
```bash
# Add dependency
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```

### Config Server
```bash
# Add dependency
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-config-server</artifactId>
</dependency>

# Annotate main class
@EnableConfigServer
```

### Resilience4j
```bash
# Add dependency
<dependency>
    <groupId>io.github.resilience4j</groupId>
    <artifactId>resilience4j-spring-boot2</artifactId>
</dependency>
```

---

## 🎯 Key Takeaways

1. **Microservices** = Independent services by business capability
2. **API Gateway** = Single entry point
3. **Service Discovery** = Dynamic service location
4. **Circuit Breaker** = Prevent cascading failures
5. **Database per Service** = Service independence
6. **Saga** = Distributed transactions
7. **Event-Driven** = Loose coupling
8. **Tracing** = Request tracking

---

**Remember**: Microservices add complexity. Use when benefits > costs. Focus on patterns and trade-offs for interviews! 🚀

