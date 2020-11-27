package dto;

import lombok.Data;

@Data
public class Employee {
    private String status;
    private EmployeeData data;
    private String message;
}
