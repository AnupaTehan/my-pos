package controller.order;

import com.jfoenix.controls.JFXTextField;
import controller.item.ItemController;
import controller.supplier.SupplierController;
import controller.supplier.SupplierService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import model.CartList;
import model.Item;
import model.Supplier;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;



public class OderManageFormController  implements Initializable {

    private ContextMenu suggestionsMenu = new ContextMenu();

    private ContextMenu itemSuggestionsMenu = new ContextMenu();


    @FXML
    private Label lblOrderID;

    @FXML
    private TextField txtSupplierEmail;

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
    private TableView tblOrder;

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

    ObservableList <CartList> cartListObservableList = FXCollections.observableArrayList();

    @FXML
    void btnAddToCartOnAction(ActionEvent event) {
      colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
      colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
      colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
      colUnitPrice.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
      colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));


      String itemCode = cmbItemId.getValue().toString();
      String itemName = txtItemName.getText();
      String itemType = txtItemUniteType.getText();
      Double unitePrice = Double.parseDouble(txtUnitePrice.getText());
      Integer quantity = Integer.parseInt(txtItemQuantity.getText());
      Double total = unitePrice*quantity;



      boolean state = true;

      if(Integer.parseInt(txtItemQuantity.getText())>Integer.parseInt(txtItemStock.getText())){
          new Alert(Alert.AlertType.ERROR , "No Sufficient Sock Available").show();
          state = false;
          txtItemQuantity.setText(null);
          txtItemQuantity.setPromptText("Quantity");
      }
      if (state){
          cartListObservableList.add(new CartList(itemCode,itemName,unitePrice,quantity,total));
          tblOrder.setItems(cartListObservableList);
          lblNetTotal.setText("RS ."+calculateTotal());
          clearItemFields();
      }



    }

    private Double calculateTotal() {
        Double netTotal = 0.0;
        for(CartList cartList : cartListObservableList){
            netTotal =  netTotal + cartList.getTotal();
        }
        return  netTotal;
    }

    @FXML
    void btnClearFieldOnAction(ActionEvent event) {
        clearSupplierFields();
        clearItemFields();







    }

    @FXML
    void btnClearTableOnAction(ActionEvent event) {
        tblOrder.getItems().clear();
        lblNetTotal.setText("Rs.0000");

    }

    @FXML
    void btnPlaceHolderOnAction(ActionEvent event) {



    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        nextIdGenerator();
        loadSuppliersIDs();
        loadItemIDs();

        txtSupplierName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 1) { // start showing suggestions after typing 1 letter
                showSuggestions(newText);
            } else {
                suggestionsMenu.hide();
            }
        });

        txtItemName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() >= 1) { // start showing suggestions after 1 letter
                showItemSuggestions(newText);
            } else {
                itemSuggestionsMenu.hide();
            }
        });


    }

    private void showItemSuggestions(String searchText) {
        List<Item> items = ItemController.getInstance().searchItemsByNamePattern(searchText);

        if (items.isEmpty()) {
            itemSuggestionsMenu.hide();
            return;
        }

        List<MenuItem> menuItems = new ArrayList<>();
        for (Item item : items) {
            MenuItem menuItem = new MenuItem(item.getItemName());
            menuItem.setOnAction(e -> {
                // Fill fields when user selects an item
                txtItemName.setText(item.getItemName());
                txtUnitePrice.setText(String.valueOf(item.getUnitPrice()));
                txtItemUniteType.setText(item.getUnitType());
                cmbItemId.setValue(item.getItemId());

                itemSuggestionsMenu.hide();
            });
            menuItems.add(menuItem);
        }

        itemSuggestionsMenu.getItems().clear();
        itemSuggestionsMenu.getItems().addAll(menuItems);

        if (!itemSuggestionsMenu.isShowing()) {
            itemSuggestionsMenu.show(txtItemName, Side.BOTTOM, 0, 0);
        }
    }


    private void showSuggestions(String searchText) {
        List<Supplier> suppliers = SupplierController.getInstance().searchSupplierByNamePattern(searchText);

        if (suppliers.isEmpty()) {
            suggestionsMenu.hide();
            return;
        }

        List<MenuItem> menuItems = new ArrayList<>();
        for (Supplier supplier : suppliers) {
            MenuItem item = new MenuItem(supplier.getSupplierName());
            item.setOnAction(e -> {
                // Fill fields when user clicks suggestion
                txtSupplierName.setText(supplier.getSupplierName());
                txtSupplierContactNo.setText(supplier.getContactNo());
                cmbSupplierId.setValue(supplier.getSupplierId());
                txtSupplierEmail.setText(supplier.getSupplierEmail());
            });
            menuItems.add(item);
        }

        suggestionsMenu.getItems().clear();
        suggestionsMenu.getItems().addAll(menuItems);

        if (!suggestionsMenu.isShowing()) {
            suggestionsMenu.show(txtSupplierName, Side.BOTTOM, 0, 0);
        }
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







    private void clearItemFields() {
        txtItemName.setText(null);
        txtItemUniteType.setText(null);
        txtUnitePrice.setText(null);
        txtItemQuantity.setText(null);
        cmbItemId.setValue(null);

        cmbItemId.setEditable(false);
        cmbItemId.setPromptText("Select Item ID");
    }

    private  void clearSupplierFields(){

        cmbSupplierId.setValue(null);
        txtSupplierName.setText(null);
        txtSupplierContactNo.setText(null);
        cmbSupplierId.setEditable(false);
        cmbSupplierId.setPromptText("Select Supplier ID");
    }



}
