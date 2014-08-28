/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import twitter4j.GeoLocation;
import twitter4j.HashtagEntity;
import twitter4j.MediaEntity;
import twitter4j.Place;
import twitter4j.Status;
import twitter4j.URLEntity;
import twitter4j.UserMentionEntity;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.TweetSource;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.MD5Util;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.ServiceLocator;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.account.Account;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.audittrail.AuditTrail;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.geolocation.Geolocation;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.hashtag.Hashtag;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.media.Media;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.placeentity.PlaceEntity;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tracklist.TrackList;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tweet.Tweet;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.url.URL;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.usermention.UserMention;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userprofile.UserProfile;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userrole.UserRole;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.service.TweetDeckService;

/**
 * @author Charles Ofoegbu
 *
 */
public class DataAccessProxy {  
//    private static Logger logger = LoggerFactory.getLogger(DataAccessProxy.class);
    private static ServiceLocator serviceLocator = ServiceLocator.getInstance();
    private static TweetDeckService tService = serviceLocator.getTweetDeckService();
     
   
    public static Object createNewRecord(Object object){
        return tService.createNewRecord(object);         
    }
    
    public static void updateRecord(Object record){
    	tService.updateRecord(record);  
    } 
    
    public static void deleteRecord(Object object){
	tService.deleteRecord(object);
    }
    
    public static Object getRecordById(Class<?> entityClass, Long recordId){
        return tService.getRecordById(entityClass, recordId);
    }

    public static Collection<?> getAllRecords(Class<?> entityClass){
    	return tService.getAllRecords(entityClass);
    }
    
    public static Collection<?> getAllRecords(Class<?> entityClass, int start, int chunkSize){
    	return tService.getAllRecords(entityClass, start, chunkSize);
    }
    
    public static Collection<?> getAllDependentTweet(Long tId){
    	return (Collection<?>)tService.getAllRecordsByHQL("SELECT t from Tweet t where t.conversationLink.id ="+tId);
    }

    public static void deleteAllUserMentionByTweet(Tweet tweet){ 
	String hql = "DELETE UserMention u WHERE u.tweet.id = :tid";
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("tid", tweet.getId());
	tService.executeHQLUpdate(hql, queryParams);
    }
    
    public static void dereferenceConvesationLink(Tweet tweet){ 
	String hql = "UPDATE Tweet t set t.conversationLink = null WHERE t.conversationLink = :cLink";
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("cLink", tweet);
	tService.executeHQLUpdate(hql, queryParams);
    }

    
    public static Long getTrackItemCount(){
   	return (Long)tService.getUniqueRecordByHQL("SELECT count(i) from TrackList i ");
    }
    
    
    public static TrackList getTrackItemByName(String name){
	String hql = "SELECT t FROM TrackList t WHERE t.name = '" + name + "'";
        return (TrackList)tService.getUniqueRecordByHQL(hql);  
    } 
    public static UserRole getRoleByCode(String code){
	String hql = "SELECT u FROM UserRole u WHERE u.code = '" + code + "'";
        return (UserRole)tService.getUniqueRecordByHQL(hql);  
    } 
    
    public static UserRole getUserRoleByAuthority(String authority){
	String hql = "SELECT u FROM UserRole u WHERE lower(u.authority) = '" + authority.toLowerCase() + "'";
        return (UserRole)tService.getUniqueRecordByHQL(hql);  
    } 
    
    public static void deleteAllHashtagByTweet(Tweet tweet){ 
	String hql = "	DELETE Hashtag h WHERE h.tweet.id = :tid";        
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("tid", tweet.getId());
	tService.executeHQLUpdate(hql,queryParams);        
    }    

    public static void deleteAllURLByTweet(Tweet tweet){ 
	String hql = "DELETE URL u WHERE u.tweet.id = :tid";
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("tid", tweet.getId());
	tService.executeHQLUpdate(hql,queryParams);
    }
    
