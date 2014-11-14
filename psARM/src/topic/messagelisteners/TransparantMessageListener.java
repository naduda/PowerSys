package topic.messagelisteners;

import java.rmi.RemoteException;
import java.util.logging.Level;

import javafx.scene.Group;
import javafx.scene.shape.Shape;
import pr.log.LogFiles;
import pr.model.TtranspLocate;
import pr.model.Ttransparant;
import single.SingleFromDB;
import single.SingleObject;

public class TransparantMessageListener extends AbstarctMessageListener {

	@Override
	void runLogic(Object obj) {
		try {
			Ttransparant t = (Ttransparant) obj;
			Group root = SingleObject.mainScheme.getRoot();
			if (t.getClosetime() == null) {
				TtranspLocate transpLocate = SingleFromDB.psClient.getTransparantLocate(t.getIdtr());
				if (t.getSettime().equals(t.getLastupdate())) {
					int counter = 0;
					while (transpLocate == null || counter > 5) {
						try {
							Thread.sleep(500);
							transpLocate = SingleFromDB.psClient.getTransparantLocate(t.getIdtr());
							counter++;
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if (transpLocate != null) {
						SingleObject.mainScheme.addTransparant(t.getTp(), transpLocate.getX(), 
								transpLocate.getY(), transpLocate.getH(), t.getIdtr());
					}
				} else {
					Shape trShape = (Shape) root.lookup("#transparant_" + t.getIdtr());
					trShape.relocate(transpLocate.getX(), transpLocate.getY());
				}
			} else {
				root.getChildren().remove(root.lookup("#transparant_" + t.getIdtr()));
			}
		} catch (RemoteException e) {
			LogFiles.log.log(Level.SEVERE, "void runLogic(...)", e);
		}
	}
	
}
