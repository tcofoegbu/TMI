/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.FilterQuery;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.User;
import twitter4j.UserStreamListener;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tracklist.TrackList;

/**
 * @author Charles Ofoegbu
 * 
 */


public class TwitterComponent {
    private static Twitter twitter;
    private static TwitterStream twitterStream;
    private static StatusListener statusListenerImpl;
    private static UserStreamListener userStreamListenerImpl;
    
    private static ArrayList<String> trackList = new ArrayList<String>();
    private static Logger logger = LoggerFactory.getLogger(TwitterComponent.class);
    
    public TwitterComponent(StatusListener customStatusListenerImpl) {
	twitterStream = TwitterStreamFactory.getSingleton();
	setTwitter(TwitterFactory.getSingleton()); 
	statusListenerImpl = customStatusListenerImpl; 
	twitterStream.addListener(statusListenerImpl);	
	updateFollowerList();	
	populateTrackList();
	long[] followArray = null;
	String[] trackArray = getTrackList().toArray(new String[getTrackList().size()]);
	// filter() method internally creates a thread which manipulates
	// TwitterStream and calls these adequate listener methods continuously.
	twitterStream.filter(new FilterQuery(0, followArray, trackArray));
    }   
  
    
    public TwitterComponent(UserStreamListener customUserStreamListenerImpl){
   	twitterStream = TwitterStreamFactory.getSingleton();
   	setTwitter(TwitterFactory.getSingleton());
   	userStreamListenerImpl = customUserStreamListenerImpl;
   	twitterStream.addListener(userStreamListenerImpl); 
   	updateFollowerList();	
   	twitterStream.user();      	
    }

    public static void closeStream(){
	twitterStream.cleanUp();
	twitterStream.shutdown();
    }
    
    @SuppressWarnings("unchecked")
    public static void updateFollowerList(){
	ArrayList<Long> followedUser = new ArrayList<Long>();	
	Collection<TrackList> trackTwitterHandles = (Collection<TrackList>) DataAccessProxy.getAllTrackListByType(Constants.TrackListType.TWITTER_HANDLE.getDisplayName(), false);
	if (trackTwitterHandles != null && !trackTwitterHandles.isEmpty()) {
	    for(TrackList tList: trackTwitterHandles){
		try {
		    User twitterUser = twitter.showUser(tList.getName());
		    followUser(twitterUser.getScreenName());
		    followedUser.add(twitterUser.getId());
		    logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> adding freind : "+twitterUser.getScreenName());
		} catch (TwitterException e) {
		    logger.error(e.getStackTrace().toString());
		}
	    }
	}else{
	    //TODO implement logic to populate the database with a default twitter handle tracklist from a properties file
	}
    }

    
    public static void updateTimeLineJob(Date startDate, Date endDate){
	@SuppressWarnings("unchecked")
	Collection<TrackList> trackTwitterHandles = (Collection<TrackList>) DataAccessProxy.getAllTrackListByType(Constants.TrackListType.TWITTER_HANDLE.getDisplayName(), false);
	if (trackTwitterHandles != null && !trackTwitterHandles.isEmpty()) {
	    for(TrackList tList: trackTwitterHandles){
		try {
		    Collection<Status> timelineStatus = getUserTimeLineByUsernameAndStartDataAndEndDate( tList.getName(), startDate, endDate);
		    for(Status currentTimelineStatus: timelineStatus){
			logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> storing retrieved timeling : "+ currentTimelineStatus.getText());
			TweetProcessor.storeTweet(currentTimelineStatus, null);
		    }		   
		} catch (Exception e) {
		    logger.error(e.getStackTrace().toString());
		}
	    }
	}	
    }
        
    public static Collection<Status> getUserTimeLineByUsernameAndStartDataAndEndDate(String screenName, Date startDate, Date endDate){
	ArrayList<Status> retrievedTimeline = new ArrayList<Status>();
	try {
	    ResponseList<Status> userTimeline = getTwitter().getUserTimeline(screenName);
	    
	    for(Status currentStatus : userTimeline){
		if(currentStatus.getCreatedAt().after(startDate) && currentStatus.getCreatedAt().before(endDate)){
		    retrievedTimeline.add(currentStatus);
		}
	    }	    
	} catch (TwitterException e) {
	    logger.error(e.getStackTrace().toString());
	}
	return retrievedTimeline;
    }
    
