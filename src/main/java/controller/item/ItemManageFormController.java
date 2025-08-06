package controller.item;

import controller.supplier.SupplierController;
import controller.supplier.SupplierService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Item;
import model.Supplier;
import single.DashBoardForm;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class ItemManageFormController implements Initializable {

    @FXML
    private ComboBox itemUnitType;


    @FXML
    private ComboBox cmdSearchItemType;

    @FXML
    private  TableColumn colItemQuantity;

    @FXML
    private TextField itemQuantity;

    @FXML
    private ComboBox cmdItemType;

    @FXML
    private TableColumn colItemId;

    @FXML
    private TableColumn colItemName;

    @FXML
    private TableColumn colItemType;

    @FXML
    private TableColumn colItemUnitPrice;

    @FXML
    private TextField itemId;

    @FXML
    private TextField itemName;

    @FXML
    private TextField itemType;

    @FXML
    private TextField itemUnitPrice;

    @FXML
    private TableView tblItem;

    @FXML
    private TextField txtItemId;

    @FXML
    private TextField txtItemName;

    @FXML
    private TextField txtItemQuantity;

    @FXML
    private TextField txtItemSearchByName;

    @FXML
    private TextField txtItemUnitPrice;


    ItemService itemservice = ItemController.getInstance();

    @FXML
    void btnAddItemOnAction(ActionEvent event) {

        Item item = new Item(
                txtItemId.getText(),
                txtItemName.getText(),
                cmdItemType.getValue().toString(),
                Double.parseDouble(txtItemUnitPrice.getText()),
                Integer.parseInt(txtItemQuantity.getText())
        );
        if (itemservice.addItem(item)){
            new Alert(Alert.AlertType.INFORMATION, "Item Added Successfully").show();
            clearField();
            nextIdGenerated();
        }else{
            new Alert(Alert.AlertType.ERROR , "Item Not Added ! Please try again").show();
        }



    }

    private void clearField() {
        txtItemName.setText(null);
        txtItemUnitPrice.setText(null);
        txtItemQuantity.setText(null);
    }


    @FXML
    void btnClearOnAction(ActionEvent event) {
            clearSearchField();
    }

    private void clearSearchField() {
        itemId.setText(null);
        itemName.setText(null);
        itemUnitPrice.setText(null);
        itemQuantity.setText(null);

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        if(itemservice.deleteItem(txtItemSearchByName.getText())){
            new Alert(Alert.AlertType.INFORMATION,"Item Deleted !!").show();
            txtItemSearchByName.setText(null);
            clearSearchField();
            loadTable();
            nextIdGenerated();
        }else{
            new Alert(Alert.AlertType.ERROR,"Invalid Item  Id ! please try again ").show();
            clearSearchField();
        }

    }


    @FXML
    void btnItemSearchByNameOnAction(ActionEvent event) {
        String searchId = txtItemSearchByName.getText();

        Item item = itemservice.searchItem(searchId);

        if (item != null && Objects.equals(item.getItemId(), searchId)) {
            itemId.setText(item.getItemId());
            itemName.setText(item.getItemName());
            itemUnitType.setValue(item.getUnitType());

            itemUnitPrice.setText(String.valueOf(item.getUnitPrice()));
            itemQuantity.setText(String.valueOf(item.getQuantity()));
        } else {
            new Alert(Alert.AlertType.ERROR, "Invalid Item ID! Please try again.").show();
            clearSearchField();
        }
    }


    @FXML
    void btnRefreshOnAction(ActionEvent event) {
        loadTable();

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        Item item = new Item(
          itemId.getText(),
          itemName.getText(),
          itemUnitType.getValue().toString(),
                Double.parseDouble(itemUnitPrice.getText()),
                Integer.parseInt(itemQuantity.getText())
        );
        if (itemservice.updateItem(item)){
            new Alert(Alert.AlertType.INFORMATION,"Item Updated Successfully").show();
            txtItemSearchByName.setText(null);
            loadTable();
            clearSearchField();
        }else{
            new Alert(Alert.AlertType.ERROR,"Item Not Updated ! please try again ").show();
        }


    }

    @FXML
    void homeMouseOnClick(MouseEvent event) {
        DashBoardForm dashBoardForm = DashBoardForm.getInstance();
        dashBoardForm.show();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemType.setCellValueFactory(new PropertyValueFactory<>("unitType"));
        colItemUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        colItemQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        ObservableList<String> itemTypeList = FXCollections.observableArrayList("Each", "Pair");
        cmdItemType.setItems(itemTypeList);
        itemUnitType.setItems(itemTypeList);

        loadTable();
        nextIdGenerated();
    }

    private void loadTable() {
        ObservableList<Item> itemObservableList = itemservice.getAll();

        if (itemObservableList.isEmpty()){
            tblItem.setItems(itemObservableList);
        }else{
            tblItem.getItems().clear();
            tblItem.setItems(itemObservableList);
        }
    }

    public void nextIdGenerated() {
        String lastId = itemservice.getNextItemId(); // e.g., "S001"

        if (lastId != null && lastId.startsWith("I")) {
            String numericPart = lastId.substring(1); // e.g., "001"
            int idNum = Integer.parseInt(numericPart); // parse "001" -> 1
            int nextId = idNum + 1; // increment -> 2

            String newId = String.format("I%03d", nextId); // format -> "S002"
            txtItemId.setText(newId);
        } else {
            // If no ID is found or invalid, start from "S001"
            txtItemId.setText("I001");
        }
    }
}
