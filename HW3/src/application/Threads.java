package application;

public class Threads {
    private int id;
    private String title;
    private String author;

    public Threads(int id, String title, String author) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Thread title cannot be empty.");
        }
        this.id = id;
        this.title = title;
        this.author = author;
    }

    public int getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }

    public void setTitle(String newTitle) {
        if (newTitle == null || newTitle.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty.");
        }
        this.title = newTitle;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
 
    @Override
    public String toString() {
        return "[#" + id + "] " + title + " (by " + author + ")";
    }
}