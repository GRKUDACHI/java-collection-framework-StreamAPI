/**
 * JVM Components Demonstration
 * 
 * This file demonstrates various JVM components and concepts:
 * 1. Memory allocation (Heap vs Stack)
 * 2. Class loading
 * 3. Garbage collection
 * 4. Stack frames
 * 5. Object lifecycle
 * 
 * Note: Some concepts are demonstrated through code examples
 * and memory visualization
 */

public class JVMComponentsDemo {
    
    // Static variable → Stored in Method Area
    static int classVariable = 100;
    static String className = "JVMComponentsDemo";
    
    // Instance variable → Stored in Heap (with object)
    private int instanceVariable = 50;
    private String instanceName = "Demo Object";
    
    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("JVM COMPONENTS DEMONSTRATION");
        System.out.println("============================================");
        
        demonstrateMemoryAreas();
        demonstrateStackFrames();
        demonstrateHeapAllocation();
        demonstrateGarbageCollection();
        demonstrateClassLoading();
    }
    
    /**
     * ============================================
     * 1. MEMORY AREAS DEMONSTRATION
     * ============================================
     * Shows where different variables are stored
     */
    public static void demonstrateMemoryAreas() {
        System.out.println("\n--- 1. MEMORY AREAS ---");
        
        // Local variables → Stack (in main() frame)
        int localInt = 10;
        String localString = "Local";
        
        System.out.println("Method Area (Static):");
        System.out.println("  - classVariable = " + classVariable);
        System.out.println("  - className = " + className);
        
        System.out.println("\nStack (Local variables in main()):");
        System.out.println("  - localInt = " + localInt);
        System.out.println("  - localString = " + localString);
        
        // Object → Heap
        JVMComponentsDemo obj = new JVMComponentsDemo();
        
        System.out.println("\nHeap (Object):");
        System.out.println("  - obj.instanceVariable = " + obj.instanceVariable);
        System.out.println("  - obj.instanceName = " + obj.instanceName);
        System.out.println("  - obj reference stored in Stack");
    }
    
    /**
     * ============================================
     * 2. STACK FRAMES DEMONSTRATION
     * ============================================
     * Shows how method calls create stack frames
     */
    public static void demonstrateStackFrames() {
        System.out.println("\n--- 2. STACK FRAMES ---");
        
        System.out.println("Stack Frame Structure:");
        System.out.println("┌─────────────────────────┐");
        System.out.println("│ Frame: main()           │ ← Current frame");
        System.out.println("│ - Local variables       │");
        System.out.println("│ - Parameters            │");
        System.out.println("│ - Return address        │");
        System.out.println("└─────────────────────────┘");
        
        // Method call creates new stack frame
        int result = calculateSum(5, 10);
        System.out.println("\nAfter method call:");
        System.out.println("┌─────────────────────────┐");
        System.out.println("│ Frame: calculateSum()   │ ← New frame");
        System.out.println("│ - a = 5                 │");
        System.out.println("│ - b = 10                │");
        System.out.println("│ - result = 15           │");
        System.out.println("└─────────────────────────┘");
        System.out.println("┌─────────────────────────┐");
        System.out.println("│ Frame: main()           │ ← Previous frame");
        System.out.println("└─────────────────────────┘");
        
        System.out.println("Result: " + result);
    }
    
    /**
     * Creates a new stack frame when called
     * Parameters and local variables stored in stack
     */
    public static int calculateSum(int a, int b) {
        // These are stored in calculateSum() stack frame
        int result = a + b;
        return result;
    }
    
    /**
     * ============================================
     * 3. HEAP ALLOCATION DEMONSTRATION
     * ============================================
     * Shows object creation in heap
     */
    public static void demonstrateHeapAllocation() {
        System.out.println("\n--- 3. HEAP ALLOCATION ---");
        
        System.out.println("Heap Structure:");
        System.out.println("┌─────────────────────────┐");
        System.out.println("│   Young Generation       │");
        System.out.println("│   ┌───────────────────┐  │");
        System.out.println("│   │  Eden Space       │  │ ← New objects");
        System.out.println("│   └───────────────────┘  │");
        System.out.println("│   ┌───────────────────┐  │");
        System.out.println("│   │  Survivor S0/S1    │  │ ← Survived GC");
        System.out.println("│   └───────────────────┘  │");
        System.out.println("└─────────────────────────┘");
        System.out.println("┌─────────────────────────┐");
        System.out.println("│   Old Generation         │");
        System.out.println("│   - Long-lived objects  │");
        System.out.println("└─────────────────────────┘");
        
        // Create objects in heap (Eden space)
        System.out.println("\nCreating objects in Heap (Eden):");
        for (int i = 0; i < 5; i++) {
            MyObject obj = new MyObject(i, "Object-" + i);
            System.out.println("  Created: " + obj);
            // Object reference stored in Stack
            // Object data stored in Heap
        }
        
        // Array allocation in heap
        System.out.println("\nCreating array in Heap:");
        int[] numbers = new int[1000];
        System.out.println("  Array allocated: " + numbers.length + " elements");
    }
    
    /**
     * ============================================
     * 4. GARBAGE COLLECTION DEMONSTRATION
     * ============================================
     * Shows object lifecycle and GC
     */
    public static void demonstrateGarbageCollection() {
        System.out.println("\n--- 4. GARBAGE COLLECTION ---");
        
        System.out.println("Object Lifecycle:");
        System.out.println("1. Object created in Eden");
        System.out.println("2. Minor GC → If survives → Survivor");
        System.out.println("3. After multiple survivals → Old Generation");
        System.out.println("4. Major GC → If unused → Collected");
        
        // Create object
        MyObject obj1 = new MyObject(1, "Object-1");
        System.out.println("\nCreated: " + obj1);
        
        // Make object eligible for GC
        obj1 = null;
        System.out.println("Set reference to null → Object eligible for GC");
        
        // Create many objects (will trigger GC)
        System.out.println("\nCreating many objects (may trigger GC):");
        for (int i = 0; i < 10000; i++) {
            new MyObject(i, "Temp-" + i);
            // These objects become eligible for GC immediately
        }
        
        // Suggest GC (doesn't guarantee execution)
        System.out.println("\nSuggesting GC (System.gc())...");
        System.gc();
        System.out.println("GC may have collected unused objects");
    }
    
    /**
     * ============================================
     * 5. CLASS LOADING DEMONSTRATION
     * ============================================
     * Shows class loading process
     */
    public static void demonstrateClassLoading() {
        System.out.println("\n--- 5. CLASS LOADING ---");
        
        System.out.println("Class Loading Process:");
        System.out.println("1. Loading: Read .class file");
        System.out.println("2. Linking:");
        System.out.println("   a. Verification: Bytecode validation");
        System.out.println("   b. Preparation: Allocate memory");
        System.out.println("   c. Resolution: Resolve references");
        System.out.println("3. Initialization: Execute static blocks");
        
        // Static block executes during initialization
        System.out.println("\nLoading MyClass:");
        MyClass obj = new MyClass();
        System.out.println("Class loaded and initialized");
    }
}

