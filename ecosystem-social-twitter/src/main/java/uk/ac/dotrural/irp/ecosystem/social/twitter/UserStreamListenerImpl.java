/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.DirectMessage;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.User;
import twitter4j.UserList;
import twitter4j.UserStreamListener;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.AutoRestore;

/**
 * @author Charles Ofoegbu
 *
 */
public class UserStreamListenerImpl  implements UserStreamListener{
    
    private static Logger logger = LoggerFactory.getLogger(UserStreamListenerImpl.class);
    TwitterComponent twitterComponent = new TwitterComponent(this);    

    public void onStatus(Status recievedTweet) {	
	//logger.debug("@" + recievedTweet.getUser().getScreenName() + " tweeted - " + recievedTweet.getText());
	TweetProcessor.storeTweet(recievedTweet, null);
	
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }
    
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {	
        logger.debug("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        TweetProcessor.processTweetDeletion(statusDeletionNotice.getStatusId());
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {	
    }

    public void onScrubGeo(long userId, long upToStatusId) {	
    }

    public void onStallWarning(StallWarning warning) {	
    }

    public void onException(Exception ex) {
	logger.error(">>>>>>>>>>>>>>>>>>>>>>>> error msg >>>>>>>>>> " + ex.getStackTrace().toString());	
    }

    public void onDeletionNotice(long directMessageId, long userId) {	
    }

    public void onFriendList(long[] friendIds) {	
    }

    public void onFavorite(User source, User target, Status favoritedStatus) {	
    }

    public void onUnfavorite(User source, User target, Status unfavoritedStatus) {	
    }

    public void onFollow(User source, User followedUser) {	
    }

    public void onDirectMessage(DirectMessage directMessage) {	
    }

    public void onUserListMemberAddition(User addedMember, User listOwner, UserList list) {	
    }

    public void onUserListMemberDeletion(User deletedMember, User listOwner, UserList list) {	
    }

    public void onUserListSubscription(User subscriber, User listOwner, UserList list) {	
    }

    public void onUserListUnsubscription(User subscriber, User listOwner, UserList list) {	
    }

    public void onUserListCreation(User listOwner, UserList list) {	
    }

    public void onUserListUpdate(User listOwner, UserList list) {	
    }

    public void onUserListDeletion(User listOwner, UserList list) {	
    }

    public void onUserProfileUpdate(User updatedUser) {	
    }

    public void onBlock(User source, User blockedUser) {	
    }
    
    public void onUnblock(User source, User unblockedUser) {	
    }


}
