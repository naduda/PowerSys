package controllers;import java.net.URL;import java.util.Locale;import java.util.ResourceBundle;import controllers.interfaces.IControllerInit;import javafx.collections.ObservableList;import javafx.fxml.FXML;import javafx.fxml.Initializable;import javafx.scene.control.Button;import javafx.scene.control.CheckBox;import javafx.scene.control.Label;import javafx.scene.control.Menu;import javafx.scene.control.MenuItem;import javafx.scene.control.TextField;import javafx.scene.control.ToolBar;import javafx.scene.input.KeyCode;import javafx.scene.input.KeyCodeCombination;import javafx.scene.input.KeyCombination;import javafx.scene.layout.GridPane;import javafx.scene.text.Text;import javafx.stage.Stage;import single.SingleObject;import state.HotKeyClass;import ui.MainStage;public class HotKeysController implements IControllerInit, Initializable {	@FXML GridPane gridMenuBar;	@FXML GridPane gridToolBar;	@FXML private Button btnOK;	@FXML private Button btnCancel;		private ToolBar toolBar;		@Override	public void initialize(URL url, ResourceBundle bundle) {		MenuBarController mbController = MainStage.controller.getMenuBarController();		ObservableList<Menu> menus = mbController.getMenus();		menus.forEach(this::setMenuBarItems);				ToolBarController tbController = MainStage.controller.getToolBarController();		toolBar = tbController.getTbMain();		setToolBarItems(tbController.getTbMain());				setElementText(Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName())));	}		@Override	public void setElementText(ResourceBundle rb) {		btnOK.setText(rb.getString("key_miLogin"));		btnCancel.setText(rb.getString("keyCancel"));	}	@FXML private void btnOK() {		SingleObject.hotkeys.clear();		SingleObject.mainStage.getScene().getAccelerators().clear();		setSingleHotKeys(gridMenuBar, true);		setSingleHotKeys(gridToolBar, false);		btnCancel();	}		private void setSingleHotKeys(GridPane grid, boolean isMenuBar) {		grid.getChildren().filtered(f -> f instanceof TextField).forEach(e -> {			TextField tf = (TextField) grid.lookup("#" + e.getId());			String id = e.getId().substring(3);			CheckBox chbC = (CheckBox) grid.lookup("#chbC_" + id);			CheckBox chbA = (CheckBox) grid.lookup("#chbA_" + id);			CheckBox chbS = (CheckBox) grid.lookup("#chbS_" + id);						if (tf.getText().length() > 0) {				KeyCodeCombination kk = new KeyCodeCombination(KeyCode.getKeyCode(tf.getText().toUpperCase()), 						chbS.isSelected() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,						chbC.isSelected() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,						chbA.isSelected() ? KeyCombination.ModifierValue.DOWN : KeyCombination.ModifierValue.UP,						KeyCombination.ModifierValue.UP, KeyCombination.ModifierValue.UP);							if (isMenuBar) {					MenuItem mi = SingleObject.getMenuItemById(null, id);					mi.setAccelerator(kk);					SingleObject.mainStage.getScene().getAccelerators().put(kk, () -> mi.fire());									} else {					Button btn = (Button) toolBar.getItems().filtered(f -> id.equals(f.getId())).get(0);	        		SingleObject.mainStage.getScene().getAccelerators().put(kk, () -> btn.fire());				}			}			SingleObject.hotkeys.put(id, new HotKeyClass(id, chbA.isSelected(), chbC.isSelected(), chbS.isSelected(), tf.getText().toUpperCase()));		});	}		private int k = 0;	private void setMenuBarItems(Menu menu) {		menu.getItems().forEach(mi -> {			if (mi instanceof Menu) {				setMenuBarItems((Menu) mi);			} else if (mi instanceof MenuItem && mi.getText() != null) {				String id = mi.getId();				HotKeyClass oldValue = SingleObject.hotkeys.get(id);				addItemToGrid(gridMenuBar, mi.getText(), id, oldValue, k++);			}		});	}		private void setToolBarItems(ToolBar tb) {		k = 0;		tb.getItems().filtered(f -> f instanceof Button).forEach(b -> {			String id = b.getId();			HotKeyClass oldValue = SingleObject.hotkeys.get(id);			addItemToGrid(gridToolBar, id, id, oldValue, k++);		});	}		private void addItemToGrid(GridPane grid, String text, String id, HotKeyClass oldValue, int k) {		grid.add(new Label(text), 0, k);				CheckBox chbC = new CheckBox("CTRL");		chbC.setId("chbC_" + id);		if (oldValue != null) chbC.setSelected(oldValue.isCtrl());		grid.add(chbC, 1, k);				CheckBox chbA = new CheckBox("ALT");		chbA.setId("chbA_" + id);		if (oldValue != null) chbA.setSelected(oldValue.isAlt());		grid.add(chbA, 2, k);				CheckBox chbS = new CheckBox("SHIFT");		chbS.setId("chbS_" + id);		if (oldValue != null) chbS.setSelected(oldValue.isShift());		grid.add(chbS, 3, k);				grid.add(new Text("Code"), 4, k);				LimitedTextField tf = new LimitedTextField(1);		tf.setPrefWidth(25);		tf.setId("tf_" + id);				if (oldValue != null) tf.setText(oldValue.getCode().toUpperCase());		grid.add(tf, 5, k);	}		@FXML private void btnCancel() {		((Stage)gridMenuBar.getScene().getWindow()).close();	}		private class LimitedTextField extends TextField {	    private final int limit;	    public LimitedTextField(int limit) {	        this.limit = limit;	    }	    @Override	    public void replaceText(int start, int end, String text) {	        super.replaceText(start, end, text);	        verify();	    }	    @Override	    public void replaceSelection(String text) {	        super.replaceSelection(text);	        verify();	    }	    private void verify() {	    	String text = getText();	        if (getText().length() > limit) {	        	text = getText().substring(0, limit);	        }	        setText(text.toUpperCase());	    }	};}