package topic;

import java.util.logging.Level;

import pr.log.LogFiles;
import single.SingleFromDB;

public class ClientRMI {
	private static final int MAX_REPET = 3;
	private IClient client;
	private int count;
	
	public ClientRMI(IClient client) {
		this.client = client;
	}
	
	public Object get() {
		while (count < MAX_REPET) {
			try {
				return client.getResult(SingleFromDB.psClient.getMyServer());
			} catch (Exception e) {
				if (count == MAX_REPET - 1) {
					LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
				} else {
					LogFiles.log.log(Level.WARNING, "Try " + count);
				}
				SingleFromDB.psClient.setMyServer(null);
			}
			count++;
		}
		return null;
	}
}
