package edu.icet.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class OrderDTO {
    private Integer orderId;
    private LocalDateTime date;
    private Double total;
    private Integer employeeId;
    private String status; // "PENDING", "COMPLETED", "CANCELLED"
}
