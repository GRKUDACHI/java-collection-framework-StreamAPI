/**
 * JPA (Java Persistence API) Complete Demo
 * 
 * This file demonstrates key JPA concepts:
 * 1. Entity definition and annotations
 * 2. Entity lifecycle and states
 * 3. EntityManager operations
 * 4. Relationships (OneToOne, OneToMany, ManyToMany)
 * 5. JPQL queries
 * 6. Spring Data JPA usage
 * 7. Transactions
 * 
 * Note: This is a conceptual demonstration. To run actual JPA code,
 * you need proper dependencies (spring-boot-starter-data-jpa) and database.
 */

package JPA;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================
 * 1. MAIN APPLICATION
 * ============================================
 */
@SpringBootApplication
public class JPADemo implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private EntityManagerDemo entityManagerDemo;
    
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("JPA (Java Persistence API) COMPLETE DEMO");
        System.out.println("============================================");
        System.out.println("\nJPA vs Hibernate:");
        System.out.println("  JPA = Specification (Interface)");
        System.out.println("  Hibernate = Implementation (Actual Code)");
        System.out.println("\nStarting JPA Demo...\n");
        
        SpringApplication.run(JPADemo.class, args);
    }
    
    @Override
    public void run(String... args) {
        System.out.println("\n============================================");
        System.out.println("DEMONSTRATING JPA CONCEPTS");
        System.out.println("============================================");
        
        // Demonstrate entity lifecycle
        entityManagerDemo.demonstrateEntityLifecycle();
        
        // Demonstrate relationships
        entityManagerDemo.demonstrateRelationships();
        
        // Demonstrate Spring Data JPA
        userService.demonstrateSpringDataJPA();
    }
}

/**
 * ============================================
 * 2. ENTITY - USER
 * ============================================
 * Demonstrates basic entity annotations
 */
@Entity
@Table(name = "users")
class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_name", nullable = false, length = 100)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(name = "age")
    private Integer age;
    
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    
    @Version
    private Long version;  // Optimistic locking
    
    @Transient
    private String temporaryData;  // Not persisted
    
    // One-to-One relationship
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;
    
    // One-to-Many relationship
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
    
    // Many-to-Many relationship
    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();
    
    // Lifecycle callbacks
    @PrePersist
    public void beforePersist() {
        System.out.println("[User] @PrePersist: Before saving to database");
        if (status == null) {
            status = UserStatus.ACTIVE;
        }
    }
    
    @PostPersist
    public void afterPersist() {
        System.out.println("[User] @PostPersist: After saving to database");
    }
    
    @PreUpdate
    public void beforeUpdate() {
        System.out.println("[User] @PreUpdate: Before updating in database");
    }
    
    @PostUpdate
    public void afterUpdate() {
        System.out.println("[User] @PostUpdate: After updating in database");
    }
    
    @PreRemove
    public void beforeRemove() {
        System.out.println("[User] @PreRemove: Before deleting from database");
    }
    
    @PostRemove
    public void afterRemove() {
        System.out.println("[User] @PostRemove: After deleting from database");
    }
    
    @PostLoad
    public void afterLoad() {
        System.out.println("[User] @PostLoad: After loading from database");
    }
    
    // Constructors
    public User() {}
    
    public User(String name, String email) {
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
    
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    
    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
    
    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
    
    public String getTemporaryData() { return temporaryData; }
    public void setTemporaryData(String temporaryData) { this.temporaryData = temporaryData; }
    
    public UserProfile getProfile() { return profile; }
    public void setProfile(UserProfile profile) { this.profile = profile; }
    
    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
    
    @Override
    public String toString() {
        return "User{id=" + id + ", name='" + name + "', email='" + email + "'}";
    }
}

/**
 * ============================================
 * 3. ENUM - USER STATUS
 * ============================================
 */
enum UserStatus {
    ACTIVE, INACTIVE, SUSPENDED
}

/**
 * ============================================
 * 4. ENTITY - USER PROFILE (One-to-One)
 * ============================================
 */
@Entity
@Table(name = "user_profiles")
class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "bio", length = 500)
    @Lob
    private String biography;
    
    @Column(name = "phone")
    private String phoneNumber;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

/**
 * ============================================
 * 5. ENTITY - ORDER (One-to-Many / Many-to-One)
 * ============================================
 */
