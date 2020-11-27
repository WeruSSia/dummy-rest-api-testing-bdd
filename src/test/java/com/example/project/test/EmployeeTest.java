package com.example.project.test;

import com.google.gson.Gson;
import com.example.project.dto.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeTest {

    private RequestSpecification requestSpecification;

    @BeforeClass
    public void createRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://dummy.restapiexample.com/api/v1")
                .build();
    }

    @DataProvider(name = "existingEmployees")
    public Object[][] existingEmployees() {
        return new Object[][]{
                {EmployeeData.builder()
                        .id(1)
                        .employeeName("Tiger Nixon")
                        .employeeSalary(320800)
                        .employeeAge(61)
                        .profileImage(StringUtils.EMPTY)
                        .build()},
                {EmployeeData.builder()
                        .id(2)
                        .employeeName("Garrett Winters")
                        .employeeSalary(170750)
                        .employeeAge(63)
                        .profileImage(StringUtils.EMPTY)
                        .build()},
                {EmployeeData.builder()
                        .id(3)
                        .employeeName("Ashton Cox")
                        .employeeSalary(86000)
                        .employeeAge(66)
                        .profileImage(StringUtils.EMPTY)
                        .build()},
        };
    }

    @Test(dataProvider = "existingEmployees")
    public void checkGetEmployeesByIds(EmployeeData employeeData) {
        val employee = getEmployee(employeeData.getId());

        val softly = new SoftAssertions();
        softly.assertThat(employee.getData().getEmployeeName()).isEqualTo(employeeData.getEmployeeName());
        softly.assertThat(employee.getData().getEmployeeSalary()).isEqualTo(employeeData.getEmployeeSalary());
        softly.assertThat(employee.getData().getEmployeeAge()).isEqualTo(employeeData.getEmployeeAge());
        softly.assertThat(employee.getData().getProfileImage()).isEqualTo(employeeData.getProfileImage());
        softly.assertAll();
    }

    @Test
    public void checkForEmployeeWithNotExistingId() {
        val notExistingId = 25;
        val employee = getEmployee(notExistingId);

        val softly = new SoftAssertions();
        softly.assertThat(employee.getData()).isNull();
        softly.assertThat(employee.getStatus()).isEqualTo("failure");
        softly.assertThat(employee.getMessage()).isEqualTo("Failure! There is no such employee.");
        softly.assertAll();
    }

    @Test
    public void testCreateNewEmployee() {
        val newEmployeeData = NewEmployeeData.builder()
                .name("Adam")
                .salary(5000)
                .age(40)
                .build();

        val employeeToBeCreated = postEmployee(newEmployeeData);

        val newEmployeeId = employeeToBeCreated.getData().getId();

        val createdEmployee = getEmployee(newEmployeeId);

        assertThat(createdEmployee.getData()).as("check if new employee has not null data").isNotNull();

        val softly = new SoftAssertions();
        softly.assertThat(employeeToBeCreated.getMessage()).isEqualTo("Successfully! Record has been added.");
        softly.assertThat(createdEmployee.getData().getId()).isEqualTo(employeeToBeCreated.getData().getId());
        softly.assertThat(createdEmployee.getData().getEmployeeName()).isEqualTo(employeeToBeCreated.getData().getName());
        softly.assertThat(createdEmployee.getData().getEmployeeAge()).isEqualTo(employeeToBeCreated.getData().getAge());
        softly.assertThat(createdEmployee.getData().getEmployeeSalary()).isEqualTo(employeeToBeCreated.getData().getSalary());
        softly.assertAll();
    }

    private Employee getEmployee(int id) {
        val employeeByIdResponse =
                given()
                        .spec(requestSpecification)
                        .pathParams("id", id)
                .when()
                        .get("/employee/{id}");

        assertThat(employeeByIdResponse.statusCode()).as("check response status code").isEqualTo(200);

        return employeeByIdResponse.as(Employee.class);
    }

    private NewEmployee postEmployee(NewEmployeeData newEmployeeData) {
        val newEmployeeResponse =
                given()
                        .spec(requestSpecification)
                        .contentType("application/json")
                        .body(new Gson().toJson(newEmployeeData))
                .when()
                        .post("/create");

        assertThat(newEmployeeResponse.statusCode()).as("check response status code").isEqualTo(200);

        return newEmployeeResponse.as(NewEmployee.class);
    }
}
