# 🖥️ JVM (Java Virtual Machine) Components - Complete Tutorial

## 📖 Table of Contents
1. [What is JVM?](#what-is-jvm)
2. [JVM Architecture Overview](#jvm-architecture-overview)
3. [Class Loader Subsystem](#class-loader-subsystem)
4. [Runtime Data Areas](#runtime-data-areas)
5. [Execution Engine](#execution-engine)
6. [Garbage Collection](#garbage-collection)
7. [JVM Memory Model](#jvm-memory-model)
8. [How JVM Executes Java Code](#how-jvm-executes-java-code)
9. [JVM Lifecycle](#jvm-lifecycle)
10. [JVM Tuning Parameters](#jvm-tuning-parameters)

---

## 🎓 What is JVM?

**JVM (Java Virtual Machine)** is an abstract computing machine that:
- Provides runtime environment for Java bytecode
- Converts Java bytecode to machine code
- Manages memory automatically
- Provides platform independence (Write Once, Run Anywhere)

### JVM vs JRE vs JDK

```
┌─────────────────────────────────────┐
│           JDK (Java Development Kit)│
│  ┌───────────────────────────────┐  │
│  │    JRE (Java Runtime Env)     │  │
│  │  ┌─────────────────────────┐  │  │
│  │  │   JVM (Java Virtual     │  │  │
│  │  │        Machine)         │  │  │
│  │  └─────────────────────────┘  │  │
│  │  + Libraries                  │  │
│  └───────────────────────────────┘  │
│  + Development Tools (javac, etc)   │
└─────────────────────────────────────┘
```

**JDK**: Development tools + JRE  
**JRE**: Runtime environment (JVM + Libraries)  
**JVM**: Virtual machine that executes bytecode

---

## 🏗️ JVM Architecture Overview

### High-Level JVM Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    Java Application                         │
│              (MyProgram.java)                               │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│                    Java Compiler (javac)                    │
│              Converts .java → .class (bytecode)             │
└──────────────────────┬──────────────────────────────────────┘
                       ↓
┌────────────────────────────────────────────────────────────┐
│                    JVM (Java Virtual Machine)              │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │        1. Class Loader Subsystem                     │  │
│  │           - Loading                                  │  │
│  │           - Linking                                  │  │
│  │           - Initialization                           │  │
│  └──────────────────────────────────────────────────────┘  │
│                       ↓                                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │        2. Runtime Data Areas                         │  │
│  │           ┌──────────────────────────────────────┐   │  │
│  │           │ Method Area (Class metadata)         │   │  │
│  │           │ Heap (Objects)                       │   │  │
│  │           │ Stack (Method calls, local variables)│   │  │
│  │           │ PC Registers (Program counter)       │   │  │
│  │           │ Native Method Stack                  │   │  │
│  │           └──────────────────────────────────────┘   │  │
│  └──────────────────────────────────────────────────────┘  │
│                       ↓                                    │
│  ┌──────────────────────────────────────────────────────┐  │
│  │        3. Execution Engine                           │  │
│  │           - Interpreter                              │  │
│  │           - JIT Compiler                             │  │
│  │           - Garbage Collector                        │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────   │
│  │        4. Native Method Interface (JNI)              │  │
│  │           - Interface to native libraries            │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
                       ↓
┌─────────────────────────────────────────────────────────────┐
│                    Operating System                         │
│                    (Windows/Linux/Mac)                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Class Loader Subsystem

### Responsibilities

The Class Loader Subsystem performs three main functions:

1. **Loading**: Reads .class file and loads into memory
2. **Linking**: Verifies, prepares, and resolves references
3. **Initialization**: Executes static initializers

### Class Loading Process

```
┌─────────────────────────────────────────┐
│  1. LOADING                             │
│     - Reads .class file                 │
│     - Creates Class object              │
│     - Stores in Method Area             │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│  2. LINKING                             │
│     ┌──────────────────────────────┐    │
│     │ a. Verification              │    │
│     │    - Bytecode validation     │    │
│     │    - Security checks         │    │
│     └──────────────────────────────┘    │
│     ┌──────────────────────────────┐    │
│     │ b. Preparation               │    │
│     │    - Allocates memory        │    │
│     │    - Sets default values     │    │
│     └──────────────────────────────┘    │
│     ┌──────────────────────────────┐    │
│     │ c. Resolution                │    │
│     │    - Resolves references     │    │
│     │    - Symbolic → Direct refs  │    │
│     └──────────────────────────────┘    │
└─────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────┐
│  3. INITIALIZATION                      │
│     - Executes static blocks            │
│     - Initializes static variables      │
│     - Calls <clinit>() method           │
└─────────────────────────────────────────┘
```

### Types of Class Loaders

#### 1. **Bootstrap Class Loader** (Primordial)
- Loads core Java classes (rt.jar)
- Written in native code (C/C++)
- Parent of all class loaders
- Loads from: `JAVA_HOME/jre/lib`

#### 2. **Extension Class Loader** (Platform)
- Loads extension classes
- Child of Bootstrap Class Loader
- Loads from: `JAVA_HOME/jre/lib/ext`

#### 3. **Application/System Class Loader**
- Loads application classes
- Child of Extension Class Loader
- Loads from: Classpath

#### 4. **User-Defined Class Loader**
- Custom class loaders
- Can be created by developers

### Delegation Hierarchy Model

```
┌─────────────────────────────┐
│  Application Class Loader   │ ← Checks first
└──────────────┬──────────────┘
               ↓ (if not found)
┌─────────────────────────────┐
│  Extension Class Loader     │
└──────────────┬──────────────┘
               ↓ (if not found)
┌─────────────────────────────┐
│  Bootstrap Class Loader     │ ← Checks last
└─────────────────────────────┘
```

**Principle**: "Delegation Hierarchy" - Child class loader delegates to parent first.

### Example: Class Loading

```java
public class ClassLoadingDemo {
    static {
        System.out.println("Static block executed during initialization");
    }
    
    public static void main(String[] args) {
        System.out.println("Main method executed");
        
        // When you use a class, it gets loaded
        MyClass obj = new MyClass();
    }
}

class MyClass {
    static {
        System.out.println("MyClass loaded and initialized");
    }
}
```

**Output:**
```
Static block executed during initialization
Main method executed
MyClass loaded and initialized
```

---

## 💾 Runtime Data Areas

### Memory Areas in JVM

```
┌────────────────────────────────────────────────────────────┐
│              Runtime Data Areas (Memory)                   │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Method Area (Shared)                                │  │
│  │  - Class metadata                                    │  │
│  │  - Static variables                                  │  │
│  │  - Method information                                │  │
│  │  - Runtime constant pool                             │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Heap (Shared)                                       │  │
│  │  ┌──────────────────┐  ┌──────────────────┐          │  │
│  │  │  Young Generation│  │  Old Generation  │          │  │
│  │  │  - Eden          │  │  - Tenured       │          │  │
│  │  │  - Survivor S0   │  │                  │          │  │
│  │  │  - Survivor S1   │  │                  │          │  │
│  │  └──────────────────┘  └──────────────────┘          │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Stack (Per Thread)                                  │  │
│  │  - Method calls                                      │  │
│  │  - Local variables                                   │  │
│  │  - Method parameters                                 │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  PC Registers (Per Thread)                           │  │
│  │  - Current instruction pointer                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                                                            │
│  ┌──────────────────────────────────────────────────────┐  │
│  │  Native Method Stack (Per Thread)                    │  │
│  │  - Native method calls                               │  │
│  └──────────────────────────────────────────────────────┘  │
└────────────────────────────────────────────────────────────┘
```

### 1. Method Area (Metaspace in Java 8+)

**Purpose**: Stores class-level data

**Contains**:
- Class metadata (name, methods, fields)
- Static variables
- Method bytecode
- Runtime constant pool
- Constructor code

**Characteristics**:
- Shared across all threads
- Created at JVM startup
- Can be garbage collected (unloading classes)

**Example**:
```java
public class MethodAreaDemo {
    // Static variable stored in Method Area
    static int count = 0;
    
    // Method bytecode stored in Method Area
    public void increment() {
        count++;
    }
}
```

### 2. Heap Memory

**Purpose**: Stores all objects and arrays

**Structure**:
```
┌─────────────────────────────────────┐
│           Heap Memory               │
├─────────────────────────────────────┤
│  ┌───────────────────────────────┐  │
│  │   Young Generation            │  │
│  │   ┌─────────┐  ┌───────────┐  │  │
│  │   │  Eden   │  │ Survivor  │  │  │
│  │   │         │  │   S0/S1   │  │  │
│  │   └─────────┘  └───────────┘  │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │   Old Generation (Tenured)    │  │
│  │   - Long-lived objects        │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

**Heap Regions**:

#### Young Generation
- **Eden Space**: New objects created here
- **Survivor Space (S0, S1)**: Objects that survive minor GC

#### Old Generation (Tenured)
- Objects that survive multiple GC cycles
- Long-lived objects

**Heap Size**:
- Default: Varies by JVM
- Can be set: `-Xms` (initial), `-Xmx` (maximum)

**Example**:
```java
public class HeapDemo {
    public static void main(String[] args) {
        // Object created in Heap (Eden space)
        MyObject obj = new MyObject();
        
        // Array created in Heap
        int[] array = new int[1000];
    }
}
```

### 3. Stack Memory

**Purpose**: Stores method calls and local variables

**Structure**:
```
┌─────────────────────────────────────┐
│         Stack (Per Thread)          │
│  ┌───────────────────────────────┐  │
│  │   Frame: main()               │  │ ← Top
│  │   - Local variables           │  │
│  │   - Parameters                │  │
│  │   - Return address            │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │   Frame: method1()            │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │   Frame: method2()            │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘
```

**Stack Frame Contains**:
- Local variables
- Method parameters
- Return address
- Reference to constant pool
- Operand stack

**Example**:
```java
public class StackDemo {
    public static void main(String[] args) {
        int x = 10;  // Stored in stack frame
        int y = 20;  // Stored in stack frame
        int result = add(x, y);  // New stack frame for add()
    }
    
    public static int add(int a, int b) {
        // a, b, and result stored in stack frame
        int result = a + b;
        return result;
    }
}
```

**Stack Size**:
- Default: 1MB per thread
- Can be set: `-Xss` (e.g., `-Xss2m`)

### 4. PC Registers (Program Counter)

**Purpose**: Stores address of currently executing instruction

**Characteristics**:
- One per thread
- Small memory area
- Points to next instruction to execute

**Example**:
```
Thread 1: PC Register = 0x1000 (pointing to instruction at address 0x1000)
Thread 2: PC Register = 0x2000 (pointing to instruction at address 0x2000)
```

### 5. Native Method Stack

**Purpose**: Stores native method calls (C/C++ code)

**Characteristics**:
- One per thread
- Used for JNI (Java Native Interface)
- Can throw StackOverflowError if full

---

## ⚙️ Execution Engine

### Components

```
┌─────────────────────────────────────────┐
│         Execution Engine                │
├─────────────────────────────────────────┤
│  ┌───────────────────────────────────┐  │
│  │  1. Interpreter                   │  │
│  │     - Executes bytecode line by   │  │
│  │       line                        │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  2. JIT Compiler                  │  │
│  │     - Compiles hot code to native │  │
│  │     - Improves performance        │  │
│  └───────────────────────────────────┘  │
│  ┌───────────────────────────────────┐  │
│  │  3. Garbage Collector             │  │
│  │     - Reclaims unused memory      │  │
│  └───────────────────────────────────┘  │
└─────────────────────────────────────────┘
```

### 1. Interpreter

**How it works**:
- Reads bytecode line by line
- Converts to machine code
- Executes immediately

**Advantages**:
- Fast startup
- No compilation overhead

**Disadvantages**:
- Slower execution (repeated interpretation)

### 2. JIT Compiler (Just-In-Time)

**How it works**:
- Identifies "hot" code (frequently executed)
- Compiles to native machine code
- Caches compiled code
- Reuses compiled code

**Types**:
- **C1 Compiler (Client)**: Fast compilation, less optimization
- **C2 Compiler (Server)**: Slower compilation, more optimization

**Example**:
```java
// This method might be compiled by JIT if called frequently
public int calculate(int n) {
    int sum = 0;
    for (int i = 0; i < n; i++) {
        sum += i;  // Hot code - will be JIT compiled
    }
    return sum;
}
```

### 3. Garbage Collector

See [Garbage Collection](#garbage-collection) section below.

---

## 🗑️ Garbage Collection

### What is Garbage Collection?

Automatic memory management that:
- Identifies unused objects
- Reclaims memory
- Prevents memory leaks

### Object Lifecycle

```
Object Created (Eden)
        ↓
Minor GC (if survives → Survivor)
        ↓
Minor GC (if survives → Survivor)
        ↓
After multiple survivals → Old Generation
        ↓
Major GC (if unused → Collected)
```

### Types of Garbage Collectors

#### 1. **Serial GC**
- Single-threaded
- For small applications
- `-XX:+UseSerialGC`

#### 2. **Parallel GC** (Default in Java 8)
- Multi-threaded
- For throughput-oriented applications
- `-XX:+UseParallelGC`

#### 3. **G1 GC** (Garbage First)
- Low latency
- For large heap sizes
- `-XX:+UseG1GC`

#### 4. **ZGC** (Java 11+)
- Ultra-low latency
- For very large heaps
- `-XX:+UseZGC`

### GC Process

#### Minor GC (Young Generation)
```
Eden + Survivor S0 → Copy live objects → Survivor S1
Eden + Survivor S1 → Copy live objects → Survivor S0
(Alternates between S0 and S1)
```

#### Major GC (Old Generation)
```
Old Generation → Mark unused objects → Sweep → Compact
```

### GC Example

```java
public class GCDemo {
    public static void main(String[] args) {
        // Object created in Eden
        MyObject obj1 = new MyObject();
        
        // obj1 becomes eligible for GC
        obj1 = null;
        
        // Suggest GC (doesn't guarantee)
        System.gc();
        
        // Create more objects
        for (int i = 0; i < 1000; i++) {
            new MyObject();  // These will be GC'd
        }
    }
}
```

---

## 🧠 JVM Memory Model

### Memory Allocation Example

```java
public class MemoryModelDemo {
    // Static variable → Method Area
    static int classVariable = 100;
    
    // Instance variable → Heap (with object)
    int instanceVariable = 50;
    
    public static void main(String[] args) {
        // Local variable → Stack
        int localVar = 10;
        
        // Object → Heap (Eden)
        MemoryModelDemo obj = new MemoryModelDemo();
        
        // Array → Heap
        int[] array = new int[100];
    }
}
```

### Memory Layout

```
┌─────────────────────────────────────┐
│  Method Area                        │
│  - classVariable = 100              │
│  - Class metadata                   │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Stack (main method frame)          │
│  - localVar = 10                    │
│  - obj (reference to heap)          │
│  - array (reference to heap)        │
└─────────────────────────────────────┘
            ↓ (references)
┌─────────────────────────────────────┐
│  Heap                               │
│  - obj (instanceVariable = 50)      │
│  - array[100]                       │
└─────────────────────────────────────┘
```

---

## 🔄 How JVM Executes Java Code

### Complete Execution Flow

```
1. Write Java Code
   MyProgram.java
        ↓
2. Compile with javac
   javac MyProgram.java
        ↓
3. Generate Bytecode
   MyProgram.class (bytecode)
        ↓
4. JVM Starts
   java MyProgram
        ↓
5. Class Loader Loads
   - Reads .class file
   - Loads into Method Area
        ↓
6. Bytecode Verification
   - Security checks
   - Validation
        ↓
7. Execution Engine
   - Interpreter executes
   - JIT compiles hot code
        ↓
8. Runtime Execution
   - Objects in Heap
   - Method calls in Stack
   - GC manages memory
        ↓
9. Program Completes
   - JVM shuts down
   - Memory released
```

### Example Execution

```java
public class ExecutionDemo {
    static int count = 0;  // Method Area
    
    public static void main(String[] args) {
        // Stack frame for main()
        int x = 10;  // Stack
        int y = 20;  // Stack
        
        // Object in Heap
        ExecutionDemo obj = new ExecutionDemo();
        
        // Method call creates new stack frame
        int result = obj.add(x, y);
        
        System.out.println("Result: " + result);
    }
    
    public int add(int a, int b) {
        // Stack frame for add()
        return a + b;
    }
}
```

**Memory Allocation**:
- `count` → Method Area
- `x, y, result` → Stack
- `obj` → Heap
- `a, b` → Stack (in add() frame)

---

## 🔄 JVM Lifecycle

### JVM Startup to Shutdown

```
1. JVM Startup
   - Loads JVM
   - Initializes memory areas
   - Creates main thread
        ↓
2. Class Loading
   - Loads main class
   - Links and initializes
        ↓
3. Execution
   - Executes main() method
   - Creates objects
   - Manages memory
        ↓
4. Runtime
   - Threads running
   - GC running
   - JIT compiling
        ↓
5. Shutdown
   - All threads complete
   - Final GC
   - Releases resources
```

### Shutdown Hooks

```java
public class ShutdownDemo {
    public static void main(String[] args) {
        // Register shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("JVM shutting down...");
            // Cleanup code
        }));
        
        System.out.println("Application running...");
    }
}
```

---

## ⚙️ JVM Tuning Parameters

### Common JVM Options

#### Heap Size
```bash
-Xms512m          # Initial heap size
-Xmx2g            # Maximum heap size
-Xmn256m          # Young generation size
```

#### Garbage Collection
```bash
-XX:+UseG1GC                    # Use G1 GC
-XX:MaxGCPauseMillis=200        # Max GC pause time
-XX:+PrintGCDetails             # Print GC details
```

#### Stack Size
```bash
-Xss1m            # Stack size per thread
```

#### Metaspace (Java 8+)
```bash
-XX:MetaspaceSize=256m          # Initial metaspace
-XX:MaxMetaspaceSize=512m       # Max metaspace
```

### Example JVM Command

```bash
java -Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 MyProgram
```

---

## 📊 JVM Components Summary

| Component | Purpose | Memory Type |
|-----------|---------|-------------|
| **Class Loader** | Loads classes | N/A |
| **Method Area** | Class metadata | Shared |
| **Heap** | Objects & arrays | Shared |
| **Stack** | Method calls | Per Thread |
| **PC Register** | Instruction pointer | Per Thread |
| **Native Stack** | Native methods | Per Thread |
| **Interpreter** | Executes bytecode | N/A |
| **JIT Compiler** | Compiles hot code | N/A |
| **GC** | Memory management | N/A |

---

## ✅ Key Takeaways

1. **JVM** provides runtime environment for Java
2. **Class Loader** loads, links, and initializes classes
3. **Method Area** stores class metadata
4. **Heap** stores all objects
5. **Stack** stores method calls and local variables
6. **Execution Engine** executes bytecode
7. **Garbage Collector** manages memory automatically
8. **JIT Compiler** optimizes frequently executed code

---

## 🎓 Next Steps

1. Learn about different GC algorithms
2. Study JVM tuning and optimization
3. Understand memory leaks and how to avoid them
4. Explore JVM monitoring tools (jstat, jmap, jconsole)
5. Learn about JVM internals in depth

---

**Remember**: Understanding JVM components helps you write better Java code and optimize application performance! 🚀

