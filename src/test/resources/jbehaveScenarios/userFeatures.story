Narrative:
In order to use REST API
As a user
I want to submit GET and POST requests

Scenario: I should be able to create new employee
Given the request specification
When I post valid new employee data, with name Adam, salary 5000 and age 40
Then response status should be 200
Then check if employee was created
Then response status should be 200

Scenario: I should be able to get employee with a specific id
Given the request specification
When I request for employee with id <id>
Then response status should be 200
Then I should get employee with name <name>, salary <salary> and age <age>
Examples:
      | id | name            | salary | age |
      | 1  | Tiger Nixon     | 320800 | 61  |
      | 2  | Garrett Winters | 170750 | 63  |
      | 3  | Ashton Cox      | 86000  | 66  |

Scenario: I shouldn't be able to get employee with id 100
Given the request specification
When I request for employee with id 100
Then response status should be 200
Then requested employee should not exist