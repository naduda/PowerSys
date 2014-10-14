package pr.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Tuser implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int iduser;
	private String un;
	private String pwd;
	private String fio;
	private String info;
	private int grid;
	private Timestamp startdt;
	private Timestamp stopdt;
	private int isblocked;
	private String dbun;
	private String dbpwd;
	
	public Tuser() {
		
	}

	public int getIduser() {
		return iduser;
	}

	public void setIduser(int iduser) {
		this.iduser = iduser;
	}

	public String getUn() {
		return un;
	}

	public void setUn(String un) {
		this.un = un;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getFio() {
		return fio;
	}

	public void setFio(String fio) {
		this.fio = fio;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public int getGrid() {
		return grid;
	}

	public void setGrid(int grid) {
		this.grid = grid;
	}

	public Timestamp getStartdt() {
		return startdt;
	}

	public void setStartdt(Timestamp startdt) {
		this.startdt = startdt;
	}

	public Timestamp getStopdt() {
		return stopdt;
	}

	public void setStopdt(Timestamp stopdt) {
		this.stopdt = stopdt;
	}

	public int getIsblocked() {
		return isblocked;
	}

	public void setIsblocked(int isblocked) {
		this.isblocked = isblocked;
	}

	public String getDbun() {
		return dbun;
	}

	public void setDbun(String dbun) {
		this.dbun = dbun;
	}

	public String getDbpwd() {
		return dbpwd;
	}

	public void setDbpwd(String dbpwd) {
		this.dbpwd = dbpwd;
	}
}
