# 🖥️ JVM Components - Quick Reference

## 📋 JVM Architecture Overview

```
┌─────────────────────────────────────┐
│      Java Application (.java)       │
└──────────────┬──────────────────────┘
               ↓ javac
┌─────────────────────────────────────┐
│      Bytecode (.class)              │
└──────────────┬──────────────────────┘
               ↓ java
┌─────────────────────────────────────┐
│           JVM                       │
│  ┌──────────────────────────────┐   │
│  │ 1. Class Loader              │   │
│  │ 2. Runtime Data Areas        │   │
│  │ 3. Execution Engine          │   │
│  └──────────────────────────────┘   │
└─────────────────────────────────────┘
```

## 🎯 Core Components

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

## 📦 Class Loader Subsystem

### Process
```
1. LOADING
   - Reads .class file
   - Creates Class object
        ↓
2. LINKING
   a. Verification (bytecode validation)
   b. Preparation (allocate memory)
   c. Resolution (resolve references)
        ↓
3. INITIALIZATION
   - Execute static blocks
   - Initialize static variables
```

### Class Loader Types
```
Bootstrap Class Loader (rt.jar)
    ↓
Extension Class Loader (ext/)
    ↓
Application Class Loader (classpath)
    ↓
User-Defined Class Loader
```

## 💾 Runtime Data Areas

### Memory Layout

```
┌─────────────────────────────────────┐
│  Method Area (Shared)               │
│  - Class metadata                   │
│  - Static variables                 │
│  - Method bytecode                  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Heap (Shared)                      │
│  ┌──────────────┐  ┌─────────────┐  │
│  │ Young Gen    │  │ Old Gen     │  │
│  │ - Eden       │  │ - Tenured   │  │
│  │ - Survivor   │  │             │  │
│  └──────────────┘  └─────────────┘  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Stack (Per Thread)                 │
│  ┌───────────────────────────────┐  │
│  │ Frame: method1()              │  │
│  │ - Local variables             │  │
│  │ - Parameters                  │  │
│  └───────────────────────────────┘  │
│  ┌───────────────────────────────┐  │
│  │ Frame: method2()              │  │
│  └───────────────────────────────┘  │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  PC Register (Per Thread)           │
│  - Current instruction pointer      │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Native Method Stack (Per Thread)   │
│  - Native method calls              │
└─────────────────────────────────────┘
```

### Where Variables Are Stored

| Variable Type | Storage Location |
|---------------|------------------|
| Static variables | Method Area |
| Instance variables | Heap (with object) |
| Local variables | Stack |
| Object references | Stack (points to Heap) |
| Objects | Heap |
| Arrays | Heap |

## ⚙️ Execution Engine

### Components

```
┌─────────────────────────────────────┐
│  Interpreter                        │
│  - Executes bytecode line by line   │
│  - Fast startup                     │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  JIT Compiler                       │
│  - Compiles hot code                │
│  - Improves performance             │
│  - Caches compiled code             │
└─────────────────────────────────────┘

┌─────────────────────────────────────┐
│  Garbage Collector                  │
│  - Reclaims unused memory           │
│  - Automatic memory management      │
└─────────────────────────────────────┘
```

## 🗑️ Garbage Collection

### Object Lifecycle

```
Object Created (Eden)
    ↓
Minor GC → Survivor (if survives)
    ↓
Minor GC → Survivor (if survives)
    ↓
Old Generation (after multiple survivals)
    ↓
Major GC → Collected (if unused)
```

### GC Types

| GC Type | Use Case | Flag |
|---------|----------|------|
| Serial GC | Small apps | `-XX:+UseSerialGC` |
| Parallel GC | Throughput | `-XX:+UseParallelGC` |
| G1 GC | Low latency | `-XX:+UseG1GC` |
| ZGC | Very large heaps | `-XX:+UseZGC` |

### GC Process

**Minor GC (Young Generation)**:
- Eden + Survivor S0 → Copy live → Survivor S1
- Alternates between S0 and S1

**Major GC (Old Generation)**:
- Mark unused objects
- Sweep
- Compact

## 🧠 Memory Example

```java
public class MemoryDemo {
    // Method Area
    static int count = 0;
    
    public static void main(String[] args) {
        // Stack (main frame)
        int x = 10;
        
        // Heap (object)
        MemoryDemo obj = new MemoryDemo();
        
        // Stack (reference to heap)
        // Heap (array)
        int[] arr = new int[100];
    }
}
```

**Memory Allocation**:
- `count` → Method Area
- `x` → Stack
- `obj` reference → Stack
- `obj` object → Heap
- `arr` reference → Stack
- `arr` array → Heap

## 🔄 Execution Flow

```
1. Write Java Code (.java)
        ↓
2. Compile (javac) → Bytecode (.class)
        ↓
3. JVM Starts
        ↓
4. Class Loader loads class
        ↓
5. Bytecode verification
        ↓
6. Execution Engine executes
        ↓
7. Objects in Heap
        ↓
8. Method calls in Stack
        ↓
9. GC manages memory
        ↓
10. Program completes
```

## ⚙️ Common JVM Options

### Heap Size
```bash
-Xms512m          # Initial heap
-Xmx2g            # Max heap
-Xmn256m          # Young generation
```

### Garbage Collection
```bash
-XX:+UseG1GC                    # Use G1 GC
-XX:MaxGCPauseMillis=200        # Max pause time
-XX:+PrintGCDetails             # Print GC info
```

### Stack Size
```bash
-Xss1m            # Stack per thread
```

### Metaspace (Java 8+)
```bash
-XX:MetaspaceSize=256m          # Initial
-XX:MaxMetaspaceSize=512m       # Maximum
```

## 📊 Stack Frame Structure

```
┌─────────────────────────────┐
│  Stack Frame                │
├─────────────────────────────┤
│  Local Variables            │
│  Parameters                 │
│  Return Address             │
│  Operand Stack              │
│  Reference to Constant Pool │
└─────────────────────────────┘
```

## 🎯 Key Concepts

- **Write Once, Run Anywhere**: JVM provides platform independence
- **Bytecode**: Intermediate code that JVM executes
- **Heap**: Stores all objects (shared across threads)
- **Stack**: Stores method calls (one per thread)
- **GC**: Automatic memory management
- **JIT**: Just-In-Time compilation for performance
- **Class Loading**: Dynamic loading of classes at runtime

## 🔍 Memory Areas Summary

| Area | Shared/Per Thread | Contains | Size |
|------|-------------------|----------|------|
| Method Area | Shared | Class metadata | Fixed |
| Heap | Shared | Objects | Configurable |
| Stack | Per Thread | Method calls | 1MB default |
| PC Register | Per Thread | Instruction pointer | Small |
| Native Stack | Per Thread | Native calls | Configurable |

## ✅ Best Practices

1. ✅ Understand heap vs stack
2. ✅ Monitor memory usage
3. ✅ Tune GC for your application
4. ✅ Avoid memory leaks
5. ✅ Use appropriate data structures
6. ✅ Profile your application
7. ❌ Don't ignore OutOfMemoryError
8. ❌ Don't create unnecessary objects
9. ❌ Don't hold references unnecessarily

## 📚 Related Topics

- Garbage Collection algorithms
- JVM tuning and optimization
- Memory leaks and profiling
- JVM monitoring tools
- Performance optimization

---

**Quick Tip**: Understanding JVM components helps you write efficient Java code and optimize application performance! 🚀

