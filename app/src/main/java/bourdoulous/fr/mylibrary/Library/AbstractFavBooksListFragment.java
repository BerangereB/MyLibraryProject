package bourdoulous.fr.mylibrary.Library;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

import bourdoulous.fr.mylibrary.Books.FavBook;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.DataBases.FromDatabaseToBook;

/*
    Cette classe abstraite définit la façon d'implémenter les fragments ReadFragments et
    ToReadFragments utilisés dans l'activité AccountActivity pour l'affichage des livres
    favoris dans l'un où l'autre des fragments suivant s'ils ont été lus ou s'ils sont à lire.
 */
public abstract class AbstractFavBooksListFragment extends Fragment {

    protected FavBooksAdapter adapter;
    protected List<FavBook> favBookList;

    /**
     * Cette fonction doit être appelée dans le onCreateView du fragment considéré (ReadFragment ou ToReadFragment)
     * elle permet d'initialiser notamment le recyclerView permettant d'afficher la liste de favoris
     *
     * @param view -> fragment
     * @param read -> précise la nature du fragment : ReadFragment (read = 1) ou ToReadFragment (read = 0)
     */
    protected void onCreateFragment(View view, int read) {

        // Création du recyclerView en utilisant le layout favBooksRecyclerView pour chaque cellule
        RecyclerView myRecyclerView = view.findViewById(R.id.favBooksRecyclerView);
        myRecyclerView.setHasFixedSize(false);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        myRecyclerView.setLayoutManager(manager);

        // A l'aide d'une requête SQL on récupère les livres favoris de l'utilisateur connecté
        // en fonction du type de fragment (read = 0/1)
        favBookList = FromDatabaseToBook.convert(getContext(), read);

        adapter = new FavBooksAdapter(favBookList, getActivity(), this);
        myRecyclerView.setAdapter(adapter);

    }


    public void setFilter(String newText){
        List<FavBook> books = new ArrayList<>(); // va contenir la nouvelle liste à afficher

        if(newText == null){
            adapter.setFilter(favBookList);
            return;
        }
        for (FavBook book : favBookList) {

            String title = book.getTitle().toLowerCase();
            // on compare le texte entré avec les titres des livres et ajout si correspondance
            if (title.contains(newText)) {
                books.add(book);
                continue;
            }

            // comparaison du texte avec les auteurs de chaque livre et ajout si correspondance
            String author = book.getAuthor().toLowerCase();
            if (author.contains(newText)) {
                books.add(book);
            }
        }
        adapter.setFilter(books);
    }

}
