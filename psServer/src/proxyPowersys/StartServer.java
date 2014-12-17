package proxyPowersys;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.powersys.IPowersys;
import pr.powersys.MySocketFactory;

public class StartServer {
	
	public StartServer() {
		try {
			PowerSys ps = new PowerSys();
			String rmiString = String.format("rmi://%s:%s/PowerSysService", "127.0.0.1", IPowersys.RMI_PORT);
			
			RMISocketFactory sf = new MySocketFactory();
			UnicastRemoteObject.unexportObject(ps, true);
			Remote stub = UnicastRemoteObject.exportObject(ps, 0, sf, sf);
			LocateRegistry.createRegistry(IPowersys.RMI_PORT);
			Naming.rebind(rmiString, stub);
			
			LogFiles.log.log(Level.INFO, "<PowerSysService> is ready. " + rmiString);
		} catch (RemoteException | MalformedURLException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		}
	}
}
