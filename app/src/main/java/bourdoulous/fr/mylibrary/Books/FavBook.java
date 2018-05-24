package bourdoulous.fr.mylibrary.Books;

/*
    Cette classe définit un objet livre de type Favoris
    C'est à dire que l'on va ajouter divers attributs
        lu/a lire (read : 0/1)
        date de lecture (readDate : format dd/mm/yyyy)
        la note attribuée (note : 0 si livre pas lu,
                                  de 1 à 5 sinon)
 */

public class FavBook extends Book {

    private String readDate;
    private int read;
    private int note;


    public FavBook(String title, String author, String publisher, String category, String publishedDate, String readDate, int read, int note) {
        this.title = replace(title);
        this.author = replace(author);
        this.publisher = replace(publisher);
        this.category = replace(category);
        this.readDate = readDate;
        this.publishedDate = publishedDate;
        this.read = read;
        this.note = note;
    }

    private String replace(String str){
        if(str != null){
            return str.replace("'"," ");
        }
        return null;
    }

    @Override
    public String toString() {
        return "FavBook{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", category='" + category + '\'' +
                ", publishedDate='" + publishedDate + '\'' +
                ", readDate='" + readDate + '\'' +
                ", read=" + read +
                ", note=" + note +
                '}';
    }



    public int getRead() {
        return read;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public String getDate(){
        return readDate;
    }

}
