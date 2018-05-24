package bourdoulous.fr.mylibrary.Library;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.support.v7.app.ActionBar;
import android.support.design.widget.TabLayout.Tab;

import com.mikepenz.materialdrawer.Drawer;

import java.util.List;

import bourdoulous.fr.mylibrary.Books.FavBook;
import bourdoulous.fr.mylibrary.Library.AddBook.AddBookFormActivity;
import bourdoulous.fr.mylibrary.Utilities.MyDrawerBuilder;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.DataBases.FromDatabaseToBook;

/*
Cette activité permet à l'utilisateur de gérer sa bibliothèque.
Il peut voir le contenu de sa bibliothèque divisé en deux parties
(LUS, A LIRE), ajouter manuellement un livre et ses références ou
rechercher parmi ses favoris pour consulter la description d'un livre
et modifier éventuellement les informations de ce dernier.
Un menu Drawer permet de retourner au menu principal, de rechercer un
livre (avec connexion internet), etc..
 */

public class AccountActivity extends AppCompatActivity {
    private String TAG = "ACCOUNT_ACTIVITY";
    private ReadFragment readFragment = new ReadFragment();
    private ToReadFragment toReadFragment = new ToReadFragment();
    private Drawer drawer;

    private int[] tabIcons = {
            R.drawable.favorite_folder,
            R.drawable.folder
    };


    /**
     * A la creation de l'activité il faut configurer le viewPager, la toolBar
     * et la liste des favoris de l'utilisateur
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate AccountActivity");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // Configuration de la toolbar

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
        }


        // Configuration du viewPager via un adapter contenant 2 fragments
        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(readFragment, getString(R.string.read)); // Fragment qui gère l'affichage des livres lus par l'utilisateur
        adapter.addFragment(toReadFragment, getString(R.string.to_read)); // Fragment qui gère l'affichage des livres que l'utilisateur voudrait lire
        viewPager.setAdapter(adapter);

        // TabLayout : barre d'accès aux éléments du viewPager
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        // Mise en place d'icons
        Tab read_tab = tabLayout.getTabAt(0), to_read_tab = tabLayout.getTabAt(1);

        if(read_tab != null && to_read_tab != null){
            read_tab.setIcon(tabIcons[0]);
            to_read_tab.setIcon(tabIcons[1]);
        }



        // Configuration du Drawer
        drawer = new MyDrawerBuilder(this, 3).getDrawerBuilder().build();
        drawer.openDrawer();
        drawer.closeDrawer();

    }


    /////////////// CONFIGURATION MENU ////////////////
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu AccountActivity");
        getMenuInflater().inflate(R.menu.menu_account, menu);

        // Ajout d'une barre de recherche pour retrouver plus facilement un
        // livre parmi les favoris. Les résultats s'actualise lorsque le texte entré
        // change
        MenuItem searchItem = menu.findItem(R.id.search_in_user_list);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                newText = newText.toLowerCase();
                Log.i(TAG, "Research : text entered " + newText);

                // si l'utilisateur efface le texte dans la barre de recherche alors
                // il faut réinitialiser les filtres sur les listes de livres lus et ceux à lire.
                if (newText.isEmpty()) {
                    Log.i(TAG, "Research : text empty");
                    readFragment.setFilter(
                            null);
                    toReadFragment.setFilter(null);
                    return true;
                }

                Log.i(TAG, "Research : apply filter");
                // Une fois la nouvelle liste à afficher créée on doit actualiser les RecyclerView dans les deux fragments
                readFragment.setFilter(newText);
                toReadFragment.setFilter(newText);

                return true;
            }

        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "Menu item clicked");
        int id = item.getItemId();
        switch (id) {
            // Item d'ajout manuel des références d'un livre
            case R.id.menu_item_add_book_manally:
                Log.i(TAG, "item selected : add book manually");
                Intent intent = new Intent(this, AddBookFormActivity.class);
                startActivity(intent);
                return true;
            default:
                Log.i(TAG, "OpenDrawer menuItem selected");
                drawer.openDrawer();
                return true;
        }
    }


    @Override
    public void onBackPressed() { Log.i(TAG, "onBackPressed AccountActivity");}

}
