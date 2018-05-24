package bourdoulous.fr.mylibrary.SearchABook;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import bourdoulous.fr.mylibrary.Utilities.MyDrawerBuilder;
import bourdoulous.fr.mylibrary.InternetConnection.AlertConnectionDialogBuilder;
import bourdoulous.fr.mylibrary.InternetConnection.AlertInternetConnectionManager;
import bourdoulous.fr.mylibrary.R;

public class SearchActivity extends AppCompatActivity {

    private TextInputLayout titleInputLayout;
    private TextInputLayout authorInputLayout;
    //private final String TAG = "SearchActivity";
    private Research newResearch;
    private AlertInternetConnectionManager connectionManager;
    private AlertDialog alertDialog;
    private Drawer drawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        connectionManager = new AlertInternetConnectionManager(this);
        alertDialog = new AlertConnectionDialogBuilder(this).create();


        newResearch = new Research();
        titleInputLayout = findViewById(R.id.titleInputLayout);
        authorInputLayout = findViewById(R.id.authorInputLayout);
        Button doAResearchButton = findViewById(R.id.searchButton);


        doAResearchButton.setOnClickListener(onClickListener);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        drawer = new MyDrawerBuilder(this,2).getDrawerBuilder().build();
        drawer.openDrawer();
        drawer.closeDrawer();

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (connectionManager.isConnectedToInternet()){
                if ( checkRequiredInput() ) {
                    Intent intent = new Intent(SearchActivity.this, SearchResultActivity.class);
                    intent.putExtra("TITLE", newResearch.title);
                    intent.putExtra("AUTHOR", newResearch.author);
                    intent.putExtra("LANGUAGE", newResearch.language);
                    intent.putExtra("PAGE",1);
                    SearchActivity.this.startActivity(intent);
                }
            }else{
                alertDialog.show();
            }
        }
    };


    private boolean checkRequiredInput() {
        boolean isVerified = false;
        newResearch.title = titleInputLayout.getEditText().getText().toString();
        newResearch.author = authorInputLayout.getEditText().getText().toString();


        if(newResearch.isNull()){
            Toast.makeText(SearchActivity.this,"Empty fields !",Toast.LENGTH_SHORT).show();

        }else{
            isVerified = true;
        }

        return isVerified;
    }

    private class Research {
        String title = "";
        String author = "";
        String language = "";

        boolean isNull(){
            return (title.isEmpty() && author.isEmpty());
        }

    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.french_button:
                if (checked){
                    newResearch.language = "fr";
                }
                break;
            case R.id.english_button:
                if (checked) {
                    newResearch.language = "en";
                }
                break;
            case R.id.all_languages_button:
                if (checked) {
                    newResearch.language = "";
                }
                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        drawer.openDrawer();
        return true;
    }


    @Override
    public void onBackPressed() { }
}
