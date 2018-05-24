package bourdoulous.fr.mylibrary.DataFetchers;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import bourdoulous.fr.mylibrary.Books.FromJsonBook;

/**
 * Created by bourd on 17/03/2018.
 */

public class JsonDataConverter {
    private static int totalItems;

    public static List<FromJsonBook> JsonDataToBooks(JSONObject jsonObject) {

        List<FromJsonBook> books = null;
        try {
            JSONArray items = jsonObject.getJSONArray("items");
            books = new ArrayList<>();

            totalItems = jsonObject.getInt("totalItems");

            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i); // iT is a book
                JSONObject volumeInfo = item.getJSONObject("volumeInfo");
                FromJsonBook book = new FromJsonBook();
                //TITRE
                if (volumeInfo.has("title")) {
                    book.setTitle(volumeInfo.getString("title"));
                } else {
                    continue;
                }
                //AUTEUR
                if (volumeInfo.has("authors")) {
                    book.setAuthor(jsonArrayToList(volumeInfo.getJSONArray("authors")));
                } else {
                    continue;
                }

                // EDITEUR
                if (volumeInfo.has("publisher")) {
                    book.setPublisher(volumeInfo.getString("publisher"));
                } else {
                    book.setPublisher("?");
                }

                //DATE
                if (volumeInfo.has("publishedDate")) {
                    String date = volumeInfo.getString("publishedDate");
                    if (date.contains("T")) {
                        int endIndex = date.indexOf("T");
                        book.setPublishedDate(date.substring(0, endIndex));
                    } else {
                        book.setPublishedDate(date);
                    }
                } else {
                    book.setPublishedDate("");
                }
                // DESCRIPTION
                if (volumeInfo.has("description")) {
                    book.setDescription(new String(volumeInfo.getString("description").getBytes("UTF-8"), "UTF-8"));
                }
                // NOMBRE DE PAGES
                if (volumeInfo.has("pageCount")) {
                    book.setPageCount(volumeInfo.getInt("pageCount"));
                }
                //CATEGORIES
                if (volumeInfo.has("categories")) {
                    book.setCategory(jsonArrayToList(volumeInfo.getJSONArray("categories")));
                }
                // LIEN IMAGE
                if (volumeInfo.has("imageLinks")) {
                    book.setImageLink(volumeInfo.getJSONObject("imageLinks").getString("thumbnail"));
                } else {
                    book.setImageLink("");
                }

                // LIEN
                JSONObject saleInfo = item.getJSONObject("saleInfo");
                if (saleInfo.has("buyLink")) {
                    book.setBuyLink(saleInfo.getString("buyLink"));
                } else {
                    book.setBuyLink(volumeInfo.getString("infoLink"));
                }

                books.add(book);
            }

        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        return books;
    }


    private static String jsonArrayToList(JSONArray jsonArray) {
        if (jsonArray.length() == 0) {
            return "";
        }
        StringBuilder list = new StringBuilder();
        try {
            list.append(String.valueOf(jsonArray.get(0)));
            for (int i = 1; i < jsonArray.length(); i++) {

                list.append(", ").append(String.valueOf(jsonArray.get(i)));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list.toString();
    }

    public static int getTotalItems() {
        return totalItems;
    }
}
