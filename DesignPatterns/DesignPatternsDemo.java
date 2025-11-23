/**
 * JAVA DESIGN PATTERNS - COMPREHENSIVE DEMO
 * =========================================
 * 
 * This file demonstrates the most common design patterns in Java
 * with practical, runnable examples.
 */

import java.util.*;

// ============================================
// CREATIONAL PATTERNS
// ============================================

// 1. SINGLETON PATTERN
// ====================
class DatabaseConnection {
    private static DatabaseConnection instance;
    private String connectionString;
    
    // Private constructor prevents external instantiation
    private DatabaseConnection() {
        this.connectionString = "jdbc:mysql://localhost:3306/mydb";
        System.out.println("✓ Database connection created");
    }
    
    // Public method to get the single instance
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    public void connect() {
        System.out.println("Connected to: " + connectionString);
    }
}

// 2. FACTORY PATTERN
// ==================
interface Animal {
    void makeSound();
}

class Dog implements Animal {
    public void makeSound() {
        System.out.println("🐕 Dog says: Woof! Woof!");
    }
}

class Cat implements Animal {
    public void makeSound() {
        System.out.println("🐱 Cat says: Meow! Meow!");
    }
}

class Bird implements Animal {
    public void makeSound() {
        System.out.println("🐦 Bird says: Tweet! Tweet!");
    }
}

class AnimalFactory {
    public Animal createAnimal(String type) {
        if (type == null) return null;
        
        switch (type.toLowerCase()) {
            case "dog": return new Dog();
            case "cat": return new Cat();
            case "bird": return new Bird();
            default: return null;
        }
    }
}

// 3. BUILDER PATTERN
// ==================
class Pizza {
    private String dough;
    private String sauce;
    private List<String> toppings;
    
    private Pizza(Builder builder) {
        this.dough = builder.dough;
        this.sauce = builder.sauce;
        this.toppings = builder.toppings;
    }
    
    public void display() {
        System.out.println("🍕 Pizza Details:");
        System.out.println("  Dough: " + dough);
        System.out.println("  Sauce: " + sauce);
        System.out.println("  Toppings: " + toppings);
    }
    
    static class Builder {
        private String dough;
        private String sauce;
        private List<String> toppings = new ArrayList<>();
        
        public Builder dough(String dough) {
            this.dough = dough;
            return this;
        }
        
        public Builder sauce(String sauce) {
            this.sauce = sauce;
            return this;
        }
        
        public Builder addTopping(String topping) {
            this.toppings.add(topping);
            return this;
        }
        
        public Pizza build() {
            return new Pizza(this);
        }
    }
}

// ============================================
// STRUCTURAL PATTERNS
// ============================================

// 4. ADAPTER PATTERN
// ==================
interface ModernPrinter {
    void print(String text);
}

class OldPrinter {
    void printOld(String text) {
        System.out.println("Old Printer: " + text);
    }
}

class PrinterAdapter implements ModernPrinter {
    private OldPrinter oldPrinter;
    
    public PrinterAdapter(OldPrinter oldPrinter) {
        this.oldPrinter = oldPrinter;
    }
    
    public void print(String text) {
        oldPrinter.printOld(text);
    }
}

// 5. DECORATOR PATTERN
// ====================
interface Coffee {
    String getDescription();
    double getCost();
}

class SimpleCoffee implements Coffee {
    public String getDescription() {
        return "Simple Coffee";
    }
    
    public double getCost() {
        return 5.0;
    }
}

abstract class CoffeeDecorator implements Coffee {
    protected Coffee coffee;
    
    public CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }
}

class MilkDecorator extends CoffeeDecorator {
    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }
    
    public String getDescription() {
        return coffee.getDescription() + ", Milk";
    }
    
    public double getCost() {
        return coffee.getCost() + 1.0;
    }
}

class SugarDecorator extends CoffeeDecorator {
    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }
    
    public String getDescription() {
        return coffee.getDescription() + ", Sugar";
    }
    
    public double getCost() {
        return coffee.getCost() + 0.5;
    }
}

// 6. FACADE PATTERN
// =================
class CPU {
    void start() {
        System.out.println("  ✓ CPU started");
    }
}

class Memory {
    void load() {
        System.out.println("  ✓ Memory loaded");
    }
}

class HardDrive {
    void read() {
        System.out.println("  ✓ Hard drive reading");
    }
}

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
        System.out.println("🖥️ Starting computer...");
        cpu.start();
        memory.load();
        hardDrive.read();
        System.out.println("✓ Computer started successfully!");
    }
}

// ============================================
// BEHAVIORAL PATTERNS
// ============================================

// 7. OBSERVER PATTERN
// ===================
interface Observer {
    void update(String news);
}

class NewsChannel implements Observer {
    private String name;
    
    public NewsChannel(String name) {
        this.name = name;
    }
    
    public void update(String news) {
        System.out.println("📺 " + name + " received: " + news);
    }
}

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

// 8. STRATEGY PATTERN
// ===================
interface PaymentStrategy {
    void pay(int amount);
}

class CreditCardPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("💳 Paid $" + amount + " using Credit Card");
    }
}

class PayPalPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("💰 Paid $" + amount + " using PayPal");
    }
}

class CashPayment implements PaymentStrategy {
    public void pay(int amount) {
        System.out.println("💵 Paid $" + amount + " using Cash");
    }
}

class ShoppingCart {
    private PaymentStrategy paymentStrategy;
    
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.paymentStrategy = strategy;
    }
    
    public void checkout(int amount) {
        paymentStrategy.pay(amount);
    }
}

// 9. COMMAND PATTERN
// ==================
interface Command {
    void execute();
    void undo();
}

class Light {
    private boolean isOn = false;
    
    void turnOn() {
        isOn = true;
        System.out.println("💡 Light is ON");
    }
    
    void turnOff() {
        isOn = false;
        System.out.println("💡 Light is OFF");
    }
}

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

class TurnOffCommand implements Command {
    private Light light;
    
    public TurnOffCommand(Light light) {
        this.light = light;
    }
    
    public void execute() {
        light.turnOff();
    }
    
    public void undo() {
        light.turnOn();
    }
}

class RemoteControl {
    private Command command;
    
    public void setCommand(Command command) {
        this.command = command;
    }
    
    public void pressButton() {
        command.execute();
    }
    
    public void pressUndo() {
        command.undo();
    }
}

// ============================================
// MAIN DEMO CLASS
// ============================================
public class DesignPatternsDemo {
    
    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   JAVA DESIGN PATTERNS - COMPREHENSIVE DEMONSTRATION");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // 1. SINGLETON PATTERN
        System.out.println("1️⃣  SINGLETON PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        DatabaseConnection db1 = DatabaseConnection.getInstance();
        DatabaseConnection db2 = DatabaseConnection.getInstance();
        System.out.println("Same instance? " + (db1 == db2));
        db1.connect();
        System.out.println();
        
        // 2. FACTORY PATTERN
        System.out.println("2️⃣  FACTORY PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        AnimalFactory factory = new AnimalFactory();
        Animal dog = factory.createAnimal("dog");
        Animal cat = factory.createAnimal("cat");
        Animal bird = factory.createAnimal("bird");
        dog.makeSound();
        cat.makeSound();
        bird.makeSound();
        System.out.println();
        
        // 3. BUILDER PATTERN
        System.out.println("3️⃣  BUILDER PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        Pizza pizza = new Pizza.Builder()
            .dough("Thin Crust")
            .sauce("Tomato")
            .addTopping("Cheese")
            .addTopping("Pepperoni")
            .addTopping("Mushrooms")
            .build();
        pizza.display();
        System.out.println();
        
        // 4. ADAPTER PATTERN
        System.out.println("4️⃣  ADAPTER PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        OldPrinter oldPrinter = new OldPrinter();
        ModernPrinter adapter = new PrinterAdapter(oldPrinter);
        adapter.print("Hello from modern interface!");
        System.out.println();
        
        // 5. DECORATOR PATTERN
        System.out.println("5️⃣  DECORATOR PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        Coffee coffee = new SimpleCoffee();
        System.out.println("Base: " + coffee.getDescription() + " - $" + coffee.getCost());
        
        coffee = new MilkDecorator(coffee);
        System.out.println("With Milk: " + coffee.getDescription() + " - $" + coffee.getCost());
        
        coffee = new SugarDecorator(coffee);
        System.out.println("With Milk & Sugar: " + coffee.getDescription() + " - $" + coffee.getCost());
        System.out.println();
        
        // 6. FACADE PATTERN
        System.out.println("6️⃣  FACADE PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        ComputerFacade computer = new ComputerFacade();
        computer.startComputer();
        System.out.println();
        
        // 7. OBSERVER PATTERN
        System.out.println("7️⃣  OBSERVER PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        NewsAgency agency = new NewsAgency();
        agency.addObserver(new NewsChannel("CNN"));
        agency.addObserver(new NewsChannel("BBC"));
        agency.addObserver(new NewsChannel("Fox News"));
        agency.notifyObservers("Breaking: Java Design Patterns are awesome! 🚀");
        System.out.println();
        
        // 8. STRATEGY PATTERN
        System.out.println("8️⃣  STRATEGY PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        ShoppingCart cart = new ShoppingCart();
        
        cart.setPaymentStrategy(new CreditCardPayment());
        cart.checkout(100);
        
        cart.setPaymentStrategy(new PayPalPayment());
        cart.checkout(200);
        
        cart.setPaymentStrategy(new CashPayment());
        cart.checkout(50);
        System.out.println();
        
        // 9. COMMAND PATTERN
        System.out.println("9️⃣  COMMAND PATTERN");
        System.out.println("─────────────────────────────────────────────────────");
        Light light = new Light();
        RemoteControl remote = new RemoteControl();
        
        remote.setCommand(new TurnOnCommand(light));
        remote.pressButton();
        
        remote.setCommand(new TurnOffCommand(light));
        remote.pressButton();
        
        remote.pressUndo(); // Undo last command
        System.out.println();
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("   ✅ All Design Patterns Demonstrated Successfully!");
        System.out.println("═══════════════════════════════════════════════════════");
    }
}

