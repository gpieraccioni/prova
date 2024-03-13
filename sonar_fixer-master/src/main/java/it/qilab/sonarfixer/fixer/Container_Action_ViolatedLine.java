package it.qilab.sonarfixer.fixer;

public class Container_Action_ViolatedLine {
	private String action;
	private int line;
	
	public Container_Action_ViolatedLine (String action, int line) {
		this.action = action;
		this.line = line;
	}
	
	public String getAction() {
		return this.action;
	}
	
	public int getViolatedLine() {
		return this.line;
	}
}

