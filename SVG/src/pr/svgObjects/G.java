package pr.svgObjects;

import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="g", namespace="http://www.w3.org/2000/svg")
public class G extends AClassSVG {
	@XmlElement(name="userDefs", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private UserDefs userDefs;
	@XmlElement(name="title")
	private String title;
	@XmlElement(name="desc")
	private String desc;
	@XmlElement(name="pageProperties", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private PageProperties pageProperties;
	
	@XmlElement(name="textBlock", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private TextBlock textBlock;
	@XmlElement(name="textRect", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private TextRect textRect;
	@XmlElement(name="layer", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Layer layer;
	@XmlAttribute(name="id")
	private String id;
	@XmlElement(name="g")
	private List<G> listG;
	@XmlElement(name="path")
	private List<PathSVG> lPath;
	@XmlElement(name="rect")
	private List<RectSVG> lRect;
	@XmlElement(name="text")
	private List<TextSVG> lText;
	@XmlElement(name="ellipse")
	private List<EllipseSVG> lEllipse;
	@XmlElement(name="text")
	private List<LineSVG> lLine;
	@XmlElement(name="custProps", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private CustProps custProps;
	@XmlAttribute(name="mID", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Integer mID;
	@XmlAttribute(name="groupContext", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String groupContext;
	@XmlAttribute(name="layerMember", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private String layerMember;
	@XmlAttribute(name="index", namespace="http://schemas.microsoft.com/visio/2003/SVGExtensions/")
	private Integer index;
	@XmlAttribute(name="transform")
	private String transform;
	
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
			ellipses.forEach(e -> group.getChildren().add(e.getNode(svg)));
		}
		
		List<LineSVG> lines = getlLine();
		if (lines != null) {
			lines.forEach(e -> group.getChildren().add(e.getNode(svg)));
		}
		
		List<PathSVG> pathes = getlPath();
		if (pathes != null) {
			pathes.forEach(e -> group.getChildren().add(e.getNode(svg)));
		}
		
		List<RectSVG> rects = getlRect();
		if (rects != null) {
			rects.forEach(e -> group.getChildren().add(e.getNode(svg)));
		}
		
		List<TextSVG> texts = getlText();
		if (texts != null) {
			texts.forEach(e -> group.getChildren().add(e.getNode(svg)));
			group.setId("text");
		}
		
		if (group.getChildren().size() == 1) {
			return transformed(group.getChildren().get(0), getTransform());
		} else {
			return transformed(group, getTransform());
		}
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public TextBlock getTextBlock() {
		return textBlock;
	}

	public void setTextBlock(TextBlock textBlock) {
		this.textBlock = textBlock;
	}

	public TextRect getTextRect() {
		return textRect;
	}

	public void setTextRect(TextRect textRect) {
		this.textRect = textRect;
	}
}
