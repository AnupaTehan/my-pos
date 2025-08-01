package controller.supplier;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Supplier;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SupplierController implements SupplierService {


    private static SupplierController instance;

    private SupplierController(){
    }

    public static SupplierController getInstance(){
        return instance == null  ? instance = new SupplierController() : instance;
    }


    @Override
    public boolean addSupplier(Supplier supplier) {
        String SQL  = "INSERT INTO supplier VALUES(?,?,?,?)";

        try{
            return CrudUtil.execute(
                    SQL,
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getSupplierAddress(),
                    supplier.getContactNo()
            );
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        String SQL = "UPDATE supplier SET supplierName = ?, supplierAddress = ?, contactNo = ? WHERE supplierId = ?";

        try {
            return CrudUtil.execute(
                    SQL,
                    supplier.getSupplierName(),     // 1st placeholder
                    supplier.getSupplierAddress(),  // 2nd placeholder
                    supplier.getContactNo(),        // 3rd placeholder
                    supplier.getSupplierId()        // 4th placeholder (for WHERE clause)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean deleteSupplier(String supplierId) {
        String SQL = "DELETE FROM supplier WHERE supplierId = ' "+supplierId +"'";
        try{
            return CrudUtil.execute(SQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Supplier SearchSupplier(String supplierId) {
       String SQL = "SELECT * FROM  supplier WHERE supplierId = ?";

       try {
           ResultSet resultSet = CrudUtil.execute(SQL , supplierId);
           while(resultSet.next()){
               return new Supplier(
                       resultSet.getString(1),
                       resultSet.getString(2),
                       resultSet.getString(3),
                       resultSet.getString(4)
               );
           }
       } catch (SQLException e) {
           throw new RuntimeException(e);
       }
       return null;
    }

    @Override
    public ObservableList<Supplier> getAll() {
        ObservableList<Supplier> supplierObservableList = FXCollections.observableArrayList();
        String SQL = "SELECT * FROM supplier";
        try{
            ResultSet resultSet = CrudUtil.execute(SQL);
            while(resultSet.next()){
                supplierObservableList.add(new Supplier(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getString(4)
                ));

            }
            return supplierObservableList;


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getNextSupplierId() {
        String SQL = "SELECT supplierId FROM supplier ORDER BY supplierId DESC LIMIT 1 ";
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement(SQL);
            ResultSet resultSet = pstm.executeQuery();
            String lastID ="";

            if (resultSet.next()){
                lastID = resultSet.getString("supplierId");
            }else{
                lastID = "S000";
            }
            return lastID;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ObservableList<String> getSupplierIds() {
        return null;
    }
}
