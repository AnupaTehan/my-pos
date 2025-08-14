package controller.item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Item;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import single.DashBoardForm;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.stage.FileChooser;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.util.IOUtils;

import java.io.*;


public class ItemManageFormController implements Initializable {

    public TableColumn colItemDate;

    public TextField txtItemDate;

    public TextField itemDate;

    public ComboBox cmdItemCodeType;

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
                txtItemDate.getText()
        );
        if (itemservice.addItem(item)){
            new Alert(Alert.AlertType.INFORMATION, "Item Added Successfully").show();
            clearField();
            setupItemCodeTypeDropdown();

        }else{
            new Alert(Alert.AlertType.ERROR , "Item Not Added ! Please try again").show();
        }



    }

    private void clearField() {
        txtItemId.setText(null);
        txtItemName.setText(null);
        txtItemUnitPrice.setText(null);
        txtItemDate.setText(null);
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
            itemDate.setText(item.getDate());

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
                itemDate.getText()
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
        colItemDate.setCellValueFactory(new PropertyValueFactory<>("date"));

        ObservableList<String> itemTypeList = FXCollections.observableArrayList("Each", "Pair");
        cmdItemType.setItems(itemTypeList);
        itemUnitType.setItems(itemTypeList);

        ObservableList<String> ItemCodeTypeList = FXCollections.observableArrayList("Head Protection", "Eye Protection" , "Ear Protection","Respirator and Mask","Hand Protection","Body Protection","Foot Protection");
        cmdItemCodeType.setItems(ItemCodeTypeList);
        setupItemCodeTypeDropdown();


        loadTable();

        setupArrowKeyNavigationForItemForm();


        txtItemDate.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
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

    private void setupArrowKeyNavigationForItemForm() {
        // txtItemId
        txtItemId.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                case RIGHT:
                    txtItemName.requestFocus();
                    break;
                case UP:
                case LEFT:
                    txtItemDate.requestFocus(); // wrap-around
                    break;
            }
        });

        // txtItemName
        txtItemName.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                case RIGHT:
                    cmdItemType.requestFocus();
                    break;
                case UP:
                case LEFT:
                    txtItemId.requestFocus();
                    break;
            }
        });

        // cmdItemType
        cmdItemType.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                case RIGHT:
                    txtItemUnitPrice.requestFocus();
                    break;
                case UP:
                case LEFT:
                    txtItemName.requestFocus();
                    break;
            }
        });

        // txtItemUnitPrice
        txtItemUnitPrice.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                case RIGHT:
                    txtItemDate.requestFocus();
                    break;
                case UP:
                case LEFT:
                    cmdItemType.requestFocus();
                    break;
            }
        });

        // txtItemDate
        txtItemDate.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                case RIGHT:
                    txtItemId.requestFocus(); // wrap-around
                    break;
                case UP:
                case LEFT:
                    txtItemUnitPrice.requestFocus();
                    break;
                case ENTER:
                    btnAddItemOnAction(new ActionEvent()); // Add item on Enter
                    break;
            }
        });
    }



    private void setupItemCodeTypeDropdown() {
        ObservableList<String> itemCodeTypeList = FXCollections.observableArrayList(
                "Head Protection",
                "Eye Protection",
                "Ear Protection",
                "Respirator and Mask",
                "Hand Protection",
                "Body Protection",
                "Foot Protection"
        );
        cmdItemCodeType.setItems(itemCodeTypeList);

        // Map category name to prefix
        Map<String, String> categoryPrefixes = Map.of(
                "Head Protection", "HEP",
                "Eye Protection", "EYP",
                "Ear Protection", "EAR",
                "Respirator and Mask", "RAM",
                "Hand Protection", "HAP",
                "Body Protection", "BOP",
                "Foot Protection", "FOP"
        );

        // Generate ID when a category is selected
        cmdItemCodeType.setOnAction(e -> {
            String selectedCategory = (String) cmdItemCodeType.getValue();
            if (selectedCategory != null) {
                String prefix = categoryPrefixes.get(selectedCategory);
                String nextId = itemservice.getNextItemId(prefix);
                txtItemId.setText(nextId);
            }
        });
    }




    public void btnExportOnAction(ActionEvent actionEvent) {
        try {
            // Get all items
            ObservableList<Item> items = itemservice.getAll();
            if (items.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "No items to export!").show();
                return;
            }

            // Ask user for save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Excel File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            fileChooser.setInitialFileName("Items.xlsx");
            File file = fileChooser.showSaveDialog(tblItem.getScene().getWindow());
            if (file == null) return;

            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Items");

            // === Insert PNG Logo with smaller size ===
            InputStream logoInput = new FileInputStream("D:\\chathuranga_project\\src\\main\\resources\\img\\logo.png");
            byte[] logoBytes = IOUtils.toByteArray(logoInput);
            logoInput.close();

            int pictureIdx = workbook.addPicture(logoBytes, Workbook.PICTURE_TYPE_PNG);
            CreationHelper helper = workbook.getCreationHelper();
            Drawing<?> drawing = sheet.createDrawingPatriarch();
            ClientAnchor anchor = helper.createClientAnchor();

            anchor.setCol1(0); // Start column
            anchor.setRow1(0); // Start row
            anchor.setCol2(2); // End column (smaller width)
            anchor.setRow2(6); // End row (smaller height)
            anchor.setDx1(50); // Horizontal offset
            anchor.setDy1(100);  // Vertical offset

            Picture pict = drawing.createPicture(anchor, pictureIdx);
            pict.resize(0.5); // Resize proportionally to fit smaller space

            // === "ITEM LIST" Title with spacing below logo ===
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setColor(IndexedColors.WHITE.getIndex());
            titleStyle.setFont(titleFont);
            titleStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            titleStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);
            titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Merge cells for title next to logo
            sheet.addMergedRegion(new CellRangeAddress(0, 2, 3, 8)); // Title starts at column 3
            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(3);
            titleCell.setCellValue("ITEM LIST");
            titleCell.setCellStyle(titleStyle);

            // Add empty row for spacing between title and table
            sheet.createRow(3);

            // === Table Header Style ===
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            String[] columns = {"Item ID", "Item Name", "Unit Type", "Unit Price", "Date"};

            // Header row below spacing
            int headerRowIndex = 4;
            Row headerRow = sheet.createRow(headerRowIndex);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // === Table Body Style ===
            CellStyle bodyStyle = workbook.createCellStyle();
            bodyStyle.setBorderBottom(BorderStyle.THIN);
            bodyStyle.setBorderTop(BorderStyle.THIN);
            bodyStyle.setBorderLeft(BorderStyle.THIN);
            bodyStyle.setBorderRight(BorderStyle.THIN);

            // Fill table data
            int rowNum = headerRowIndex + 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getItemId());
                row.createCell(1).setCellValue(item.getItemName());
                row.createCell(2).setCellValue(item.getUnitType());
                row.createCell(3).setCellValue(item.getUnitPrice());
                row.createCell(4).setCellValue(item.getDate());

                for (int i = 0; i < columns.length; i++) {
                    row.getCell(i).setCellStyle(bodyStyle);
                }
            }

            // === Set Column Widths ===
            sheet.setColumnWidth(0, 13000);
            sheet.setColumnWidth(1, 10000);
            sheet.setColumnWidth(2, 7000);
            sheet.setColumnWidth(3, 7000);
            sheet.setColumnWidth(4, 8000);

            // Save file
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
            }
            workbook.close();

            new Alert(Alert.AlertType.INFORMATION, "Excel exported with style!").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error: " + e.getMessage()).show();
        }
    }



}


