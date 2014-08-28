/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;


/**
 * @author Charles Ofoegbu
 *
 */
public class ApplicationStatePojo {
    private Users currentUser;
    private String currentUserRole;
    private ExceptionPojo exceptionPojo;
    
   
    public Users getCurrentUser() {
	return currentUser;
    }
    public void setCurrentUser(Users currentUser) {
	this.currentUser = currentUser;
    }
   
    public ExceptionPojo getExceptionPojo() {
	return exceptionPojo;
    }
    public void setExceptionPojo(ExceptionPojo exceptionPojo) {
	this.exceptionPojo = exceptionPojo;
    }
    public String getCurrentUserRole() {
	return currentUserRole;
    }
    public void setCurrentUserRole(String currentUserRole) {
	this.currentUserRole = currentUserRole;
    }
}
