package controllers;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import controllers.interfaces.IControllerInit;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.TtranspHistory;
import pr.model.Ttransparant;
import single.SingleFromDB;
import single.SingleObject;
import svg2fx.fxObjects.EShape;
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
	@FXML private Menu mOperationMode;
	@FXML private MenuItem miAddTransparant;
	@FXML private MenuItem miEdit;
	@FXML private MenuItem miDelete;
	
	@FXML
	protected void addTransparant(ActionEvent event) {
		getTransparantStage().show();
	}
	
	private final Stage transparantStage = new Stage();
	private TransparantController controller;
	private Stage getTransparantStage() {
		if (transparantStage.getScene() == null) {
			try {
				FXMLLoader loader = new FXMLLoader(new File(Utils.getFullPath("./ui/transparant/Transparants.xml")).toURI().toURL());
				Parent root = loader.load();
				controller = loader.getController();
				
				Scene scene = new Scene(root);
				transparantStage.setScene(scene);
				transparantStage.initModality(Modality.WINDOW_MODAL);
				transparantStage.initOwner(SingleObject.mainStage);
			} catch (IOException e) {
				LogFiles.log.log(Level.SEVERE, "void getTransparantStage(...)", e);
			}
		}
		ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
		transparantStage.setTitle(rb.getString("keyFormTitle"));
		controller.setElementText(rb);
		return transparantStage;
	}
	
	@FXML
	protected void deleteTransparant(ActionEvent event) {
		Shape transp = getSelectedTransparant(event);
		int trref = getTranspID(transp);

		SingleFromDB.psClient.updateTtransparantCloseTime(trref);
		SingleFromDB.psClient.deleteTtranspLocate(trref, SingleObject.mainScheme.getIdScheme());
		
		SingleObject.mainScheme.getRoot().getChildren().remove(transp);
	}
	
	private Shape getSelectedTransparant(ActionEvent event) {
		String id = ((MenuItem)event.getSource()).getParentPopup().getId();
		return (Shape) SingleObject.mainScheme.getRoot().lookup("#" + id);
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
		TtranspHistory transpHistory = SingleFromDB.psClient.getTtranspHistory(trref);
		Stage stage = getTransparantStage();
		controller.setTxtArea(transpHistory.getTxt());
		controller.setTrref(trref);
		controller.setTransparant(transp);
		controller.setEdit(true);
		ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
		controller.setOKtext(rb.getString("keyEdit"));
		Ttransparant tTransparant = SingleFromDB.psClient.getTtransparantById(trref);
		controller.selectTransparantById(tTransparant.getTp() - 1);
		controller.disableListView();
		stage.show();
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		if (mOperationMode != null) mOperationMode.setText(rb.getString("keyOperationMode"));
		if (miAddTransparant != null) miAddTransparant.setText(rb.getString("keyAddTransparant"));
		if (miEdit != null) miEdit.setText(rb.getString("keyEdit"));
		if (miDelete != null) miDelete.setText(rb.getString("keyDeleteTransparant"));
	}

	public Menu getmOperationMode() {
		return mOperationMode;
	}
	
	public void changeMode(ActionEvent event) {
		CheckMenuItem mi = (CheckMenuItem)event.getSource();
		String id = mi.getParentMenu().getParentPopup().getId();
		id = id.substring(id.indexOf("_") + 1);
		EShape sh = (EShape) SingleObject.mainScheme.getRoot().lookup("#" + id);
		int newStatus = Integer.parseInt(mi.getId().substring(mi.getId().indexOf("_") + 1));
		if (newStatus != sh.getStatus()) {
			SingleFromDB.psClient.updateTsignalStatus(sh.gettSignalIDTS().getIdsignal(), newStatus);
			SingleFromDB.tsignals.get(sh.gettSignalIDTS().getIdsignal()).setStatus(newStatus);
			
			SingleFromDB.psClient.insertDeventLog(5, sh.gettSignalIDTS().getIdsignal(), 
					new Timestamp(System.currentTimeMillis()), newStatus, sh.getStatus(), -1);
		}
		sh.getTsignalProp().set(true);
	}
}
