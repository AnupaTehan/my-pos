package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CartList {

    private  String itemId;
    private String itemName;
    private Integer quantity;
    private double unitPrice;
    private Double total;

}
