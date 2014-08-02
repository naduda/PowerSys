package topic;

import inter.IJMSConnection;

public class JMSConnection implements IJMSConnection{

	private String address;
	private String port;
	private String user;
	private String password;
	
	public JMSConnection(String address, String port, String user, String password) {
		this.address = address;
		this.port = port;
		this.user = user;
		this.password = password;
	}
	
	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getConnConfiguration() {
		String ret = String.format("mq://%s:%s", address, port);
		
		return ret + "," + ret;
	}

	@Override
	public String getPort() {
		return port;
	}

}
