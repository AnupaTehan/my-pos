package controller.item;

import db.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Item;
import util.CrudUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemController implements ItemService {

    private static ItemController instance;

    private ItemController(){
    }

    public static ItemController getInstance(){
        return instance == null  ? instance = new ItemController() : instance;
    }


    @Override
    public boolean addItem(Item item) {

        String SQL = "INSERT INTO item VALUES(?,?,?,?,?)";
        try{
            return CrudUtil.execute(
                    SQL,
                    item.getItemId(),
                    item.getItemName(),
                    item.getUnitType(),
                    item.getUnitPrice(),
                    item.getDate()
            );


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updateItem(Item item) {

        String itemId = item.getItemId();
        String SQL = "UPDATE item SET itemName = ?, unitType = ?, unitPrice = ?,date = ?  WHERE itemId = ?";
        try{
            return CrudUtil.execute(
                    SQL,
                    item.getItemName(),
                    item.getUnitType(),
                    item.getUnitPrice(),
                    item.getDate(),
                    item.getItemId()
            );

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteItem(String itemId) {
        String SQL = "DELETE FROM item WHERE itemId = ?";
        try {
            return CrudUtil.execute(SQL, itemId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Item searchItem(String itemId) {

        String SQL = "SELECT * FROM item WHERE itemId=?";
        try{
            ResultSet resultSet = CrudUtil.execute(SQL , itemId);
            while(resultSet.next()){
                return new Item(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getString(3),
                        resultSet.getDouble(4),
                        resultSet.getString(5)
                );
            }

        }catch (SQLException e){
            throw  new RuntimeException(e);
        }
        return null;

    }

    @Override
    public ObservableList<Item> getAll() {

        ObservableList<Item>itemObservableList = FXCollections.observableArrayList();
       String SQL= "SELECT * FROM item";
       try{
           ResultSet resultSet = CrudUtil.execute(SQL);
           while(resultSet.next()){
               itemObservableList.add(new Item(
                       resultSet.getString(1),
                       resultSet.getString(2),
                       resultSet.getString(3),
                       resultSet.getDouble(4),
                       resultSet.getString(5)
               ));
           }
           return itemObservableList;
       }catch (SQLException e){
           throw  new RuntimeException(e);
       }
    }

    @Override
    public String getNextItemId() {
        String SQL ="SELECT itemId FROM item ORDER BY itemId DESC LIMIT 1 ";
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement(SQL);
            ResultSet resultSet = pstm.executeQuery();
            String lastID ="";

            if (resultSet.next()){
                lastID = resultSet.getString("itemId");
            }else{
                lastID = "I000";
            }
            return lastID;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }



    }


    @Override
    public ObservableList<String> getItemIds() {
        ObservableList<String> itemCodes = FXCollections.observableArrayList();
        ObservableList<Item> itemObservableList = getAll();
        itemObservableList.forEach(item -> {
            itemCodes.add(item.getItemId());
        });
        return itemCodes;
    }

    @Override
    public List<Item> searchItemsByNamePattern(String itemNamePart) {
        String SQL = "SELECT * FROM item WHERE itemName LIKE ?";

        List<Item> items = new ArrayList<>();

        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement pstm = connection.prepareStatement(SQL);
            pstm.setString(1, itemNamePart + "%"); // Matches items starting with itemNamePart
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                items.add(new Item(
                        rs.getString("itemId"),
                        rs.getString("itemName"),
                        rs.getString("unitType"),
                        rs.getDouble("unitPrice"),
                        rs.getString("date")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

}
