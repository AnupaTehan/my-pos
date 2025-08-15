package controller.order;

import com.jfoenix.controls.JFXTextField;
import controller.item.ItemController;
import controller.supplier.SupplierController;
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

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.io.File;
import java.io.FileOutputStream;
import java.awt.Desktop;

// === Apache POI imports ===
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.ss.util.CellRangeAddress;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class OderManageFormController implements Initializable {

    @FXML
    private Label lblOrderID;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblNetTotal;
    @FXML
    private Label lblTime;

    @FXML
    private TextField txtSupplierAddress;
    @FXML
    private TextField txtSupplierEmail;
    @FXML
    private TextField txtSupplierContactNo;
    @FXML
    private TextField txtSupplierName;

    @FXML
    private ComboBox cmbSupplierId;
    @FXML
    private ComboBox cmbItemId;

    @FXML
    private TextField txtItemName;
    @FXML
    private TextField txtItemStock;
    @FXML
    private TextField txtItemUniteType;
    @FXML
    private TextField txtUnitePrice;
    @FXML
    private TextField txtItemQuantity;

    @FXML
    private TableView tblOrder;
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
    private JFXTextField txtOrderID;

    OrderService orderService = OrderController.getInstance();
    ObservableList<CartList> cartListObservableList = FXCollections.observableArrayList();
    private ContextMenu suggestionsMenu = new ContextMenu();
    private ContextMenu itemSuggestionsMenu = new ContextMenu();

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
        Double total = unitePrice * quantity;

        cartListObservableList.add(new CartList(orderID, itemCode, itemName, unitePrice, quantity, total));
        tblOrder.setItems(cartListObservableList);
        lblNetTotal.setText("RS. " + calculateTotal());
        clearItemFields();
    }

    private Double calculateTotal() {
        Double netTotal = 0.0;
        for (CartList cartList : cartListObservableList) {
            netTotal += cartList.getTotal();
        }
        return netTotal;
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
        String supplierName = txtSupplierName.getText();
        String supplierContact = txtSupplierContactNo.getText();
        String supplierAddress = txtSupplierAddress.getText();
        String supplierEmail = txtSupplierEmail.getText();

        String netTotalText = lblNetTotal.getText().replace("RS.", "").trim();
        double netTotal = 0.0;
        try {
            netTotal = Double.parseDouble(netTotalText);
        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Invalid net total value: " + netTotalText).show();
            return;
        }

        if (cartListObservableList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Cart is empty").show();
            return;
        }

        List<CartList> cartLists = new ArrayList<>();
        cartListObservableList.forEach(obj -> {
            cartLists.add(new CartList(orderID, obj.getItemId(), obj.getItemName(), obj.getUnitPrice(), obj.getQuantity(), obj.getTotal()));
        });

        Orders orders = new Orders(orderID, date, supplierId, supplierName, supplierContact, supplierAddress, supplierEmail, netTotal, cartLists);

        if (orderService.placeOrder(orders)) {
            new Alert(Alert.AlertType.INFORMATION, "Order Placed successfully").show();
            createExcelInvoice(orderID, date, supplierId, supplierName, supplierContact, supplierEmail, supplierAddress, cartLists, netTotal);
            tblOrder.getItems().clear();
            lblNetTotal.setText("Rs.0000");
            nextIdGenerator();
        } else {
            new Alert(Alert.AlertType.ERROR, "Order could not be placed").show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadDateAndTime();
        nextIdGenerator();
        loadSuppliersIDs();
        loadItemIDs();
        setupSupplierAutoComplete();
        setupItemAutoComplete();
    }

    private void setupSupplierAutoComplete() {
        txtSupplierName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.length() >= 1) showSuggestions(newText);
            else suggestionsMenu.hide();
        });
    }

    private void setupItemAutoComplete() {
        txtItemName.textProperty().addListener((obs, oldText, newText) -> {
            if (newText != null && newText.length() >= 1) showItemSuggestions(newText);
            else itemSuggestionsMenu.hide();
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
        if (!itemSuggestionsMenu.isShowing()) itemSuggestionsMenu.show(txtItemName, Side.BOTTOM, 0, 0);
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
                txtSupplierName.setText(supplier.getSupplierName());
                txtSupplierContactNo.setText(supplier.getContactNo());
                txtSupplierAddress.setText(supplier.getSupplierAddress());
                cmbSupplierId.setValue(supplier.getSupplierId());
                txtSupplierEmail.setText(supplier.getSupplierEmail());
            });
            menuItems.add(item);
        }
        suggestionsMenu.getItems().clear();
        suggestionsMenu.getItems().addAll(menuItems);
        if (!suggestionsMenu.isShowing()) suggestionsMenu.show(txtSupplierName, Side.BOTTOM, 0, 0);
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
            try {
                String numericPart = lastId.substring(2);
                int nextId = Integer.parseInt(numericPart) + 1;
                lblOrderID.setText(String.format("PO%02d", nextId));
            } catch (NumberFormatException e) {
                lblOrderID.setText("PO01");
            }
        } else {
            lblOrderID.setText("PO01");
        }
    }

    private void loadDateAndTime() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        lblDate.setText(sdf.format(date));

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalTime now = LocalTime.now();
            lblTime.setText(now.getHour() + " : " + now.getMinute() + " : " + now.getSecond());
        }), new KeyFrame(Duration.seconds(1)));
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

    private void clearSupplierFields() {
        cmbSupplierId.setValue(null);
        txtSupplierName.setText(null);
        txtSupplierContactNo.setText(null);
        txtSupplierEmail.setText(null);
        cmbSupplierId.setEditable(true);
        cmbSupplierId.setPromptText("Select Supplier ID");
    }

    private void createExcelInvoice(String orderId, String date, String supplierId, String supplierName,
                                    String contactNo, String supplierEmail, String supplierAddress,
                                    List<CartList> items, double netTotal) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Invoice");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Workbook", "*.xlsx"));
        fileChooser.setInitialFileName(orderId + "_invoice.xlsx");

        Stage stage = (Stage) lblOrderID.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        DecimalFormat df = new DecimalFormat("0.00");

        try (XSSFWorkbook workbook = new XSSFWorkbook(); FileOutputStream out = new FileOutputStream(file)) {
            XSSFSheet sheet = workbook.createSheet("Invoice");
            int rowNum = 0;

            // ===== Company Name =====
            Row companyRow = sheet.createRow(rowNum++);
            Cell companyCell = companyRow.createCell(0);
            companyCell.setCellValue("UNIQUE INDUSTRIAL SOLUTIONS (PVT) LTD");
            CellStyle companyStyle = workbook.createCellStyle();
            Font companyFont = workbook.createFont();
            companyFont.setBold(true);
            companyFont.setFontHeightInPoints((short) 18);
            companyFont.setFontName("Century");
            companyFont.setColor(IndexedColors.BLACK.getIndex());
            companyStyle.setFont(companyFont);
            companyStyle.setAlignment(HorizontalAlignment.CENTER);
            companyCell.setCellStyle(companyStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4));

            try {
                InputStream logoInputStream = new FileInputStream("D:\\chathuranga project\\my-pos\\src\\main\\resources\\img\\logo.png"); // <-- replace with your logo path
                byte[] bytes = logoInputStream.readAllBytes();
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                logoInputStream.close();

                Drawing<?> drawing = sheet.createDrawingPatriarch();
                CreationHelper helper = workbook.getCreationHelper();

                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0); // Column A
                anchor.setRow1(companyRow.getRowNum()); // Current row
                anchor.setCol2(1); // Column B (logo width)
                anchor.setRow2(companyRow.getRowNum() + 1); // Row height
                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize(0.5); // Resize to 50%
            } catch (Exception e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Error adding logo: " + e.getMessage()).show();
            }


            // ===== Title =====
            Row titleRow = sheet.createRow(rowNum++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("PURCHASE ORDER");
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 18);
            titleFont.setFontName("Century");
            titleFont.setColor(IndexedColors.GREEN.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 4));


            // ===== Order ID & Date =====
            Row orderIdRow = sheet.createRow(rowNum++);
            Cell orderIdCell = orderIdRow.createCell(3);
            orderIdCell.setCellValue("Order ID: " + orderId);

            CellStyle orderStyle = workbook.createCellStyle();
            Font orderFont = workbook.createFont();
            orderFont.setFontName("Century");
            orderFont.setFontHeightInPoints((short) 11);
            orderStyle.setFont(orderFont);
            orderStyle.setAlignment(HorizontalAlignment.RIGHT);
            orderIdCell.setCellStyle(orderStyle);

