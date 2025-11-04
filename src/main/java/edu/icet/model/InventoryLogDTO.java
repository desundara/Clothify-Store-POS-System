package edu.icet.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class InventoryLogDTO {
    private Integer logId;
    private Integer productId;
    private Integer supplierId;
    private String changeType; // "IN" or "OUT"
    private Integer qtyChanged;
    private LocalDateTime date;
}
