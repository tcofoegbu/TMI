/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tweet.Tweet;

/**
 * @author Charles Ofoegbu
 *
 */
public class ConversationChainPojo {
    private HashMap<Long,Long> conversationMap; 
    private ArrayList<Tweet> conversations;
    
    public ConversationChainPojo(){
	this.conversationMap = new HashMap<Long, Long>();
	this.conversations = new ArrayList<Tweet>();
    }
    public HashMap<Long,Long> getConversationMap() {
	return conversationMap;
    }
    public void setConversationMap(HashMap<Long,Long> conversationMap) {
	this.conversationMap = conversationMap;
    }
    public ArrayList<Tweet> getConversations() {
	return conversations;
    }
    public void setConversations(ArrayList<Tweet> conversations) {
	this.conversations = conversations;
    }
    
}
