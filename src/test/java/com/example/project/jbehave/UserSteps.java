package com.example.project.jbehave;

import com.example.project.dto.Employee;
import com.example.project.dto.NewEmployee;
import com.example.project.dto.NewEmployeeData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.assertj.core.api.SoftAssertions;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class UserSteps {
    private Response response;
    private RequestSpecification requestSpecification;
    private final ObjectMapper objectMapper;

    public UserSteps() {
        objectMapper = new ObjectMapper();
    }

    @Given("the request specification")
    public void createRequestSpecification() {
        requestSpecification = new RequestSpecBuilder()
                .setBaseUri("http://dummy.restapiexample.com/api/v1")
                .build();
    }

    @When("I post valid new employee data, with name $name, salary $salary and age $age")
    public void sendNewEmployeeData(String name, int salary, int age) throws JsonProcessingException {
        val newEmployeeData = NewEmployeeData.builder()
                .name(name)
                .salary(salary)
                .age(age)
                .build();
        response = postEmployee(newEmployeeData);
    }

    @Then("response status should be $code")
    public void checkResponseStatusCode(int code) {
        assertThat(response.statusCode()).isEqualTo(code);
    }

    @Then("check if employee was created")
    public void checkIfEmployeeWasCreated() {
        val employeeToBeCreated = response.as(NewEmployee.class);

        val newEmployeeId = employeeToBeCreated.getData().getId();

        val createdEmployee = getEmployee(newEmployeeId).as(Employee.class);

        assertThat(createdEmployee.getData()).as("check if new employee has not null data").isNotNull();

        val softly = new SoftAssertions();
        softly.assertThat(createdEmployee.getMessage()).isEqualTo("Successfully! Record has been added.");
        softly.assertThat(areEmployeesEqual(createdEmployee, employeeToBeCreated)).isTrue();
        softly.assertAll();
    }

    @When("I request for employee with id $id")
    public void requestForEmployee(int id) {
        response = getEmployee(id);
    }

    @Then("I should get employee with name $name, salary $salary and age $age")
    public void checkIfRequestedEmployeeExists(String name, int salary, int age) {
        val employee = response.as(Employee.class);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(employee.getData().getEmployeeName()).isEqualTo(name);
        softly.assertThat(employee.getData().getEmployeeSalary()).isEqualTo(salary);
        softly.assertThat(employee.getData().getEmployeeAge()).isEqualTo(age);
        softly.assertAll();
    }

    @Then("requested employee should not exist")
    public void checkIfRequestedEmployeeDoesNotExist() {
        val employee = response.as(Employee.class);
        val softly = new SoftAssertions();
        softly.assertThat(employee.getData()).isNull();
        softly.assertThat(employee.getStatus()).isEqualTo("failure");
        softly.assertThat(employee.getMessage()).isEqualTo("Failure! There is no such employee.");
        softly.assertAll();
    }

    private Response getEmployee(int id) {
        return given()
                .spec(requestSpecification)
                .pathParams("id", id)
                .when()
                .get("/employee/{id}");
    }

    private Response postEmployee(NewEmployeeData newEmployeeData) throws JsonProcessingException {
        return given()
                .spec(requestSpecification)
                .contentType("application/json")
                .body(objectMapper.writeValueAsString(newEmployeeData))
                .when()
                .post("/create");
    }

    private boolean areEmployeesEqual(Employee employee1, NewEmployee employee2) {
        return employee1.getData().getEmployeeName().equals(employee2.getData().getName())
                && employee1.getData().getEmployeeAge() == employee2.getData().getAge()
                && employee1.getData().getEmployeeSalary() == employee2.getData().getSalary()
                && employee1.getData().getId() == employee2.getData().getId();
    }
}
