package controller.item;

import controller.supplier.SupplierController;
import javafx.collections.ObservableList;
import model.Item;
import model.OrderDetails;

import java.util.List;

public class ItemController implements ItemService {

    private static ItemController instance;

    private ItemController(){
    }

    public static ItemController getInstance(){
        return instance == null  ? instance = new ItemController() : instance;
    }


    @Override
    public boolean addItem(Item item) {
        return false;
    }

    @Override
    public boolean updateItem(Item item) {
        return false;
    }

    @Override
    public Item searchItem(String itemId) {
        return null;
    }

    @Override
    public ObservableList<Item> getAll() {
        return null;
    }

    @Override
    public String getNextItemId() {
        return "";
    }

    @Override
    public boolean updateStock(List<OrderDetails> orderDetailsList) {
        return false;
    }
}
