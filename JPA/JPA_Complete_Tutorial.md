# 🗄️ JPA (Java Persistence API) - Complete Tutorial

## 📖 Table of Contents
1. [What is JPA?](#what-is-jpa)
2. [JPA vs Hibernate](#jpa-vs-hibernate)
3. [JPA Architecture](#jpa-architecture)
4. [Core JPA Components](#core-jpa-components)
5. [Entity Lifecycle](#entity-lifecycle)
6. [JPA Annotations](#jpa-annotations)
7. [Entity Relationships](#entity-relationships)
8. [JPA Query Language (JPQL)](#jpa-query-language-jpql)
9. [EntityManager and Persistence Context](#entitymanager-and-persistence-context)
10. [Transactions](#transactions)
11. [Spring Data JPA](#spring-data-jpa)
12. [Best Practices](#best-practices)
13. [Common Interview Questions](#common-interview-questions)

---

## 🎓 What is JPA?

**JPA (Java Persistence API)** is a **Java specification** that provides a standard way to manage relational data in Java applications.

### Key Points:
- **Specification, Not Implementation**: JPA is a standard/interface, not an actual implementation
- **ORM (Object-Relational Mapping)**: Maps Java objects to database tables
- **Part of Java EE**: Included in Java Enterprise Edition
- **Vendor Independent**: Can work with different implementations (Hibernate, EclipseLink, etc.)

### What JPA Does:
1. **Maps Java Objects to Database Tables**: Converts Java classes to database tables
2. **Manages Object Lifecycle**: Handles creation, updates, deletion of entities
3. **Provides Query Language**: JPQL for database-independent queries
4. **Handles Relationships**: Manages one-to-one, one-to-many, many-to-many relationships
5. **Transaction Management**: Manages database transactions

### Simple Analogy:
Think of JPA as a **translator** between Java (object-oriented) and SQL (relational):
- **Java**: Objects, classes, inheritance
- **Database**: Tables, rows, columns
- **JPA**: Translates between them automatically

---

## 🔄 JPA vs Hibernate

### The Key Difference

**JPA** = **Specification** (the interface/contract)
**Hibernate** = **Implementation** (the actual code that implements JPA)

### Analogy:
- **JPA** is like a **car specification** (must have 4 wheels, engine, steering wheel)
- **Hibernate** is like a **Toyota car** (actual implementation of the specification)

### Detailed Comparison

| Aspect | JPA | Hibernate |
|--------|-----|-----------|
| **Type** | Specification/Interface | Implementation |
| **What it is** | Set of rules and APIs | Actual code/library |
| **Vendor** | Java Community (JSR) | Red Hat (JBoss) |
| **Portability** | Can switch implementations | Specific to Hibernate |
| **Features** | Standard features only | Additional features beyond JPA |
| **Usage** | Use JPA APIs | Use Hibernate APIs (or JPA APIs) |
| **Dependency** | Depends on implementation | Standalone ORM framework |

### Relationship Diagram

```
┌─────────────────────────────────────────┐
│           JPA (Specification)            │
│  - Defines interfaces                    │
│  - Defines annotations                   │
│  - Defines JPQL                          │
│  - Defines EntityManager API             │
└──────────────┬──────────────────────────┘
               │
       ┌───────┴────────┐
       │                 │
┌──────▼──────┐  ┌───────▼────────┐
│  Hibernate  │  │  EclipseLink   │
│ (Implementation)│ (Implementation)│
│             │  │                │
│ - Implements│  │ - Implements   │
│   JPA spec  │  │   JPA spec     │
│ - Adds extra│  │ - Adds extra   │
│   features  │  │   features    │
└─────────────┘  └────────────────┘
```

### Can You Use JPA Without Hibernate?

**Yes!** You can use other JPA implementations:
- **EclipseLink** (Oracle)
- **OpenJPA** (Apache)
- **DataNucleus**

### Can You Use Hibernate Without JPA?

**Yes!** Hibernate existed before JPA and can be used directly with its own APIs.

### Which Should You Use?

**For New Projects:**
- Use **JPA APIs** (vendor-independent, portable)
- Let Spring Boot choose implementation (usually Hibernate)

**For Existing Projects:**
- If using Hibernate directly, continue
- Consider migrating to JPA for portability

---

## 🏗️ JPA Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────┐
│         Java Application                │
│                                         │
│  ┌──────────────────────────────────┐   │
│  │      Entity Classes              │   │
│  │  @Entity                         │   │
│  │  @Table                          │   │
│  │  @Id, @Column, etc.              │   │
│  └──────────────┬───────────────────┘   │
│                 │                       │
│  ┌──────────────▼─────────────────────┐ │
│  │      JPA API (javax.persistence)   │ │
│  │  - EntityManager                   │ │
│  │  - EntityManagerFactory            │ │
│  │  - PersistenceContext              │ │
│  └──────────────┬─────────────────────┘ │
│                 │                       │
│  ┌──────────────▼─────────────────────┐ │
│  │      JPA Implementation            │ │
│  │  (Hibernate, EclipseLink, etc.)    │ │
│  │  - Converts JPA calls to SQL       │ │
│  │  - Manages connections             │ │
│  │  - Handles caching                 │ │
│  └──────────────┬─────────────────────┘ │
└─────────────────┼───────────────────────┘
                  │
         ┌────────▼────────┐
         │   Database      │
         │  (MySQL,        │
         │   PostgreSQL,   │
         │   Oracle, etc.) │
         └─────────────────┘
```

### JPA Layers

1. **Entity Layer**: Java classes annotated with JPA annotations
2. **JPA API Layer**: Standard interfaces (EntityManager, etc.)
3. **Implementation Layer**: Hibernate or other implementations
4. **Database Layer**: Actual database

---

## 🎯 Core JPA Components

### 1. **EntityManagerFactory**
- Creates EntityManager instances
- Expensive to create (usually one per application)
- Thread-safe

```java
EntityManagerFactory emf = Persistence.createEntityManagerFactory("my-persistence-unit");
```

### 2. **EntityManager**
- Main interface for JPA operations
- Manages entity lifecycle
- Not thread-safe (one per thread)
- Represents persistence context

```java
EntityManager em = emf.createEntityManager();
```

### 3. **Persistence Context**
- Set of managed entity instances
- Cache of entities in memory
- Tracks changes to entities
- Flushes changes to database

### 4. **Entity**
- Java class mapped to database table
- Annotated with `@Entity`
- Must have `@Id` field

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "user_name")
    private String name;
}
```

### 5. **Persistence Unit**
- Configuration in `persistence.xml`
- Defines database connection, entities, etc.

---

## 🔄 Entity Lifecycle

### Entity States

```
┌──────────┐
│  NEW     │  (Transient) - Not managed by JPA
└────┬─────┘
     │ persist()
     ↓
┌──────────┐
│ MANAGED  │  - Tracked by EntityManager
└────┬─────┘  - Changes automatically saved
     │
     ├─── remove() ───→ ┌──────────┐
     │                  │ REMOVED  │
     │                  └──────────┘
     │
     ├─── detach() ────→ ┌──────────┐
     │                   │ DETACHED │  - No longer managed
     │                   └──────────┘
     │
     └─── merge() ──────→┌──────────┐
                         │ MANAGED  │  (Re-attached)
                         └──────────┘
```

### Entity States Explained

#### 1. **NEW (Transient)**
- Object exists in memory
- Not associated with EntityManager
- Not in database

```java
User user = new User();  // NEW state
user.setName("John");
```

#### 2. **MANAGED (Persistent)**
- Object is managed by EntityManager
- Changes are tracked
- Automatically synchronized with database

```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();
em.persist(user);  // Now MANAGED
user.setName("Jane");  // Change tracked automatically
em.getTransaction().commit();  // Saves to database
```

#### 3. **DETACHED**
- Previously managed
- No longer tracked by EntityManager
- Changes not automatically saved

```java
em.detach(user);  // Now DETACHED
user.setName("Bob");  // Change NOT tracked
```

#### 4. **REMOVED**
- Marked for deletion
- Will be deleted from database on commit

```java
em.remove(user);  // Now REMOVED
em.getTransaction().commit();  // Deletes from database
```

### Lifecycle Methods

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @PrePersist
    public void beforePersist() {
        System.out.println("Before saving to database");
    }
    
    @PostPersist
    public void afterPersist() {
        System.out.println("After saving to database");
    }
    
    @PreUpdate
    public void beforeUpdate() {
        System.out.println("Before updating in database");
    }
    
    @PostUpdate
    public void afterUpdate() {
        System.out.println("After updating in database");
    }
    
    @PreRemove
    public void beforeRemove() {
        System.out.println("Before deleting from database");
    }
    
    @PostRemove
    public void afterRemove() {
        System.out.println("After deleting from database");
    }
    
    @PostLoad
    public void afterLoad() {
        System.out.println("After loading from database");
    }
}
```

---

## 📝 JPA Annotations

### Core Annotations

#### 1. **@Entity**
Marks a class as a JPA entity (maps to database table).

```java
@Entity
public class User {
    // ...
}
```

#### 2. **@Table**
Specifies the table name (optional if class name matches table name).

```java
@Entity
@Table(name = "users", schema = "public")
public class User {
    // ...
}
```

#### 3. **@Id**
Marks a field as primary key.

```java
@Entity
public class User {
    @Id
    private Long id;
}
```

#### 4. **@GeneratedValue**
Specifies how primary key is generated.

```java
@Id
@GeneratedValue(strategy = GenerationType.AUTO)  // Auto-increment
private Long id;

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)  // Database identity
private Long id;

@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE)  // Database sequence
private Long id;

@Id
@GeneratedValue(strategy = GenerationType.TABLE)  // Table generator
private Long id;
```

#### 5. **@Column**
Maps field to database column.

```java
@Column(name = "user_name", nullable = false, length = 100, unique = true)
private String name;
```

#### 6. **@Basic**
Marks a field as basic (default, usually omitted).

```java
@Basic(optional = false, fetch = FetchType.LAZY)
private String email;
```

#### 7. **@Transient**
Excludes field from persistence.

```java
@Transient
private String temporaryData;  // Not saved to database
```

#### 8. **@Temporal**
Maps date/time types.

```java
@Temporal(TemporalType.DATE)
private Date birthDate;

@Temporal(TemporalType.TIME)
private Date loginTime;

@Temporal(TemporalType.TIMESTAMP)
private Date createdAt;
```

#### 9. **@Lob**
Maps large objects (BLOB/CLOB).

```java
@Lob
private byte[] profilePicture;  // BLOB

@Lob
private String biography;  // CLOB
```

#### 10. **@Enumerated**
Maps enum types.

```java
@Enumerated(EnumType.STRING)  // Store as "ACTIVE", "INACTIVE"
private Status status;

@Enumerated(EnumType.ORDINAL)  // Store as 0, 1, 2
private Status status;
```

---

## 🔗 Entity Relationships

### 1. **@OneToOne**

One user has one profile.

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile profile;
}

@Entity
public class UserProfile {
    @Id
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### 2. **@OneToMany**

One user has many orders.

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders = new ArrayList<>();
}

@Entity
public class Order {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### 3. **@ManyToOne**

Many orders belong to one user (inverse of OneToMany).

```java
@Entity
public class Order {
    @Id
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
```

### 4. **@ManyToMany**

Many users have many roles.

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @ManyToMany
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();
}

@Entity
public class Role {
    @Id
    private Long id;
    
    @ManyToMany(mappedBy = "roles")
    private List<User> users = new ArrayList<>();
}
```

### Relationship Options

#### **Cascade Types**
```java
@OneToMany(cascade = CascadeType.ALL)  // All operations cascade
@OneToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE})  // Specific operations

// Available types:
// - CascadeType.PERSIST: Save child when parent saved
// - CascadeType.MERGE: Update child when parent updated
// - CascadeType.REMOVE: Delete child when parent deleted
// - CascadeType.REFRESH: Refresh child when parent refreshed
// - CascadeType.DETACH: Detach child when parent detached
// - CascadeType.ALL: All of the above
```

#### **Fetch Types**
```java
@OneToMany(fetch = FetchType.EAGER)   // Load immediately
@OneToMany(fetch = FetchType.LAZY)    // Load on demand

// EAGER: Load related entities immediately
// LAZY: Load related entities when accessed
```

#### **Orphan Removal**
```java
@OneToMany(orphanRemoval = true)  // Delete child when removed from collection
```

---

## 📊 JPA Query Language (JPQL)

### What is JPQL?

**JPQL (Java Persistence Query Language)** is a database-independent query language similar to SQL but works with entities instead of tables.

### JPQL vs SQL

| Aspect | JPQL | SQL |
|--------|------|-----|
| **Works with** | Entities (Java objects) | Tables (database) |
| **Database** | Database-independent | Database-specific |
| **Syntax** | `SELECT u FROM User u` | `SELECT * FROM users` |
| **Portability** | Works with any database | May need changes for different DBs |

### Basic JPQL Queries

#### 1. **Select All**
```java
// JPQL
String jpql = "SELECT u FROM User u";
TypedQuery<User> query = em.createQuery(jpql, User.class);
List<User> users = query.getResultList();

// Equivalent SQL
// SELECT * FROM users
```

#### 2. **Select with Where**
```java
// JPQL
String jpql = "SELECT u FROM User u WHERE u.name = :name";
TypedQuery<User> query = em.createQuery(jpql, User.class);
query.setParameter("name", "John");
User user = query.getSingleResult();

// Equivalent SQL
// SELECT * FROM users WHERE name = ?
```

#### 3. **Select with Join**
```java
// JPQL
String jpql = "SELECT u FROM User u JOIN u.orders o WHERE o.total > :amount";
TypedQuery<User> query = em.createQuery(jpql, User.class);
query.setParameter("amount", 100.0);
List<User> users = query.getResultList();
```

#### 4. **Aggregate Functions**
```java
// JPQL
String jpql = "SELECT COUNT(u) FROM User u";
Long count = em.createQuery(jpql, Long.class).getSingleResult();

String jpql2 = "SELECT AVG(o.total) FROM Order o";
Double avg = em.createQuery(jpql2, Double.class).getSingleResult();
```

#### 5. **Named Queries**
```java
@Entity
@NamedQuery(
    name = "User.findByName",
    query = "SELECT u FROM User u WHERE u.name = :name"
)
public class User {
    // ...
}

// Usage
TypedQuery<User> query = em.createNamedQuery("User.findByName", User.class);
query.setParameter("name", "John");
User user = query.getSingleResult();
```

### Native SQL Queries

```java
// Native SQL (database-specific)
String sql = "SELECT * FROM users WHERE name = ?";
Query query = em.createNativeQuery(sql, User.class);
query.setParameter(1, "John");
User user = (User) query.getSingleResult();
```

---

## 🔧 EntityManager and Persistence Context

### EntityManager Operations

#### 1. **persist()** - Save new entity
```java
EntityManager em = emf.createEntityManager();
em.getTransaction().begin();

User user = new User();
user.setName("John");
em.persist(user);  // Saves to database

em.getTransaction().commit();
```

#### 2. **find()** - Find by ID
```java
User user = em.find(User.class, 1L);  // Returns null if not found
```

#### 3. **getReference()** - Get proxy (lazy)
```java
User user = em.getReference(User.class, 1L);  // Returns proxy, loads on access
```

#### 4. **merge()** - Update detached entity
```java
User detachedUser = new User();
detachedUser.setId(1L);
detachedUser.setName("Jane");

em.getTransaction().begin();
User managedUser = em.merge(detachedUser);  // Updates in database
em.getTransaction().commit();
```

#### 5. **remove()** - Delete entity
```java
em.getTransaction().begin();
User user = em.find(User.class, 1L);
em.remove(user);  // Deletes from database
em.getTransaction().commit();
```

#### 6. **refresh()** - Reload from database
```java
User user = em.find(User.class, 1L);
user.setName("Changed");  // Changed in memory
em.refresh(user);  // Reloads from database, discards changes
```

#### 7. **detach()** - Remove from persistence context
```java
User user = em.find(User.class, 1L);
em.detach(user);  // No longer managed
```

#### 8. **clear()** - Clear entire persistence context
```java
em.clear();  // All entities become detached
```

#### 9. **flush()** - Synchronize with database
```java
em.persist(user);
em.flush();  // Forces SQL execution without committing
```

#### 10. **contains()** - Check if entity is managed
```java
boolean isManaged = em.contains(user);
```

### Persistence Context Types

#### 1. **Transaction-Scoped (Default)**
- Persistence context tied to transaction
- Created when transaction begins
- Closed when transaction ends

```java
@PersistenceContext(type = PersistenceContextType.TRANSACTION)
private EntityManager em;
```

#### 2. **Extended**
- Persistence context tied to stateful session
- Lives longer than transaction
- Can span multiple transactions

```java
@PersistenceContext(type = PersistenceContextType.EXTENDED)
private EntityManager em;
```

---

## 💼 Transactions

### Transaction Management

#### 1. **Resource-Local Transactions (JTA not available)**
```java
EntityManager em = emf.createEntityManager();
EntityTransaction tx = em.getTransaction();

try {
    tx.begin();
    
    User user = new User();
    user.setName("John");
    em.persist(user);
    
    tx.commit();
} catch (Exception e) {
    if (tx.isActive()) {
        tx.rollback();
    }
    throw e;
} finally {
    em.close();
}
```

#### 2. **JTA Transactions (Container-managed)**
```java
@Stateless
public class UserService {
    @PersistenceContext
    private EntityManager em;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void createUser(User user) {
        em.persist(user);  // Transaction managed by container
    }
}
```

### Transaction Attributes

```java
@TransactionAttribute(TransactionAttributeType.REQUIRED)    // Default
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
@TransactionAttribute(TransactionAttributeType.SUPPORTS)
@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@TransactionAttribute(TransactionAttributeType.NEVER)
```

---

## 🌱 Spring Data JPA

### What is Spring Data JPA?

**Spring Data JPA** is a **Spring framework** that simplifies JPA repository implementation.

### Benefits:
- **Less Boilerplate**: No need to write repository implementations
- **Automatic Queries**: Generate queries from method names
- **Paging and Sorting**: Built-in support
- **Custom Queries**: Easy to add custom queries

### Basic Repository

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Automatically provides:
    // - save()
    // - findById()
    // - findAll()
    // - delete()
    // - count()
    // etc.
}
```

### Query Methods

```java
public interface UserRepository extends JpaRepository<User, Long> {
    // Find by property
    List<User> findByName(String name);
    
    // Find with conditions
    List<User> findByNameAndEmail(String name, String email);
    List<User> findByNameOrEmail(String name, String email);
    
    // Find with comparison
    List<User> findByAgeGreaterThan(int age);
    List<User> findByAgeBetween(int min, int max);
    
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
}
```

### Usage in Service

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User createUser(User user) {
        return userRepository.save(user);
    }
    
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    public List<User> getUsersByName(String name) {
        return userRepository.findByName(name);
    }
}
```

---

## ✅ Best Practices

### 1. **Entity Design**
- ✅ Keep entities simple (POJOs)
- ✅ Use `@Entity` only for database-mapped classes
- ✅ Use `@Transient` for non-persistent fields
- ✅ Implement `equals()` and `hashCode()` correctly
- ✅ Use `@Version` for optimistic locking

### 2. **Relationships**
- ✅ Use `LAZY` fetching by default
- ✅ Use `EAGER` only when necessary
- ✅ Use `mappedBy` correctly to avoid duplicate columns
- ✅ Use `cascade` carefully (avoid `CascadeType.ALL` unless needed)

### 3. **Queries**
- ✅ Use JPQL for portability
- ✅ Use named queries for reusable queries
- ✅ Use native queries only when necessary
- ✅ Use parameterized queries (prevent SQL injection)

### 4. **Transactions**
- ✅ Keep transactions short
- ✅ Don't hold transactions open for user input
- ✅ Handle exceptions properly (rollback on error)
- ✅ Use `@Transactional` at service layer, not repository

### 5. **Performance**
- ✅ Use pagination for large result sets
- ✅ Use `@BatchSize` for collections
- ✅ Avoid N+1 query problem
- ✅ Use `@EntityGraph` for eager loading specific fields

### 6. **Entity Lifecycle**
- ✅ Understand entity states
- ✅ Use `merge()` for detached entities
- ✅ Use `persist()` for new entities
- ✅ Don't modify entities outside transaction

---

## 🎤 Common Interview Questions

### Q1: What is JPA?

**Answer:**
JPA (Java Persistence API) is a Java specification that provides a standard way to manage relational data in Java applications. It's an ORM (Object-Relational Mapping) framework that maps Java objects to database tables.

**Key Points:**
- Specification, not implementation
- Part of Java EE
- Vendor-independent
- Provides EntityManager API, JPQL, annotations

---

### Q2: What is the difference between JPA and Hibernate?

**Answer:**
- **JPA** is a **specification** (interface/contract)
- **Hibernate** is an **implementation** of JPA

**Analogy:** JPA is like a car specification, Hibernate is like a Toyota car.

**Details:**
- JPA defines the standard APIs and annotations
- Hibernate implements JPA and adds extra features
- You can use JPA with other implementations (EclipseLink, OpenJPA)
- Hibernate can be used directly without JPA

---

### Q3: What is an Entity?

**Answer:**
An Entity is a Java class that is mapped to a database table. It must:
- Be annotated with `@Entity`
- Have a primary key (`@Id`)
- Have a no-arg constructor
- Not be final

**Example:**
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    private Long id;
    
    @Column(name = "user_name")
    private String name;
}
```

---

### Q4: What is EntityManager?

**Answer:**
EntityManager is the main interface for JPA operations. It:
- Manages entity lifecycle
- Provides methods to persist, find, merge, remove entities
- Represents a persistence context
- Is not thread-safe (one per thread)

**Key Methods:**
- `persist()` - Save new entity
- `find()` - Find by ID
- `merge()` - Update detached entity
- `remove()` - Delete entity
- `flush()` - Synchronize with database

---

### Q5: What is Persistence Context?

**Answer:**
Persistence Context is a set of managed entity instances. It:
- Tracks entities in memory
- Tracks changes to entities
- Automatically synchronizes changes with database
- Acts as a cache

**Types:**
- **Transaction-scoped**: Tied to transaction
- **Extended**: Lives longer than transaction

---

### Q6: What are Entity States?

**Answer:**
Entities have 4 states:

1. **NEW (Transient)**: Not managed, not in database
2. **MANAGED (Persistent)**: Managed, tracked, synchronized
3. **DETACHED**: Previously managed, no longer tracked
4. **REMOVED**: Marked for deletion

---

### Q7: What is JPQL?

**Answer:**
JPQL (Java Persistence Query Language) is a database-independent query language that works with entities instead of tables.

**Example:**
```java
String jpql = "SELECT u FROM User u WHERE u.name = :name";
TypedQuery<User> query = em.createQuery(jpql, User.class);
```

**Difference from SQL:**
- JPQL works with entities (Java objects)
- SQL works with tables (database)
- JPQL is database-independent
- SQL is database-specific

---

### Q8: What is the difference between persist() and merge()?

**Answer:**

**persist():**
- Used for **new entities** (NEW state)
- Entity becomes MANAGED
- Throws exception if entity already exists

**merge():**
- Used for **detached entities**
- Creates new managed instance or updates existing
- Returns managed entity

**Example:**
```java
// persist() - for new entity
User newUser = new User();
em.persist(newUser);  // Saves to database

// merge() - for detached entity
User detachedUser = new User();
detachedUser.setId(1L);
User managedUser = em.merge(detachedUser);  // Updates in database
```

---

### Q9: What is the difference between find() and getReference()?

**Answer:**

**find():**
- Immediately loads entity from database
- Returns `null` if not found
- Eager loading

**getReference():**
- Returns proxy (lazy loading)
- Loads from database when accessed
- Throws exception if not found when accessed

**Example:**
```java
User user1 = em.find(User.class, 1L);  // Loads immediately
User user2 = em.getReference(User.class, 1L);  // Returns proxy
String name = user2.getName();  // Loads from database now
```

---

### Q10: What is CascadeType?

**Answer:**
CascadeType defines which operations should cascade from parent to child entities.

**Types:**
- `PERSIST`: Save child when parent saved
- `MERGE`: Update child when parent updated
- `REMOVE`: Delete child when parent deleted
- `REFRESH`: Refresh child when parent refreshed
- `DETACH`: Detach child when parent detached
- `ALL`: All of the above

**Example:**
```java
@OneToMany(cascade = CascadeType.ALL)
private List<Order> orders;  // All operations cascade to orders
```

---

### Q11: What is the difference between EAGER and LAZY fetching?

**Answer:**

**EAGER:**
- Loads related entities immediately
- Can cause performance issues (N+1 problem)
- Use when you always need the data

**LAZY:**
- Loads related entities on demand
- Better performance (loads only when needed)
- Default for `@OneToMany` and `@ManyToMany`
- Can cause `LazyInitializationException` if accessed outside transaction

**Example:**
```java
@OneToMany(fetch = FetchType.LAZY)  // Loads on access
private List<Order> orders;

@ManyToOne(fetch = FetchType.EAGER)  // Loads immediately
private User user;
```

---

### Q12: What is Spring Data JPA?

**Answer:**
Spring Data JPA is a Spring framework that simplifies JPA repository implementation. It:
- Reduces boilerplate code
- Generates queries from method names
- Provides built-in pagination and sorting
- Makes custom queries easy

**Example:**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByName(String name);  // Auto-generated query
}
```

---

### Q13: What is the N+1 Query Problem?

**Answer:**
N+1 Query Problem occurs when:
- 1 query to fetch parent entities
- N queries to fetch child entities (one per parent)

**Example:**
```java
// Problem: 1 query for users, N queries for orders
List<User> users = userRepository.findAll();
for (User user : users) {
    List<Order> orders = user.getOrders();  // N queries
}

// Solution: Use JOIN FETCH
@Query("SELECT u FROM User u JOIN FETCH u.orders")
List<User> findAllWithOrders();
```

---

### Q14: What is @GeneratedValue?

**Answer:**
`@GeneratedValue` specifies how primary key is generated.

**Strategies:**
- `AUTO`: Let JPA choose (default)
- `IDENTITY`: Database identity column (auto-increment)
- `SEQUENCE`: Database sequence
- `TABLE`: Table generator

**Example:**
```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;  // Auto-increment
```

---

### Q15: What is optimistic locking?

**Answer:**
Optimistic locking prevents concurrent updates by using a version field.

**How it works:**
- Entity has `@Version` field
- JPA increments version on update
- If version changed, update fails

**Example:**
```java
@Entity
public class User {
    @Id
    private Long id;
    
    @Version
    private Long version;  // Optimistic locking
}
```

---

## 🎓 Summary

### Key Takeaways:

1. **JPA** = Specification, **Hibernate** = Implementation
2. **EntityManager** = Main interface for JPA operations
3. **Persistence Context** = Cache of managed entities
4. **Entity States** = NEW, MANAGED, DETACHED, REMOVED
5. **JPQL** = Database-independent query language
6. **Spring Data JPA** = Simplifies repository implementation
7. **Relationships** = @OneToOne, @OneToMany, @ManyToOne, @ManyToMany
8. **Fetch Types** = EAGER (immediate) vs LAZY (on demand)

### Interview Preparation Tips:

1. **Understand the difference** between JPA and Hibernate
2. **Know entity lifecycle** and states
3. **Understand relationships** and cascade types
4. **Know JPQL** basics
5. **Understand Spring Data JPA** benefits
6. **Be ready to discuss** performance issues (N+1 problem)

---

## 📚 Additional Resources

- **JPA Specification**: https://jcp.org/en/jsr/detail?id=338
- **Hibernate Documentation**: https://hibernate.org/orm/documentation/
- **Spring Data JPA**: https://spring.io/projects/spring-data-jpa

---

**Remember**: JPA is a specification, Hibernate is an implementation. For interviews, focus on understanding JPA concepts, entity lifecycle, and relationships! 🚀

