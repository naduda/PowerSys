package pr.svgObjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="svg", namespace=INamespaces.SVG)
public class SVG {
	@XmlTransient
	private String fileName;
	@XmlElement(name="title")
	private String title;
	@XmlElement(name="documentProperties", namespace=INamespaces.VISIO)
	private DocumentProperties documentProperties;
	@XmlAttribute(name="width")
	private String width;
	@XmlAttribute(name="height")
	private String height;
	@XmlAttribute(name="viewBox")
	private String viewBox;
	@XmlElement(name="style")
	private Style style;
	@XmlElement(name="defs")
	private Def defs;
	@XmlElement(name="g")
	private List<G> g;
	@XmlAttribute(name = "space", namespace=INamespaces.XML, required = true)
	private String space;
	@XmlAttribute(name="color-interpolation-filters")
	private String colorInterpolationFilters;
	@XmlAttribute(name="class")
	private String clazz;
	@XmlTransient
	private double fontSize = 10;
	@XmlTransient
	private Map<String, G> groupMap;
	
	public Map<String, G> getGroupMap() {
		if(groupMap == null) {
			groupMap = new HashMap<>();
			addGtoMap(groupMap, g);
		}
		return groupMap;
	}
	
	private void addGtoMap(Map<String, G> gMap, List<G> g) {
		if (g != null) {
			g.forEach(gr -> {
				gMap.put(gr.getId(), gr);
				addGtoMap(gMap, gr.getListG());
			});
		}
	}

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

	public String getColorInterpolationFilters() {
		return colorInterpolationFilters;
	}

	public void setColorInterpolationFilters(String colorInterpolationFilters) {
		this.colorInterpolationFilters = colorInterpolationFilters;
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public double getFontSize() {
		try {
			String styleMain = getStyleByName(clazz);
			String size = styleMain.substring(styleMain.indexOf("font-size:") + 10);
			if (size.contains(";")) {
				size = size.substring(0, size.indexOf(";")).replace("px", "").trim();
			} else {
				size = size.replace("px", "").trim();
			}
			fontSize = Double.parseDouble(size);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return fontSize;
	}

	public DocumentProperties getDocumentProperties() {
		return documentProperties;
	}

	public void setDocumentProperties(DocumentProperties documentProperties) {
		this.documentProperties = documentProperties;
	}

	public String getSpace() {
		return space;
	}

	public void setSpace(String space) {
		this.space = space;
	}

	public void setFontSize(double fontSize) {
		this.fontSize = fontSize;
	}
}