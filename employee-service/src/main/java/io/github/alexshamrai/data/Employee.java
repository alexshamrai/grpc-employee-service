package io.github.alexshamrai.data;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Employee {

    private Integer id;
    private String name;
    private Integer age;
    private String department;
    private String position;
    private Integer hiringYear;
    private Double salary;
}
