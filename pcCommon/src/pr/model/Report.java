package pr.model;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Report implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private File template;
	private Map<String, String> queries = new HashMap<>();
	
	public Report() {
		
	}

	public File getTemplate() {
		return template;
	}

	public void setTemplate(File template) {
		this.template = template;
	}

	public Map<String, String> getQueries() {
		return queries;
	}

	public void setQueries(Map<String, String> queries) {
		this.queries = queries;
	}
}
