package it.qilab.sonarfixer.webapi;
import java.io.Closeable;
import java.io.IOException;
public interface SonarClient {
    BaseHttpClient getBaseSonarClient();

    CeClient getCeClient();

    ComponentClient getComponentClient();

    UserClient getUserClient();

    MeasureClient getMeasureClient();

    ProjectClient getProjectClient();

    SystemClient getSystemClient();
    
    IssueClient getIssueClient();
}
