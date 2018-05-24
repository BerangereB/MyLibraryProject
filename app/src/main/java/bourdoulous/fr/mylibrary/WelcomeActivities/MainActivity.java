package bourdoulous.fr.mylibrary.WelcomeActivities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.materialdrawer.Drawer;

import java.io.File;
import java.util.Calendar;

import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.DataFetchers.MyIntentService;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.Utilities.MyDrawerBuilder;
import bourdoulous.fr.mylibrary.Utilities.StatsUtils;

public class MainActivity extends AppCompatActivity {

    private Drawer drawer;
    public static final String UPDATE = "bourdoulous.fr.mylibrary";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbarMain));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        drawer = new MyDrawerBuilder(this,1).getDrawerBuilder().build();
        drawer.openDrawer();
        drawer.closeDrawer();

        Button generatePDF = findViewById(R.id.pdfbutton);
        generatePDF.setOnClickListener(listenerNotif);

        // Statisques sur la bibliothèque de l'utilisateur
        printUserData();
    }


    private void printUserData() {
        StatsUtils utils = new StatsUtils(this);
        int nb_books = utils.getBooksLength();

        if (nb_books > 0) {

            setTextInCardView(0, 0, nb_books + "");
            setTextInCardView(0, 1, utils.getAuthorsWithMaxOcc().toString().replace("[", "").replace("]", ""));
            setTextInCardView(1, 0, utils.getYearMaxOcc());
            setTextInCardView(1, 1, utils.getMeanGrade());
        }else{
            //On retire les layout contenant les CardView dans le cas où l'utilisateur n'a pas de livres
            findViewById(R.id.line_1).setVisibility(View.GONE);
            findViewById(R.id.line_2).setVisibility(View.GONE);
        }
    }

    // On ne peut pas atteindre directement l'élément d'un CardView...
    private void setTextInCardView(int row, int column, String s) {
        LinearLayout view = findViewById(R.id.mainContent);
        LinearLayout linearLayout = (LinearLayout) view.getChildAt(row);
        CardView cardView = (CardView) linearLayout.getChildAt(column);
        LinearLayout linearLayoutInCardView = (LinearLayout) cardView.getChildAt(0);
        ((TextView) linearLayoutInCardView.getChildAt(1)).setText(s);
    }

    private Notification.Builder notificationBuilder;
    private NotificationManager manager;
    private Notification notification;
    private String file;

    View.OnClickListener listenerNotif = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            if(findViewById(R.id.line_1).getVisibility()==View.GONE){ //=> pas de livres
                Toast.makeText(MainActivity.this, R.string.no_books_no_pdf,Toast.LENGTH_LONG).show();
                return;
            }
            notificationBuilder = new Notification.Builder(MainActivity.this)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_pdf)
                    .setContentTitle("MyLibrary")
                    .setContentText(getString(R.string.pdf_conversion_loading))
                    .setProgress(100, 0, true);
            notification = notificationBuilder.build();

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if(manager != null){
                manager.notify(0, notification);
            }

            file = "mylibrary_" + AccountStateManager.getSingleton().getAccountConnected() + "_" + Calendar.getInstance().getTimeInMillis() + ".pdf";

            MyIntentService.start(MainActivity.this, null, file);
            IntentFilter intentFilter = new IntentFilter(UPDATE);

            LocalBroadcastManager
                    .getInstance(MainActivity.this)
                    .registerReceiver(myBroadcastReceiver, intentFilter);
        }
    };


    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Intent myIntent = new Intent(Intent.ACTION_VIEW);
            myIntent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/PDF/" + file)), "application/pdf");
            myIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

            notificationBuilder = new Notification.Builder(MainActivity.this)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.ic_pdf)
                    .setContentTitle("MyLibrary")
                    .setContentIntent(PendingIntent.getActivity(MainActivity.this,
                            0, myIntent, PendingIntent.FLAG_ONE_SHOT))
                    .setContentText(getString(R.string.pdf_download_end));

            notification = notificationBuilder.build();

            notification.flags |= Notification.FLAG_AUTO_CANCEL; // dès qu'on clique sur la notif, elle disparait de la liste des notif
            manager.notify(0, notification);
        }
    };


    /*
     *   MENU
     */

    //gère le click sur une action de l'ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        drawer.openDrawer();
        return true;
    }

    @Override
    public void onBackPressed() { }

}