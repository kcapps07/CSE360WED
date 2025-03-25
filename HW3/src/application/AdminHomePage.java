package application;

import javafx.scene.Scene;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import databasePart1.DatabaseHelper;

import java.util.List;
import java.sql.SQLException;

/**
 * AdminPage class represents the user interface for the admin user.
 * This page displays a simple welcome message for the admin.
 */

public class AdminHomePage {
    private DatabaseHelper databaseHelper;
    //private String adminUserName;

    public AdminHomePage(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
        
    }
    
    
	/**
     * Displays the admin page in the provided primary stage.
     * @param primaryStage The primary stage where the scene will be displayed.
     */

    public void show(Stage primaryStage) {
        VBox layout = new VBox(10);
        layout.setStyle("-fx-alignment: center; -fx-padding: 20;");

        // Label to display the welcome message for the admin
        Label adminLabel = new Label("Hello, Admin!");
        adminLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        VBox userList = new VBox(5);
        userList.setStyle("-fx-alignment: center; -fx-padding: 20;");

        Button getUsers = new Button("Get Current Users");
        getUsers.setOnAction(a -> {
            try {
                List<User> users = databaseHelper.getAllUsers();
                userList.getChildren().clear();

//Looks through all the users in the database and lists them out in the admin homepage
                
                for (User user : users) {
                    Label userInformation = new Label(
                            "USERNAME: " + user.getUserName() +
                            ", ROLES: " + user.getRoles());
                    
//create a button to add users 
                  //Button addRoleButton = new Button("Add a Role");
                  
//Created a button to where it will allow you to delete the users inside the admin homepage
//FIXME: It allows me to delte accounts, but we need to preent it from deleting admin accounts
                    Button deleteUserButton = new Button("Remove?");
                    deleteUserButton.setOnAction(e -> {
                        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmDialog.setHeaderText("Are you sure?");
                        confirmDialog.showAndWait().ifPresent(response -> {
                            if (response == ButtonType.OK) {
                                try {
                                    boolean deleted = databaseHelper.DeleteUser(user.getUserName(), "admin");
                                    if (deleted) {
                                        userList.getChildren().remove(userInformation);
                                        userList.getChildren().remove(deleteUserButton);
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    new Alert(Alert.AlertType.ERROR, "Failed to delete selected user: " + ex.getMessage()).show();
                                }
                            }
                        });
                    });
                    
                    

                    userList.getChildren().addAll(userInformation, deleteUserButton);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to retrieve users: " + e.getMessage()).show();
            }
        });
                    
        //This button should add roles to users
        Button addRoleButton = new Button("Add a Role");
        addRoleButton.setOnAction(e -> 
        {
        	showAddRoleDialog(primaryStage);
        });
                   

        layout.getChildren().addAll(adminLabel, getUsers, addRoleButton, userList);
        Scene adminScene = new Scene(layout, 800, 400);

        // Set the scene to primary stage
        primaryStage.setScene(adminScene);
        primaryStage.setTitle("Admin Page");
    }
    //this should display user info and roles 
    private void showAddRoleDialog(Stage primaryStage) {
    	
    	//JavaFx gui menu for user to add new roles
    	Dialog<String> dialog = new Dialog<>();
    	dialog.setTitle("Give a role to User");
    	dialog.setHeaderText("Select User and role:");
    	
    	//gather input on the user ends
    	TextField userNameField = new TextField();
    	userNameField.setPromptText("Enter desired username: ");
    	
    	TextField roleField = new TextField();
    	roleField.setPromptText("Enter desired role: ");
    	
    	Button addRole = new Button("Add a Role");
    	addRole.setOnAction(e -> {
    		String userName = userNameField.getText();
    		String role = roleField.getText();
    		
    		try {
    			//this prat should add a role to the user
    			databaseHelper.addRoleFromAdmin(userName, role);
    			
    		}catch(SQLException ex) {
    			ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Failed to add role: " + ex.getMessage()).show();
    		}
    		});
    	VBox dialogPane = new VBox(10, userNameField, roleField, addRole);
        dialogPane.setStyle("-fx-padding: 20; -fx-alignment: center;");
        
     // Set the dialog content
        dialog.getDialogPane().setContent(dialogPane);
        
        // Show the dialog and wait for user interaction
        dialog.showAndWait();
    	
    }
}