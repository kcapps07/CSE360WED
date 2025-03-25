package application;

import databasePart1.DatabaseHelper;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import application.Threads;
import application.Replies;
import java.util.List;

public class StudentHomePage {

    private final DatabaseHelper databaseHelper;
    private final User user;

    // Display area
    private TextArea displayArea;
    // Separate input area for new thread titles
    private TextField newThreadField;
    private TextField searchField;
    
    //create a private var to hold current user
    
    

    public StudentHomePage(DatabaseHelper databaseHelper, User user) {
        this.databaseHelper = databaseHelper;
        this.user = user;
    }

    public void show(Stage primaryStage) {
        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(20));

        Label headingLabel = new Label("Q&A (User: " + user.getUserName() + ", Role: " + user.getRoles() + ")");
        headingLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Add search field
        HBox searchBox = new HBox(10);
        searchField = new TextField();
        searchField.setPromptText("Search threads...");
        Button searchButton = new Button("Search");
        searchBox.getChildren().addAll(searchField, searchButton);

        // Display area for existing threads and replies
        displayArea = new TextArea();
        displayArea.setPrefHeight(250);
        displayArea.setEditable(false); // Make it read-only
        displayArea.setPromptText("Existing threads and replies appear here...");

        // New thread input
        HBox postBox = new HBox(10);
        newThreadField = new TextField();
        newThreadField.setPromptText("Enter new thread title...");
        Button postButton = new Button("Post");
        postBox.getChildren().addAll(newThreadField, postButton);

        // Other buttons
        Button replyButton = new Button("Reply");
        Button deleteButton = new Button("Delete");
        Button logoutButton = new Button("Logout");
        Button markResolvedButton = new Button ("Mark as Resolved");
        Button replyToReplyButton = new Button("Reply to a Reply");
        //create a dropdown for different edit options
        ComboBox<String> editChoices = new ComboBox<>();
        editChoices.getItems().addAll("Edit Thread", "Edit Reply");
        editChoices.setPromptText("Select to Edit");
        editChoices.setVisible(false);

        Button editButton = new Button("Edit");
        HBox buttonBar = new HBox(10, replyButton, editButton, editChoices, deleteButton, markResolvedButton, logoutButton);

        
        // Add everything to the main layout
        mainLayout.getChildren().addAll(headingLabel, searchBox, displayArea, postBox, buttonBar);

        // Button actions
        postButton.setOnAction(e -> handlePost());
        replyButton.setOnAction(e -> handleReply());
        searchButton.setOnAction(e -> handleSearch());  // Trigger search on button click
        editButton.setOnAction(e -> {editChoices.setVisible(!editChoices.isVisible());});
        
        editChoices.setOnAction(e-> {
        	String selectedOption = editChoices.getValue();
        	if(selectedOption != null) {
        		if(selectedOption.equals("Edit Thread")) {
        			handleEditThread();
        		}else if(selectedOption.equals("Edit Reply")) {
        			handleEditReply();
        		}
        		editChoices.setVisible(false);
        	}
        });
        markResolvedButton.setOnAction(e -> {
            String replyIdStr = showInputDialog("Mark as Resolved", "Enter the Reply ID:");
            if (replyIdStr == null || replyIdStr.trim().isEmpty()) {
                return;
            }
            try {
                int replyId = Integer.parseInt(replyIdStr.trim());
                DatabaseHelper.markReplyAsResolved(replyId);
                showAlert(Alert.AlertType.INFORMATION, "Success", "Reply #" + replyId + " marked as resolved.");
                refreshDisplay();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Input Error", "Please enter a valid reply ID.");
            }
        });
        deleteButton.setOnAction(e -> {
            ChoiceDialog<String> choiceDialog = new ChoiceDialog<>("Thread", "Thread", "Reply");
            choiceDialog.setTitle("Delete Option");
            choiceDialog.setHeaderText("Choose what to delete");
            choiceDialog.setContentText("Delete:");
            String choice = choiceDialog.showAndWait().orElse(null);
            if (choice == null) return;

            if (choice.equals("Thread")) {
                String threadIdStr = showInputDialog("Delete Thread", "Enter the Thread ID to delete:");
                if (threadIdStr == null || threadIdStr.trim().isEmpty()) return;
                try {
                    int threadId = Integer.parseInt(threadIdStr.trim());
                    Threads thread = DatabaseHelper.getThreadById(threadId);
                    if (thread == null) {
                        showAlert(Alert.AlertType.ERROR, "Delete Error", "Thread not found.");
                        return;
                    }
                    if (!thread.getAuthor().equals(user.getUserName())) {
                        showAlert(Alert.AlertType.ERROR, "Delete Error", "You cannot delete a thread you did not write.");
                        return;
                    }
                    DatabaseHelper.deleteThread(threadId);
                    showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Thread deleted successfully.");
                    refreshDisplay();
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid thread ID.");
                }
            } else if (choice.equals("Reply")) {
                String replyIdStr = showInputDialog("Delete Reply", "Enter the Reply ID to delete:");
                if (replyIdStr == null || replyIdStr.trim().isEmpty()) return;
                try {
                    int replyId = Integer.parseInt(replyIdStr.trim());
                    Replies reply = DatabaseHelper.getReplyById(replyId);
                    if (reply == null) {
                        showAlert(Alert.AlertType.ERROR, "Delete Error", "Reply not found.");
                        return;
                    }
                    if (!reply.getAuthor().equals(user.getUserName())) {
                        showAlert(Alert.AlertType.ERROR, "Delete Error", "You cannot delete a reply you did not write.");
                        return;
                    }
                    DatabaseHelper.deleteMessage(replyId);
                    showAlert(Alert.AlertType.INFORMATION, "Delete Success", "Reply deleted successfully.");
                    refreshDisplay();
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid reply ID.");
                }
            }
        });
        
