package controllers.config;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import pr.model.LinkedValue;
import pr.model.Tconftree;
import pr.model.Tsignal;
import single.SingleFromDB;
import controllers.interfaces.AController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.Stage;

public class SignalTreeController extends AController implements Initializable {
	@FXML TreeView<LinkedValue> tvSignals;
	@FXML TreeItem<LinkedValue> trSignals;
	@FXML Button btnOK;
	@FXML Button btnCancel;
	
	private TextField tField;
	private Map<Integer, TreeItem<LinkedValue>> treeLeaves = new HashMap<>();
	
	@FXML protected void btnOK() {
		TreeItem<LinkedValue> it = tvSignals.getSelectionModel().getSelectedItem();
		Tsignal tSignal = (Tsignal) ((LinkedValue)it.getValue()).getDt();
		if (tSignal != null) {
			tField.setText(tSignal.getIdsignal() + "");
		}
		btnCancel();
	}
	
	@FXML protected void btnCancel() {
		((Stage)tvSignals.getScene().getWindow()).hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
		addNodes(trSignals);
		tvSignals.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) btnOK();
		});
	}
	
	int idParent = 0;
	private void addNodes(TreeItem<LinkedValue> parent) {
		LinkedValue val = (LinkedValue) parent.getValue();		
		if (val != null) {
			idParent = val.getId();
		} else {
			idParent = 0;
		}
		List<Tconftree> nodes = SingleFromDB.tConftree.values().stream().
				filter(f -> f.getParentref() == idParent).collect(Collectors.toList());
		if (nodes.size() > 0) {
			nodes.forEach(n -> {
				TreeItem<LinkedValue> ti = new TreeItem<LinkedValue>(new LinkedValue(n.getIdnode(), n, null));
				parent.getChildren().add(ti);
				addNodes(ti);
			});
		} else {
			List<Tsignal> leafs = SingleFromDB.tsignals.values().stream()
					.filter(f -> f.getNoderef() == ((Tconftree)parent.getValue().getVal()).getIdnode())
					.collect(Collectors.toList());
			leafs.forEach(l -> {
				TreeItem<LinkedValue> ti = new TreeItem<LinkedValue>(new LinkedValue(l.getIdsignal(), l.getNamesignal(), l));
				parent.getChildren().add(ti);
				treeLeaves.put(l.getIdsignal(), ti);
			});
		}
	}
	
	public void selectNode(int idSignal) {
		tvSignals.getSelectionModel().select(treeLeaves.get(idSignal));
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		if (tvSignals.getScene() != null) {
			((Stage)tvSignals.getScene().getWindow()).setTitle(rb.getString("keySignalTreeTitle"));
		}
		btnOK.setText(rb.getString("keyApply"));
		btnCancel.setText(rb.getString("keyCancel"));
	}

	public void setTextField(TextField tField) {
		this.tField = tField;
	}
}
