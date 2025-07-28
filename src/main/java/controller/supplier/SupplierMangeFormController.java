package controller.supplier;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import single.DashBoardForm;

public class SupplierMangeFormController {

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

}
