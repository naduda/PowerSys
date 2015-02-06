package pr.javafx;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class Controller implements Initializable {
	@FXML SidePane sp;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		sp.setContent(sp.getControlButton("collapse", "show"));
	}
	
	@FXML protected void showHide() {
		System.out.println("showHide");
		if (sp.getSideBar().isVisible()) {
			sp.hideSide();
		} else {
			sp.showSide();
		}
	}
}
