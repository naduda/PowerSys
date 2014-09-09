package svg2fx.fxObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.script.Invocable;
import javax.script.ScriptException;

import svg2fx.Convert;
import svg2fx.SignalState;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class EShape extends AShape {
	
	public enum typeSignalRef {
		 TI(1), TS(2), TU(3);
		 
		 private int code;
		 
		 private typeSignalRef(int c) {
		   code = c;
		 }
		 
		 public int getCode() {
		   return code;
		 }
	}
	
	public boolean isON = false;
	private SignalState value = new SignalState();
	
	private Map<String, Integer> signals = new HashMap<>();
	private int id;
	private int idTS;
	private boolean textShape;
	
	private HashMap<String, String> custProps;
	@SuppressWarnings("unchecked")
	public EShape(Group g) {
		super(g);
		
		if (g.getUserData() != null) {
			custProps = (HashMap<String, String>) g.getUserData();
			String v = custProps.get("id");
			setIdSignal(v != null ? Integer.parseInt(v) : 0);
			v = custProps.get("idTS");
			setIdTS(v != null ? Integer.parseInt(v) : 0);
			v = custProps.get("signals");
			if (v != null) {
				StringTokenizer st = new StringTokenizer(v, "|");
				while (st.hasMoreElements()) {
					String[] sign = ((String) st.nextElement()).split(":");
					signals.put(sign[0], Integer.parseInt(sign[1]));
				}
			}
		}
	}
	
	public void changeTS(int val) {
		isON = val == 0 ? false : true;
	}
	
	public void setTS(boolean val) {
		isON = !isON;
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
	
	public Map<String, Integer> getSignals() {
		return signals;
	}

	public void setSignals(Map<String, Integer> signals) {
		this.signals = signals;
	}

	public int getIdSignal() {
		return id;
	}

	public void setIdSignal(int id) {
		this.id = id;
	}

	public int getIdTS() {
		return idTS;
	}

	public void setIdTS(int idTS) {
		this.idTS = idTS;
	}

	public boolean isTextShape() {
		return textShape;
	}

	public void setTextShape(boolean textShape) {
		this.textShape = textShape;
	}

	@Override
	public void onDoubleClick() {
		double start = System.currentTimeMillis();
		if (custProps != null && custProps.get("Name") != null) {
			String sName = custProps.get("Name");
			
			if (sName.startsWith("DisConnector") || sName.startsWith("Breaker")) {
				try {
					Convert.engine.eval(new FileReader(new File("d:/"+ sName.substring(0, sName.indexOf(".")) +".js")));
					Invocable inv = (Invocable) Convert.engine;
		            inv.invokeFunction("dblClick", this );
				} catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {
					System.out.println("Script not found");
				}
			}
			System.out.println((System.currentTimeMillis() - start) + " mc");
		}
	}

	@Override
	public void onValueChange(Double newValue) {
		double start = System.currentTimeMillis();
		if (custProps != null && custProps.get("Name") != null) {
			String sName = custProps.get("Name");
			
			if (sName.startsWith("DisConnector") || sName.startsWith("Breaker")) {
				try {
					byte[] encoded = Files.readAllBytes(Paths.get("d:/"+ sName.substring(0, sName.indexOf(".")) +".js"));
					String script = new String(encoded, "utf-8");
					script = script.replace("[id]", "sh.getValue().getIdValue()");
					script = script.replace("[idTS]", "sh.getValue().getIdTSValue()");
					Convert.engine.eval(script);
					
					Invocable inv = (Invocable) Convert.engine;
		            inv.invokeFunction("valueChange", this );
		            System.out.println(newValue + "/" + getIdTS());
				} catch (ScriptException | NoSuchMethodException | IOException e) {
					System.out.println("Script not found or with error ");
				}
			}
			System.out.println((System.currentTimeMillis() - start) + " mc");
		}
	}
}
