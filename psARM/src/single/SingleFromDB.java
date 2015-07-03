package single;

import java.io.File;
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
import pr.model.Tconftree;
import pr.model.Transparant;
import pr.model.Tscheme;
import pr.model.Tsignal;
import pr.model.Tuser;
import pr.model.VsignalView;
import topic.ClientPowerSys;

public class SingleFromDB {
	public static ClientPowerSys psClient;
	
	public static Map<Integer, Tscheme> schemesMap;
	public static Map<Integer, VsignalView> signals;
	public static Map<Integer, Tconftree> tConftree;
	public static Map<Integer, Tsignal> tsignals;
	public static Map<Integer, Tuser> users;
	public static Tuser curentUser;
	public static Map<Integer, Transparant> transpMap;
	public static List<SpTuCommand> spTuCommands;
	public static Map<Integer, SpTypeSignal> spTypeSignals;
	public static int validTimeOutTI;
	public static int validTimeOutTS;
	public static File svgFile;
	public static String sqlConnectParameters;
	
	public Collection<DvalTS> oldTS;
	public Collection<DvalTI> oldTI;
	
	@SuppressWarnings({ "static-access", "unchecked" })
	public SingleFromDB(ClientPowerSys psClient) {
		this.psClient = psClient;
		
		long start = System.currentTimeMillis();
		LogFiles.log.log(Level.INFO, "Start reading DB");
		Map<String, Future<Object>> futures = new HashMap<>();
		Map<String, Object> results = new HashMap<>();
		
		final ExecutorService service = Executors.newFixedThreadPool(9);
		
		try {
			futures.put("sqlConnectParameters", service.submit(() -> psClient.getDBparameters()));
			futures.put("tConftree", service.submit(() -> psClient.getTconftreeMap()));
			futures.put("signals", service.submit(() -> psClient.getVsignalViewMap()));
			futures.put("tsignals", service.submit(() -> psClient.getTsignalsMap()));
			futures.put("users", service.submit(() -> psClient.getTuserMap()));
			futures.put("transpMap", service.submit(() -> psClient.getTransparants()));
			futures.put("spTuCommands", service.submit(() -> psClient.getSpTuCommand()));
			futures.put("spTypeSignals", service.submit(() -> psClient.getSpTypeSignalMap()));
			futures.put("schemesMap", service.submit(() -> psClient.getSchemesMap()));
			
			for (String k : futures.keySet()) {
				Future<Object> f = futures.get(k);
				results.put(k, f.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			LogFiles.log.log(Level.SEVERE, e.getMessage(), e);
		} finally {
			service.shutdown();
		}
		
		sqlConnectParameters = results.get("sqlConnectParameters").toString();
		signals = (Map<Integer, VsignalView>) results.get("signals");
		tConftree = (Map<Integer, Tconftree>) results.get("tConftree");
		tsignals = (Map<Integer, Tsignal>) results.get("tsignals");
		users = (Map<Integer, Tuser>) results.get("users");
		transpMap = (Map<Integer, Transparant>) results.get("transpMap");
		spTuCommands = (List<SpTuCommand>) results.get("spTuCommands");
		spTypeSignals = (Map<Integer, SpTypeSignal>) results.get("spTypeSignals");
		schemesMap = (Map<Integer, Tscheme>) results.get("schemesMap");
		
		validTimeOutTI = Integer.parseInt(psClient.getTSysParam("OBJ_TI_TIMEOUT").values().iterator().next().getVal());
		validTimeOutTS = Integer.parseInt(psClient.getTSysParam("OBJ_TS_TIMEOUT").values().iterator().next().getVal());
//				Map<String, TSysParam> modes = SingleFromDB.psClient.getTSysParam("SIGNAL_STATUS");
		
		LogFiles.log.log(Level.INFO, "Finish reading DB - " + (System.currentTimeMillis() - start) / 1000 + " s");
	}
}
