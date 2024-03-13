package it.qilab.sonarfixer.view;
import it.qilab.sonarfixer.view.repositorymanager.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;

import it.qilab.sonarfixer.model.Issues;
import it.qilab.sonarfixer.fixer.Container_Action_ViolatedLine;
import it.qilab.sonarfixer.fixer.FixerFactory;
import it.qilab.sonarfixer.fixer.RuleFixer;
import it.qilab.sonarfixer.webapi.ScanInfo;
import it.qilab.sonarfixer.webapi.SonarServer;

public class SonarFacade {
	private Config config;
    private SonarServer sonarServer;
    private MapAggregator aggregator;
    private RepositoryManager repositoryManager;

    public SonarFacade(String[] args) {
        this.config = new Config(args);
        this.sonarServer = new SonarServer(URI.create(config.getSonarHostUrl()), config.getSonarUsername(), config.getSonarPassword());
        this.aggregator = new MapAggregator();
        this.repositoryManager = GitLabRepositoryManager.getInstance();
        this.repositoryManager.setGitUsername(config.getGitlabUsername());
        this.repositoryManager.setupGitRepositoryPaths(config.getGitlabBaseUrl(), config.getGitlabFolderPath(), config.getLocalDirectory());
        this.repositoryManager.setCommitMessage("new commit");
        this.repositoryManager.setBranchName(config.getBranchName());
        this.repositoryManager.setTokenAuth(config.getGitlabTokenAuth());
    }
    
    public void runScanAndFixIssues() {
    	try {
    		repositoryManager.cloneRepository();
    		
    		repositoryManager.createAndCheckoutNewBranch();
    		
    		long startTime = System.currentTimeMillis();
            String sonarLogin = config.getSonarLogin();
            String localDir = config.getLocalDirectory();
            String projectKey = config.getProjectKey();
            String workingDirectory = config.getWorkingDirectory();
            Map<String, Boolean> rules = config.getRules();

            boolean anyRuleToFix = true;
            List<String> resolvedRules = new ArrayList<>();
            
            ScanInfo preInfo = sonarServer.getScanInfo(projectKey);
        	System.out.println("\n Last scan Id: " + preInfo.getScanId() + "\n");
            
            while (System.currentTimeMillis() - startTime < config.getTimeoutMillis() && anyRuleToFix) {
                    anyRuleToFix = false;
                    
                    sonarServer.executeScanner(projectKey, config.getSonarHostUrl(), sonarLogin, workingDirectory, true);
                    ScanInfo postInfo = sonarServer.getScanInfo(projectKey);
                    
                    while (System.currentTimeMillis() - startTime < config.getScanTimeoutMillis() && 
                    		postInfo.getScanId().equals(preInfo.getScanId())) {
                    	Thread.sleep(2000);
                    	postInfo = sonarServer.getScanInfo(projectKey); 
                    }
                   
                    sonarServer.getScannerAnalysisOnComponent(projectKey);
                    preInfo = postInfo;
                    
                    for (Map.Entry<String, Boolean> ruleEntry : rules.entrySet()) {
                        String ruleKey = ruleEntry.getKey();
                        boolean isEnabled = ruleEntry.getValue();

                        if (!isEnabled || resolvedRules.contains(ruleKey)) {
                            continue; // Salta la regola se è disabilitata o già risolta
                        }

                        Map<String, List<Container_Action_ViolatedLine>> map = new HashMap<>();
                        Issues issues = sonarServer.getIssues(projectKey, ruleKey);

                        if (!issues.getIssueList().isEmpty()) {
                            RuleFixer fixer = FixerFactory.createFixer(ruleKey, issues, localDir + config.getRepositoryFolderName(), projectKey);
                            map = fixer.fixIssues();
                            if (!fixer.getIssuesStatus()) {
                                System.out.println("\u001B[32m All " + ruleKey + " violations resolved in project " + projectKey + "\u001B[0m");
                                resolvedRules.add(ruleKey);
                            } else {
                                aggregator.aggregate(map);
                                System.out.println("\u001B[31m ... Still remaining issues to fix for rule: " + ruleKey + "\u001B[0m");
                                anyRuleToFix = true;
                            }
                        } else {
                            System.out.println(" No issues to fix for rule " + ruleKey + " in project " + projectKey);
                            resolvedRules.add(ruleKey);
                        }
                    }

                    Map<String, List<Container_Action_ViolatedLine>> aggregatedMap = aggregator.getAggregatedMap();
                    aggregator.fixAllIssues();
                    aggregator.clearAggregatedMap();

                    for (String resolvedRule : resolvedRules) {
                        rules.remove(resolvedRule);
                    }
                    resolvedRules.clear();
            }

            
            sonarServer.executeScanner(projectKey, config.getSonarHostUrl(), sonarLogin, workingDirectory, true);
            sonarServer.getScannerAnalysisOnComponent(projectKey);
    		
            repositoryManager.commitAndPushToCurrentBranch("Push Commit");
            
    		repositoryManager.createMergeRequest("Merge Request Test", "Description Test");
    		
    		repositoryManager.closeRepository();
    		
    		File localRepositoryDir = new File(repositoryManager.getLocalPath());
    		if (localRepositoryDir.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(localRepositoryDir); 
                    System.out.println("Cancellata correttamente la directory dove era clonato il repository");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            

    		
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

