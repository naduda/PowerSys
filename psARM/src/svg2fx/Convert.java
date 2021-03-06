package svg2fx;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import pr.SVGModel;
import pr.log.LogFiles;
import pr.model.LinkedValue;
import pr.svgObjects.G;
import pr.svgObjects.SVG;
import single.ProgramProperty;
import svg2fx.fxObjects.EShape;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Convert {
	private static List<LinkedValue> listSignals = new ArrayList<>();
	public static int idScheme = 0;
	public static Paint backgroundColor;
	public static SVGModel svgModel;
	public static SVG svg;

	private static Node getFXgroup(G g, boolean isInRoot) {
		Group group = new Group();
		group.setId(g.getTitle().replace(".", "_"));
		
		if (g.getCustProps() != null) {
			HashMap<String, String> gData = new HashMap<>();
			g.getCustProps().getCustomProps().forEach(p -> {
				String nameCP = p.getNameU();
				gData.put(nameCP, p.getVal());
				if (p.getVal() != null) {
					if (nameCP.toLowerCase().equals("id") || nameCP.toLowerCase().equals("idts")) {
						listSignals.add(new LinkedValue(Integer.parseInt(p.getVal()), group.getId(), nameCP));
					}
				}
			}); 
			group.setUserData(gData);
		}
		
		boolean isInRootFinal = !isInRoot;
		List<G> listG = g.getListG();
		if (listG != null) {
			listG.forEach(itGroup -> {	
				Node n = getFXgroup(itGroup, isInRootFinal);
				group.setId("text".equals(n.getId()) ? n.getId() : null);
				group.getChildren().add(n);
			});
			if (isInRoot) {
				EShape eSh = new EShape(group, g);
				eSh.setId(g.getTitle().replace(".", "_"));
				return g.transformed(eSh, g.getTransform());
			} else {
				return g.transformed(group, g.getTransform());
			}
		} else {
			Node n = g.getNode(svg);
			group.getChildren().add(n);
			group.setId("text".equals(n.getId()) ? n.getId() : null);
			
			if (isInRoot) {
				EShape eSh = new EShape(group, g);
				eSh.setId(g.getTitle().replace(".", "_"));
				return eSh;
			} else {				
				return group;
			}
		}
	}
	
	public static Node getNodeBySVG(String filePath) {
		listSignals.clear();
		svgModel = SVGModel.getInstance();
		svg = svgModel.getSVG(filePath);
		if (svg == null) return new Group();
		
		svg.setFileName(filePath);
		
		try {
			idScheme = Integer.parseInt(svg.getTitle());
		} catch (Exception e) {
			LogFiles.log.log(Level.WARNING, "idScheme not set");
		}
		
		Group ret = new Group();
		setRootElement(ret);
		return ret;
	}
	
	public static Node getNodeBySVG(InputStream inputStream) {
		listSignals.clear();
		
		svgModel = SVGModel.getInstance();
		svg = svgModel.getSVG(inputStream);
		
		Group ret = new Group();
		setRootElement(ret);
		Platform.runLater(() -> ProgramProperty.schemeReadyProperty.set(true));
		return ret;
	}
	
	private static void setRootElement(Group ret) {
		svg.getG().forEach(g -> {
			Node gFX = getFXgroup(g, false);

			if (g.getTitle().toLowerCase().contains("background")) {
				try {
					Node r = gFX;
					while (!r.getClass().equals(Rectangle.class)) {
						r = ((Group) r).getChildren().get(0);
					}
					backgroundColor = ((Rectangle) r).getFill();
				} catch (Exception e) {
					LogFiles.log.log(Level.WARNING, "Can't find background");
				}
			} else {
				ret.getChildren().add(gFX);
			}
		});
	}

	public static List<LinkedValue> getListSignals() {
		return listSignals;
	}
}
