package it.qilab.sonarfixer.webapi;

import java.io.IOException;
import it.qilab.sonarfixer.model.*;

public class IssueClient {
	private BaseHttpClient httpClient;
	
	public IssueClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

	public Issues getAllIssuesFromRuleKey(String componentKeys, String ruleKey) throws IOException {
        return this.httpClient.get(String.format("api/issues/search?componentKeys=%s&rules=%s", componentKeys, ruleKey), Issues.class);
    }
}