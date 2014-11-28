package reports;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.Report;
import single.SQLConnect;
import single.SingleFromDB;
import net.sf.jasperreports.engine.JRDataset;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JRDesignDataset;
import net.sf.jasperreports.engine.design.JRDesignQuery;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;

public class ReportTools {
	private SQLConnect sqlConnect;
	
	public ReportTools(SQLConnect sqlConnect) {
		this.sqlConnect = sqlConnect;
	}
	
	public String getReportById(int idReport, LocalDate dtBeg, LocalDate dtEnd) {
		String pattern = "yyyy-MM-dd";
		Report r = SingleFromDB.getReports().get(idReport);
		if (r == null) return null;
		try (Connection conn = getConnection();) {
			FileInputStream fis = new FileInputStream(r.getTemplate());
			JasperDesign design = JRXmlLoader.load(fis);
			Map<String, JRDataset> datasetMap = design.getDatasetMap();
			
			r.getQueries().keySet().forEach(k -> {
				JRDesignQuery query = new JRDesignQuery();
				String qString = r.getQueries().get(k);
				qString = qString.replace("dtBeg", dtBeg.format(DateTimeFormatter.ofPattern(pattern)))
						.replace("dtEnd", dtEnd.format(DateTimeFormatter.ofPattern(pattern)));
				query.setText(qString);
				
				if (k.toString().startsWith("ds")) {
					((JRDesignDataset)datasetMap.get(k)).setQuery(query);
				} else {
					design.setQuery(query);
				}
			});
			
			JasperReport rep = JasperCompileManager.compileReport(design);
			JasperPrint jp = JasperFillManager.fillReport(rep, null, conn);
			
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			HtmlExporter exporter = new HtmlExporter();
			exporter.setExporterInput(new SimpleExporterInput(jp));
			exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));
			
			exporter.exportReport();
			try (BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(bos.toByteArray()), "UTF-8"));) {
		        StringBuilder sb = new StringBuilder();
		        String line = br.readLine();

		        while (line != null) {
		            sb.append(line);
		            sb.append(System.lineSeparator());
		            line = br.readLine();
		        }
		        String everything = sb.toString();
		        return everything;
		    }
		} catch (JRException | SQLException | IOException e) {
			e.printStackTrace();
			LogFiles.log.log(Level.SEVERE, "getReportById(...)", e);
		}
		return null;
	}
	
	private Connection getConnection() throws SQLException {
	    Properties connectionProps = new Properties();
	    connectionProps.put("user", sqlConnect.getUser());
	    connectionProps.put("password", sqlConnect.getPassword());
	    
	    String connString = "jdbc:postgresql://" + sqlConnect.getIpAddress() + 
	    		":" + sqlConnect.getPort() + "/" + sqlConnect.getDbName();
	    Connection conn = DriverManager.getConnection(connString, connectionProps);
	    
	    return conn;
	}
}
