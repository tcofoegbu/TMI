/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Charles Ofoegbu
 *
 */
public class MD5Util {
    public static String getMD5(String stringToHash){
	String md5 = null;    
	if (null == stringToHash){
	    return null;
	}
	try {
	    MessageDigest digest = MessageDigest.getInstance("MD5");
	    digest.update(stringToHash.getBytes(), 0, stringToHash.length());
	    md5 = new BigInteger(1, digest.digest()).toString(16);
	} catch (NoSuchAlgorithmException e) {

	    e.printStackTrace();
	} 
	return md5;
    }
}
