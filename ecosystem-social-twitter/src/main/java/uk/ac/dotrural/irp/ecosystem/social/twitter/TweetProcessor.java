/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter;


import java.util.Iterator;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.TwitterException;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.AutoRestore;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ConversationChainPojo;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.geolocation.Geolocation;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.placeentity.PlaceEntity;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tweet.Tweet;

/**
 * @author Charles Ofoegbu
 * 
 */ 
public class TweetProcessor { 

    private static Logger logger = LoggerFactory.getLogger(TweetProcessor.class);
    private static StringBuilder alreadyProcessed = new StringBuilder(); 
    
    public static Tweet storeTweet(Status tweet, Tweet retrievedTweet){
	Geolocation storedGeolocation = null;
	PlaceEntity storedPlaceEntity = null;
	Tweet storedTweet = null;
	try {
	    if (tweet.getGeoLocation() != null) {
		storedGeolocation = DataAccessProxy.createGeoLocation(tweet.getGeoLocation());
	    }
	    if (tweet.getPlace() != null) {
		storedPlaceEntity = DataAccessProxy.createPlaceEntity(tweet.getPlace());
	    }
	    storedTweet = DataAccessProxy.createTweet(tweet, retrievedTweet, storedGeolocation, storedPlaceEntity);
	    if (tweet.getUserMentionEntities() != null && tweet.getUserMentionEntities().length > 0) {
		DataAccessProxy.createUserMention(storedTweet,	tweet.getUserMentionEntities());
	    }
	    if (tweet.getHashtagEntities() != null && tweet.getHashtagEntities().length > 0) {
		DataAccessProxy.createHashtag(storedTweet, tweet.getHashtagEntities());
	    }
	    if (tweet.getURLEntities() != null && tweet.getURLEntities().length > 0) {
		DataAccessProxy.createURL(storedTweet, tweet.getURLEntities());
	    }
	    if (tweet.getMediaEntities() != null && tweet.getMediaEntities().length > 0) {
		DataAccessProxy.createMedia(storedTweet, tweet.getMediaEntities());
	    }
	    storedPlaceEntity = null;
	    storedGeolocation = null;
	} catch (org.hibernate.exception.ConstraintViolationException e) {
	    storedTweet = DataAccessProxy.getTweetByOriginalTweetId(tweet.getId());
	    //Roll-back duplicate placeEntity and Geolocation if already stored for identified duplicate record
	    if(storedGeolocation != null){
		DataAccessProxy.deleteRecord(storedGeolocation);
		storedGeolocation = null;
	    }
	    if(storedPlaceEntity != null){
		DataAccessProxy.deleteRecord(storedPlaceEntity);
		storedPlaceEntity = null;
	    }
	     logger.error(e.getStackTrace().toString());
	}
	return storedTweet;
    }

    public static void updateTweets() {
	Collection<?> unLinkedTweet = DataAccessProxy.getUnLinkedTweet(20);
	if (unLinkedTweet != null && !unLinkedTweet.isEmpty()) {
	    logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Running reprocess tweet Quartz Job! <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	    for (Iterator<?> it = unLinkedTweet.iterator(); it.hasNext();) {
		AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
		Tweet tweet = (Tweet) it.next();
		alreadyProcessed.append(tweet.getId()).append(",");
		//logger.debug(">>>>>>>>>>>>>>>>>>>>>>>re-processing "+tweet.getId() + "<<<<<<<<<<<<<<<<<<<<<<<<<<<");
		Tweet retrievedTweetFromDataBase = DataAccessProxy.getTweetByOriginalTweetId(tweet.getInReplyToStatusId());
		if (retrievedTweetFromDataBase != null) {
		    tweet.setConversationLink(retrievedTweetFromDataBase);
		    tweet.setConversationId(DataAccessProxy.fetchConversationId(tweet));
		    DataAccessProxy.updateRecord(tweet);
		} else {
		    try {
			Status fetchedTweet = TwitterComponent.getTwitter().showStatus(tweet.getInReplyToStatusId());
			//logger.debug("Found reprocess tweet >>>>>>>> "+fetchedTweet.getText());
			Tweet storedTweet = storeTweet(fetchedTweet, null);
			tweet.setConversationLink(storedTweet);
			tweet.setConversationId(DataAccessProxy.fetchConversationId(tweet));
			DataAccessProxy.updateRecord(tweet);
			
			storedTweet = null;
		    } catch (TwitterException e) {
			if(e.getErrorMessage().equalsIgnoreCase("Sorry, you are not authorized to see this status.") || e.getErrorMessage().equalsIgnoreCase("Sorry, that page does not exist")){
			    tweet.setConversationLinkAccessible(false);
			    DataAccessProxy.updateRecord(tweet);
			}else if(e.getErrorMessage().equalsIgnoreCase("Rate limit exceeded")){
			    //logger.error(">>>>>>>>>>>>>>>>>>>>>>>> Rate limit exceeded");
			    break;
			}else{
			     logger.error(e.getStackTrace().toString());
			}
		    }
		}
		retrievedTweetFromDataBase = null;
	    }
	    unLinkedTweet = null;
	}
    }

