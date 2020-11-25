import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static java.lang.Integer.parseInt;
import static org.assertj.core.api.Assertions.assertThat;

public class EmployeeTest {

    RequestSpecification requestSpecification;

    @BeforeClass
    public void createRequestSpecification(){
        requestSpecification = new RequestSpecBuilder().
                setBaseUri("http://dummy.restapiexample.com/api/v1").
                build();
    }

    @Test
    public void checkForEmployeeWithNotExistingId(){
        Employee employee =
                given().
                        spec(requestSpecification).
                when().
                        get("/employee/25").
                as(Employee.class);

        assertThat(employee.getData()).isNull();
        //assertThat(employee.getStatus()).isEqualTo("failure");
    }

    @Test
    public void checkIfThereAre24Employees(){
        Employees employees =
                given().
                        spec(requestSpecification).
                when().
                        get("/employees").
                as(Employees.class);

        assertThat(employees.getData()).hasSize(24);
    }

    @Test
    public void checkForNegativeAgeValueOfEmployees(){
        Employees employees =
                given().
                        spec(requestSpecification).
                when().
                        get("/employees").
                as(Employees.class);

        assertThat(employees.getData().stream().filter(it->parseInt(it.getEmployee_age())<0)).isEmpty();
    }

    @Test
    public void checkForNegativeSalaryValueOfEmployees(){
        Employees employees =
                given().
                        spec(requestSpecification).
                when().
                        get("/employees").
                as(Employees.class);

        assertThat(employees.getData().stream().filter(it->parseInt(it.getEmployee_salary())<0)).isEmpty();
    }

    @Test
    public void checkForDigitsInEmployeesNames(){
        Employees employees =
                given().spec(requestSpecification).when().get("/employees").as(Employees.class);

        assertThat(employees.getData().stream().filter(it->it.getEmployee_name().matches(".*\\d.*"))).isEmpty();
    }

    @Test
    public void testCreateNewEmployee(){
        NewEmployee employeeToBeCreated =
            given().
                    spec(requestSpecification).
                    params("name","Adam","salary","5000","age","40").
            when().
                    post("/create").
            as(NewEmployee.class);

        String id = String.valueOf(employeeToBeCreated.getData().getId());
        System.out.print(id);

        Employee createdEmployee =
                given().
                        spec(requestSpecification).
                        pathParams("id",id).
                when().
                        get("/employee/{id}").
                as(Employee.class);

        given().spec(requestSpecification).when().get("/employees").then().log().body();

        assertThat(employeeToBeCreated.getMessage()).isEqualTo("Successfully! Record has been added.");
        assertThat(createdEmployee.getData().getEmployee_name()).isEqualTo(employeeToBeCreated.getData().getName());
    }
}
