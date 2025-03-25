package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Platform;
import databasePart1.*;

public class WelcomeLoginPage {

private final DatabaseHelper databaseHelper;

    public WelcomeLoginPage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }
    
    public void show(Stage primaryStage, User user) {
    
    VBox layout = new VBox(5);
    layout.setStyle("-fx-alignment: center; -fx-padding: 20;");
    
    Label welcomeLabel = new Label("Welcome!!");
    welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    
    // Role selection dropdown (only shown if the user has multiple roles)
    ChoiceBox<String> roleSelector = new ChoiceBox<>();
    
    if ("admin".equalsIgnoreCase(user.getRoles())) {
        roleSelector.getItems().add("Admin");
    }
    if ("user".equalsIgnoreCase(user.getRoles())) {
        roleSelector.getItems().add("User");
    }
    if ("student".equalsIgnoreCase(user.getRoles())) {
    	roleSelector.getItems().add("Student");
    }

    // Pre-select the first available role
    if (!roleSelector.getItems().isEmpty()) {
        roleSelector.setValue(roleSelector.getItems().get(0));
    }

    // Button to navigate to the selected role's page
    Button continueButton = new Button("Continue to your Page");
    continueButton.setOnAction(a -> {
        String selectedRole = roleSelector.getValue();
        
        if ("Admin".equals(selectedRole)) {
            new AdminHomePage(databaseHelper).show(primaryStage);
        } else if ("User".equals(selectedRole)) {
            new UserHomePage(databaseHelper).show(primaryStage);
        } else if ("Student".equals(selectedRole)) { 
        	new StudentHomePage(databaseHelper, user).show(primaryStage);
        }
    });

    // Logout Button - Allows users to log out and return to login screen
    Button logoutButton = new Button("Logout");
    logoutButton.setOnAction(a -> {
        new SetupLoginSelectionPage(databaseHelper).show(primaryStage); 
    });
    
    // Switch Role Button - Allows users to switch between User and Admin roles    
    Button switchRoleButton = new Button("Switch Role");
    switchRoleButton.setOnAction(a -> {
        new UserLoginPage(databaseHelper).show(primaryStage); 
    });

    // Add the Switch Role button to the UI layout
    layout.getChildren().add(switchRoleButton);

    // Button to quit the application
    Button quitButton = new Button("Quit");
    quitButton.setOnAction(a -> {
    databaseHelper.closeConnection();
    Platform.exit(); // Exit the JavaFX application
    });
    
    if ("admin".equals(user.getRoles())) {
            Button inviteButton = new Button("Invite");
            inviteButton.setOnAction(a -> {
                new InvitationPage().show(databaseHelper, primaryStage);
            });
            layout.getChildren().add(inviteButton);
        }

    // Add all components to the layout
    layout.getChildren().addAll(welcomeLabel, roleSelector, continueButton, logoutButton, quitButton);
    Scene welcomeScene = new Scene(layout, 800, 400);

    // Set the scene to primary stage
    primaryStage.setScene(welcomeScene);
    primaryStage.setTitle("Welcome Page");
    }
}