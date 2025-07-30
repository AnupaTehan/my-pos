package controller.supplier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import single.DashBoardForm;

import java.net.URL;
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
    private TextField supplierId;

    @FXML
    private TextField supplierName;

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

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {

    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

    @FXML
    void homeMouseOnClick(MouseEvent event) {
        DashBoardForm dashBoardForm = DashBoardForm.getInstance();
        dashBoardForm.show();
    }

   public void nextIdGenerated(){
        String lastId = supplierService.getNextSupplierId();

        String numericPart = lastId.substring(1);

        int nextId  = Integer.parseInt((numericPart) + 1);
        String newId = String.format("S%03d" , nextId);

        txtSupplierId.setText(newId);
   }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        nextIdGenerated();
    }
}
