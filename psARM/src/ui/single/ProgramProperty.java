package ui.single;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import pr.model.Alarm;

public class ProgramProperty {
	public static final ObjectProperty<Alarm> alarmProperty = new SimpleObjectProperty<>();
	public static final BooleanProperty selectedShapeChangeProperty = new SimpleBooleanProperty();
}
