package bourdoulous.fr.mylibrary.Accounts;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TextInputLayout;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.pkmmte.view.CircularImageView;
import com.soundcloud.android.crop.Crop;

import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.WelcomeActivities.MainActivity;
import bourdoulous.fr.mylibrary.R;

public class UpdateAccountActivity extends AppCompatActivity {

    private String TAG = "SignUp";
    private TextInputLayout newPasswordLayout;
    private TextInputLayout newPasswordConfirmLayout;
    private boolean changePasswordPaneIsVisible = false;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_account);
        //findViewById + setOnClickListener
        initViews();
        //init Dialog change image
        initChangeAvatar();

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back);
        }
    }

    private void initViews() {
        newPasswordLayout = findViewById(R.id.newPassword);
        newPasswordConfirmLayout = findViewById(R.id.newPasswordConfirm);
        newPasswordConfirmLayout.setVisibility(View.GONE);
        newPasswordLayout.setVisibility(View.GONE);

        findViewById(R.id.imageLayout).setVisibility(View.GONE);
    }



    public void changePasswordLayoutVisibility(View view) {
        if (!changePasswordPaneIsVisible) { //On déploie les deux champs d'entrée pour changer le mot de passe
            newPasswordConfirmLayout.setVisibility(View.VISIBLE);
            newPasswordLayout.setVisibility(View.VISIBLE);
        } else { // on cache les deux champs d'entrée
            newPasswordConfirmLayout.setVisibility(View.GONE);
            newPasswordLayout.setVisibility(View.GONE);
        }
        changePasswordPaneIsVisible = !changePasswordPaneIsVisible;
    }



    public void update(View view) {
        Log.i(TAG,"Update button clicked");
        String username = AccountStateManager.getSingleton().getAccountConnected();

        String answer = ((EditText) findViewById(R.id.answerUpdate)).getText().toString();
        String firstname = ((EditText) findViewById(R.id.firstnameUpdate)).getText().toString();
        String newPassword = "";
        if(newPasswordLayout.getEditText() != null){
             newPassword = newPasswordLayout.getEditText().getText().toString();
        }

        newPasswordConfirmLayout.setErrorEnabled(false);
        newPasswordLayout.setErrorEnabled(false);

        boolean changePassword = false;

        if (changePasswordPaneIsVisible && !newPassword.isEmpty()) {
            if (!checkNewPassword(newPassword)) return;
            changePassword = true;
        }

        MyDatabaseHelper databaseAccountHelper = new MyDatabaseHelper(UpdateAccountActivity.this);

        if (!firstname.isEmpty() || !answer.isEmpty() || (changePasswordPaneIsVisible && !newPassword.isEmpty()) || findViewById(R.id.imageLayout).getVisibility() == View.VISIBLE) { // no empty fields
            if(!changePassword) {
                newPassword = "";
            }

            String image_fileName = "";
            if(findViewById(R.id.imageLayout).getVisibility() == View.VISIBLE){
               image_fileName = imageUtils.saveImage();
            }
            databaseAccountHelper.updateAccount(username, newPassword, answer, firstname, image_fileName);

            Log.i(TAG,"Account updated");
            startActivity(new Intent(UpdateAccountActivity.this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(UpdateAccountActivity.this, getResources().getString(R.string.empty_fields), Toast.LENGTH_SHORT).show();
        }
    }


    private boolean checkNewPassword(String newPassword) {
        String newPasswordConfirm = newPasswordConfirmLayout.getEditText().getText().toString();

        if (newPassword.length() > 4) {
            if (newPassword.equals(newPasswordConfirm)) {
                return true;
            } else {
                Log.i(TAG,"Password too short");
                newPasswordConfirmLayout.setErrorEnabled(true);
                newPasswordConfirmLayout.setError(getString(R.string.different_password));
                return false;
            }
        }
        newPasswordLayout.setErrorEnabled(true);
        newPasswordLayout.setError(getString(R.string.invalid_password_format));
        return false;
    }


    /********* IMAGE UPDATE *******/

    private boolean setVisible = true;

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

    private MyImageUtils imageUtils;
    private int imageRequestCode;

    private void initChangeAvatar() {

        String[] items = new String[]{getResources().getString(R.string.from_cam), getResources().getString(R.string.from_sd_card)};
        CircularImageView imageView = findViewById(R.id.avatar_image);
        imageUtils = new MyImageUtils(this, imageView);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, items);

        dialog = new AlertDialog.Builder(this)
                .setTitle("Source")
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) return;

        switch(requestCode){

            case 1:
                imageRequestCode = requestCode;
                Crop.of(imageUtils.getImage_uri(), imageUtils.getImage_uri()).asSquare().start(this);
                break;
            case 2:
                imageRequestCode = requestCode;
                Crop.of(data.getData(), imageUtils.getImage_uri()).asSquare().start(this);
                break;
            default:
                imageUtils.displayImage(imageRequestCode);
        }
    }


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
