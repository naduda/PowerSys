package state;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override public void start(Stage stage) {
        Group group = new Group();
        Scene scene = new Scene(group); 

        SplitPane sp = new SplitPane();
        final StackPane sp1 = new StackPane();
        sp1.getChildren().add(new Button("Button One"));
        final StackPane sp2 = new StackPane();
        sp2.getChildren().add(new Button("Button Two"));
        final StackPane sp3 = new StackPane();
        sp3.getChildren().add(new Button("Button Three"));
        sp.getItems().addAll(sp1, sp2, sp3);
        System.out.println(sp.getDividers().size());
        sp.setDividerPositions(0.3f, 0.6f, 0.9f);
        
        group.getChildren().add(sp);
        
        stage.setTitle("Welcome to JavaFX!"); 
        stage.setScene(scene); 
        stage.sizeToScene(); 
        stage.show(); 
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}
