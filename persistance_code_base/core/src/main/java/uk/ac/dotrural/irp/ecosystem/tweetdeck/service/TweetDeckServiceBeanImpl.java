// license-header java merge-point
package uk.ac.dotrural.irp.ecosystem.tweetdeck.service;

/**
 * @see uk.ac.dotrural.irp.ecosystem.tweetdeck.service.TweetDeckService
 */
 
 // all the imports in the system 
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tweet.Tweet;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.usermention.UserMention;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.url.URL;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.placeentity.PlaceEntity;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.media.Media;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.hashtag.Hashtag;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.geolocation.Geolocation;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.judgement.Judgement;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.category.Category;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userrole.UserRole;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.tracklist.TrackList;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.account.Account;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.followers.Followers;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.friends.Friends;
import	uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userprofile.UserProfile;
//import hibernate
import org.hibernate.Hibernate;

public class TweetDeckServiceBeanImpl
    extends TweetDeckServiceBean
    implements TweetDeckService /*Implement the TweetDeckService interface*/
{

//-----------------Internal crud methods Implementation--------------------
	
	//-----------------------General crud methods for the App (upgraded by Charles Ofoegbu)----------------------
	/**this method creates a new Record. Irrespective of the Entity's Class. It throws an exception if there is a problem*/
    protected java.lang.Object handleCreateNewRecord(org.hibernate.Session session, java.lang.Object newRecord, uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.audittrail.AuditTrail aTrail)
	{
		session.save(newRecord);
		session.save(aTrail);
		return newRecord;
	}
	
	/**this method updates an existing Record. Irrespective of the Entity's Class. It throws an exception if there is a problem*/
    protected void handleUpdateRecord(org.hibernate.Session session, java.lang.Object existingRecord,  uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.audittrail.AuditTrail aTrail)
	{	
		session.saveOrUpdate(existingRecord);
		session.save(aTrail);
	}
	
	/**this method deletes an existing Record. Irrespective of the Entity's Class. It throws an exception if there is a problem*/
    protected void handleDeleteRecord(org.hibernate.Session session, java.lang.Object existingRecord,  uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.audittrail.AuditTrail aTrail)
	{		
		session.delete(existingRecord);
		session.save(aTrail);
	}
	
	/**this method returns an object of the specified by clazz paramter using the supplied id. It throws an exception if there is a problem*/
    protected java.lang.Object handleGetRecordById(org.hibernate.Session session, java.lang.Class clazz, java.lang.Long recordId)
	{
		return session.get(clazz, recordId);
	}
	
	/**this method returns collection of the records, specified by the clazz parameter. It throws an exception if there is a problem*/
    protected java.util.Collection handleGetAllRecords(org.hibernate.Session session, java.lang.Class clazz)
	{
		java.lang.String hql = "SELECT u FROM " + clazz.getName() + " u";
        org.hibernate.Query query = session.createQuery(hql);
		java.util.Collection returnCol = (java.util.Collection)query.list();
		return returnCol;
	}	
	
	/**this method returns collection of the records, specified by the clazz parameter that is within the pageIndex and pageSize. It throws an exception if there is a problem*/
    protected java.util.Collection handleGetAllRecords(org.hibernate.Session session, java.lang.Class clazz, int pageIndex, int pageSize)
	{
		java.lang.String hql = "SELECT u FROM " + clazz.getName() + " u";
        org.hibernate.Query query = session.createQuery(hql);
		query.setFirstResult(pageIndex);
        query.setMaxResults(pageSize);
		java.util.Collection returnCol = (java.util.Collection)query.list();
		return returnCol;
	}
	
	/* ------------------------- (section added by Charles Ofoegbu) -------------------------------- */
    //--------Generic Methods; used by parsing HQL -----------//
	protected java.lang.Object handleGetUniqueRecordByHQL(org.hibernate.Session session, java.lang.String hql)
	{
		org.hibernate.Query query = session.createQuery(hql);
		return query.uniqueResult();
	}
	
	protected java.util.Collection handleGetAllRecordsByHQL(org.hibernate.Session session, java.lang.String hql)
	{
		org.hibernate.Query query = session.createQuery(hql);
		java.util.Collection returnCol = (java.util.Collection)query.list();
		return returnCol;
	}
	
	//this is necessary for panination things. i.e. using same query, you can return results in paged format
	protected java.util.Collection handleGetAllRecordsByHQL(org.hibernate.Session session, java.lang.String hql, int pageIndex, int pageSize)
	{
		org.hibernate.Query query = session.createQuery(hql);
		query.setFirstResult(pageIndex);
        query.setMaxResults(pageSize);
		java.util.Collection returnCol = (java.util.Collection)query.list();
		return returnCol;
	}
	
	//--------Generic Methods; used by parsing Class names and paremeter Ids -----------//
	//solves the bottle neck of many to one relationship -- all methods take pagination into account
	
	//for a single many to one relationship
	protected java.util.Collection handleGetAllRecordsByParameterId(org.hibernate.Session session, java.lang.Class manyClass, java.lang.Class oneClass, java.lang.Long parameterId, int pageIndex, int pageSize){
		java.lang.String strOneClass = getProcessedClassName(oneClass);
		java.lang.String hql = "SELECT c FROM " + manyClass.getName() + " c WHERE c." + strOneClass + ".id=" + parameterId;
        org.hibernate.Query query = session.createQuery(hql);
		query.setFirstResult(pageIndex);
        query.setMaxResults(pageSize);
		java.util.Collection returnCol = (java.util.Collection)query.list();
		return returnCol;
	}
	
	//for a many to one relationship where there are two CMR
	protected java.util.Collection handleGetAllRecordsByTwoParameterIds(org.hibernate.Session session, java.lang.Class manyClass, java.lang.Class firstOneClass, java.lang.Class secondOneClass, java.lang.Long firstParameterId, java.lang.Long secondParameterId, java.lang.String strOperator, int pageIndex, int pageSize){
		if(strOperator != null && (!strOperator.equalsIgnoreCase("and") || !strOperator.equalsIgnoreCase("or"))){
			strOperator = "AND"; //set a default value
		}
		java.lang.String strFirstOneClass = getProcessedClassName(firstOneClass);
		java.lang.String strSecondOneClass = getProcessedClassName(secondOneClass);
		
		java.lang.String hql = "SELECT DISTINCT c FROM " + manyClass.getName() + " c WHERE c." + strFirstOneClass + ".id=" + firstParameterId +  " " + strOperator + " c." + strSecondOneClass + ".id=" +secondParameterId;
        org.hibernate.Query query = session.createQuery(hql);
		query.setFirstResult(pageIndex);
        query.setMaxResults(pageSize);
		java.util.Collection returnCol = (java.util.Collection)query.list();
		return returnCol;
		
	}
	
	//Overloaded create, update and delete (Useful for swing impl and impl without user action and audit trail requirement)
	/**this method creates a new Record. Irrespective of the Entity's Class. It throws an exception if there is a problem*/
    protected java.lang.Object handleCreateNewRecord(org.hibernate.Session session, java.lang.Object newRecord)
	{
		session.save(newRecord);
		return newRecord;
	}
	
	/**this method updates an existing Record. Irrespective of the Entity's Class. It throws an exception if there is a problem*/
    protected void handleUpdateRecord(org.hibernate.Session session, java.lang.Object existingRecord)
	{	
		session.saveOrUpdate(existingRecord);
	}
	
	/**this method deletes an existing Record. Irrespective of the Entity's Class. It throws an exception if there is a problem*/
    protected void handleDeleteRecord(org.hibernate.Session session, java.lang.Object existingRecord)
	{		
		session.delete(existingRecord);
	}
	
	//------auxilliary method to get a Criteria Object (needed by developer who will want to use criteria in place of HQL)------- //
	 protected org.hibernate.Criteria handleGetCriteriaObject(org.hibernate.Session session, java.lang.Class clazz)
	{		
		return session.createCriteria(clazz);
	}
	
	/**this method returns a value object; accepts a criteria object as a parameter. It throws an exception if there is a problem*/
	protected java.lang.Object handleGetUniqueRecordByCriteria(org.hibernate.Session session, org.hibernate.Criteria criteria)
	{
		return criteria.uniqueResult();
	}
	
	protected java.util.Collection handleGetAllRecordsByCriteria(org.hibernate.Session session, org.hibernate.Criteria criteria)
	{
		java.util.Collection returnCol = (java.util.Collection)criteria.list();
		return returnCol;
	}
	
	//----- helper method (Charles Ofoegbu)-----//
	private java.lang.String getProcessedClassName(java.lang.Class clazz){
		java.lang.String retString = clazz + "";
		retString = retString.substring(retString.lastIndexOf("."));
		
        retString = retString.substring(1, 2).toLowerCase() + "" + retString.substring(2);
		return retString;
	}
	
	//--------Generic Methods; useful for search Operations used by parsing the attribute name and attribute values -----------//
	//TODO: add more codes here to perform generic level search by virtue of criteria 


}