package it.qilab.sonarfixer.webapi;

import java.io.IOException;

public class MeasureClient {
    private BaseHttpClient httpClient;

    public MeasureClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

    public String getComponentMeasures(String componentId) throws IOException {
        return this.httpClient.get(String.format("api/measures/component?componentId=%s&metricKeys=ncloc,complexity,violations", componentId));
    }

    public String getComponentMeasuresTree(String baseComponentId) throws IOException {
        return this.httpClient.get(String.format("api/measures/component_tree?baseComponentId=%s&metricKeys=ncloc,complexity,violations", baseComponentId));
    }

}