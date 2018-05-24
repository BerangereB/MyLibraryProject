package bourdoulous.fr.mylibrary.Library.AddBook;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.Library.AccountActivity;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.Library.ViewPagerAdapter;

/*
AddBookActivity va afficher un formulaire contenant 2pages
que l'utilisateur devra remplir pour ajouter un livre manuellement
à sa bibliothèque (certains champs seront obligatoires)
Une fois les informations validées on ajoute ce livre
à la base de données qui référence les livres et l'utilisateur
qui l'a ajouté.
 */

public class AddBookFormActivity extends AppCompatActivity {

    private AddBookInformationFragment infoFragment = new AddBookInformationFragment();
    private AddBookNotesFragment notesFragment = new AddBookNotesFragment();
    private ViewPager viewPager;
    private boolean update;
    private String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_form);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            update = true;
            title = bundle.getString("TITLE");
            toolbar.setTitle(R.string.update_personal_info);

        } else {
            update = false;
        }

        initializeVar();

    }

    private void initializeVar() {
        // Configuration du ViewPager
        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(infoFragment, "INFORMATION"); // Renseigner les informations propres au livre
        adapter.addFragment(notesFragment, "NOTES"); // Informations personnelles : commentaires, catégorie (lu, à lire), ...
        viewPager.setAdapter(adapter);

        // Tab Layout donnant l'accès à l'utilisateur aux différentes pages du ViewPager
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


    }


    public void valid(View view) {
        if(!infoFragment.checkDateFormat()){
            return;
        }

        if (!infoFragment.checkTitleNotEmpty()) {
            viewPager.setCurrentItem(0);
            infoFragment.checkTitleNotEmpty();
            return;
        }

        if (update) {
            new MyDatabaseHelper(this).deleteBook(AccountStateManager.getSingleton().getAccountConnected(), title);
        }


        new MyDatabaseHelper(this)
                .insertBook(AccountStateManager.getSingleton().getAccountConnected()
                        , infoFragment.getTitleInput()
                        , infoFragment.getAuthorInput()
                        , infoFragment.getPublisherInput()
                        , infoFragment.getCategoryInput()
                        , infoFragment.getPublishedDateInput()
                        , notesFragment.isRead()
                        , notesFragment.getDate()
                        , notesFragment.getNote());

                notesFragment.addEventInCalendar(infoFragment.getTitleInput());
    }

    public void smileyListener(View view) {
        notesFragment.smileyListener(view);
    }

    public void showDatePickerDialog(View view) {
        notesFragment.showDatePickerDialog();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.book_info_formular_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.cancel_action:
                startActivity(new Intent(this, AccountActivity.class));
                finish();
                break;
        }
        return true;
    }
}
