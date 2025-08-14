package controller.supplier;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import model.Item;
import model.Supplier;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import single.DashBoardForm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SupplierMangeFormController implements Initializable {

    public TableColumn colSupplierEmail;

    public TextField txtSupplierEmail;

    public TextField supplierEmail;
    @FXML
    private TextField SupplierAddress;

    @FXML
    private TextField SupplierContactNo;

    @FXML
    private TableColumn  colSupplierAddress;

    @FXML
    private TableColumn  colSupplierContactNo;

    @FXML
    private TableColumn  colSupplierId;

    @FXML
    private TableColumn  colSupplierName;

    @FXML
    private TextField SupplierId;

    @FXML
    private TextField SupplierName;

    @FXML
    private TableView tblSupplier;

    @FXML
    private TextField txtSearchSupplierByName;

    @FXML
    private TextArea txtSupplierAddress;

    @FXML
    private TextField txtSupplierContactNo;

    @FXML
    private TextField txtSupplierId;

    @FXML
    private TextField txtSupplierName;

    SupplierService supplierService = SupplierController.getInstance();

    @FXML
    void btnAddSupplierOnAction(ActionEvent event) {

        String contactNo = txtSupplierContactNo.getText();
        boolean state =  true;

        if (!(contactNo.startsWith("07") && contactNo.length()==10)){
            new Alert(Alert.AlertType.ERROR, "Invalid contact Number ! please try again again").show();
            state = false;
            txtSupplierContactNo.setText(null);
        }
        if(state){
            Supplier supplier = new Supplier(
                    txtSupplierId.getText(),
                    txtSupplierName.getText(),
                    txtSupplierAddress.getText(),
                    txtSupplierEmail.getText(),
                    txtSupplierContactNo.getText()
            );
            if(supplierService.addSupplier(supplier)){
                new Alert(Alert.AlertType.INFORMATION , "Supplier Added successfully").show();
                clearFields();
                nextIdGenerated();
            }else{
                new Alert(Alert.AlertType.ERROR,"Supplier not Added ! please try again").show();
            }
        }




    }



    @FXML
    void btnClearOnAction(ActionEvent event) {
        txtSearchSupplierByName.setText(null);
        SupplierId.setText(null);
        SupplierName.setText(null);
        SupplierAddress.setText(null);
        supplierEmail.setText(null);
        SupplierContactNo.setText(null);

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
            if(supplierService.deleteSupplier(txtSearchSupplierByName.getText())){
                new Alert(Alert.AlertType.INFORMATION,"Supplier Deleted !!").show();
                txtSearchSupplierByName.setText(null);
                clearSearchField();
                loadTable();
                nextIdGenerated();
            }else{
                new Alert(Alert.AlertType.ERROR,"Invalid Supplier Id ! please try again ").show();
                clearSearchField();
            }
    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {
            loadTable();
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String supplierId = txtSearchSupplierByName.getText();
        Supplier supplier = supplierService.SearchSupplier(supplierId);
        if (Objects.equals(supplier.getSupplierId(), supplierId)){
            SupplierId.setText(supplier.getSupplierId());
            SupplierName.setText(supplier.getSupplierName());
            SupplierAddress.setText(supplier.getSupplierAddress());
            supplierEmail.setText(supplier.getSupplierEmail());
            SupplierContactNo.setText(supplier.getContactNo());
        }else{
            new Alert(Alert.AlertType.ERROR,"Invalid Supplier Id! please try again").show();
            clearSearchField();
        }

    }

    private void clearSearchField() {
        txtSearchSupplierByName.setText(null);
        SupplierId.setText(null);
        SupplierName.setText(null);
        SupplierAddress.setText(null);
        supplierEmail.setText(null);
        SupplierContactNo.setText(null);
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        Supplier supplier =  new Supplier(
                SupplierId.getText(),
                SupplierName.getText(),
                SupplierAddress.getText(),
                supplierEmail.getText(),
                SupplierContactNo.getText()
        );
        if (supplierService.updateSupplier(supplier)){
            new Alert(Alert.AlertType.INFORMATION, "Supplier Update Successfully").show();
            loadTable();
            clearSearchField();


        }else{
            new Alert(Alert.AlertType.ERROR ,  "Supplier Not Updated! please try again").show();
        }

    }

    @FXML
    void homeMouseOnClick(MouseEvent event) {
        DashBoardForm dashBoardForm = DashBoardForm.getInstance();
        dashBoardForm.show();
    }

    private void clearFields() {
        txtSupplierId.setText(null);
        txtSupplierName.setText(null);
        txtSupplierAddress.setText(null);
        txtSupplierEmail.setText(null);
        txtSupplierContactNo.setText(null);
    }

    public void nextIdGenerated() {
        String lastId = supplierService.getNextSupplierId(); // e.g., "S001"

        if (lastId != null && lastId.startsWith("S")) {
            String numericPart = lastId.substring(1); // e.g., "001"
            int idNum = Integer.parseInt(numericPart); // parse "001" -> 1
            int nextId = idNum + 1; // increment -> 2

            String newId = String.format("S%03d", nextId); // format -> "S002"
            txtSupplierId.setText(newId);
        } else {
            // If no ID is found or invalid, start from "S001"
            txtSupplierId.setText("S001");
        }
    }

    public void loadTable(){
        ObservableList<Supplier> supplierObservableList = supplierService.getAll();

        if (supplierObservableList.isEmpty()){
            tblSupplier.setItems(supplierObservableList);
        }else{
            tblSupplier.getItems().clear();
            tblSupplier.setItems(supplierObservableList);
        }
    }




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colSupplierAddress.setCellValueFactory(new PropertyValueFactory<>("supplierAddress"));
        colSupplierEmail.setCellValueFactory(new PropertyValueFactory<>("supplierEmail"));
        colSupplierContactNo.setCellValueFactory(new PropertyValueFactory<>("contactNo"));

        loadTable();

        nextIdGenerated();
    }

    public void btnExportOnAction(ActionEvent actionEvent) {

        try {
            // Get all items
            ObservableList<Supplier> suppliers = supplierService.getAll();
            if (suppliers.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "No supplier to export!").show();
                return;
            }

            // Ask user for save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Excel File");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
            fileChooser.setInitialFileName("supplier.xlsx");
            File file = fileChooser.showSaveDialog(tblSupplier.getScene().getWindow());
            if (file == null) return;

            // Create workbook and sheet
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Suppliers");

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
            org.apache.poi.ss.usermodel.Cell titleCell = titleRow.createCell(3);
            titleCell.setCellValue("SUPPLIER LIST");
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

            String[] columns = {"Supplier ID", "Supplier Name", "Supplier Address", "Supplier Email", "Contact No"};

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
            for (Supplier supplier : suppliers) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(supplier.getSupplierId());
                row.createCell(1).setCellValue(supplier.getSupplierName());
                row.createCell(2).setCellValue(supplier.getSupplierAddress());
                row.createCell(3).setCellValue(supplier.getSupplierEmail());
                row.createCell(4).setCellValue(supplier.getContactNo());

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

