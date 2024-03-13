package it.qilab.sonarfixer.webapi;

import it.qilab.sonarfixer.model.*;
import com.alibaba.fastjson.JSON;
import java.io.IOException;
import java.util.ArrayList;

public class CeClient {
    private BaseHttpClient httpClient;

    public CeClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

    public Tasks getCeTasks() throws IOException {
        String result = this.httpClient.get(String.format("api/ce/activity"));
        Tasks tasks = JSON.parseObject(result).getObject("tasks", Tasks.class);
        return tasks;
    }

    public Task getCeTask(String taskId) throws IOException {
        String result = this.httpClient.get(String.format("api/ce/task?id=%s", taskId));
        Task task = JSON.parseObject(result).getObject("task", Task.class);
        return task;
    }

    public String getCurrentCeTask(String componentId) throws IOException {
        String result = this.httpClient.get(String.format("api/ce/component?component=%s", componentId));
        return result;
    }
    
    public CeComponentAnalysis getCurrentComponentCe(String componentId)throws IOException {
    	return this.httpClient.get(String.format("api/ce/component?component=%s", componentId),CeComponentAnalysis.class);
    }
   
}
