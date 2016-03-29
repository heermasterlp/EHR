package com.um.ehr.action;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opensymphony.xwork2.ActionSupport;
import com.um.ehr.setting.DataBaseSetting;

public class LoginAction extends ActionSupport implements SessionAware {
	
	private static final long serialVersionUID = 1L;
	
	private Map<String, Object> sessionMap;

	private String username;
	
	private String password;
	
	/**
	 * Login check
	 * @return
	 */
	public String login(){
		
		// logged user name
		String loggedNameString = null;
		
		// check if the user name stored in the session
		if (sessionMap.containsKey("userName")) {
			loggedNameString = (String)sessionMap.get("userName");
		}
		if (loggedNameString != null && loggedNameString.equals(username)) {
			return SUCCESS;
		}
		
		// if user name not in session, check from database
		if (!"".equals(username) && !"".equals(password)) {
			// mongodb operation
			MongoClient client = new MongoClient(DataBaseSetting.host,DataBaseSetting.port);
			MongoDatabase db = client.getDatabase(DataBaseSetting.database);
			MongoCollection<Document> collection = db.getCollection(DataBaseSetting.infocollection);
		     
	        String passString = "";
	         
	        FindIterable<Document> iterable = collection.find(new BasicDBObject("doctorinfo.username",username));
	         
	        Document document = null;
	         
	        if(iterable.first() != null){
	       	 document = (Document) iterable.first().get("doctorinfo");
	        }
	         
	        if(document != null){
	        	 passString = document.getString("password");
	        }
	        
	        //close database
	      	client.close();
	        
			if(!"".equals(passString) && password.equals(passString)){
				
				// add userName to the session
	            sessionMap.put("userName", username);
	            
				return SUCCESS;
			}
		}
		return ERROR;
	}
	
	/**
	 * Logout
	 * @return
	 */
	public String logout(){
		if (sessionMap.containsKey("userName")) {
			sessionMap.remove("userName");
		}
		return SUCCESS;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public void setSession(Map<String, Object> arg0) {
		// TODO Auto-generated method stub
		sessionMap = arg0;
	}
	
}
