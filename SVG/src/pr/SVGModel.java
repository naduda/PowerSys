package pr;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.marshaller.DataWriter;

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

	private Object getObject(String xmlFilePath, Class<?> xmlClass) {
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
	
	private Object getObject(InputStream inputStream, Class<?> xmlClass) {
		Object result = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(xmlClass);
			
			SAXParserFactory spf = SAXParserFactory.newInstance();
			String FEATURE_NAMESPACES = "http://xml.org/sax/features/namespaces";
			spf.setFeature(FEATURE_NAMESPACES, true);
			String FEATURE_LOAD_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
			spf.setFeature(FEATURE_LOAD_DTD, false);

			XMLReader xmlReader = spf.newSAXParser().getXMLReader();

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
		Object obj = getObject(xmlFilePath, SVG.class);
		return obj == null ? null : (SVG)obj;
	}
	
	public SVG getSVG(InputStream inputStream) {
		Object obj = getObject(inputStream, SVG.class);
		return obj == null ? null : (SVG)obj;
	}
	
	public void setObject(String xmlFilePath, Object object) {
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();

			try {
				m.setProperty(CharacterEscapeHandler.class.getName(),
						(CharacterEscapeHandler) (ac, i, j, flag, writer) -> writer.write(ac, i, j));
			} catch (PropertyException e) {
				m.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler",
						(com.sun.xml.bind.marshaller.CharacterEscapeHandler) (ac, i, j, flag, writer) -> writer.write(ac, i, j));
			}
//			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//			String doctype = "<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 1.0//EN\" \"http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd\">";
//			m.setProperty("com.sun.xml.internal.bind.xmlHeaders", "\n" + doctype);
			
			m.marshal(object, new File(xmlFilePath));
		} catch (JAXBException e) {
			e.printStackTrace();
//			LogFiles.log.log(Level.SEVERE, "void setObject(...)", e);
		}
	}
	
	public ByteArrayOutputStream getOtputStream(Object object) {
		try(ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();

			try {
				m.setProperty(CharacterEscapeHandler.class.getName(),
						(CharacterEscapeHandler) (ac, i, j, flag, writer) -> writer.write(ac, i, j));
			} catch (PropertyException e) {
				m.setProperty("com.sun.xml.bind.marshaller.CharacterEscapeHandler",
						(com.sun.xml.bind.marshaller.CharacterEscapeHandler) (ac, i, j, flag, writer) -> writer.write(ac, i, j));
			}
			
			m.marshal(object, out);
			return out;
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String setObject(Object object) {
		try {
			JAXBContext jc = JAXBContext.newInstance(object.getClass());
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

			StringWriter stringWriter = new StringWriter();
			PrintWriter printWriter = new PrintWriter(stringWriter);
			DataWriter dataWriter = new DataWriter(printWriter, "UTF-8", new JaxbCharacterEscapeHandler());
			m.marshal(object, dataWriter);

			return stringWriter.toString();
		} catch (JAXBException e) {
			e.printStackTrace();
			System.out.println("err - 2");
		}
		return null;
	}
}