@Entity
@Table(name = "orders")
class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "order_number", unique = true)
    private String orderNumber;
    
    @Column(name = "total_amount")
    private Double totalAmount;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    
    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}

/**
 * ============================================
 * 6. ENTITY - ROLE (Many-to-Many)
 * ============================================
 */
@Entity
@Table(name = "roles")
class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "role_name", unique = true)
    private String name;
    
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<User> getUsers() { return users; }
    public void setUsers(List<User> users) { this.users = users; }
}

/**
 * ============================================
 * 7. ENTITYMANAGER DEMONSTRATIONS
 * ============================================
 * Shows core JPA operations
 */
@Service
class EntityManagerDemo {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Demonstrates entity lifecycle states
     */
    public void demonstrateEntityLifecycle() {
        System.out.println("\n--- Entity Lifecycle Demo ---");
        
        // 1. NEW (Transient) state
        System.out.println("\n1. NEW (Transient) State:");
        User user = new User("John Doe", "john@example.com");
        System.out.println("   Created user: " + user);
        System.out.println("   State: NEW (not managed by EntityManager)");
        
        // 2. MANAGED (Persistent) state
        System.out.println("\n2. MANAGED (Persistent) State:");
        System.out.println("   Calling entityManager.persist(user)...");
        // In real code: entityManager.persist(user);
        System.out.println("   State: MANAGED (tracked by EntityManager)");
        System.out.println("   Changes are automatically tracked");
        
        // 3. DETACHED state
        System.out.println("\n3. DETACHED State:");
        System.out.println("   Calling entityManager.detach(user)...");
        // In real code: entityManager.detach(user);
        System.out.println("   State: DETACHED (no longer tracked)");
        System.out.println("   Changes are NOT automatically saved");
        
        // 4. REMOVED state
        System.out.println("\n4. REMOVED State:");
        System.out.println("   Calling entityManager.remove(user)...");
        // In real code: entityManager.remove(user);
        System.out.println("   State: REMOVED (marked for deletion)");
        System.out.println("   Will be deleted on commit");
    }
    
    /**
     * Demonstrates EntityManager operations
     */
    public void demonstrateEntityManagerOperations() {
        System.out.println("\n--- EntityManager Operations Demo ---");
        
        System.out.println("\n1. persist() - Save new entity:");
        System.out.println("   User user = new User(\"John\", \"john@example.com\");");
        System.out.println("   entityManager.persist(user);");
        System.out.println("   // Entity becomes MANAGED");
        
        System.out.println("\n2. find() - Find by ID:");
        System.out.println("   User user = entityManager.find(User.class, 1L);");
        System.out.println("   // Loads immediately, returns null if not found");
        
        System.out.println("\n3. getReference() - Get proxy:");
        System.out.println("   User user = entityManager.getReference(User.class, 1L);");
        System.out.println("   // Returns proxy, loads on access");
        
        System.out.println("\n4. merge() - Update detached entity:");
        System.out.println("   User detached = new User();");
        System.out.println("   detached.setId(1L);");
        System.out.println("   User managed = entityManager.merge(detached);");
        System.out.println("   // Updates in database, returns managed entity");
        
        System.out.println("\n5. remove() - Delete entity:");
        System.out.println("   User user = entityManager.find(User.class, 1L);");
        System.out.println("   entityManager.remove(user);");
        System.out.println("   // Marks for deletion");
        
        System.out.println("\n6. refresh() - Reload from database:");
        System.out.println("   User user = entityManager.find(User.class, 1L);");
        System.out.println("   user.setName(\"Changed\");");
        System.out.println("   entityManager.refresh(user);");
        System.out.println("   // Reloads from database, discards changes");
        
        System.out.println("\n7. flush() - Synchronize with database:");
        System.out.println("   entityManager.persist(user);");
        System.out.println("   entityManager.flush();");
        System.out.println("   // Forces SQL execution without committing");
    }
    
