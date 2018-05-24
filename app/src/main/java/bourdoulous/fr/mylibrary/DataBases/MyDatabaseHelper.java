package bourdoulous.fr.mylibrary.DataBases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_FIRSTNAME_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_PASSWORD_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_QUESTION_ANSWER_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_USERNAME_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.DATABASE_ACCOUNT_NAME;

public class MyDatabaseHelper extends SQLiteOpenHelper {


    private FavoriteBooksHelper favoriteBooksHelper;

    private AccountHelper accountHelper;


    public MyDatabaseHelper(Context context) {
        super(context,"libraryDatabase", null, 3);
        favoriteBooksHelper = new FavoriteBooksHelper();
        accountHelper = new AccountHelper();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // table comptes
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS accounts_table");
        sqLiteDatabase.execSQL("CREATE TABLE accounts_table(username text primary key, password text, answer text, firstname text, avatar text)");

        //Ajout de l'utilisateur par défaut (admin|admin)
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_USERNAME_COLUMN, "admin");
        contentValues.put(ACCOUNT_PASSWORD_COLUMN, "admin");
        contentValues.put(ACCOUNT_FIRSTNAME_COLUMN, "The Great Admin!");
        contentValues.put(ACCOUNT_QUESTION_ANSWER_COLUMN, "admin");
        sqLiteDatabase.insert(DATABASE_ACCOUNT_NAME, null, contentValues);

        // table livres
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS favorites");
        sqLiteDatabase.execSQL("CREATE TABLE favorites(owner text, title text, author text, publisher text, category text,published_date text, read integer, read_date text, note integer)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        onCreate(sqLiteDatabase);
    }


    /******************** ACCOUNTS ******************/

    public void insertAccount(String username, String password, String answer, String firstname, String avatar){
        SQLiteDatabase database = getWritableDatabase();
        accountHelper.insertAccount(database, username,password, answer, firstname, avatar);
    }

    public Cursor getAccountData(String username){
        SQLiteDatabase database = getWritableDatabase();
        return accountHelper.getData(database,username);
    }

    public void updateAccount(String username, String password, String answer, String firstname,String avatar){
        SQLiteDatabase database = getWritableDatabase();
        accountHelper.updateAccount(database,username,password,answer,firstname,avatar);
    }

    public void deleteAccount(String username){
        SQLiteDatabase database = getWritableDatabase();
        accountHelper.deleteAccount(database, username);
    }





    /********************** BOOKS *******************/

    public boolean insertBook(String owner, String title, String auhtor, String publisher, String category, String publishedDate, Boolean read, String read_date, int note) {
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.insertBook(database, owner, title, auhtor, publisher, category, publishedDate, read, read_date, note);
    }

    public void deleteBook(String owner, String title) {
        SQLiteDatabase database = getWritableDatabase();
        favoriteBooksHelper.deleteBook(database, owner, title);
    }

    public Cursor getBooksData(String owner, int read){
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.getData(database,owner,read);    }

    public Cursor getBooksData(String owner){
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.getData(database,owner);
    }

    public Cursor getBooksData(String owner, String title){
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.getData(database,owner,title);
    }

    public Cursor getDataGrades(String owner, int grade){
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.getDataGrades(database, owner, grade);
    }

    public Cursor getAuthorsByCount(String owner) {
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.getAuthorsByCount(database, owner);
    }

    public Cursor getGrades(String owner) {
        SQLiteDatabase database = getWritableDatabase();
        return favoriteBooksHelper.getGrades(database,owner);
    }
}
