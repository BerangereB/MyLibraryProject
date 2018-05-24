package bourdoulous.fr.mylibrary.DataBases;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.Books.FavBook;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.AUTHOR_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.CATEGORY_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.NOTE_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.PUBLISHED_DATE_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.PUBLISHER_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.READ_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.READ_DATE_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.FavoriteBooksHelper.TITLE_COLUMN;

public class FromDatabaseToBook {

    public static List<FavBook> convert(Context context){
        Cursor cursor = new MyDatabaseHelper(context).getBooksData(AccountStateManager.getSingleton().getAccountConnected());
        List<FavBook> books = getFavBookList(cursor);
        cursor.close();
        return books;
    }

    public static List<FavBook> convert(Context context, int read){
        Cursor cursor = new MyDatabaseHelper(context).getBooksData(AccountStateManager.getSingleton().getAccountConnected(),read);
        List<FavBook> books = getFavBookList(cursor);
        cursor.close();
        return books;
    }

    private static List<FavBook> getFavBookList(Cursor cursor) {
        List<FavBook> favBookList = new ArrayList<>();
        cursor.moveToFirst();

        int n = cursor.getCount();

        for(int i = 0; i<n ; i++){
            favBookList.add(new FavBook(cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(AUTHOR_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(PUBLISHER_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(PUBLISHED_DATE_COLUMN)),
                    cursor.getString(cursor.getColumnIndex(READ_DATE_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(READ_COLUMN)),
                    cursor.getInt(cursor.getColumnIndex(NOTE_COLUMN))
            ));
            cursor.moveToNext();
        }
        cursor.close();
        return favBookList;
    }

    public static FavBook convert(Context context, String title){
        Cursor cursor = new MyDatabaseHelper(context).getBooksData(AccountStateManager.getSingleton().getAccountConnected(),title);
        cursor.moveToFirst();
        FavBook favBook =  new FavBook(cursor.getString(cursor.getColumnIndex(TITLE_COLUMN))
                        ,cursor.getString(cursor.getColumnIndex(AUTHOR_COLUMN))
                        ,cursor.getString(cursor.getColumnIndex(PUBLISHER_COLUMN))
                        ,cursor.getString(cursor.getColumnIndex(CATEGORY_COLUMN))
                        ,cursor.getString(cursor.getColumnIndex(PUBLISHED_DATE_COLUMN))
                        ,cursor.getString(cursor.getColumnIndex(READ_DATE_COLUMN))
                        ,cursor.getInt(cursor.getColumnIndex(READ_COLUMN))
                        ,cursor.getInt(cursor.getColumnIndex(NOTE_COLUMN))
                    );

        cursor.close();
        return favBook;
    }

    public static String[] getBooksByGrades(Context context, int grade){
        Cursor cursor = new MyDatabaseHelper(context).getDataGrades(AccountStateManager.getSingleton().getAccountConnected(),grade);
        cursor.moveToFirst();
        String[] booksTitle = new String[cursor.getCount()];
        for(int i = 0; i < booksTitle.length ; i++) {
            String author = cursor.getString(cursor.getColumnIndex(AUTHOR_COLUMN));
            booksTitle[i] = cursor.getString(cursor.getColumnIndex(TITLE_COLUMN)) + " by " + (author.isEmpty()?" ? ":author);
            cursor.moveToNext();
        }

        cursor.close();
        return booksTitle;
    }

    public static List<String> getAuthorsWithOcc(Context context) {
        Cursor cursor = new MyDatabaseHelper(context).getAuthorsByCount(AccountStateManager.getSingleton().getAccountConnected());
        cursor.moveToFirst();
        int maxOcc = cursor.getInt(1);
        List<String> booksAuthor = new ArrayList<>();
        booksAuthor.add(cursor.getString(0));
        cursor.moveToNext();
        while(!cursor.isAfterLast() && cursor.getInt(1) == maxOcc){
            booksAuthor.add(cursor.getString(0));
            cursor.moveToNext();
        }

        cursor.close();
        return booksAuthor;
    }

    public static int[] getGrades(Context context) {
        Cursor cursor = new MyDatabaseHelper(context).getGrades(AccountStateManager.getSingleton().getAccountConnected());
        cursor.moveToFirst();
        int[] grades = new int[cursor.getCount()];
        for(int i = 0; i < grades.length; i++){
            grades[i] = cursor.getInt(0);
            cursor.moveToNext();
        }
        cursor.close();
        return grades;
    }
}
