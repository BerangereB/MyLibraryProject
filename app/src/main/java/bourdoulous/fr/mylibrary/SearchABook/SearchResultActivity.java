package bourdoulous.fr.mylibrary.SearchABook;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;
import java.util.Objects;

import bourdoulous.fr.mylibrary.Books.FromJsonBook;
import bourdoulous.fr.mylibrary.DataFetchers.JsonDataConverter;
import bourdoulous.fr.mylibrary.WelcomeActivities.MainActivity;
import bourdoulous.fr.mylibrary.DataFetchers.URLconverter;
import bourdoulous.fr.mylibrary.DataFetchers.MyIntentService;
import bourdoulous.fr.mylibrary.R;



public class SearchResultActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView pageNumberView;
    public static JSONObject jsonObject;
    private List<FromJsonBook> books;
    private boolean isFirstCall = true;
    public static final String UPDATE = "bourdoulous.fr.mylibrary";

    private String title = "";
    private String author = "";
    private String language = "";
    private int page = 1;
    private int max_page;
    private ImageView nextPageView;
    private ImageView previousPageView;
    private RecyclerView.Adapter adapter;

    private final String TAG = "SearchResultActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        recyclerView = findViewById(R.id.list);
        nextPageView = findViewById(R.id.next_page);
        previousPageView = findViewById(R.id.previous_page);
        pageNumberView = findViewById(R.id.pageNumber);
        recyclerView = findViewById(R.id.list);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            title = (String) bundle.get("TITLE");
            author = (String) bundle.get("AUTHOR");
            language = (String) bundle.get("LANGUAGE");
            page = (int) bundle.get("PAGE");


            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
            }else{
                updateJson();
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            }

        }

        nextPageView.setOnClickListener(pageListener);
        previousPageView.setOnClickListener(pageListener);


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.home);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                    updateJson();
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                }
        }
    }

    private void updateJson() {
        String strJson = URLconverter.getUrl(title, author, language, page);
        nextPageView.setClickable(false);
        previousPageView.setClickable(false);

        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        MyIntentService.start(this, strJson, null);
        IntentFilter intentFilter = new IntentFilter(UPDATE);
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(myBroadcastReceiver, intentFilter);

    }

    private View.OnClickListener pageListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean changePage = false;

            switch (view.getId()) {
                case R.id.next_page:

                    if (page < max_page) {
                        page += 1;
                        changePage = true;
                    }
                    break;
                case R.id.previous_page:
                    if (page > 1) {
                        page -= 1;
                        changePage = true;
                    }
            }
            if (changePage) {
                pageNumberView.setText("Page " + page + " / " + max_page);
                updateJson();
            }
        }

    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.action_change_research:
                onBackPressed();
                finish();
                break;
            default:
                // HOME
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }


    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("BROADCAST_RECEIVER", "Json downloaded");

            jsonObject = new MyIntentService().getJSONfromFile();
            if (jsonObject != null) {
                books = JsonDataConverter.JsonDataToBooks(jsonObject);
                if (isFirstCall) {
                    isFirstCall = false;
                    int totalResults = JsonDataConverter.getTotalItems();

                    if(totalResults ==0 || books == null){
                        Log.i(TAG,"no books found");
                        new AlertDialog.Builder(SearchResultActivity.this)
                                .setMessage(R.string.no_results_check_info)
                                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        onBackPressed();
                                    }
                                })
                                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        onBackPressed();
                                    }
                                }).show();

                        return;


                    }
                    max_page = totalResults / 40 + (totalResults % 40 == 0 ? 0 : 1);

                }

                if (books != null) {
                    adapter = new BooksRecyclerViewAdapter(SearchResultActivity.this, books);
                }
            }


            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            if (books != null) {
                recyclerView.setLayoutManager(new GridLayoutManager(SearchResultActivity.this, 3));
                recyclerView.setAdapter(adapter);
            }

            pageNumberView.setText("Page " + page + " / " + max_page);

            nextPageView.setClickable(true);
            previousPageView.setClickable(true);

        }

    };
}
