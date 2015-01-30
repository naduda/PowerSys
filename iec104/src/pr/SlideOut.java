package pr;

import javafx.animation.*;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Duration;
 
public class SlideOut extends Application {
  WebView webView;
  public static void main(String[] args) throws Exception { launch(args); }
  public void start(final Stage stage) throws Exception {
    stage.setTitle("Slide out YouTube demo");
 
    // create a WebView to show to the right of the SideBar.
    webView = new WebView();
    webView.setPrefSize(800, 300);
 
    // create a sidebar with some content in it.
    final Pane lyricPane = createSidebarContent();
    SideBar sidebar = new SideBar(250, lyricPane, new Label("qwe"));
    SideBar sidebar2 = new SideBar(250, new Label("qwe2"));
    VBox.setVgrow(lyricPane, Priority.ALWAYS);
     
    VBox mainPane = new VBox();
    mainPane.getChildren().addAll(sidebar.getControlButton(), sidebar2.getControlButton(), webView);
    
    SplitPane sp = new SplitPane();
    sp.getItems().addAll(sidebar, mainPane, sidebar2);
    
    sidebar.prefWidthProperty().addListener((observ, old, newValue) -> {
    	sp.setDividerPosition(0, round((double)newValue / sp.getWidth()));
    });
    
    sidebar2.prefWidthProperty().addListener((observ, old, newValue) -> {
    	sp.setDividerPosition(1, round(1 - (double)newValue / sp.getWidth()));
    });
    
    Scene scene = new Scene(sp);
    stage.setScene(scene);
    stage.show();
  }
 
  private double round(double value) {
	  value = Math.round(value * 100);
	  return value/100;
	}
  
  private BorderPane createSidebarContent() {
    final Text lyric = new Text("rew");
    final Button changeLyric = new Button("New Song");
    changeLyric.setMaxWidth(Double.MAX_VALUE);
    final BorderPane lyricPane = new BorderPane();
    lyricPane.setCenter(lyric);
    lyricPane.setBottom(changeLyric);
    return lyricPane;
  }
 
  class SideBar extends VBox {
	  public Button getControlButton() { return controlButton; }
	  private final Button controlButton;
	  private double expandedWidth;
	  private boolean isButton = false;
	  
	  public SideBar(double expandedWidth2, Node... nodes) {
		  expandedWidth = expandedWidth2;
		  this.setPrefWidth(expandedWidth);
		  this.setMinWidth(0);
		  
		  widthProperty().addListener((observ, old, newValue) -> {
			  setPrefWidth((double)newValue);
			  if (!isButton && (double)newValue != 0) {
				  expandedWidth = (double)newValue;
			  }
		});
      
      setAlignment(Pos.CENTER);
      getChildren().addAll(nodes);
      
      controlButton = new Button("Collapse");
      controlButton.getStyleClass().add("hide-left");
      
      controlButton.setOnAction(e -> {

    	  isButton = true;
    	  controlButton.setDisable(true);
    	  final Animation hideSidebar = new Transition() {
              { setCycleDuration(Duration.millis(250)); }
              protected void interpolate(double frac) {
                final double curWidth = expandedWidth * (1.0 - frac);
                setPrefWidth(curWidth);
                setTranslateX(-expandedWidth + curWidth);
              }
            };
            hideSidebar.onFinishedProperty().set(eah -> {
                setVisible(false);
                controlButton.setText("Show");
                controlButton.getStyleClass().remove("hide-left");
                controlButton.getStyleClass().add("show-right");
                isButton = false;
                controlButton.setDisable(false);
            });
            
            final Animation showSidebar = new Transition() {
              { setCycleDuration(Duration.millis(250)); }
              protected void interpolate(double frac) {
                final double curWidth = expandedWidth * frac;
                setPrefWidth(curWidth);
                setTranslateX(-expandedWidth + curWidth);
              }
            };
            
            showSidebar.onFinishedProperty().set(eaf -> {
                controlButton.setText("Collapse");
                controlButton.getStyleClass().add("hide-left");
                controlButton.getStyleClass().remove("show-right");
                isButton = false;
                controlButton.setDisable(false);
            });
    
            //if (showSidebar.statusProperty().get() == Animation.Status.STOPPED && hideSidebar.statusProperty().get() == Animation.Status.STOPPED) {
            	if (isVisible()) {
	                hideSidebar.play();
              } else {
                setVisible(true);
                showSidebar.play();
              }
              
           // }
      });
      
    }
  }
  
}
