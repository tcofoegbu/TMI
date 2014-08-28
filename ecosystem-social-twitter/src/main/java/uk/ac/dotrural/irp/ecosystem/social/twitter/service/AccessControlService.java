/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.AccessStatus;
import uk.ac.dotrural.irp.ecosystem.social.twitter.exceptions.AuthenticationException;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.AccessPojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.AuthenticationPojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ExceptionPojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.security.ApplicationSecurity;


/**
 * @author Charles Ofoegbu
 *
 */

@Path("/auth")
@Produces({ MediaType.APPLICATION_JSON})
public class AccessControlService {

    private static Logger logger = LoggerFactory.getLogger(AccessControlService.class);
    @POST
    @Produces({ MediaType.APPLICATION_JSON})     
    public Response logIn(AuthenticationPojo authPojo) {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> POST called - login -- Authorization : ");
	AccessPojo accessPojo = null;
	try {
	    accessPojo = ApplicationSecurity.AuthenticateUser(authPojo.getEmail(), authPojo.getPassword());
	} catch (AuthenticationException e) {
	    return Response.status(Constants.HTTP_CODE.NOT_ACCEPTED.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.NOT_ACCEPTED, new String[]{e.getTitle()}, e.getExceptionMessage())).build();
	}
	
	return Response.status(Constants.HTTP_CODE.OK.getCode()).entity(accessPojo).build();	    
    }
    
    @POST
    @Path("/all/logout")
    @Produces({ MediaType.APPLICATION_JSON})     
    public Response logOut(@Context HttpHeaders headers) {	
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> POST called - logout -- Authorization : ");
	if(headers.getRequestHeader("Authorization") == null){
	    return Response.status(Constants.HTTP_CODE.NOT_ACCEPTED.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.NOT_ACCEPTED, new String[]{"No authorization key supplied!"}, "A valid Authorization key is required for this service.")).build();
	}
	
	String apiKey = headers.getRequestHeader("Authorization").get(0);
	AccessStatus accessStatus = ApplicationSecurity.logUserOut(apiKey);
	return Response.status(accessStatus.getCode()).entity(accessStatus).build();	    

    }

}
