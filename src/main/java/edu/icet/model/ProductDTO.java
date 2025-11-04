package edu.icet.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDTO {
    private Integer productId;
    private String code;
    private String name;
    private Double price;
    private Integer qty;
    private Integer categoryId;
}
