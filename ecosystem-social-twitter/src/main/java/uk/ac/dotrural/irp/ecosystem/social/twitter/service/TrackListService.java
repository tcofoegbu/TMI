/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.TrackListType;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.AutoRestore;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ApplicationStatePojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.ExceptionPojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.pojo.TrackListsPojo;
import uk.ac.dotrural.irp.ecosystem.social.twitter.security.ApplicationSecurity;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tracklist.TrackList;


/**
 * @author Charles Ofoegbu
 *
 */

@Path("/tracklist")
@Produces({ MediaType.APPLICATION_JSON})
public class TrackListService {


    private static Logger logger = LoggerFactory.getLogger(TrackListService.class);
    
    @GET
    @Produces({ MediaType.APPLICATION_JSON})     
    @SuppressWarnings("unchecked")
    public Response getItems( @QueryParam("start") Integer start,  @QueryParam("size") Integer chunkSize, @Context HttpServletRequest req) {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET called - get all tracklist " );	
	
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req, Constants.Role.ADMIN, Constants.Role.SUPER_ADMIN, Constants.Role.USER);
	if(authorizationState.getExceptionPojo() != null){
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	Response response = null;
	TrackListsPojo trackListsPojo = null;
	try{
	    Collection<TrackList> trackLists = (start != null && chunkSize != null) ? (Collection<TrackList>) DataAccessProxy.getAllRecords(TrackList.class, start, chunkSize) : (Collection<TrackList>) DataAccessProxy.getAllRecords(TrackList.class);
	    if(trackLists != null && !trackLists.isEmpty()){
		trackListsPojo = new TrackListsPojo(trackLists, new ArrayList<TrackList>());
	    }
	    response = Response.status(Constants.HTTP_CODE.OK.getCode()).entity(trackListsPojo).build();
	}catch(Exception e){
	    e.printStackTrace();
	}
	authorizationState = null;
	
	
	return response; 
    }
    
    @GET
    @Path("/count")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getItemsCount(@Context HttpServletRequest req, @Context HttpServletResponse res) throws IOException {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET called - get items count" );
	
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req,  Constants.Role.SUPER_ADMIN, Constants.Role.ADMIN, Constants.Role.USER);
	if(authorizationState.getExceptionPojo() != null){
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	Response response = null;
	try{
	    Long trackItemsCount = (Long) DataAccessProxy.getTrackItemCount(); 
	    response = Response.status(Constants.HTTP_CODE.OK.getCode()).entity(trackItemsCount).build();
	}catch(Exception e){
	    e.printStackTrace();
	}   
	authorizationState = null;
	return 	response;	
    }
    

