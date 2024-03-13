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

import it.qilab.sonarfixer.model.Flow;
import it.qilab.sonarfixer.model.FlowLocation;
import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class S1066Fixer implements RuleFixer {
	private Issues issues;
	private String localPath;
	private String componentKeys;
	private boolean issuesStatus;
	
	public S1066Fixer(Issues issues, String localPath, String componentKeys) {
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
	
		Map<String, List<ViolationsContainerS1066Fixer>> violationMap = new HashMap<>();
		
		for (Issue issue : issueList) {
			if(issue.getStatus().equals("OPEN") && issue.getResolution()== null) {
				numIssueToResolve++;
				String fullComponentPath = extractComponentPath(issue, this.localPath);
                int startLine = issue.getTextRange().getStartLine();
                int endLine = issue.getTextRange().getEndLine();
                int line = issue.getLine();
                List<Flow> flows = issue.getFlows();
                List<Integer> flowsLine;
                for (Flow flow : flows) {
                    for (FlowLocation flowLocation : flow.getLocations()) {
                        int flowStartLine = flowLocation.getTextRange().getStartLine();
                        violationMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new ViolationsContainerS1066Fixer(line, startLine, endLine, flowStartLine));
                    }
                }
               
			}
        }
		if(!violationMap.isEmpty()) {
			for (Map.Entry<String, List<ViolationsContainerS1066Fixer>> entry : violationMap.entrySet()) {
                String fullComponentPath = entry.getKey();
                List<ViolationsContainerS1066Fixer> violationList = entry.getValue();
                List<String> lines;
				try {
					lines = Files.readAllLines(Paths.get(fullComponentPath));
	                List<String> modifiedLines = new ArrayList<>(lines);
	
	                // Rimuoviamo le righe di violazione dall'alto verso il basso
	                for (ViolationsContainerS1066Fixer violationInfo : violationList) {
	                    int startLine = violationInfo.getStartLine();
	                    int endLine = violationInfo.getEndLine();
	                    if (startLine == endLine) {
	                    	if(modifiedLines.get(startLine - 1).contains("if")) {
	                    		Pattern pattern = Pattern.compile("\\((.*?)\\)");
	                    		Matcher matcher = pattern.matcher(modifiedLines.get(startLine - 1));
	                    		if (matcher.find()) {
	                    		    String extractedText = matcher.group(1); // Ottieni il testo tra parentesi
	                    		    System.out.println(extractedText);
	                    		    String modifiedLine = insertText("&& " + extractedText, "if", extractedText);
	                    		    infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("replace:"+modifiedLine, violationInfo.getFlowLine()));
	                    		    infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("remove:"+modifiedLine, violationInfo.getFlowLine()));
	                    		}
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
	
	private String insertText(String source, String insertPoint, String extractedText) {
	    int indexInsert = source.indexOf(insertPoint);
	    if (indexInsert != -1) {
	        return source.substring(0, indexInsert + insertPoint.length()) + " && " + extractedText + source.substring(indexInsert + insertPoint.length());
	    } else {
	        return source;
	    }
	}
	
	private String extractComponentPath(Issue issue, String localDir) {
		String fullComponentPath = issue.getComponent();
		String parte_dopo_due_punti = fullComponentPath.split(":")[1].strip();
		String modifiedPath = localDir + "/" + parte_dopo_due_punti;
        return modifiedPath;
    }

}
