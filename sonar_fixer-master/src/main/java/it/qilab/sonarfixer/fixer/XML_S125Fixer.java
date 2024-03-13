package it.qilab.sonarfixer.fixer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class XML_S125Fixer implements RuleFixer{
	private Issues issues;
    private String localPath;
    private String componentKeys;
    private boolean issuesStatus;
    
    public XML_S125Fixer(Issues issues, String localPath, String componentKeys) {
        this.issues = issues;
        this.localPath = localPath;
        this.componentKeys = componentKeys;
    }
    
	@Override
	public Map<String, List<Container_Action_ViolatedLine>> fixIssues() {
		// TODO Auto-generated method stub
		
		Map<String, List<Container_Action_ViolatedLine>> infoMap = new HashMap<>();
		List<Issue> issueList = this.issues.getIssueList();
		issuesStatus = true;
		int numIssueToResolve = 0;
		// Creiamo una mappa per raggruppare le informazioni delle violazioni per ciascun fullComponentPath
        Map<String, List<Integer>> violationLineMap = new HashMap<>();
        
        for (Issue issue : issueList) {
        	//controllo se le issue non sono già state risolte 
        	if(issue.getStatus().equals("OPEN") && issue.getResolution()== null) {
        		numIssueToResolve++;
        		String fullComponentPath = extractComponentPath(issue, this.localPath);
                int startLine = issue.getTextRange().getStartLine();
                int endLine = issue.getTextRange().getEndLine();
                
                List<Integer> linesInRange = new ArrayList<>();
                for (int line = startLine; line <= endLine; line++) {
                    linesInRange.add(line);
                }
                
                // Aggiungiamo le informazioni della violazione alla mappa usando fullComponentPath come chiave
                violationLineMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).addAll(linesInRange);
                
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

