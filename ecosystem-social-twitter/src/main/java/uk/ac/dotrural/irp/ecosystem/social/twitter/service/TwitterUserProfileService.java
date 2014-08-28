/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.User;
import uk.ac.dotrural.irp.ecosystem.social.twitter.TwitterComponent;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.AutoRestore;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ProfilePojo;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.account.Account;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.followers.Followers;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.friends.Friends;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userprofile.UserProfile;

/**
 * @author Charles Ofoegbu
 *
 */

@Path("/profile")
@Produces({ MediaType.APPLICATION_JSON})
public class TwitterUserProfileService {
    private static Logger logger = LoggerFactory.getLogger(TwitterUserProfileService.class);
    
    @POST
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response pullUser(@PathParam("id") String id, @Context HttpServletRequest req){
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Post called - create user profile " );
	URI uri = null;
	UserProfile userProfile = new UserProfile();
	ProfilePojo pp = null;
	try{
	    User user = TwitterComponent.getTwitter().showUser(id);
	    if (user == null) {
		user = TwitterComponent.getTwitter().showUser(Long.valueOf(id));
	    }
	    
	    if(user != null && !DataAccessProxy.userExist(id)){
		userProfile.setAccessLevel(user.getAccessLevel());
		userProfile.setCreatedAt(user.getCreatedAt());
		userProfile.setDescription(user.getDescription());
		userProfile.setFollowersCount(user.getFollowersCount());
		userProfile.setFriendsCount(user.getFriendsCount());
		userProfile.setStatusCount(user.getStatusesCount());
		userProfile.setLang(user.getLang());
		userProfile.setLocation(user.getLocation());
		userProfile.setName(user.getName());
		userProfile.setScreenName(user.getScreenName());
		userProfile.setTimeZone(user.getTimeZone());
		userProfile.setUrl(user.getURL());
		userProfile.setTwitterId(Long.valueOf(user.getId()));
		
		userProfile = (UserProfile)DataAccessProxy.createNewRecord(userProfile);
		
		long[] followersList = TwitterComponent.getTwitter().getFollowersIDs(user.getId(), -1).getIDs();
		long[] friendsList = TwitterComponent.getTwitter().getFriendsIDs(user.getId(), -1).getIDs();
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> followers count: "+followersList.length);
		logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> friends count: "+friendsList.length);
		for(long userId : friendsList){
		    Account account = (Account) DataAccessProxy.getAccountByTwitterId(userId);
		    if(account == null){
			account = new Account();
			account.setTwitterId(userId);
			account = (Account)DataAccessProxy.createNewRecord(account);
		    }
		    Friends friend = new Friends();
		    friend.setAccount(account);
		    friend.setUserProfile(userProfile);
		    friend.setTimeStamp(new Timestamp(new Date().getTime()));
		    DataAccessProxy.createNewRecord(friend);
		}
		
		for(long userId : followersList){
		    Account account = (Account) DataAccessProxy.getAccountByTwitterId(userId);
		    if(account == null){
			account = new Account();
			account.setTwitterId(userId);
			account = (Account)DataAccessProxy.createNewRecord(account);
		    }
		    Followers follower = new Followers();
		    follower.setAccount(account);
		    follower.setUserProfile(userProfile);
		    follower.setTimeStamp(new Timestamp(new Date().getTime()));
		    DataAccessProxy.createNewRecord(follower);
		}
		pp = new ProfilePojo(user, friendsList, followersList);
	    }
	    
	    try {
		uri = new URI(AutoRestore.getContextPath(req)  + "/" + Constants.APP_NAME + "/" + Constants.APP_VERSION +  "/profile/"+userProfile.getId());
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }
	}catch(Exception e ){
	    e.printStackTrace();
	}
	return Response.status(201).entity(pp).location(uri).build();
    }
    
}
