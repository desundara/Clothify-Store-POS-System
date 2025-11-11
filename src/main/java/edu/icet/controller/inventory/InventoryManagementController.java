package edu.icet.controller.inventory;

import edu.icet.dao.InventoryLogDAO;
import edu.icet.dao.ProductDAO;
import edu.icet.dao.SupplierDAO;
import edu.icet.model.InventoryLogDTO;
import edu.icet.model.ProductDTO;
import edu.icet.model.SupplierDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Getter
@Setter
public class InventoryManagementController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> cmbChangeType;
    @FXML private DatePicker dateFrom;
    @FXML private DatePicker dateTo;
    @FXML private Button btnSearch;
    @FXML private Button btnClearFilter;
    @FXML private TableView<InventoryLogTableModel> inventoryTable;
    @FXML private TableColumn<InventoryLogTableModel, Integer> colLogId;
    @FXML private TableColumn<InventoryLogTableModel, String> colProduct;
    @FXML private TableColumn<InventoryLogTableModel, String> colSupplier;
    @FXML private TableColumn<InventoryLogTableModel, String> colChangeType;
    @FXML private TableColumn<InventoryLogTableModel, Integer> colQtyChanged;
    @FXML private TableColumn<InventoryLogTableModel, String> colDate;
    @FXML private Label lblTotalIn;
    @FXML private Label lblTotalOut;
    @FXML private Label lblNetChange;
    @FXML private ComboBox<String> cmbProduct;
    @FXML private ComboBox<String> cmbSupplier;
    @FXML private RadioButton rbStockIn;
    @FXML private RadioButton rbStockOut;
    @FXML private TextField txtQuantity;
    @FXML private TextArea txtNotes;
    @FXML private Button btnAddTransaction;
    @FXML private Button btnClearForm;

    private ObservableList<InventoryLogTableModel> inventoryLogs = FXCollections.observableArrayList();
    private ObservableList<ProductDTO> products = FXCollections.observableArrayList();
    private ObservableList<SupplierDTO> suppliers = FXCollections.observableArrayList();
    private ToggleGroup transactionTypeGroup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Controller initialize method started...");

        setupTableColumns();
        setupTransactionTypeToggle();
        loadComboBoxData();
        loadInventoryData();
        calculateSummary();

        System.out.println("✅ Controller initialization completed!");
    }

    private void setupTableColumns() {
        colLogId.setCellValueFactory(new PropertyValueFactory<>("logId"));
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colChangeType.setCellValueFactory(new PropertyValueFactory<>("changeType"));
        colQtyChanged.setCellValueFactory(new PropertyValueFactory<>("qtyChanged"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        inventoryTable.setItems(inventoryLogs);
    }

    private void setupTransactionTypeToggle() {
        transactionTypeGroup = new ToggleGroup();
        rbStockIn.setToggleGroup(transactionTypeGroup);
        rbStockOut.setToggleGroup(transactionTypeGroup);
        rbStockIn.setSelected(true);
    }

    private void loadComboBoxData() {
        System.out.println("Loading combo box data...");

        // Load change type filter options
        cmbChangeType.getItems().addAll("All Types", "IN", "OUT");
        cmbChangeType.setValue("All Types");
        System.out.println("✅ Change type combo box loaded: " + cmbChangeType.getItems().size() + " items");

        // Load products and suppliers
        loadProducts();
        loadSuppliers();
    }

    private void loadProducts() {
        try {
            System.out.println("Loading products from database...");
            List<ProductDTO> productList = ProductDAO.getAllProducts();
            System.out.println("Products found: " + productList.size());

            products.clear();
            products.addAll(productList);

            cmbProduct.getItems().clear();
            for (ProductDTO product : productList) {
                String displayText = product.getName() + " - " + product.getCode();
                cmbProduct.getItems().add(displayText);
                System.out.println("   ➕ Added product: " + displayText);
            }

            if (!productList.isEmpty()) {
                cmbProduct.setPromptText("Select Product (" + productList.size() + " available)");
                System.out.println("✅ Product combo box loaded with " + cmbProduct.getItems().size() + " items");
            } else {
                cmbProduct.setPromptText("No products available");
                System.out.println("No products available in database");

                // Add some sample items for testing
                cmbProduct.getItems().addAll("T-Shirt - TS001", "Jeans - JN001", "Jacket - JK001");
                System.out.println("➕ Added sample products for testing");
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading products: " + e.getMessage());
            cmbProduct.setPromptText("Error loading products");

            // Add sample items even if there's an error
            cmbProduct.getItems().addAll("T-Shirt - TS001", "Jeans - JN001", "Jacket - JK001");
            System.out.println("➕ Added sample products due to error");
        }

        // Force UI refresh
        cmbProduct.getSelectionModel().clearSelection();
        System.out.println("Final product combo box items: " + cmbProduct.getItems().size());
    }

    private void loadSuppliers() {
        try {
            System.out.println("Loading suppliers from database...");
            List<SupplierDTO> supplierList = SupplierDAO.getAllSuppliers();
            System.out.println("Suppliers found: " + supplierList.size());

            suppliers.clear();
            suppliers.addAll(supplierList);

            cmbSupplier.getItems().clear();
            for (SupplierDTO supplier : supplierList) {
                cmbSupplier.getItems().add(supplier.getName());
                System.out.println("   ➕ Added supplier: " + supplier.getName());
            }

            // Add "N/A" option for no supplier
            cmbSupplier.getItems().add("N/A");
            System.out.println("   ➕ Added N/A option");

            if (!supplierList.isEmpty()) {
                cmbSupplier.setPromptText("Select Supplier (" + supplierList.size() + " available)");
                System.out.println("✅ Supplier combo box loaded with " + cmbSupplier.getItems().size() + " items");
            } else {
                cmbSupplier.setPromptText("No suppliers available");
                System.out.println("No suppliers available in database");

                // Add some sample items for testing
                cmbSupplier.getItems().addAll("Fashion Suppliers", "Textile World", "Garment Plus");
                System.out.println("➕ Added sample suppliers for testing");
            }

        } catch (Exception e) {
            System.err.println("❌ Error loading suppliers: " + e.getMessage());
            cmbSupplier.setPromptText("Error loading suppliers");

            // Add sample items even if there's an error
            cmbSupplier.getItems().addAll("Fashion Suppliers", "Textile World", "Garment Plus", "N/A");
            System.out.println("➕ Added sample suppliers due to error");
        }

        // Force UI refresh
        cmbSupplier.getSelectionModel().clearSelection();
        System.out.println("Final supplier combo box items: " + cmbSupplier.getItems().size());
    }

    private void loadInventoryData() {
        try {
            System.out.println("Loading inventory data...");
            List<InventoryLogDTO> logList = InventoryLogDAO.getAllInventoryLogs();
            System.out.println("Inventory logs found: " + logList.size());

            inventoryLogs.clear();

            for (InventoryLogDTO logDTO : logList) {
                String productName = getProductNameById(logDTO.getProductId());
                String supplierName = getSupplierNameById(logDTO.getSupplierId());
                String formattedDate = formatDateTime(logDTO.getDate());
                String changeTypeDisplay = "IN".equals(logDTO.getChangeType()) ? "Stock In" : "Stock Out";

                inventoryLogs.add(new InventoryLogTableModel(
                        logDTO.getLogId(),
                        productName,
                        supplierName,
                        changeTypeDisplay,
                        logDTO.getQtyChanged(),
                        formattedDate
                ));
            }

            System.out.println("✅ Loaded " + inventoryLogs.size() + " inventory logs to table");

        } catch (Exception e) {
            System.err.println("❌ Error loading inventory data: " + e.getMessage());
            showAlert("Error", "Failed to load inventory data: " + e.getMessage());
        }
    }

    private String getProductNameById(int productId) {
        return products.stream()
                .filter(product -> product.getProductId() == productId)
                .findFirst()
                .map(product -> product.getName() + " - " + product.getCode())
                .orElse("Unknown Product (ID: " + productId + ")");
    }

    private String getSupplierNameById(Integer supplierId) {
        if (supplierId == null) {
            return "N/A";
        }
        return suppliers.stream()
                .filter(supplier -> supplier.getSupplierId() == supplierId)
                .findFirst()
                .map(SupplierDTO::getName)
                .orElse("Unknown Supplier (ID: " + supplierId + ")");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ?
                dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A";
    }

    @FXML
    void btnSearchOnAction(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = cmbChangeType.getValue();
        LocalDate fromDate = dateFrom.getValue();
        LocalDate toDate = dateTo.getValue();

        ObservableList<InventoryLogTableModel> filteredList = FXCollections.observableArrayList();

        for (InventoryLogTableModel log : inventoryLogs) {
            boolean matchesSearch = searchText.isEmpty() ||
                    log.getProductName().toLowerCase().contains(searchText) ||
                    log.getSupplierName().toLowerCase().contains(searchText);

            boolean matchesType = "All Types".equals(selectedType) ||
                    log.getChangeType().equals(selectedType.equals("IN") ? "Stock In" : "Stock Out");

            if (matchesSearch && matchesType) {
                filteredList.add(log);
            }
        }

        inventoryTable.setItems(filteredList);
        calculateSummaryFromList(filteredList);

        if (filteredList.isEmpty() && !inventoryLogs.isEmpty()) {
            showAlert("Info", "No matching records found!");
        }
    }

    @FXML
    void btnClearFilterOnAction(ActionEvent event) {
        searchField.clear();
        cmbChangeType.setValue("All Types");
        dateFrom.setValue(null);
        dateTo.setValue(null);
        loadInventoryData();
        calculateSummary();
    }

    @FXML
    void btnAddTransactionOnAction(ActionEvent event) {
        if (validateForm()) {
            try {
                String selectedProductDisplay = cmbProduct.getValue();
                String selectedSupplierDisplay = cmbSupplier.getValue();
                String changeType = rbStockIn.isSelected() ? "IN" : "OUT";
                int quantity = Integer.parseInt(txtQuantity.getText());

                System.out.println("Adding transaction:");
                System.out.println("   Product: " + selectedProductDisplay);
                System.out.println("   Supplier: " + selectedSupplierDisplay);
                System.out.println("   Type: " + changeType);
                System.out.println("   Qty: " + quantity);

                // Find actual ProductDTO from display text
                ProductDTO selectedProduct = findProductByDisplay(selectedProductDisplay);
                if (selectedProduct == null) {
                    showAlert("Error", "Invalid product selection!");
                    return;
                }

                // Find actual SupplierDTO from display text (optional)
                SupplierDTO selectedSupplier = null;
                if (selectedSupplierDisplay != null && !"N/A".equals(selectedSupplierDisplay)) {
                    selectedSupplier = findSupplierByName(selectedSupplierDisplay);
                }

                // Create InventoryLogDTO
                InventoryLogDTO logDTO = new InventoryLogDTO();
                logDTO.setProductId(selectedProduct.getProductId());
                logDTO.setSupplierId(selectedSupplier != null ? selectedSupplier.getSupplierId() : null);
                logDTO.setChangeType(changeType);
                logDTO.setQtyChanged(quantity);
                logDTO.setDate(LocalDateTime.now());

                // Save to database
                boolean success = InventoryLogDAO.addInventoryLog(logDTO);

                if (success) {
                    showAlert("Success", "Inventory transaction added successfully!");
                    clearForm();
                    loadInventoryData(); // Reload data
                    calculateSummary();
                } else {
                    showAlert("Error", "Failed to add inventory transaction!");
                }

            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter a valid quantity!");
            } catch (Exception e) {
                showAlert("Error", "An error occurred: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private ProductDTO findProductByDisplay(String displayText) {
        return products.stream()
                .filter(product -> (product.getName() + " - " + product.getCode()).equals(displayText))
                .findFirst()
                .orElse(null);
    }

    private SupplierDTO findSupplierByName(String supplierName) {
        return suppliers.stream()
                .filter(supplier -> supplier.getName().equals(supplierName))
                .findFirst()
                .orElse(null);
    }

    @FXML
    void btnClearFormOnAction(ActionEvent event) {
        clearForm();
    }

    private boolean validateForm() {
        // Validate product selection
        if (cmbProduct.getValue() == null || cmbProduct.getValue().isEmpty()) {
            showAlert("Validation Error", "Please select a product!");
            cmbProduct.requestFocus();
            return false;
        }

        // Validate quantity
        if (txtQuantity.getText().isEmpty()) {
            showAlert("Validation Error", "Please enter quantity!");
            txtQuantity.requestFocus();
            return false;
        }

        try {
            int quantity = Integer.parseInt(txtQuantity.getText());
            if (quantity <= 0) {
                showAlert("Validation Error", "Quantity must be greater than zero!");
                txtQuantity.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Validation Error", "Please enter a valid number for quantity!");
            txtQuantity.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        cmbProduct.setValue(null);
        cmbSupplier.setValue(null);
        rbStockIn.setSelected(true);
        txtQuantity.clear();
        txtNotes.clear();
    }

    private void calculateSummary() {
        calculateSummaryFromList(inventoryLogs);
    }

    private void calculateSummaryFromList(ObservableList<InventoryLogTableModel> logList) {
        int totalIn = 0;
        int totalOut = 0;

        for (InventoryLogTableModel log : logList) {
            if ("Stock In".equals(log.getChangeType()) || "IN".equals(log.getChangeType())) {
                totalIn += log.getQtyChanged();
            } else if ("Stock Out".equals(log.getChangeType()) || "OUT".equals(log.getChangeType())) {
                totalOut += log.getQtyChanged();
            }
        }

        lblTotalIn.setText(String.valueOf(totalIn));
        lblTotalOut.setText(String.valueOf(totalOut));
        lblNetChange.setText(String.valueOf(totalIn - totalOut));
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Table Model class for display
    @Getter
    @Setter
    public static class InventoryLogTableModel {
        private final Integer logId;
        private final String productName;
        private final String supplierName;
        private final String changeType;
        private final Integer qtyChanged;
        private final String date;

        public InventoryLogTableModel(Integer logId, String productName, String supplierName,
                                      String changeType, Integer qtyChanged, String date) {
            this.logId = logId;
            this.productName = productName;
            this.supplierName = supplierName;
            this.changeType = changeType;
            this.qtyChanged = qtyChanged;
            this.date = date;
        }
    }
}