    public void populateTrackList() {
	Collection<?> trackHashTags = DataAccessProxy.getAllRecords(TrackList.class);
	if (trackHashTags != null && !trackHashTags.isEmpty()) {
	    for (Iterator<?> trackListIter = trackHashTags.iterator(); trackListIter.hasNext();) {
		TrackList tList = (TrackList) trackListIter.next();
		getTrackList().add(tList.getName());
	    }
	}

    }
    
    public void sendDirectMessage(String screenName, String text) {
	try {
	    getTwitter().sendDirectMessage(screenName, text);
	} catch (TwitterException e) {
	    e.printStackTrace();
	}

    }

    public Collection<Status> getAllTweetsByUserScreenName(String screenName) {
	List<Status> userTweets = new ArrayList<Status>();
	try {
	    List<Status> userTimeLine = getTwitter().getUserTimeline(screenName);	    
	    for (Status status : userTimeLine) {
		if (status.getUser().getScreenName().equalsIgnoreCase(screenName)) {
		    userTweets.add(status);
		}
	    }
	} catch (TwitterException e) {
	    e.printStackTrace();
	}
	return userTweets;
    }

    public Collection<Status> getTweetsBySearchTerm(String searchTerm) {
	List<Status> tweetList = new ArrayList<Status>();
	try {
	    Query query = new Query(searchTerm);
	    QueryResult result;
	    do {
		result = getTwitter().search(query);
		List<Status> tweets = result.getTweets();
		tweetList.addAll(tweets);
	    } while ((query = result.nextQuery()) != null);

	} catch (TwitterException te) {
	    te.printStackTrace();
	}
	return tweetList;
    }

    public Collection<Status> getTweetsByHashTag(String userId, String hashTag) {
	List<Status> tweetList = new ArrayList<Status>();
	try {
	    Query query = new Query(hashTag);
	    QueryResult result;
	    do {
		result = getTwitter().search(query);
		List<Status> tweets = result.getTweets();
		tweetList.addAll(tweets);
	    } while ((query = result.nextQuery()) != null);

	} catch (TwitterException te) {
	    te.printStackTrace();
	}
	return tweetList;
    }
    
    public Collection<Status> getTweetsByTwitterHandle(String userId, String twitterHandle) {
	// TODO Aimplement getTweetsByTwitterHandle
	return null;
    }

    public Collection<Status> getTweetsByUserIdAndSearchTerm(String userId, String searchTerm) {
	// TODO implement getTweetsByUserIdAndSearchTerm
	return null;
    }

    public Collection<Status> getTweetsByUserIdAndHashTag(String userId, String hashTag) {
	// TODO implement getTweetsByUserIdAndHashTag
	return null;
    }

    public Collection<Status> getTweetsByUserIdAndTwitterHandle(String userId, String twitterHandle) {
	// TODO implement getTweetsByUserIdAndTwitterHandle
	return null;
    }

    public long[] getFollowersId(String screenName) {
	long[] followersIds = null;
	try {
	    followersIds = getTwitter().getFollowersIDs(screenName, -1).getIDs();
	} catch (TwitterException e) {
	    e.printStackTrace();
	}
	return followersIds;
    }

    public static long[] getFriendsId(String screenName) {
	long[] friendsIds = null;
	try {
	    friendsIds = getTwitter().getFriendsIDs(screenName, -1).getIDs();
	} catch (TwitterException e) {
	    e.printStackTrace();
	}
	return friendsIds;
    }
    
//    public static long[] getFriendsList(String screenName) {
//	PagableResponseList<User> friendsList = (PagableResponseList<User>) new ArrayList<User>();
//	try {
//	    friendsList = getTwitter().getFriendsList(screenName, -1);
//	} catch (TwitterException e) {
//	    e.printStackTrace();
//	}
//	return friendsIds;
//    }

    public Collection<?> retrieveConversationBetweenUsers(String tweetEntryId, String userId1, String userId2) {
	// TODO implement retrieving of conversations b/w two users
	return null;
    }

    public static void followUser(String screenName) {
	try {
	    getTwitter().createFriendship(screenName);
	} catch (TwitterException e) {
	    e.printStackTrace();
	}

    }

    public void unFollowUser(String screenName) {
	try {
	    getTwitter().destroyFriendship(screenName);
	} catch (TwitterException e) {
	    e.printStackTrace();
	}

    }

    public static Twitter getTwitter() {
	return twitter;
    }

    public static void setTwitter(Twitter _twitter) {
	twitter = _twitter;
    }

    public static ArrayList<String> getTrackList() {
	return trackList;
    }

    public static void setTrackList(ArrayList<String> trackList) {
	TwitterComponent.trackList = trackList;
    }

}


















