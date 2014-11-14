package svg2fx.fxObjects;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptException;

import controllers.Controller;
import controllers.ShapeController;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.TSysParam;
import pr.model.Tsignal;
import single.SingleFromDB;
import single.SingleObject;
import svg2fx.Convert;
import svg2fx.SignalState;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class EShape extends AShape {
	private static final String TEXT_CONST = "text";
	
	private SignalState value = new SignalState();

	private Map<String, Integer> signals = new HashMap<>();
	private int id = -1;
	private int idTS = -1;
	private int status = 1;
	private int rcode;
	private Timestamp dt;
	private String scriptName;
	private boolean textShape;
	private ScriptClass scripts;
	private String scriptPath;
	private Tsignal tSignalID;
	private Tsignal tSignalIDTS;
	
	private ContextMenu contextMenu;
	
	private HashMap<String, String> custProps;
	@SuppressWarnings("unchecked")
	public EShape(Group g) {
		super(g);
		
		if (g.getUserData() != null) {
			custProps = (HashMap<String, String>) g.getUserData();
			String v = custProps.get("id");
			id = v != null ? Integer.parseInt(v) : 0;
			v = custProps.get("idTS");
			idTS = v != null ? Integer.parseInt(v) : 0;
			v = custProps.get("script");
			scriptName = v != null ? v : null;
			v = custProps.get("signals");
			if (v != null) {
				StringTokenizer st = new StringTokenizer(v, "|");
				while (st.hasMoreElements()) {
					String[] sign = ((String) st.nextElement()).split(":");
					signals.put(sign[0], Integer.parseInt(sign[1]));
				}
			}
			
			setTextShape(TEXT_CONST.equals(g.getId()) && custProps.get("Value") != null);
			if (isTextShape() && id > -1 ) {
				setValue(0.0, "id");
			}
		}
	    
	    rect.setOnMouseClicked(t -> {
			if(t.getButton().toString().equals("SECONDARY")) {
				setContextMenu();
				contextMenu.show(rect, t.getScreenX(), t.getSceneY());
			}
	    });
	    
	    setOnDragDetected(e -> {
	    	if (id > 0) {
		    	Dragboard db = startDragAndDrop(TransferMode.ANY);
		    	ClipboardContent content = new ClipboardContent();
	            content.putString(id + "");
	            db.setContent(content);
	            
	            e.consume();
	    	}
	    });
	}
	
	@Override
	public void onDoubleClick() {
		runScriptByName("onDoubleClick");
	}
	
	@Override
	public void onValueChange(Double newValue) {
		runScriptByName("onValueChange");
	}
	
	@Override
	public void onSignalUpdate() {
		runScriptByName("onSignalUpdate");
		getTsignalProp().set(false);
	}
	
	private void runScriptByName(String scriptName) {
		try {
			if (TEXT_CONST.equals(getId())) return;
			String script = getScripts().getScriptByName(scriptName).replace(scriptName, scriptName + "_" + getId());
				
			if (script != null) {
				Platform.runLater(() -> {
					try {
						double start = System.currentTimeMillis();
						Convert.engine.eval(script);
						Invocable inv = (Invocable) Convert.engine;
			            inv.invokeFunction(scriptName + "_" + getId(), this);
			            
			            double execTime = System.currentTimeMillis() - start; 
			            if (execTime > 25) LogFiles.log.log(Level.WARNING, String.format("script execute time: %s ms", execTime));
					} catch (ScriptException | NoSuchMethodException e) {
						LogFiles.log.log(Level.SEVERE, "void runScriptByName(...)", e);
						LogFiles.log.log(Level.INFO, scriptName);
					}
				});
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "void runScriptByName(...)", e);
		}
	}
	
	private void setContextMenu() {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/ShapeContextMenu.xml")));
			contextMenu = loader.load();
			ShapeController shapeController = loader.getController();
			contextMenu.setId(contextMenu.getId() + "_" + getId());
			 
			if (idTS < 1 && id < 1) {
				shapeController.getmOperationMode().setDisable(true);
			} else {
				Map<String, TSysParam> modes = SingleFromDB.psClient.getTSysParam("SIGNAL_STATUS");
				modes.values().forEach(m -> {
					CheckMenuItem mi = new CheckMenuItem(m.getParamdescr());
					mi.setId("miMode_" + m.getVal());
					mi.setSelected(Integer.parseInt(m.getVal()) == getStatus());
					mi.setOnAction(shapeController::changeMode);
					shapeController.getmOperationMode().getItems().add(mi);
				});
			}
			contextMenu.setOnShowing(e -> {
				ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
				shapeController.setElementText(rb);
			});
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, "void setContextMenu()", e);
		}
	}
	
	public void setTS(int idSignal, int val) {
		try {
			SingleFromDB.psClient.setTS(idSignal, val, SingleObject.mainScheme.getIdScheme());
		} catch (RemoteException | NumberFormatException e) {
			LogFiles.log.log(Level.SEVERE, "void setTS(...)", e);
		}
	}

	public SignalState getValue() {
		return value;
	}

	public void setValue(double val, String typeSignal) {
		if (typeSignal.toLowerCase().equals("id")) {
			value.setIdValue(val);
		} else if (typeSignal.toLowerCase().equals("idts")) {
			value.setIdTSValue(val);
		}
		
		if (isTextShape()) {
			String precision = custProps.get("Precision");
			String format = precision != null ? precision : "0.0";
			setTextValue(this, val, format);
		}
		
		setLastDataDate(new Date(System.currentTimeMillis()));
		getValueProp().set(val); //Listener
	}

	private void setTextValue(Node n, double val, final String format) {
		if (n instanceof Group) {
			Group gr = (Group)n;
			gr.getChildren().forEach(ch -> setTextValue(ch, val, format) );
		} else {
			if (n instanceof Text) {
				Text t = (Text) n;
				
				try {
					if (t.getText().length() < 5) {
						t.setText("");
						return;
					}
									
					DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
					decimalFormatSymbols.setDecimalSeparator(',');
					decimalFormatSymbols.setGroupingSeparator(' ');
					
					DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
					String textValue = decimalFormat.format(val) + "  " + SingleFromDB.signals.get(id).getNameunit().trim();
					
					t.setText(textValue);
					
					if (t.getParent().getParent().getParent().getParent().getId().toLowerCase().startsWith("digitaldevice") && 
							((Group)t.getParent()).getChildren().size() > 1) {
						Group p = (Group)t.getParent();

						Platform.runLater(() -> p.getChildren().remove(p.getChildren().get(1)));
						
						Node nn = ((Group)((Group)t.getParent().getParent().getParent().getParent()).getChildren().get(0)).getChildren().get(1);
						Double textVal = nn.getBoundsInLocal().getWidth();
						t.setWrappingWidth(textVal);
						t.setTranslateX(-nn.getBoundsInLocal().getHeight());
					}
					
					t.setTextAlignment(TextAlignment.RIGHT);
				} catch (Exception e) {
					//LogFiles.log.log(Level.WARNING, "void setTextValue(...)", e);
				}
			}
		}
	}

	public int getIdSignal() {
		return id;
	}

	public int getIdTS() {
		return idTS;
	}

	public boolean isTextShape() {
		return textShape;
	}

	public void setTextShape(boolean textShape) {
		this.textShape = textShape;
	}

	public int getRcode() {
		return rcode;
	}

	public void setRcode(int rcode) {
		this.rcode = rcode;
	}

	public Timestamp getDt() {
		return dt;
	}

	public void setDt(Timestamp dt) {
		this.dt = dt;
	}

	public ScriptClass getScripts() {
		if (scripts == null) {
			String scriptPath = getScriptPath();
			if (scriptPath != null) {
				scripts = new ScriptClass(getScriptPath());
			}
		}
		return scripts;
	}

	public String getScriptPath() {
		if (scriptName != null) {
			scriptPath = Utils.getFullPath("./scripts/" + scriptName + ".js");
		} else if (custProps != null) {
			String sName = getId().replace("_", ".");
			sName = sName.contains(".") ? sName.substring(0, sName.indexOf(".")) : sName;
			if (TEXT_CONST.equals(sName)) {
				return null;
			}
			scriptPath = Utils.getFullPath("./scripts/" + sName + ".js");
		}
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}
	
	private int getStateVal(int idSignal, String denom) {
		try {
			Tsignal tSignal = SingleFromDB.tsignals.get(idSignal);
			return SingleFromDB.psClient.getSpTuCommand().stream()
				.filter(f -> f.getObjref() == tSignal.getStateref() && f.getDenom().equals(denom.toUpperCase()))
				.collect(Collectors.toList()).get(0).getVal();
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "int getStateVal(...)", e);
		}
		return 0;
	}
	
	public int getStateIdTS(String denom) {
		return getStateVal(idTS, denom);
	}
	
	public int getStateId(String denom) {
		return getStateVal(id, denom);
	}

	public int getStatus() {
		Tsignal tSignal = SingleFromDB.tsignals.get(idTS);
		return tSignal != null ? tSignal.getStatus() : status;
	}
	
	public void setShapeFill(Shape sh, String colorName1, String colorName2) {
		if (getStatus() == 1) {
			sh.setFill(getColorByName(getValue().getIdTSValue() == getStateIdTS("ON") ? colorName1 : colorName2));
		} else {
			sh.setFill(getColorByName("yellow", getValue().getIdTSValue() == getStateIdTS("ON") ? colorName1 : colorName2));
		}
	}

	public Tsignal gettSignalID() {
		tSignalID = SingleFromDB.tsignals.get(id);
		return tSignalID;
	}
	
	public Tsignal gettSignalIDTS() {
		tSignalIDTS = SingleFromDB.tsignals.get(idTS);
		return tSignalIDTS;
	}
}
