package topic;

import java.util.logging.Level;

import pr.log.LogFiles;
import single.SingleFromDB;

public class ClientRMI {
	private IClient client;
	
	public ClientRMI(IClient client) {
		this.client = client;
	}
	
	public Object get() {
		try {
			return client.getResult(SingleFromDB.psClient.getMyServer());
		} catch (Exception e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
			SingleFromDB.psClient.setMyServer(null);
			return null;
		}
	}
}
