package single;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;
import pr.common.WMF2PNG;
import pr.model.SpTuCommand;
import pr.model.SpTypeSignal;
import pr.model.Transparant;
import pr.model.Tsignal;
import pr.model.Tuser;
import pr.model.VsignalView;
import topic.ClientPowerSys;

public class SingleFromDB {
	public static final ClientPowerSys psClient = new ClientPowerSys();
	
	public static final Map<Integer, VsignalView> signals = psClient.getVsignalViewMap();
	public static final Map<Integer, Tsignal> tsignals = psClient.getTsignalsMap();
	public static final Map<Integer, Tuser> users = psClient.getTuserMap();
	public static final Map<Integer, Transparant> transpMap = psClient.getTransparants();
	public static final List<SpTuCommand> spTuCommands = psClient.getSpTuCommand();
	public static final Map<Integer, SpTypeSignal> spTypeSignals = psClient.getSpTypeSignalMap();
	
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
}
