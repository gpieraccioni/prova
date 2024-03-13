package it.qilab.sonarfixer.webapi;

import java.io.IOException;
import com.alibaba.fastjson.JSON;

public class SystemClient {
    private BaseHttpClient httpClient;

    public SystemClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

    public boolean restart() throws IOException {
        httpClient.post("api/system/restart", null);
        return true;
    }

    public String logs() throws IOException {
        return httpClient.get("api/system/logs");
    }

  
}
