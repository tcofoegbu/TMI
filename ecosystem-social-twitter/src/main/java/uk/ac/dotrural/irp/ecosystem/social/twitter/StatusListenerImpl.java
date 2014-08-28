/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.AutoRestore;

/**
 * @author Charles Ofoegbu
 *
 */
public class StatusListenerImpl implements StatusListener {
    
    private static Logger logger = LoggerFactory.getLogger(StatusListenerImpl.class);
    TwitterComponent twitterComponent = new TwitterComponent(this); 
    
    public void onStatus(Status recievedTweet) {
	//logger.debug("@" + recievedTweet.getUser().getScreenName() + " - " + recievedTweet.getText());
	TweetProcessor.storeTweet(recievedTweet, null);	
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }

    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        logger.debug("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
        TweetProcessor.processTweetDeletion(statusDeletionNotice.getStatusId());
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }

    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
	logger.debug("Got track limitation notice:" + numberOfLimitedStatuses);
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }

    public void onScrubGeo(long userId, long upToStatusId) {
	logger.debug("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }

    public void onStallWarning(StallWarning warning) {
	logger.debug("Got stall warning:" + warning);
	AutoRestore.evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded();
    }

    public void onException(Exception ex) {
	logger.error(">>>>>>>>>>>>>>>>>>>>>>>> error msg >>>>>>>>>> " + ex.getStackTrace().toString());
    }


}
