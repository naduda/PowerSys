package controllers;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.ChatMessage;
import single.ProgramProperty;
import single.SingleFromDB;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import controllers.interfaces.AController;

public class ChatController extends AController implements Initializable {
	@FXML private TextArea txtArea;
	@FXML private TextArea txtMessage;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	
	private final ChatMessage chMessage = new ChatMessage();
	private static final StringProperty chatMessageProperty = new SimpleStringProperty();
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		txtArea.setWrapText(true);
		txtArea.setEditable(false);
		txtMessage.setWrapText(true);
		
		chatMessageProperty.bind(ProgramProperty.chatMessageProperty);
		chatMessageProperty.addListener((observ, oldValue, newValue) -> 
			txtArea.appendText(newValue + "\n"));
		
		try {
			java.net.InetAddress localMachine = java.net.InetAddress.getLocalHost();
			chMessage.setAddress(localMachine.getHostName() + " (" + 
					localMachine.getHostAddress() + ")");
		} catch (UnknownHostException e) {
			LogFiles.log.log(Level.SEVERE, "ChatController ...", e);
		}
		
		txtMessage.addEventHandler(KeyEvent.KEY_PRESSED, t -> {
			if (t.getCode()==KeyCode.ENTER && t.isControlDown()) {
				btnOK();
			}
		});
		Platform.runLater(() -> txtMessage.requestFocus());
	}
	
	@FXML protected void btnOK() {
		chMessage.setTextValue(txtMessage.getText());
		SingleFromDB.psClient.sendChatMessage(chMessage);
		txtMessage.clear();
	}
	
	@FXML protected void btnCancel() {
		((Stage)txtArea.getScene().getWindow()).hide();
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		if (txtArea.getScene() != null) {
			((Stage)txtArea.getScene().getWindow()).setTitle(rb.getString("keyChatTitle"));
		}
		btnOK.setText(rb.getString("keySend"));
		btnCancel.setText(rb.getString("keyCancel"));
	}
}
