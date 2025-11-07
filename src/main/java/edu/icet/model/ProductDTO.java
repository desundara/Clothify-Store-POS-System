package edu.icet.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProductDTO {
    private int productId;
    private String code;
    private String name;
    private double price;
    private int qty;
    private int categoryId;
    private String categoryName;

}
