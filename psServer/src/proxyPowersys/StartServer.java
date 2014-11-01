package proxyPowersys;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.RMISocketFactory;
import java.rmi.server.UnicastRemoteObject;

import pr.powersys.IPowersys;
import pr.powersys.MySocketFactory;
import jdbc.PostgresDB;

public class StartServer {
	
	public StartServer(PostgresDB pdb) {
		try {
			PowerSys ps = new PowerSys(pdb);
			String rmiString = String.format("rmi://%s:%s/PowerSysService", "0.0.0.0", IPowersys.RMI_PORT);
			
			RMISocketFactory sf = new MySocketFactory();
			UnicastRemoteObject.unexportObject(ps, true);
			Remote stub = UnicastRemoteObject.exportObject(ps, 0, sf, sf);
			LocateRegistry.createRegistry(IPowersys.RMI_PORT);
			Naming.rebind(rmiString, stub);
			
			System.out.println("<PowerSysService> is ready. " + rmiString);
			
		} catch (RemoteException | MalformedURLException e) {
			System.out.println(e.getMessage());
		}
	}
}
