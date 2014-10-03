package proxyPowersys;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import pr.powersys.IPowersys;
import jdbc.PostgresDB;

public class StartServer {
	
	public StartServer(PostgresDB pdb) {
		try {
			PowerSys ps = new PowerSys(pdb);
			Naming.rebind(IPowersys.bindString, ps);
			
			System.out.println("<PowerSysService> server is ready. " + IPowersys.bindString);
			
		} catch (RemoteException | MalformedURLException e) {
			System.out.println(e.getMessage());
		}
	}
}
