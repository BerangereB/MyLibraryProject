package bourdoulous.fr.mylibrary.Accounts;

import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.R;

import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_PASSWORD_COLUMN;
import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_QUESTION_ANSWER_COLUMN;


public class PasswordMissedActivity extends AppCompatActivity {

    private TextInputLayout usernameInputLayout;
    private TextInputLayout answerInputLayout;
    private Button getPasswordButton;
    private String answerInput;
    private String usernameInput;
    private Animation animationShake;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_missed);

        initViews();
        animationShake = AnimationUtils.loadAnimation(this, R.anim.shake);

        getPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPassword();
            }
        });

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }

    private void initViews() {
        usernameInputLayout = findViewById(R.id.usernameLayout);
        answerInputLayout = findViewById(R.id.answerLayout);
        getPasswordButton = findViewById(R.id.valid);
    }

    private boolean checkFields() {
        boolean hasEmptyFields = false;
        if (usernameInput.isEmpty()) {
            hasEmptyFields = true;
            usernameInputLayout.setErrorEnabled(true);
            usernameInputLayout.setError(getString(R.string.empty_fields));
            usernameInputLayout.startAnimation(animationShake);
        }
        if (answerInput.isEmpty()) {
            hasEmptyFields = true;
            answerInputLayout.setErrorEnabled(true);
            answerInputLayout.setError(getString(R.string.empty_fields));
            answerInputLayout.startAnimation(animationShake);
        }

        return !hasEmptyFields;
    }

    public boolean checkUsernameAnswer() {

        MyDatabaseHelper databaseHelper = new MyDatabaseHelper(this);
        Cursor cursor = databaseHelper.getAccountData(usernameInput);

        if (cursor.getCount() == 0) {
            usernameInputLayout.setErrorEnabled(true);
            usernameInputLayout.setError(getString(R.string.username_doesnt_exist));
            usernameInputLayout.startAnimation(animationShake);
            cursor.close();
            return false;
        }
        cursor.moveToFirst();
        String answer = cursor.getString(cursor.getColumnIndex(ACCOUNT_QUESTION_ANSWER_COLUMN));

        if (answer != null && answer.equals(answerInput)) {
            password = cursor.getString(cursor.getColumnIndex(ACCOUNT_PASSWORD_COLUMN));
            cursor.close();
            return true;
        } else {
            answerInputLayout.setErrorEnabled(true);
            answerInputLayout.setError(getString(R.string.wrong_answer));
            answerInputLayout.startAnimation(animationShake);
        }
        cursor.close();
        return false;
    }


    public void showPassword() {
        usernameInputLayout.setErrorEnabled(false);
        usernameInputLayout.setErrorEnabled(false);

        usernameInput = usernameInputLayout.getEditText().getText().toString();
        answerInput = answerInputLayout.getEditText().getText().toString();

        if (checkFields()) {
            if (checkUsernameAnswer()) {
                new AlertDialog.Builder(this)
                        .setMessage(getString(R.string.your_pwd_is) + " " + password + getString(R.string.keep_it))
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                onBackPressed();
                                finish();
                            }
                        })
                        .show();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
