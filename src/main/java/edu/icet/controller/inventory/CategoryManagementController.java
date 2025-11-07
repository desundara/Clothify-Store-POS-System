package edu.icet.controller.inventory;

import edu.icet.dao.CategoryDAO;
import edu.icet.model.CategoryDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

public class CategoryManagementController implements Initializable {

    @FXML private TableView<CategoryDTO> categoriesTable;
    @FXML private TableColumn<CategoryDTO, String> colCategoryName;
    @FXML private TableColumn<CategoryDTO, String> colDescription;

    @FXML private TextField searchField;
    @FXML private TextField txtCategoryName;
    @FXML private TextArea txtDescription;
    @FXML private Label lblEmptyTable;

    private ObservableList<CategoryDTO> categoriesData = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupTableListener();
        loadCategoriesFromDatabase();
        System.out.println("Category Management loaded successfully!");
    }

    private void setupTableColumns() {
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        categoriesTable.setItems(categoriesData);
    }

    private void setupTableListener() {
        // Show/hide empty table message
        categoriesTable.itemsProperty().addListener((observable, oldValue, newValue) -> {
            lblEmptyTable.setVisible(newValue.isEmpty());
        });
    }

    private void loadCategoriesFromDatabase() {
        try {
            java.util.List<CategoryDTO> categories = CategoryDAO.getAllCategories();
            categoriesData.clear();
            categoriesData.addAll(categories);

            lblEmptyTable.setVisible(categoriesData.isEmpty());
            System.out.println("📦 Loaded " + categories.size() + " categories from database");

        } catch (Exception e) {
            System.err.println("❌ Error loading categories: " + e.getMessage());
            showAlert("Error", "Failed to load categories from database!", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void btnSearchOnAction(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            categoriesTable.setItems(categoriesData);
        } else {
            ObservableList<CategoryDTO> filteredList = FXCollections.observableArrayList();
            for (CategoryDTO category : categoriesData) {
                if (category.getName().toLowerCase().contains(searchText) ||
                        category.getDescription().toLowerCase().contains(searchText)) {
                    filteredList.add(category);
                }
            }
            categoriesTable.setItems(filteredList);
        }

        // Update empty table message
        lblEmptyTable.setVisible(categoriesTable.getItems().isEmpty());
    }

    @FXML
    private void btnSaveCategoryOnAction(ActionEvent event) {
        if (validateForm()) {
            String name = txtCategoryName.getText().trim();
            String description = txtDescription.getText().trim();

            // Create new category
            CategoryDTO newCategory = new CategoryDTO(name, description);

            // Save to DATABASE
            boolean success = CategoryDAO.addCategory(newCategory);

            if (success) {
                showAlert("Success", "Category '" + name + "' saved successfully to database! ✅", Alert.AlertType.INFORMATION);
                clearForm();
                loadCategoriesFromDatabase(); // Reload from database
            } else {
                showAlert("Error", "Failed to save category to database! ❌", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void btnCancelOnAction(ActionEvent event) {
        clearForm();
    }

    private boolean validateForm() {
        if (txtCategoryName.getText().trim().isEmpty()) {
            showAlert("Error", "Please enter a category name!", Alert.AlertType.ERROR);
            txtCategoryName.requestFocus();
            return false;
        }

        // Check if category name already exists in DATABASE
        String categoryName = txtCategoryName.getText().trim();
        if (CategoryDAO.isCategoryNameExists(categoryName)) {
            showAlert("Error", "Category name '" + categoryName + "' already exists in database!", Alert.AlertType.ERROR);
            txtCategoryName.requestFocus();
            return false;
        }

        return true;
    }

    private void clearForm() {
        txtCategoryName.clear();
        txtDescription.clear();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}