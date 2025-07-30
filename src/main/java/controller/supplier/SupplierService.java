package controller.supplier;

import javafx.collections.ObservableList;
import model.Supplier;

public interface SupplierService {

    boolean addSupplier(Supplier supplier);

    boolean updateSupplier(Supplier supplier);

    boolean deleteSupplier(String SupplierId);

    Supplier SearchSupplier(String SupplierId);

    ObservableList<Supplier> getAll();

    String getNextSupplierId();

    ObservableList<String> getSupplierIds();
}
