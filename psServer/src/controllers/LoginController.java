package controllers;

import commands.Commands;
import topic.SendTopic;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

public class LoginController {
	@FXML private Text actiontarget;
	@FXML private TextField dbServer;
	@FXML private TextField dbName;
	@FXML private TextField user;
	@FXML private PasswordField password;
	@FXML private Button btnSignin;
	
    @FXML protected void login(ActionEvent event) {	
    	new Thread(new Runnable() {
            @Override public void run() {
                Platform.runLater(new Runnable() {
                    @Override public void run() {
                    	dbServer.setDisable(true);
                    	dbName.setDisable(true);
                    	user.setDisable(true);
                    	password.setDisable(true);
                    	btnSignin.setDisable(true);
                    }
                });
            }
        }, "disableElements").start();

        Commands commands = new Commands();
        
        final Task<Void> taskResult = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				commands.run();
				return null;
			}       	
        };
        new Thread(taskResult, "taskResult").start();

        final Task<Void> taskSendTopic = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				new SendTopic(dbServer.getText(), dbName.getText(), user.getText(), password.getText());
				return null;
			}       	
        };
        new Thread(taskSendTopic, "taskSendTopic").start();
        
        actiontarget.setText("Sending...");
    }

	public TextField getDbServer() {
		return dbServer;
	}

	public TextField getDbName() {
		return dbName;
	}

	public TextField getUser() {
		return user;
	}

	public PasswordField getPassword() {
		return password;
	}

	public Button getBtnSignin() {
		return btnSignin;
	}
}
