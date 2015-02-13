package ui;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

import controllers.Controller;
import controllers.ShapeController;
import controllers.ToolBarController;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.model.LinkedValue;
import pr.model.Transparant;
import pr.model.Tscheme;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import pr.model.VsignalView;
import single.SingleFromDB;
import single.SingleObject;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
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
	private final List<Integer> signalsTU = new ArrayList<>();
	private final List<LinkedValue> listSignals = new ArrayList<>();
	private final List<Integer> idSignals = new ArrayList<>();
	
	private int idScheme = 0;
	private String schemeName;
	private String schemeFileName;
	
	public Scheme() {
		LogFiles.log.log(Level.INFO, "Start building scheme");
		setContent(rootScheme);
		
		Events events = new Events();
		addEventFilter(ScrollEvent.ANY, events::setOnScroll);
		
		setOnMouseReleased(e -> {
			if (SingleObject.selectedShape != null) {
				SingleObject.selectedShape.setSelection(false);
				SingleObject.selectedShape = null;
			}
		});
	}
	
	public Scheme(String fileName) {
		this();
		
		schemeFileName = fileName;
		setSchemeName(fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")));
		
		SingleFromDB.svgFile = new File(fileName);
		
		LogFiles.log.log(Level.INFO, "Start convert scheme");
		long start = System.currentTimeMillis();
		root = (Group) Convert.getNodeBySVG(fileName);
		LogFiles.log.log(Level.INFO, "Finish convert scheme - " + (System.currentTimeMillis() - start) / 1000 + " s");
		
		setIdScheme(Convert.idScheme);
		rootScheme.getChildren().add(root);
		setSchemeParams();
	}
	
	public Scheme(Tscheme tScheme) {
		this();
		
		setSchemeName(tScheme.getSchemename());
		
		LogFiles.log.log(Level.INFO, "Start convert scheme");
		long start = System.currentTimeMillis();
		root = (Group) Convert.getNodeBySVG(new ByteArrayInputStream((byte[])tScheme.getSchemefile()));
		LogFiles.log.log(Level.INFO, "Finish convert scheme - " + (System.currentTimeMillis() - start) / 1000 + " s");
		
		setIdScheme(tScheme.getIdscheme());
		rootScheme.getChildren().add(root);
		setSchemeParams();
		LogFiles.log.log(Level.INFO, "Finish building scheme");
	}
	
	private void setSchemeParams() {
		listSignals.addAll(Convert.getListSignals());
		listSignals.forEach(e -> {
			if (e.getId() != 0) {
				if (!idSignals.contains(e.getId())) idSignals.add(e.getId());
				try {
					VsignalView vSignal = SingleFromDB.signals.get(e.getId());
					if (vSignal != null) {
						int typeSignal = vSignal.getTypesignalref();
						
						switch (typeSignal) {
						case 1:
							signalsTI.add(e.getId());
							break;
						case 2:
							signalsTS.add(e.getId());
							break;
						case 3:
							signalsTU.add(e.getId());
							break;
						default:
							break;
						}
					} else {
						System.out.println(e.getId());
					}
				} catch (Exception e1) {
					LogFiles.log.log(Level.SEVERE, "signalsTI", e1);
				}
			}
		});
		
		Platform.runLater(() -> setTransparants());
		
		if (Convert.backgroundColor != null) {
			String bgColor = String.format("-fx-background: %s;", Convert.backgroundColor.toString().replace("0x", "#"));
			setStyle(bgColor);
		}
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
			LogFiles.log.log(Level.SEVERE, "EShape getDeviceById(...)", e);
		}
		return tt;
	}
	
	private void setTransparants() {
		List<Ttransparant> tTransparants = SingleFromDB.psClient.getTtransparantsActive(getIdScheme());
		if (tTransparants != null) {
			tTransparants.forEach(t -> {
				Platform.runLater(() -> {
					try {
						TtranspLocate transpLocate = SingleFromDB.psClient.getTransparantLocate(t.getIdtr());
						addTransparant(t.getTp(), transpLocate.getX(), transpLocate.getY(), transpLocate.getH(), 
								t.getIdtr());
					} catch (Exception e) {
						LogFiles.log.log(Level.SEVERE, "void setTransparants()", e);
					}
				});
			});
		}
	}
	
	public void addTransparant(int idTransparant, double xT, double yT, double sizeT, int ident) {
		try {
			Shape transparant = createCircle(idTransparant, xT, yT, sizeT, ident);
			root.getChildren().add(transparant);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "void addTransparant(...)", e);
		}
	}
	
	private Shape createCircle(int idTransarant, double xT, double yT, double sizeT, int ident) {
		Circle n = new Circle(xT + sizeT / 2, yT + sizeT / 2, sizeT / 2);
		n.setStroke(Color.TRANSPARENT);
		Transparant item = SingleFromDB.transpMap.get(idTransarant);
		Image image = new Image(new ByteArrayInputStream(item.getImageByteArray()));
		n.setFill(new ImagePattern(image));
		n.setId("transparant_" + ident);
		addContextMenu(n);
		
		n.setOnMouseDragged(new TransparantEventHandler());
        n.setOnMouseReleased(new TransparantEventHandler(ident));

        return n;
    }
	
	private void addContextMenu(Shape sh) {
		try {
			FXMLLoader loader = new FXMLLoader(new File(Utils.getFullPath("./ui/transparant/TransparantContextMenu.xml")).toURI().toURL());
			ContextMenu contextMenu = loader.load();
			ShapeController shapeController = loader.getController();
			contextMenu.setId(sh.getId());
			
			sh.setOnMouseClicked(new EventHandler<MouseEvent>() {
		        @Override
		        public void handle(MouseEvent t) {
		            if(t.getButton().toString().equals("SECONDARY")) {
		            	ResourceBundle rb = Controller.getResourceBundle(new Locale(SingleObject.getProgramSettings().getLocaleName()));
		            	shapeController.setElementText(rb);
		            	contextMenu.show(sh, t.getScreenX(), t.getSceneY());
		            }
		        }
		    });
		} catch (IOException e) {
			LogFiles.log.log(Level.SEVERE, "void addContextMenu(...)", e);
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
	
	public List<LinkedValue> getListSignals() {
		return listSignals;
	}
	
	public List<Integer> getIdSignals() {
		return idSignals;
	}
	
	public String getSchemeFileName() {
		return schemeFileName;
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
                
				ToolBarController.changeZoom(root.getScaleX() * zoomFactor);
                event.consume();

                double newW = root.getBoundsInParent().getWidth();
                double newH = root.getBoundsInParent().getHeight();
                double deltaX = newW - startW;
   
                deltaX = lX * (deltaX / newW);
                deltaY = lY * (deltaY / newH);

                setHvalue(getHvalue() + deltaX / (newW - getWidth()));
                setVvalue(getVvalue() + deltaY / (newH - getHeight()));
                
                ToolBarController.zoomProperty.set(root.getScaleX());
            } else if (event.isShiftDown()) {
            	setHvalue(event.getDeltaX() < 0 ? getHvalue() + 0.05 : getHvalue() - 0.05);
            }
		}
	}
	
	private final class TransparantEventHandler implements EventHandler<Event> {
		private int ident;
		private double maxX = root.getBoundsInLocal().getMaxX();
		private double maxY = root.getBoundsInLocal().getMaxY();
    	
		public TransparantEventHandler() {}
		
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
					SingleFromDB.psClient.updateTtranspLocate(ident, getIdScheme(), 
							(int)x, (int)y, (int)r * 2, (int)r * 2);
					
					SingleFromDB.psClient.updateTtransparantLastUpdate(ident);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
				Point2D p = getRoot().sceneToLocal(((MouseEvent)e).getSceneX(), ((MouseEvent)e).getSceneY());
				double x = p.getX();
	        	double y = p.getY();
	            c.relocate(x < r ? r : x - r > maxX ? maxX - r : x - r, 
	            		   y < r ? r : y - r > maxY ? maxY - r : y - r);
			}
		}
	}
}
