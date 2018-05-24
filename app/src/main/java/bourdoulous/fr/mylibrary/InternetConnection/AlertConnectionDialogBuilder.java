package bourdoulous.fr.mylibrary.InternetConnection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import java.lang.ref.PhantomReference;

import bourdoulous.fr.mylibrary.R;

/**
 * Created by bourd on 19/03/2018.
 */

public class AlertConnectionDialogBuilder extends AlertDialog.Builder {

    private AppCompatActivity activity;

    public AlertConnectionDialogBuilder(AppCompatActivity activity) {
        super(activity);
        this.activity = activity;
        initAlertDialogBuilder();
    }

    private void initAlertDialogBuilder(){
        setIcon(R.drawable.offline);
        setTitle(R.string.internet_connection_error);
        setMessage(R.string.check_internet_connection);

        setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = activity.getIntent();
                activity.finish();
                activity.startActivity(intent);
            }
        });

        setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                activity.finish();
            }
        });
    }


}
