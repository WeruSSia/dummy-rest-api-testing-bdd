package com.example.project.dto;

import lombok.Data;

@Data
public class NewEmployee {
    private String status;
    private NewEmployeeData data;
    private String message;
}
