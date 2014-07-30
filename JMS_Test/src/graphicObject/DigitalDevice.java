package graphicObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import xml.ShapeX;

public class DigitalDevice extends AShape {

	private final double SH_WIDTH = rect.getWidth() - lineWidth;
	
	private final Text text = new Text("*****");
	
	private DecimalFormat decimalFormat;
	
	public DigitalDevice(ShapeX sh) {
		super(sh);

		rect.setFill(fillColor);
		text.setX(0);
		text.setY(sh.getHeight()/2 + sh.getFontSize()/4);
		text.setFont(new Font("Arial", sh.getFontSize()));
		text.setWrappingWidth(SH_WIDTH);
		text.setTextAlignment(TextAlignment.CENTER);
		text.setFill(lineColor);
		
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		decimalFormat = new DecimalFormat(sh.getPrecision(), dfs);
		
		getChildren().add(text);
	}

	public void setText(String text) {
		this.text.setText(text);
	}
	
	public DecimalFormat getDecimalFormat() {
		return decimalFormat;
	}

	public void setDecimalFormat(DecimalFormat decimalFormat) {
		this.decimalFormat = decimalFormat;
	}
}
