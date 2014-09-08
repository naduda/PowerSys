package svg2fx.svgObjects;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class G extends AClassSVG {
	@XmlAttribute(name="id")
	private String id;
	@XmlAttribute(name="transform")
	private String transform;
	@XmlElement(name="title", namespace="http://www.w3.org/2000/svg")
	private String title;
	@XmlElement(name="g", namespace="http://www.w3.org/2000/svg")
	private List<G> listG;
	@XmlElement(name="path", namespace="http://www.w3.org/2000/svg")
	private List<PathSVG> lPath;
	@XmlElement(name="rect", namespace="http://www.w3.org/2000/svg")
	private List<RectSVG> lRect;
	@XmlElement(name="text", namespace="http://www.w3.org/2000/svg")
	private List<TextSVG> lText;
	@XmlElement(name="ellipse", namespace="http://www.w3.org/2000/svg")
	private List<EllipseSVG> lEllipse;
	@XmlElement(name="text", namespace="http://www.w3.org/2000/svg")
	private List<LineSVG> lLine;
	@XmlElement(name="custProps", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private CustProps custProps;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<EllipseSVG> getlEllipse() {
		return lEllipse;
	}

	public void setlEllipse(List<EllipseSVG> lEllipse) {
		this.lEllipse = lEllipse;
	}

	public List<LineSVG> getlLine() {
		return lLine;
	}

	public void setlLine(List<LineSVG> lLine) {
		this.lLine = lLine;
	}

	public String getTransform() {
		return transform;
	}

	public void setTransform(String transform) {
		this.transform = transform;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<G> getListG() {
		return listG;
	}

	public void setListG(List<G> lg) {
		this.listG = lg;
	}

	public List<PathSVG> getlPath() {
		return lPath;
	}

	public void setlPath(List<PathSVG> lPath) {
		this.lPath = lPath;
	}

	public List<RectSVG> getlRect() {
		return lRect;
	}

	public void setlRect(List<RectSVG> lRect) {
		this.lRect = lRect;
	}

	public List<TextSVG> getlText() {
		return lText;
	}

	public void setlText(List<TextSVG> lText) {
		this.lText = lText;
	}

	public CustProps getCustProps() {
		return custProps;
	}

	public void setCustProps(CustProps custProps) {
		this.custProps = custProps;
	}

	@Override
	public Node getNode(SVG svg) {
		Group group = new Group();
		
		List<EllipseSVG> ellipses = getlEllipse();
		if (ellipses != null) {
			ellipses.forEach(e -> {
				group.getChildren().add(e.getNode(svg));
			});
		}
		
		List<LineSVG> lines = getlLine();
		if (lines != null) {
			lines.forEach(e -> {
				group.getChildren().add(e.getNode(svg));
			});
		}
		
		List<PathSVG> pathes = getlPath();
		if (pathes != null) {
			pathes.forEach(e -> {
				group.getChildren().add(e.getNode(svg));
			});
		}
		
		List<RectSVG> rects = getlRect();
		if (rects != null) {
			rects.forEach(e -> {
				group.getChildren().add(e.getNode(svg));
			});
		}
		
		List<TextSVG> texts = getlText();
		if (texts != null) {
			texts.forEach(e -> {
				group.getChildren().add(e.getNode(svg));
			});
		}
		
		if (group.getChildren().size() == 1) {
			return transformed(group.getChildren().get(0), getTransform());
		} else {
			return transformed(group, getTransform());
		}
	}
}
