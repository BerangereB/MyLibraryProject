package bourdoulous.fr.mylibrary.DataFetchers;


import android.util.Log;


public class URLconverter {


    private static String encodeSpaces(String str)
    {
        return str.replaceAll("\\s+", "%20");
    }

    private static String replaceSpaces(String str)
    {
        return str.replaceAll("\\s+", "+");
    }


    public static String getUrl(String title, String authorSearch, String language, int pageNumber) {
        if (!authorSearch.isEmpty()) {
            authorSearch = "+inauthor:" + replaceSpaces(authorSearch);
        }

        if (!language.isEmpty()) {
            language = "&langRestrict=" + language;
        }
        if (!title.isEmpty()) {
            title = "+intitle:%22" + encodeSpaces(title) + "%22";
        }

        String url = "https://www.googleapis.com/books/v1/volumes?q=" + title + authorSearch + "&orderBy=newest&startIndex=" + getStartResults(pageNumber) + "&maxResults=40" + language;

        Log.i("JSON_CONVERTER",url);
        return url;
    }

    private static int getStartResults(int pageNumber){
        // 1 : 0
        // 2 : 40
        // ...
        return (pageNumber -1) * 40;
    }
}



