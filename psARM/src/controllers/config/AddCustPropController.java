package controllers.config;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import pr.svgObjects.CP;
import pr.svgObjects.CustProps;
import single.SingleObject;
import svg2fx.Convert;
import controllers.interfaces.AController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddCustPropController extends AController implements Initializable {
	@FXML private TextField txtName;
	@FXML private TextField txtValue;
	@FXML private Label lblName;
	@FXML private Label lblValue;
	@FXML private Button btnOK;
	@FXML private Button btnCancel;
	
	@FXML protected void btnOK() {
		if (txtName.getText().length() == 0 && txtValue.getText().length() == 0) {
			
		} else {
			CustProps cProps = SingleObject.selectedShape.getSvgGroup().getCustProps();
			CP newCP = new CP();
			newCP.setLangID("2057");
			newCP.setType(0);
			newCP.setLbl(txtName.getText());
			newCP.setNameU(txtName.getText());
			newCP.setVal(txtValue.getText());
			
			if (cProps == null) {
				cProps = new CustProps();
				cProps.setCustomProps(new ArrayList<CP>());
				SingleObject.selectedShape.getSvgGroup().setCustProps(cProps);
			}
			cProps.getCustomProps().add(newCP);
			Convert.svgModel.setObject(Convert.svg.getFileName(), Convert.svg);
		}
		btnCancel();
	}
	
	@FXML protected void btnCancel() {
		((Stage)txtName.getScene().getWindow()).hide();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		init();
	}

	@Override
	public void setElementText(ResourceBundle rb) {
		if (txtName.getScene() != null) {
			((Stage)txtName.getScene().getWindow()).setTitle(rb.getString("keyNewPropertyTitle"));
		}
		txtName.setText(rb.getString("keyName"));
		txtValue.setText(rb.getString("keyValue"));
		lblName.setText(rb.getString("keyName"));
		lblValue.setText(rb.getString("keyValue"));
		btnOK.setText(rb.getString("keyApply"));
		btnCancel.setText(rb.getString("keyCancel"));
	}
}
