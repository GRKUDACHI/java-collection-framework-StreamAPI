# 🎨 Java Design Patterns - Complete Tutorial

## 📖 Table of Contents
1. [What are Design Patterns?](#what-are-design-patterns)
2. [Types of Design Patterns](#types-of-design-patterns)
3. [Creational Patterns](#creational-patterns)
4. [Structural Patterns](#structural-patterns)
5. [Behavioral Patterns](#behavioral-patterns)
6. [When to Use Which Pattern?](#when-to-use-which-pattern)

---

## 🎯 What are Design Patterns?

### Definition
**Design Patterns** are **reusable solutions** to common problems that occur in software design. They are like **templates** or **blueprints** that you can customize to solve a particular design problem in your code.

### Real-World Analogy
Think of design patterns like **cooking recipes**:
- A recipe (pattern) tells you the **general approach** to make a dish
- You can **customize** it with different ingredients (your specific code)
- Many chefs (developers) use the same recipes (patterns) because they work well

### Why Use Design Patterns?
- ✅ **Proven Solutions**: Tested solutions to common problems
- ✅ **Code Reusability**: Reuse solutions instead of reinventing
- ✅ **Better Communication**: Common vocabulary among developers
- ✅ **Maintainability**: Easier to understand and modify code
- ✅ **Best Practices**: Industry-standard approaches

### Key Principles
1. **DRY (Don't Repeat Yourself)**: Avoid code duplication
2. **SOLID Principles**: Single Responsibility, Open/Closed, etc.
3. **Separation of Concerns**: Divide code into logical sections
4. **Loose Coupling**: Minimize dependencies between classes

---

## 📚 Types of Design Patterns

Design patterns are categorized into **three main types**:

### 1. **Creational Patterns** (5 patterns)
**Purpose**: How objects are created
- Singleton
- Factory
- Abstract Factory
- Builder
- Prototype

### 2. **Structural Patterns** (7 patterns)
**Purpose**: How objects are composed/structured
- Adapter
- Decorator
- Facade
- Proxy
- Bridge
- Composite
- Flyweight

### 3. **Behavioral Patterns** (11 patterns)
**Purpose**: How objects interact and communicate
- Observer
- Strategy
- Command
- Template Method
- Iterator
- State
- Chain of Responsibility
- Mediator
- Memento
- Visitor
- Interpreter

---

## 🏗️ Creational Patterns

### 1. Singleton Pattern

**Purpose**: Ensure only **ONE instance** of a class exists.

**When to Use**:
- Database connections
- Logging systems
- Configuration managers
- Cache managers

**Example**:
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    
    private DatabaseConnection() { } // Private constructor
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
}

// Usage
DatabaseConnection db1 = DatabaseConnection.getInstance();
DatabaseConnection db2 = DatabaseConnection.getInstance();
// db1 and db2 are the SAME instance
```

**Your Codebase**: See `OopsConcept/SingletonEasyTutorial.java`

---

### 2. Factory Pattern

**Purpose**: Create objects without specifying the exact class.

**When to Use**:
- When you don't know which class to instantiate
- When object creation is complex
- When you want to decouple object creation from usage

**Example**:
```java
// Product interface
interface Animal {
    void makeSound();
}

// Concrete products
class Dog implements Animal {
    public void makeSound() { System.out.println("Woof!"); }
}

class Cat implements Animal {
    public void makeSound() { System.out.println("Meow!"); }
}

// Factory
class AnimalFactory {
    public Animal createAnimal(String type) {
        if (type.equals("dog")) return new Dog();
        if (type.equals("cat")) return new Cat();
        return null;
    }
}

// Usage
AnimalFactory factory = new AnimalFactory();
Animal dog = factory.createAnimal("dog");
dog.makeSound(); // Woof!
```

---

### 3. Builder Pattern

**Purpose**: Construct complex objects step by step.

**When to Use**:
- When object has many optional parameters
- When you want to make object creation more readable
- When you want to create immutable objects

**Example**:
```java
class Pizza {
    private String dough;
    private String sauce;
    private String topping;
    
    private Pizza(Builder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.topping = builder.topping;
    }
    
    static class Builder {
        private String dough;
        private String sauce;
        private String topping;
        
        public Builder dough(String dough) {
            this.dough = dough;
            return this;
        }
        
        public Builder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }
        
        public Builder topping(String topping) {
            this.topping = topping;
            return this;
        }
        
        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// Usage
Pizza pizza = new Pizza.Builder()
    .dough("thin")
    .sauce("tomato")
    .topping("cheese")
    .build();
```

---

## 🏛️ Structural Patterns

### 1. Adapter Pattern

**Purpose**: Allow incompatible interfaces to work together.

**When to Use**:
- When you need to use an existing class with incompatible interface
- When integrating third-party libraries
- When you want to reuse existing code

**Example**:
```java
// Old interface (incompatible)
interface OldPrinter {
    void printOld(String text);
}

// New interface (what we want)
interface ModernPrinter {
    void print(String text);
}

// Adapter
class PrinterAdapter implements ModernPrinter {
    private OldPrinter oldPrinter;
    
    public PrinterAdapter(OldPrinter oldPrinter) {
        this.oldPrinter = oldPrinter;
    }
    
    public void print(String text) {
        oldPrinter.printOld(text); // Adapting old to new
    }
}
```

---

### 2. Decorator Pattern

**Purpose**: Add new functionality to objects dynamically.

**When to Use**:
- When you want to add features without modifying existing code
- When inheritance is not suitable
- When you want flexible feature combination

**Example**:
```java
// Base component
interface Coffee {
    String getDescription();
    double getCost();
}

// Concrete component
class SimpleCoffee implements Coffee {
    public String getDescription() { return "Simple Coffee"; }
    public double getCost() { return 5.0; }
}

// Decorator
abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

// Concrete decorators
class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) { super(coffee); }
    
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
    
    public double getCost() {
        return coffee.getCost() + 1.0;
    }
}

// Usage
Coffee coffee = new SimpleCoffee();
coffee = new MilkDecorator(coffee);
System.out.println(coffee.getDescription()); // "Simple Coffee, Milk"
System.out.println(coffee.getCost()); // 6.0
```

---

### 3. Facade Pattern

**Purpose**: Provide a simple interface to a complex subsystem.

**When to Use**:
- When you want to hide complexity
- When you want to provide a simple API
- When integrating multiple subsystems

**Example**:
```java
// Complex subsystems
class CPU {
    void start() { System.out.println("CPU started"); }
}

class Memory {
    void load() { System.out.println("Memory loaded"); }
}

class HardDrive {
    void read() { System.out.println("Hard drive reading"); }
}

// Facade (simple interface)
class ComputerFacade {
    private CPU cpu;
    private Memory memory;
    private HardDrive hardDrive;
    
    public ComputerFacade() {
        this.cpu = new CPU();
        this.memory = new Memory();
        this.hardDrive = new HardDrive();
    }
    
    public void startComputer() {
        cpu.start();
        memory.load();
        hardDrive.read();
        System.out.println("Computer started!");
    }
}

// Usage
ComputerFacade computer = new ComputerFacade();
computer.startComputer(); // Simple one call instead of 3
```

---

## 🎭 Behavioral Patterns

### 1. Observer Pattern

**Purpose**: Notify multiple objects about state changes.

**When to Use**:
- Event handling systems
- Model-View architecture
- When one object needs to notify many others

**Example**:
```java
// Observer interface
interface Observer {
    void update(String message);
}

// Subject (observable)
class NewsAgency {
    private List<Observer> observers = new ArrayList<>();
    
    public void addObserver(Observer observer) {
        observers.add(observer);
    }
    
    public void notifyObservers(String news) {
        for (Observer observer : observers) {
            observer.update(news);
        }
    }
}

// Concrete observers
class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    public void update(String message) {
        System.out.println(name + " received: " + message);
    }
}

// Usage
NewsAgency agency = new NewsAgency();
agency.addObserver(new NewsChannel("CNN"));
agency.addObserver(new NewsChannel("BBC"));
agency.notifyObservers("Breaking news!");
```

---

### 2. Strategy Pattern

**Purpose**: Define a family of algorithms and make them interchangeable.

**When to Use**:
- When you have multiple ways to do something
- When you want to switch algorithms at runtime
- When you want to avoid if-else chains

**Example**:
```java
// Strategy interface
interface PaymentStrategy {
    void pay(int amount);
}

// Concrete strategies
class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using Credit Card");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("Paid $" + amount + " using PayPal");
    }
}

// Context
class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(int amount) {
        paymentStrategy.pay(amount);
    }
}

// Usage
ShoppingCart cart = new ShoppingCart();
cart.setPaymentStrategy(new CreditCardPayment());
cart.checkout(100); // Paid $100 using Credit Card

cart.setPaymentStrategy(new PayPalPayment());
cart.checkout(200); // Paid $200 using PayPal
```

---

### 3. Command Pattern

**Purpose**: Encapsulate requests as objects.

**When to Use**:
- Undo/redo functionality
- Queue operations
- Remote procedure calls
- Logging requests

**Example**:
```java
// Command interface
interface Command {
    void execute();
    void undo();
}

// Receiver
class Light {
    void turnOn() { System.out.println("Light is ON"); }
    void turnOff() { System.out.println("Light is OFF"); }
}

// Concrete command
class TurnOnCommand implements Command {
    private Light light;
    
    public TurnOnCommand(Light light) {
        this.light = light;
    }
    
    public void execute() {
        light.turnOn();
    }
    
    public void undo() {
        light.turnOff();
    }
}

// Invoker
class RemoteControl {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
}

// Usage
Light light = new Light();
Command turnOn = new TurnOnCommand(light);
RemoteControl remote = new RemoteControl();
remote.setCommand(turnOn);
remote.pressButton(); // Light is ON
```

---

## 🎯 When to Use Which Pattern?

### Quick Decision Guide

| Problem | Pattern to Use |
|---------|---------------|
| Need only one instance | **Singleton** |
| Don't know which class to create | **Factory** |
| Complex object creation | **Builder** |
| Incompatible interfaces | **Adapter** |
| Add features dynamically | **Decorator** |
| Hide complex subsystem | **Facade** |
| Notify multiple objects | **Observer** |
| Multiple algorithms | **Strategy** |
| Undo/redo operations | **Command** |

---

## 📊 Pattern Comparison

### Creational Patterns

| Pattern | Complexity | Use Case |
|---------|-----------|----------|
| Singleton | ⭐ | One instance needed |
| Factory | ⭐⭐ | Object creation logic |
| Builder | ⭐⭐⭐ | Complex object construction |

### Structural Patterns

| Pattern | Complexity | Use Case |
|---------|-----------|----------|
| Adapter | ⭐⭐ | Interface compatibility |
| Decorator | ⭐⭐⭐ | Dynamic feature addition |
| Facade | ⭐⭐ | Simplify complex system |

### Behavioral Patterns

| Pattern | Complexity | Use Case |
|---------|-----------|----------|
| Observer | ⭐⭐ | Event notifications |
| Strategy | ⭐⭐ | Algorithm selection |
| Command | ⭐⭐⭐ | Request encapsulation |

---

## 💡 Best Practices

1. **Don't Overuse**: Not every problem needs a pattern
2. **Understand First**: Know the problem before choosing a pattern
3. **Start Simple**: Use simple solutions first, add patterns when needed
4. **Follow SOLID**: Patterns should follow SOLID principles
5. **Document**: Explain why you used a pattern

---

## 🚀 Learning Path

1. **Start with**: Singleton, Factory, Observer (most common)
2. **Then learn**: Builder, Strategy, Adapter
3. **Advanced**: Command, Decorator, Facade
4. **Practice**: Implement patterns in real projects

---

## 📚 Summary

- **Design Patterns** = Reusable solutions to common problems
- **3 Types**: Creational, Structural, Behavioral
- **23 Patterns** total (we covered 9 most common)
- **Use wisely**: Don't force patterns where simple code works
- **Practice**: Implement patterns to understand them better

---

**Remember**: Design patterns are tools, not goals. Use them when they solve real problems! 🎉

