package it.qilab.sonarfixer.webapi;

import java.io.IOException;
import java.net.URI;

public class SonarClientImpl implements SonarClient{
    private final BaseHttpClient baseHttpClient;
    private final CeClient ceClient;
    private final ComponentClient componentClient;
    private final UserClient userClient;
    private final MeasureClient measureClient;
    private final ProjectClient projectClient;
    private final SystemClient systemClient;
    private final IssueClient issueClient;

    public SonarClientImpl(URI uri, String username, String passwordOrToken) {
        this.baseHttpClient = new BaseHttpClient(uri, username, passwordOrToken);

        this.ceClient = new CeClient(baseHttpClient);
        this.componentClient = new ComponentClient(baseHttpClient);
        this.userClient = new UserClient(baseHttpClient);
        this.measureClient = new MeasureClient(baseHttpClient);
        this.projectClient = new ProjectClient(baseHttpClient);
        this.systemClient = new SystemClient(baseHttpClient);
        
        this.issueClient = new IssueClient(baseHttpClient);
    }

    @Override
    public BaseHttpClient getBaseSonarClient() {
        return this.baseHttpClient;
    }

    @Override
    public CeClient getCeClient() {
        return this.ceClient;
    }

    @Override
    public ComponentClient getComponentClient() {
        return this.componentClient;
    }

    @Override
    public UserClient getUserClient() {
        return this.userClient;
    }

    @Override
    public MeasureClient getMeasureClient() {
        return this.measureClient;
    }

    @Override
    public ProjectClient getProjectClient() {
        return this.projectClient;
    }

    @Override
    public SystemClient getSystemClient() {
        return this.systemClient;
    }
    
    @Override
    public IssueClient getIssueClient() {
    	 return this.issueClient;
    }
    
}
