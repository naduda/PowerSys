package controllers;

import ui.Main;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;

public class ToolBarController {

	@FXML private Label lLastDate;
	private long tsLastDate;
	
	@FXML 
	protected void testAction(ActionEvent event) {
		System.out.println("test ToolBarController " + lLastDate.hashCode());
	}
	
	@FXML 
	protected void exitButtonAction(ActionEvent event) {
		Controller.exitProgram();
	}

	public void updateLabel(String text) {
		lLastDate.setText(text);
	}
	
	@FXML 
	protected void fitVertical(ActionEvent event) {
		fitSchemeVertical();
	}
	
	public void fitSchemeVertical() {
		Group root = Main.mainScheme.getRoot();
		double k = MainStage.bpScheme.getHeight() * 0.95 / root.getBoundsInLocal().getHeight();
		root.setScaleY(k);
		root.setScaleX(k);
	}
	
	@FXML 
	protected void fitHorizontal(ActionEvent event) {
		fitSchemeHorizontal();
	}
	
	public void fitSchemeHorizontal() {
		Group root = Main.mainScheme.getRoot();
		double k = MainStage.bpScheme.getWidth() * 0.95 / root.getBoundsInLocal().getWidth();
		root.setScaleY(k);
		root.setScaleX(k);
	}

	public long getTsLastDate() {
		return tsLastDate;
	}

	public void setTsLastDate(long tsLastDate) {
		this.tsLastDate = tsLastDate;
	}

}
