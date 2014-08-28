package uk.ac.dotrural.irp.ecosystem.social.twitter.common;


import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.dotrural.irp.ecosystem.social.twitter.dao.DataAccessProxy;
import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;


/**
 * @author Charles Ofoegbu
 *
 */
public class AutoRestore {
    private static Logger logger = LoggerFactory.getLogger(AutoRestore.class);
    private static boolean isExcalationInstanceAlreadyStarted = false;
    private static Properties prop = new PropertyFileUtil("EcosystemSocialTwitter.properties", "Properties used to configure ecosystem social twitter").getProperties();
    private static int minMemoryThreshold = Integer.valueOf(prop.getProperty("available_memory_threshold", "60"));
    private static int memoryCheckInterval = Integer.valueOf(prop.getProperty("memory_check_interval", "5"));
    private static String shutdownCommand  = prop.getProperty("shutdown_command", "cmd /c stop");
    private static String shutdownFileName  = prop.getProperty("shutdownfile", "/shutdown.bat");
    private static boolean gracefulExcalationEnabled  = Boolean.valueOf(prop.getProperty("excalation_activated", "false"));
    public static boolean firehoseEnabled  = Boolean.valueOf(prop.getProperty("firehose_enabled", "false"));
    public static boolean userstreamEnabled  = Boolean.valueOf(prop.getProperty("userstream_enabled", "false"));
    public static boolean convJobEnabled  = Boolean.valueOf(prop.getProperty("conv_job_enabled", "false"));
    public static boolean tweetCountJobEnabled  = Boolean.valueOf(prop.getProperty("tweet_count_job_enabled", "false"));      
    public static boolean timeLineUpdateJobEnabled  = Boolean.valueOf(prop.getProperty("timeline_update_job_enabled", "false"));        
    public static String MEMCACHE_SERVERS = String.valueOf(prop.getProperty("MEMCACHE_SERVERS", "127.0.0.1:11211"));
       
    private static int loopIntervalcounter = 0;
	    
    public static String getContextPath(HttpServletRequest req){
	return "http://" + req.getServerName() + ":" + req.getServerPort();
    }
    
    public static String getInitiator(HttpServletRequest req){
	String userName =  req.getHeader("userName");
	Users user = DataAccessProxy.getUserByUserName(userName);
	if(user == null){
	    return "";
	}else{
	    return user.getId().toString();
	}
    }

    
    public static void evaluateMemoryUsageAndExcalateIfThreasholdIsExceeded(){
	loopIntervalcounter++;
	if(loopIntervalcounter > memoryCheckInterval){
	    Runtime runtime = Runtime.getRuntime();
	    StringBuilder sb = new StringBuilder();
	    int mb = 1024 * 1024;
	    long allocatedMemory = runtime.totalMemory() / mb;
	    long freeMemory = runtime.freeMemory() / mb;
	    long usedMemory = allocatedMemory - freeMemory;
	    long availableMemory = allocatedMemory - usedMemory;
	    sb.append("\n------------------- MEMORY INFO -----------------------");
	    sb.append("\nAllocated memory : " + allocatedMemory);
	    sb.append("\nUsed Memory      : " + usedMemory);
	    sb.append("\nAvailable Memory : " + availableMemory).append("\n");
	    //logger.debug(sb.toString());
	    sb = null;
	    System.gc();
	    if (availableMemory <= minMemoryThreshold && gracefulExcalationEnabled && !isExcalationInstanceAlreadyStarted) {
		logger.debug("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Memory Threshold of " + minMemoryThreshold + " has been exceded!!!");
		isExcalationInstanceAlreadyStarted = true;
		excalateInstance();
	    }
	}
      }
    
    public static void excalateInstance() {
	String currentServerInstance = System.getProperty("user.dir").replace("\\", "/");
	try {
	    logger.debug("\n-------------------- Excalation has been initiated!!! ----------------------");
	    executeProcess(currentServerInstance, shutdownFileName, shutdownCommand);
	} catch (Exception e) {
	    logger.error("----------------------------------------------> " + e.getMessage());
	}
    }

    public static void executeProcess(String bin_directory, String process, String command){
	File objDirectory = new File(bin_directory);
	try {
	    Runtime.getRuntime().exec(new String[]{bin_directory + process}, null, objDirectory);  
	} catch (IOException e) {
	    e.printStackTrace();
	} 
    }
}
