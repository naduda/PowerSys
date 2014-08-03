package proxyPowersys;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;

import jdbc.PostgresDB;

public class StartServer {
	
	public StartServer(PostgresDB pdb) {
		try {			   
		    PowerSys ps = new PowerSys(pdb);
		    Naming.rebind("rmi://localhost:1099/PowerSysService", ps);

		    System.out.println("<PowerSysService> server is ready.");

		   }catch (MalformedURLException e1){
			  System.out.println(e1.getMessage());   
		   }catch(RemoteException ex) {
			   ex.printStackTrace();
		   }
	}
}
