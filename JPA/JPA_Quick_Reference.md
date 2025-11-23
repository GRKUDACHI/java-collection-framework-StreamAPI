# 🗄️ JPA (Java Persistence API) - Quick Reference

## 📋 Core Concepts

### What is JPA?
- **Specification** for managing relational data in Java
- **ORM** (Object-Relational Mapping) framework
- **Vendor-independent** (can use Hibernate, EclipseLink, etc.)
- Part of **Java EE**

### JPA vs Hibernate
- **JPA** = Specification (interface/contract)
- **Hibernate** = Implementation (actual code)
- **Analogy**: JPA = car specification, Hibernate = Toyota car

---

## 🏗️ Architecture

```
Java Application
    ↓
Entity Classes (@Entity)
    ↓
JPA API (javax.persistence)
    ↓
JPA Implementation (Hibernate, EclipseLink)
    ↓
Database
```

---

## 🎯 Core Components

### 1. EntityManagerFactory
```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
```
- Creates EntityManager instances
- Expensive to create (one per application)
- Thread-safe

### 2. EntityManager
```java
EntityManager em = emf.createEntityManager();
```
- Main interface for JPA operations
- Manages entity lifecycle
- Not thread-safe (one per thread)

### 3. Persistence Context
- Set of managed entity instances
- Cache of entities in memory
- Tracks changes automatically

---

## 🔄 Entity States

```
NEW (Transient)
    ↓ persist()
MANAGED (Persistent)
    ↓ remove()
REMOVED
    ↓ detach()
DETACHED
    ↓ merge()
MANAGED (Re-attached)
```

### States Explained
- **NEW**: Not managed, not in database
- **MANAGED**: Tracked, synchronized with database
- **DETACHED**: Previously managed, no longer tracked
- **REMOVED**: Marked for deletion

---

## 📝 Core Annotations

### Entity Annotations
```java
@Entity                    // Marks class as entity
@Table(name = "users")     // Table name
@Id                        // Primary key
@GeneratedValue            // Auto-generate ID
@Column(name = "user_name") // Column mapping
@Transient                 // Exclude from persistence
@Temporal(TemporalType.DATE) // Date mapping
@Lob                       // Large object
@Enumerated(EnumType.STRING) // Enum mapping
```

### Relationship Annotations
```java
@OneToOne                  // One-to-one relationship
@OneToMany                 // One-to-many relationship
@ManyToOne                 // Many-to-one relationship
@ManyToMany               // Many-to-many relationship
@JoinColumn               // Foreign key column
@JoinTable                // Join table for many-to-many
```

---

## 🔗 Relationships

### @OneToOne
```java
@Entity
public class User {
    @OneToOne(mappedBy = "user")
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### @OneToMany / @ManyToOne
```java
@Entity
public class User {
    @OneToMany(mappedBy = "user")
    private List<Order> orders;
}

