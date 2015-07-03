//@javax.xml.bind.annotation.XmlSchema(namespace = "http://www.w3.org/2000/svg", 
//elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)

@javax.xml.bind.annotation.XmlSchema (
	xmlns = { 
			@javax.xml.bind.annotation.XmlNs(prefix = "", namespaceURI="http://www.w3.org/2000/svg"),
			@javax.xml.bind.annotation.XmlNs(prefix = "v", namespaceURI="http://schemas.microsoft.com/visio/2003/SVGExtensions/"),
			@javax.xml.bind.annotation.XmlNs(prefix = "xlink", namespaceURI="http://www.w3.org/1999/xlink"),
			@javax.xml.bind.annotation.XmlNs(prefix = "ev", namespaceURI="http://www.w3.org/2001/xml-events")
	},
	namespace = "http://www.w3.org/2000/svg", 
	elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED,
	attributeFormDefault = javax.xml.bind.annotation.XmlNsForm.UNQUALIFIED)
package pr.svgObjects;