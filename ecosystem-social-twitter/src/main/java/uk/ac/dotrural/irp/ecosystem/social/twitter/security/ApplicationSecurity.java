/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.security;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.AccessStatus;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.Role;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.MD5Util;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.social.twitter.exceptions.AuthenticationException;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.AccessPojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ApplicationStatePojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ExceptionPojo;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;


/**
 * @author Charles Ofoegbu
 *
 */
public class ApplicationSecurity {
    
    public static ApplicationStatePojo isAuthorized(HttpServletRequest req,  Role... roles){
	ApplicationStatePojo currentState = new ApplicationStatePojo();	
	String apiKey =  req.getHeader("Authorization");
	Users user = DataAccessProxy.getUserByAuthorizationKey(apiKey);
	if(user == null){
	    ExceptionPojo ePojo = new ExceptionPojo(Constants.HTTP_CODE.UNAUTHORIZED, new String[]{"Invalid/Expired Authorization"},  "The supplied Authorization key is invalid or expired.");
	    currentState.setExceptionPojo(ePojo);
	    return currentState;	    
	}
	if(!user.getEnabled()){
	    ExceptionPojo ePojo = new ExceptionPojo(Constants.HTTP_CODE.UNAUTHORIZED, new String[]{"User Deactivated"},  "The User Account is currently deactivated.");
	    currentState.setExceptionPojo(ePojo);
	    return currentState;	
	}
	String currentUserRole = user.getUserRole().getAuthority();
	currentState.setCurrentUser(user);
	currentState.setCurrentUserRole(currentUserRole);
	Boolean isAuthorized = false;
	
	for(Role role : roles){
	    isAuthorized = (role.getRole().equalsIgnoreCase(currentUserRole)) ? true : false;
	    if(isAuthorized){break;}
	}
	if(!isAuthorized){
	    ExceptionPojo ePojo = new ExceptionPojo(Constants.HTTP_CODE.UNAUTHORIZED, new String[]{"Insufficient privilages"},  "The current user has insufficent privilage to access this method.");
	    currentState.setExceptionPojo(ePojo);
	    return currentState;	    
	}
	return currentState;
	 
    }
    

    public static AccessPojo AuthenticateUser(String email, String password) throws AuthenticationException{
	Users user = null;
	try{
	    user = DataAccessProxy.getUserByEmailAndPassword(email, password);
	}catch(Exception e){
	    e.printStackTrace();
	}
	
	if(user == null){
	    throw new AuthenticationException("Authentication Failed!", "Could not authenticate the user with the supplied credential.");
	}else if(!user.getEnabled()){
	    throw new AuthenticationException("User NOT Activated!", "The User with the supplied credentials is yet to be activated.");    
	}else{
	    AccessPojo acPojo = new AccessPojo();
	    acPojo.setEmail(user.getUsername());
	    acPojo.setUserFullName(user.getFirstName() + " " + user.getSurname());
	    String apiKey = generateApiKey(email, password);
	    acPojo.setUserApiKey(apiKey);
	    acPojo.setStatusCode(Constants.HTTP_CODE.OK.getCode());
	    acPojo.setUserRole(user.getUserRole().getAuthority());
	    user.setAuthorizationKey(apiKey);

  	    DataAccessProxy.updateRecord(user);
	    return acPojo;
	}
    }
    
    public static AccessStatus logUserOut(String authorizationString){
	Users user = DataAccessProxy.getUserByAuthorizationKey(authorizationString);
	if(user != null){
	    user.setAuthorizationKey(null);
	    DataAccessProxy.updateRecord(user);
	    return Constants.AccessStatus.LOGOUT_SUCCESSFUL;
	}else{
	    return Constants.AccessStatus.LOGOUT_FAILED;
	}
    }

    public static String generateApiKey(String email, String password){
	Timestamp timeStamp = new Timestamp(new Date().getTime());
	String randomUUId = UUID.randomUUID().toString();	
	return MD5Util.getMD5(email+password+randomUUId+timeStamp);
    }

}
