package single;

public class SQLConnect {
	private String ipAddress;
	private String port;
	private String dbName;
	private String user;
	private String password;
	
	public SQLConnect(String ipAddress, String port, String dbName, String user, String password) {
		this.ipAddress = ipAddress;
		this.port = port;
		this.dbName = dbName;
		this.user = user;
		this.password = password;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
