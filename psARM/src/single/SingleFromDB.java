package single;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javafx.scene.image.Image;
import pr.common.WMF2PNG;
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
	
	private static Map<Integer, VsignalView> signals;
	private static Map<Integer, Tsignal> tsignals;
	private static Map<Integer, Tuser> users;
	private static Map<Integer, Transparant> transpMap;
	private static List<SpTuCommand> spTuCommands;
	private static Map<Integer, SpTypeSignal> spTypeSignals;
	private Collection<DvalTS> oldTS;
	private Collection<DvalTI> oldTI;
	
	@SuppressWarnings("static-access")
	public SingleFromDB(ClientPowerSys psClient) {
		this.psClient = psClient;
		
		new Thread(() -> SingleFromDB.getSignals()).start();
		new Thread(() -> oldTS = SingleFromDB.psClient.getOldTS().values()).start();
		new Thread(() -> oldTI = SingleFromDB.psClient.getOldTI().values()).start();
		
		LogFiles.log.log(Level.INFO, "Start =================");
		while (signals == null || oldTS == null || oldTI == null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		LogFiles.log.log(Level.INFO, "End =================");
	}
	
	private static final Map<Integer, Image> imageMap = new HashMap<>();
	public static Map<Integer, Image> getImageMap() {
		if (imageMap.size() == 0) {
			transpMap.forEach((key, transp) -> {
				byte[] bytes = (byte[]) transpMap.get(key).getImg();
				InputStream is = WMF2PNG.convert(new ByteArrayInputStream(bytes), 250);
				imageMap.put(key, new Image(is));
			});
		}
		return imageMap;
	}
	
	public static Map<Integer, VsignalView> getSignals() {
		if (signals == null) signals = psClient.getVsignalViewMap();
		return signals;
	}
	
	public static Map<Integer, Tsignal> getTsignals() {
		if (tsignals == null) tsignals = psClient.getTsignalsMap();
		return tsignals;
	}
	
	public static Map<Integer, Tuser> getUsers() {
		if (users == null) users = psClient.getTuserMap();
		return users;
	}
	
	public static Map<Integer, Transparant> getTranspmap() {
		if (transpMap == null) transpMap = psClient.getTransparants();
		return transpMap;
	}
	
	public static List<SpTuCommand> getSptucommands() {
		if (spTuCommands == null) spTuCommands = psClient.getSpTuCommand();
		return spTuCommands;
	}
	
	public static Map<Integer, SpTypeSignal> getSptypesignals() {
		if (spTypeSignals == null) spTypeSignals = psClient.getSpTypeSignalMap();
		return spTypeSignals;
	}
}
