package state;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "settings")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProgramSettings {
	@XmlElement(name = "windowstate")
	private WindowState winState;
	@XmlElement(name = "defaultScheme")
	private String defaultScheme;
	
	public ProgramSettings() {
		
	}
	
	public ProgramSettings(WindowState winState) {
		this.winState = winState;
	}
	
	public void setWindowState(WindowState bookList) {
		this.winState = bookList;
	}
	
	public WindowState getWinState() {
		return winState;
	}
	
	public String getDefaultScheme() {
		return defaultScheme;
	}

	public void setDefaultScheme(String defaultScheme) {
		this.defaultScheme = defaultScheme;
	}

	public void saveToFile(String fileName) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ProgramSettings.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		m.marshal(this, System.out);
		m.marshal(this, new File(fileName));
	}
	
	public static ProgramSettings getFromFile(String fileName) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(ProgramSettings.class);
		Unmarshaller um = context.createUnmarshaller();

		return (ProgramSettings) um.unmarshal(new FileReader(fileName));
	}
} 