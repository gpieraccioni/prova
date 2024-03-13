package it.qilab.sonarfixer.view;

import java.util.HashMap;
import java.util.Map;

public class Config {
	private String sonarHostUrl;
    private String sonarLogin;
    private String sonarUsername;
    private String sonarPassword;
    
    private String gitlabTokenAuth;
    private String gitlabBaseUrl;
    private String gitlabUsername;
    private String gitlabLocalDir;
    
    private long timeoutMillis = 30 * 60 * 1000; // 30 min di timeout
    private long scanTimeoutMillis = 5 * 60 * 1000;
    
    private String projectKey;
    private String gitlabFolderPath;
    private String branchName = "master"; // Valore di default
    private Map<String, Boolean> rules = getDefaultRules(); // Regole di default
    
    public Config(String[] args) {
        if (args.length >= 2) {
            this.projectKey = args[0];
            this.gitlabFolderPath = args[1];
            loadFromEnvironmentVariables();
            parseCommandLineOptions(args);
        } else {
            printUsage();
            System.exit(1);
        }
    }
    
    public void loadFromEnvironmentVariables() {
	    if (System.getenv("SONAR_URL") != null) {
	    	this.sonarHostUrl = System.getenv("SONAR_URL");
	    }
	    if (System.getenv("SONAR_LOGIN") != null) {
	    	this.sonarLogin = System.getenv("SONAR_LOGIN");
	    }
	    if (System.getenv("SONAR_USERNAME") != null) {
	    	this.sonarUsername = System.getenv("SONAR_USERNAME");
	    }
	    if (System.getenv("SONAR_PASSWORD") != null) {
	    	this.sonarPassword = System.getenv("SONAR_PASSWORD");
	    }
	    if (System.getenv("GITLAB_LOCAL_DIRECTORY") != null) {
	    	this.gitlabLocalDir = System.getenv("GITLAB_LOCAL_DIRECTORY");
	    }
	    if (System.getenv("GITLAB_USERNAME") != null) {
	    	this.gitlabUsername = System.getenv("GITLAB_USERNAME");
	    }
	    if (System.getenv("GITLAB_TOKEN_AUTH") != null) {
	    	this.gitlabTokenAuth = System.getenv("GITLAB_TOKEN_AUTH");
	    }
	    if (System.getenv("GITLAB_BASE_URL") != null) {
	    	this.gitlabBaseUrl = System.getenv("GITLAB_BASE_URL");
	    } 
    }
    
    private void parseCommandLineOptions(String[] args) {
        for (int i = 2; i < args.length; i++) {
            String option = args[i];
            if (option.startsWith("-r=") || option.startsWith("--rules=")) {
                // Esempio: -r 'java:S1068:true,java:S1118:true,java:S106:false'
                String rulesString = option.substring(option.indexOf('=') + 1);
                parseRules(rulesString);
            } else if (option.startsWith("-b=") || option.startsWith("--branch=")) {
                // Esempio: -b feature-branch
                this.branchName = option.substring(option.indexOf('=') + 1);
            } else {
                System.err.println("Opzione non riconosciuta: " + option);
                printUsage();
                System.exit(1);
            }
        }
    }
    
    private void parseRules(String rulesString) {
        // Esempio di stringa di regole: 'java:S1068:true,java:S1118:true,java:S106:false'
        String[] rulePairs = rulesString.split(",");
        for (String rulePair : rulePairs) {
            String[] parts = rulePair.split(":");
            if (parts.length == 3) {
                String ruleKey = parts[0];
                boolean isEnabled = Boolean.parseBoolean(parts[2]);
                rules.put(ruleKey, isEnabled);
            } else {
                System.err.println("Formato errato per le regole: " + rulePair);
                printUsage();
                System.exit(1);
            }
        }
    }
    
    private Map<String, Boolean> getDefaultRules() {
        Map<String, Boolean> defaultRules = new HashMap<>();
        defaultRules.put("java:S1068", true);
        defaultRules.put("java:S1118", true);
        defaultRules.put("java:S125", true);
        defaultRules.put("java:S1144", true);
        defaultRules.put("xml:S125", true);
        defaultRules.put("docker:S6476", true);
        defaultRules.put("java:S108", true);
        return defaultRules;
    }
    
    private void printUsage() {
    	String usage = """
  
					  Usage: java SonarQubeFixer <projectKey> <GitLab repository local directory> [options]
					  Options:
					    -r, --rules=<rules>      Comma-separated list of custom rule settings in the format 'rule_key:true/false'
					    -b, --branch=<branch>    Specify the Git branch name (Default: master)
					
					  By default, the following SonarQube rules are enabled:
					    java:S1068 (Unused private fields), 
					    java:S1118 (Utility classes should not have public constructors),
					    java:S125 (Sections of code should not be commented out),
					    java:S1144 (Unused class method should be removed), 
					    xml:S125 (Sections of code should not be commented out),
					    docker:S6476 (Sections of code should not be commented out), 
					    java:S108 (Nested blocks of code should not be left empty)
					  
					  Make sure you have set the following environment variables with your specific values:
					    SONAR_URL: Your SonarQube server URL (e.g., http://sonar.dev.qilab.it)
					    SONAR_LOGIN: Your SonarQube login or authentication token
					    SONAR_USERNAME: Your SonarQube username
					    SONAR_PASSWORD: Your SonarQube password or authentication token
					    GITLAB_TOKEN_AUTH: Your GitLab authentication token
					    GITLAB_BASE_URL: Your GitLab server URL (e.g., https://gitlab.qilab.it)
					    GITLAB_USERNAME: Your GitLab username
					    GITLAB_LOCAL_DIRECTORY: Your GitLab local directory (e.g., C:/Users/UserName/Desktop
					 
					  Example command-line usage:
					    java SonarQubeFixer sonartest_myvay_svc test_group/myvay_svc_sonartest.git -r='java:S1068:true,java:S1118:true,java:S106:false'
    			""";
    	
    	System.err.println(usage);
    }

    public String getSonarHostUrl() {
        return this.sonarHostUrl;
    }

    public String getSonarLogin() {
        return this.sonarLogin;
    }

    public String getSonarUsername() {
        return this.sonarUsername;
    }

    public String getSonarPassword() {
        return this.sonarPassword;
    }

    public String getProjectKey() {
        return this.projectKey;
    }

    public long getTimeoutMillis() {
        return this.timeoutMillis;
    }
    
    public long getScanTimeoutMillis(){
    	return this.scanTimeoutMillis;
    }

    public String getWorkingDirectory() {
    	String[] partsOfRoute = gitlabFolderPath.split("/");
    	String repositoryFolderName = partsOfRoute[partsOfRoute.length - 1];
        return this.gitlabLocalDir + "/" + repositoryFolderName;
    }
    
    public String getRepositoryFolderName()
    {
    	String[] partsOfRoute = gitlabFolderPath.split("/");
    	String repositoryFolderName = partsOfRoute[partsOfRoute.length - 1];
    	return repositoryFolderName;
    }
    
    public String getLocalDirectory() {
    	return this.gitlabLocalDir + "/";
    }
    
    public Map<String, Boolean> getRules() {
        return this.rules;
    }

    public String getBranchName() {
        return this.branchName;
    }

    public String getGitlabBaseUrl() {
        return this.gitlabBaseUrl; 
    }
    
    public String getGitlabFolderPath() {
        return this.gitlabFolderPath;
    }
    
    public String getGitlabUsername() {
        return this.gitlabUsername;
    }
    
    public String getGitlabTokenAuth() {
        return this.gitlabTokenAuth;
    }
}
