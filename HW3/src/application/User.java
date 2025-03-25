package application;

import java.util.Arrays;
import java.util.List;

/**
 * The User class represents a user entity in the system.
 * It contains the user's details such as userName, password, and role.
 */
public class User {
    private String userName;
    private String password;
    private String roles;
    //private String emailAddress;
    //private String name;

    // Constructor to initialize a new User object with userName, password, and role, name and emailAddress
    public User(String userName, String password, String roles) {
        this.userName = userName;
        this.password = password;
        this.roles = roles;
        //this.emailAddress = emailAddress;
        //this.name = name;
    }
    
    
   
    
    // Sets the role of the user.
    public void setRoles(String roles) {
    	this.roles=roles;
    }

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getRoles() { return roles; }
    //public String getemailAddress() { return emailAddress; }
    //public String getname() { return name; }
    
    //get roles as a list
    public List<String> getRolesList(){
    	return Arrays.asList(roles.split(","));
    }
  //this method gives a role to a user, should mostly be used by the admin
    public void giveRole(String role) {
    	if(!roles.contains(role)) {
    		roles += "," + role;
    	}
    }
}




//NOTE: WHEN CREATING NEW USERS THE THIRD PARMETER COULD BE LIKE "admin" or "admin,user,student", etc. this is how we check for the multiple roles in a single string 