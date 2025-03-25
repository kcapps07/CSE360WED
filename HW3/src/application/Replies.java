package application;

public class Replies {
	  private int id;
	  private int threadId;
	  private String text;
	  private String author;
	  private boolean isResolved;
	  private Integer parentId;

	  // Primary constructor (all parameters)
	  public Replies(int id, int threadId, String text, String author, boolean isResolved, Integer parentId) {
	    this.id = id;
	    this.threadId = threadId;
	    this.text = text;
	    this.author = author;
	    this.isResolved = isResolved;
	    this.parentId = parentId;
	  }

	  // Overloaded constructor
	  public Replies(int id, int threadId, String text, String author) {
	    this(id, threadId, text, author, false, null);
	  }

	  // Overloaded constructor
	  public Replies(int id, int threadId, String text, String author, boolean isResolved) {
	    this(id, threadId, text, author, isResolved, null);
	  }

	  public int getId() { return id; }
	  public int getThreadId() { return threadId; }
	  public String getText() { return text; }
	  public String getAuthor() { return author; }
	  public boolean isResolved() { return isResolved; }
	  public void setResolved(boolean resolved) { this.isResolved = resolved; }
	  public Integer getParentId() { return parentId; }
	  public void setParentId(Integer parentId) { this.parentId = parentId; }

	  @Override
	  public String toString() {
	    return "[Reply " + id + "] " + text + " (by " + author + ")" + (isResolved ? " [Marked as resolved]" : "");
	  }
	}