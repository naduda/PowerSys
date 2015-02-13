package controllers.tree;

import java.util.ResourceBundle;

import pr.model.LinkedValue;
import pr.model.Tscheme;
import single.SingleFromDB;
import ui.MainStage;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import controllers.interfaces.IControllerInit;
import controllers.interfaces.StageLoader;

public class SchemeController implements IControllerInit {
	@FXML private MenuItem miAdd;
	@FXML private MenuItem miEdit;
	@FXML private MenuItem miDelete;
		
	private StageLoader form;
	private AddUpdateController formController;
	
	@Override
	public void setElementText(ResourceBundle rb) {
		miAdd.setText(rb.getString("keyAdd"));
		miEdit.setText(rb.getString("keyEdit"));
		miDelete.setText(rb.getString("keyDelete"));
	}
	
	@FXML protected void add() {
		System.out.println("add");
		form = getForm();
		formController.setUpdate(false);
		form.show();
	}
	
	@FXML protected void edit() {
		form = getForm();
		formController.setUpdate(true);
		form.show();
	}
	
	private StageLoader getForm() {
		if (form == null) {
			form = new StageLoader("tree/AddUpdate.xml", "", true);
			formController = (AddUpdateController) form.getController();
		}
		TreeController trController = MainStage.controller.getSpTreeController();
		TreeItem<LinkedValue> selectedNode = trController.getTvSchemes().getSelectionModel().getSelectedItem();
		formController.setSelectedNode(selectedNode);
		form.setTitle(selectedNode.getValue().getVal().toString());
		return form;
	}

	@FXML protected void delete() {
		TreeController trController = MainStage.controller.getSpTreeController();
		TreeItem<LinkedValue> selectedNode = trController.getTvSchemes().getSelectionModel().getSelectedItem();
		Tscheme nodeScheme = (Tscheme) selectedNode.getValue().getVal();
		SingleFromDB.psClient.deleteScheme(nodeScheme.getIdscheme());
		((TreeItem<LinkedValue>)selectedNode.getParent()).getChildren().remove(selectedNode);
	}
}
