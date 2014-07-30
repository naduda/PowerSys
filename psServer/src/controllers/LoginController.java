package controllers;

import commands.Commands;

import topic.SendTopic;
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
    	SendTopic st = new SendTopic();
    	st.setConnectionParams(dbServer.getText(), dbName.getText(), user.getText(), password.getText());
    	
    	final Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				st.run(dbServer, dbName, user, password, btnSignin);
				return null;
			}
        	
        };
        new Thread(task, "SendMainTopic").start();
        
        Commands commands = new Commands();
        
        final Task<Void> taskResult = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				commands.run();
				return null;
			}       	
        };
        new Thread(taskResult, "taskResult").start();

        actiontarget.setText("Sending...");
    }
}
