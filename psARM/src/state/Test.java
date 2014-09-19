package state;

import java.io.IOException;
import javax.xml.bind.JAXBException;

public class Test {
	private static final String BOOKSTORE_XML = "./bookstore-jaxb.xml";
	
	public static void main(String[] args) throws JAXBException, IOException {
	    WindowState book1 = new WindowState(100,200,300,400);
	    
	    ProgramSettings bookstore = new ProgramSettings();
	    bookstore.setWindowState(book1);
	    
	    bookstore.saveToFile(BOOKSTORE_XML);
	    
	    ProgramSettings bookstore2 = ProgramSettings.getFromFile(BOOKSTORE_XML);
	    
	    WindowState list = bookstore2.getWinState();
	    System.out.println("Book: " + list.getX() + " from "
		          + list.getWidth());
	  }
}
