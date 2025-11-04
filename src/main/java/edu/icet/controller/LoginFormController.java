package edu.icet.controller;

import edu.icet.db.DBConnection;
import edu.icet.util.PasswordUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    void btnLoginOnAction(ActionEvent event) {
        String username = txtUsername.getText();
        String password = txtPassword.getText();

        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password!");
            return;
        }

        // Authenticate user
        if (isValidUser(username, password)) {
            showSuccess("Login Successful!");
            // Open dashboard or main window
            openDashboard();
        } else {
            showError("Invalid username or password!");
        }
    }

    private boolean isValidUser(String username, String password) {
        String sql = "SELECT password FROM user WHERE username = ?";

        try (var connection = DBConnection.getInstance().getConnection();
             var pstm = connection.prepareStatement(sql)) {

            pstm.setString(1, username);
            var resultSet = pstm.executeQuery();

            if (resultSet.next()) {
                String storedHashedPassword = resultSet.getString("password");
                // Encrypted password check
                return PasswordUtil.checkPassword(password, storedHashedPassword);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Database error: " + e.getMessage());
        }

        return false;
    }

    private void openDashboard() {
        try {
            // Open Dashboard
            Parent root = FXMLLoader.load(getClass().getResource("/view/Dashboard.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

            // Current login window close
            ((Stage) btnLogin.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
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

    public void linkForgotPasswordOnAction(ActionEvent actionEvent) {
        try {
            // Forgot Password window open
            Parent root = FXMLLoader.load(getClass().getResource("/view/ForgotPassword.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Reset Password");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot open password reset page!");
        }
    }

    public void linkSignupOnAction(ActionEvent actionEvent) {
        try {
            // Registration window open
            Parent root = FXMLLoader.load(getClass().getResource("/view/Registration.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Create New Account");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Cannot open registration page!");
        }
    }
}