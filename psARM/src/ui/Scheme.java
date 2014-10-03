package ui;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Scheme extends ScrollPane {
	
	private static final double KOEF = 0.78;
	
	private Group root;
	private final Group rootScheme = new Group();
	private final List<Integer> signalsTI = new ArrayList<>();
	private final List<Integer> signalsTS = new ArrayList<>();
	public static EShape selectedShape;
	private int idScheme = 0;
	private String schemeName;
	private double currentVvalue;
	
	private String bgColor = "-fx-background: black;";
	
	public Scheme() {
		setContent(rootScheme);
		setStyle(bgColor);
		Events events = new Events();
		setOnScroll(event -> { events.setOnScroll(event); });
		setOnKeyPressed(e -> { if (e.getCode() == KeyCode.SHIFT) currentVvalue = getVvalue(); });
	}
	
	public Scheme(String fileName) {
		this();
		
		setSchemeName(fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")));
		setIdScheme(1);
		
		root = (Group) Convert.getNodeBySVG(fileName);
		rootScheme.getChildren().add(root);
		
		Convert.listSignals.forEach(e -> {
			try {
				int typeSignal = MainStage.signals.get(e.getKey()).getTypesignalref();
				
				switch (typeSignal) {
				case 1:
					signalsTI.add(e.getKey());
					break;
				case 2:
					signalsTS.add(e.getKey());
					break;
				default:
					break;
				}
			} catch (Exception e1) {
				System.err.println(e.getKey());
			}
		});
		
		try {
			Map<Integer, DvalTI> oldTI = MainStage.psClient.getOldTI();
			Map<Integer, DvalTS> oldTS = MainStage.psClient.getOldTS();
			
			for (DvalTI ti : oldTI.values()) {
				MainStage.controller.updateTI(this, ti);
			}

			for (DvalTS ts : oldTS.values()) {
				MainStage.controller.updateTI(this, ts);
			}
			
			setTransparants();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

//		bgColor = String.format("-fx-background: %s;", doc.getPage().getFillColor());
		setStyle(bgColor);
	}
	
	@Override
	public String toString() {
		return schemeName;
	}

	public static Color getColor(String col) {
		if (col == null) return null;
		if (col.startsWith("#")) {
			return Color.web(col);
		} else {
			String lColor = decToHex(Long.parseLong(col)).substring(2);
			return Color.web("0x" + lColor);
		}
	}
	
	private static String decToHex(long dec) {
		char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	    StringBuilder hexBuilder = new StringBuilder(8);
	    hexBuilder.setLength(8);
	    for (int i = 8 - 1; i >= 0; --i)
	    {
	    	long j = dec & 0x0F;
		    hexBuilder.setCharAt(i, hexDigits[(int)j]);
		    dec >>= 4;
	    }
	    return hexBuilder.toString(); 
	}
	
	public EShape getDeviceById(String id) {
		EShape tt = null;
		try {
			tt = (EShape) root.lookup("#" + id);
		} catch (Exception e) {
			System.err.println("getDeviceById ...");
		}
		return tt;
	}
	
	private void setTransparants() throws RemoteException {
		List<Ttransparant> tTransparants = MainStage.psClient.getTtransparantsActive(getIdScheme());
		if (tTransparants != null) {
			tTransparants.forEach(t -> {
				try {
					TtranspLocate transpLocate = MainStage.psClient.getTransparantLocate(t.getIdtr());
					addTransparant(t.getTp(), transpLocate.getX(), transpLocate.getY(), transpLocate.getH());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	public void addTransparant(int idTransparant, double xT, double yT, double sizeT) {
		Shape transparant = createCircle(idTransparant, xT, yT, sizeT);
		root.getChildren().add(transparant);
	}
	
	private Shape createCircle(int idTransarant, double xT, double yT, double sizeT) {
		Circle n = new Circle(xT * KOEF, yT * KOEF, sizeT * KOEF / 2);
		n.setStroke(Color.TRANSPARENT);
		n.setFill(new ImagePattern(MainStage.imageMap.get(idTransarant)));
		
        n.setOnMouseDragged(event -> {
        	Point2D p = Main.mainScheme.getRoot().sceneToLocal(event.getSceneX(), event.getSceneY());
        	
        	double x = p.getX();
        	double y = p.getY();
        	double maxX = MainStage.bpScheme.getWidth();
        	double maxY = MainStage.bpScheme.getHeight();
        	double r = n.getRadius();
        	
            n.relocate(x < 2 * r ? r : x + r > maxX ? maxX - r : x - r, 
            		   y < 2 * r ? r : y + r > maxY ? maxY - r : y - r);
        });

        return n;
    }

	public List<Integer> getSignalsTI() {
		return signalsTI;
	}

	public List<Integer> getSignalsTS() {
		return signalsTS;
	}
	
	public int getIdScheme() {
		return idScheme;
	}

	public void setIdScheme(int idScheme) {
		this.idScheme = idScheme;
	}

	public String getSchemeName() {
		return schemeName;
	}

	public void setSchemeName(String schemeName) {
		this.schemeName = schemeName;
	}
	
	public Group getRoot() {
		return root;
	}
	//	--------------------------------------------------------------
	private final class Events {
		public void setOnScroll(ScrollEvent event) {
			double deltaY = event.getDeltaY();
			
			if (event.isControlDown()) {
				double zoomFactor = 1.1;
                if (deltaY < 0) {
                  zoomFactor = 0.9;
                }

                double startW = root.getBoundsInParent().getWidth();
                double startH = root.getBoundsInParent().getHeight();
                
                double lX = sceneToLocal(event.getSceneX(), 0).getX() + getHvalue() * (startW - getWidth());
                double lY = sceneToLocal(0, event.getSceneY()).getY() + getVvalue() * (startH - getHeight());
                
                root.setScaleX(root.getScaleX() * zoomFactor);
                root.setScaleY(root.getScaleY() * zoomFactor);
                event.consume();

                double newW = root.getBoundsInParent().getWidth();
                double newH = root.getBoundsInParent().getHeight();
                double deltaX = newW - startW;
   
                deltaX = lX * (deltaX / newW);
                deltaY = lY * (deltaY / newH);

                setHvalue(getHvalue() + deltaX / (newW - getWidth()));
                setVvalue(getVvalue() + deltaY / (newH - getHeight()));
            } else if (event.isShiftDown()) {
            	setVvalue(currentVvalue);
            	setHvalue(deltaY < 0 ? getHvalue() + 0.05 : getHvalue() - 0.05);
            }
		}
	}
}
