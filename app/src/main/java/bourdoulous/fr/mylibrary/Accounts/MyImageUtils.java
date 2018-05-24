package bourdoulous.fr.mylibrary.Accounts;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;

import static android.content.ContentValues.TAG;
import static bourdoulous.fr.mylibrary.DataBases.AccountHelper.ACCOUNT_AVATAR_COLUMN;

/**
 * Cet utilitaire permet de gérer l'affichage, la modification
 * ,le redimensionnement et la sauvegarde de l'avatar de
 * l'utilisateur courant.
 */
public class MyImageUtils {

    private Activity activity;
    private ImageView imageView;
    private Bitmap originalBitmap;
    private File temp_file;
    private Uri image_uri;
    private File myDir;


    /****** CONSTRUCTOR ***********/
    public MyImageUtils(Activity activity, ImageView imageView) {

        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/MyLibrary/saved_images");
        myDir.mkdirs();

        if(activity != null ){
            this.activity = activity;
            originalBitmap = getCurrentAvatar();
        }

        if(imageView != null){
            this.imageView = imageView;
        }

        String name = "avatar_logo_temp" + System.currentTimeMillis() + ".jpg";
        temp_file = new File(Environment.getExternalStorageDirectory()+"/MyLibrary/saved_images", name);
        image_uri = Uri.fromFile(temp_file);

    }


    /************ MODIFICATION DE L'AVATAR **************/


    /// Changer l'avatar en prenant une photo
    public void fromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        image_uri = Uri.fromFile(temp_file);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        intent.putExtra("return data", true);

        activity.startActivityForResult(intent, 1);
    }

    /// choisir le nouvel avatar à partir des images du téléphone
    public void fromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
    }



    /* Après récupération du "fichier" contenant le nouvel avatar (URI)
    * on crée l'objet Bitmap associé et on l'affiche à l'emplacement
    * choisi (imageView)
    */
    public void displayImage(int requestCode) {

        InputStream inputStream;
        Bitmap bmp = null;
        try {
            inputStream = activity.getContentResolver().openInputStream(image_uri);
            bmp = BitmapFactory.decodeStream(inputStream);
            if (inputStream != null) {
                inputStream.close();
            }

        } catch (IOException e) {
            Log.e(TAG,e.getMessage());
        }

        originalBitmap = bmp;
        if(requestCode == 1){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            originalBitmap = Bitmap.createBitmap(originalBitmap , 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        }
        imageView.setImageBitmap(originalBitmap);
    }


    /**
     * Sauvegarde du nouvel avatar et suppression de l'avatar précédent
     */
    public String saveImage() {
        // on supprime le fichier temporaire
        if(temp_file != null){
            temp_file.delete();
        }
        // on supprime l'ancien avatar
        String name = getFileName();
        if(name != null){
            new File(Environment.getExternalStorageDirectory().toString() + "/MyLibrary/saved_images/" + name).delete();
        }


        String file_name = "avatar_logo_" + System.currentTimeMillis() + ".jpg";

        File file = new File (myDir, file_name);

        try {
            FileOutputStream out = new FileOutputStream(file);
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file_name;
    }

    /*
        Cette fonction permet de récupérer l'avatar
        du login de l'utilisateur couramment connecté à partir
     */
    private Bitmap getCurrentAvatar(){
        Bitmap image = null;
        String file_name = getFileName();
        if(file_name != null) {
            File file = new File(myDir, file_name);
            if (file.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(file);
                    image = BitmapFactory.decodeStream(fis);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        return image;
    }

    /*
        Permet de récupérer le nom du fichier contenant
        l'avatar de l'utilisateur courant
     */
    private String getFileName(){
        String file_name;
        String connected = AccountStateManager.getSingleton().getAccountConnected();
        Cursor cursor = (new MyDatabaseHelper(activity)).getAccountData(connected);
        if(cursor.getCount() != 0) {
            cursor.moveToFirst();
            file_name = cursor.getString(cursor.getColumnIndex(ACCOUNT_AVATAR_COLUMN));
            cursor.close();
            return file_name;
        }
        cursor.close();
        return null;
    }


    // getters
    public Bitmap getOriginalBitmap() {
        return originalBitmap;
    }

    public Uri getImage_uri() {
        return image_uri;
    }
}
