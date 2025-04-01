package io.github.alexshamrai;

import com.google.protobuf.Empty;
import io.github.alexshamrai.data.Employee;
import io.github.alexshamrai.grpc.AddEmployeeRequest;
import io.github.alexshamrai.grpc.AddEmployeeResponse;
import io.github.alexshamrai.grpc.Department;
import io.github.alexshamrai.grpc.EmployeeFilterRequest;
import io.github.alexshamrai.grpc.EmployeeListResponse;
import io.github.alexshamrai.grpc.GetAllEmployeesResponse;
import io.github.alexshamrai.grpc.GetDepartmentsResponse;
import io.github.alexshamrai.grpc.GetEmployeeRequest;
import io.github.alexshamrai.grpc.GetEmployeeResponse;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeTest extends BaseTest {

    @Test
    @DisplayName("Add new employee")
    public void addEmployeeTest() {
        Employee testEmployee = Employee.builder()
            .name("Aaron Lennon")
            .age(32)
            .department("IT")
            .position("Support")
            .hiringYear(2020)
            .salary(1500.0)
            .build();

        AddEmployeeRequest request = AddEmployeeRequest.newBuilder()
            .setName(testEmployee.getName())
            .setAge(testEmployee.getAge())
            .setDepartment(testEmployee.getDepartment())
            .setPosition(testEmployee.getPosition())
            .setHiringYear(testEmployee.getHiringYear())
            .setSalary(testEmployee.getSalary())
            .build();
        AddEmployeeResponse addEmployeeResponse = blockingStub.addEmployee(request);

        GetEmployeeRequest getRequest = GetEmployeeRequest.newBuilder()
            .setEmployeeId(addEmployeeResponse.getEmployeeId())
            .build();
        GetEmployeeResponse getResponse = blockingStub.getEmployee(getRequest);

        assertEquals(testEmployee.getName(), getResponse.getEmployee().getName());
        assertEquals(testEmployee.getAge(), getResponse.getEmployee().getAge());
        assertEquals(testEmployee.getDepartment(), getResponse.getEmployee().getDepartment());
        assertEquals(testEmployee.getPosition(), getResponse.getEmployee().getPosition());
        assertEquals(testEmployee.getHiringYear(), getResponse.getEmployee().getHiringYear());
        assertEquals(testEmployee.getSalary(), getResponse.getEmployee().getSalary());
    }

    @Test
    @DisplayName("Add new employee with negative salary")
    public void addEmployeeWithNegativeSalaryTest() {
        Employee testEmployee = Employee.builder()
            .name("Theo Black")
            .age(45)
            .department("Finance")
            .position("Accounting")
            .hiringYear(2022)
            .salary(-500.0)
            .build();

        AddEmployeeRequest request = AddEmployeeRequest.newBuilder()
            .setName(testEmployee.getName())
            .setAge(testEmployee.getAge())
            .setDepartment(testEmployee.getDepartment())
            .setPosition(testEmployee.getPosition())
            .setHiringYear(testEmployee.getHiringYear())
            .setSalary(testEmployee.getSalary())
            .build();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.addEmployee(request));
        assertEquals("INVALID_ARGUMENT: Employee salary must be greater than 0", exception.getMessage());
    }

    @Test
    @DisplayName("Add new employee with age below minimum")
    public void addEmployeeWithAgeBelowMinimumTest() {
        Employee testEmployee = Employee.builder()
            .name("Young Person")
            .age(17)
            .department("IT")
            .position("Intern")
            .hiringYear(2023)
            .salary(1000.0)
            .build();

        AddEmployeeRequest request = AddEmployeeRequest.newBuilder()
            .setName(testEmployee.getName())
            .setAge(testEmployee.getAge())
            .setDepartment(testEmployee.getDepartment())
            .setPosition(testEmployee.getPosition())
            .setHiringYear(testEmployee.getHiringYear())
            .setSalary(testEmployee.getSalary())
            .build();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.addEmployee(request));
        assertEquals("INVALID_ARGUMENT: Employee age must be between 18 and 60", exception.getMessage());
    }

    @Test
    @DisplayName("Add new employee with age above maximum")
    public void addEmployeeWithAgeAboveMaximumTest() {
        Employee testEmployee = Employee.builder()
            .name("Old Person")
            .age(61)
            .department("Finance")
            .position("Consultant")
            .hiringYear(2023)
            .salary(5000.0)
            .build();

        AddEmployeeRequest request = AddEmployeeRequest.newBuilder()
            .setName(testEmployee.getName())
            .setAge(testEmployee.getAge())
            .setDepartment(testEmployee.getDepartment())
            .setPosition(testEmployee.getPosition())
            .setHiringYear(testEmployee.getHiringYear())
            .setSalary(testEmployee.getSalary())
            .build();
        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.addEmployee(request));
        assertEquals("INVALID_ARGUMENT: Employee age must be between 18 and 60", exception.getMessage());
    }

    @Test
    @DisplayName("Get all employees")
    public void getAllEmployeesTest() {
        GetAllEmployeesResponse response = blockingStub.getAllEmployees(Empty.getDefaultInstance());

        assertFalse(response.getEmployeesList().isEmpty());
        assertTrue(response.getEmployeesList().size() >= 4);

        response.getEmployeesList().forEach(employee -> {
            assertNotEquals(0, employee.getId());
            assertFalse(employee.getName().isEmpty());
            assertTrue(employee.getAge() > 0);
            assertFalse(employee.getDepartment().isEmpty());
            assertFalse(employee.getPosition().isEmpty());
            assertTrue(employee.getHiringYear() > 0);
            assertTrue(employee.getSalary() > 0);
        });
    }

    @Test
    @DisplayName("Get employee with invalid ID")
    public void getEmployeeWithInvalidIdTest() {
        GetEmployeeRequest request = GetEmployeeRequest.newBuilder()
            .setEmployeeId(-1)
            .build();

        StatusRuntimeException exception = assertThrows(StatusRuntimeException.class, () -> blockingStub.getEmployee(request));
        assertEquals("NOT_FOUND: Employee with ID -1 not found", exception.getMessage());
    }

    @Test
    @DisplayName("Get departments")
    public void getDepartmentsTest() {
        GetDepartmentsResponse response = blockingStub.getDepartments(Empty.getDefaultInstance());

        assertTrue(response.getDepartmentsList().stream()
            .map(Department::getDepartmentName)
            .toList()
            .containsAll(List.of("IT", "HR", "Marketing", "Finance")));
    }

    @Test
    @DisplayName("Filter employees by department")
    public void filterEmployeesByDepartmentTest() {
        EmployeeFilterRequest request = EmployeeFilterRequest.newBuilder()
            .setDepartment("IT")
            .build();

        EmployeeListResponse response = blockingStub.filterEmployees(request);

        assertFalse(response.getEmployeesList().isEmpty());

        response.getEmployeesList().forEach(employee -> {
            assertEquals("IT", employee.getDepartment());
        });
    }

    @Test
    @DisplayName("Filter employees by age range")
    public void filterEmployeesByAgeRangeTest() {
        int minAge = 25;
        int maxAge = 30;

        EmployeeFilterRequest request = EmployeeFilterRequest.newBuilder()
            .setMinAge(minAge)
            .setMaxAge(maxAge)
            .build();

        EmployeeListResponse response = blockingStub.filterEmployees(request);

        response.getEmployeesList().forEach(employee -> {
            assertTrue(employee.getAge() >= minAge);
            assertTrue(employee.getAge() <= maxAge);
        });
    }

    @Test
    @DisplayName("Filter employees by hiring year")
    public void filterEmployeesByHiringYearTest() {
        int hiringYear = 2021;

        EmployeeFilterRequest request = EmployeeFilterRequest.newBuilder()
            .setHiringYear(hiringYear)
            .build();

        EmployeeListResponse response = blockingStub.filterEmployees(request);

        response.getEmployeesList().forEach(employee -> {
            assertEquals(hiringYear, employee.getHiringYear());
        });
    }

    @Test
    @DisplayName("Filter employees by salary range")
    public void filterEmployeesBySalaryRangeTest() {
        double minSalary = 60000.0;
        double maxSalary = 80000.0;

        EmployeeFilterRequest request = EmployeeFilterRequest.newBuilder()
            .setMinSalary(minSalary)
            .setMaxSalary(maxSalary)
            .build();

        EmployeeListResponse response = blockingStub.filterEmployees(request);

        response.getEmployeesList().forEach(employee -> {
            assertTrue(employee.getSalary() >= minSalary);
            assertTrue(employee.getSalary() <= maxSalary);
        });
    }

    @Test
    @DisplayName("Filter employees by position")
    public void filterEmployeesByPositionTest() {
        String position = "Software Engineer";

        EmployeeFilterRequest request = EmployeeFilterRequest.newBuilder()
            .setPosition(position)
            .build();

        EmployeeListResponse response = blockingStub.filterEmployees(request);

        response.getEmployeesList().forEach(employee -> {
            assertEquals(position, employee.getPosition());
        });
    }
}
