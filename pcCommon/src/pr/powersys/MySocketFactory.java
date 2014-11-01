package pr.powersys;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMISocketFactory;

public class MySocketFactory extends RMISocketFactory implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static String server = "localhost";

	@Override
	public ServerSocket createServerSocket(int port) throws IOException {
        return getFactory().createServerSocket(port);
	}

	@Override
	public Socket createSocket(String host, int port) throws IOException {
        return getFactory().createSocket(server, port);
	}
	
	private RMISocketFactory getFactory() {
        return RMISocketFactory.getDefaultSocketFactory();
    }
	
	public static void setServer(String host) {
        server = host;
    }
}
