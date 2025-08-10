package controller.item;

import javafx.collections.ObservableList;
import model.Item;

import java.util.List;


public interface ItemService {

    boolean addItem (Item item);

    boolean updateItem(Item item);

    boolean deleteItem(String itemId ) ;

    Item searchItem(String itemId);

    ObservableList<Item> getAll();

    String getNextItemId();

    ObservableList<String>getItemIds();


    List<Item> searchItemsByNamePattern(String itemNamePart);
}
