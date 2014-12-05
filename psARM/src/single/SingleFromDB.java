package single;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;

import pr.log.LogFiles;
import pr.model.DvalTI;
import pr.model.DvalTS;
import pr.model.SpTuCommand;
import pr.model.SpTypeSignal;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.Tuser;
import pr.model.VsignalView;
import topic.ClientPowerSys;

public class SingleFromDB {
	public static ClientPowerSys psClient;
	
	public static Map<Integer, VsignalView> signals;
	public static Map<Integer, Tsignal> tsignals;
	public static Map<Integer, Tuser> users;
	public static Map<Integer, Transparant> transpMap;
	public static List<SpTuCommand> spTuCommands;
	public static Map<Integer, SpTypeSignal> spTypeSignals;
	
	public Collection<DvalTS> oldTS;
	public Collection<DvalTI> oldTI;
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public SingleFromDB(ClientPowerSys psClient) {
		this.psClient = psClient;
		
		long start = System.currentTimeMillis();
		LogFiles.log.log(Level.INFO, "Start reading DB");
		Map<String, Future<Object>> futures = new HashMap<>();
		Map<String, Object> results = new HashMap<>();
		
		final ExecutorService service = Executors.newFixedThreadPool(6);
		
		try {
			futures.put("signals", service.submit(() -> psClient.getVsignalViewMap()));
			futures.put("tsignals", service.submit(() -> psClient.getTsignalsMap()));
			futures.put("users", service.submit(() -> psClient.getTuserMap()));
			futures.put("transpMap", service.submit(() -> psClient.getTransparants()));
			futures.put("spTuCommands", service.submit(() -> psClient.getSpTuCommand()));
			futures.put("spTypeSignals", service.submit(() -> psClient.getSpTypeSignalMap()));
			
			for (String k : futures.keySet()) {
				Future<Object> f = futures.get(k);
				results.put(k, f.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			service.shutdown();
		}
		
		signals = (Map<Integer, VsignalView>) results.get("signals");
		tsignals = (Map<Integer, Tsignal>) results.get("tsignals");
		users = (Map<Integer, Tuser>) results.get("users");
		transpMap = (Map<Integer, Transparant>) results.get("transpMap");
		spTuCommands = (List<SpTuCommand>) results.get("spTuCommands");
		spTypeSignals = (Map<Integer, SpTypeSignal>) results.get("spTypeSignals");
		
		LogFiles.log.log(Level.INFO, "Finish reading DB - " + (System.currentTimeMillis() - start) / 1000 + " s");
	}
}
