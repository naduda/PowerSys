package controllers.tree;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;

import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.LinkedValue;
import pr.model.Tscheme;
import single.SingleFromDB;
import single.SingleObject;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import controllers.interfaces.IControllerInit;

public class AddUpdateController implements IControllerInit, Initializable {
	private final FileChooser fileChooser = new FileChooser();
	private boolean isUpdate;
	private TreeItem<LinkedValue> selectedNode;
	private File file;
	
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	@FXML private TextField schemeName;
	@FXML private TextField schemeDesc;
	@FXML private Label fileName;
	@FXML private Button btnFileChooser;
	@FXML private Button saveOnDisk;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		btnFileChooser.setOnAction(a -> {
			FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("SVG files (*.svg)", "*.svg");
			fileChooser.getExtensionFilters().add(extentionFilter);
			
			File userDirectory = new File(Utils.getFullPath("./schemes"));
			fileChooser.setInitialDirectory(userDirectory);
			
			file = fileChooser.showOpenDialog(new Stage());
	        if (file != null) {
	        	fileName.setText(file.getName());
	        }
		});
		
		saveOnDisk.setOnAction(a -> {
			Tscheme nodeScheme = (Tscheme) selectedNode.getValue().getVal();
			File schemeFile = new File(Utils.getFullPath("./schemes/tmp.svg"));
			if (!schemeFile.exists()) {
				try {
					schemeFile.createNewFile();
				} catch (IOException e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			
			try (FileOutputStream fos = new FileOutputStream(schemeFile)) {
				fos.write((byte[]) nodeScheme.getSchemefile());
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				fileName.setText(e.getMessage());
				return;
			}
			fileName.setText("Scheme saved as ./schemes/tmp.svg");
		});
		
		setElementText(SingleObject.getResourceBundle());
	}
	
	@Override
	public void setElementText(ResourceBundle rb) {
		btnOK.setText(rb.getString("keyApply"));
		btnCancel.setText(rb.getString("keyCancel"));
		btnFileChooser.setText(rb.getString("keyFile"));
		saveOnDisk.setText(rb.getString("keySaveOnDisk"));
	}
	
	@FXML protected void btnOK() {
		Tscheme nodeScheme = (Tscheme) selectedNode.getValue().getVal();
		if (isUpdate) {
			updateScheme(nodeScheme);
		} else {
			addScheme(nodeScheme);
		}

		btnCancel();
	}
	
	private void addScheme(Tscheme nodeScheme) {
		byte[] array = null;
		if (file != null) {
			byte[] buf = null;
			try (InputStream in = new FileInputStream(file)) {
				buf = new byte[in.available()];
				while (in.read(buf) != -1) {}
				
				try (ByteArrayInputStream bais = new ByteArrayInputStream(buf != null ? buf : new byte[0])) {
					array = new byte[bais.available()];
				    bais.read(array);
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				}
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		SingleFromDB.psClient.addScheme("SCHEME", schemeName.getText(), schemeDesc.getText(), 
				nodeScheme.getIdscheme(), array, nodeScheme.getUserid());
		
		Tscheme newScheme = new Tscheme();
		List<Integer> arr = new ArrayList<Integer>();
		SingleFromDB.schemesMap.values().forEach(i -> arr.add(i.getIdscheme()));
		newScheme.setIdscheme(arr.stream().max(Integer::compare).get() + 1);
		newScheme.setSchemedenom("SCHEME");
		newScheme.setSchemename(schemeName.getText());
		newScheme.setSchemedescr(schemeDesc.getText());
		newScheme.setParentref(nodeScheme.getIdscheme());
		newScheme.setSchemefile(array);
		newScheme.setUserid(nodeScheme.getUserid());
		TreeItem<LinkedValue> newNode = new TreeItem<LinkedValue>(new LinkedValue(newScheme.getIdscheme(), newScheme, null));
		
		selectedNode.getChildren().add(newNode);
		LogFiles.log.log(Level.INFO, "Scheme add");
	}
	
	private void updateScheme(Tscheme nodeScheme) {
		byte[] array = null;
		if (file != null) {
			byte[] buf = null;
			try (InputStream in = new FileInputStream(file)) {
				buf = new byte[in.available()];
				while (in.read(buf) != -1) {}
				
				try (ByteArrayInputStream bais = new ByteArrayInputStream(buf != null ? buf : new byte[0])) {
					array = new byte[bais.available()];
				    bais.read(array);
				} catch (Exception e) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				}
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		} else {
			array = (byte[]) nodeScheme.getSchemefile();
		}
		SingleFromDB.psClient.updateTScheme(nodeScheme.getIdscheme(), nodeScheme.getSchemedenom(), schemeName.getText(), 
				schemeDesc.getText(), nodeScheme.getParentref(), array, nodeScheme.getUserid());
		nodeScheme.setSchemefile(array);
		nodeScheme.setSchemename(schemeName.getText());
		nodeScheme.setSchemedescr(schemeDesc.getText());
		selectedNode.setValue(new LinkedValue(nodeScheme.getIdscheme(), nodeScheme, null));
		LogFiles.log.log(Level.INFO, "Scheme updated");
	}
	
	private void setFormParameters(Tscheme nodeScheme) {
		schemeName.setText(nodeScheme.getSchemename());
		schemeDesc.setText(nodeScheme.getSchemedescr());
		fileName.setText("");
	}
	
	@FXML protected void btnCancel() {
		((Stage)btnOK.getScene().getWindow()).hide();
	}

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public void setSelectedNode(TreeItem<LinkedValue> selectedNode) {
		this.selectedNode = selectedNode;
		setFormParameters((Tscheme) selectedNode.getValue().getVal());
	}
}
