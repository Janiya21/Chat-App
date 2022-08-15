package controllers;

import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginFormController {
    public JFXTextField txtUsername;
    public static String userName = "";

    public void loginOnAction(ActionEvent actionEvent) throws IOException {

        Stage stage = (Stage) txtUsername.getScene().getWindow();
        stage.close();

        userName = txtUsername.getText();

        Parent root = FXMLLoader.load(this.getClass().getResource("../View/ClientForm.fxml"));
        Scene scene = new Scene(root);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Login Form");
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
}
