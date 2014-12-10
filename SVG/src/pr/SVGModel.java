package pr;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

import pr.log.LogFiles;
import pr.svgObjects.SVG;

public class SVGModel {
	private static SVGModel INSTANCE = null;
	
	public static SVGModel getInstance() {
		if (INSTANCE == null) {
			synchronized (SVGModel.class) {
				INSTANCE = new SVGModel();
			}
		}
		return INSTANCE;
	}

	public Object getObject(String xmlFilePath, Class<?> xmlClass) {
		Object result = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(xmlClass);
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
			spf.setFeature(FEATURE_NAMESPACES, true);
			String FEATURE_LOAD_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			spf.setFeature(FEATURE_LOAD_DTD, false);
	        
	        XMLReader xmlReader = spf.newSAXParser().getXMLReader();
	        
	        InputStream inputStream = new FileInputStream(xmlFilePath);
			
			Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
	        InputSource inputSource = new InputSource(reader);
	        inputSource.setEncoding("UTF-8");
	        SAXSource source = new SAXSource(xmlReader, inputSource);
	        
	        Unmarshaller um = jc.createUnmarshaller();
	        
			result = um.unmarshal(source);
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, "Object getObject(...)", e);
		}
		return result;
	}
	
	public SVG getSVG(String xmlFilePath) {
		return (SVG) getObject(xmlFilePath, SVG.class);
	}
	
	public void setObject(String xmlFilePath, Object object) {
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();
			m.setProperty(CharacterEscapeHandler.class.getName(),
	                new CharacterEscapeHandler() {
	                    @Override
	                    public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException {
	                        writer.write(ac, i, j);
	                    }
	                });
//			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			String doctype = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">";
//			m.setProperty("com.sun.xml.internal.bind.xmlHeaders", "\n" + doctype);
			
			m.marshal(object, new File(xmlFilePath));
		} catch (JAXBException e) {
			LogFiles.log.log(Level.SEVERE, "void setObject(...)", e);
		}
	}
}
