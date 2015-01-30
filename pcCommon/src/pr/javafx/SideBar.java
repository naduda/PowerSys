package pr.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {
	private double expandedSize;
	private boolean isSeted = false;
	private Node content;
	private Pos aligment;
	
	public SideBar() {
		super();
		setAlignment(Pos.CENTER);
		setPadding(new Insets(0, 0, 0, 0));
	}
	
	public SideBar(double prefSize, Node content) {
		this();
		expandedSize = prefSize;
		getChildren().addAll(content);
	}

	public double getExpandedSize() {
		return expandedSize;
	}

	public void setExpandedSize(double expandedSize) {
		this.expandedSize = expandedSize;
	}

	public void setSeted(boolean isSeted) {
		this.isSeted = isSeted;
	}

	public boolean isSeted() {
		return isSeted;
	}

	public Node getContent() {
		return content;
	}

	public void setContent(Node content) {
		this.content = content;
		getChildren().addAll(content);
	}

	public Pos getAligment() {
		return aligment;
	}

	public void setAligment(Pos aligment) {
		this.aligment = aligment;
		setAlignment(aligment);
	}
}
