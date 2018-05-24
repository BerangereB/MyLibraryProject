package bourdoulous.fr.mylibrary.Accounts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.support.design.widget.TextInputLayout;
import android.widget.Toast;
import com.pkmmte.view.CircularImageView;
import com.soundcloud.android.crop.Crop;

import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.R;


public class SignUpActivity extends AppCompatActivity {
    private String TAG = "SignUp";

    private TextInputLayout usernameInputLayout;
    private TextInputLayout passwordInputLayout;
    private TextInputLayout checkPasswordInputLayout;
    private TextInputLayout firstnameInputLayout;
    private TextInputLayout answerInputLayout;

    private String usernameInput = "";
    private String passwordInput = "";
    private String passwordCheckInput = "";
    private String answerInput = "";
    private String firstnameInput = "";

    private Button signUpButton;
    private Animation animationShake;

    MyDatabaseHelper databaseAccountHelper = new MyDatabaseHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initViews();

        animationShake = AnimationUtils.loadAnimation(this,R.anim.shake);

        signUpButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.i(TAG,"Sign up button clicked");
                signUp();
            }
        });

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }

        initChangeAvatar();

    }

    private void initViews() {
        usernameInputLayout = findViewById(R.id.usernameLayout);
        passwordInputLayout = findViewById(R.id.passwordLayout);
        checkPasswordInputLayout = findViewById(R.id.checkPasswordLayout);
        firstnameInputLayout = findViewById(R.id.nameLayout);
        answerInputLayout = findViewById(R.id.answerLayout);
        findViewById(R.id.imageLayout).setVisibility(View.GONE);
        signUpButton = findViewById(R.id.buttonSignUp);

    }





    private void signUp() {
        getTextWithTextInputLayout();

        setErrorsEnableFalse();

        if(noEmptyFields() && checkValidUsername() && checkValidPassword()&& checkEqualPasswords()) {
            Log.i(TAG,"valid inputs -> insert new Account");

            String image_fileName = "";
            if(findViewById(R.id.imageLayout).getVisibility() == View.VISIBLE){
                Log.i(TAG,"Avatar has changed -> save it");
                image_fileName = imageUtils.saveImage();
            }
            databaseAccountHelper.insertAccount(usernameInput, passwordInput, answerInput, firstnameInput,image_fileName);
            Toast.makeText(this,R.string.creation_succeed,Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void setErrorsEnableFalse() {
        usernameInputLayout.setErrorEnabled(false);
        passwordInputLayout.setErrorEnabled(false);
        checkPasswordInputLayout.setErrorEnabled(false);
        answerInputLayout.setErrorEnabled(false);
        firstnameInputLayout.setErrorEnabled(false);
    }


    private void getTextWithTextInputLayout() {
        usernameInput = usernameInputLayout.getEditText().getText().toString();
        passwordInput = passwordInputLayout.getEditText().getText().toString();
        passwordCheckInput = checkPasswordInputLayout.getEditText().getText().toString();
        answerInput = answerInputLayout.getEditText().getText().toString();
        firstnameInput = firstnameInputLayout.getEditText().getText().toString();
    }


    private boolean checkEqualPasswords() {
        boolean areEqual = passwordInput.equals(passwordCheckInput);
        if(!areEqual) {
            Log.i(TAG,"Password entered not equals error");
            checkPasswordInputLayout.setErrorEnabled(true);
            checkPasswordInputLayout.setError(getString(R.string.different_password));
            checkPasswordInputLayout.startAnimation(animationShake);
        }
        return areEqual;
    }


    // Champs vides ?
    private boolean noEmptyFields() {
        boolean hasEmptyFields = false;
        if(usernameInput.isEmpty()){
            hasEmptyFields = true;
            usernameInputLayout.setErrorEnabled(true);
            usernameInputLayout.setError(getString(R.string.empty_fields));
            usernameInputLayout.startAnimation(animationShake);
        }

        if(passwordInput.isEmpty()){
            hasEmptyFields = true;
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getString(R.string.empty_fields));
            passwordInputLayout.startAnimation(animationShake);
        }
        if(passwordCheckInput.isEmpty()){
            hasEmptyFields = true;
            checkPasswordInputLayout.setErrorEnabled(true);
            checkPasswordInputLayout.setError(getString(R.string.empty_fields));
            checkPasswordInputLayout.startAnimation(animationShake);
        }

        if(answerInput.isEmpty()){
            hasEmptyFields = true;
            answerInputLayout.setErrorEnabled(true);
            answerInputLayout.setError(getString(R.string.empty_fields));
            answerInputLayout.startAnimation(animationShake);
        }

        return !hasEmptyFields;
    }


    // On vérifie que cet identifiant n'existe pas déjà
    private boolean checkValidUsername() {
        Cursor c = databaseAccountHelper.getAccountData(usernameInput);
        boolean isValid = c.getCount() == 0;
        if(!isValid){
            Log.i(TAG,"Username doesn't exist");
            usernameInputLayout.setErrorEnabled(true);
            usernameInputLayout.setError(getString(R.string.username_always_exists));
            usernameInputLayout.startAnimation(animationShake);
        }
        c.close();
        return isValid;
    }

    // Condition du format du mot de passe
    private boolean checkValidPassword() {
        boolean isValid = passwordInput.length()> 4;
        if(!isValid){
            passwordInputLayout.setErrorEnabled(true);
            passwordInputLayout.setError(getString(R.string.invalid_password_format));
            passwordInputLayout.startAnimation(animationShake);
        }
        return isValid;
    }



    /********* IMAGE UPDATE *******/

    private boolean setVisible = true;
    private AlertDialog dialog;
    private MyImageUtils imageUtils;

    public void displayImageLayout(View view) {
        if( setVisible && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }else {
            findViewById(R.id.imageLayout).setVisibility(setVisible ? View.VISIBLE : View.GONE);
            setVisible = !setVisible;
        }
    }

    public void changeAvatarImage(View view) {
        dialog.show();
    }

    private void initChangeAvatar() {

        String[] items = new String[]{getResources().getString(R.string.from_cam), getResources().getString(R.string.from_sd_card)};
        CircularImageView imageView = findViewById(R.id.avatar_image);
        imageUtils = new MyImageUtils(this, imageView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);

        dialog = new AlertDialog.Builder(this)
                .setTitle("Select Image")
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) { // CAMERA
                            imageUtils.fromCamera();
                        } else {
                            imageUtils.fromGallery();
                        }
                    }
                })
                .create();
    }

    private int imageRequestCode;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch(requestCode){

            case 1: // CAMERA
                imageRequestCode = requestCode;
                Crop.of(imageUtils.getImage_uri(), imageUtils.getImage_uri()).asSquare().start(this);
                break;
            case 2: // GALLERY
                imageRequestCode = requestCode;
                Crop.of(data.getData(), imageUtils.getImage_uri()).asSquare().start(this);
                break;
            default: // Résultat du CROP reçu : on affiche l'image
                imageUtils.displayImage(imageRequestCode);
        }
    }


    /********** MENU ***********/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }
}
