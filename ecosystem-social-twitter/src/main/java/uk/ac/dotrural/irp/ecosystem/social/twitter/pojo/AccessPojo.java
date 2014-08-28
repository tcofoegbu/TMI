/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

/**
 * @author Charles Ofoegbu
 *
 */
public class AccessPojo {
    private int statusCode;
    private String userFullName;
    private String userApiKey;
    private String email;
    private String userRole;
    
    public AccessPojo(){
	
    }
    
    public int getStatusCode() {
	return statusCode;
    }

    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }
    
    public String getUserFullName() {
	return userFullName;
    }
    
    public void setUserFullName(String userFullName) {
	this.userFullName = userFullName;
    }
    
    public String getUserApiKey() {
	return userApiKey;
    }
    
    public void setUserApiKey(String userApiKey) {
	this.userApiKey = userApiKey;
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getUserRole() {
	return userRole;
    }

    public void setUserRole(String userRole) {
	this.userRole = userRole;
    }

 

}