@Entity
public class Order {
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### @ManyToMany
```java
@Entity
public class User {
    @ManyToMany
    @JoinTable(name = "user_roles",
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;
}
```

### Relationship Options
```java
cascade = CascadeType.ALL        // All operations cascade
fetch = FetchType.LAZY           // Load on demand (default)
fetch = FetchType.EAGER          // Load immediately
orphanRemoval = true             // Delete child when removed
```

---

## 🔧 EntityManager Operations

### Basic Operations
```java
// Save new entity
em.persist(user);

// Find by ID
User user = em.find(User.class, 1L);

// Get proxy (lazy)
User user = em.getReference(User.class, 1L);

// Update detached entity
User managed = em.merge(detachedUser);

// Delete entity
em.remove(user);

// Reload from database
em.refresh(user);

// Remove from context
em.detach(user);

// Clear context
em.clear();

// Synchronize with database
em.flush();

// Check if managed
boolean isManaged = em.contains(user);
```

---

## 📊 JPQL (Java Persistence Query Language)

### Basic Queries
```java
// Select all
String jpql = "SELECT u FROM User u";
TypedQuery<User> query = em.createQuery(jpql, User.class);
List<User> users = query.getResultList();

// Select with where
String jpql = "SELECT u FROM User u WHERE u.name = :name";
TypedQuery<User> query = em.createQuery(jpql, User.class);
query.setParameter("name", "John");
User user = query.getSingleResult();

// Select with join
String jpql = "SELECT u FROM User u JOIN u.orders o WHERE o.total > :amount";
```

### Named Queries
```java
@Entity
@NamedQuery(name = "User.findByName",
            query = "SELECT u FROM User u WHERE u.name = :name")
public class User { }

// Usage
TypedQuery<User> query = em.createNamedQuery("User.findByName", User.class);
```

### Native SQL
```java
String sql = "SELECT * FROM users WHERE name = ?";
Query query = em.createNativeQuery(sql, User.class);
query.setParameter(1, "John");
```

---

## 💼 Transactions

### Resource-Local Transactions
```java
EntityTransaction tx = em.getTransaction();
try {
    tx.begin();
    em.persist(user);
    tx.commit();
} catch (Exception e) {
    if (tx.isActive()) {
        tx.rollback();
    }
}
```

### JTA Transactions (Container-managed)
```java
@Stateless
public class UserService {
    @PersistenceContext
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUser(User user) {
        em.persist(user);
    }
}
```

---

## 🌱 Spring Data JPA

### Repository Interface
```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Auto-generated methods:
    // save(), findById(), findAll(), delete(), count()
}
```

### Query Methods
```java
// Find by property
List<User> findByName(String name);

// Find with conditions
List<User> findByNameAndEmail(String name, String email);
List<User> findByAgeGreaterThan(int age);

// Find with sorting
List<User> findByNameOrderByAgeDesc(String name);

// Find with pagination
Page<User> findByName(String name, Pageable pageable);

// Custom query
@Query("SELECT u FROM User u WHERE u.name = :name")
User findUserByName(@Param("name") String name);

// Native query
@Query(value = "SELECT * FROM users WHERE name = ?1", nativeQuery = true)
User findUserByNameNative(String name);
```

---

## 🔄 Lifecycle Callbacks

```java
@Entity
public class User {
    @PrePersist
    public void beforePersist() { }
    
    @PostPersist
    public void afterPersist() { }
    
    @PreUpdate
    public void beforeUpdate() { }
    
    @PostUpdate
    public void afterUpdate() { }
    
    @PreRemove
    public void beforeRemove() { }
    
    @PostRemove
    public void afterRemove() { }
    
    @PostLoad
    public void afterLoad() { }
}
```

---

## ⚙️ @GeneratedValue Strategies

```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)      // Auto (default)
@GeneratedValue(strategy = GenerationType.IDENTITY)   // Auto-increment
@GeneratedValue(strategy = GenerationType.SEQUENCE)   // Database sequence
@GeneratedValue(strategy = GenerationType.TABLE)     // Table generator
```

---

## 🔍 Fetch Types

### EAGER vs LAZY
```java
@OneToMany(fetch = FetchType.LAZY)   // Load on demand (default for collections)
@ManyToOne(fetch = FetchType.EAGER)  // Load immediately (default for single)
```

**LAZY:**
- Loads on access
- Better performance
- Can cause LazyInitializationException

**EAGER:**
- Loads immediately
- Can cause N+1 problem
- Use when always needed

---

## ✅ Best Practices

### Entity Design
- ✅ Keep entities simple (POJOs)
- ✅ Use `@Transient` for non-persistent fields
- ✅ Implement `equals()` and `hashCode()` correctly
- ✅ Use `@Version` for optimistic locking

### Relationships
- ✅ Use `LAZY` fetching by default
- ✅ Use `EAGER` only when necessary
- ✅ Use `mappedBy` correctly
- ✅ Use `cascade` carefully

### Queries
- ✅ Use JPQL for portability
- ✅ Use named queries for reusable queries
- ✅ Use parameterized queries
- ✅ Use pagination for large result sets

### Transactions
- ✅ Keep transactions short
- ✅ Use `@Transactional` at service layer
- ✅ Handle exceptions properly

---

## 🎤 Top Interview Questions

### Q1: What is JPA?
**A**: Java Persistence API - specification for ORM, maps Java objects to database tables.

### Q2: JPA vs Hibernate?
**A**: JPA = specification, Hibernate = implementation.

### Q3: What is EntityManager?
**A**: Main interface for JPA operations, manages entity lifecycle.

### Q4: Entity States?
**A**: NEW, MANAGED, DETACHED, REMOVED.

### Q5: persist() vs merge()?
**A**: persist() for new entities, merge() for detached entities.

### Q6: find() vs getReference()?
**A**: find() loads immediately, getReference() returns proxy (lazy).

### Q7: What is JPQL?
**A**: Java Persistence Query Language - database-independent query language.

### Q8: EAGER vs LAZY?
**A**: EAGER loads immediately, LAZY loads on demand.

### Q9: What is Spring Data JPA?
**A**: Spring framework that simplifies JPA repository implementation.

### Q10: What is N+1 Query Problem?
**A**: 1 query for parents, N queries for children. Solution: JOIN FETCH.

---

## 📊 Comparison Table

| Aspect | JPA | Hibernate |
|--------|-----|-----------|
| Type | Specification | Implementation |
| Portability | High | Medium |
| Features | Standard only | Extra features |
| Usage | JPA APIs | Hibernate or JPA APIs |

---

## 🔧 Quick Setup

### Maven Dependency
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

### Entity Example
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_name", nullable = false)
    private String name;
    
    @Column(unique = true)
    private String email;
}
```

### Repository Example
```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);
}
```

### Service Example
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
}
```

---

## 🎯 Key Takeaways

1. **JPA** = Specification, **Hibernate** = Implementation
2. **EntityManager** = Main interface for JPA operations
3. **Entity States** = NEW, MANAGED, DETACHED, REMOVED
4. **JPQL** = Database-independent query language
5. **Spring Data JPA** = Simplifies repository implementation
6. **Relationships** = @OneToOne, @OneToMany, @ManyToOne, @ManyToMany
7. **Fetch Types** = EAGER (immediate) vs LAZY (on demand)

---

**Remember**: JPA is a specification, Hibernate implements it. Focus on JPA concepts for interviews! 🚀

