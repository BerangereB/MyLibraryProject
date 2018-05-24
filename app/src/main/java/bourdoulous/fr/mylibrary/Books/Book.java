package bourdoulous.fr.mylibrary.Books;

public class Book {

    protected String title;
    protected String author;
    protected String publisher;
    protected String category;
    protected String publishedDate;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPublishedDate(){
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate){
        this.publishedDate = publishedDate;
    }
}
