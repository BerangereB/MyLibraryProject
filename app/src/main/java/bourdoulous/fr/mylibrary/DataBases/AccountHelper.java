package bourdoulous.fr.mylibrary.DataBases;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


public class AccountHelper {

    static final String DATABASE_ACCOUNT_NAME = "accounts_table";
    static final String ACCOUNT_USERNAME_COLUMN = "username";
    public static final String ACCOUNT_PASSWORD_COLUMN = "password";
    public static final String ACCOUNT_QUESTION_ANSWER_COLUMN = "answer";
    static final String ACCOUNT_FIRSTNAME_COLUMN = "firstname";
    public static final String ACCOUNT_AVATAR_COLUMN = "avatar";

    public void insertAccount(SQLiteDatabase database,String username, String password, String answer, String firstname, String avatar){
        Cursor cursor = getData(database, username);

        //Si le username n'existe pas déjà
        if (cursor.getCount() == 0) {
            ContentValues contentValues = new ContentValues();
            putInfo(database, contentValues,username,password,answer,firstname,avatar, false);
            database.insert(DATABASE_ACCOUNT_NAME, null, contentValues);
        }
        cursor.close();
    }

    private void putInfo(SQLiteDatabase database, ContentValues contentValues, String username, String password, String answer, String firstname, String avatar, Boolean isUpdate){


        // INSERT
        if(!isUpdate){
            contentValues.put(ACCOUNT_PASSWORD_COLUMN, password);

            contentValues.put(ACCOUNT_USERNAME_COLUMN, username);

            contentValues.put(ACCOUNT_QUESTION_ANSWER_COLUMN, answer);

            if(!firstname.isEmpty()){
                contentValues.put(ACCOUNT_FIRSTNAME_COLUMN, firstname);
            }else{
                contentValues.put(ACCOUNT_FIRSTNAME_COLUMN, "");
            }

            /// AVATAR MODIFICATION
            if(!avatar.isEmpty()){
                contentValues.put(ACCOUNT_AVATAR_COLUMN, avatar);
            }else{
                contentValues.put(ACCOUNT_AVATAR_COLUMN, "");
            }

        // UPDATE
        }else{
            Cursor cursor = getData(database, username);
            cursor.moveToFirst();

            if(password != null && !password.isEmpty()) {
                contentValues.put(ACCOUNT_PASSWORD_COLUMN, password);
            }else{
                contentValues.put(ACCOUNT_PASSWORD_COLUMN, cursor.getString(cursor.getColumnIndex(ACCOUNT_PASSWORD_COLUMN)));
            }
            /// ANSWER TO THE QUESTION MODIFICATION
            if(answer != null && !answer.isEmpty()){
                contentValues.put(ACCOUNT_QUESTION_ANSWER_COLUMN, answer);
            }else{
                contentValues.put(ACCOUNT_QUESTION_ANSWER_COLUMN, cursor.getString(cursor.getColumnIndex(ACCOUNT_QUESTION_ANSWER_COLUMN)));
            }

            /// FIRSTNAME MODIFICATION
            if(firstname != null && !firstname.isEmpty()){
                contentValues.put(ACCOUNT_FIRSTNAME_COLUMN, firstname);
            }else{
                contentValues.put(ACCOUNT_FIRSTNAME_COLUMN, cursor.getString(cursor.getColumnIndex(ACCOUNT_FIRSTNAME_COLUMN)));
            }

            /// AVATAR MODIFICATION
            if(avatar != null && !avatar.isEmpty()){
                contentValues.put(ACCOUNT_AVATAR_COLUMN, avatar);
            }else{
                contentValues.put(ACCOUNT_AVATAR_COLUMN, cursor.getString(cursor.getColumnIndex(ACCOUNT_AVATAR_COLUMN)));
            }
            cursor.close();
        }
    }

    public Cursor getData(SQLiteDatabase database,String username){
        return database.rawQuery("SELECT * FROM accounts_table WHERE username = '"+username +"';",null);
    }

    public void updateAccount(SQLiteDatabase database,String username, String password, String answer, String firstname,String avatar){
        ContentValues contentValues = new ContentValues();
        putInfo(database, contentValues,username,password,answer,firstname,avatar,true);
        database.update(DATABASE_ACCOUNT_NAME,contentValues,"username = ?",new String[]{username});
    }

    public void deleteAccount(SQLiteDatabase database,String username){
        database.delete(DATABASE_ACCOUNT_NAME, "username = ?", new String[]{username});
    }
}
