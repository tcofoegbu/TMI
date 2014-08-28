/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.exceptions;

/**
 * @author Charles Ofoegbu
 *
 */
public class AuthenticationException extends Exception{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String title;
    private String exceptionMessage;
    public AuthenticationException(String title, String msg){
	this.title = title;
	this.exceptionMessage = msg;
    }
    public String getTitle() {
	return title;
    }
    public void setTitle(String title) {
	this.title = title;
    }
    public String getExceptionMessage() {
	return exceptionMessage;
    }
    public void setExceptionMessage(String exceptionMessage) {
	this.exceptionMessage = exceptionMessage;
    }

}
