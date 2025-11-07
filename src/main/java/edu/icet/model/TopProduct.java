package edu.icet.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TopProduct {
    private String productName;
    private int unitsSold;
    private String revenue;
}
