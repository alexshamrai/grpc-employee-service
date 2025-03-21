package io.github.alexshamrai;

import io.github.alexshamrai.data.Employee;
import io.github.alexshamrai.grpc.AddEmployeeRequest;
import io.github.alexshamrai.grpc.AddEmployeeResponse;
import io.github.alexshamrai.grpc.GetEmployeeRequest;
import io.github.alexshamrai.grpc.GetEmployeeResponse;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
}
