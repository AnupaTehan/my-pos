package controller.supplier;

import db.DBConnection;
import javafx.collections.ObservableList;
import model.Supplier;

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
        return false;
    }

    @Override
    public boolean updateSupplier(Supplier supplier) {
        return false;
    }

    @Override
    public boolean deleteSupplier(String SupplierId) {
        return false;
    }

    @Override
    public Supplier SearchSupplier(String SupplierId) {
        return null;
    }

    @Override
    public ObservableList<Supplier> getAll() {
        return null;
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
