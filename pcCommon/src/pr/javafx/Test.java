package pr.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Test extends Application {

	@Override
	public void start(Stage stage) throws Exception {
		stage.setTitle("Side demo");
		
		SideBar sidebar1 = new SideBar(250, "Collapse", "Show", new Label("SideBar_1"));
		SideBar sidebar2 = new SideBar(250, "Collapse", "Show", new Label("SideBar_2"));
		SideBar sidebar3 = new SideBar(250, "Collapse", "Show", new Label("SideBar_3"));
		SideBar sidebar4 = new SideBar(250, "Collapse", "Show", new Label("SideBar_4"));
		
		SidePane sp1 = new SidePane(false, "top", sidebar1, new Label("cont1"));
		SidePane sp2 = new SidePane(true, "bottom", sidebar2, new Label("cont2"));
		SidePane sp3 = new SidePane(true, "left", sidebar3, new Label("cont3"));
		SidePane sp4 = new SidePane(true, "right", sidebar4, new Label("cont4"));
		
		BorderPane bp = new BorderPane();
		bp.setTop(sp1);
		bp.setBottom(sp2);
		bp.setLeft(sp3);
		bp.setRight(sp4);
		
		VBox vb = new VBox();
		vb.getChildren().addAll(sidebar1.getControlButton(), sidebar2.getControlButton(),
				sidebar3.getControlButton(), sidebar4.getControlButton());
		bp.setCenter(vb);
		
		Scene scene = new Scene(bp);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
