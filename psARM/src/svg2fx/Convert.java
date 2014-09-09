package svg2fx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;


import svg2fx.fxObjects.EShape;
import svg2fx.svgObjects.G;
import svg2fx.svgObjects.SVG;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class Convert extends Application {
	
	public static ScriptEngineManager manager = new ScriptEngineManager();
	public static ScriptEngine engine = manager.getEngineByName("JavaScript");
	public static List<LinkedValue> listSignals = new ArrayList<>();
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private static boolean wasInRoot = false;
	private static Node getFXgroup(G g, boolean isInRoot, SVG svg) {
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
		
		boolean isInRootFinal = !isInRoot && !wasInRoot;
		List<G> listG = g.getListG();
		if (listG != null) {
			listG.forEach(itGroup -> {	
				Node n = getFXgroup(itGroup, isInRootFinal, svg);
				group.setId("text".equals(n.getId()) ? n.getId() : null);
				group.getChildren().add(n);
			});
			if (isInRoot) {
				EShape eSh = new EShape(group);
				eSh.setTextShape("text".equals(group.getId()));
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
				EShape eSh = new EShape(group);
				eSh.setTextShape("text".equals(group.getId()));
				eSh.setId(g.getTitle().replace(".", "_"));
				return eSh;
			} else {				
				return group;
			}
		}
	}
	
	public static Node getNodeBySVG(String filePath) {
		EntityFromXML efx = new EntityFromXML();
		SVG svg = (SVG)efx.getObject(filePath, SVG.class);
		
		return getFXgroup(svg.getG(), false, svg);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		double start = System.currentTimeMillis();
		
		Node group = getNodeBySVG("d:/01_4.svg");
		
		Group g = new Group();
		g.getChildren().add(group);
		//group.getTransforms().add(new Scale(4, 4));
		
        ScrollPane sp = new ScrollPane();
        sp.setContent(g);
        
        primaryStage.setScene(new Scene(sp, 500, 500));
        primaryStage.show();
        System.out.println(System.currentTimeMillis() - start);
	}
}