    public static void deleteAllMediaByTweet(Tweet tweet){ 
	String hql = "DELETE Media m WHERE m.tweet.id = :tid";
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("tid", tweet.getId());
	tService.executeHQLUpdate(hql,queryParams);
    }
    
    public static void deletePlaceRecordByTweetId(Long pId){ 
	String hql = "DELETE Media m WHERE m.id = :pId";	
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("pId", pId);
	tService.executeHQLUpdate(hql,queryParams);	
    }
    
    public static void deleteGeolocationRecordByTweetId(Long gId){ 
	String hql = "DELETE Geolocation g WHERE g.id = :gId";
	Map<String, Object> queryParams = new HashMap<String, Object>();
	queryParams.put("gId", gId);
	tService.executeHQLUpdate(hql,queryParams);
    }    
   
    public static Tweet getTweetByConversationLink(Long id){
	return (Tweet)tService.getUniqueRecordByHQL("SELECT t from Tweet t where t.id="+id+"");  
    }    
    
    public static Users getUserByUserName(String userName){
        return (Users)tService.getUniqueRecordByHQL("SELECT u from Users u where u.username='"+userName+"'");        
    }
    
    public static Account getAccountByTwitterId(Long twitterId){
	return (Account)tService.getUniqueRecordByHQL("SELECT a from Account a where a.twitterId=" + twitterId);       
    }
    
    public static Collection<?> getAllTrackListByType(String trackListType, Boolean isAllowSearchTerm){
	String appendedQuery = (isAllowSearchTerm) ? "' or t.type='" + Constants.TrackListType.SEARCH_TERM.getDisplayName() + "'":"'";
	return (Collection<?>) tService.getAllRecordsByHQL("SELECT t from TrackList t where t.type='" + trackListType + appendedQuery);
    }

    public static Tweet getTweetByOriginalTweetId(Long id){
	return (Tweet)tService.getUniqueRecordByHQL("SELECT t from Tweet t where t.originalTweetId="+id+"");  
    }  
    
    public static Users getUserByAuthorizationKey(String authorizationKey){
 	return (Users)tService.getUniqueRecordByHQL("SELECT u from Users u where u.authorizationKey='" + authorizationKey + "' and u.enabled="+Boolean.TRUE); 
     }
    
    public static Boolean userExist(String id){
	UserProfile userProfile = (UserProfile)tService.getUniqueRecordByHQL("SELECT u from UserProfile u where u.screenName='"+id+"'");
	return (userProfile == null) ? false : true;
    }

    public static Users getUserByEmailAndPassword(String email, String password){
	return (Users)tService.getUniqueRecordByHQL("SELECT u from Users u where u.username='" + email +"' and u.password='" + MD5Util.getMD5(password)+"'"); 
    }

    public static boolean isEmailExist(String email){
	return ((Users)tService.getUniqueRecordByHQL("SELECT u from Users u where u.username='" + email + "'") == null)?false:true; 
    }
    
    public static Long fetchConversationId(Tweet tweet){
	Long conversationId = null;
	if(tweet.getConversationLink() == null){
	    conversationId = tweet.getId();
	    return conversationId;
	}else{
	    Tweet parentConversation = getTweetByConversationLink(tweet.getConversationLink().getId());
	    conversationId = fetchConversationId(parentConversation);
	    parentConversation = null;
	}
	return conversationId;
    }
    
    public static Boolean isStakeHolder(String author){
	Boolean isStakeHoler = false;
	Criteria stakeHolderCriteria = tService.getCriteriaObject(TrackList.class);
	stakeHolderCriteria.add(Restrictions.ilike("name", author));
	TrackList foundStakeHolder = (TrackList)tService.getUniqueRecordByCriteria(stakeHolderCriteria);
	if(foundStakeHolder != null){
	    isStakeHoler = true;
	}
	foundStakeHolder = null;
	return isStakeHoler;
    }
        
