
package databasePart1;
import java.sql.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;

import application.User;
import java.util.List;

import application.Replies;
import application.Threads;
import java.util.ArrayList;
import java.util.List;


/**
 * The DatabaseHelper class is responsible for managing the connection to the database,
 * performing operations such as user registration, login validation, and handling invitation codes.
 */
public class DatabaseHelper {

	// JDBC driver name and database URL 
	static final String JDBC_DRIVER = "org.h2.Driver";   
	static final String DB_URL = "jdbc:h2:~/FoundationDatabase";  

	//  Database credentials 
	static final String USER = "sa"; 
	static final String PASS = ""; 

	private Connection connection = null;
	private Statement statement = null; 
	//	PreparedStatement pstmt

	public void connectToDatabase() throws SQLException {
		try {
			Class.forName(JDBC_DRIVER); // Load the JDBC driver
			System.out.println("Connecting to database...");
			connection = DriverManager.getConnection(DB_URL, USER, PASS);
			statement = connection.createStatement(); 
			// You can use this command to clear the database and restart from fresh.
			//statement.execute("DROP ALL OBJECTS");

			createTables();  // Create the necessary tables if they don't exist
			//questionTableHW2(); //this is a new table to store questions
		} catch (ClassNotFoundException e) {
			System.err.println("JDBC Driver not found: " + e.getMessage());
		}
	}

	private void createTables() throws SQLException {
		String userTable = "CREATE TABLE IF NOT EXISTS cse360users ("
				+ "id INT AUTO_INCREMENT PRIMARY KEY, "
				+ "userName VARCHAR(255) UNIQUE, "
				+ "password VARCHAR(255), "
				+ "role VARCHAR(20))";
				//+ "emailAddress VARCHAR(255), "
				//+ "name VARCHAR(255))";
		
		statement.execute(userTable);
		
		// Create the invitation codes table
	    String invitationCodesTable = "CREATE TABLE IF NOT EXISTS InvitationCodes ("
	            + "code VARCHAR(10) PRIMARY KEY, "
	            + "isUsed BOOLEAN DEFAULT FALSE)";
	    statement.execute(invitationCodesTable);
	    
	    //create the studdent_questions table
	    String questionTableHW2 = "CREATE TABLE IF NOT EXISTS student_questions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255), "
	            + "question TEXT)";
	    statement.execute(questionTableHW2);
	    
	    String createThreads = "CREATE TABLE IF NOT EXISTS threads ("
                + "thread_id INTEGER AUTO_INCREMENT PRIMARY KEY,"
                + "title VARCHAR(255),"
                + "author VARCHAR(255)"
                + ")";
        statement.executeUpdate(createThreads);

        // Replies table
        String createMessages = "CREATE TABLE IF NOT EXISTS messages ("
        	    + "id INTEGER AUTO_INCREMENT PRIMARY KEY,"
        	    + "thread_id INTEGER,"           // Make sure this line exists
        	    + "text VARCHAR(255),"
        	    + "author VARCHAR(255),"
        	    + "isResolved BOOLEAN DEFAULT FALSE,"
        	    + "parent_id INTEGER,"
        	    + "FOREIGN KEY (thread_id) REFERENCES threads(thread_id) ON DELETE CASCADE,"
        	    + "FOREIGN KEY (parent_id) REFERENCES messages(id) ON DELETE CASCADE"
        	    + ")";
        	statement.executeUpdate(createMessages);
	}


	// Check if the database is empty
	public boolean isDatabaseEmpty() throws SQLException {
		String query = "SELECT COUNT(*) AS count FROM cse360users";
		ResultSet resultSet = statement.executeQuery(query);
		if (resultSet.next()) {
			return resultSet.getInt("count") == 0;
		}
		return true;
	}

