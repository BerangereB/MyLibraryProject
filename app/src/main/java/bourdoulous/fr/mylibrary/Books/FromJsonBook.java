package bourdoulous.fr.mylibrary.Books;


public class FromJsonBook extends Book {
    private String description;
    private int pageCount;
    private String imageLink;
    private String buyLink;


    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
                ", authors=" + author +
                ", publishedDate='" + publishedDate + '\'' +
                ", publisher='" + publisher + '\'' +
                ", description='" + description + '\'' +
                ", pageCount=" + pageCount +
                ", categories=" + category +
                ", imageLink='" + imageLink + '\'' +
                ", buyLink='" + buyLink + '\'' +
                '}';
    }

    // GETTERS & SETTERS

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getBuyLink() {
        return buyLink;
    }

    public void setBuyLink(String buyLink) {
        this.buyLink = buyLink;
    }
}
