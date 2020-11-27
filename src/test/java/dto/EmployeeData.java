package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeData {
    private int id;
    @JsonProperty("employee_name")
    private String employeeName;
    @JsonProperty("employee_salary")
    private int employeeSalary;
    @JsonProperty("employee_age")
    private int employeeAge;
    @JsonProperty("profile_image")
    private String profileImage;
}
