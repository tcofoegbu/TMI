package uk.ac.dotrural.irp.ecosystem.social.twitter.listeners;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.*;

import org.slf4j.*;

import uk.ac.dotrural.irp.ecosystem.social.twitter.StatusListenerImpl;
import uk.ac.dotrural.irp.ecosystem.social.twitter.TwitterComponent;
import uk.ac.dotrural.irp.ecosystem.social.twitter.UserStreamListenerImpl;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.Role;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants;
import uk.ac.dotrural.irp.ecosystem.social.twitter.common.AutoRestore;
import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.social.twitter.quartzjob.*;  
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.userrole.UserRole;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;

/**
 * Web application lifecycle listener.
 *
 * @author Charles Ofoegbu
 */
public class SystemComponentInitializer implements ServletContextListener {    
    Logger logger = LoggerFactory.getLogger(SystemComponentInitializer.class);
    
    public void contextInitialized(ServletContextEvent sce) {
	logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Inside SystemComponentInitializer <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        logger.debug("found Users : >>>>>>>>>>>>>>>>>>>>>>>>>> "+DataAccessProxy.getAllRecords(Users.class).size());       
        createRoles();
        
        if(AutoRestore.userstreamEnabled){
            new UserStreamListenerImpl();
        }
        
        if(AutoRestore.firehoseEnabled){
            new StatusListenerImpl(); 
        }

        if(AutoRestore.convJobEnabled){
            UpdateTweetJob.startTweetUpdateJob();        
        }
        
        
        
        if(AutoRestore.timeLineUpdateJobEnabled){
	    try {
		Date startDate = Constants.DATE_FORMAT.parse("2014/07/25");
		Date endDate = Constants.DATE_FORMAT.parse("2014/07/28");
		TwitterComponent.updateTimeLineJob(startDate, endDate); 
	    } catch (ParseException e) {
		logger.error(e.getStackTrace().toString()); 
	    }      
        }   
                
    }


     
    public void createRoles(){
	logger.debug("Creating default user roles...");
  	for (Role role : Role.values()) {
  	    UserRole foundRole = DataAccessProxy.getRoleByCode(role.name());
  	    if (foundRole == null) {
  		UserRole uRole = new UserRole();
  		uRole.setAuthority(role.getRole());
  		uRole.setCode(role.name());
  		DataAccessProxy.createNewRecord(uRole);
  		uRole = null;
  	    }
  	  foundRole = null;
  	}
      }

    public void contextDestroyed(ServletContextEvent sce) {
	TwitterComponent.closeStream();
	Enumeration<Driver> drivers = DriverManager.getDrivers();
	while (drivers.hasMoreElements()) {
	    Driver driver = drivers.nextElement();
	    try {
		DriverManager.deregisterDriver(driver);
		logger.debug("deregistering jdbc driver: " + driver);
	    } catch (SQLException e) {
		logger.debug("Error deregistering driver " + driver, e);
	    }

	}
    }
}
