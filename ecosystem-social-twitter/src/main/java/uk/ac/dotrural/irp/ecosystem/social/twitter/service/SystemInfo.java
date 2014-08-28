/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.service;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
/**
 * @author Charles Ofoegbu
 *
 */


@Path("/sysinfo")
@Produces({ MediaType.APPLICATION_JSON})
public class SystemInfo {
    private static Logger logger = LoggerFactory.getLogger(SystemInfo.class);
    private static Long tweetCount;
    
    @GET
    @Path("/tweet_count")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getItemsCount(@Context HttpServletRequest req, @Context HttpServletResponse res) throws IOException {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET called - get items count" );
	Response response = null;
	try{
	    response = Response.status(Constants.HTTP_CODE.OK.getCode()).entity(tweetCount).build();
	}catch(Exception e){
	    e.printStackTrace();
	}   
	return 	response;
	
    }

    public static Long getTweetCount() {
	return tweetCount;
    }

    public static void setTweetCount(Long tweetCount) {
	SystemInfo.tweetCount = tweetCount;
    }

    public static void updateTweetCount() {
	tweetCount++;
    }
   
     

}
