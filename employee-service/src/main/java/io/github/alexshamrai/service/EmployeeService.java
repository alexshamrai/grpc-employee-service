package io.github.alexshamrai.service;

import com.google.protobuf.Empty;
import io.github.alexshamrai.data.Employee;
import io.github.alexshamrai.data.EmployeeRepository;
import io.github.alexshamrai.grpc.AddEmployeeRequest;
import io.github.alexshamrai.grpc.AddEmployeeResponse;
import io.github.alexshamrai.grpc.Department;
import io.github.alexshamrai.grpc.EmployeeServiceGrpc;
import io.github.alexshamrai.grpc.GetAllEmployeesResponse;
import io.github.alexshamrai.grpc.GetDepartmentsResponse;
import io.github.alexshamrai.grpc.GetEmployeeRequest;
import io.github.alexshamrai.grpc.GetEmployeeResponse;
import io.github.alexshamrai.grpc.GetEmployeesByHiringYearRequest;
import io.github.alexshamrai.grpc.GetEmployeesByHiringYearResponse;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@GrpcService
public class EmployeeService extends EmployeeServiceGrpc.EmployeeServiceImplBase {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void getAllEmployees(Empty request, StreamObserver<GetAllEmployeesResponse> responseObserver) {
        GetAllEmployeesResponse response = GetAllEmployeesResponse.newBuilder()
            .addAllEmployees(employeeRepository.getAllEmployees().stream()
                .map(employee -> io.github.alexshamrai.grpc.Employee.newBuilder()
                    .setId(employee.getId())
                    .setName(employee.getName())
                    .setAge(employee.getAge())
                    .setDepartment(employee.getDepartment())
                    .setPosition(employee.getPosition())
                    .setHiringYear(employee.getHiringYear())
                    .setSalary(employee.getSalary())
                    .build())
                .toList())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void addEmployee(AddEmployeeRequest request, StreamObserver<AddEmployeeResponse> responseObserver) {
        if (request.getAge() < 18 || request.getAge() > 60) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Employee age must be between 18 and 60")
                .asRuntimeException());
            return;
        }

        if (request.getSalary() <= 0) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Employee salary must be greater than 0")
                .asRuntimeException());
            return;
        }

        Employee newEmployee = Employee.builder()
            .name(request.getName())
            .age(request.getAge())
            .department(request.getDepartment())
            .position(request.getPosition())
            .hiringYear(request.getHiringYear())
            .salary(request.getSalary())
            .build();

        var createdEmployee = employeeRepository.save(newEmployee);

        AddEmployeeResponse response = AddEmployeeResponse.newBuilder()
            .setEmployeeId(createdEmployee.getId())
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getEmployee(GetEmployeeRequest request, StreamObserver<GetEmployeeResponse> responseObserver) {
        var employee = employeeRepository.findById(request.getEmployeeId());
        if (employee.isPresent()) {
            Employee model = employee.get();
            GetEmployeeResponse response = GetEmployeeResponse.newBuilder()
                .setEmployee(io.github.alexshamrai.grpc.Employee.newBuilder()
                    .setId(model.getId())
                    .setName(model.getName())
                    .setAge(model.getAge())
                    .setDepartment(model.getDepartment())
                    .setPosition(model.getPosition())
                    .setHiringYear(model.getHiringYear())
                    .setSalary(model.getSalary())
                    .build())
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            responseObserver.onError(Status.NOT_FOUND
                .withDescription("Employee with ID " + request.getEmployeeId() + " not found")
                .asRuntimeException());
        }
    }

    @Override
    public void getEmployeesByHiringYear(GetEmployeesByHiringYearRequest request,
                                         StreamObserver<GetEmployeesByHiringYearResponse> responseObserver) {
        if (request.getHiringYear() < 1900 || request.getHiringYear() > 2100) {
            responseObserver.onError(Status.INVALID_ARGUMENT
                .withDescription("Hiring year must be between 1900 and 2100")
                .asRuntimeException());
            return;
        }

        List<io.github.alexshamrai.grpc.Employee> employees = employeeRepository.findByHiringYear(request.getHiringYear()).stream()
            .map(employee -> io.github.alexshamrai.grpc.Employee.newBuilder()
                .setId(employee.getId())
                .setName(employee.getName())
                .setAge(employee.getAge())
                .setDepartment(employee.getDepartment())
                .setPosition(employee.getPosition())
                .setHiringYear(employee.getHiringYear())
                .setSalary(employee.getSalary())
                .build())
            .toList();

        GetEmployeesByHiringYearResponse response = GetEmployeesByHiringYearResponse.newBuilder()
            .addAllEmployees(employees)
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getDepartments(Empty request, StreamObserver<GetDepartmentsResponse> responseObserver) {
        List<Department> departments = employeeRepository.getDepartments().stream()
            .map(departmentName -> Department.newBuilder()
                .setDepartmentName(departmentName)
                .build())
            .toList();

        GetDepartmentsResponse response = GetDepartmentsResponse.newBuilder()
            .addAllDepartments(departments)
            .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}