	// Registers a new user in the database.
	public void register(User user) throws SQLException {
		String insertUser = "INSERT INTO cse360users (userName, password, role) VALUES (?, ?, ?)";
				//+ ", emailAddress, name) VALUES (?, ?, ?, ?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(insertUser)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRoles());
			//pstmt.setString(4, user.getemailAddress());
			//pstmt.setString(5, user.getname());
			pstmt.executeUpdate();
		}
	}

	// Validates a user's login credentials.
	public boolean login(User user) throws SQLException {
		String query = "SELECT * FROM cse360users WHERE userName = ? AND password = ? AND role = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			pstmt.setString(1, user.getUserName());
			pstmt.setString(2, user.getPassword());
			pstmt.setString(3, user.getRoles());
			try (ResultSet rs = pstmt.executeQuery()) {
				if(rs.next()) {
					String roles = rs.getString("role");
					user.setRoles(roles);
					return true;
				}
				
			}
		}
		return false;
	}
	
	// Checks if a user already exists in the database based on their userName.
	public boolean doesUserExist(String userName) {
	    String query = "SELECT COUNT(*) FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            // If the count is greater than 0, the user exists
	            return rs.getInt(1) > 0;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false; // If an error occurs, assume user doesn't exist
	}
	
	// Retrieves the role of a user from the database using their UserName.
	public String getUserRole(String userName) {
	    String query = "SELECT role FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        ResultSet rs = pstmt.executeQuery();
	        
	        if (rs.next()) {
	            return rs.getString("role"); // Return the role if user exists
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return null; // If no user exists or an error occurs
	}
	
	// Retrieves the user from the database
	
	public List<User> getAllUsers() throws SQLException{
		List<User> users = new ArrayList<>();
		String query = "SELECT userName, password, role FROM cse360users";
				//+ ", name, emailAddress FROM cse360users";
		try (PreparedStatement pstmt = connection.prepareStatement(query)) {
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String userName = rs.getString("userName");
				String password = rs.getString("password");
				String role = rs.getString("role");
				//String name = rs.getString("name");
				//String emailAddress = rs.getString("emailAddress");
				users.add(new User(userName, password, role));
						//, name, emailAddress));
			}
		}
		return users;
		
	}
	
	// Generates a new invitation code and inserts it into the database.
	public String generateInvitationCode() {
	    String code = UUID.randomUUID().toString().substring(0, 4); // Generate a random 4-character code
	    String query = "INSERT INTO InvitationCodes (code) VALUES (?)";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return code;
	}
	
	// Validates an invitation code to check if it is unused.
	public boolean validateInvitationCode(String code) {
		String query = "SELECT * FROM InvitationCodes WHERE code = ? AND isUsed = FALSE";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        ResultSet rs = pstmt.executeQuery();
	        if (rs.next()) {
	            // Mark the code as used
	            markInvitationCodeAsUsed(code);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	// Marks the invitation code as used in the database.
	private void markInvitationCodeAsUsed(String code) {
	    String query = "UPDATE InvitationCodes SET isUsed = TRUE WHERE code = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, code);
	        pstmt.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	//Method for deleting userName 
	public boolean DeleteUser(String userName, String adminUserName) throws SQLException {
		if(userName.equalsIgnoreCase(adminUserName)) {
			System.out.println("Unable to Delete User with the Admin Role.");
			return false;
		}
		String query = "DELETE FROM cse360users WHERE userName = ?";
	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, userName);
	        int userDeletion = pstmt.executeUpdate();
	        return userDeletion > 0;
	}
}
	
	//This is a helper method for the admin to  adding roles to a user
	public void addRoleFromAdmin(String userName, String role) throws SQLException {
	    String query = "UPDATE cse360users SET role = ? WHERE userName = ?";

	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
	        pstmt.setString(1, role);  
	        pstmt.setString(2, userName);  
	        int rowsAffected = pstmt.executeUpdate();

	        if (rowsAffected == 0) {
	            throw new SQLException("No user found with username: " + userName);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new SQLException("Error adding role to user: " + e.getMessage(), e);
	    }
	}

	
	//These helper methods are used for submitting answrs and questions
	
	//This allows the submission of questions for a student
	/*
	public void addQuestion(String userName, String question) throws SQLException{
		String query = "INSERT INTO cse360users (userName, role) VALUES (?.?)";
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);  
	        pstmt.setString(2, question);  
	        int rowsAffected = pstmt.executeUpdate();
			
		}
		
	}*/
	
	//create a new table to store student questions: 
	
	// Create the student_questions table if it doesn't exist.
	private void questionTableHW2() throws SQLException {
	    String createTableQuery = "CREATE TABLE IF NOT EXISTS student_questions ("
	            + "id INT AUTO_INCREMENT PRIMARY KEY, "
	            + "userName VARCHAR(255), "
	            + "question TEXT";
	           
	    statement.execute(createTableQuery);
	}
	
	//helper method to add question to new question table 
	
	public void addQuestion(String userName, String question) throws SQLException{
		String query = "INSERT INTO student_questions (userName, question) VALUES (?,?)"; // made changes here 
		try(PreparedStatement pstmt = connection.prepareStatement(query)){
			pstmt.setString(1, userName);  
	        pstmt.setString(2, question);  
	        pstmt.executeUpdate();
			
		}
		
	}


	
	
	// Closes the database connection and statement.
	public void closeConnection() {
		try{ 
			if(statement!=null) statement.close(); 
		} catch(SQLException se2) { 
			se2.printStackTrace();
		} 
		try { 
			if(connection!=null) connection.close(); 
		} catch(SQLException se){ 
			se.printStackTrace(); 
		} 
	}
	
	//Threads(Questions)
    public static void createThread(String author, String title) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO threads (title, author) VALUES (?, ?)")) {
            stmt.setString(1, title);
            stmt.setString(2, author);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Threads> getAllThreads() {
        List<Threads> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM threads")) {
            while (rs.next()) {
                int id = rs.getInt("thread_id");
                String title = rs.getString("title");
                String author = rs.getString("author");
                list.add(new Threads(id, title, author));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static void updateThread(int threadId, String newTitle) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE threads SET title=? WHERE thread_id=?")) {
            stmt.setString(1, newTitle);
            stmt.setInt(2, threadId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteThread(int threadId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM threads WHERE thread_id=?")) {
            stmt.setInt(1, threadId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

  //Replies
    public static void createMessage(int threadId, String text, String author) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO messages (thread_id, text, author) VALUES (?, ?, ?)")) {
            stmt.setInt(1, threadId);
            stmt.setString(2, text);
            stmt.setString(3, author);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Replies> getMessagesForThread(int threadId) {
        List<Replies> list = new ArrayList<>();
        String query = "SELECT * FROM messages WHERE thread_id=? ORDER BY id";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, threadId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String text = rs.getString("text");
                    String author = rs.getString("author");
                    boolean isResolved = rs.getBoolean("isResolved");
                    Integer parentId = rs.getObject("parent_id") == null 
                                       ? null 
                                       : rs.getInt("parent_id");
                    list.add(new Replies(id, threadId, text, author, isResolved, parentId));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    
   

  //EDIT FOR LATER  
    public static Threads getThreadById(int threadID) {
        String query = "SELECT * FROM threads WHERE thread_id = ?"; // Fixed column name
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, threadID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Threads(
                    rs.getInt("thread_id"), // Fixed column name
                    rs.getString("title"),
                    rs.getString("author")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no thread is found
    }


  //EDIT FOR LATER 
    public static Replies getReplyById(int replyID) {
        String query = "SELECT * FROM messages WHERE id = ?"; // Ensure column name matches
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, replyID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new Replies(
                    rs.getInt("id"),
                    rs.getInt("thread_id"),
                    rs.getString("text"),
                    rs.getString("author")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no reply is found
    }
  //EDIT FOR LATER 
    public static void updateThreadTitle(int threadID, String newTitle) {
        String query = "UPDATE threads SET title = ? WHERE thread_id = ?"; // Fixed column name
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newTitle);
            pstmt.setInt(2, threadID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
  //EDIT FOR LATER 
    public static void updateReplyText(int replyID, String newText) {
        String query = "UPDATE messages SET text = ? WHERE id = ?"; // Ensure column name matches
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newText);
            pstmt.setInt(2, replyID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
    public static void updateMessage(int messageId, String newText) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE messages SET text=? WHERE id=?")) {
            stmt.setString(1, newText);
            stmt.setInt(2, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteMessage(int messageId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM messages WHERE id=?")) {
            stmt.setInt(1, messageId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void markReplyAsResolved(int replyId) {
        String query = "UPDATE messages SET isResolved = TRUE WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, replyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void createReplyToReply(int parentReplyId, String text, String author) {
        // First, find which thread the parent reply belongs to
        Replies parent = getReplyById(parentReplyId);
        if (parent == null) {
            // Could throw an exception or handle the error
            return;
        }
        String sql = "INSERT INTO messages (thread_id, text, author, isResolved, parent_id) "
                   + "VALUES (?, ?, ?, FALSE, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, parent.getThreadId());
            stmt.setString(2, text);
            stmt.setString(3, author);
            stmt.setInt(4, parentReplyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}