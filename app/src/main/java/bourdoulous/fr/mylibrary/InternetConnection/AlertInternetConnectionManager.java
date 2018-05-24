package bourdoulous.fr.mylibrary.InternetConnection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by bourd on 19/03/2018.
 */

public class AlertInternetConnectionManager {
    Context context;

    public AlertInternetConnectionManager(Context context){
        this.context = context;
    }

    public boolean isConnectedToInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }
    // initialize alertDialog properties

}
