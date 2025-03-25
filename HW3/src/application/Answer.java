package application;
//import java.util.ArrayList;
//import java.util.List;

public class Answer {
private String answer;
private boolean doesResolveAnswer;



//add constructor here
public Answer(String answer) {this.answer = answer; this.doesResolveAnswer = false;}


//getter methods here 
public String getAnswer(){return answer;}
public boolean getIfResolved() {return doesResolveAnswer;}

//setter methods here
public void setAnswer(String answer) {this.answer = answer;}
public void setResolved(boolean resolved) {doesResolveAnswer = resolved;}

//helper methods to use for future refrence 
//Mark the answer as the resolver 
public void markAsResolved() {this.doesResolveAnswer = true;}

}