package pr.svgObjects;

import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="svg", namespace="http://www.w3.org/2000/svg")
public class SVG {
	@XmlAttribute(name="width")
	private String width;
	@XmlAttribute(name="height")
	private String height;
	@XmlAttribute(name="viewBox")
	private String viewBox;
	@XmlElement(name="title")
	private String title;
	@XmlElement(name="style")
	private Style style;
	@XmlElement(name="defs")
	private Def defs;
	@XmlElement(name="g")
	private List<G> g;

	public String getWidth() {
		if (width.toLowerCase().endsWith("in")) {
			width = width.replace("in", "").trim();
			width = Double.parseDouble(width) * 72 + "";
		}
		return width;
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
	public String getHeight() {
		if (height.toLowerCase().endsWith("in")) {
			height = height.replace("in", "").trim();
			height = Double.parseDouble(height) * 72 + "";
		}
		return height;
	}
	
	public void setHeight(String height) {
		this.height = height;
	}
	
	public String getViewBox() {
		return viewBox;
	}

	public void setViewBox(String viewBox) {
		this.viewBox = viewBox;
	}

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}
	
	public String getStyleByName(String name) {
		HashMap<String, String> styles = new HashMap<>();
		StringTokenizer st = new StringTokenizer(style.getContent(), "}");
		while (st.hasMoreElements()) {
			String elem = st.nextElement().toString().trim();
			if (elem.contains("{")) {
				String[] elems = elem.split("\\{");
				styles.put(elems[0].substring(1).trim(), elems[1]);
			}
		}
		return styles.get(name);
	}

	public Def getDefs() {
		return defs;
	}

	public void setDefs(Def defs) {
		this.defs = defs;
	}

	public List<G> getG() {
		return g;
	}

	public void setG(List<G> g) {
		this.g = g;
	}
}