package topic;

import java.rmi.RemoteException;
import pr.powersys.IPowersys;

public interface IClient {
	Object getResult(IPowersys server) throws RemoteException;
}
