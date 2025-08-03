package controller.item;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import single.DashBoardForm;

import java.net.URL;
import java.util.ResourceBundle;

public class ItemManageFormController implements Initializable {

    @FXML
    private ComboBox  cmdItemType;

    @FXML
    private TableColumn colItemId;

    @FXML
    private TableColumn  colItemName;

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
    private TextField txtItemSearchByName;

    @FXML
    private TextField txtItemUnitPrice;

     ItemService itemService = ItemController.getInstance();

    @FXML
    void btnAddItemOnAction(ActionEvent event) {

    }

    @FXML
    void btnClearOnAction(ActionEvent event) {

    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {

    }

    @FXML
    void btnItemSearchByNameOnAction(ActionEvent event) {

    }

    @FXML
    void btnRefreshOnAction(ActionEvent event) {

    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {

    }

    @FXML
    void homeMouseOnClick(MouseEvent event) {
        DashBoardForm dashBoardForm = DashBoardForm.getInstance();
        dashBoardForm.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> itemTypeList= FXCollections.observableArrayList();
        itemTypeList.add("Single");
        itemTypeList.add("Double");
        cmdItemType.setItems(itemTypeList);
    }
}
