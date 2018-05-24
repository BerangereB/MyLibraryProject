package bourdoulous.fr.mylibrary.Library;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import bourdoulous.fr.mylibrary.R;

/*
    Ce fragment contient un recyclerView pour lister les livres favoris spécifiés "à lire" par
    l'utilisateur connecté.
 */

public class ToReadFragment extends AbstractFavBooksListFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_to_read, container, false);
        onCreateFragment(view,0);
        return view;
    }


}