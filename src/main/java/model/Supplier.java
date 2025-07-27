package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class Supplier {

    private String supplierId;
    private String SupplierName;
    private String SupplierAddress;
    private String contactNo;


}
