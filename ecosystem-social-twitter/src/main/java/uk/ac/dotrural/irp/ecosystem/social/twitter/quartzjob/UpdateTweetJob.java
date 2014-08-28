/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.quartzjob;

import java.text.ParseException;

import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import uk.ac.dotrural.irp.ecosystem.social.twitter.TweetProcessor;

/**
 * @author Charles Ofoegbu
 * 
 */
public class UpdateTweetJob implements Job {
    public void execute(JobExecutionContext context) throws JobExecutionException {
	TweetProcessor.updateTweets();
    }
    
    public static void startTweetUpdateJob(){	
    	JobDetail job = new JobDetail();
    	job.setName("TweetUpdateJob");
    	job.setJobClass(UpdateTweetJob.class); 
	CronTrigger trigger = new CronTrigger();
	trigger.setName("TweetUpdateJobTrigger");
	try {
	    trigger.setCronExpression("0 0/2 * * * ?"); //every 5 mins

	    // schedule it
	    Scheduler scheduler = new StdSchedulerFactory().getScheduler();
	    scheduler.start();
	    scheduler.scheduleJob(job, trigger);
	} catch (ParseException e) {
	    e.printStackTrace();
	} catch (SchedulerException e) {
	    e.printStackTrace();
	}

    }
}
