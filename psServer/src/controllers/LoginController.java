package controllers;

import commands.CommandResult;
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

        

        //final task<void> tasksendtopic = new task<void>() {
		//	@override
		//	protected void call() throws exception {
		//		new sendtopic(dbserver.gettext(), dbname.gettext(), user.gettext(), password.gettext());
		//		return null;
		//	}       	
        //};
        SendTopic sTopic = new SendTopic(dbServer.getText(), dbName.getText(), user.getText(), password.getText());
        new Thread(sTopic, "taskSendTopic").start();
        
        CommandResult cResult = new CommandResult(sTopic);
        new Thread(cResult, "taskResult").start();
        
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
