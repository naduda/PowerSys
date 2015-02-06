package single;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.xml.bind.JAXBException;

import controllers.Controller;
import controllers.ToolBarController;
import controllers.interfaces.StageLoader;
import pr.SVGModel;
import pr.common.Utils;
import pr.log.LogFiles;
import pr.svgObjects.SVG;
import state.HotKeyClass;
import state.ProgramSettings;
import svg2fx.Convert;
import svg2fx.fxObjects.EShape;
import ui.MainStage;
import ui.Scheme;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class SingleObject {
	public static String ipAddress = "";
	public static Stage mainStage;
	public static Scheme mainScheme;
	public static String activeSchemeSignals = "";
	public static EShape selectedShape;
	public static SVGModel svgModel;
	public static SVG svg;
	public static Map<String, HotKeyClass> hotkeys = new HashMap<>();
	public static AlarmActivities alarmActivities = new AlarmActivities();
	
	public static final ScriptEngineManager manager = new ScriptEngineManager();
	public static final ScriptEngine engine = manager.getEngineByName("nashorn");
	public static final Invocable invokeEngine = (Invocable) SingleObject.engine;
	public static final Map<String, Map<String, String>> readedScripts = new HashMap<>();
	public static StageLoader chat = null;
	private static Stage navigator = null;
	
	private static ClassLoader classLoader;
	public static ClassLoader getClassLoader() {
		if (classLoader == null) {
			try {
				File file = new File(Utils.getFullPath("./lang"));
				URL[] urls = {file.toURI().toURL()};
				SingleObject.classLoader = new URLClassLoader(urls);
			} catch (MalformedURLException e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return classLoader;
	}
	
	public static final String FILE_SETTINGS = Utils.getFullPath("./Settings.xml");
	private static ProgramSettings ps;
	public static ProgramSettings getProgramSettings() {
		if (ps == null) {
			try {
				ps = ProgramSettings.getFromFile(FILE_SETTINGS);
			} catch (FileNotFoundException | JAXBException e) {
				LogFiles.log.log(Level.SEVERE, "void getProgramSettings()", e);
			}
		}
		return ps;
	}
	
	public static ResourceBundle getResourceBundle() {
		return Controller.getResourceBundle(new Locale(getProgramSettings().getLocaleName()));
	}
	
	public static MenuItem getMenuItemById(Object obj, String id) {
		if (obj == null) {
			obj = MainStage.controller.getMenuBarController().getMenuBar();
			for(Menu m : ((MenuBar)obj).getMenus()) {
				MenuItem mi = getMenuItemById(m, id);
				if (mi != null) return mi;
			}
		} else if (obj instanceof Menu) {
			for (MenuItem it : ((Menu)obj).getItems()) {
				if (it instanceof Menu) {
					MenuItem mi = getMenuItemById(it, id);
					if (mi != null) return mi;
				}
				if (id.equals(it.getId())) return it;
			}
		}
		return null;
	}
	
	public static List<Integer> getActiveSchemeSignals() {
		List<Integer> ret = new ArrayList<>();
		String str = activeSchemeSignals.substring(1, activeSchemeSignals.length() - 1);
		
		StringTokenizer st = new StringTokenizer(str, ",");
		while (st.hasMoreElements()) {
			ret.add(Integer.parseInt(st.nextElement().toString()));
		}
		return ret;
	}

	public static Stage getNavigator() {
		if (navigator == null) {
			navigator = new Stage();
			navigator.initModality(Modality.NONE);
			navigator.initOwner(SingleObject.mainStage.getScene().getWindow());
			
			ImageView imV = getSchemeImage(0);
			imV.setPreserveRatio(true);
			imV.fitWidthProperty().bind(navigator.widthProperty().subtract(5));
		    imV.fitHeightProperty().bind(navigator.heightProperty().subtract(5));
		    
		    Rectangle rect = new Rectangle();
		    rect.setStroke(Color.RED);
		    rect.setStrokeWidth(2);
		    rect.setFill(Color.TRANSPARENT);
		    
		    Group root = new Group(imV, rect);
		    BorderPane pane = new BorderPane(root);
		    
		    DoubleProperty rWidthProperty = new SimpleDoubleProperty();
		    DoubleProperty rHeightProperty = new SimpleDoubleProperty();
		    DoubleProperty rXProperty = new SimpleDoubleProperty();
		    DoubleProperty rYProperty = new SimpleDoubleProperty();
		    
		    ChangeListener<Object> changeListener = new ChangeListener<Object>() {
	            @Override
	            public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
	            	rect.relocate(0, 0);
	            	double hmin = SingleObject.mainScheme.getHmin();
	                double hmax = SingleObject.mainScheme.getHmax();
	                double hvalue = SingleObject.mainScheme.getHvalue();
	                double contentWidth = SingleObject.mainScheme.getContent().getLayoutBounds().getWidth();
	                double viewportWidth = SingleObject.mainScheme.getViewportBounds().getWidth();
	                
	                double hoffset = Math.max(0, contentWidth - viewportWidth) * (hvalue - hmin) / (hmax - hmin);
	
	                double vmin = SingleObject.mainScheme.getVmin();
	                double vmax = SingleObject.mainScheme.getVmax();
	                double vvalue = SingleObject.mainScheme.getVvalue();
	                double contentHeight = SingleObject.mainScheme.getContent().getLayoutBounds().getHeight();
	                double viewportHeight = SingleObject.mainScheme.getViewportBounds().getHeight();
	                
	                double voffset = Math.max(0,  contentHeight - viewportHeight) * (vvalue - vmin) / (vmax - vmin);
	                
	                double koefX = imV.getBoundsInLocal().getWidth() / contentWidth;
	    	 	    double koefY = imV.getBoundsInLocal().getHeight() / contentHeight;
	    	 	    
	    	 	    rWidthProperty.set(viewportWidth * koefX);
	    	 	    rHeightProperty.set(viewportHeight * koefY);
	    	 	    rXProperty.set(hoffset * koefX);
	    	 	    rYProperty.set(voffset * koefY);
	            }
	        };
	        
	        rect.setOnMouseDragged(e -> {
	        	Point2D p = imV.sceneToLocal(((MouseEvent)e).getSceneX(), ((MouseEvent)e).getSceneY());
	        	
				double x = p.getX() - rect.getWidth() / 2;
	        	double y = p.getY() - rect.getHeight() / 2;
	        	
	    		rect.relocate(x, y);
	    		// ----------------------------------------------------------------------
	    		x = rect.getLayoutX();
	    		y = rect.getLayoutY();
	    		SingleObject.mainScheme.setHvalue(x / (imV.getBoundsInLocal().getWidth() - rect.getWidth()));
	    		SingleObject.mainScheme.setVvalue(y / (imV.getBoundsInLocal().getHeight() - rect.getHeight()));
	        });
	        
		    SingleObject.mainScheme.viewportBoundsProperty().addListener(changeListener);
		    ToolBarController.zoomProperty.addListener(changeListener);
		    imV.boundsInParentProperty().addListener(changeListener);
		    SingleObject.mainScheme.hvalueProperty().addListener(changeListener);
		    SingleObject.mainScheme.vvalueProperty().addListener(changeListener);
		    
		    rect.widthProperty().bind(rWidthProperty);
		    rect.heightProperty().bind(rHeightProperty);
		    rect.translateXProperty().bind(rXProperty.add(-rect.getTranslateX()));
		    rect.translateYProperty().bind(rYProperty.add(-rect.getTranslateY()));
		    
		    Image image = imV.getImage();
			Scene scene = new Scene(pane, 300, 300 * image.getHeight() / image.getWidth());
			navigator.setScene(scene);
			navigator.setResizable(false);
		}
		return navigator;
	}
	
	public static ImageView getSchemeImage(int sizeOfXAxis) {
		try (ByteArrayOutputStream os = new ByteArrayOutputStream();) {
			SnapshotParameters parameters = new SnapshotParameters();
	        parameters.setFill(Convert.backgroundColor);
	        parameters.setDepthBuffer(true);
	        
	        double oldScale = SingleObject.mainScheme.getRoot().getScaleX();
	        double oldX = SingleObject.mainScheme.getHvalue();
	        double oldY = SingleObject.mainScheme.getVvalue();
	        
	        if (sizeOfXAxis == 0) {
	        	MainStage.fitToPage();
	        } else {
	        	Bounds bounds = SingleObject.mainScheme.getRoot().getBoundsInLocal();
	        	if (bounds.getHeight() > bounds.getWidth()) {
		        	SingleObject.mainScheme.getRoot()
		        		.setScaleX(sizeOfXAxis / SingleObject.mainScheme.getRoot().getBoundsInLocal().getWidth());
	        	} else {
	        		sizeOfXAxis = (int) (sizeOfXAxis * bounds.getWidth() / bounds.getHeight());
	        		SingleObject.mainScheme.getRoot()
	        			.setScaleX(sizeOfXAxis / SingleObject.mainScheme.getRoot().getBoundsInLocal().getWidth());
	        	}
	        }
			WritableImage writableImage = SingleObject.mainScheme.getContent().snapshot(parameters, null);
			
			SingleObject.mainScheme.getRoot().setScaleX(oldScale);
			SingleObject.mainScheme.setHvalue(oldX);
			SingleObject.mainScheme.setVvalue(oldY);
			
			ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", os);
			
			try (InputStream inputStream = new ByteArrayInputStream(os.toByteArray());) {
				Image image = new Image(inputStream);
				return new ImageView(image);
			} catch (Exception e) {
				LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			}
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
	
	public static Image invertImage(Image image) {
		BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
		
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                int rgba = bufferedImage.getRGB(x, y);
                java.awt.Color col = new java.awt.Color(rgba, true);
                col = new java.awt.Color(255 - col.getRed(), 255 - col.getGreen(), 255 - col.getBlue());
                bufferedImage.setRGB(x, y, col.getRGB());
            }
        }
        
        WritableImage wr = null;
        if (bufferedImage != null) {
            wr = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    pw.setArgb(x, y, bufferedImage.getRGB(x, y));
                }
            }
        }
        
        return wr;
    }
}