        replyToReplyButton.setOnAction(e -> {
            // Prompt user for parent reply ID
            String parentIdStr = showInputDialog("Reply to a Reply", "Enter the Reply ID you want to respond to:");
            if (parentIdStr == null || parentIdStr.trim().isEmpty()) {
                return;
            }
            int parentId;
            try {
                parentId = Integer.parseInt(parentIdStr.trim());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Reply Error", "Invalid reply ID.");
                return;
            }
            // Ask for the reply text
            String replyText = showInputDialog("Reply to a Reply", "Enter your reply text:");
            if (replyText == null || replyText.trim().isEmpty()) {
                return;
            }
            // Create the nested reply
            DatabaseHelper.createReplyToReply(parentId, replyText, user.getUserName());
            refreshDisplay();
        });
        
        logoutButton.setOnAction(e -> new SetupLoginSelectionPage(databaseHelper).show(primaryStage));

        // Setup the scene
        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Q&A with Drop-Down Replies");
        primaryStage.show();

        // Load existing data into displayArea
        refreshDisplay();
    }

    private void refreshDisplay() {
        displayArea.clear();
        List<Threads> threads = DatabaseHelper.getAllThreads();
        for (Threads thread : threads) {
            displayArea.appendText("Thread #" + thread.getId() +
                    " | Title: " + thread.getTitle() +
                    " | Author: " + thread.getAuthor() + "\n");
            List<Replies> replies = DatabaseHelper.getMessagesForThread(thread.getId());
            for (Replies reply : replies) {
                if (reply.getParentId() == null) {
                    displayRepliesRecursively(reply, replies, 1);
                }
            }
            displayArea.appendText("\n");
        }
    }

    private void displayRepliesRecursively(Replies reply, List<Replies> allReplies, int indentLevel) {
        String indent = "   ".repeat(indentLevel);   
        String resolvedMarker = reply.isResolved() ? " (Marked as resolved)" : "";
        displayArea.appendText(indent + "-> Reply #" + reply.getId() +
                " by " + reply.getAuthor() +
                ": " + reply.getText() + resolvedMarker + "\n");
        for (Replies r : allReplies) {
            if (r.getParentId() != null && r.getParentId() == reply.getId()) {
                displayRepliesRecursively(r, allReplies, indentLevel + 1);
            }
        }
    }


    private void handlePost() {
        // Use ONLY the newThreadField for the thread title
        String title = newThreadField.getText().trim();
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Post Error", "Please enter a thread title.");
            return;
        }
        // Create new thread
        DatabaseHelper.createThread(user.getUserName(), title);

        // Clear the input field
        newThreadField.clear();

        // Refresh
        refreshDisplay();
    }

    private void handleReply() {
    	  ChoiceDialog<String> replyChoiceDialog = new ChoiceDialog<>("Thread", "Thread", "Reply");
    	  replyChoiceDialog.setTitle("Reply Type");
    	  replyChoiceDialog.setHeaderText("What do you want to reply to?");
    	  replyChoiceDialog.setContentText("Choose:");
    	  String choice = replyChoiceDialog.showAndWait().orElse(null);
    	  if (choice == null) return;
    	  
    	  if (choice.equals("Thread")) {
    	    String threadIdStr = showInputDialog("Reply to Thread", "Enter the Thread ID:");
    	    if (threadIdStr == null || threadIdStr.trim().isEmpty()) return;
    	    int threadId;
    	    try {
    	      threadId = Integer.parseInt(threadIdStr.trim());
    	    } catch (NumberFormatException e) {
    	      showAlert(Alert.AlertType.ERROR, "Reply Error", "Invalid Thread ID.");
    	      return;
    	    }
    	    String replyText = showInputDialog("Reply", "Enter your reply text:");
    	    if (replyText == null || replyText.trim().isEmpty()) return;
    	    DatabaseHelper.createMessage(threadId, replyText, user.getUserName());
    	  } else if (choice.equals("Reply")) {
    	    String parentReplyIdStr = showInputDialog("Reply to Reply", "Enter the Reply ID to reply to:");
    	    if (parentReplyIdStr == null || parentReplyIdStr.trim().isEmpty()) return;
    	    int parentReplyId;
    	    try {
    	      parentReplyId = Integer.parseInt(parentReplyIdStr.trim());
    	    } catch (NumberFormatException e) {
    	      showAlert(Alert.AlertType.ERROR, "Reply Error", "Invalid Reply ID.");
    	      return;
    	    }
    	    String replyText = showInputDialog("Reply", "Enter your reply text:");
    	    if (replyText == null || replyText.trim().isEmpty()) return;
    	    DatabaseHelper.createReplyToReply(parentReplyId, replyText, user.getUserName());
    	  }
    	  refreshDisplay();
    	}

    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            refreshDisplay(); // Show all threads if the search is empty
            return;
        }

        displayArea.clear();
        List<Threads> threads = DatabaseHelper.getAllThreads();
        for (Threads thread : threads) {
            boolean threadMatches = thread.getTitle().toLowerCase().contains(query);

            // Check replies if thread title doesn't match
            List<Replies> replies = DatabaseHelper.getMessagesForThread(thread.getId());
            boolean repliesMatch = false;

            // Check if any reply matches the search query
            for (Replies reply : replies) {
                if (reply.getText().toLowerCase().contains(query)) {
                    repliesMatch = true;
                    break;
                }
            }

            // Display thread if either the thread title or replies match the search query
            if (threadMatches || repliesMatch) {
                displayArea.appendText("Thread #" + thread.getId() +
                        " | Title: " + thread.getTitle() +
                        " | Author: " + thread.getAuthor() + "\n");

                // Display replies that match the search query
                for (Replies reply : replies) {
                    if (reply.getText().toLowerCase().contains(query)) {
                        displayArea.appendText("   -> Reply #" + reply.getId() +
                                " by " + reply.getAuthor() +
                                ": " + reply.getText() + "\n");
                    }
                }
                displayArea.appendText("\n");
            }
        }
    }

    
   
    	//EDIT FOR LATER 
    	
    private void handleEditThread() {
        // Prompt user for thread ID
        String threadIdStr = showInputDialog("Edit Thread", "Enter the Thread ID to edit:");
        if (threadIdStr == null || threadIdStr.trim().isEmpty()) {
            return;
        }

        int threadId;
        try {
            threadId = Integer.parseInt(threadIdStr.trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Edit Error", "Invalid thread ID.");
            return;
        }

        // Debug: Print the thread ID being searched
        System.out.println("Searching for thread with ID: " + threadId);

        // Get the thread by ID
        Threads thread = DatabaseHelper.getThreadById(threadId);
        if (thread == null) {
            // Debug: Print a message if no thread is found
            System.out.println("No thread found with ID: " + threadId);
            showAlert(Alert.AlertType.ERROR, "Edit Error", "Thread not found.");
            return;
        }

        // Debug: Print the thread details
        System.out.println("Thread found: " + thread.getTitle() + " by " + thread.getAuthor());

        // Check if the current user is the author
        if (!thread.getAuthor().equals(user.getUserName())) {
            showAlert(Alert.AlertType.ERROR, "Edit Error", "You cannot edit this thread. It is not your own.");
            return;
        }

        // Prompt for new title
        String newTitle = showInputDialog("Edit Thread", "Enter a new title for the thread:");
        if (newTitle == null || newTitle.trim().isEmpty()) {
            return;
        }

        // Update the thread title
        DatabaseHelper.updateThreadTitle(threadId, newTitle);
        showAlert(Alert.AlertType.INFORMATION, "Edit Success", "Thread title updated successfully.");
        refreshDisplay(); // Refresh the display to show the updated thread
    }
  //EDIT FOR LATER 
    private void handleEditReply() {
        // Prompt user for reply ID
        String replyIdStr = showInputDialog("Edit Reply", "Enter the Reply ID to edit:");
        if (replyIdStr == null || replyIdStr.trim().isEmpty()) {
            return;
        }

        int replyId;
        try {
            replyId = Integer.parseInt(replyIdStr.trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Edit Error", "Invalid reply ID.");
            return;
        }

        // Debug: Print the reply ID being searched
        System.out.println("Searching for reply with ID: " + replyId);

        // Get the reply by ID
        Replies reply = DatabaseHelper.getReplyById(replyId);
        if (reply == null) {
            // Debug: Print a message if no reply is found
            System.out.println("No reply found with ID: " + replyId);
            showAlert(Alert.AlertType.ERROR, "Edit Error", "Reply not found.");
            return;
        }

        // Debug: Print the reply details
        System.out.println("Reply found: " + reply.getText() + " by " + reply.getAuthor());

        // Check if the current user is the author
        if (!reply.getAuthor().equals(user.getUserName())) {
            showAlert(Alert.AlertType.ERROR, "Edit Error", "You cannot edit this reply. It is not your own.");
            return;
        }

        // Prompt for new reply text
        String newText = showInputDialog("Edit Reply", "Enter the new reply text:");
        if (newText == null || newText.trim().isEmpty()) {
            return;
        }

        // Update the reply text
        DatabaseHelper.updateReplyText(replyId, newText);
        showAlert(Alert.AlertType.INFORMATION, "Edit Success", "Reply updated successfully.");
        refreshDisplay(); // Refresh the display to show the updated reply
    }


    
    
    
    
    private String showInputDialog(String title, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        return dialog.showAndWait().orElse(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}