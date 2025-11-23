import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * COLLECTORS CONCEPT - How It Works
 * ============================================================================
 * 
 * WHAT ARE COLLECTORS?
 * -------------------
 * Collectors are helper objects that describe how to transform a Stream into 
 * a final result (like List, Map, Set, or a single value).
 * 
 * WHY DO WE NEED COLLECTORS?
 * --------------------------
 * Stream operations are lazy (intermediate) or eager (terminal).
 * Terminal operations END the stream and produce a result:
 *   - forEach() - just consumes, returns void
 *   - collect() - accumulates elements using a Collector, returns a collection
 *   - reduce() - reduces to a single value
 * 
 * collect() + Collectors = Transform Stream → Data Structure
 * 
 * BASIC PATTERN:
 * --------------
 * stream.collect(Collector)  ← Collector tells HOW to accumulate
 * 
 * COMMON COLLECTORS:
 * -----------------
 * 1. Collectors.toList()        → Stream → List
 * 2. Collectors.toSet()         → Stream → Set
 * 3. Collectors.toMap()         → Stream → Map
 * 4. Collectors.groupingBy()    → Stream → Map<K, List<V>> (groups by key)
 * 5. Collectors.maxBy()         → Stream → Optional<T> (finds maximum)
 * 6. Collectors.collectingAndThen() → Transform result after collection
 * 
 * IN THIS CODE:
 * ------------
 * We use THREE Collectors together:
 * 1. groupingBy() - Groups employees by department
 * 2. maxBy() - Finds max salary in each group
 * 3. collectingAndThen() - Unwraps Optional to get Employee directly
 * 
 * VISUAL FLOW:
 * -----------
 * Stream → [collect()] → Collector → Result
 * 
 * Without Collector: stream.max() → Optional<T>
 * With Collector:     stream.collect(Collector) → Collection/Map
 * 
 * NESTED COLLECTORS:
 * -----------------
 * groupingBy(classifier, downstreamCollector)
 *   ↓
 * First: Groups by classifier
 * Then:  Applies downstreamCollector to each group
 * Result: Transformed groups
 */
public class EmployeeStreamDemo {

	public static final class Department {
		private final int id;
		private final String name;

		public Department(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Department that = (Department) o;
			return id == that.id && Objects.equals(name, that.name);
		}

		@Override
		public int hashCode() {
			return Objects.hash(id, name);
		}

		@Override
		public String toString() {
			return "Department{" +
					"id=" + id +
					", name='" + name + '\'' +
					'}';
		}
	}

	public static final class Employee {
		private final int id;
		private final String name;
		private final Department department;
		private final double salary;

		public Employee(int id, String name, Department department, double salary) {
			this.id = id;
			this.name = name;
			this.department = department;
			this.salary = salary;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}

		public Department getDepartment() {
			return department;
		}

		public double getSalary() {
			return salary;
		}

		@Override
		public String toString() {
			return "Employee{" +
					"id=" + id +
					", name='" + name + '\'' +
					", department=" + department.getName() +
					", salary=" + salary +
					'}';
		}
	}

