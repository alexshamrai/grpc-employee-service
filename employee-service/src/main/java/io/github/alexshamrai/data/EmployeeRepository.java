package io.github.alexshamrai.data;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Repository
public class EmployeeRepository {

    private static List<Employee> employees = new ArrayList<>();
    private static Set<String> departments = new HashSet<>();
    private final Random random = new Random();

    static {
        departments.add("IT");
        departments.add("HR");
        departments.add("Marketing");
        departments.add("Finance");

        employees.add(Employee.builder()
            .id(1)
            .name("John Doe")
            .age(28)
            .department("IT")
            .position("Software Engineer")
            .hiringYear(2021)
            .salary(70000.0)
            .build());

        employees.add(Employee.builder()
            .id(2)
            .name("Jane Smith")
            .age(32)
            .department("HR")
            .position("HR Manager")
            .hiringYear(2020)
            .salary(60000.0)
            .build());

        employees.add(Employee.builder()
            .id(3)
            .name("Alice Brown")
            .age(25)
            .department("Marketing")
            .position("Content Strategist")
            .hiringYear(2019)
            .salary(50000.0)
            .build());

        employees.add(Employee.builder()
            .id(4)
            .name("Bob Johnson")
            .age(30)
            .department("Finance")
            .position("Financial Analyst")
            .hiringYear(2022)
            .salary(80000.0)
            .build());
    }

    /**
     * Retrieves all employees.
     *
     * @return List of all employees.
     */
    public List<Employee> getAllEmployees() {
        return employees;
    }

    /**
     * Retrieves all available departments.
     *
     * @return List of department names.
     */
    public List<String> getDepartments() {
        return new ArrayList<>(departments);
    }

    /**
     * Saves an employee to the repository by assigning them a random unique ID.
     *
     * @param employee The employee to save.
     * @return The created employee with a unique ID.
     */
    public Employee save(Employee employee) {
        employee.setId(Math.abs(random.nextInt()));
        employees.add(employee);

        departments.add(employee.getDepartment());
        return employee;
    }

    /**
     * Finds an employee by ID.
     *
     * @param id The ID of the employee to find.
     * @return An Optional containing the found employee or empty if not found.
     */
    public Optional<Employee> findById(int id) {
        return employees.stream().filter(employee -> employee.getId() == id).findFirst();
    }

}