    public static Collection<?> getUnLinkedTweet(int fetchSize){
    	Criteria reprocessCriteria = tService.getCriteriaObject(Tweet.class);
    	reprocessCriteria.add(Restrictions.isNull("conversationLink"));
    	reprocessCriteria.add(Restrictions.gt("inReplyToStatusId", 1l));
    	reprocessCriteria.add(Restrictions.isNull("conversationLinkAccessible")); 
    	reprocessCriteria.setMaxResults(fetchSize);
    	return (Collection<?>)tService.getAllRecordsByCriteria(reprocessCriteria);	
    }
    
    public static Geolocation createGeoLocation(GeoLocation recievedGeoLocation) {
   	Geolocation createdGeolocation = new Geolocation();
   	createdGeolocation.setLatitude(recievedGeoLocation.getLatitude());
   	createdGeolocation.setLongitude(recievedGeoLocation.getLongitude());
   	return (Geolocation) tService.createNewRecord(createdGeolocation);
       }

       public static PlaceEntity createPlaceEntity(Place recievedPlace) {
   	PlaceEntity createdPlaceEntity = new PlaceEntity();
   	createdPlaceEntity.setAccessLevel(recievedPlace.getAccessLevel());
   	createdPlaceEntity.setCountry(recievedPlace.getCountry());
   	createdPlaceEntity.setCountryCode(recievedPlace.getCountryCode());
   	createdPlaceEntity.setFullName(recievedPlace.getFullName());
   	createdPlaceEntity.setName(recievedPlace.getName());
   	createdPlaceEntity.setPlaceType(recievedPlace.getPlaceType());
   	createdPlaceEntity.setStreetAddress(recievedPlace.getStreetAddress());
   	if (recievedPlace.getBoundingBoxCoordinates().length > 0) {
   	    StringBuilder boundingBoxCoordinateBuilder = new StringBuilder();
   	    for (GeoLocation[] vGeo : recievedPlace.getBoundingBoxCoordinates()) {
   		for (GeoLocation hGeo : vGeo) {
   		    boundingBoxCoordinateBuilder.append("lat:").append(hGeo.getLatitude()).append(",");
   		    boundingBoxCoordinateBuilder.append("lng:").append(hGeo.getLongitude()).append("; ");
   		}
   	    }
   	    createdPlaceEntity.setBoundaryBoxType(boundingBoxCoordinateBuilder.toString());
   	}
   	return (PlaceEntity) tService.createNewRecord(createdPlaceEntity);
       }

       public static Tweet createTweet(Status recievedTweet, Tweet retreivedTweet, Geolocation geolocation, PlaceEntity placeEntity) throws org.hibernate.exception.ConstraintViolationException{
   	Tweet createdTweet = new Tweet();
   	createdTweet.setGeolocation(geolocation);
   	createdTweet.setPlaceEntity(placeEntity);
   	createdTweet.setAccessLevel(recievedTweet.getAccessLevel());
   	createdTweet.setCreatedAt(new Timestamp(recievedTweet.getCreatedAt().getTime())); 
   	createdTweet.setCurrentUserRetweetId(recievedTweet.getCurrentUserRetweetId());
   	createdTweet.setFavouriteCount(recievedTweet.getFavoriteCount());
   	createdTweet.setInReplyToScreenName(recievedTweet.getInReplyToScreenName());
   	createdTweet.setInReplyToStatusId(recievedTweet.getInReplyToStatusId());
   	createdTweet.setInReplyToUserId(recievedTweet.getInReplyToUserId());
   	createdTweet.setIsoLanguageCode(recievedTweet.getIsoLanguageCode());
   	createdTweet.setReTweeetCount(recievedTweet.getRetweetCount());	
   	createdTweet.setText(recievedTweet.getText());
   	createdTweet.setOriginalTweetId(recievedTweet.getId());
   	createdTweet.setUserId(recievedTweet.getUser().getId());
   	createdTweet.setAuthor(recievedTweet.getUser().getScreenName());
   	createdTweet.setStakeHolder(DataAccessProxy.isStakeHolder(createdTweet.getAuthor()));
   	createdTweet.setTimeStamp(new Timestamp(new Date().getTime()));
   	createdTweet.setSource(recievedTweet.getSource());
   	
   	if(retreivedTweet == null){
   	    createdTweet.setConversationLink(DataAccessProxy.getTweetByOriginalTweetId(recievedTweet.getInReplyToStatusId()));
   	}else{
   	    createdTweet.setConversationLink(retreivedTweet);
   	}
   	
   	for(TweetSource tweetSource : TweetSource.values()){
   	    if(recievedTweet.getSource().contains(tweetSource.getMatcher())){
   		createdTweet.setSource(tweetSource.getDisplayName());
   	    }
   	}
   	
   	if (recievedTweet.getContributors().length > 0) {
   	    StringBuilder contributorsBuilder = new StringBuilder();
   	    for (long contributors : recievedTweet.getContributors()) {
   		contributorsBuilder.append(contributors).append(";");
   	    }
   	    createdTweet.setContributors(contributorsBuilder.toString());
   	}	
//   	AuditTrail auditTrail = createAuditTrail(Constants.Actions.CREATE.name(), Constants.Entities.TWEET.name(), "TweetProcessor", null, "description", "initiators ip Address");
//   	createdTweet = (Tweet) tService.createNewRecord(createdTweet, auditTrail);	
   	createdTweet = (Tweet) tService.createNewRecord(createdTweet);	
//   	SystemInfo.updateTweetCount();
   	createdTweet.setConversationId(DataAccessProxy.fetchConversationId(createdTweet));
   	tService.updateRecord(createdTweet);
   	return createdTweet;
       }
       