	public static void main(String[] args) {
		Department it = new Department(1, "IT");
		Department hr = new Department(2, "HR");
		Department finance = new Department(3, "Finance");

		List<Employee> employees = new ArrayList<>();
		employees.add(new Employee(101, "Alice", it, 95000));
		employees.add(new Employee(102, "Bob", it, 120000));
		employees.add(new Employee(103, "Charlie", hr, 70000));
		employees.add(new Employee(104, "Diana", hr, 88000));
		employees.add(new Employee(105, "Eve", finance, 99000));
		employees.add(new Employee(106, "Frank", finance, 123000));

		/* =====================================================================
		 * EXAMPLE 1: HOW COLLECTORS WORK - Step by Step
		 * =====================================================================
		 * 
		 * GOAL: Find highest salary employee in EACH department
		 * 
		 * STEP-BY-STEP EXECUTION:
		 * 
		 * INPUT Stream:
		 *   [Alice(IT-95k), Bob(IT-120k), Charlie(HR-70k), Diana(HR-88k), Eve(Finance-99k), Frank(Finance-123k)]
		 * 
		 * STEP 1: .stream()
		 *   ──────────────────────────────────────────────────────────────────
		 *   Creates a Stream from the List
		 * 
		 * STEP 2: .collect(Collectors.groupingBy(...))
		 *   ──────────────────────────────────────────────────────────────────
		 *   groupingBy() is a Collector that GROUPS elements by a key
		 *   
		 *   Syntax: groupingBy(classifier, downstreamCollector)
		 *     - classifier: Employee::getDepartment 
		 *       → Groups by department (IT, HR, Finance)
		 *     - downstreamCollector: What to do with each group
		 * 
		 *   After groupingBy(Employee::getDepartment):
		 *   Intermediate Map:
		 *     IT      → [Alice(95k), Bob(120k)]
		 *     HR      → [Charlie(70k), Diana(88k)]
		 *     Finance → [Eve(99k), Frank(123k)]
		 *   
		 *   But we want Map<Dept, Employee> not Map<Dept, List<Employee>>
		 *   So we need a downstreamCollector to process each group!
		 * 
		 * STEP 3: Collectors.collectingAndThen(...)
		 *   ──────────────────────────────────────────────────────────────────
		 *   collectingAndThen = "First collect, THEN transform"
		 *   
		 *   Syntax: collectingAndThen(collector, finisher)
		 *     - collector: What collection operation to do first
		 *     - finisher: Function to transform the result
		 * 
		 * STEP 4: Collectors.maxBy(...) (inside collectingAndThen)
		 *   ──────────────────────────────────────────────────────────────────
		 *   maxBy() finds the maximum element using a Comparator
		 *   
		 *   Comparator.comparingDouble(Employee::getSalary)
		 *     → Compares by salary field, finds highest
		 *   
		 *   maxBy returns Optional<Employee> because:
		 *     - Group might be empty (no employees)
		 *     - Optional safely handles "no result" case
		 * 
		 *   After maxBy in each group:
		 *     IT      → Optional[Bob(120k)]
		 *     HR      → Optional[Diana(88k)]
		 *     Finance → Optional[Frank(123k)]
		 * 
		 * STEP 5: Optional::get (finisher in collectingAndThen)
		 *   ──────────────────────────────────────────────────────────────────
		 *   This is the "THEN" part - unwraps Optional to get Employee
		 *   
		 *   Without this: Map<Dept, Optional<Employee>>
		 *   With this:    Map<Dept, Employee> ✓
		 * 
		 * FINAL RESULT:
		 *   IT      → Bob(120k)
		 *   HR      → Diana(88k)
		 *   Finance → Frank(123k)
		 * 
		 * KEY INSIGHT:
		 *   ──────────────────────────────────────────────────────────────────
		 *   Collectors can be NESTED! 
		 *   groupingBy() takes another Collector as 2nd parameter.
		 *   This lets us: GROUP → PROCESS EACH GROUP → TRANSFORM RESULT
		 * ===================================================================== */
		Map<Department, Employee> highestByDept = employees
				.stream()  // 1. Create Stream
				.collect(
					Collectors.groupingBy(
						Employee::getDepartment,  // 2. Group by department (IT, HR, Finance)
						
						// 3. For each group, process it with maxBy, then transform
						Collectors.collectingAndThen(
							Collectors.maxBy(
								Comparator.comparingDouble(Employee::getSalary)  // 4. Find max by salary
							),
							Optional::get  // 5. Unwrap Optional<Employee> → Employee
						)
					)
				);

		System.out.println("Highest salary per department:");
		highestByDept.forEach((dept, emp) ->
				System.out.println(dept.getName() + " -> " + emp));

		/* =====================================================================
		 * SIMPLER EXAMPLES: Basic Collectors Usage
		 * =====================================================================
		 * 
		 * EXAMPLE A: Collect to List
		 *   List<Employee> itEmployees = employees.stream()
		 *       .filter(e -> e.getDepartment().equals(it))
		 *       .collect(Collectors.toList());  ← Simple collector
		 * 
		 * EXAMPLE B: Collect to Set
		 *   Set<Department> depts = employees.stream()
		 *       .map(Employee::getDepartment)
		 *       .collect(Collectors.toSet());  ← Another simple collector
		 * 
		 * EXAMPLE C: Just groupingBy (without downstream)
		 *   Map<Department, List<Employee>> byDept = employees.stream()
		 *       .collect(Collectors.groupingBy(Employee::getDepartment));
		 *   Result: IT → [Alice, Bob], HR → [Charlie, Diana], etc.
		 * 
		 * ===================================================================== */

		/* =====================================================================
		 * EXAMPLE 2: When NOT to use Collectors
		 * =====================================================================
		 * 
		 * GOAL: Find highest salary in ONE specific department
		 * 
		 * WHY NOT USE COLLECTORS HERE?
		 *   - We're filtering FIRST (only IT department)
		 *   - Then finding ONE maximum value
		 *   - .max() is a terminal operation, returns Optional<Employee>
		 *   - No grouping needed = no need for groupingBy collector
		 * 
		 * COMPARISON:
		 *   - Example 1: Multiple groups → Use Collectors.groupingBy()
		 *   - Example 2: Single result → Use .max() directly
		 * 
		 * When to use Collectors vs direct operations:
		 *   - Multiple results? → Collectors
		 *   - Single result? → .max(), .min(), .findFirst(), etc.
		 * ===================================================================== */
		Optional<Employee> highestInIT = employees
				.stream()
				.filter(e -> e.getDepartment().equals(it))  // Filter first
				.max(Comparator.comparingDouble(Employee::getSalary));  // Then max (no Collector needed)

		System.out.println();
		System.out.println("Highest salary in IT department:");
		highestInIT.ifPresent(System.out::println);
	}
}


