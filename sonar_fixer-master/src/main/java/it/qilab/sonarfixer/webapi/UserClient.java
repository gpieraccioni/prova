package it.qilab.sonarfixer.webapi;
import it.qilab.sonarfixer.model.*;
import java.io.IOException;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

public class UserClient {
	
    private BaseHttpClient httpClient;

    public UserClient(BaseHttpClient baseHttpClient) {
        this.httpClient = baseHttpClient;
    }

    public User getUser(String login) throws IOException {
        return this.httpClient.get(String.format("api/users/search?q=%s", login),User.class);
    }

    public boolean authentication() throws IOException {
    	
        String result = this.httpClient.get("api/authentication/validate");
        return false;
    }
    
    public Users getUsers(String p, String ps) throws IOException {
        return this.httpClient.get(String.format("api/users/search?p=%s&ps=%s", p, ps),Users.class);
    }
}