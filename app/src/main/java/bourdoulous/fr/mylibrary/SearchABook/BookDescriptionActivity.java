package bourdoulous.fr.mylibrary.SearchABook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.materialdrawer.Drawer;

import java.io.IOException;
import java.io.InputStream;

import bourdoulous.fr.mylibrary.Utilities.MyDrawerBuilder;
import bourdoulous.fr.mylibrary.Books.FavBook;
import bourdoulous.fr.mylibrary.R;

public class BookDescriptionActivity extends AppCompatActivity {

    private String TAG = "BookDescription";
    private TextView txtTitle;
    private TextView txtAuthor;
    private TextView txtDate;
    private TextView txtPublisher;
    private TextView txtDescription;
    private TextView txtCountPage;
    private TextView txtCategories;
    private TextView txtLink;
    private ImageView image;
    private Drawer drawer;
    private AddBookManager addBookManager;
    private FavBook book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_description);

        // findView
        initTxtViews();

        // affichage des données du livre sélectionné dans l'activité précédente (SearchResultActivity)
        setTxtViews();

        // ActionBar
        setSupportActionBar((Toolbar) findViewById(R.id.toolbarBook));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }

        // Drawer
        drawer = new MyDrawerBuilder(this, 2).getDrawerBuilder().build();
        drawer.openDrawer();
        drawer.closeDrawer();


        addBookManager = new AddBookManager(this,book);
    }

/************************** METHODS ON CREATE ACTIVITY ****************/

    private void initTxtViews() {
        txtTitle = findViewById(R.id.txtTitle);
        txtAuthor = findViewById(R.id.txtAuthor);
        txtDate = findViewById(R.id.txtPublishedDate);
        txtPublisher = findViewById(R.id.txtPublisher);
        txtDescription = findViewById(R.id.txtDescription);
        txtCountPage = findViewById(R.id.txtCountPage);
        txtCategories = findViewById(R.id.txtCategories);
        txtLink = findViewById(R.id.txtLink);
        image = findViewById(R.id.imageView);
    }

    private void setTxtViews() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle == null) {
            return;
        }
        Log.i(TAG,"Getting book information from Bundle");
        setViews(bundle);
    }

    /*************************************************
     Remplissage des champs en fonction des données
     transmises par l'activité précédente
     *************************************************/
    public void setViews(Bundle bundle) {

        String title = bundle.getString("TITLE");
        String author = bundle.getString("AUTHOR");
        String publisher = bundle.getString("PUBLISHER");
        String categories = bundle.getString("CATEGORIES");
        String publishedDate = bundle.getString("PUBLISHEDDATE");
        book = new FavBook(title,author,publisher,categories,publishedDate,null,0,0);

        String description = bundle.getString("DESCRIPTION");
        int pages = bundle.getInt("PAGES");


        // TITRE
        txtTitle.setText(title);

        // AUTEUR
        txtAuthor.setText(author != null ?
                getString(R.string.by) + " " + author :
                getString(R.string.unavailable_author));

        // DATE DE PUBLICATION
        String date = getString(R.string.published_in) + " " + (publishedDate == null ? "?" : publishedDate);
        txtDate.setText(date);

        // CATEGORIES
        txtCategories.setText(categories != null ?
                categories : "");

        // EDITEUR
        txtPublisher.setText(publisher);

        // RESUME
        if (description != null && !description.isEmpty()) {
            txtDescription.setText(description);
        }

        // NOMBRE DE PAGES
        if (pages != 0) {
            txtCountPage.setText(pages + "");
        }

        // LIEN
        txtLink.setText(bundle.getString("LINK"));

        // IMAGE
        InputStream inputStream;
        Bitmap bmp = null;
        try {
            inputStream = getContentResolver().openInputStream(Uri.parse(bundle.getString("URI")));
            bmp = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }
        image.setImageBitmap(bmp);
    }


/***************** MENU *************************/
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_book_description, menu);
        return true;
    }

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_add_book_in_favorites:
                Log.i(TAG,"AddBook menuItem selected");
                addBookManager.add_book();
                return true;
            default:
                drawer.openDrawer();
                return true;
        }
    }



    public void smileyListener(View v){
        addBookManager.smileyListener(v);
    }

    public void showDatePickerDialog(View v){
        addBookManager.showDatePickerDialog();
    }
}
