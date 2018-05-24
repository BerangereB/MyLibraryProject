package bourdoulous.fr.mylibrary.DataFetchers;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.DataBases.FromDatabaseToBook;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.Utilities.PDFtemplate;
import bourdoulous.fr.mylibrary.Utilities.StatsUtils;
import bourdoulous.fr.mylibrary.WelcomeActivities.MainActivity;
import bourdoulous.fr.mylibrary.SearchABook.SearchResultActivity;
import bourdoulous.fr.mylibrary.Books.FavBook;


public class MyIntentService extends IntentService {
    private static final String ACTION_GET_BOOKS = "bourdoulous.fr.mylibrary.action.GET_BOOKS";
    private static final String ACTION_PDF = "bourdoulous.fr.mylibrary.action.PDF";
    private static final String EXTRA_URL = "bourdoulous.fr.mylibrary.extra.URL";
    private static final String EXTRA_PDF = "bourdoulous.fr.mylibrary.extra.PDF";
    public static Context context;

    public MyIntentService() {
        super("MyIntentService");
    }

    public static void start(Context c, String url,String file) {
        context = c;
        Intent intent = new Intent(context, MyIntentService.class);
        if(url != null){
            intent.setAction(ACTION_GET_BOOKS);
            intent.putExtra(EXTRA_URL, url);
        }else{
            intent.setAction(ACTION_PDF);
            intent.putExtra(EXTRA_PDF, file);
        }
        context.startService(intent);

    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_GET_BOOKS.equals(action)) {
                final String url = intent.getStringExtra(EXTRA_URL);
                handleActionJSON(url);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MainActivity.UPDATE));
            }else{
                final String file = intent.getStringExtra(EXTRA_PDF);
                handleActionPDF(file);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SearchResultActivity.UPDATE));
            }
        }


    }

        private void handleActionJSON (String strUrl){
            try {
                final URL URL = new URL(strUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) URL.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.connect();

                if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode()) {
                    File file = new File(Environment.getExternalStorageDirectory() + "/MyLibrary/temp_download.json");
                    readLines(httpURLConnection.getInputStream(), file);

                }

            } catch (IOException e) {
                Log.e("MY_INTENT_SERVICE",e.getMessage());
            }


        }

        private void readLines (InputStream inputStream, File file){
            OutputStream out ;
            try {
                out = new FileOutputStream(file);
                byte[] buff = new byte[1024];
                int len;
                while ((len = inputStream.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
                out.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    public JSONObject getJSONfromFile (){
        InputStream in;
        try {
            in = new FileInputStream(Environment.getExternalStorageDirectory() + "/MyLibrary/temp_download.json");
            byte[] buff = new byte[in.available()];
            in.read(buff);
            in.close();
            return new JSONObject(new String(buff,"UTF-8"));
        } catch (IOException | JSONException e) {
            Log.e("MY_INTENT_SERVICE",e.getMessage());
        }
        return null;

    }





    private void handleActionPDF (String fileName){

        PDFtemplate template = new PDFtemplate();
        template.openDocument(fileName);


        String[] headers_read = new String[]{context.getResources().getString(R.string.title)
                , context.getResources().getString(R.string.author)
                , context.getResources().getString(R.string.publisher)
                , context.getResources().getString(R.string.category)
                , context.getResources().getString(R.string.published_date)
                , context.getResources().getString(R.string.reading_date)
                , context.getResources().getString(R.string.grade_given)};

        String[] headers_not_read = new String[]{context.getResources().getString(R.string.title)
                , context.getResources().getString(R.string.author)
                , context.getResources().getString(R.string.publisher)
                , context.getResources().getString(R.string.category)
                , context.getResources().getString(R.string.published_date)};
        ArrayList<String[]> readBooks = getBooksInfo(true);
        ArrayList<String[]> notReadBooks = getBooksInfo(false);

        template.addDocumentData("TITLE", "SUBJECT", "AUTHOR");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        template.addTitles(getString(R.string.recap_books) + AccountStateManager.getSingleton().getAccountConnected(), getString(R.string.did_in_date) + sdf.format(Calendar.getInstance().getTimeInMillis()));


        template.addPart(getString(R.string.one_books));

        // BOOKS READ
        template.addParagraph(getString(R.string.total_read_books) + readBooks.size());
        if(!readBooks.isEmpty()) {
            template.createTable(headers_read, readBooks);
        }
        template.setSpacingAfter(40);

        // BOOKS NOT READ
        template.addParagraph(getString(R.string.total_to_read_books) + notReadBooks.size());
        if(!notReadBooks.isEmpty()){
            template.createTable(headers_not_read, notReadBooks);
        }
        template.setSpacingAfter(50);

        template.addPart(getString(R.string.two_some_stats));
        StatsUtils statsUtils = new StatsUtils(context);
        template.addStats(getString(R.string.recurrent_author),statsUtils.getAuthorsWithMaxOcc().toString());
        template.addStats(context.getResources().getString(R.string.best_year),statsUtils.getYearMaxOcc()+"");
        template.addStats(getString(R.string.max_grade_book),Arrays.toString(statsUtils.getMaxGrade()));
        template.addStats(getString(R.string.min_grade_books),Arrays.toString(statsUtils.getMinGrade()));
        template.addStats(context.getResources().getString(R.string.mean_grade),statsUtils.getMeanGrade() +"");

        template.closeDocument();


    }

    public ArrayList<String[]> getBooksInfo(boolean read) {
        ArrayList<String[]> booksInfo = new ArrayList<>();

        if (read) {
            List<FavBook> readBooks = FromDatabaseToBook.convert(context, 1);


            for (FavBook book : readBooks) {
                String[] infos = new String[]{book.getTitle()
                        , book.getAuthor().isEmpty()?"?":book.getAuthor()
                        , book.getPublisher().isEmpty()?"?":book.getPublisher()
                        , book.getCategory().isEmpty()?"?":book.getCategory()
                        , book.getPublishedDate().isEmpty()?"?":book.getPublishedDate()
                        , ( book.getDate() == null || book.getDate().isEmpty() )?"?":book.getDate()
                        , book.getNote() == 0 ? "?" : String.valueOf(book.getNote())};
                booksInfo.add(infos);
            }

        } else {
            List<FavBook> notReadBooks = FromDatabaseToBook.convert(context, 0);
            for (FavBook book : notReadBooks) {
                String[] infos = new String[]{book.getTitle()
                        , book.getAuthor().isEmpty()?"?":book.getAuthor()
                        , book.getPublisher().isEmpty()?"?":book.getPublisher()
                        , book.getCategory().isEmpty()?"?":book.getCategory()
                        , book.getPublishedDate().isEmpty()?"?":book.getPublishedDate()};
                booksInfo.add(infos);

            }
        }


        return booksInfo;
    }



}
