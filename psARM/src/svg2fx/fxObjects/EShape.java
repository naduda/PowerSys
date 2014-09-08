package svg2fx.fxObjects;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.script.Invocable;
import javax.script.ScriptException;

import svg2fx.Convert;
import javafx.scene.Group;

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
	public double val;
	
	private Map<String, Integer> signals = new HashMap<>();
	private int id;
	private int idTS;
	
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

	public double getVal() {
		return val;
	}

	public void setVal(double val) {
		this.val = val;
		getValue().set(val);
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
		}
    	System.out.println(System.currentTimeMillis() - start);
	}

	@Override
	public void onValueChange(Double newValue) {
		double start = System.currentTimeMillis();
		if (custProps != null && custProps.get("Name") != null) {
			String sName = custProps.get("Name");
			
			if (sName.startsWith("DisConnector") || sName.startsWith("Breaker")) {
				try {
					Convert.engine.eval(new FileReader(new File("d:/"+ sName.substring(0, sName.indexOf(".")) +".js")));
					Invocable inv = (Invocable) Convert.engine;
		            inv.invokeFunction("valueChange", this );
				} catch (FileNotFoundException | ScriptException | NoSuchMethodException e) {
					System.out.println("Script not found");
				}
			}
		}
    	System.out.println(System.currentTimeMillis() - start);
	}
}
