/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;

import uk.ac.dotrural.irp.ecosystem.social.twitter.common.Constants.HTTP_CODE;


/**
 * @author Charles Ofoegbu
 *
 */
public class ExceptionPojo {
    private int statusCode;
    private String[] errors;
    private String moreInfo;
    
    public ExceptionPojo (HTTP_CODE statusCode, String[]errors, String moreInfo){
	this.statusCode = statusCode.getCode();
	this.moreInfo = moreInfo;
	this.errors = errors;
    }
    
    public int getStatusCode() {
	return statusCode;
    }
    public void setStatusCode(int statusCode) {
	this.statusCode = statusCode;
    }
    public String[] getErrors() {
	return errors;
    }
    public void setErrors(String[] errors) {
	this.errors = errors;
    }
    public String getMoreInfo() {
	return moreInfo;
    }
    public void setMoreInfo(String moreInfo) {
	this.moreInfo = moreInfo;
    }
}
