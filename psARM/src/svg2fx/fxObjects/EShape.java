package svg2fx.fxObjects;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.script.ScriptException;

import controllers.Controller;
import controllers.ShapeController;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.TSysParam;
import pr.model.Tsignal;
import pr.svgObjects.G;
import single.Constants;
import single.SingleFromDB;
import single.SingleObject;
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
	private static final int WORK_STATUS = 1;
	private final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();	
	private final DecimalFormat decimalFormat = new DecimalFormat("", decimalFormatSymbols);
	
	private final G svgGroup;
	
	private SignalState value = new SignalState();

	private Map<String, Integer> signals = new HashMap<>();
	private int id = -1;
	private int idTS = -1;
	private int status = -1;
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
	public EShape(Group g, G svgGroup) {
		super(g);
		this.svgGroup = svgGroup;
		
		decimalFormatSymbols.setDecimalSeparator(',');
		decimalFormatSymbols.setGroupingSeparator(' ');
		
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
				setValue(-1, 0.0, "id");
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
			String script = getScripts().getScriptByName(scriptName);
				
			if (script != null) {
				try {
					double start = System.currentTimeMillis();
					SingleObject.engine.eval(script);
					SingleObject.invokeEngine.invokeFunction(scriptName, this);
		            double execTime = System.currentTimeMillis() - start; 
		            if (execTime > 25) LogFiles.log.log(Level.WARNING, String.format(getId() + ": script execute time: %s ms", execTime));
				} catch (ScriptException | NoSuchMethodException e) {
					LogFiles.log.log(Level.SEVERE, "void runScriptByName(...) " + scriptName + " - " + getId(), e);
					LogFiles.log.log(Level.INFO, scriptName);
				}
			}
		} catch (Exception e) {
			//LogFiles.log.log(Level.SEVERE, "void runScriptByName(...) " + scriptName, e);
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
			SingleFromDB.psClient.setTS(idSignal, val, 107, -1, SingleObject.mainScheme.getIdScheme());
		} catch (RemoteException | NumberFormatException e) {
			LogFiles.log.log(Level.SEVERE, "void setTS(...)", e);
		}
	}
	
	public void setTU(int idSignal, double val) {
		try {
			switch (getStatus()) {
			case 1:
				SingleFromDB.psClient.setTU(idSignal, val, 0, -1, SingleObject.mainScheme.getIdScheme());
				break;
			case 3:
				SingleFromDB.psClient.setTU(idSignal, val, 107, -1, SingleObject.mainScheme.getIdScheme());
				break;
			}
			
		} catch (RemoteException | NumberFormatException e) {
			LogFiles.log.log(Level.SEVERE, "void setTU(...)", e);
		}
	}

	public SignalState getValue() {
		return value;
	}

	public void setValue(int idSignal, double val, String typeSignal) {
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
		
		setLastDataDate(dt);
		if (idSignal > 0) {
			int status = idSignal == id ? gettSignalID().getStatus() : gettSignalIDTS().getStatus();
			int typeSignalRef = idSignal == id ? tSignalID.getTypesignalref() : tSignalIDTS.getTypesignalref();
			if (typeSignalRef == Constants.TI_SIGNAL && status == WORK_STATUS) {
				updateSignal(SingleFromDB.validTimeOutTI);
			} else if (typeSignalRef != Constants.TI_SIGNAL && status == WORK_STATUS) {
				updateSignal(SingleFromDB.validTimeOutTS);
			}
		}
		
		getValueProp().set(val); //Listener
	}
	
	public void setTextValue(Text txt, String val) {
		txt.setText(val);
		setTextParameters(txt);
	}
	
	public void setTextValue(Text txt, String val, String format) {
		decimalFormat.applyPattern(format);
		try {
			txt.setText(decimalFormat.format(Double.parseDouble(val)));
		} catch (Exception e) {
			txt.setText(val);
		}		
	}

	private void setTextValue(Node n, double val, final String format) {
		decimalFormat.applyPattern(format);
		if (n instanceof Group) {
			Group gr = (Group)n;
			gr.getChildren().forEach(ch -> setTextValue(ch, val, format));
		} else {
			if (n instanceof Text) {
				Text t = (Text) n;
				
				try {
					if (t.getParent().getParent().getParent().getParent().getId().toLowerCase().startsWith("digitaldevice")) {
						String unit = " " + SingleFromDB.signals.get(id).getNameunit().trim();
						if (unit.length() > 7) unit = ""; //Ne zadano
						String textValue = decimalFormat.format(val) + unit;
						t.setText(textValue);
					}
					setTextParameters(t);
//						
//						Double textVal = svgGroup.getlRect().get(0).getWidth();
//						t.setWrappingWidth(textVal);
//						
//						if (svgGroup.getlText().get(0).getvParagraph().getHorizAlign() != null) {
//							int ihAlign = svgGroup.getlText().get(0).getvParagraph().getHorizAlign();
//							switch (ihAlign) {
//							case 1:
//								t.setTextAlignment(TextAlignment.CENTER);
//								break;
//							case 2:
//								t.setTextAlignment(TextAlignment.RIGHT);
//								break;
//							}
//						}
//						
//						String margins = svgGroup.getTextBlock().getMargins();
//						margins = margins.substring(margins.indexOf("(") + 1, margins.length() - 1);
//						String[] marginsArr = margins.split(",");
//						t.setTranslateX(-(t.getBoundsInParent().getMinX() + Double.parseDouble(marginsArr[1])));
//					}
				} catch (Exception e) {
					//LogFiles.log.log(Level.WARNING, "void setTextValue(...)", e);
				}
			}
		}
	}
	
	private void setTextParameters(Text t) {
		if (t.getTranslateX() == 0) {
			Double textVal = svgGroup.getlRect().get(0).getWidth();
			t.setWrappingWidth(textVal);
			
			if (svgGroup.getlText().get(0).getvParagraph().getHorizAlign() != null) {
				int ihAlign = svgGroup.getlText().get(0).getvParagraph().getHorizAlign();
				switch (ihAlign) {
				case 1:
					t.setTextAlignment(TextAlignment.CENTER);
					break;
				case 2:
					t.setTextAlignment(TextAlignment.RIGHT);
					break;
				}
			}
			
			String margins = svgGroup.getTextBlock().getMargins();
			margins = margins.substring(margins.indexOf("(") + 1, margins.length() - 1);
			String[] marginsArr = margins.split(",");
			t.setTranslateX(-(t.getBoundsInParent().getMinX() + Double.parseDouble(marginsArr[1])));
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
		} else {
			String sName = getId().replace("_", ".");
			sName = sName.contains(".") ? sName.substring(0, sName.indexOf(".")) : sName;
			if (TEXT_CONST.equals(sName)) return null;
			scriptPath = Utils.getFullPath("./scripts/" + sName + ".js");
		}
		return scriptPath;
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

	public G getSvgGroup() {
		return svgGroup;
	}
}
