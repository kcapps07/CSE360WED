package application;

/*******
 * <p> Title: HW3AutomatedTests Class. </p>
 * 
 * <p> Description: A Java demonstration for semi-automated tests on team project 2's code </p>
 * 
 * @author Kaden Capps
 * 
 * @version 1.00	2025-03-25 5 automated test cases based to fulfill HW3 CSE360
 * 
 */

public class HW3AutomatedTests {
    private static int numPassed = 0; //counter for passed tests
    private static int numFailed = 0; //counter for test failed

    public static void main(String[] args){
        testAddReplyToThread(1, 1001,1, "This is a test reply.", "UserTh4tReplies!", true);
        testCreateEmptyThread(2, 2001, "This is a standalone thread","thread_r1pper", true);
        testMaxLengthCredentials(3, "Maximillion_Arbside0", "SomeVery_L0ng_street", "admin", true);
        testMinLengthCredentials(4,"OKi", "Pass_001", "user", true);
        performUpdateThread(5, 8, "User001", "This is a thread by User001","User002", "Update tried by User002", false);   //expected to fail
        
        System.out.println("Tests Passed: "+numPassed);	//outputs final amount of fails & passes
        System.out.println("Tests Failed: "+numFailed);
    }
    
    
    /**
     * Testing to add a reply to a thread
     *
     * @param testCase     The test case number
     * @param replyId      The ID of the reply
     * @param  threadId     The ID of the thread which the reply is being added
     * @param text         The text field of the reply
     * @param auth0r       Who makes the reply
     * @param expectedPass Tells if test is expected to pass.
     */
    private static void testAddReplyToThread(int testCase, int replyId, int threadId, String text, String author, boolean expectedPass){
        System.out.println("Test case: "+ testCase);	//header for test output
        System.out.println("Testing reply can be added to a thread");
        System.out.println("______________ ");
        Replies newReply = null;
        boolean isTestPassed =false;
        try{
            newReply = new Replies(replyId, threadId, text, author);	//attempts to create a reply with the username and text provided
            if(newReply.getId()==replyId && newReply.getThreadId()==threadId && newReply.getText().equals(text) && newReply.getAuthor().equals(author)){
                isTestPassed = true;
            }
        } catch(Exception e){
            System.out.println("Error: "+e.getMessage());
        }

        evaluateTest(isTestPassed, expectedPass); //checks if test behaved as expected 
    }
    
    
    
    
    /**
     * Test to create a standalone thread
     *
     * @param testCase     The test case number
     * @param threadId     The id of the thread thats being made
     * @param title        The title of the thread
     * @param author       The creator of the thread
     * @param expectedPass Tells if the test is expected to pass
     */
    private static void testCreateEmptyThread(int testCase, int threadId, String title, String author, boolean expectedPass){
        System.out.println("Test case: " + testCase);		//test output header 
        System.out.println("Testing empty thread creation ");
        System.out.println("______________");
        Threads newThread = null;
        boolean isTestPassed = false;

        try{
            newThread = new Threads(threadId, title, author);	//atempting to create a new thread with given username and text
            if(newThread.getId()==threadId && newThread.getTitle().equals(title) && newThread.getAuthor().equals(author)){
                isTestPassed = true;
            }
        } catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
        evaluateTest(isTestPassed, expectedPass);		//checks to make sure the test passed as expected 
    }

    /**
     * Tests user creation with largest possible length username and password
     *
     * @param testCase     The test case number
     * @param username     The username to be tested
     * @param password     The password to be tested
     * @param roles        The roles assigned to the user
     * @param expectedPass tells if the test is expected to pass
     */
    private static void testMaxLengthCredentials(int testCase, String username, String password, String roles, boolean expectedPass){
        System.out.println("Test case: " + testCase);    //header for test output
        System.out.println("Testing largest credentials possible");
        System.out.println("______________");
        User user = null;
        boolean isTestPassed =false;
        try{
            user = new User(username, password, roles);		//makes sure that a user can be made using the max length for username and password 
            if(user.getUserName().length()==20 && user.getPassword().length()== 20) {
                isTestPassed =true;
            }
        } catch(Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
        evaluateTest(isTestPassed, expectedPass);	//test if passed as expected 
    }
    
    /**
     * Tests user creation with smallest possible smallest username and password
     *
     * @param testCase     The test case number
     * @param username     The username to be tested
     * @param password     The password to be tested
     * @param roles        The roles assigned to the user
     * @param expectedPass tells if the test is expected to pass
     */
    private static void testMinLengthCredentials(int testCase, String username, String password, String roles, boolean expectedPass){
        System.out.println("Test case: " + testCase);	//output header for test 
        System.out.println("Testing min-length credentials");
        System.out.println("______________");

        User user =null;
        boolean isTestPassed =false;
        try{
            user =new User(username, password, roles);		//test to make sure that a user can be made with the shortest possible usernamen and password 
            if(user.getUserName().length()>=3 && user.getPassword().length()>=8){
                isTestPassed= true;
            }
        } catch(Exception e){
            System.out.println("Error: " + e.getMessage());
        }
        evaluateTest(isTestPassed, expectedPass);	//test is pass as expected
    }
    
    /**
     * Tests updating a thread from an user who is not the author
     *
     * @param testCase     The test case number
     * @param threadId     The id of the thread tring to update updated
     * @param author       The author of the thread
     * @param originalText The original content of the thread
     * @param editor       The user attempting to edit the thread
     * @param newContent   The new content tested to update the thread with
     * @param expectedPass Tells whether the test is expected to pass
     */
    private static void performUpdateThread(int testCase, int threadId, String author, String originalText, String editor, String newContent, boolean expectedPass){
        System.out.println("Test case: " + testCase);	//test header for output
        System.out.println("Testing non-author tries to edit thread");
        System.out.println("______________");	

        Threads threadToEdit =new Threads(threadId, author, originalText);
        boolean isTestPassed =true;
        try{
	        if(author.equals(editor)){		//uses same logic as editor 
	            threadToEdit.setTitle(newContent);	//if possible updates the thread 
	            isTestPassed =true; 
	        } else {
	        	isTestPassed = false; 
	        } 
        } catch (Exception e){
            System.out.println("Error: "+ e.getMessage());
        }
        evaluateTest(isTestPassed,expectedPass);	//checks if failed as expected 
    }
    
    /**
     * Test by comparison if a test has given the expected pass or fail 
     *
     * @param isTestPassed The boolean value that a test case has ended with
     * @param expectedPass Tells whether the test is expected to pass
     */
    private static void evaluateTest(boolean isTestPassed, boolean expectedPass){
        if(isTestPassed == expectedPass){	//you can read and hopefully know what if statement does 
            System.out.println("***Success*** Test passed as expected.");
            numPassed++;
        } else{
            System.out.println("***Failure*** Test did not behave as expected.");
            numFailed++;
            
        }
        System.out.println("=====================================");
    }
    
    
}
