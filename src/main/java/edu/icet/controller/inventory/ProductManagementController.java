package edu.icet.controller.inventory;

import edu.icet.dao.ProductDAO;
import edu.icet.model.ProductDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class ProductManagementController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<ProductDTO> productTable;
    @FXML private TableColumn<ProductDTO, Integer> colProductId;
    @FXML private TableColumn<ProductDTO, String> colCode;
    @FXML private TableColumn<ProductDTO, String> colName;
    @FXML private TableColumn<ProductDTO, Double> colPrice;
    @FXML private TableColumn<ProductDTO, Integer> colQty;
    @FXML private TableColumn<ProductDTO, String> colCategory;

    @FXML private TextField txtProductId;
    @FXML private TextField txtCode;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    @FXML private TextField txtQty;
    @FXML private ComboBox<String> cmbCategory;
    @FXML private TextField txtCategoryId;

    @FXML private Button btnSearch;
    @FXML private Button btnAddProduct;
    @FXML private Button btnUpdateProduct;
    @FXML private Button btnDeleteProduct;
    @FXML private Button btnClear;

    private ObservableList<ProductDTO> productList = FXCollections.observableArrayList();
    private ObservableList<String> categoryNames = FXCollections.observableArrayList();
    private Map<String, Integer> categoryMap = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Product Management Controller Started - Using DBConnection");

        setupTableColumns();
        setupCategoryComboBox();
        loadProductsFromDatabase();
        setupButtonStyles();
    }

    private void setupTableColumns() {
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        productTable.setItems(productList);
        System.out.println("✅ Table columns setup completed");
    }

    private void setupCategoryComboBox() {
        // Use ACTUAL categories from your database
        categoryMap.put("Denim Collection", 1);
        categoryMap.put("Summer Tops", 2);
        categoryMap.put("Shorts & Skirts", 3);
        categoryMap.put("Casual Shirts", 4);

        categoryNames.addAll(categoryMap.keySet());
        cmbCategory.setItems(categoryNames);
        cmbCategory.setPromptText("-- Select Category --");

        // Add listener to automatically set category ID when category is selected
        cmbCategory.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                Integer categoryId = categoryMap.get(newValue);
                if (categoryId != null) {
                    txtCategoryId.setText(String.valueOf(categoryId));
                }
            } else {
                txtCategoryId.clear();
            }
        });

        txtCategoryId.setVisible(false);
        txtCategoryId.setEditable(false);
    }

    private void loadProductsFromDatabase() {
        try {
            List<ProductDTO> products = ProductDAO.getAllProducts();

            // Set category names for each product
            for (ProductDTO product : products) {
                String categoryName = getCategoryNameById(product.getCategoryId());
                product.setCategoryName(categoryName);
            }

            productList.clear();
            productList.addAll(products);

            System.out.println("📦 Loaded " + products.size() + " products into ObservableList");

            if (products.isEmpty()) {
                System.out.println("ℹ️ No products found in database. Add some products!");
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading products: " + e.getMessage());
            showAlert("Error", "Failed to load products from database!", Alert.AlertType.ERROR);
        }
    }

    private String getCategoryNameById(int categoryId) {
        for (Map.Entry<String, Integer> entry : categoryMap.entrySet()) {
            if (entry.getValue() == categoryId) {
                return entry.getKey();
            }
        }
        return "Unknown Category";
    }

    private void setupButtonStyles() {
        btnAddProduct.setStyle("-fx-background-color: #4663AC; -fx-text-fill: white;");
        btnUpdateProduct.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        btnDeleteProduct.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        btnClear.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
    }

    @FXML
    void btnAddProductOnAction() {
        try {
            if (validateForm()) {
                String code = txtCode.getText().trim();
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText());
                int qty = Integer.parseInt(txtQty.getText());
                int categoryId = Integer.parseInt(txtCategoryId.getText());

                // Check if product code already exists in DATABASE
                if (ProductDAO.isCodeExists(code)) {
                    showAlert("Error", "Product code '" + code + "' already exists in database!", Alert.AlertType.ERROR);
                    return;
                }

                // Create product using setter methods
                ProductDTO newProduct = new ProductDTO();
                newProduct.setProductId(0); // Will be auto-generated by database
                newProduct.setCode(code);
                newProduct.setName(name);
                newProduct.setPrice(price);
                newProduct.setQty(qty);
                newProduct.setCategoryId(categoryId);

                // Add to DATABASE
                boolean success = ProductDAO.addProduct(newProduct);

                if (success) {
                    showAlert("Success", "Product '" + name + "' added successfully to database! ✅", Alert.AlertType.INFORMATION);
                    clearForm();
                    loadProductsFromDatabase();
                    productTable.getSelectionModel().select(newProduct);
                } else {
                    showAlert("Error", "Failed to add product to database! ❌", Alert.AlertType.ERROR);
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Price and Quantity!", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void btnUpdateProductOnAction() {
        ProductDTO selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            try {
                if (validateForm()) {
                    String code = txtCode.getText().trim();
                    String name = txtName.getText().trim();
                    double price = Double.parseDouble(txtPrice.getText());
                    int qty = Integer.parseInt(txtQty.getText());
                    int categoryId = Integer.parseInt(txtCategoryId.getText());

                    // Update the product
                    selectedProduct.setCode(code);
                    selectedProduct.setName(name);
                    selectedProduct.setPrice(price);
                    selectedProduct.setQty(qty);
                    selectedProduct.setCategoryId(categoryId);

                    // Update in DATABASE
                    boolean success = ProductDAO.updateProduct(selectedProduct);

                    if (success) {
                        showAlert("Success", "Product '" + name + "' updated successfully in database! ✅", Alert.AlertType.INFORMATION);
                        loadProductsFromDatabase();
                        productTable.getSelectionModel().select(selectedProduct);
                    } else {
                        showAlert("Error", "Failed to update product in database! ❌", Alert.AlertType.ERROR);
                    }
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Please enter valid numbers!", Alert.AlertType.ERROR);
            } catch (Exception e) {
                showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        } else {
            showAlert("Error", "Please select a product to update!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void btnDeleteProductOnAction() {
        ProductDTO selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Product");
            alert.setContentText("Are you sure you want to delete product: " + selectedProduct.getName() + "?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    boolean success = ProductDAO.deleteProduct(selectedProduct.getProductId());

                    if (success) {
                        showAlert("Success", "Product '" + selectedProduct.getName() + "' deleted successfully from database! ✅", Alert.AlertType.INFORMATION);
                        clearForm();
                        loadProductsFromDatabase();
                    } else {
                        showAlert("Error", "Failed to delete product from database! ❌", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    showAlert("Error", "Database error: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        } else {
            showAlert("Error", "Please select a product to delete!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void btnSearchOnAction() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            loadProductsFromDatabase();
        } else {
            ObservableList<ProductDTO> filteredList = FXCollections.observableArrayList();
            List<ProductDTO> allProducts = ProductDAO.getAllProducts();

            for (ProductDTO product : allProducts) {
                if (product.getCode().toLowerCase().contains(searchText) ||
                        product.getName().toLowerCase().contains(searchText) ||
                        String.valueOf(product.getProductId()).contains(searchText)) {
                    filteredList.add(product);
                }
            }

            productList.clear();
            productList.addAll(filteredList);
            System.out.println("🔍 Found " + filteredList.size() + " products matching: " + searchText);
        }
    }

    @FXML
    void btnClearOnAction() {
        clearForm();
        productTable.getSelectionModel().clearSelection();
        searchField.clear();
        loadProductsFromDatabase();
    }

    @FXML
    void handleTableOnMouseClick(MouseEvent event) {
        ProductDTO selectedProduct = productTable.getSelectionModel().getSelectedItem();
        if (selectedProduct != null) {
            txtProductId.setText(String.valueOf(selectedProduct.getProductId()));
            txtCode.setText(selectedProduct.getCode());
            txtName.setText(selectedProduct.getName());
            txtPrice.setText(String.valueOf(selectedProduct.getPrice()));
            txtQty.setText(String.valueOf(selectedProduct.getQty()));

            // Set category name in combo box and ID in hidden field
            String categoryName = getCategoryNameById(selectedProduct.getCategoryId());
            cmbCategory.setValue(categoryName);
            txtCategoryId.setText(String.valueOf(selectedProduct.getCategoryId()));

            System.out.println("📝 Form filled with: " + selectedProduct.getName());
        }
    }

    private boolean validateForm() {
        if (txtCode.getText().isEmpty() ||
                txtName.getText().isEmpty() ||
                txtPrice.getText().isEmpty() ||
                txtQty.getText().isEmpty() ||
                cmbCategory.getValue() == null ||
                cmbCategory.getValue().equals("-- Select Category --")) {
            showAlert("Validation Error", "Please fill all fields!", Alert.AlertType.ERROR);
            return false;
        }
        return true;
    }

    private void clearForm() {
        txtProductId.clear();
        txtCode.clear();
        txtName.clear();
        txtPrice.clear();
        txtQty.clear();
        cmbCategory.getSelectionModel().clearSelection();
        txtCategoryId.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}