package bourdoulous.fr.mylibrary.SearchABook;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import bourdoulous.fr.mylibrary.Books.FromJsonBook;
import bourdoulous.fr.mylibrary.DataFetchers.ImageFetcher;
import bourdoulous.fr.mylibrary.InternetConnection.AlertInternetConnectionManager;
import bourdoulous.fr.mylibrary.R;


public class BooksRecyclerViewAdapter extends RecyclerView.Adapter<BooksRecyclerViewAdapter.BookViewHolder> {

    private Context context;
    private List<FromJsonBook> books;

    BooksRecyclerViewAdapter(Context context, List<FromJsonBook> books) {
        this.context = context;
        this.books = books;
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView img;
        CardView cardView;

        BookViewHolder(View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.title);
            img = itemView.findViewById(R.id.icon);
            cardView = itemView.findViewById(R.id.cardView);

        }
    }

    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_book, parent, false);

        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final BookViewHolder holder, final int position) {
        FromJsonBook book = books.get(position);
        holder.tv_title.setText(book.getTitle());

        if (!book.getImageLink().equals("")) {
            if (new AlertInternetConnectionManager(context).isConnectedToInternet()) {
                // S'il y a internet
                String url = book.getImageLink();
                holder.img.setTag(url);
                new ImageFetcher().execute(holder.img);
            }
        }else{
            holder.img.setImageResource(R.drawable.image_not_found_icon);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(position, holder);
            }
        });

    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    private void onItemClick(int i, BookViewHolder holder) {
        Intent intent = new Intent(context, BookDescriptionActivity.class);
        FromJsonBook book = books.get(i);
        intent.putExtra("TITLE", book.getTitle());
        intent.putExtra("AUTHOR", book.getAuthor());
        intent.putExtra("PUBLISHEDDATE", book.getPublishedDate());
        intent.putExtra("PUBLISHER", book.getPublisher());
        intent.putExtra("DESCRIPTION", book.getDescription());
        intent.putExtra("PAGES", book.getPageCount());
        intent.putExtra("CATEGORIES", book.getCategory());
        intent.putExtra("LINK", book.getBuyLink());
        intent.putExtra("ICON", book.getImageLink());



        String name = "temp_img" + System.currentTimeMillis() + ".jpg";
        File temp_file = new File(Environment.getExternalStorageDirectory()+"/MyLibrary/saved_images", name);

        Bitmap bmp = ((BitmapDrawable) holder.img.getDrawable()).getBitmap();
        try {
            FileOutputStream out = new FileOutputStream(temp_file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri image_uri = Uri.fromFile(temp_file);
        intent.putExtra("URI", image_uri.toString());

        context.startActivity(intent);
    }
}


