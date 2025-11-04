package edu.icet.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderItemDTO {
    private Integer orderItemId;
    private Integer orderId;
    private Integer productId;
    private Integer qty;
    private Double price;
    private String status; // "PREPARING", "DELIVERED"
}
