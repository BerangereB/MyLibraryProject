package bourdoulous.fr.mylibrary.Accounts;


import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.WelcomeActivities.MainActivity;
import bourdoulous.fr.mylibrary.R;

import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_PASSWORD_COLUMN;

public class LoginActivity extends AppCompatActivity  {
    private String TAG = "LoginActivity";

    private Button sign_in;
    private TextView sign_up;
    private TextView password_missed;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout usernameInputLayout;


    Animation animationShake;

    AccountStateManager accountStateManager = AccountStateManager.getSingleton();
    MyDatabaseHelper databaseHelper = new MyDatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // findViewsById
        initViews();

        setListeners();

        // shaker used when entries have errors
        animationShake = AnimationUtils.loadAnimation(this,R.anim.shake);


    }

        /////////////////////////////
        ////////// METHODS /////////

    private void setListeners() {
        sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Sign in button clicked");
                signIn();
            }
        });
        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"Sign up button clicked");
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
        password_missed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG,"PasswordMissed button clicked");
                Intent intent = new Intent(LoginActivity.this, PasswordMissedActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViews() {
        sign_in = findViewById(R.id.sign_in);
        sign_up = findViewById(R.id.sign_up);

        password_missed = findViewById(R.id.password_missed);

        usernameInputLayout = findViewById(R.id.usernameLayout);
        usernameInputLayout.setError(null);
        passwordInputLayout = findViewById(R.id.passwordLayout);
        passwordInputLayout.setError(null);
    }



    private void signIn() {

        // On récupère les entrées de l'utilisateur
        EditText usernameInput = usernameInputLayout.getEditText();
        EditText passwordInput = passwordInputLayout.getEditText();
        String username, password;
        if(usernameInput != null && passwordInput != null){
            username = usernameInput.getText().toString();
            password = passwordInput.getText().toString();
        }else{
            return;
        }

        passwordInputLayout.setErrorEnabled(false);
        usernameInputLayout.setErrorEnabled(false);

        boolean isValid = validations(username,password);
        if(!isValid) {
            Log.i(TAG,"Errors detected in entries");
            return;
        }

        passwordInputLayout.setErrorEnabled(false);
        usernameInputLayout.setErrorEnabled(false);

        Log.i(TAG,"Connexion de l'utilisateur " + username);
        accountStateManager.connect(username);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }


    private boolean validations(String usernameInput, String passwordInput) {

        boolean hasEmptyFields = false;

        // Les champs sont-il remplis ?
        if(usernameInput.isEmpty() ){
            Log.i(TAG,"Username field is empty");
            usernameInputLayout.setErrorEnabled(true);
            usernameInputLayout.setError(getResources().getString(R.string.empty_fields));
            usernameInputLayout.startAnimation(animationShake);
            hasEmptyFields = true;
        }

        if(passwordInput.isEmpty() ){
            Log.i(TAG,"Password field is empty");
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getResources().getString(R.string.empty_fields));
            passwordInputLayout.startAnimation(animationShake);
            hasEmptyFields = true;
        }
        if(hasEmptyFields){
            return false;
        }

        // On récupère l'ensemble des lignes de la base de données utilisateur
        // qui ont usernameInput comme valeur de clé_primaire(USERNAME_COLUMN)
        Cursor cursor = databaseHelper.getAccountData(usernameInput);

        // Si cet identifiant n'existe pas ...
        if (cursor.getCount() == 0){
            Log.i(TAG,"Username entered doesn't exist");
            usernameInputLayout.setErrorEnabled(true);
            usernameInputLayout.setError(getString(R.string.username_doesnt_exist));
            usernameInputLayout.startAnimation(animationShake);
            return false;
        }

        // On vérifie le mot de passe
        cursor.moveToFirst();
        String password = cursor.getString(cursor.getColumnIndex(ACCOUNT_PASSWORD_COLUMN));

        if(!password.equals(passwordInput)) {
            Log.i(TAG,"Wrong password entered. It doesn't correspond with the username.");
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getString(R.string.wrong_password));
            passwordInputLayout.startAnimation(animationShake);
            return false;
        }

        cursor.close();
        return true;
    }


    // The user can't go back to the SplashScreen
    @Override
    public void onBackPressed() {}

}