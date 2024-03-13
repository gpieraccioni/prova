package it.qilab.sonarfixer.webapi;

import java.io.IOException;

public class ComponentClient {
    private BaseHttpClient httpClient;

    public ComponentClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

    public String getComponentById(String id) throws IOException {
        return this.httpClient.get(String.format("api/components/show?id=%s", id));
    }

    public String getComponentByKey(String key) throws IOException {
        return this.httpClient.get(String.format("api/components/show?key=%s", key));
    }

    public String getComponentTree(String baseComponentId) throws IOException {
        return this.httpClient.get(String.format("api/components/trees?baseComponentId=%s", baseComponentId));
    }
}
