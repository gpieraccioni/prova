package it.qilab.sonarfixer.fixer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class S1118Fixer implements RuleFixer {
	private Issues issues;
    private String localPath;
    private String componentKeys;
    private boolean issuesStatus;
    
	public S1118Fixer (Issues issues, String localPath, String componentKeys) {
		this.issues = issues;
		this.localPath = localPath;
		this.componentKeys = componentKeys;
	}
	
	@Override
	public Map<String, List<Container_Action_ViolatedLine>> fixIssues() {
		
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

                // Aggiungiamo le informazioni della violazione alla mappa usando fullComponentPath come chiave
                violationLineMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(startLine);
        	}
        }
        
        if(!violationLineMap.isEmpty()) {
        	for (Map.Entry<String, List<Integer>> entry : violationLineMap.entrySet()) {
                String fullComponentPath = entry.getKey();
                List<Integer> startLineViolationList = entry.getValue();
                
                Collections.sort(startLineViolationList, Collections.reverseOrder());
                
                List<String> lines;
				try {
					
					lines = Files.readAllLines(Paths.get(fullComponentPath));
					List<String> modifiedLines = new ArrayList<>(lines);
             
	                for (Integer line : startLineViolationList) {
	                	String classNamePattern = "class\\s+(\\w+)\\s*";
	                	Pattern pattern = Pattern.compile(classNamePattern);
	                    Matcher matcher = pattern.matcher(modifiedLines.get(line-1));
	
	                    if (matcher.find()) {
	                        String className = matcher.group(1);
	                        String newConstructor = "\n    private " + className + "() {\n    }\n";	                        
	                        infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("add:"+newConstructor, line));
	                    }
	                }
                
				} catch (IOException e) {
					e.printStackTrace();
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
