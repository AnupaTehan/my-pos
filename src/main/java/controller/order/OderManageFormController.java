package controller.order;

import com.jfoenix.controls.JFXTextField;
import controller.item.ItemController;
import controller.supplier.SupplierController;
import controller.supplier.SupplierService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.Item;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class OderManageFormController  implements Initializable {

    public Label lblOrderID;
    @FXML
    private ComboBox cmbItemId;

    @FXML
    private ComboBox cmbSupplierId;

    @FXML
    private TableColumn colItemId;

    @FXML
    private TableColumn colItemName;

    @FXML
    private TableColumn colItemType;

    @FXML
    private TableColumn colQuantity;

    @FXML
    private TableColumn colTotal;

    @FXML
    private TableColumn colUnitPrice;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblNetTotal;

    @FXML
    private Label lblTime;

    @FXML
    private TableView<?> tblOrder;

    @FXML
    private TextField txtItemName;

    @FXML
    private TextField txtItemStock;

    @FXML
    private TextField txtItemUniteType;

    @FXML
    private JFXTextField txtOrderID;

    @FXML
    private TextField txtSupplierContactNo;

    @FXML
    private TextField txtSupplierName;

    @FXML
    private TextField txtUnitePrice;

    @FXML
    private TextField txtItemQuantity;

    OrderService orderService = OrderController.getInstance();

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {

    }

    @FXML
    void btnClearFieldOnAction(ActionEvent event) {

    }

    @FXML
    void btnClearTableOnAction(ActionEvent event) {

    }

    @FXML
    void btnPlaceHolderOnAction(ActionEvent event) {

    }

    @FXML
    void btnReloadOnAction(ActionEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        nextIdGenerator();
        loadSuppliersIDs();
        loadItemIDs();

        // Trigger search on Enter
        txtItemName.setOnAction(event -> {
            String name = txtItemName.getText().trim();
            if (!name.isEmpty()) {
                searchItemByName(name);
            } else {
                clearItemFields();
            }
        });

// Clear field when focused
        txtItemName.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) { // When field gets focus
                txtItemName.clear();
            }
        });





    }

    private void loadItemIDs() {
        ObservableList<String> itemIds = ItemController.getInstance().getItemIds();
        cmbItemId.setItems(itemIds);
    }

    private void loadSuppliersIDs() {
        ObservableList<String> supplierIds = SupplierController.getInstance().getSupplierIds();
        cmbSupplierId.setItems(supplierIds);
    }

    private void nextIdGenerator() {
        String lastId = orderService.getNextOrderID(); // e.g., "PO01"

        if (lastId != null && lastId.startsWith("PO")) {
            String numericPart = lastId.substring(2); // e.g., "01"
            int idNum = Integer.parseInt(numericPart); // parse -> 1
            int nextId = idNum + 1; // increment -> 2

            String newId = String.format("PO%02d", nextId); // format -> "PO02"

            lblOrderID.setText(newId);

        } else {
            // If no ID is found or invalid, start from "PO01"

            lblOrderID.setText("PO01");

        }
    }


    private void loadDateAndTime() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateNow = simpleDateFormat.format(date);
        lblDate.setText(dateNow);


        Timeline timeline =  new Timeline(new KeyFrame(Duration.ZERO ,e ->{
            LocalTime now = LocalTime.now();
            lblTime.setText(now.getHour()+" : "+ now.getMinute()+" : "+ now.getSecond());
        }),
                new KeyFrame(Duration.seconds(1))
                );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

    }

    private void searchItemByName(String itemName) {
        Item item = ItemController.getInstance().searchItemByName(itemName); // You must implement this in your ItemController

        if (item != null) {
            txtItemName.setText(item.getItemName());
            txtItemUniteType.setText(item.getUnitType());
            txtUnitePrice.setText(String.valueOf(item.getUnitPrice()));
            txtItemStock.setText(String.valueOf(item.getQuantity()));
            cmbItemId.setValue(item.getItemId());
        } else {
            clearItemFields(); // If no match found, clear fields
        }
    }









    private void clearItemFields() {
        txtItemName.setText(null);
        txtItemUniteType.setText(null);
        txtUnitePrice.setText(null);
        txtItemStock.setText(null);
        txtItemQuantity.setText(null);
        cmbItemId.getSelectionModel().clearSelection();
        cmbItemId.setValue(null);
    }
}
