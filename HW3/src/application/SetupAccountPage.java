package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.SQLException;

import databasePart1.*;

/**
 * SetupAccountPage class handles the account setup process for new users.
 * Users provide their userName, password, and a valid invitation code to register.
 */
public class SetupAccountPage {
	
    private final DatabaseHelper databaseHelper;
    // DatabaseHelper to handle database operations.
    public SetupAccountPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    /**
     * Displays the Setup Account page in the provided stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */
    public void show(Stage primaryStage) {
    	// Input fields for userName, password, and invitation code
        TextField userNameField = new TextField();
        userNameField.setPromptText("Enter userName");
        userNameField.setMaxWidth(250);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setMaxWidth(250);
        
        TextField inviteCodeField = new TextField();
        inviteCodeField.setPromptText("Enter InvitationCode");
        inviteCodeField.setMaxWidth(250);
        
        // Label to display error messages for invalid input or registration issues
        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 12px;");
        
        Label messageLabel = new Label(); // Message label for empty input
        messageLabel.setStyle("-fx-text-fill: red;");
        
        
        Button setupButton = new Button("Setup");
        
        setupButton.setOnAction(a -> {
        	// Retrieve user input
            String userName = userNameField.getText();
            String password = passwordField.getText();
            String code = inviteCodeField.getText();
            
            if (!checkUsername(userName)) {
                messageLabel.setText("Your user name must be 3-20 characetrs long including a upper case, lower case, number, and special char ");
                return;
            }

            // Validate password
            if (!checkPassword(password)) {
                messageLabel.setText("Your password must be 8-20 characetrs long including a upper case, lower case, number, and special char ");
                return;
            }
            
            try {
            	// Check if the user already exists
            	if(!databaseHelper.doesUserExist(userName)) {
            		
            		// Validate the invitation code
            		if(databaseHelper.validateInvitationCode(code)) {
            			
            			// Create a new user and register them in the database
		            	User user= new User(userName, password, "user");
		                databaseHelper.register(user);
		                
		             // Navigate to the Welcome Login Page
		                new WelcomeLoginPage(databaseHelper).show(primaryStage,user);
            		}
            		else {
            			errorLabel.setText("Please enter a valid invitation code");
            		}
            	}
            	else {
            		errorLabel.setText("This useruserName is taken!!.. Please use another to setup an account");
            	}
            	
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        VBox layout = new VBox(10);
        layout.setStyle("-fx-padding: 20; -fx-alignment: center;");
        layout.getChildren().addAll(userNameField, passwordField,inviteCodeField, setupButton, errorLabel);

        primaryStage.setScene(new Scene(layout, 800, 400));
        primaryStage.setTitle("Account Setup");
        primaryStage.show();
    }
    private boolean checkUsername(String username) {
        if (username.length() < 3 || username.length() > 20) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;

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
        }

        return hasUpper && hasLower;
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