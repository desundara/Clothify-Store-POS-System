package edu.icet.service.interfaces;

import edu.icet.model.InventoryLogDTO;
import edu.icet.model.ProductDTO;

import java.util.List;

public interface InventoryService {
    boolean updateStock(String productId, int quantity);
    int getCurrentStock(String productId);
    List<InventoryLogDTO> getInventoryHistory(String productId);
    List<ProductDTO> getLowStockProducts(int threshold);
    boolean addStock(String productId, int quantity, String reason);
    boolean removeStock(String productId, int quantity, String reason);
}