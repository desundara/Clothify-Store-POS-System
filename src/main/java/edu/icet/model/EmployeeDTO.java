package edu.icet.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EmployeeDTO {
    private Integer employeeId;
    private String fullName;
    private String position;
    private String contactNo;
    private String address;
}
