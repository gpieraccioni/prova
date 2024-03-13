package it.qilab.sonarfixer.model;

import java.util.List;

public class Issue {
	private String key;
	private String rule;
	private String severity;
	private String component;
	private String project;
	private int line;
	private String hash;
	private TextRange textRange;
	private List<Flow> flows;
	private String resolution;
	private String status;
	private String message;
	private String effort;
	private String debt;
	private String author;
	private String type; 
	private String scope;
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getRule()
	{
		return this.rule;
	}
	
	public String getSeverity()
	{
		return this.severity;
	}
	
	public String getComponent()
	{
		return this.component;
	}
	
	public String getProject()
	{
		return this.project;
	}
	
	public int getLine()
	{
		return this.line;
	}
	
	public String getHash()
	{
		return this.hash;
	}
	
	public TextRange getTextRange() {
        return this.textRange;
    }
	
	public List<Flow> getFlows() {
	    return this.flows;
    }
	
	public String getResolution() {
		return this.resolution;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public String getMessage()
	{
		return this.message;
	}
	
	public String getEffort()
	{
		return this.effort;
	}
	
	public String getDebt()
	{
		return this.debt;
	}
	
	public String getAuthor()
	{
		return this.author;
	}
	
	public String getType()
	{
		return this.type;
	}
	
	public String getScope()
	{
		return this.scope;
	}
}