package pr.model;

import java.io.Serializable;

public class TtranspLocate implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int trref;
	private int scref;
	private int x;
	private int y;
	private int h;
	private int w;
	
	public TtranspLocate() {
		
	}

	public int getTrref() {
		return trref;
	}

	public void setTrref(int trref) {
		this.trref = trref;
	}

	public int getScref() {
		return scref;
	}

	public void setScref(int scref) {
		this.scref = scref;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getH() {
		return h;
	}

	public void setH(int h) {
		this.h = h;
	}

	public int getW() {
		return w;
	}

	public void setW(int w) {
		this.w = w;
	}
}
