package test;

import dto.Employee;
import dto.NewEmployee;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.json.JSONObject;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeTest {

    private RequestSpecification requestSpecification;

    @BeforeClass
    public void createRequestSpecification() {
        requestSpecification = new RequestSpecBuilder().
                setBaseUri("http://dummy.restapiexample.com/api/v1").
                build();
    }

    @DataProvider(name = "existingEmployees")
    public Object[][] existingEmployees() {
        return new Object[][]{
                {1, "Tiger Nixon", 320800, 61, ""},
                {2, "Garrett Winters", 170750, 63, ""},
                {3, "Ashton Cox", 86000, 66, ""},
                {4, "Cedric Kelly", 433060, 22, ""},
                {5, "Airi Satou", 162700, 33, ""}
        };
    }

    @Test(dataProvider = "existingEmployees")
    public void checkGetEmployeesByIds(int id, String employeeName, int employeeSalary, int employeeAge, String profileImage) {
        Employee employee = getEmployee(id);

        SoftAssertions softly = new SoftAssertions();
            softly.assertThat(employee.getData().getEmployee_name()).isEqualTo(employeeName);
            softly.assertThat(employee.getData().getEmployee_salary()).isEqualTo(employeeSalary);
            softly.assertThat(employee.getData().getEmployee_age()).isEqualTo(employeeAge);
            softly.assertThat(employee.getData().getProfile_image()).isEqualTo(profileImage);
        softly.assertAll();
    }

    @Test
    public void checkForEmployeeWithNotExistingId() {
        int notExistingId = 25;
        Employee employee = getEmployee(notExistingId);

        SoftAssertions softly = new SoftAssertions();
            softly.assertThat(employee.getData()).isNull();
            softly.assertThat(employee.getStatus()).isEqualTo("failure");
            softly.assertThat(employee.getMessage()).isEqualTo("Failure! There is no such employee.");
        softly.assertAll();
    }

    @Test
    public void testCreateNewEmployee() {
        JSONObject newEmployeeData = new JSONObject().
                put("name", "Adam").
                put("salary", "5000").
                put("age", "40");

        NewEmployee employeeToBeCreated = postEmployee(newEmployeeData);

        int newEmployeeId = employeeToBeCreated.getData().getId();

        Employee createdEmployee = getEmployee(newEmployeeId);

        assertThat(createdEmployee.getData()).as("check if new employee has not null data").isNotNull();

        SoftAssertions softly = new SoftAssertions();
            softly.assertThat(employeeToBeCreated.getMessage()).isEqualTo("Successfully! Record has been added.");
            softly.assertThat(createdEmployee.getData().getId()).isEqualTo(employeeToBeCreated.getData().getId());
            softly.assertThat(createdEmployee.getData().getEmployee_name()).isEqualTo(employeeToBeCreated.getData().getName());
            softly.assertThat(createdEmployee.getData().getEmployee_age()).isEqualTo(employeeToBeCreated.getData().getAge());
            softly.assertThat(createdEmployee.getData().getEmployee_salary()).isEqualTo(employeeToBeCreated.getData().getSalary());
        softly.assertAll();
    }

    private Employee getEmployee(int id){
        Response employeeByIdResponse =
                given().
                    spec(requestSpecification).
                    pathParams("id",id).
                when().
                    get("/employee/{id}");

        assertThat(employeeByIdResponse.statusCode()).as("check response status code").isEqualTo(200);

        return employeeByIdResponse.as(Employee.class);
    }

    private NewEmployee postEmployee(JSONObject newEmployeeData){
        Response newEmployeeResponse =
                given().
                        spec(requestSpecification).
                        params(newEmployeeData.toMap()).
                when().
                        post("/create");

        assertThat(newEmployeeResponse.statusCode()).as("check response status code").isEqualTo(200);

        return newEmployeeResponse.as(NewEmployee.class);
    }
}
