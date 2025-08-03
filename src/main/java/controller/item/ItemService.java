package controller.item;

import javafx.collections.ObservableList;
import model.Item;
import model.OrderDetails;

import java.util.List;

public interface ItemService {

    boolean addItem (Item item);

    boolean updateItem(Item item);

    Item searchItem(String itemId);

    ObservableList<Item> getAll();

    String getNextItemId();

    boolean updateStock(List<OrderDetails> orderDetailsList);



}
