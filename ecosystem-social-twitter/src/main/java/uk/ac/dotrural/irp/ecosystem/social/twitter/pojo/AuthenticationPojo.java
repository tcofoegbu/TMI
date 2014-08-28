/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

/**
 * @author Charles Ofoegbu
 *
 */
public class AuthenticationPojo {
    private String email;
    private String password;
    
    public AuthenticationPojo(){
	
    }

    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }
    
}
