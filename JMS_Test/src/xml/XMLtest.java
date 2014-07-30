package xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLtest {

	public static void main(String[] args) {
		Object result = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(Document.class);
			Unmarshaller u = jc.createUnmarshaller();
			result = u.unmarshal(new FileInputStream(new File("d:/export.xml")));
		} catch (JAXBException e) {
			System.err.println("Error in EntityFromXML.getObject(...). JAXBException: " + e);
		} catch (FileNotFoundException ex) {
			System.err.println("Error in EntityFromXML.getObject(...). FileNotFoundException: " + ex);
		}
		Document doc = (Document) result;
		System.out.println(doc.getPage().getName());
		System.out.println(doc.getPage().getShapes().get(8).getShapes().get(1).getX());
	}

}
