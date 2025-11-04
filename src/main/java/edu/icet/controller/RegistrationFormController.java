package edu.icet.controller;

import edu.icet.db.DBConnection;
import edu.icet.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class RegistrationFormController {

    @FXML
    private Button btnSignup;

    @FXML
    private CheckBox checkAgree;

    @FXML
    private PasswordField txtConfirmPassword;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtLastName;

    @FXML
    private PasswordField txtPassword;

    @FXML
    void btnSignupOnAction(ActionEvent event) {
        String firstName = txtFirstName.getText();
        String lastName = txtLastName.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill all fields!");
            return;
        }

        if (!checkAgree.isSelected()) {
            showError("Please agree to terms and conditions!");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match!");
            return;
        }

        if (password.length() < 5) {
            showError("Password must be at least 5 characters long!");
            return;
        }

        // Register user - FIRST NAME as username
        if (registerUser(firstName, password, email)) {
            showSuccess("Registration Successful!");
            clearForm();
        } else {
            showError("Registration failed! Username may already exist.");
        }
    }

    private boolean registerUser(String username, String password, String email) {
        String sql = "INSERT INTO user (username, password, role, employee_id, email) VALUES (?, ?, 'STAFF', NULL, ?)";

        try (var connection = DBConnection.getInstance().getConnection();
             var pstm = connection.prepareStatement(sql)) {

            // Password encrypt
            String hashedPassword = PasswordUtil.hashPassword(password);

            pstm.setString(1, username);
            pstm.setString(2, hashedPassword); // Encrypted password
            pstm.setString(3, email);

            int affectedRows = pstm.executeUpdate();
            return affectedRows > 0;

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.show();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setContentText(message);
        alert.show();
    }

    private void clearForm() {
        txtFirstName.clear();
        txtLastName.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtConfirmPassword.clear();
        checkAgree.setSelected(false);
    }
}