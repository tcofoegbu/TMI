/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

import java.util.Collection;

import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tracklist.TrackList;

/**
 * @author Charles Ofoegbu
 *
 */
public class TrackListsPojo {
    public TrackListsPojo(){}
    public TrackListsPojo(Collection<TrackList> trackLists, Collection<TrackList> existingTrackList){
	this.trackLists = trackLists;
	this.existingTrackList = existingTrackList;
    }
    
    private Collection<TrackList> trackLists;
    
    private Collection<TrackList> existingTrackList;

    public Collection<TrackList> getTrackLists() {
	return trackLists;
    }

    public void setTrackLists(Collection<TrackList> trackLists) {
	this.trackLists = trackLists;
    }
    public Collection<TrackList> getExistingTrackList() {
	return existingTrackList;
    }
    public void setExistingTrackList(Collection<TrackList> existingTrackList) {
	this.existingTrackList = existingTrackList;
    }

}