/**
 * ============================================
 * HELPER CLASSES
 * ============================================
 */

/**
 * Sample class to demonstrate object creation
 * Objects of this class are stored in Heap
 */
class MyObject {
    private int id;
    private String name;
    
    public MyObject(int id, String name) {
        this.id = id;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "MyObject{id=" + id + ", name='" + name + "'}";
    }
}

/**
 * Sample class to demonstrate class loading
 * Static block executes during initialization phase
 */
class MyClass {
    // Static variable → Method Area
    static int count = 0;
    
    // Static block → Executes during initialization
    static {
        System.out.println("  [MyClass] Static block executed (Initialization phase)");
        count = 100;
    }
    
    // Instance variable → Heap (with object)
    private int value;
    
    public MyClass() {
        System.out.println("  [MyClass] Constructor called");
        this.value = count++;
    }
    
    public int getValue() {
        return value;
    }
}

/**
 * ============================================
 * MEMORY VISUALIZATION
 * ============================================
 * 
 * When you run this program, here's how memory is organized:
 * 
 * METHOD AREA (Shared):
 *   - JVMComponentsDemo.class metadata
 *   - MyObject.class metadata
 *   - MyClass.class metadata
 *   - Static variables (classVariable, className, count)
 * 
 * HEAP (Shared):
 *   - All MyObject instances
 *   - All MyClass instances
 *   - Arrays
 * 
 * STACK (Per Thread - main thread):
 *   ┌─────────────────────────┐
 *   │ Frame: main()            │
 *   │ - args                  │
 *   │ - local variables       │
 *   │ - object references     │
 *   └─────────────────────────┘
 *   ┌─────────────────────────┐
 *   │ Frame: calculateSum()    │ (when called)
 *   │ - a, b                  │
 *   │ - result                │
 *   └─────────────────────────┘
 * 
 * PC REGISTER:
 *   - Points to current instruction
 * 
 * NATIVE METHOD STACK:
 *   - Used for native method calls
 */

