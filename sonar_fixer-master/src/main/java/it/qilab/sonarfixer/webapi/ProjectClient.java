package it.qilab.sonarfixer.webapi;

import it.qilab.sonarfixer.model.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class ProjectClient {
    private BaseHttpClient httpClient;

    public ProjectClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

    public ProjectInfo getProjectById(String id) throws IOException {
        String projectsContext = this.httpClient.get(String.format("api/projects/index?format=json&key=%s", id));
        String projectContext = JSON.parseArray(projectsContext).getJSONObject(0).toJSONString();
        return JSON.parseObject(projectContext, ProjectInfo.class);
    }

    public ProjectInfo getProjectByKey(String key) throws IOException {
        String projectsContext = this.httpClient.get(String.format("api/projects/index?format=json&key=%s", key));
        String projectContext = JSON.parseArray(projectsContext).getJSONObject(0).toJSONString();
        return JSON.parseObject(projectContext, ProjectInfo.class);
    }

    public List<ProjectInfo> getProjects() throws IOException {
        String projectsContext = this.httpClient.get("api/projects/index?format=json");
        return JSON.parseObject(projectsContext, new ArrayList<ProjectInfo>().getClass());
    }

}