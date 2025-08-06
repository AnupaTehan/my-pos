package model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class OrderDetails {

    private String OrderId;
    private String ItemId;
    private Integer quantity;
    private  double discount ;
}
