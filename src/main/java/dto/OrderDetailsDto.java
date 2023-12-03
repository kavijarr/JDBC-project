package dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class OrderDetailsDto {
    private String orderID;
    private String itemCode;
    private int qty;
    private double unitPrice;
}
