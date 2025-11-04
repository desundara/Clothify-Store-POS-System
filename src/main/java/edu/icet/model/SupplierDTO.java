package edu.icet.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SupplierDTO {
    private Integer supplierId;
    private String name;
    private String contactNo;
    private String address;
}
