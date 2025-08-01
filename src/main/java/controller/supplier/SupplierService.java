package controller.supplier;

import javafx.collections.ObservableList;
import model.Supplier;

public interface SupplierService {

    boolean addSupplier(Supplier supplier);

    boolean updateSupplier(Supplier supplier);

    boolean deleteSupplier(String supplierId);

    Supplier SearchSupplier(String supplierId);

    ObservableList<Supplier> getAll();

    String getNextSupplierId();

    ObservableList<String> getSupplierIds();
}
