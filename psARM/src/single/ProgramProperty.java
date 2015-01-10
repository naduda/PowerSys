package single;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import pr.model.Alarm;

public class ProgramProperty {
	public static final ObjectProperty<Alarm> alarmProperty = new SimpleObjectProperty<>();
	public static final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
	public static final StringProperty localeName = new SimpleStringProperty();
	public static final ObjectProperty<Alarm> hightPriorityAlarmProperty = new SimpleObjectProperty<>();
	public static final StringProperty chatMessageProperty = new SimpleStringProperty();
}
