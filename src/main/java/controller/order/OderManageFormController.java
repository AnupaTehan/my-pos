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
            XSSFSheet sheet = workbook.createSheet("Purchase Order");
            int rowNum = 0;

            // ===== COMPANY LOGO AND INFO (TOP LEFT) =====
            // Logo placement
            try {
                InputStream logoInputStream = new FileInputStream("src/main/resources/img/logo.png");
                byte[] bytes = logoInputStream.readAllBytes();
                int pictureIdx = workbook.addPicture(bytes, Workbook.PICTURE_TYPE_PNG);
                logoInputStream.close();

                Drawing<?> drawing = sheet.createDrawingPatriarch();
                CreationHelper helper = workbook.getCreationHelper();
                ClientAnchor anchor = helper.createClientAnchor();
                anchor.setCol1(0); // Column A
                anchor.setRow1(0); // Row 1
                anchor.setCol2(2); // Column C
                anchor.setRow2(4); // Row 5
                Picture pict = drawing.createPicture(anchor, pictureIdx);
                pict.resize(1.0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // Company details (left side, starting from row 5)
            rowNum = 4; // Start after logo space

            Row companyNameRow = sheet.createRow(rowNum++);
            Cell companyNameCell = companyNameRow.createCell(0);
            companyNameCell.setCellValue("Unique Industrial Solutions(Pvt)Ltd");
            CellStyle companyStyle = workbook.createCellStyle();
            Font companyFont = workbook.createFont();
            companyFont.setBold(true);
            companyFont.setFontHeightInPoints((short) 12);
            companyFont.setColor(IndexedColors.TEAL.getIndex());
            companyStyle.setFont(companyFont);
            companyNameCell.setCellStyle(companyStyle);

            // Default company information
            String[] companyInfo = {
                    "No.15, Uyankele Road,",
                    "Panadura 12500",
                    "Phone: 076-8235111",
                    "E Mail: projects@uniquem.lk",
                    "VAT Reg No.100987845-7000",
                    "SVAT No.10798"
            };

            CellStyle infoStyle = workbook.createCellStyle();
            Font infoFont = workbook.createFont();
            infoFont.setFontHeightInPoints((short) 10);
            infoStyle.setFont(infoFont);

            for (String info : companyInfo) {
                Row r = sheet.createRow(rowNum++);
                Cell c = r.createCell(0);
                c.setCellValue(info);
                c.setCellStyle(infoStyle);
            }

            // ===== PURCHASE ORDER TITLE AND DETAILS (TOP RIGHT) =====
            // Purchase Order title
            Row poTitleRow = sheet.getRow(4) != null ? sheet.getRow(4) : sheet.createRow(4);
            Cell poTitleCell = poTitleRow.createCell(4);
            poTitleCell.setCellValue("PURCHASE ORDER");
            CellStyle poTitleStyle = workbook.createCellStyle();
            Font poTitleFont = workbook.createFont();
            poTitleFont.setBold(true);
            poTitleFont.setFontHeightInPoints((short) 18);
            poTitleFont.setColor(IndexedColors.TEAL.getIndex());
            poTitleStyle.setFont(poTitleFont);
            poTitleStyle.setAlignment(HorizontalAlignment.RIGHT);
            poTitleCell.setCellStyle(poTitleStyle);

            // Date and PO# (right aligned)
            Row dateRowRight = sheet.getRow(6) != null ? sheet.getRow(6) : sheet.createRow(6);
            Cell dateCellRight = dateRowRight.createCell(4);
            dateCellRight.setCellValue("DATE");
            Cell dateValueCell = dateRowRight.createCell(5);
            dateValueCell.setCellValue(date);

            Row poRowRight = sheet.getRow(7) != null ? sheet.getRow(7) : sheet.createRow(7);
            Cell poCellRight = poRowRight.createCell(4);
            poCellRight.setCellValue("PO #");
            Cell poValueCell = poRowRight.createCell(5);
            poValueCell.setCellValue(orderId);

            CellStyle rightAlignStyle = workbook.createCellStyle();
            Font rightFont = workbook.createFont();
            rightFont.setFontHeightInPoints((short) 10);
            rightAlignStyle.setFont(rightFont);
            rightAlignStyle.setAlignment(HorizontalAlignment.RIGHT);

            dateCellRight.setCellStyle(rightAlignStyle);
            poCellRight.setCellStyle(rightAlignStyle);

            // ===== VENDOR AND SHIP TO SECTION =====
            rowNum = Math.max(rowNum, 12); // Ensure enough space

            // Create vendor and ship-to headers with green background
            Row vendorHeaderRow = sheet.createRow(rowNum++);
            Cell vendorHeaderCell = vendorHeaderRow.createCell(0);
            vendorHeaderCell.setCellValue("VENDOR");
            Cell shipToHeaderCell = vendorHeaderRow.createCell(3);
            shipToHeaderCell.setCellValue("SHIP TO");

            CellStyle greenHeaderStyle = workbook.createCellStyle();
            Font greenHeaderFont = workbook.createFont();
            greenHeaderFont.setBold(true);
            greenHeaderFont.setColor(IndexedColors.WHITE.getIndex());
            greenHeaderFont.setFontHeightInPoints((short) 11);
            greenHeaderStyle.setFont(greenHeaderFont);
            greenHeaderStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            greenHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            greenHeaderStyle.setBorderTop(BorderStyle.THIN);
            greenHeaderStyle.setBorderBottom(BorderStyle.THIN);
            greenHeaderStyle.setBorderLeft(BorderStyle.THIN);
            greenHeaderStyle.setBorderRight(BorderStyle.THIN);

            vendorHeaderCell.setCellStyle(greenHeaderStyle);
            shipToHeaderCell.setCellStyle(greenHeaderStyle);

            // Merge cells for headers
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));

            // Vendor details (left side)
            String[] vendorDetails = {
                    supplierName,
                    "[Contact or Department]",
                    supplierAddress,
                    "Phone: " + contactNo,
                    "Email: " + supplierEmail
            };

            // Ship To details (right side - using default company info)
            String[] shipToDetails = {
                    "No.15, Uyankele Road,",
                    "Panadura 12500",
                    "Phone: 076-8235111",
                    "E Mail: projects@uniquem.lk"
            };

            CellStyle vendorStyle = workbook.createCellStyle();
            Font vendorFont = workbook.createFont();
            vendorFont.setFontHeightInPoints((short) 10);
            vendorStyle.setFont(vendorFont);
            vendorStyle.setBorderLeft(BorderStyle.THIN);
            vendorStyle.setBorderRight(BorderStyle.THIN);

            for (int i = 0; i < Math.max(vendorDetails.length, shipToDetails.length); i++) {
                Row detailRow = sheet.createRow(rowNum++);

                // Vendor side
                if (i < vendorDetails.length) {
                    Cell vendorCell = detailRow.createCell(0);
                    vendorCell.setCellValue(vendorDetails[i]);
                    vendorCell.setCellStyle(vendorStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
                }

                // Ship To side
                if (i < shipToDetails.length) {
                    Cell shipCell = detailRow.createCell(3);
                    shipCell.setCellValue(shipToDetails[i]);
                    shipCell.setCellStyle(vendorStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 3, 5));
                }
            }

            // Add bottom border to vendor section
            Row bottomBorderRow = sheet.createRow(rowNum++);
            for (int i = 0; i <= 5; i++) {
                Cell borderCell = bottomBorderRow.createCell(i);
                CellStyle bottomBorderStyle = workbook.createCellStyle();
                bottomBorderStyle.setBorderBottom(BorderStyle.THIN);
                borderCell.setCellStyle(bottomBorderStyle);
            }

            rowNum++; // Add some space

            // ===== ITEMS TABLE =====
            String[] headers = {"ITEM #", "DESCRIPTION", "QTY", "UNIT PRICE", "TOTAL"};
            Row headerRow = sheet.createRow(rowNum++);

            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderFont.setColor(IndexedColors.WHITE.getIndex());
            tableHeaderFont.setFontHeightInPoints((short) 11);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tableHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
            tableHeaderStyle.setBorderTop(BorderStyle.THIN);
            tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
            tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
            tableHeaderStyle.setBorderRight(BorderStyle.THIN);

            for (int i = 0; i < headers.length; i++) {
                Cell headerCell = headerRow.createCell(i);
                headerCell.setCellValue(headers[i]);
                headerCell.setCellStyle(tableHeaderStyle);
            }

            // Item rows with alternating colors
            CellStyle normalRowStyle = workbook.createCellStyle();
            normalRowStyle.setBorderTop(BorderStyle.THIN);
            normalRowStyle.setBorderBottom(BorderStyle.THIN);
            normalRowStyle.setBorderLeft(BorderStyle.THIN);
            normalRowStyle.setBorderRight(BorderStyle.THIN);

            CellStyle alternateRowStyle = workbook.createCellStyle();
            alternateRowStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
            alternateRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            alternateRowStyle.setBorderTop(BorderStyle.THIN);
            alternateRowStyle.setBorderBottom(BorderStyle.THIN);
            alternateRowStyle.setBorderLeft(BorderStyle.THIN);
            alternateRowStyle.setBorderRight(BorderStyle.THIN);

            boolean alternate = false;
            for (CartList item : items) {
                Row itemRow = sheet.createRow(rowNum++);
                CellStyle rowStyle = alternate ? alternateRowStyle : normalRowStyle;

                Cell itemIdCell = itemRow.createCell(0);
                itemIdCell.setCellValue(item.getItemId() == null ? "" : item.getItemId());
                itemIdCell.setCellStyle(rowStyle);

                Cell itemNameCell = itemRow.createCell(1);
                itemNameCell.setCellValue(item.getItemName() == null ? "" : item.getItemName());
                itemNameCell.setCellStyle(rowStyle);

                Cell qtyCell = itemRow.createCell(2);
                qtyCell.setCellValue(item.getQuantity());
                qtyCell.setCellStyle(rowStyle);

                Cell unitPriceCell = itemRow.createCell(3);
                unitPriceCell.setCellValue(Double.parseDouble(df.format(item.getUnitPrice())));
                unitPriceCell.setCellStyle(rowStyle);

                Cell totalCell = itemRow.createCell(4);
                totalCell.setCellValue(Double.parseDouble(df.format(item.getTotal())));
                totalCell.setCellStyle(rowStyle);

                alternate = !alternate;
            }

            // Add empty rows for spacing (like in template)
            for (int i = 0; i < 5; i++) {
                Row emptyRow = sheet.createRow(rowNum++);
                for (int j = 0; j < 5; j++) {
                    Cell emptyCell = emptyRow.createCell(j);
                    emptyCell.setCellValue("-");
                    emptyCell.setCellStyle(normalRowStyle);
                }
            }

            rowNum++; // Space before totals

            // ===== TOTALS SECTION =====
            // Subtotal
            Row subtotalRow = sheet.createRow(rowNum++);
            Cell subtotalLabelCell = subtotalRow.createCell(3);
            subtotalLabelCell.setCellValue("SUBTOTAL");
            Cell subtotalValueCell = subtotalRow.createCell(4);
            subtotalValueCell.setCellValue(Double.parseDouble(df.format(netTotal)));

            // Tax, Shipping, Other (set to dash/zero as per template)
            String[] totalLabels = {"TAX", "SHIPPING", "OTHER"};
            for (String label : totalLabels) {
                Row totalRow = sheet.createRow(rowNum++);
                Cell labelCell = totalRow.createCell(3);
                labelCell.setCellValue(label);
                Cell valueCell = totalRow.createCell(4);
                valueCell.setCellValue("-");
            }

            // Final Total with yellow background
            Row finalTotalRow = sheet.createRow(rowNum++);
            Cell totalLabelCell = finalTotalRow.createCell(3);
            totalLabelCell.setCellValue("TOTAL");
            Cell totalValueCell = finalTotalRow.createCell(4);
            totalValueCell.setCellValue("Rs. " + df.format(netTotal));

            CellStyle yellowTotalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalFont.setFontHeightInPoints((short) 12);
            yellowTotalStyle.setFont(totalFont);
            yellowTotalStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            yellowTotalStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            yellowTotalStyle.setBorderTop(BorderStyle.THICK);
            yellowTotalStyle.setBorderBottom(BorderStyle.THICK);
            yellowTotalStyle.setBorderLeft(BorderStyle.THICK);
            yellowTotalStyle.setBorderRight(BorderStyle.THICK);

            totalLabelCell.setCellStyle(yellowTotalStyle);
            totalValueCell.setCellStyle(yellowTotalStyle);

            // ===== Comments Section =====
            rowNum++;
            Row commentsHeaderRow = sheet.createRow(rowNum++);
            Cell commentsHeaderCell = commentsHeaderRow.createCell(0);
            commentsHeaderCell.setCellValue("Comments or Special Instructions");
            commentsHeaderCell.setCellStyle(greenHeaderStyle);
            sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

            // Add a few empty rows for comments
            for (int i = 0; i < 3; i++) {
                Row commentRow = sheet.createRow(rowNum++);
                Cell commentCell = commentRow.createCell(0);
                commentCell.setCellValue("");
                commentCell.setCellStyle(normalRowStyle);
                sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));
            }

            // ===== Set Column Widths =====
            sheet.setColumnWidth(0, 3000);  // Item #
            sheet.setColumnWidth(1, 8000);  // Description
            sheet.setColumnWidth(2, 2500);  // QTY
            sheet.setColumnWidth(3, 3000);  // Unit Price
            sheet.setColumnWidth(4, 3000);  // Total
            sheet.setColumnWidth(5, 3000);  // Extra space

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

        // Success alert
        new Alert(Alert.AlertType.INFORMATION, "Purchase Order saved successfully at:\n" + file.getAbsolutePath()).show();
    }
}