    public static void processTweetDeletion(Long tweetId){
	Tweet retrievedTweetFromDataBase = DataAccessProxy.getTweetByOriginalTweetId(tweetId);
	if(retrievedTweetFromDataBase != null){
	    DataAccessProxy.dereferenceConvesationLink(retrievedTweetFromDataBase);
	    deleteTweet(retrievedTweetFromDataBase);
	    retrievedTweetFromDataBase = null;
	}
    }
    
    @SuppressWarnings("unchecked")
    public static ConversationChainPojo getConversationChainIteratively(Collection<Tweet> tweets, ConversationChainPojo helperPojo){
	if(tweets == null || tweets.isEmpty()){
	    return helperPojo;
	}	
	for(Tweet tweet: tweets){
	    helperPojo.getConversationMap().put(tweet.getId(), tweet.getConversationLink().getId());
	    helperPojo.getConversations().add(tweet);
	    Collection<Tweet> dependentTweets = (Collection<Tweet>)DataAccessProxy.getAllDependentTweet(tweet.getId());
	    helperPojo = getConversationChainIteratively(dependentTweets, helperPojo);
	    dependentTweets = null;
	}	
	return helperPojo;
    }
    
    public static void deleteConversationChain(ConversationChainPojo ccPojo){
	int count = ccPojo.getConversations().size();
	while(count > 0){
	    for (Tweet tweet : ccPojo.getConversations()) {
		if (!ccPojo.getConversationMap().containsValue(tweet.getId())) {
		    try{
		    deleteTweet(tweet);
		    count --;
		    }catch(Exception e){
			 logger.error(e.getStackTrace().toString());
		    }
		}
	    }
	}
    }
    
    public static void deleteTweet(Tweet tweet){
	try{
	    DataAccessProxy.deleteAllUserMentionByTweet(tweet);// delete UserMention's
	    DataAccessProxy.deleteAllHashtagByTweet(tweet);// delete Hashtag's
	    DataAccessProxy.deleteAllURLByTweet(tweet);// delete URL's
	    DataAccessProxy.deleteAllMediaByTweet(tweet);// delete Media's
	    PlaceEntity pEntity = tweet.getPlaceEntity();
	    Geolocation geolocation = tweet.getGeolocation();
	    DataAccessProxy.deleteRecord(tweet);// delete Tweet
	    if (pEntity != null) {
		DataAccessProxy.deletePlaceRecordByTweetId(pEntity.getId());// delete Place
		pEntity = null;
	    }
	    if (geolocation != null) {
		DataAccessProxy.deleteGeolocationRecordByTweetId(geolocation.getId());// delete Geolocation
		geolocation = null;
	    }
	}catch(Exception e){
	     logger.error(e.getStackTrace().toString());
	}
    }
    
    public static String getAlreadyProcessed() {
	return alreadyProcessed.append("0").toString();
    }



}
