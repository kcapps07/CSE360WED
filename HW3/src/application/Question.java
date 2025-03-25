package application;
import java.util.ArrayList;
import java.util.List;

public class Question {

	//look back to user stories to find params needed
	private String question;
	private String whoAsked;
	private List<Answer> potentialAnswers; 
	private boolean questionResolved; 
	
//add constructors here 
	public Question(String question, String whoAsked) {
		this.question = question; 
		this.whoAsked = whoAsked;
		this.potentialAnswers = new ArrayList<>();
		this.questionResolved = false;
	}

	
	//add getter methods 
	
	public String getQuestion() {return question;}
	public String getWhoAsked() {return whoAsked;}
	public List<Answer> getPotentialAnswers(){return potentialAnswers; }
	public boolean getResolve() {return questionResolved;}
	
	//add setter methods 
	public void setQuestion(String question) {this.question = question; }
	public void setWhoAsked(String whoAsked) {this.whoAsked = whoAsked;}
	public void setResolved(boolean resolved) {questionResolved = resolved;}
	
	
//future helper methods which could help later in establishing use cases 
	
	public void putInAnswer(Answer answer) {potentialAnswers.add(answer);}
	public void markResolved() {this.questionResolved = true;}
	
	
	
	
	
}