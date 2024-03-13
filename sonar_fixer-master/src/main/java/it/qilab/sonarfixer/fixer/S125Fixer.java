package it.qilab.sonarfixer.fixer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.qilab.sonarfixer.model.Flow;
import it.qilab.sonarfixer.model.FlowLocation;
import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class S125Fixer implements RuleFixer {
	private Issues issues;
    private String localPath;
    private String componentKeys;
    private boolean issuesStatus;
    
    public S125Fixer(Issues issues, String localPath, String componentKeys) {
        this.issues = issues;
        this.localPath = localPath;
        this.componentKeys = componentKeys;
    }
    
	@Override
	public Map<String, List<Container_Action_ViolatedLine>> fixIssues() {
		issuesStatus = true;
		Map<String, List<Container_Action_ViolatedLine>> infoMap = new HashMap<>();

		List<Issue> issueList = this.issues.getIssueList();
		// Creiamo una mappa per raggruppare le informazioni delle violazioni per ciascun fullComponentPath
        Map<String, List<Integer>> violationLineMap = new HashMap<>();
        
        for (Issue issue : issueList) {
        	//controllo se le issue non sono già state risolte 
        	if(issue.getStatus().equals("OPEN") && issue.getResolution()== null) {
        		String fullComponentPath = extractComponentPath(issue, this.localPath);
                int startLine = issue.getTextRange().getStartLine();

                // Aggiungiamo le informazioni della violazione alla mappa usando fullComponentPath come chiave
                violationLineMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(startLine);
                List<Flow> flows = issue.getFlows();
                for (Flow flow : flows) {
                    for (FlowLocation flowLocation : flow.getLocations()) {
                        int flowStartLine = flowLocation.getTextRange().getStartLine();
                        violationLineMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(flowStartLine);
                    }
                }
        	}
        }
        
        if(!violationLineMap.isEmpty()) {
        	for (Map.Entry<String, List<Integer>> entry : violationLineMap.entrySet()) {
                String fullComponentPath = entry.getKey();
                List<Integer> startLineViolationList = entry.getValue();
                Collections.sort(startLineViolationList, Collections.reverseOrder());
                for (Integer line : startLineViolationList) {
                	infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("remove", line));
                }
            }
    
        }
        else {
        	issuesStatus = false;
        }
        
		return infoMap;
	
	}
	
	@Override
	public boolean getIssuesStatus() {
		return issuesStatus;
	}
	
	private String extractComponentPath(Issue issue, String localDir) {
		String fullComponentPath = issue.getComponent();
		String parte_dopo_due_punti = fullComponentPath.split(":")[1].strip();
		String modifiedPath = localDir + "/" + parte_dopo_due_punti;
        return modifiedPath;
    }

}
