/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.common;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author Charles Ofoegbu
 *
 */
public class PropertyFileUtil {
    static Logger logger = LoggerFactory.getLogger(PropertyFileUtil.class);
    private static Properties properties = new Properties();
    private static String propertiesFilename;
    private static String comment;

    public PropertyFileUtil(String propertiesFileName, String comments) {
	propertiesFilename = propertiesFileName;
	comment = comments;
	loadProperties();
    }

    public synchronized Properties getProperties() {
	loadProperties();
	return properties;
    }

    private Properties loadProperties() {

	try {
	    properties.load(new FileInputStream(propertiesFilename));
	} catch (FileNotFoundException e) {
	    updateProperties();
	     logger.error("Exception : ",e);
	} catch (IOException e) {
	     logger.error("Exception : ",e);
	}
	return properties;
    }

    private void updateProperties() {
	try {
	    properties.store(new FileOutputStream(propertiesFilename), comment);
	    System.out.println("Properties Stored");
	} catch (FileNotFoundException e) {
	     logger.error("Exception : ",e);
	} catch (IOException e) {
	     logger.error("Exception : ",e);
	}
    }

    public static String getPropertiesFilename() {
	return propertiesFilename;
    }

    public static void setPropertiesFilename(String propertiesFilename) {
	PropertyFileUtil.propertiesFilename = propertiesFilename;
    }
}