    /**
     * Demonstrates relationships
     */
    public void demonstrateRelationships() {
        System.out.println("\n--- Relationships Demo ---");
        
        System.out.println("\n1. @OneToOne Relationship:");
        System.out.println("   User ←→ UserProfile");
        System.out.println("   - One user has one profile");
        System.out.println("   - One profile belongs to one user");
        System.out.println("   - Use @JoinColumn on owning side");
        System.out.println("   - Use mappedBy on inverse side");
        
        System.out.println("\n2. @OneToMany / @ManyToOne Relationship:");
        System.out.println("   User ←→ Order");
        System.out.println("   - One user has many orders");
        System.out.println("   - Many orders belong to one user");
        System.out.println("   - @OneToMany on User (with mappedBy)");
        System.out.println("   - @ManyToOne on Order (with @JoinColumn)");
        
        System.out.println("\n3. @ManyToMany Relationship:");
        System.out.println("   User ←→ Role");
        System.out.println("   - Many users have many roles");
        System.out.println("   - Many roles belong to many users");
        System.out.println("   - Requires @JoinTable");
        System.out.println("   - One side uses mappedBy");
        
        System.out.println("\n4. Cascade Types:");
        System.out.println("   - CascadeType.ALL: All operations cascade");
        System.out.println("   - CascadeType.PERSIST: Save child when parent saved");
        System.out.println("   - CascadeType.REMOVE: Delete child when parent deleted");
        
        System.out.println("\n5. Fetch Types:");
        System.out.println("   - FetchType.LAZY: Load on demand (default for collections)");
        System.out.println("   - FetchType.EAGER: Load immediately (default for single)");
    }
    
    /**
     * Demonstrates JPQL queries
     */
    public void demonstrateJPQL() {
        System.out.println("\n--- JPQL (Java Persistence Query Language) Demo ---");
        
        System.out.println("\n1. Basic Select:");
        System.out.println("   String jpql = \"SELECT u FROM User u\";");
        System.out.println("   TypedQuery<User> query = entityManager.createQuery(jpql, User.class);");
        System.out.println("   List<User> users = query.getResultList();");
        
        System.out.println("\n2. Select with Where:");
        System.out.println("   String jpql = \"SELECT u FROM User u WHERE u.name = :name\";");
        System.out.println("   TypedQuery<User> query = entityManager.createQuery(jpql, User.class);");
        System.out.println("   query.setParameter(\"name\", \"John\");");
        System.out.println("   User user = query.getSingleResult();");
        
        System.out.println("\n3. Select with Join:");
        System.out.println("   String jpql = \"SELECT u FROM User u JOIN u.orders o WHERE o.totalAmount > :amount\";");
        System.out.println("   TypedQuery<User> query = entityManager.createQuery(jpql, User.class);");
        System.out.println("   query.setParameter(\"amount\", 100.0);");
        System.out.println("   List<User> users = query.getResultList();");
        
        System.out.println("\n4. Named Query:");
        System.out.println("   @Entity");
        System.out.println("   @NamedQuery(name = \"User.findByName\",");
        System.out.println("               query = \"SELECT u FROM User u WHERE u.name = :name\")");
        System.out.println("   public class User { }");
        System.out.println("   ");
        System.out.println("   TypedQuery<User> query = entityManager.createNamedQuery(\"User.findByName\", User.class);");
    }
}

/**
 * ============================================
 * 8. SPRING DATA JPA REPOSITORY
 * ============================================
 */
@Repository
interface UserRepository extends JpaRepository<User, Long> {
    
    // Query methods (auto-generated from method names)
    List<User> findByName(String name);
    List<User> findByEmail(String email);
    List<User> findByNameAndEmail(String name, String email);
    List<User> findByAgeGreaterThan(int age);
    List<User> findByNameOrderByAgeDesc(String name);
    
    // Custom JPQL query
    @Query("SELECT u FROM User u WHERE u.name = :name")
    User findUserByName(@Param("name") String name);
    
    // Custom native query
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findUserByEmailNative(String email);
}

/**
 * ============================================
 * 9. SERVICE LAYER
 * ============================================
 */
