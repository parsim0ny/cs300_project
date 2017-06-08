package demo;

import java.io.BufferedReader;
import java.io.PrintWriter;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginWindow {
	protected static boolean exited = false;
	
	public static boolean hasNotExited() {
		if (!exited) {
			return true;
		}
		return false;
	}
	
	public static void display(String title, PrintWriter out, BufferedReader in, boolean rejected) {
		Stage window = new Stage();
		window.initModality(Modality.APPLICATION_MODAL);
		window.setTitle(title);
		window.setMinWidth(250);
		window.setOnCloseRequest(c -> {
			exited = true;
		});
		
		Text usernameText1 = new Text(" Enter your username: ");
		Text usernameText2 = new Text(" Enter your username: ");
		Text passwordText1 = new Text(" Enter your password: ");
		Text passwordText2 = new Text(" Enter your password: ");
		Text confirmText = new Text("Re-type your password: ");
		Text status = new Text();
		Text confirmStatus = new Text();
		
		if (rejected == true) {
			status.setText("Username or password not recognized. Please try again.");
			confirmStatus.setText("Invalid Username or password. Please try again.");
		}
		
		TextField username1 = new TextField();
		username1.setPromptText("Username");
		
		TextField username2 = new TextField();
		username2.setPromptText("Username");
		
		PasswordField password1 = new PasswordField();
		password1.setPromptText("Password");
		
		PasswordField password2 = new PasswordField();
		password2.setPromptText("Password");
		
		PasswordField confirm = new PasswordField();
		confirm.setPromptText("Confirm Password");
		
		HBox Username1 = new HBox(10);
		Username1.getChildren().addAll(usernameText1, username1);
		
		HBox Username2 = new HBox(10);
		Username2.getChildren().addAll(usernameText2, username2);
		
		HBox Password1 = new HBox(10);
		Password1.getChildren().addAll(passwordText1, password1);
		
		HBox Password2 = new HBox(10);
		Password2.getChildren().addAll(passwordText2, password2);
		
		HBox Confirm = new HBox(10);
		Confirm.getChildren().addAll(confirmText, confirm);
		
		Button loginButton = new Button("Login");
		Button submitButton = new Button("Submit");
		
		Text or1 = new Text("or");
		Text or2 = new Text("or");
		
		Button registerButton = new Button("Register");
		Button backButton = new Button("Back to Login");
		
		VBox layout1 = new VBox(10);
		layout1.getChildren().addAll(status, Username1, Password1, loginButton, or1, registerButton);
		layout1.setAlignment(Pos.CENTER);
		
		VBox layout2 = new VBox(10);
		layout2.getChildren().addAll(confirmStatus, Username2, Password2, Confirm, submitButton, or2, backButton);
		layout2.setAlignment(Pos.CENTER);
		
		Scene login = new Scene(layout1);
		Scene register = new Scene(layout2);
		
		loginButton.setOnAction(e -> {
			out.println("EXISTING");
			out.println(username1.getText());
			out.println(password1.getText());
			out.flush();
			window.close();
		});
		
		registerButton.setOnAction(e1 -> {
			window.setScene(register);
		});
		
		submitButton.setOnAction(e2 -> {
			if (password2.getText().compareTo(confirm.getText()) == 0) {
				out.println("NEW");
				out.println(username2.getText());
				out.println(password2.getText());
				out.flush();
				window.close();
			}
			else {
				confirmStatus.setText("Error: Passwords do not match.");
				password2.setText("");
				confirm.setText("");
			}
		});
		
		backButton.setOnAction(e3 -> {
			window.setScene(login);
		});
		
		window.setScene(login);
		window.showAndWait();
	}
}
