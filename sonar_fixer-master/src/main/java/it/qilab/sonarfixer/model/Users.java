package it.qilab.sonarfixer.model;

import java.util.List;

public class Users extends BaseModel{
	private List<User> users;
	
	public User getUserByLoginName(String loginName) {
        for (User user : users) {
            if (user.getLoginName().equals(loginName)) {
                return user; 
            }
        }
        return null; 
    }
}