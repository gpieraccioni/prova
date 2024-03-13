package it.qilab.sonarfixer.model;

public class User extends BaseModel{
    private String login;
    private String name;
    private String email;
    private boolean active;
    private boolean local;
    private String externalIdentity;
    private String externalProvider;
    private String[] groups;
    private String[] scmAccounts;
    private int tokensCount;
    
    public String getLoginName() {
    	return this.login;
    }
    
    public String getEmail() {
    	return this.email;
    }
    
    

}