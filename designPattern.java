import java.util.*;
//Creational Patterns
//Factory Pattern 
//Create objects without specifying the exact class
/*When you don't know which class to instantiate
When object creation is complex
When you want to decouple object creation from usage*/
class designPattern
{
    public static void main (String[] args)
    {
        
        AnimalFactory factory = new AnimalFactory();
        Animal dog = factory.createAnimal("dog");
        dog.makeSound(); // Woof!
    }
}

interface Animal 
{
    void makeSound();
}


class Dog implements Animal 
{
    public void makeSound() 
    {
        System.out.println("Woof!"); 
   }
}

class Cat implements Animal 
{
    public void makeSound()
    {
        System.out.println("Meow!");
    }
}


class AnimalFactory 
{
    public Animal createAnimal(String type) 
    {
        if (type.equals("dog")) return new Dog();
        if (type.equals("cat")) return new Cat();
        return null;
    }
}