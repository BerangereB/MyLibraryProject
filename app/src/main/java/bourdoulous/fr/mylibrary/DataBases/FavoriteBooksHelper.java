package bourdoulous.fr.mylibrary.DataBases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class FavoriteBooksHelper {

    private static final String OWNER_COLUMN = "owner"; // username
    static final String TITLE_COLUMN = "title";
    static final String AUTHOR_COLUMN = "author";
    static final String PUBLISHER_COLUMN = "publisher";
    static final String CATEGORY_COLUMN = "category";
    static final String PUBLISHED_DATE_COLUMN = "published_date";
    static final String READ_COLUMN = "read"; // 1 if read / 0 if to read
    static final String READ_DATE_COLUMN = "read_date";
    static final String NOTE_COLUMN = "note";

    public boolean insertBook(SQLiteDatabase database, String owner, String title, String auhtor, String publisher, String category, String publishedDate, Boolean read, String read_date, int note){
        Cursor cursor = database.rawQuery("SELECT * FROM favorites WHERE title = '"+title +"' AND owner = '"+owner +"';",null);

        if (cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            putInfo(contentValues, owner, title, auhtor, publisher, category, publishedDate, read, read_date, note);

            database.insert("favorites", null, contentValues);
            cursor.close();
            return true;
        }else{
            cursor.close();
            return false;
        }
    }

    private void putInfo(ContentValues contentValues, String owner, String title, String auhtor, String publisher, String category, String publishedDate, Boolean read, String read_date,int note) {
        contentValues.put(OWNER_COLUMN,owner);
        contentValues.put(TITLE_COLUMN,title);
        contentValues.put(AUTHOR_COLUMN,auhtor);
        contentValues.put(PUBLISHER_COLUMN,publisher);
        contentValues.put(CATEGORY_COLUMN,category);
        contentValues.put(PUBLISHED_DATE_COLUMN,publishedDate);

        int int_read;
        if(read){
            int_read = 1;
        }else{
            int_read = 0;
        }
        contentValues.put(READ_COLUMN,int_read);
        contentValues.put(NOTE_COLUMN,note);

        if(read_date != null && !read_date.isEmpty()){
            contentValues.put(READ_DATE_COLUMN, read_date);
        }
    }


    public void deleteBook(SQLiteDatabase database, String owner, String title){
        database.delete("favorites", "owner = '"+owner+"' AND title = '"+title+"'",null);
    }

    public Cursor getData(SQLiteDatabase database,String owner, int read){
        return database.rawQuery("SELECT * FROM favorites WHERE owner = '"+owner +"' AND read = "+ read +" ORDER BY LOWER(title) ASC;",null);
    }

    public Cursor getData(SQLiteDatabase database,String owner){
        return database.rawQuery("SELECT * FROM favorites WHERE owner = '"+owner +"' ORDER BY LOWER(title) ASC;",null);
    }

    public Cursor getData(SQLiteDatabase database,String owner, String title){
        return database.rawQuery("SELECT * FROM favorites WHERE owner = '"+owner + "' AND title = '"+title+"' ORDER BY LOWER(title) ASC",null);
    }

    public Cursor getDataGrades(SQLiteDatabase database,String owner, int grade){
        return database.rawQuery("SELECT * FROM favorites WHERE owner = '"+owner + "' AND note = '"+grade+"' ORDER BY LOWER(title) ASC",null);
    }

    public Cursor getAuthorsByCount(SQLiteDatabase database,String owner) {
        return database.rawQuery("SELECT author, count(*) count FROM " +
                "(SELECT author FROM favorites WHERE owner = '"+owner + "')" +
                " GROUP BY author ORDER BY count DESC",null);
    }

    public Cursor getGrades(SQLiteDatabase database,String owner) {
        return database.rawQuery("SELECT note FROM favorites WHERE owner = '"+owner + "' AND note > 0;",null);
    }
}
