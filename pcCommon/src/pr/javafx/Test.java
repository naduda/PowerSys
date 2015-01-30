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
		
		SideBar sidebar1 = new SideBar(25, new Label("SideBar_1"));
		SideBar sidebar2 = new SideBar(50, new Label("SideBar_2"));
		SideBar sidebar3 = new SideBar(100, new Label("SideBar_3"));
		SideBar sidebar4 = new SideBar(100, new Label("SideBar_4"));
				
		VBox vb = new VBox();
				
		SidePane sp3 = new SidePane(true, "left", sidebar3, vb);
		SidePane sp4 = new SidePane(true, "right", sidebar4, sp3);
		
		SidePane sp1 = new SidePane(false, "top", sidebar1, sp4);
		sidebar1.setStyle("-fx-background-color: #336000;");
		
//		SidePane sp2 = new SidePane(true, "bottom", sidebar2, sp1);
		SidePane sp2 = new SidePane(true, "bottom");
		sp2.setSideBar(sidebar2);
		sp2.setContent(sp1);
		
		vb.getChildren().addAll(sp1.getControlButton("collapse", "show"),sp2.getControlButton("collapse", "show"),
				sp3.getControlButton("collapse", "show"),sp4.getControlButton("collapse", "show"));
		
		Scene scene = new Scene(new BorderPane(sp2), 600, 300);
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
