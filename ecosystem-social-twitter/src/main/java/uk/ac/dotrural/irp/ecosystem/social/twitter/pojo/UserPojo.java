/**
 * 
 */
package uk.ac.dotrural.irp.ecosystem.social.twitter.pojo;


import uk.ac.dotrural.irp.ecosystem.tweetdeck.entity.users.Users;


/**
 * @author Charles Ofoegbu
 *
 */
public class UserPojo {
    private Long id;
    private String email;
    private String firstName;
    private String surname;
    private String password;
    
    private String userRole;

    public UserPojo(){
	
    }
    
    public UserPojo(Users user){
	this.id = user.getId();
	this.email = user.getUsername();
	this.firstName = user.getFirstName();
	this.surname = user.getSurname();
	this.userRole = user.getUserRole().getAuthority();
	this.password = "*****";	
	
    }

    public Long getId() {
	return id;
    }

    public void setId(Long id) {
	this.id = id;
    }
    public String getEmail() {
	return email;
    }

    public void setEmail(String email) {
	this.email = email;
    }
    
    public String getFirstName() {
	return firstName;
    }

    public void setFirstName(String firstName) {
	this.firstName = firstName;
    }

    public String getSurName() {
	return surname;
    }

    public void setSurname(String surname) {
	this.surname = surname;
    }

    public String getPassword() {
	return password;
    }

    public void setPassword(String password) {
	this.password = password;
    }

      public String getUserRole() {
	return userRole;
    }

    public void setUserRole(String userRole) {
	this.userRole = userRole;
    }

    
}
