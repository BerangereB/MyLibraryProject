package bourdoulous.fr.mylibrary.DataFetchers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bourdoulous.fr.mylibrary.R;

/**
 * Created by bourd on 18/03/2018.
 */

public class ImageFetcher extends AsyncTask<ImageView, Void, Bitmap> {

        private ImageView imageView;
        //private String TAG = "imageFetcher";

        @Override
        protected Bitmap doInBackground(ImageView... imageViews) {

            this.imageView = imageViews[0];
            return download_Image((String)imageView.getTag());
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            // On affiche l'image
            if(bitmap != null){
                imageView.setImageBitmap(bitmap);
            }else{
                imageView.setImageResource(R.drawable.image_not_found_icon);
            }
        }

        private Bitmap download_Image(String url) {
            if(url.isEmpty()) return null;

            Bitmap bmp;
            try{
                URL ulrn = new URL(url);
                HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
                InputStream is = con.getInputStream();
                bmp = BitmapFactory.decodeStream(is);
                if (null != bmp)
                    return bmp;

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


    }
