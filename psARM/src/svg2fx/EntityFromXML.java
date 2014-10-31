package svg2fx;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class EntityFromXML {
	private final String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private final String FEATURE_LOAD_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	public Object getObject(String xmlFilePath, Class<?> xmlClass) {
		Object result = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(xmlClass);
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
	        spf.setFeature(FEATURE_NAMESPACES, true);
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
			System.err.println("Error in EntityFromXML.getObject(...). JAXBException: " + e);
		}
		return result;
	}
	
	public void setObject(String xmlFilePath, Object object) {
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();
			m.marshal(object, new File(xmlFilePath));
		} catch (JAXBException e) {
			System.err.println("Error in EntityFromXML.setObject(...). JAXBException: " + e);
		}
	}
}
