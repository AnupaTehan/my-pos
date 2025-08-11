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
import model.Orders;
import model.Supplier;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;


//use for connect with word pad

// === imports you need ===
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblBorders;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STBorder;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;
import java.util.List;





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

        String orderID = lblOrderID.getText();
        String itemCode = cmbItemId.getValue().toString();
      String itemName = txtItemName.getText();
      String itemType = txtItemUniteType.getText();
      Double unitePrice = Double.parseDouble(txtUnitePrice.getText());
      Integer quantity = Integer.parseInt(txtItemQuantity.getText());
      Double total = unitePrice*quantity;



      boolean state = true;


      if (state){
          cartListObservableList.add(new CartList(orderID,itemCode,itemName,unitePrice,quantity,total));
          tblOrder.setItems(cartListObservableList);
          lblNetTotal.setText("RS. " + calculateTotal());
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

    String orderID = lblOrderID.getText();
        String date = lblDate.getText();
        String supplierId = cmbSupplierId.getValue().toString();
        String supplierName =txtSupplierName.getText();
        String supplierContact = txtSupplierContactNo.getText();
        String supplierEmail = txtSupplierEmail.getText();

        String netTotalText = lblNetTotal.getText();  // e.g. "RS. 21702.5"
        netTotalText = netTotalText.replace("RS.", "").trim();  // remove prefix
        double netTotal = 0.0;
        try {
            netTotal = Double.parseDouble(netTotalText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid net total value: " + netTotalText).show();
            return;
        }

        List <CartList> cartLists = new ArrayList<>();

        if (cartListObservableList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty").show();
            return;
        }

        cartListObservableList.forEach(obj ->{
            cartLists.add(new CartList(orderID,obj.getItemId(),obj.getItemName(),obj.getUnitPrice(),obj.getQuantity(), obj.getTotal()));
        });



        Orders orders = new Orders(orderID,date,supplierId,supplierName,supplierContact,supplierEmail,netTotal,cartLists);




        if ( orderService.placeOrder(orders)){
            new Alert(Alert.AlertType.INFORMATION , "Order Placed successfully ").show();
            createWordInvoice(orderID, date, supplierId, supplierName, supplierContact, supplierEmail, cartLists, netTotal);
            tblOrder.getItems().clear();
            lblNetTotal.setText("Rs.0000");
            nextIdGenerator();
        }else{
            new Alert(Alert.AlertType.ERROR,"Order could not be placed").show();
        }





    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        nextIdGenerator();
        loadSuppliersIDs();
        loadItemIDs();

        txtSupplierName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.length() >= 1) { // safe check
                showSuggestions(newText);
            } else {
                suggestionsMenu.hide();
            }
        });


        txtItemName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.length() >= 1) {
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

        cmbItemId.setEditable(true);
        cmbItemId.setPromptText("Select Item ID");
    }

    private  void clearSupplierFields(){

        cmbSupplierId.setValue(null);
        txtSupplierName.setText(null);
        txtSupplierContactNo.setText(null);
        txtSupplierEmail.setText(null);
        cmbSupplierId.setEditable(true);
        cmbSupplierId.setPromptText("Select Supplier ID");
    }



    // for a connect with word document

    private void createWordInvoice(String orderId,
                                   String date,
                                   String supplierId,
                                   String supplierName,
                                   String contactNo,
                                   String supplierEmail,
                                   List<CartList> items,
                                   double netTotal) {

        // ask the user where to save
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Invoice");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Word Document", "*.docx"));
        fileChooser.setInitialFileName(orderId + "_invoice.docx");

        // get stage from any control
        Stage stage = (Stage) lblOrderID.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return; // user cancelled

        DecimalFormat df = new DecimalFormat("0.00");

        try (XWPFDocument doc = new XWPFDocument();
             FileOutputStream out = new FileOutputStream(file)) {

            // Title styling
            XWPFParagraph title = doc.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            title.setSpacingAfter(200); // space after title
            XWPFRun rTitle = title.createRun();
            rTitle.setBold(true);
            rTitle.setFontSize(20);
            rTitle.setFontFamily("Calibri");
            rTitle.setColor("2E74B5");  // blue-ish color
            rTitle.setText("Purchase Order");

            // Order & Supplier info paragraph
            XWPFParagraph info = doc.createParagraph();
            XWPFRun r = info.createRun();
            r.setFontSize(11);
            r.setFontFamily("Calibri");
            r.setText("Order ID: " + orderId); r.addBreak();
            r.setText("Date: " + date); r.addBreak();
            r.setText("Supplier ID: " + supplierId); r.addBreak();
            r.setText("Supplier Name: " + supplierName); r.addBreak();
            r.setText("Contact No: " + contactNo); r.addBreak();
            r.setText("Email: " + supplierEmail); r.addBreak();
            r.addBreak();

            // Create items table
            XWPFTable table = doc.createTable();

            // Set table width to 100%
            table.setWidth("100%");

            // Header row (table initially has one row)
            XWPFTableRow header = table.getRow(0);
            header.getCell(0).setText("Item ID");
            header.addNewTableCell().setText("Item Name");
            header.addNewTableCell().setText("Quantity");
            header.addNewTableCell().setText("Unit Price");
            header.addNewTableCell().setText("Total");

            // Style header cells: background color + white bold text
            for (XWPFTableCell cell : header.getTableCells()) {
                cell.setColor("4472C4");  // Dark blue background

                for (XWPFParagraph p : cell.getParagraphs()) {
                    for (XWPFRun run : p.getRuns()) {
                        run.setBold(true);
                        run.setColor("FFFFFF");  // White text
                        run.setFontFamily("Calibri");
                        run.setFontSize(12);
                    }
                }
            }

            // Add data rows with alternating row shading
            boolean shade = false;
            for (CartList it : items) {
                XWPFTableRow row = table.createRow();

                row.getCell(0).setText(it.getItemId() == null ? "" : it.getItemId());
                row.getCell(1).setText(it.getItemName() == null ? "" : it.getItemName());
                row.getCell(2).setText(String.valueOf(it.getQuantity()));
                row.getCell(3).setText(df.format(it.getUnitPrice()));
                row.getCell(4).setText(df.format(it.getTotal()));

                if (shade) {
                    for (XWPFTableCell cell : row.getTableCells()) {
                        cell.setColor("D9E1F2");  // Light blue shading
                    }
                }
                shade = !shade;
            }

            // Add table borders (solid lines)
            CTTblBorders borders = table.getCTTbl().getTblPr().addNewTblBorders();
            borders.addNewTop().setVal(STBorder.SINGLE);
            borders.addNewBottom().setVal(STBorder.SINGLE);
            borders.addNewLeft().setVal(STBorder.SINGLE);
            borders.addNewRight().setVal(STBorder.SINGLE);
            borders.addNewInsideH().setVal(STBorder.SINGLE);
            borders.addNewInsideV().setVal(STBorder.SINGLE);

            // Net total paragraph (right aligned, bold)
            XWPFParagraph totalPara = doc.createParagraph();
            totalPara.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun rTotal = totalPara.createRun();
            rTotal.setBold(true);
            rTotal.setFontFamily("Calibri");
            rTotal.setFontSize(14);
            rTotal.setText("Net Total: Rs. " + df.format(netTotal));

            // Write document to file
            doc.write(out);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save invoice: " + e.getMessage()).show();
            return;
        }

        // Optionally open the file after creation (desktop must be supported)
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            // ignore if can't open — file is still saved.
        }

        new Alert(Alert.AlertType.INFORMATION, "Invoice saved: " + file.getAbsolutePath()).show();
    }




}