       public static void createUserMention(Tweet tweet,
   	    UserMentionEntity[] mentions) {
   	for (UserMentionEntity mention : mentions) {
   	    UserMention userMention = new UserMention();
   	    userMention.setName(mention.getName());
   	    userMention.setScreenName(mention.getScreenName());
   	    userMention.setText(mention.getText());
   	    userMention.setTweet(tweet);
   	    userMention.setUserId(tweet.getUserId());
   	    tService.createNewRecord(userMention);
   	}
       }

       public static void createHashtag(Tweet tweet, HashtagEntity[] hashtags) {
   	for (HashtagEntity hashtag : hashtags) {
   	    Hashtag hTag = new Hashtag();
   	    hTag.setText(hashtag.getText());
   	    hTag.setTweet(tweet);
   	    tService.createNewRecord(hTag);
   	}
       }

       public static void createURL(Tweet tweet, URLEntity[] urls) {
   	for (URLEntity urlEntity : urls) {
   	    URL url = new URL();
   	    url.setDisplayURL(urlEntity.getDisplayURL());
   	    url.setExpandedURL(urlEntity.getExpandedURL());
   	    url.setText(urlEntity.getText());
   	    url.setTweet(tweet);
   	    url.setURL(urlEntity.getURL());
   	    tService.createNewRecord(url);
   	}
       }

       public static void createMedia(Tweet tweet, MediaEntity[] medias) {
   	for (MediaEntity mediaEntity : medias) {
   	    Media media = new Media();
   	    media.setMediaId(mediaEntity.getId());
   	    media.setMediaURL(mediaEntity.getMediaURL());
   	    media.setMediaURLHttps(mediaEntity.getMediaURLHttps());
   	    media.setTweet(tweet);
   	    tService.createNewRecord(media);
   	}
       }

       public static AuditTrail createAuditTrail(String action,String targetEntity, String initiator, Long recordId, String description, String initiatorIpAddress) {
   	AuditTrail createdAuditTrail = new AuditTrail();
   	createdAuditTrail.setAction(action);
   	createdAuditTrail.setInitiator(initiator);
   	createdAuditTrail.setRecordId(recordId);
   	createdAuditTrail.setDescription(description);
   	createdAuditTrail.setInitiatorIpAddress(initiatorIpAddress);
   	createdAuditTrail.setTargetEntity(targetEntity);
   	createdAuditTrail.setTimestamp(new Timestamp(new Date().getTime()));
   	return (AuditTrail) tService.createNewRecord(createdAuditTrail);
       }
}
