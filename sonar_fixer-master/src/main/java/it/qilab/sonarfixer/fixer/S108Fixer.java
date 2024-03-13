package it.qilab.sonarfixer.fixer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class S108Fixer implements RuleFixer{
	private Issues issues;
	private String localPath;
	private String componentKeys;
	private boolean issuesStatus;
	
	public S108Fixer(Issues issues, String localPath, String componentKeys) {
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
	
		Map<String, List<ViolationLine>> violationMap = new HashMap<>();
		
		for (Issue issue : issueList) {
			if(issue.getStatus().equals("OPEN") && issue.getResolution()== null) {
				numIssueToResolve++;
				String fullComponentPath = extractComponentPath(issue, this.localPath);
                int startLine = issue.getTextRange().getStartLine();
                int endLine = issue.getTextRange().getEndLine();
                int line = issue.getLine();
                violationMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new ViolationLine(line, startLine, endLine));
			}
        }
		if(!violationMap.isEmpty()) {
			for (Map.Entry<String, List<ViolationLine>> entry : violationMap.entrySet()) {
                String fullComponentPath = entry.getKey();
                List<ViolationLine> violationList = entry.getValue();
                
                List<String> lines;
				try {
					lines = Files.readAllLines(Paths.get(fullComponentPath));
	                List<String> modifiedLines = new ArrayList<>(lines);
	                for (ViolationLine violationInfo : violationList) {
	                    int startLine = violationInfo.getStartLine();
	                    int endLine = violationInfo.getEndLine();
	                    if (startLine == endLine) {
	                    	if(modifiedLines.get(startLine - 1).contains("catch")) {
                    			infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("add:\n	System.err.println(\"Exception Error\");", startLine));
	                    	}
	                    	if(modifiedLines.get(startLine - 1).contains("else")) {
	                    		int blockEnd = -1;
	                    		int offset = 0;
	                    		if(modifiedLines.get(startLine - 1).contains("} else")) {
	                    			String modifiedLine = modifiedLines.get(startLine - 1).replace("else {", " ").replace("else", " ");
	                    			infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("replace:"+modifiedLine, startLine));
	                    			offset++;
	                    		}
	                            int currentLine = startLine;
	                            while (currentLine < modifiedLines.size()) {
	                                if (modifiedLines.get(currentLine - 1).trim().equals("}")) {
	                                    blockEnd = currentLine;
	                                    break;
	                                }
	                                currentLine++;
	                            }
	                            if (blockEnd != -1) {
	                                for (int i = startLine + offset; i <= blockEnd; i++) {
	                                	infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("remove", i));
	                                }
	                                Collections.sort(infoMap.get(fullComponentPath), Comparator.comparingInt(Container_Action_ViolatedLine::getViolatedLine).reversed());
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
	
	private String extractComponentPath(Issue issue, String localDir) {
		String fullComponentPath = issue.getComponent();
		String parte_dopo_due_punti = fullComponentPath.split(":")[1].strip();
		String modifiedPath = localDir + "/" + parte_dopo_due_punti;
        return modifiedPath;
    }

}