    @GET
    @Path("/{id}")
    @Produces({ MediaType.APPLICATION_JSON})
    public Response getItemById(@PathParam("id") Long id, @Context HttpServletRequest req, @Context HttpServletResponse res) throws IOException {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> GET called - get item by id" );
	
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req, Constants.Role.SUPER_ADMIN, Constants.Role.ADMIN, Constants.Role.USER);
	if(authorizationState.getExceptionPojo() != null){
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	TrackList item = (TrackList)DataAccessProxy.getRecordById(TrackList.class, id);
	if(item == null){
	    return Response.status(Constants.HTTP_CODE.NOT_FOUND.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.NOT_FOUND, null, "Track item with id = "+id+" was not found!")).build();
	}else{
	    authorizationState = null;
	    return Response.status(200).entity(item).build();	    
	}
    }


    
    @POST
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response createItem(TrackList jsonItem,  @Context HttpServletRequest req, @Context HttpServletResponse res)  throws IOException {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Post called - create item ");
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req, Constants.Role.SUPER_ADMIN, Constants.Role.ADMIN);
	if (authorizationState.getExceptionPojo() != null) {
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	URI uri = null;
	TrackList item = new TrackList();
	ArrayList<TrackList> tracklists = new ArrayList<TrackList>();
	ArrayList<TrackList> duplicateTrackItems = new ArrayList<TrackList>();
	String trackList = jsonItem.getName();
	String tracklistType = null;
	for (TrackListType trackItemType : TrackListType.values()) {
	    if ((jsonItem.getType().toLowerCase().equals(trackItemType.getDisplayName()))) {
		tracklistType = jsonItem.getType().toLowerCase();
	    }
	}
	if (tracklistType == null) {
	    return Response.status(Constants.HTTP_CODE.BAD_REQUEST.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.BAD_REQUEST, null, "Invalid tracklist type supplied!")).build();
	}

	if (trackList.contains(",")) {
	    for (String trackItem : trackList.split(",")) {
		String trackItemStr = trackItem.trim();
		if (trackItemStr.length() == 0) {
		    continue;
		}
		TrackList foundTrackItem = DataAccessProxy.getTrackItemByName(trackItemStr);
		if (foundTrackItem == null) {
		    TrackList newTrackList = new TrackList();
		    try {
			newTrackList.setName(trackItemStr);
			newTrackList.setType(tracklistType);
			newTrackList = (TrackList) DataAccessProxy.createNewRecord(newTrackList);
			tracklists.add(newTrackList);
			newTrackList = null;
		    } catch (org.hibernate.exception.ConstraintViolationException ex) {
			duplicateTrackItems.add(newTrackList);
			logger.error("The supplied " + tracklistType + " of '" + trackItem.trim() + "' already exists in the database!");
		    }
		} else {
		    duplicateTrackItems.add(foundTrackItem);
		}

		foundTrackItem = null;
	    }

	    TrackListsPojo tLP = new TrackListsPojo(tracklists, duplicateTrackItems);
	    duplicateTrackItems = null;
	    return Response.status(Constants.HTTP_CODE.CREATED.getCode()).entity(tLP).location(uri).build();
	} else {
	    String trackItemStr = trackList.trim();
	    if (trackItemStr.length() == 0) {
		return Response.status(Constants.HTTP_CODE.BAD_REQUEST.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.BAD_REQUEST, null, "Cannot add an empty Track item!")).build();
	    }
	    TrackList foundTrackItem = DataAccessProxy.getTrackItemByName(trackItemStr);
	    if (foundTrackItem == null) {
		item.setName(trackItemStr);
		item.setType(tracklistType);
		try {
		    item = (TrackList) DataAccessProxy.createNewRecord(item);
		    uri = new URI(AutoRestore.getContextPath(req) + "/" + Constants.APP_NAME + "/" + Constants.APP_VERSION + "/tracklist/" + item.getId());
		} catch (URISyntaxException e) {
		    e.printStackTrace();
		} catch (org.hibernate.exception.ConstraintViolationException ex) {
		    duplicateTrackItems.add(item);
		}

	    } else {
		duplicateTrackItems.add(item);
		return Response.status(Constants.HTTP_CODE.BAD_REQUEST.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.BAD_REQUEST, null, "The supplied Track item already exists!")).build();
	    }

	    Response re = getItemById(item.getId(), req, res);
	    authorizationState = null;
	    tracklists.add((TrackList) re.getEntity());
	    TrackListsPojo tLP = new TrackListsPojo(tracklists,  duplicateTrackItems);
	    duplicateTrackItems = null;
	    return Response.status(201).entity(tLP).location(uri).build();
	}

    }

    @PUT 
    @Path("/{id}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public Response updateItem(@PathParam("id") Long id, TrackList jsonItem, @Context HttpServletRequest req, @Context HttpServletResponse res){
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Put called - update item" );	
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req,  Constants.Role.SUPER_ADMIN);
	if(authorizationState.getExceptionPojo() != null){
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	URI uri = null;
	TrackList item = null;
	Response response = null;
	try{
	    item = (TrackList)DataAccessProxy.getRecordById(TrackList.class, id);
	    if(item == null){
		authorizationState = null;
		return Response.status(Constants.HTTP_CODE.NOT_FOUND.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.NOT_FOUND, null, "Track item with id = "+id+" was not found!")).build();		
	    }
	    item.setName(jsonItem.getName());
	    item.setType(jsonItem.getType());
	    DataAccessProxy.updateRecord(item);
	    try {
		uri = new URI(AutoRestore.getContextPath(req) + "/" + Constants.APP_NAME + "/" + Constants.APP_VERSION + "/item/" + id);
	    } catch (URISyntaxException e) {
		e.printStackTrace();
	    }
	    Response re = getItemById(id, req, res);
	    response = Response.status(200).entity(re.getEntity()).location(uri).build();
	}catch(Exception e ){
	    e.printStackTrace();
	}
	authorizationState = null;
	return response;
    }    
    
    @DELETE 
    @Path("/{id}")    
    @Produces({ MediaType.APPLICATION_JSON})  
    public Response deleteItem(@PathParam("id") Long id, @Context HttpServletRequest req){
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> DELETE called - delete item" );	
	ApplicationStatePojo authorizationState = ApplicationSecurity.isAuthorized(req,  Constants.Role.SUPER_ADMIN);
	if(authorizationState.getExceptionPojo() != null){
	    return Response.status(authorizationState.getExceptionPojo().getStatusCode()).entity(authorizationState.getExceptionPojo()).build();
	}
	
	TrackList item = null;
	Response response = null;
	try{
	    item = (TrackList)DataAccessProxy.getRecordById(TrackList.class, id);
	    if(item == null){
		authorizationState = null;
		return Response.status(Constants.HTTP_CODE.NOT_FOUND.getCode()).entity(new ExceptionPojo(Constants.HTTP_CODE.NOT_FOUND, null, "Track item with id = "+id+" was not found!")).build();		
	    }else{		
		DataAccessProxy.deleteRecord(item);	
		response = Response.status(200).build();
	    }
	}catch(Exception e ){
	    e.printStackTrace();
	}
	authorizationState = null;
	return response;
    }
   
}
