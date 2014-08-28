/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.service;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import org.slf4j.*;

import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.*;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.*;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userrole.UserRole;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.social.twitter.security.ApplicationSecurity;



/**
 * @author Charles Ofoegbu
 *
 */
@Path("/users")
public class UserService {
    @POST
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Response createUser(UserPojo jsonUser, @Context HttpServletRequest req){	
	Logger logger = LoggerFactory.getLogger(UserService.class);	
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Post called - create user " );
	
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req, Constants.Role.SUPER_ADMIN);
	if(authorizationState.getExceptionPojo() != null){
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	URI uri = null;
	UserRole userRole = null;
	if(jsonUser.getUserRole() != null){
	    userRole = DataAccessProxy.getUserRoleByAuthority(jsonUser.getUserRole());
	}
	
	if(userRole == null){
	    authorizationState = null;
	    return Response.status(Constants.HTTP_CODE.NOT_FOUND.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.NOT_FOUND, null, "Invalid userRole specified!")).build();	
	}
	
	if(DataAccessProxy.isEmailExist(jsonUser.getEmail())){
	    return Response.status(Constants.HTTP_CODE.CONFLICT.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.CONFLICT, new String[]{"Email must be Unique!"}, "An Account aready exist with the supplied email!")).build();
	}

	Users user = new Users();		
	user.setUserRole(userRole); 
	user.setUsername(jsonUser.getEmail());
	user.setFirstName(jsonUser.getFirstName());
	user.setSurname(jsonUser.getSurName());
	user.setPassword(MD5Util.getMD5(jsonUser.getPassword())); 
	user.setEnabled(false); 		
	user = (Users)DataAccessProxy.createNewRecord(user);		
		
	UserPojo userPojo = new UserPojo(user);
	try {
		uri = new URI(AutoRestore.getContextPath(req)   + "/" + Constants.APP_NAME + "/" + Constants.APP_VERSION +  "/users/"+user.getId());
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }
	authorizationState = null;
	return Response.status(Constants.HTTP_CODE.CREATED.getCode()).entity(userPojo).location(uri).build();
    }

}
