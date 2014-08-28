/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

import java.util.Date;

import twitter4j.User;

/**
 * @author Charles Ofoegbu
 *
 */
public class ProfilePojo {
    private String name;
    private String description;
    private String lang;
    private String location;
    private String screenName;
    private String timeZone;
    private String url;
    private Date createdAt;
    private Integer accessLevel;
    private Integer friendsCount;
    private Integer statusCount;
    private Long twitterId;    
    private long[] friends;
    private long[] followers;
    public ProfilePojo(){}
    
    public ProfilePojo(User user, long[] friends, long[] followers){
	this.name = user.getName();
	this.description = user.getDescription();
	this.lang = user.getLang();
	this.location = user.getLocation();
	this.screenName = user.getScreenName();
	this.timeZone = user.getTimeZone();
	this.url = user.getURL();
	this.createdAt = user.getCreatedAt();
	this.accessLevel = user.getAccessLevel();
	this.friendsCount = user.getFriendsCount();
	this.statusCount = user.getStatusesCount();
	this.twitterId = user.getId();
	this.friends = friends;
	this.followers = followers;
    }
    
    public String getName() {
	return name;
    }
    public void setName(String name) {
	this.name = name;
    }
    public String getDescription() {
	return description;
    }
    public void setDescription(String description) {
	this.description = description;
    }
    public String getLang() {
	return lang;
    }
    public void setLang(String lang) {
	this.lang = lang;
    }
    public String getLocation() {
	return location;
    }
    public void setLocation(String location) {
	this.location = location;
    }
    public String getScreenName() {
	return screenName;
    }
    public void setScreenName(String screenName) {
	this.screenName = screenName;
    }
    public String getTimeZone() {
	return timeZone;
    }
    public void setTimeZone(String timeZone) {
	this.timeZone = timeZone;
    }
    public String getUrl() {
	return url;
    }
    public void setUrl(String url) {
	this.url = url;
    }
    public Date getCreatedAt() {
	return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
	this.createdAt = createdAt;
    }
    public Integer getAccessLevel() {
	return accessLevel;
    }
    public void setAccessLevel(Integer accessLevel) {
	this.accessLevel = accessLevel;
    }
    public Integer getFriendsCount() {
	return friendsCount;
    }
    public void setFriendsCount(Integer friendsCount) {
	this.friendsCount = friendsCount;
    }
    public Integer getStatusCount() {
	return statusCount;
    }
    public void setStatusCount(Integer statusCount) {
	this.statusCount = statusCount;
    }
    public Long getTwitterId() {
	return twitterId;
    }
    public void setTwitterId(Long twitterId) {
	this.twitterId = twitterId;
    }
    public long[] getFriends() {
	return friends;
    }
    public void setFriends(long[] friends) {
	this.friends = friends;
    }
    public long[] getFollowers() {
	return followers;
    }
    public void setFollowers(long[] followers) {
	this.followers = followers;
    }
}
