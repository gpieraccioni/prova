package it.qilab.sonarfixer.model;

public class FlowLocation {
	private String component;
	private TextRange textRange;
    private String msg;

    public String getComponent() {
        return this.component;
    }

    public TextRange getTextRange() {
        return this.textRange;
    }

    public String getMsg() {
        return this.msg;
    }

}