// ===== Date =====
            Row dateRow = sheet.createRow(rowNum++);
            Cell dateCell = dateRow.createCell(3);
            dateCell.setCellValue("Date: " + date);

            dateCell.setCellStyle(orderStyle); // reuse the same style

            rowNum++;

            // ===== Ship To =====
            // Column index for right side (adjust as needed)
            int rightColumn = 3;

// ===== Ship To =====
            Row shipRow = sheet.createRow(rowNum++);
            Cell shipCell = shipRow.createCell(rightColumn);
            shipCell.setCellValue("Ship To");

            CellStyle shipStyle = workbook.createCellStyle();
            Font shipFont = workbook.createFont();
            shipFont.setBold(true);
            shipFont.setFontHeightInPoints((short) 14);
            shipFont.setFontName("Century");
            shipStyle.setFont(shipFont);
            shipStyle.setAlignment(HorizontalAlignment.RIGHT); // align text to the right
            shipCell.setCellStyle(shipStyle);

// ===== Supplier Info =====
            String[] supplierInfo = {
                    "Supplier ID: " + supplierId,
                    "Supplier Name: " + supplierName,
                    "Contact No: " + contactNo,
                    "Supplier Address: " + supplierAddress,
                    "Email: " + supplierEmail
            };

            for (String info : supplierInfo) {
                Row r = sheet.createRow(rowNum++);
                Cell c = r.createCell(rightColumn);
                c.setCellValue(info);

                CellStyle infoStyle = workbook.createCellStyle();
                Font infoFont = workbook.createFont();
                infoFont.setFontName("Century");
                infoFont.setFontHeightInPoints((short) 12);
                infoStyle.setFont(infoFont);
                infoStyle.setAlignment(HorizontalAlignment.RIGHT); // right-align
                c.setCellStyle(infoStyle);
            }


            rowNum++;

            // ===== Items Table =====
            String[] headers = {"Item ID", "Item Name", "Quantity", "Unit Price", "Total"};
            Row headerRow = sheet.createRow(rowNum++);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setFontName("Calibri");
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            boolean shade = false;
            CellStyle shadeStyle = workbook.createCellStyle();
            shadeStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
            shadeStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            for (CartList item : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getItemId() == null ? "" : item.getItemId());
                row.createCell(1).setCellValue(item.getItemName() == null ? "" : item.getItemName());
                row.createCell(2).setCellValue(item.getQuantity());
                row.createCell(3).setCellValue(df.format(item.getUnitPrice()));
                row.createCell(4).setCellValue(df.format(item.getTotal()));

                if (shade) {
                    for (int i = 0; i < 5; i++) {
                        row.getCell(i).setCellStyle(shadeStyle);
                    }
                }
                shade = !shade;
            }

            // ===== Net Total =====
            Row totalRow = sheet.createRow(rowNum++);
            Cell totalCell = totalRow.createCell(4);
            totalCell.setCellValue("Net Total: Rs. " + df.format(netTotal));
            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalFont.setFontName("Calibri");
            totalFont.setFontHeightInPoints((short) 14);
            totalStyle.setFont(totalFont);
            totalCell.setCellStyle(totalStyle);

            // Auto-size columns
            for (int i = 0; i < 5; i++) sheet.autoSizeColumn(i);

            // ===== Set Custom Column Widths =====
            sheet.setColumnWidth(0, 13000);
            sheet.setColumnWidth(1, 10000);
            sheet.setColumnWidth(2, 7000);
            sheet.setColumnWidth(3, 7000);
            sheet.setColumnWidth(4, 8000);



            workbook.write(out);

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not save Excel invoice: " + e.getMessage()).show();
            return;
        }

        // Open the generated file automatically
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().open(file);
        } catch (Exception ignored) {
        }

        // ===== Success alert =====
        new Alert(Alert.AlertType.INFORMATION, "Invoice saved successfully at:\n" + file.getAbsolutePath()).show();
    }
}

