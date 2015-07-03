package reports;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
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
import pr.powersys.ObjectSerializable;
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
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleHtmlExporterOutput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

public class ReportTools {
	private SQLConnect sqlConnect;
	
	public ReportTools(SQLConnect sqlConnect) {
		this.sqlConnect = sqlConnect;
	}
	
	
	public ObjectSerializable getReportById(int idReport, LocalDate dtBeg, LocalDate dtEnd, String format) {
		String pattern = "YYYY-MM-dd";
		Report r = SingleFromDB.getReports().get(idReport);
		if (r == null) return null;
		try (Connection conn = getConnection(); 
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				FileInputStream fis = new FileInputStream(r.getTemplate());) {
			
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
			
			switch (format.toLowerCase()) {
			case "html":
				HtmlExporter exporter = new HtmlExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleHtmlExporterOutput(bos));
				exporter.exportReport();
				break;
			case "xls":
				JRXlsExporter exporterXLS = new JRXlsExporter();
				exporterXLS.setExporterInput(new SimpleExporterInput(jp));
				exporterXLS.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				//configuration.setOnePagePerSheet(true);
				configuration.setDetectCellType(true);
				configuration.setCollapseRowSpan(false);
				exporterXLS.setConfiguration(configuration);
				exporterXLS.exportReport();
				break;
			case "pdf":
				JRPdfExporter exporterPDF = new JRPdfExporter();
				exporterPDF.setExporterInput(new SimpleExporterInput(jp));
				exporterPDF.setExporterOutput(new SimpleOutputStreamExporterOutput(bos));
				exporterPDF.exportReport();
				break;
			}
			
			ObjectSerializable os = new ObjectSerializable();
			os.setBaosSerializable(bos.toByteArray());
			return os;
		} catch (JRException | SQLException | IOException e) {
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
