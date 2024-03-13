package it.qilab.sonarfixer.fixer;
import spoon.Launcher;
import spoon.reflect.CtModel;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import it.qilab.sonarfixer.model.Issue;
import it.qilab.sonarfixer.model.Issues;

public class S1144Fixer implements RuleFixer {
	private Issues issues;
    private String localPath;
    private String componentKeys;
    private boolean issuesStatus;
	
    public S1144Fixer(Issues issues, String localPath, String componentKeys) {
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
        Map<String, List<ViolationInfo>> violationMap = new HashMap<>();
        
        for (Issue issue : issueList) {
        	//controllo se le issue non sono già state risolte 
        	if(issue.getStatus().equals("OPEN") && issue.getResolution()== null) {
        		numIssueToResolve++;
        		String fullComponentPath = extractComponentPath(issue, this.localPath);
                String message = issue.getMessage();
                String methodNameToRemove = findMethodNameInMessage(message);
                
                int startLine = issue.getTextRange().getStartLine();
                int endLine = issue.getTextRange().getEndLine();
                int line = issue.getLine();

                // Aggiungiamo le informazioni della violazione alla mappa usando fullComponentPath come chiave
                violationMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new ViolationInfo(methodNameToRemove, line, startLine, endLine));
        	}
        }
        
        if(!violationMap.isEmpty()) {
        	for (Map.Entry<String, List<ViolationInfo>> entry : violationMap.entrySet()) {
                String fullComponentPath = entry.getKey();
                List<ViolationInfo> violationList = entry.getValue();
                
                Collections.sort(violationList, Comparator.comparingInt(ViolationInfo::getLine).reversed());
 
                for (ViolationInfo violationInfo : violationList) {
                    int startLine = violationInfo.getStartLine();
                    int endLine = violationInfo.getEndLine();
                    if (startLine == endLine) {
 
                    	Launcher launcher = new Launcher();
                        launcher.addInputResource(fullComponentPath);
                        
                        launcher.buildModel();
                        CtModel model = launcher.getModel();
                        
                        String methodNameToRemove = violationInfo.getMessage();

                        CtMethod<?> targetMethod = model.getElements(new TypeFilter<>(CtMethod.class))
                        	    .stream()
                        	    .filter(method -> method.getSimpleName().equals(methodNameToRemove))
                        	    .findFirst()
                        	    .orElse(null);
                        
                        if (targetMethod != null) {
                        
                            int methodEndLine = targetMethod.getPosition().getEndLine();
                            
                            for (int i = startLine; i <= methodEndLine ; i++) {
                                infoMap.computeIfAbsent(fullComponentPath, key -> new ArrayList<>()).add(new Container_Action_ViolatedLine("remove", i));      
                            }
                            
                        }    
                    }
                }
                Collections.sort(infoMap.get(fullComponentPath), Comparator.comparingInt(Container_Action_ViolatedLine::getViolatedLine).reversed());

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
	
	private String findMethodNameInMessage(String message)
	{
		String regex = "\"(.*?)\"";
		Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            String variableName = matcher.group(1);
            return variableName;
        }
        return null;
	}

}
