package edu.icet.controller.sales;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class POSController implements Initializable {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> productColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;
    @FXML private TableColumn<CartItem, Double> priceColumn;
    @FXML private TableColumn<CartItem, Double> totalColumn;
    @FXML private TableColumn<CartItem, Void> actionColumn;
    @FXML private Label subtotalLabel;
    @FXML private Label discountLabel;
    @FXML private Label taxLabel;
    @FXML private Label totalLabel;
    @FXML private ComboBox<String> paymentMethodComboBox;
    @FXML private TextField customerNameField;
    @FXML private Button clearCartButton;
    @FXML private Button processPaymentButton;
    @FXML private Button searchButton;
    @FXML private VBox productContainer;

    @FXML private Button btnDashboard;
    @FXML private Button btnPOS;
    @FXML private Button btnProducts;
    @FXML private Button btnMySales;
    @FXML private Button btnInventory;
    @FXML private Button btnViewStock;
    @FXML private Button btnMyProfile;
    @FXML private Button btnLogout;

    // Database product data - Products table data
    private final ObservableList<Product> allProducts = FXCollections.observableArrayList(
            new Product("Denim", 4500.0, 17, "#3498db", "AP001", "Denim Collection"),
            new Product("Cropped Top", 1950.0, 10, "#e74c3c", "AP002", "Summer Tops"),
            new Product("Silk Short", 1150.0, 0, "#f39c12", "AP003", "Shorts & Skirts"),
            new Product("Cotton Shirt", 2900.0, 9, "#9b59b6", "AP004", "Casual Shirts"),
            new Product("Pyjama", 3800.0, 15, "#1abc9c", "AP005", "Nightwear")
    );

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
    private final String[] paymentMethods = {"Cash", "Credit Card", "Debit Card", "Mobile Payment"};

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupCategoryComboBox();
        setupPaymentMethodComboBox();
        setupCartTable();
        setupEventHandlers();
        loadProducts();
        updateCartSummary();
    }

    // Sidebar Button Actions
    @FXML
    private void btnDashboardOnAction() {
        System.out.println("Dashboard clicked from POS");
        try {
            // Close current POS window
            Stage currentStage = (Stage) btnDashboard.getScene().getWindow();
            currentStage.close();

            // Open Dashboard
            Parent root = FXMLLoader.load(getClass().getResource("/view/dashboard/StaffDashboard.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open Dashboard!");
        }
    }

    @FXML
    private void btnPOSOnAction() {
        System.out.println("POS clicked - Already in POS");
        // Already in POS, so just refresh or do nothing
        showAlert(Alert.AlertType.INFORMATION, "POS", "You are already in POS system!");
    }

    @FXML
    private void btnProductsOnAction() {
        System.out.println("Products View clicked from POS");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/inventory/ProductManagement.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Products - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open Products view!");
        }
    }

    @FXML
    private void btnMySalesOnAction() {
        System.out.println("My Sales clicked from POS");
        showAlert(Alert.AlertType.INFORMATION, "My Sales", "My Sales feature coming soon!");
    }

    @FXML
    private void btnInventoryOnAction() {
        System.out.println("Inventory clicked from POS");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/inventory/InventoryManagement.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Inventory - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open Inventory!");
        }
    }

    @FXML
    private void btnViewStockOnAction() {
        System.out.println("View Stock clicked from POS");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/inventory/StockView.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Stock View - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open Stock View!");
        }
    }

    @FXML
    private void btnMyProfileOnAction() {
        System.out.println("My Profile clicked from POS");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/MyProfile.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("My Profile - CLOTHIFY");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Cannot open Profile!");
        }
    }

    @FXML
    private void btnLogoutOnAction() {
        System.out.println("Logout clicked from POS");
        try {
            // Confirm logout
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Logout Confirmation");
            confirmAlert.setHeaderText("Are you sure you want to logout?");
            confirmAlert.setContentText("Any unsaved sales will be lost.");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                // Close current POS window
                Stage currentStage = (Stage) btnLogout.getScene().getWindow();
                currentStage.close();

                // Open login window
                Parent root = FXMLLoader.load(getClass().getResource("/view/auth/LoginForm.fxml"));
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Login - CLOTHIFY POS");
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Logout Error", "Error during logout!");
        }
    }

    // Rest of your existing POS methods remain the same...
    private void setupCategoryComboBox() {
        ObservableList<String> categories = FXCollections.observableArrayList();
        categories.add("All Categories");

        for (Product product : allProducts) {
            if (!categories.contains(product.getCategory())) {
                categories.add(product.getCategory());
            }
        }

        categoryComboBox.setItems(categories);
        categoryComboBox.getSelectionModel().selectFirst();
    }

    private void setupPaymentMethodComboBox() {
        paymentMethodComboBox.setItems(FXCollections.observableArrayList(paymentMethods));
    }

    private void loadProducts() {
        productContainer.getChildren().clear();

        String selectedCategory = categoryComboBox.getValue();
        String searchText = searchField.getText().toLowerCase();

        for (Product product : allProducts) {
            // Category filter
            if (!selectedCategory.equals("All Categories") && !product.getCategory().equals(selectedCategory)) {
                continue;
            }

            // Search filter
            if (!searchText.isEmpty() &&
                    !product.getName().toLowerCase().contains(searchText) &&
                    !product.getCode().toLowerCase().contains(searchText) &&
                    !product.getCategory().toLowerCase().contains(searchText)) {
                continue;
            }

            // Create product card
            HBox productCard = createProductCard(product);
            productContainer.getChildren().add(productCard);
        }
    }

    private HBox createProductCard(Product product) {
        HBox card = new HBox();
        card.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        card.setSpacing(15);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        card.setPrefWidth(350); // Fixed width සැකසීම

        // Color rectangle
        Rectangle colorRect = new Rectangle(50, 50);
        colorRect.setArcWidth(10);
        colorRect.setArcHeight(10);
        colorRect.setStyle("-fx-fill: " + product.getColor() + ";");

        // Product details - VBox fixed width
        VBox details = new VBox(5);
        details.setPrefWidth(200); // Fixed width
        details.setMaxWidth(200);

        // Product Name - wrap text enable
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true); // Text wrap
        nameLabel.setMaxWidth(180);

        Label priceLabel = new Label(String.format("LKR %,.2f", product.getPrice()));
        priceLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 13;");

        Label stockLabel = new Label("Stock: " + product.getStock());
        if (product.getStock() < 10) {
            stockLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 12;");
        } else {
            stockLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 12;");
        }

        Label codeLabel = new Label("Code: " + product.getCode());
        codeLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11;");

        // Category Label
        Label categoryLabel = new Label("Category: " + product.getCategory());
        categoryLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 11;");
        categoryLabel.setWrapText(true); // Category text wrap
        categoryLabel.setMaxWidth(180);

        details.getChildren().addAll(nameLabel, priceLabel, stockLabel, codeLabel, categoryLabel);

        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        // Add button
        Button addButton = new Button("Add");
        addButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 70; -fx-pref-height: 35; -fx-cursor: hand; -fx-font-size: 12;");
        addButton.setOnAction(event -> handleAddToCart(product));

        // Disable button if out of stock
        if (product.getStock() <= 0) {
            addButton.setDisable(true);
            addButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-pref-width: 130; -fx-pref-height: 35; -fx-font-size: 11;");
            addButton.setText("Out of Stock");
        }

        card.getChildren().addAll(colorRect, details, spacer, addButton);
        return card;
    }

    private void setupCartTable() {
        productColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));

        actionColumn.setCellFactory(param -> new TableCell<CartItem, Void>() {
            private final Button removeButton = new Button("Remove");

            {
                removeButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 12; -fx-cursor: hand;");
                removeButton.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    removeFromCart(item);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });

        cartTable.setItems(cartItems);
    }

    private void setupEventHandlers() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadProducts();
        });

        searchButton.setOnAction(event -> loadProducts());

        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            loadProducts();
        });

        clearCartButton.setOnAction(event -> clearCart());
        processPaymentButton.setOnAction(event -> processPayment());
    }

    private void handleAddToCart(Product product) {
        if (product.getStock() <= 0) {
            showAlert("Out of Stock", "This product is out of stock.");
            return;
        }

        for (CartItem item : cartItems) {
            if (item.getProductName().equals(product.getName())) {
                if (item.getQuantity() >= product.getStock()) {
                    showAlert("Stock Limit", "Cannot add more than available stock.");
                    return;
                }
                item.setQuantity(item.getQuantity() + 1);
                cartTable.refresh();
                updateCartSummary();
                return;
            }
        }

        CartItem newItem = new CartItem(
                product.getName(),
                product.getPrice(),
                1,
                product.getPrice()
        );
        cartItems.add(newItem);
        updateCartSummary();
    }

    private void removeFromCart(CartItem item) {
        cartItems.remove(item);
        updateCartSummary();
    }

    private void updateCartSummary() {
        double subtotal = cartItems.stream()
                .mapToDouble(CartItem::getTotal)
                .sum();

        double discount = 0.0;
        double tax = subtotal * 0.08;
        double total = subtotal - discount + tax;

        subtotalLabel.setText(String.format("LKR %,.2f", subtotal));
        discountLabel.setText(String.format("LKR %,.2f", discount));
        taxLabel.setText(String.format("LKR %,.2f", tax));
        totalLabel.setText(String.format("LKR %,.2f", total));
    }

    @FXML
    private void clearCart() {
        cartItems.clear();
        updateCartSummary();
    }

    @FXML
    private void processPayment() {
        if (cartItems.isEmpty()) {
            showAlert("Empty Cart", "Please add items to the cart before processing payment.");
            return;
        }

        if (paymentMethodComboBox.getValue() == null) {
            showAlert("Payment Method Required", "Please select a payment method.");
            return;
        }

        String customerName = customerNameField.getText().isEmpty() ? "Walk-in Customer" : customerNameField.getText();
        String paymentMethod = paymentMethodComboBox.getValue();
        double totalAmount = Double.parseDouble(totalLabel.getText().replace("LKR ", "").replace(",", ""));

        showAlert("Payment Successful",
                String.format("Payment processed successfully!\n\nCustomer: %s\nPayment Method: %s\nTotal: LKR %,.2f",
                        customerName, paymentMethod, totalAmount));

        clearCart();
        customerNameField.clear();
        paymentMethodComboBox.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Product class
    public static class Product {
        private final String name;
        private final double price;
        private final int stock;
        private final String color;
        private final String code;
        private final String category;

        public Product(String name, double price, int stock, String color, String code, String category) {
            this.name = name;
            this.price = price;
            this.stock = stock;
            this.color = color;
            this.code = code;
            this.category = category;
        }

        public String getName() { return name; }
        public double getPrice() { return price; }
        public int getStock() { return stock; }
        public String getColor() { return color; }
        public String getCode() { return code; }
        public String getCategory() { return category; }
    }

    // CartItem class
    public static class CartItem {
        private final String productName;
        private final double price;
        private int quantity;
        private double total;

        public CartItem(String productName, double price, int quantity, double total) {
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.total = total;
        }

        public String getProductName() { return productName; }
        public double getPrice() { return price; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return total; }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
            this.total = this.price * quantity;
        }
    }
}