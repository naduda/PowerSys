package svg2fx.fxObjects;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.script.Invocable;
import javax.script.ScriptException;

import pr.common.Utils;
import pr.model.Tsignal;
import svg2fx.Convert;
import svg2fx.SignalState;
import ui.Main;
import ui.MainStage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class EShape extends AShape {
	private static final String TEXT_CONST = "text";
	
	private SignalState value = new SignalState();
	
	private Map<String, Integer> signals = new HashMap<>();
	private int id = -1;
	private int idTS = -1;
	private boolean textShape;
	private ScriptClass scripts;
	private String scriptPath;
	
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
	}
	
	public void setTS(int idSignal, int val) {
		try {
			MainStage.psClient.setTS(idSignal, val, Main.mainScheme.getIdScheme());
		} catch (RemoteException | NumberFormatException e) {
			e.printStackTrace();
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
			(gr).getChildren().forEach(ch -> { setTextValue(ch, val, format); });
		} else {
			if (n instanceof Text) {
				Text t = (Text) n;
				try {
					Double textVal = Double.parseDouble(t.getText());
					textVal = t.getBoundsInLocal().getWidth();
					
					DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
					decimalFormatSymbols.setDecimalSeparator('.');
					decimalFormatSymbols.setGroupingSeparator(' ');
					
					DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
					String textValue = decimalFormat.format(val) + "  ";
					
					t.setText(textValue);
					t.setWrappingWidth(textVal);
					t.setTextAlignment(TextAlignment.RIGHT);
				} catch (Exception e) {
					//e.printStackTrace();
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
		if (custProps != null) {
			String sName = getId().replace("_", ".");
			sName = sName.indexOf(".") > -1 ? sName.substring(0, sName.indexOf(".")) : sName;
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

	@Override
	public void onDoubleClick() {
		runScriptByName("onDoubleClick");
	}
	
	@Override
	public void onValueChange(Double newValue) {
		runScriptByName("onValueChange");
	}
	
	private void runScriptByName(String scriptName) {
		if (TEXT_CONST.equals(getId())) return;
		double start = System.currentTimeMillis();
		
		String script = "";
		try {
			script = getScripts().getScriptByName(scriptName);
			
			if (script != null) {
				Convert.engine.eval(script);
				Invocable inv = (Invocable) Convert.engine;
	            inv.invokeFunction(scriptName, this);
			}
		} catch (ScriptException | NoSuchMethodException e) {
			System.out.println("Script not found - " + script);
		}
		
		if ((System.currentTimeMillis() - start) > 10)
        	System.out.println((System.currentTimeMillis() - start) + " mc, :" + scriptName + " -> " + getId());
	}
	
	private int getStateVal(int idSignal, String denom) {
		try {
			Tsignal tSignal = MainStage.psClient.getTsignalsMap().get(idSignal);
			return MainStage.psClient.getSpTuCommand().stream()
				.filter(f -> f.getObjref() == tSignal.getStateref() && f.getDenom().equals(denom.toUpperCase()))
				.collect(Collectors.toList()).get(0).getVal();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getStateIdTS(String denom) {
		return getStateVal(idTS, denom);
	}
	
	public int getStateId(String denom) {
		return getStateVal(id, denom);
	}
}
