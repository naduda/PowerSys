package ui;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import controllers.Controller;
import controllers.ShapeController;
import pr.common.Utils;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Scheme extends ScrollPane {
	private Group root;
	private final Group rootScheme = new Group();
	private final List<Integer> signalsTI = new ArrayList<>();
	private final List<Integer> signalsTS = new ArrayList<>();
	public static EShape selectedShape;
	public static final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
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
		
		root = (Group) Convert.getNodeBySVG(fileName);
		setIdScheme(Convert.idScheme);
		rootScheme.getChildren().add(root);
		
		Convert.listSignals.forEach(e -> {
			if (e.getKey() != 0) {
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
					e1.printStackTrace();
					System.err.println(e.getKey());
				}
			}
		});
		
		try {
			MainStage.psClient.getOldTI().values().forEach(s -> MainStage.controller.updateTI(this, s));
			MainStage.psClient.getOldTS().values().forEach(s -> MainStage.controller.updateTI(this, s));
			
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
					addTransparant(t.getTp(), transpLocate.getX(), transpLocate.getY(), transpLocate.getH(), 
							t.getIdtr());
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		}
	}
	
	public void addTransparant(int idTransparant, double xT, double yT, double sizeT, int ident) {
		try {
			Shape transparant = createCircle(idTransparant, xT, yT, sizeT, ident);
			root.getChildren().add(transparant);
		} catch (Exception e) {
			System.out.println(idTransparant + " not found");
		}
	}
	
	private Shape createCircle(int idTransarant, double xT, double yT, double sizeT, int ident) {
		Circle n = new Circle(xT + sizeT / 2, yT + sizeT / 2, sizeT / 2);
		n.setStroke(Color.TRANSPARENT);
		n.setFill(new ImagePattern(MainStage.imageMap.get(idTransarant)));
		n.setId("transparant_" + ident);
		addContextMenu(n);
		
		n.setOnMouseDragged(new TransparantEventHandler());
        n.setOnMouseReleased(new TransparantEventHandler(ident));

        return n;
    }
	
	private void addContextMenu(Shape sh) {
		try {
			FXMLLoader loader = new FXMLLoader(new URL("file:/" + Utils.getFullPath("./ui/TransparantContextMenu.xml")));
			ContextMenu contextMenu = loader.load();
			ShapeController shapeController = loader.getController();
			contextMenu.setId(sh.getId());
			
			sh.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent t) {
		            if(t.getButton().toString().equals("SECONDARY")) {
		            	ResourceBundle rb = Controller.getResourceBundle(new Locale(Main.getProgramSettings().getLocaleName()));
		            	shapeController.setElementText(rb);
		            	contextMenu.show(sh, t.getScreenX(), t.getSceneY());
		            }
		        }
		    });
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	
	private final class TransparantEventHandler implements EventHandler<Event> {
		private int ident;
		private double maxX = root.getBoundsInLocal().getMaxX();
		private double maxY = root.getBoundsInLocal().getMaxY();
    	
		public TransparantEventHandler() {
			
		}
		
		public TransparantEventHandler(int ident) {
			this();
			this.ident = ident;
		}
		
		@Override
		public void handle(Event e) {
			Circle c = (Circle) e.getSource();
			double r = c.getRadius();
			if (e.getEventType().equals(MouseEvent.MOUSE_RELEASED)) {
				Bounds bounds = c.boundsInParentProperty().getValue();
	        	double x = bounds.getMinX() + r;
	        	x = x < r ? r : x - r > maxX ? maxX - r : x - r;
	        	double y = bounds.getMinY() + r;
	        	y = y < r ? r : y - r > maxY ? maxY - r : y - r;
	        	
				try {
					MainStage.psClient.updateTtranspLocate(ident, Main.mainScheme.getIdScheme(), 
							(int)x, (int)y, (int)r * 2, (int)r * 2);
					
					MainStage.psClient.updateTtransparantLastUpdate(ident);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Point2D p = Main.mainScheme.getRoot().sceneToLocal(((MouseEvent)e).getSceneX(), ((MouseEvent)e).getSceneY());
				double x = p.getX();
	        	double y = p.getY();
	            c.relocate(x < r ? r : x - r > maxX ? maxX - r : x - r, 
	            		   y < r ? r : y - r > maxY ? maxY - r : y - r);
			}
		}
		
	}
}
