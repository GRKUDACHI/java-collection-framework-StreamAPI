import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
public class collectiontest
{
    public static void main (String[] args)
    {
        // ArrayList is a dynamic array that can grow and shrink as needed.
        // ArrayList is a resizable array that can grow and shrink as needed.
        // ArrayList is a collection of elements that can be accessed by index.
        // ArraList Time Complexity is O(1) for add, remove, and get operations.
        // ArrayList 0(n) for search operation.
        // ArrayList allows duplicate elements.
        // ArrayList allows null elements.
        // ArrayList allows heterogeneous elements.
        // ArrayList allows dynamic size.
        // ArrayList allows random access.
        // ArrayList allows thread safe.
       ArrayList<Integer> list = new ArrayList<>();
       list.add(87);
       list.add(90);
       list.add(45);
       list.add(34);
       list.add(23);
       list.add(12);

       System.out.println(list);
       list.set(3,35);
       System.out.println(list);
       
       int lastindex = list.size()-1;
       System.out.println("The Last Index of the List Element is "+list.get(lastindex));

       for(int i=0;  i<list.size(); i++)
       {
        System.out.println(list.get(i));
    
       }

       list.sort(new numsort());
       System.out.println("The Sorted List is "+list);

       list.sort(new numsortdesc());
       System.out.println("The Sorted List in DESC order is "+list);

       String fruits[] = {"Banana", "Apple", "Orange", "Mango", "Pineapple"};

       List<String> fruitbasket = new ArrayList<>(Arrays.asList(fruits));

       System.out.println("The Fruits List is "+fruitbasket);

       fruitbasket.sort((o1,o2) ->o1.length() -o2.length());
       System.out.println("The Sorted List in ASC order is "+fruitbasket);

       fruitbasket.sort(new fruitsortdesc());
       System.out.println("The Sorted List in ASC order is "+fruitbasket);


       fruitbasket.get(2);
       System.out.println("The 2nd Index of the Fruits List is "+fruitbasket.get(2));



    // LinkedList is a linear data structure that is used to store the elements in a sequential manner.
    // LinkedList is a collection of elements that can be accessed by index.
    // LinkedList have object called node each node contains two part data and pointer.
    // Data part contains the element and pointer part contains the address of the next node.
    // LinkedList is better to insert and delete operation because there is no need to shift the elements.
    // LinkedList is slower to access the elements because it needs to traverse the list to access the elements.
    // LinkedList has more memory overhead because it needs to store the address of the next and previous node.
    // LinkedList is not thread safe.
    // LinkedList is not synchronized.
    // LinkedList is not reentrant.
    // LinkedList is not stack.
    // Time Complexity of LinkedList is O(1) for add, remove, and get operations.
    // Time Complexity of LinkedList is O(n) for search operation.
    // LinkedList allows duplicate elements.
    // LinkedList allows null elements.
    // LinkedList allows heterogeneous elements.
    // LinkedList allows dynamic size.
    // LinkedList allows random access.
    // LinkedList allows thread safe.
    // Add first and last element in LinkedList is O(1)
    // Remove first and last element in LinkedList is O(1)
    // Search element in LinkedList is O(n)
    // Sort element in LinkedList is O(nlogn)
    // Print element in LinkedList is O(n)
    // Print element in LinkedList is O(n)
       LinkedList<Integer> linklist = new LinkedList<>();
       linklist.add(87);
       linklist.add(90);
       linklist.add(45);
       linklist.add(34);
       linklist.add(23);
       linklist.add(12);

       System.out.println(linklist);
       linklist.addFirst(10);
       linklist.addLast(50);
       System.out.println(linklist);
       linklist.removeFirst();
       linklist.removeLast();
       System.out.println(linklist);
       linklist.sort(new numsort());
       System.out.println(linklist);
       linklist.sort(new numsortdesc());


       LinkedList<String> linklist2 = new LinkedList<>();
       linklist2.add("Apple");
       linklist2.add("Banana");
       linklist2.add("Orange");
       linklist2.add("Mango");
       linklist2.add("Pineapple");
       System.out.println(linklist2);
       linklist2.sort(new fruitsort());
       System.out.println(linklist2);

       System.out.println("The First Element of the Linklist is "+linklist2.getFirst());
       System.out.println("The Last Element of the Linklist is "+linklist2.getLast());
      //Linkedlist can removed the collection all element removeAll() method.
      //   linklist2.removeAll(linklist2);

      for(int j=0; j<linklist2.size(); j++)
      {
        System.out.println(linklist2.get(j));
      }

      //Vector is a part of java collection and it is called vector legacy class because vector is one of the java released feature.
      //Vector is a thread safe environment.
      //We can construct vector with specific capacity to resize capacity.
      //If the list is over then its resize 2.5 time of previous capacity.
      //Features remaining same as ArrayList.
      Vector<Integer> vector = new Vector<>(5,3);
      vector.add(10);
      vector.add(20);
      vector.add(30);

      System.out.println("The Vector is "+vector);
      System.out.println("The Default Vector Capacity is "+vector.capacity());
      System.out.println("The After Specific Increment Vector Capacity is "+vector.capacity());
      System.out.println("The Vector Elements are "+vector);
      System.out.println("The Vector Elements are "+vector.get(2));
     

     //Stack is a part of java collection and it is called stack legacy class because stack is one of the java released feature.
     //Stack is a thread safe environment.
     //We can construct stack with specific capacity to resize capacity.
     //If the list is over then its resize 2.5 time of previous capacity.
     //Features remaining same as ArrayList.
     // Stack follows LIFO principle Last in First Out which element add Last when pop() then last element removed first
     // Stack is extends Vector
     // Since Stack extends Vector then All vector Feature are Inherited in Stack Also and Thread-safe
     // Time Complexity ot Stack operation in 0(1)
     Stack<Integer> stack = new Stack<>();
     stack.push(10);
     stack.push(20);
     stack.push(30);
     stack.push(40);
     stack.add(50);
     stack.sort(new numsort());
     stack.add(60);
     System.out.println("The Sorted Stack is "+stack);
     System.out.println("The Stack is "+stack);
     System.out.println("The Stack Elements are "+stack.pop());
     System.out.println("The Stack Elements are top element "+stack.peek());
     System.out.println("The Stack Elements are "+stack);
     System.out.println("The Stack Elements size is "+stack.size());
     System.out.println("The Stack Elements are "+stack.isEmpty());
     System.out.println("The Stack Elements are "+stack.contains(10));
     System.out.println("The Stack Elements are "+stack.contains(20));
     System.out.println("The Stack Elements are "+stack.contains(90));
     System.out.println("The Stack Elements are "+stack.contains(100));
     for(int k=0; k<stack.size(); k++)
     {
        System.out.println(stack.get(k));
     }
    }

}
// Comparator is a interface that is used to compare the elements of the collection.
// Comparator is a functional interface that is used to compare the elements of the collection.
// Return Positive value then sorted in ASC order
// Return Negative value then sorted in DESC order
// Return 0 if o1 is equal to o2
class numsort implements Comparator<Integer>
{
    @Override
    public int compare(Integer o1, Integer o2)
    {
        return o1-o2;
    }
}
// Return negative value if o1 is less than o2
// Return positive value if o1 is greater than o2
// Return 0 if o1 is equal to o2
class numsortdesc implements Comparator<Integer>
{
    @Override
    public int compare(Integer o1, Integer o2)
    {
        return o2-o1;
    }
}
// for String length sort
class fruitsort implements Comparator<String>
{
    @Override
    public int compare(String o1, String o2)
    {
        return o1.length()-o2.length();
    }
}
class fruitsortdesc implements Comparator<String>
{
    @Override
    public int compare(String o1, String o2)
    {
        return o2.length() - o1.length();
    }
}