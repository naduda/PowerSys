package controllers;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.interfaces.IControllerInit;
import pr.common.Utils;
import pr.model.TtranspHistory;
import pr.model.Ttransparant;
import svg2fx.fxObjects.EShape;
import ui.Main;
import ui.MainStage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Shape;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ShapeController implements IControllerInit {

	@FXML Menu mOperationMode;
	@FXML MenuItem miAddTransparant;
	@FXML MenuItem miEdit;
	@FXML MenuItem miDelete;
	
	@FXML
	protected void add(ActionEvent event) {
		System.out.println("test ShapeController add");
	}
	
	@FXML
	protected void addTransparant(ActionEvent event) {
		getTransparantStage("Add transparant").show();
	}
	
	private final Stage transparantStage = new Stage();
	private TransparantController controller;
	private Stage getTransparantStage(String title) {
		if (transparantStage.getScene() == null) {
			try {
				FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/Transparants.xml")));
				Parent root = loader.load();
				controller = loader.getController();
				
				Scene scene = new Scene(root);
				transparantStage.setScene(scene);
				transparantStage.initModality(Modality.WINDOW_MODAL);
				transparantStage.initOwner(Main.mainStage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
		transparantStage.setTitle(rb.getString("keyFormTitle"));
		controller.setElementText(rb);
		return transparantStage;
	}
	
	@FXML
	protected void deleteTransparant(ActionEvent event) {
		Shape transp = getSelectedTransparant(event);
		int trref = getTranspID(transp);

		try {
			MainStage.psClient.updateTtransparantCloseTime(trref);
			MainStage.psClient.deleteTtranspLocate(trref, Main.mainScheme.getIdScheme());
			
			Main.mainScheme.getRoot().getChildren().remove(transp);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	private Shape getSelectedTransparant(ActionEvent event) {
		String id = ((MenuItem)event.getSource()).getParentPopup().getId();
		return (Shape) Main.mainScheme.getRoot().lookup("#" + id);
	}
	
	private int getTranspID(Shape t) {
		String id = t.getId();
		id = id.substring(id.indexOf("_") + 1);
		return Integer.parseInt(id);
	}
	
	@FXML
	protected void editTransparant(ActionEvent event) {
		Shape transp = getSelectedTransparant(event);		
		int trref = getTranspID(transp);
		try {
			TtranspHistory transpHistory = MainStage.psClient.getTtranspHistory(trref);
			Stage stage = getTransparantStage("Edit transparant");
			controller.setTxtArea(transpHistory.getTxt());
			controller.setTrref(trref);
			controller.setTransparant(transp);
			controller.setEdit(true);
			ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
			controller.setOKtext(rb.getString("keyEdit"));
			Ttransparant tTransparant = MainStage.psClient.getTtransparantById(trref);
			controller.selectTransparantById(tTransparant.getTp() - 1);
			controller.disableListView();
			stage.show();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (mOperationMode != null) mOperationMode.setText(rb.getString("keyOperationMode"));
		if (miAddTransparant != null) miAddTransparant.setText(rb.getString("keyAddTransparant"));
		if (miEdit != null) miEdit.setText(rb.getString("keyEdit"));
		if (miDelete != null) miDelete.setText(rb.getString("keyDelete"));
	}

	public Menu getmOperationMode() {
		return mOperationMode;
	}
	
	public void changeMode(ActionEvent event) {
		CheckMenuItem mi = (CheckMenuItem)event.getSource();
		String id = mi.getParentMenu().getParentPopup().getId();
		id = id.substring(id.indexOf("_") + 1);
		EShape sh = (EShape) Main.mainScheme.getRoot().lookup("#" + id);
		int newStatus = Integer.parseInt(mi.getId().substring(mi.getId().indexOf("_") + 1));
		if (newStatus != sh.getStatus()) {
			try {
				MainStage.psClient.updateTsignalStatus(sh.gettSignalIDTS().getIdsignal(), newStatus);
				MainStage.tsignals.get(sh.gettSignalIDTS().getIdsignal()).setStatus(newStatus);
				
				MainStage.psClient.insertDeventLog(5, sh.gettSignalIDTS().getIdsignal(), 
						new Timestamp(System.currentTimeMillis()), newStatus, sh.getStatus(), -1);		
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		sh.getTsignalProp().set(true);
	}
}
