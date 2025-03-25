package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * The SetupAdmin class handles the setup process for creating an administrator account.
 * This is intended to be used by the first user to initialize the system with admin credentials.
 */
public class AdminSetupPage {
	
    private final DatabaseHelper databaseHelper;

    public AdminSetupPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public void show(Stage primaryStage) {
    	
    	
    	
    	// Input fields for userName and password
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter Admin userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        //This section confirms that the user is typing the right by comparing the previously typed password to a retyped version
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(250);
        
      //create labels for empty input
        Label messageLabel = new Label(); // Message label for empty input
        messageLabel.setStyle("-fx-text-fill: red;");

        Button setupButton = new Button("Setup your Admin Account");
        
        
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            //gather confirm password input
            String confirmPassword = confirmPasswordField.getText();

            
            //produce the requirments for passwords 
            
            //this if statement should check and make sure that the username or password is not empty
         // Validate username
            if (!checkUsername(userName)) {
                messageLabel.setText("Your user name must be 3-20 characetrs long including a upper case, lower case, number, and special char ");
                return;
            }

            // Validate password
            if (!checkPassword(password)) {
                messageLabel.setText("Your password must be 3-20 characetrs long including a upper case, lower case, number, and special char ");
                return;
            }

            // Check if passwords match
            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match!");
                return;
            }
            
            
            try {
            	// Create a new User object with admin role and register in the database
            	User user= new User(userName, password, "admin");
            	
            	
                databaseHelper.register(user);
                System.out.println("Administrator setup completed.");
                
                // Navigate to the Welcome Login Page
                //the next line of code is commeneted out but is orginal code
                new WelcomeLoginPage(databaseHelper).show(primaryStage, user);
                //new UserLoginPage(databaseHelper).show(primaryStage);
            } catch (SQLException e) {
            	messageLabel.setText("Error setting up admin. Try again.");
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10, userNameField, passwordField, confirmPasswordField, setupButton, messageLabel);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Administrator Setup");
        primaryStage.show();
    }
    
    
    //Method of creating the 
    //These are private helper methods which will validate our userName and password that can be used later 
    
    /*
   To have a proper userName per our deginated rquirments you must: 
   -	Uppercase Character
	-	Lower Case Character
	-	Numbers 
	-	Special Character(_,-,.,!,$,,etc).
	-	Min 3 characters 
	-	Max 20 characters

   */
    private boolean checkUsername(String username) {
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // Check each character in the username
        for (int i = 0; i < username.length(); i++) {
            char c = username.charAt(i);

            // Check if it's an uppercase letter
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } 
            // Check if it's a lowercase letter
            else if (Character.isLowerCase(c)) {
                hasLower = true;
            } 
            // Check if it's a digit
            else if (Character.isDigit(c)) {
                hasDigit = true;
            } 
            // Check for special characters (anything not alphanumeric)
            else if (!Character.isLetterOrDigit(c)) {
                hasSpecial = true;
            }
        }

        return hasUpper && hasLower && hasDigit && hasSpecial;
    }

    /*
    To have a proper password per our deginated rquirments you must: 
   -	Uppercase Character
-	Lower Case Character
-	Numbers 
-	Special Character(_,-,.,!,$,,etc).
-	Confirm password 
-	Min 8 characters 
-	Max 20 characters


    */
    private boolean checkPassword(String password) {
        if (password.length() < 8 || password.length() > 20) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        // Check each character in the password
        for (int i = 0; i < password.length(); i++) {
            char c = password.charAt(i);

            // Check if it's an uppercase letter
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } 
            // Check if it's a lowercase letter
            else if (Character.isLowerCase(c)) {
                hasLower = true;
            } 
            // Check if it's a digit
            else if (Character.isDigit(c)) {
                hasDigit = true;
            } 
            // Check for special characters (anything not alphanumeric)
            else if (!Character.isLetterOrDigit(c)) { //checks for anything other a letter or digit, found at https://www.geeksforgeeks.org/java-program-to-check-whether-the-string-consists-of-special-characters/
                hasSpecial = true;
            }
        }

        // Ensure password meets all criteria
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    
    
    
}