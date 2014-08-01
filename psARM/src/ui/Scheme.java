package ui;

import graphicObject.AShape;
import graphicObject.Breaker;
import graphicObject.Car;
import graphicObject.DigitalDevice;
import graphicObject.DisConnectorGRND;
import graphicObject.Disconnector;
import graphicObject.Kettle;
import graphicObject.Protector;
import graphicObject.ShapeFX;
import graphicObject.TC;
import graphicObject.VCar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import model.LinkedValue;
import xml.Document;
import xml.ShapeX;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

public class Scheme extends ScrollPane {
	
	private final Group root = new Group();
	private List<Integer> signalsTI;
	private List<Integer> signalsTS;
	public static AShape selectedShape;
	private int idScheme = 0;
	private String schemeName;
	
	private String bgColor = "-fx-background: gray;";
	
	public Scheme() {
		setContent(root);
		setStyle(bgColor);
		Events events = new Events();
		setOnScroll(event -> { events.setOnScroll(event); });
	}
	
	public Scheme(String fileName) {
		this();

		Object result = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Document.class);
			Unmarshaller u = jc.createUnmarshaller();
			result = u.unmarshal(new FileInputStream(new File(fileName)));
		} catch (JAXBException | FileNotFoundException e) {
			System.err.println("Error in EntityFromXML.getObject(...). " + e);
		}
		
		signalsTI = new ArrayList<>();
		signalsTS = new ArrayList<>();
		
		Document doc = (Document) result;
		setSchemeName(doc.getPage().getName());
		
		doc.getPage().getShapes().forEach(shapeX -> {
			if (shapeX.getType() != null) {
				if (shapeX.getType().toLowerCase().equals("group")) {
					paintGroup(root, shapeX, true);
				} else {
					paintShape(root, shapeX, true);
				}
			}
		});
		
		Map<Integer, LinkedValue> oldTI = Main.pdb.getOldTI();
		Map<Integer, LinkedValue> oldTS = Main.pdb.getOldTS();
		
		for (LinkedValue lv : oldTI.values()) {
			MainStage.controller.updateTI(this, lv);
		}

		for (LinkedValue lv : oldTS.values()) {
			MainStage.controller.updateTS(this, lv);
		}

		bgColor = String.format("-fx-background: %s;", doc.getPage().getFillColor());
		setStyle(bgColor);
	}
	
	@Override
	public String toString() {
		return schemeName;
	}

	private void paintGroup(Group root, ShapeX shGr, boolean canSelect) {
		Group gr = new Group();
		shGr.getShapes().forEach(sh -> {
			if (sh.getType().toLowerCase().equals("group")) {
				paintGroup(gr, sh, false);
			} else {
				paintShape(gr, sh, false);
			}
		});

		gr.setRotate(shGr.getAngle());
		if (shGr.getFlipY() != 0) {
			gr.setScaleY(-shGr.getFlipY());
		}
		if (shGr.getFlipX() != 0) {
			gr.setScaleX(-shGr.getFlipX());
		}
		
		if (canSelect) {
			Bounds bounds = gr.localToScene(gr.getBoundsInLocal());
			shGr.setX(bounds.getMinX());
			shGr.setY(bounds.getMinY());
			shGr.setWidth(bounds.getWidth());
			shGr.setHeight(bounds.getHeight());
			ShapeFX fx = new ShapeFX(shGr);
			root.getChildren().addAll(gr, fx);
		} else {
			root.getChildren().add(gr);
		}
	}
	
	private void paintShape(Group gr, ShapeX sh, boolean canSelect) {
		Shape shFX = null;
				
		if ("text".equals(sh.getType().toLowerCase())) {
			if (sh.getId() != null && sh.getId().toLowerCase().startsWith("digitaldevice")) {
				DigitalDevice dd = new DigitalDevice(sh);
				signalsTI.add(Integer.parseInt(dd.getId()));
				gr.getChildren().add(dd);
				return;
			}
			
			if (sh.isFilled() || sh.getLinePattern() == 1) {
				shFX = new Rectangle(0, 0, sh.getWidth(), sh.getHeight());
				sh.setLineWeight(1);
				shapeTransform(gr, shFX, sh, canSelect);
			}

			Text text = new Text(sh.getX(), sh.getY() + sh.getHeight()/2 + sh.getFontSize()/4, sh.getText());
			text.setFont(new Font("Arial", sh.getFontSize()));
			text.setWrappingWidth(sh.getWidth());
			text.setTextAlignment(TextAlignment.CENTER);
			text.setFill(getColor(sh.getLineColor()));

			gr.getChildren().add(text);
			return;
		}
		if ("gdisconnector".equals(sh.getType().toLowerCase())) {
			Disconnector dd = new Disconnector(sh);
			signalsTS.add(Integer.parseInt(dd.getId()));
			gr.getChildren().add(dd);
			return;
		}
		if ("gdisconnectorgrnd".equals(sh.getType().toLowerCase())) {
			DisConnectorGRND dd = new DisConnectorGRND(sh);
			signalsTS.add(Integer.parseInt(dd.getId()));
			gr.getChildren().add(dd);
			return;
		}
		if ("gbreaker".equals(sh.getType().toLowerCase())) {
			Breaker dd = new Breaker(sh);
			signalsTS.add(Integer.parseInt(dd.getId()));
			gr.getChildren().add(dd);
			return;
		}
		if ("gcar".equals(sh.getType().toLowerCase())) {
			gr.getChildren().add(new Car(sh));
			return;
		}
		if ("gtc".equals(sh.getType().toLowerCase())) {
			gr.getChildren().add(new TC(sh));
			return;
		}
		if ("gprotector".equals(sh.getType().toLowerCase())) {
			gr.getChildren().add(new Protector(sh));
			return;
		}
		if ("gkettle".equals(sh.getType().toLowerCase())) {
			gr.getChildren().add(new Kettle(sh));
			return;
		}
		if ("gvcar".equals(sh.getType().toLowerCase())) {
			gr.getChildren().add(new VCar(sh));
			return;
		}
		
		if ("line".equals(sh.getType().toLowerCase())) {			
			shFX = new Line(0, 0, sh.getWidth(), sh.getHeight());
		}
		if ("rect".equals(sh.getType().toLowerCase())) {
			shFX = new Rectangle(0, 0, sh.getWidth(), sh.getHeight());
		}
		if ("circle".equals(sh.getType().toLowerCase())) {
			shFX = new Circle(sh.getWidth()/2, sh.getHeight()/2, sh.getWidth()/2);
		}
		if ("arc".equals(sh.getType().toLowerCase())) {
			shFX = new Arc(sh.getWidth()/2, sh.getHeight()/2, sh.getWidth()/2, sh.getHeight()/2, -sh.getStartAng(), -sh.getLenAng());
		}
		
		shapeTransform(gr, shFX, sh, canSelect);
		if (idScheme == 0 && signalsTI.size() > 0) {
			String sId = signalsTI.get(0).toString();
			setIdScheme(Integer.parseInt(sId.substring(0, sId.length() - 6)));
		}
	}
	
	private void shapeTransform(Group gr, Shape shFX, ShapeX sh, boolean canSelect) {
		double w = sh.getLineWeight();
		if (shFX != null) {
			shFX.setTranslateX(sh.getX());
			shFX.setTranslateY(sh.getY());
			shFX.setStrokeWidth(w);
			shFX.setRotate(sh.getAngle());			
			if (sh.isFilled()) {
				shFX.setFill(getColor(sh.getFillColor()));
			} else {
				shFX.setFill(Color.TRANSPARENT);
			}
			shFX.setStroke(getColor(sh.getLineColor()));
			
			if (canSelect) {
				if (sh.getWidth() < 0) {
					sh.setX(sh.getX() + sh.getWidth());
					sh.setWidth(-sh.getWidth());
				}
				if (sh.getHeight() < 0) {
					sh.setY(sh.getY() + sh.getHeight());
					sh.setHeight(-sh.getHeight());
				}
				if (sh.getWidth() < AShape.ONE_MM) {
					sh.setWidth(sh.getLineWeight() * 3);
					sh.setX(sh.getX() - sh.getLineWeight());
				}
				if (sh.getHeight() < AShape.ONE_MM) {
					sh.setHeight(sh.getLineWeight() * 3);
					sh.setY(sh.getY() - sh.getLineWeight());
				}
				ShapeFX fx = new ShapeFX(sh);				
				gr.getChildren().addAll(shFX, fx);
			} else {
				gr.getChildren().add(shFX);
			}
			if (sh.getId() != null && sh.getId().toLowerCase().startsWith("bus")) {
				shFX.toBack();
			}
		} else {
			System.out.println(sh.getId());
		}
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
	
	public DigitalDevice getDigitalDeviceById(String id) {
		DigitalDevice tt = null;
		try {
			tt = (DigitalDevice) root.lookup("#" + id);
		} catch (Exception e) {
			System.err.println("getDigitalDeviceById ...");
		}
		return tt;
	}
	
	public Group getDeviceById(String id) {
		Group tt = null;
		try {
			tt = (Group) root.lookup("#" + id);
		} catch (Exception e) {
			System.err.println("getDeviceById ...");
		}
		return tt;
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
	//	--------------------------------------------------------------
	private final class Events {
		public void setOnScroll(ScrollEvent event) {
			ScrollPane sp = (ScrollPane) event.getSource();
			double deltaY = event.getDeltaY();
			if (Main.ctrlPressed) {
				double zoomFactor = 1.1;
                if (deltaY < 0) {
                  zoomFactor = 2.0 - zoomFactor;
                }

                root.setScaleX(root.getScaleX() * zoomFactor);
                root.setScaleY(root.getScaleY() * zoomFactor);
                event.consume();
            } else if (Main.shiftPressed) {
            	if (deltaY < 0) {
            		sp.setHvalue(sp.getHvalue() + 0.04);
	            } else {
	            	sp.setHvalue(sp.getHvalue() - 0.04);
	            }
            }
		}
	}
}
