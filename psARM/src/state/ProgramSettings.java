package state;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

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
	@XmlElement(name = "schemeSettings")
	private SchemeSettings schemeSettings;
	private String localeName;
	private String showAlarmColumns;
	private int iconWidth;
	
	public ProgramSettings() {
		
	}
	
	public ProgramSettings(WindowState winState) {
		this.winState = winState;
	}
	
	public void setWindowState(WindowState winState) {
		this.winState = winState;
	}
	
	public WindowState getWinState() {
		return winState;
	}

	public SchemeSettings getSchemeSettings() {
		return schemeSettings;
	}

	public void setSchemeSettings(SchemeSettings schemeSettings) {
		this.schemeSettings = schemeSettings;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

	public String getShowAlarmColumns() {
		return showAlarmColumns;
	}

	public void setShowAlarmColumns(String showAlarmColumns) {
		this.showAlarmColumns = showAlarmColumns;
	}
	
	public int getIconWidth() {
		return iconWidth;
	}

	public void setIconWidth(int iconWidth) {
		this.iconWidth = iconWidth;
	}

	public void saveToFile(String fileName) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(ProgramSettings.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		//m.marshal(this, System.out);
		m.marshal(this, new File(fileName));
	}
	
	public static ProgramSettings getFromFile(String fileName) throws JAXBException, FileNotFoundException {
		JAXBContext context = JAXBContext.newInstance(ProgramSettings.class);
		Unmarshaller um = context.createUnmarshaller();
		InputStream inputStream = new FileInputStream(fileName);
		
		Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

		return (ProgramSettings) um.unmarshal(reader);
	}
} 