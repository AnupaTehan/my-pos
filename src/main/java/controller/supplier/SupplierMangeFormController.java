package controller.supplier;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Supplier;
import single.DashBoardForm;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SupplierMangeFormController implements Initializable {

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
        SupplierContactNo.setText(null);
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

        Supplier supplier =  new Supplier(
                SupplierId.getText(),
                SupplierName.getText(),
                SupplierAddress.getText(),
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
        colSupplierContactNo.setCellValueFactory(new PropertyValueFactory<>("contactNo"));

        loadTable();

        nextIdGenerated();
    }
}
