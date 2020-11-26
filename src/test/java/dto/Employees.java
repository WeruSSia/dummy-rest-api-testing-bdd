package dto;

import java.util.List;

public class Employees {
    private String status;
    private List<EmployeeData> data;

    public Employees() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<EmployeeData> getData() {
        return data;
    }

    public void setData(List<EmployeeData> data) {
        this.data = data;
    }
}