@Service
class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Demonstrates Spring Data JPA usage
     */
    @Transactional
    public void demonstrateSpringDataJPA() {
        System.out.println("\n--- Spring Data JPA Demo ---");
        
        System.out.println("\n1. Save Entity:");
        System.out.println("   User user = new User(\"John\", \"john@example.com\");");
        System.out.println("   userRepository.save(user);");
        System.out.println("   // Automatically persists to database");
        
        System.out.println("\n2. Find by ID:");
        System.out.println("   Optional<User> user = userRepository.findById(1L);");
        System.out.println("   // Returns Optional, handles null safely");
        
        System.out.println("\n3. Find All:");
        System.out.println("   List<User> users = userRepository.findAll();");
        System.out.println("   // Returns all entities");
        
        System.out.println("\n4. Query Methods:");
        System.out.println("   List<User> users = userRepository.findByName(\"John\");");
        System.out.println("   // Auto-generated query from method name");
        
        System.out.println("\n5. Custom Query:");
        System.out.println("   User user = userRepository.findUserByName(\"John\");");
        System.out.println("   // Uses @Query annotation");
        
        System.out.println("\n6. Delete:");
        System.out.println("   userRepository.deleteById(1L);");
        System.out.println("   // Deletes entity by ID");
        
        System.out.println("\n7. Count:");
        System.out.println("   long count = userRepository.count();");
        System.out.println("   // Returns total number of entities");
    }
    
    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }
}

/**
 * ============================================
 * JPA CONCEPTS SUMMARY
 * ============================================
 * 
 * KEY DIFFERENCES: JPA vs Hibernate
 * 
 * JPA (Java Persistence API):
 * - Specification/Interface
 * - Defines standard APIs and annotations
 * - Vendor-independent
 * - Part of Java EE
 * 
 * Hibernate:
 * - Implementation of JPA
 * - Actual code/library
 * - Adds extra features beyond JPA
 * - Can be used directly or via JPA
 * 
 * ENTITY LIFECYCLE STATES:
 * 
 * 1. NEW (Transient)
 *    - Not managed by EntityManager
 *    - Not in database
 *    - Created with: new User()
 * 
 * 2. MANAGED (Persistent)
 *    - Managed by EntityManager
 *    - Changes tracked automatically
 *    - Created with: persist(), find(), merge()
 * 
 * 3. DETACHED
 *    - Previously managed
 *    - No longer tracked
 *    - Created with: detach(), clear(), close()
 * 
 * 4. REMOVED
 *    - Marked for deletion
 *    - Created with: remove()
 * 
 * ENTITYMANAGER OPERATIONS:
 * 
 * - persist(entity): Save new entity (NEW → MANAGED)
 * - find(Class, id): Find by ID (returns entity or null)
 * - getReference(Class, id): Get proxy (lazy loading)
 * - merge(entity): Update detached entity (DETACHED → MANAGED)
 * - remove(entity): Delete entity (MANAGED → REMOVED)
 * - refresh(entity): Reload from database
 * - detach(entity): Remove from context (MANAGED → DETACHED)
 * - flush(): Synchronize with database
 * - contains(entity): Check if managed
 * 
 * RELATIONSHIPS:
 * 
 * @OneToOne:
 * - One entity relates to one other entity
 * - Example: User ←→ UserProfile
 * - Use @JoinColumn on owning side
 * - Use mappedBy on inverse side
 * 
 * @OneToMany / @ManyToOne:
 * - One entity relates to many other entities
 * - Example: User ←→ Order
 * - @OneToMany on parent (with mappedBy)
 * - @ManyToOne on child (with @JoinColumn)
 * 
 * @ManyToMany:
 * - Many entities relate to many other entities
 * - Example: User ←→ Role
 * - Requires @JoinTable
 * - One side uses mappedBy
 * 
 * CASCADE TYPES:
 * - CascadeType.ALL: All operations cascade
 * - CascadeType.PERSIST: Save child when parent saved
 * - CascadeType.REMOVE: Delete child when parent deleted
 * - CascadeType.MERGE: Update child when parent updated
 * 
 * FETCH TYPES:
 * - FetchType.LAZY: Load on demand (better performance)
 * - FetchType.EAGER: Load immediately (can cause N+1 problem)
 * 
 * JPQL (Java Persistence Query Language):
 * - Database-independent query language
 * - Works with entities instead of tables
 * - Example: "SELECT u FROM User u WHERE u.name = :name"
 * 
 * SPRING DATA JPA:
 * - Simplifies JPA repository implementation
 * - Auto-generates queries from method names
 * - Provides built-in pagination and sorting
 * - Reduces boilerplate code
 * 
 * BEST PRACTICES:
 * - Use LAZY fetching by default
 * - Keep transactions short
 * - Use @Transactional at service layer
 * - Use JPQL for portability
 * - Use named queries for reusable queries
 * - Implement equals() and hashCode() correctly
 * - Use @Version for optimistic locking
 */

