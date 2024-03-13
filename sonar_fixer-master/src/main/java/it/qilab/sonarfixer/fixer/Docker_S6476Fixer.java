package it.qilab.sonarfixer.fixer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class Docker_S6476Fixer implements RuleFixer{
	private Issues issues;
	private String localPath;
	private String componentKeys;
	private boolean issuesStatus;
	
	public Docker_S6476Fixer(Issues issues, String localPath, String componentKeys) {
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
		
			Map<String, List<ViolationInfo>> violationMap = new HashMap<>();
			
			for (Issue issue : issueList) {
				if(issue.getStatus().equals("OPEN") && issue.getResolution()== null) {
					numIssueToResolve++;
					String fullComponentPath = extractComponentPath(issue, this.localPath);
	                int startLine = issue.getTextRange().getStartLine();
	                int endLine = issue.getTextRange().getEndLine();
	                int line = issue.getLine();
	                String message = issue.getMessage();
	                // Aggiungiamo le informazioni della violazione alla mappa usando fullComponentPath come chiave
	                violationMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new ViolationInfo(message, line, startLine, endLine));
				}
	        }
			if(!violationMap.isEmpty()) {
				for (Map.Entry<String, List<ViolationInfo>> entry : violationMap.entrySet()) {
	                String fullComponentPath = entry.getKey();
	                List<ViolationInfo> violationList = entry.getValue();
	                
	                List<String> lines;
					try {
						lines = Files.readAllLines(Paths.get(fullComponentPath));
		                List<String> modifiedLines = new ArrayList<>(lines);
		
		                // Rimuoviamo le righe di violazione dall'alto verso il basso
		                for (ViolationInfo violationInfo : violationList) {
		                    int startLine = violationInfo.getStartLine();
		                    int endLine = violationInfo.getEndLine();
		                    if (startLine == endLine) {
	                    	
	                    		String apiMessage = violationInfo.getMessage();
	                    		Pattern pattern = Pattern.compile("`([^`]*)`");
	                            Matcher matcher = pattern.matcher(apiMessage);
	                            if (matcher.find()) {
	                            	String matchedText = matcher.group(0).replace("`", "");;
	                                String textToReplace = matcher.group(1).toUpperCase();
		                            String modifiedLine = modifiedLines.get(startLine-1).replace(matchedText, textToReplace);
		                            infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("replace:"+modifiedLine, startLine));
	                            }
	                    		
		                    	
		                    }
		                }
	                }catch (IOException e) {
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

