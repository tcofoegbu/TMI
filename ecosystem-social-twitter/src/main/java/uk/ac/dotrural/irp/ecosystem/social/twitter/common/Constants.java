/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;


/**
 * @author Charles Ofoegbu
 *
 */
public class Constants { 
    public static final String APP_NAME = "/ecosystem-social-twitter";
    public static final String APP_VERSION = "service";
    public static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd"); 
    
    public static enum Actions {
	CREATE, DELETE, UPDATE, READ;
    }

    public static enum Entities {
	TWEET, GEOLOCATION, PLACE_ENTITY, USER_MENTION, HASHTAG, URL, MEDIA, JUDGEMENT, CATEGORY;
    }
    
    public static enum TrackListType{
	TWITTER_HANDLE("handle"), HASHTAG("hashtag"), SEARCH_TERM("search-term");
	private String displayName;
	TrackListType(String displayName){
	    this.setDisplayName(displayName);
	}
	
	public String getDisplayName() {
	    return displayName;
	}
	public void setDisplayName(String displayName) {
	    this.displayName = displayName;
	}
    }
    
    public static enum TweetSource{
	WEB("web", "Web"), 
	IPHONE("Twitter for iPhone", "Twitter for iPhone"), 
	WINDOWS_PHONE("Twitter for Windows Phone", "Twitter for Windows Phone"), 
	IPAD("Twitter for iPad", "Twitter for iPad"),
	TWEETBOT_MAC("Tweetbot for Mac", "Tweetbot for Mac"),
	ANDROID_PHONE("Twitter for Android", "Twitter for Android"),
	BLACKBERRY_PHONE("Twitter for BlackBerry", "Twitter for BlackBerry"),	
	MOBILE_WEB("Mobile Web", "Mobile Web"),
	JANETTER("Janetter", "Janetter - http://janetter.net/"),
	TWITROCKER2("TwitRocker2 for iPad", "TwitRocker2 for iPad"),
	UBER_SOCIAL4IPHONE("UberSocial for iPhone", "UberSocial for iPhone"),
	DLVR_IT("dlvr.it", "dlvr.it"),
	TXT("txt", "txt"),
	ROUND_TEAM("RoundTeam", "RoundTeam - https://roundteam.co"),
	TWEEDLE("Tweedle", "Tweedle - http://tweedleapp.com/"),
	BANJO("Banjo", "Banjo - http://ban.jo/"),
	CARBON_4_ANDRIOD("Carbon for Android", "Carbon for Android"),
	GOOGLE("Google", "Google"),
	SEESMIC("Seesmic", "Seesmic - http://seesmic.com/"),
	TWITTER_FEED("twitterfeed", "twitterfeed - http://twitterfeed.com"),
	RS_RETWEETS("RS-Retweets", "RS-Retweets - http://radiosaltire.org"),
	TWEET_BOT_IOS("Tweetbot for iOS", "Tweetbot for iOS - http://tapbots.com/tweetbot"),
	TWEET_BOT_MAC("Tweetbot for Mac", "Tweetbot for Mac - http://tapbots.com/software/tweetbot/mac"),
	TRAFFIC_SCOTLAND("TrafficScotland", "TrafficScotland - http://trafficscotland.org/"),
	TWEETINGS_ANDROID("Tweetings for Android", "Tweetings for Android"),
	SPROUT_SOCIAL("Sprout Social", "Sprout Social - http://sproutsocial.com"),
	PLUME_ANDRIOD("Plume for Android", "Plume for Android - http://www.myplume.com/"),
	DABR("Dabr", "Dabr - http://m.dabr.co.uk"),
	BLAQ_BLACKBERRY("Blaq for BlackBerry", "Blaq for BlackBerry"),
	HOTTSUITE("HootSuite", "HootSuite - http://www.hootsuite.com"),
	TWEET_BUTTON("Tweet Button", "Tweet Button"),
	CONVERSOCIAL("Conversocial", "Conversocial - http://www.conversocial.com"),
	TWEETCASTER_ANDRIOD("TweetCaster for Android", "TweetCaster for Android - http://www.tweetcaster.com"),
	TWITTER_4_MAC("Twitter for Mac", "Twitter for Mac"),
	TWEET_DECK("TweetDeck", "TweetDeck"),
	TWITTERLATOR("Twittelator", "Twittelator - http://stone.com/Twittelator"),
	ECHOFON("Echofon", "Echofon - http://www.echofon.com/"),
	TWITBIN("Twitbin", "Twitbin - http://www.twitbin.com"),
	IOS("iOS", "iOS"),
	FIRST_TRAVEL_NEWS("firsttravelnews", "First Travel News - http://www.firstgroup.com");
	
	private String matcher;
	private String displayName;
	TweetSource(String matcher, String displayName){
	    this.matcher = matcher;
	    this.displayName = displayName;
	}
	
	public String getMatcher(){
	    return matcher;
	}
	public String getDisplayName(){
	    return displayName;
	}
    }
    

    public static enum HTTP_CODE{
	CONTINUE(100),
	OK(200),
	CREATED(201),
	ACCEPTED(202),
	NO_CONTENT(204),
	BAD_REQUEST(400),
	UNAUTHORIZED(401),
	PAYMENT_REQUIRED(402),
	FORBIDDEN(403),
	NOT_FOUND(404),
	METHOD_NOT_ALLOWED(405),
	NOT_ACCEPTED(406), 
	PROXY_AUTHENTICATION_REQUIRED(407),
	REQUEST_TIME_OUT(408),
	CONFLICT(409),
	GONE(410),
	UNSUPPORTED_MEDIA_TYPE(415),
	INTERNAL_SERVER_ERROR(500),
	NOT_IMPLEMENTED(501),
	BAD_GATEWAY(502),
	SERVICE_UNAVAILABLE(503),
	GATEWAY_TIME_OUT(504),
	HTTP_VERSION_NOT_SUPPORTED(505);	
	
	private int code;	
	HTTP_CODE(int code){
	    this.setCode(code);
	}
	public int getCode() {
	    return code;
	}
	public void setCode(int code) {
	    this.code = code;
	}
    }
    
    public static enum AccessStatus {
	LOGOUT_SUCCESSFUL("Logout ok!", HTTP_CODE.OK.getCode()), 
	LOGOUT_FAILED("Logout failed!", HTTP_CODE.NOT_ACCEPTED.getCode());
	private String status;
	private int code;
	AccessStatus(String status, int code) {
	    this.status = status;
	    this.code = code;
	}
	AccessStatus() {
	}
	public String getStatus() {
	    return status;
	}
	public int getCode(){
	    return code;
	}

    }
    
    
    public static enum Role{
	ADMIN("Admin"),
	SUPER_ADMIN("Super Admin"),
	USER("User");
	private String role;
	Role(String role){
	    this.setRole(role);
	}
	
	public String getRole() {
	    return role;
	}
	public void setRole(String role) {
	    this.role = role;
	}
    }

}
