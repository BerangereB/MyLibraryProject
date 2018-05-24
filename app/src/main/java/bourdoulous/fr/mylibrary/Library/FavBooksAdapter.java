package bourdoulous.fr.mylibrary.Library;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

import bourdoulous.fr.mylibrary.Books.FavBook;
import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.Library.AddBook.AddBookFormActivity;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.DataBases.FromDatabaseToBook;

import static bourdoulous.fr.mylibrary.Utilities.SmileyGrade.getDrawableWithNote;

public class FavBooksAdapter extends RecyclerView.Adapter<FavBooksAdapter.ViewHolder> {

    private List<FavBook> books;
    private Activity context;
    private Fragment fragment;

    public FavBooksAdapter(List<FavBook> books, Activity context, Fragment fragment) {
        this.fragment = fragment;
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LinearLayout view = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.books_list_read, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.title.setText(books.get(position).getTitle());
        holder.author.setText(books.get(position).getAuthor());
        holder.note.setImageResource(getDrawableWithNote(books.get(position).getNote()));

        holder.itemOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("FavBooksAdapter","item menu clicked");
                final PopupMenu menu = new PopupMenu(context,holder.itemOptions);
                menu.inflate(R.menu.books_item_options);

                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()){
                            case R.id.details:
                                FavBook book_selected = FromDatabaseToBook.convert(context, holder.title.getText().toString());

                                Dialog dialog = new Dialog(context);
                                dialog.setContentView(R.layout.fav_book_description_layout);
                                TextView title = dialog.findViewById(R.id.title);
                                TextView author = dialog.findViewById(R.id.author);
                                TextView publisher = dialog.findViewById(R.id.publisher);
                                TextView category = dialog.findViewById(R.id.category);
                                TextView publishedDate = dialog.findViewById(R.id.publishedDate);
                                TextView readDate = dialog.findViewById(R.id.readDate);

                                title.setText(book_selected.getTitle());
                                author.setText(author.getText() + " " + book_selected.getAuthor());
                                publisher.setText(publisher.getText() + " " + book_selected.getPublisher());
                                category.setText(category.getText() + " " + book_selected.getCategory());
                                publishedDate.setText(publishedDate.getText() + " " + book_selected.getPublishedDate());

                                String read = "A lire";
                                if(book_selected.getRead() == 1){
                                    read = "Lu";
                                }
                                ((TextView) dialog.findViewById(R.id.readOrNot)).setText(read);

                                int grade =  book_selected.getNote();
                                if( grade != 0){
                                    ((TextView) dialog.findViewById(R.id.grade)).setText(context.getString(R.string.grade_given) + grade);
                                }else{
                                    dialog.findViewById(R.id.grade).setVisibility(View.GONE);
                                }

                                readDate.setText(readDate.getText() + " " + book_selected.getDate());

                                dialog.show();

                                break;

                            case R.id.modify:
                                Intent intent = new Intent(context, AddBookFormActivity.class);
                                FavBook book = FromDatabaseToBook.convert(context,holder.title.getText().toString());
                                intent.putExtra("TITLE", book.getTitle());
                                intent.putExtra("AUTHOR", book.getAuthor());
                                intent.putExtra("PUBLISHER", book.getPublisher());
                                intent.putExtra("CATEGORY", book.getCategory());
                                intent.putExtra("PUBLISHEDDATE", book.getPublishedDate());
                                intent.putExtra("DATE",book.getDate());
                                intent.putExtra("READ", book.getRead());
                                intent.putExtra("GRADE", book.getNote());

                                context.startActivity(intent);
                                break;

                            case R.id.delete:
                                new MyDatabaseHelper(context)
                                        .deleteBook(AccountStateManager.getSingleton().getAccountConnected(),holder.title.getText().toString());
                                delete(holder.title.getText().toString());

                                //refresh fragment
                                FragmentTransaction ft = fragment.getFragmentManager().beginTransaction();
                                ft.detach(fragment).attach(fragment).commit();
                        }



                        return false;

                    }
                });
                menu.show();
            }
        });


    }

    private void delete(String title){
        for(FavBook book : books){
            if(title.equals(book.getTitle())){
                books.remove(book);
                return;
            }
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView title;
        TextView author;
        ImageView note;
        TextView itemOptions;

        ViewHolder(View view) {
            super(view);

            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            note = view.findViewById(R.id.note);
            itemOptions = view.findViewById(R.id.booksItemOptions);

        }
    }

    public void setFilter(List<FavBook> books){
        this.books = new ArrayList<>();
        this.books.addAll(books);
        notifyDataSetChanged();
    }

}





