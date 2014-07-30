package model;

public class ConfTree {

	private int idnode;
	private String typedenom;
	private String nodename;
	private int parentref;
	private int sortnum;
	
	public ConfTree() {
		
	}

	public int getIdnode() {
		return idnode;
	}

	public void setIdnode(int idnode) {
		this.idnode = idnode;
	}

	public String getTypedenom() {
		return typedenom;
	}

	public void setTypedenom(String typedenom) {
		this.typedenom = typedenom;
	}

	public String getNodename() {
		return nodename;
	}

	public void setNodename(String nodename) {
		this.nodename = nodename;
	}

	public int getParentref() {
		return parentref;
	}

	public void setParentref(int parentref) {
		this.parentref = parentref;
	}

	public int getSortnum() {
		return sortnum;
	}

	public void setSortnum(int sortnum) {
		this.sortnum = sortnum;
	}
}
