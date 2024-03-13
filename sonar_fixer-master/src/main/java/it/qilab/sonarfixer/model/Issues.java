package it.qilab.sonarfixer.model;

import java.util.List;

public class Issues extends BaseModel {
	private List<Issue> issues;
	
	public List<Issue> getIssueList()
	{
		return this.issues;
	}
	
}