package bourdoulous.fr.mylibrary.Utilities;

import android.content.Context;
import android.util.SparseIntArray;

import java.util.List;

import bourdoulous.fr.mylibrary.DataBases.FromDatabaseToBook;
import bourdoulous.fr.mylibrary.Books.FavBook;

public class StatsUtils {

    private List<FavBook> books;
    private int[] grades = null;
    private Context context;

    private float meanGrade = -1f;
    private int maxGrade = -1;
    private int minGrade = -1;
    private int maxYear = 0;

    private List<String> authorsMaxOcc = null;
    private SparseIntArray yearsOccMap = null;

    public StatsUtils(Context context){
        this.context = context;
        books = FromDatabaseToBook.convert(context);
    }

    public int getBooksLength(){
        return books.size();
    }



    public List<String> getAuthorsWithMaxOcc(){
        if(authorsMaxOcc == null){
            authorsMaxOcc = FromDatabaseToBook.getAuthorsWithOcc(context);
        }
        return authorsMaxOcc;
    }

    private void statsYear(){
        yearsOccMap = new SparseIntArray();
        int maxOcc = 0;

        for(FavBook book : books){
            if(book.getDate() != null) {
                int year = Integer.parseInt(book.getDate().substring(6));
                if (yearsOccMap.get(year, -1) != -1) {
                    yearsOccMap.put(year, yearsOccMap.get(year) + 1);
                } else {
                    yearsOccMap.put(year, 1);
                }
                if (yearsOccMap.get(year) > maxOcc) {
                    maxOcc = yearsOccMap.get(year);
                    maxYear = year;
                }
            }
        }

    }

    public String getYearMaxOcc(){
        if(yearsOccMap == null){
            statsYear();
        }
        return maxYear != 0 ? Integer.toString(maxYear) : "XXXX";
    }





    ////// GRADES /////////

    private int[] getGrades(){
        grades = FromDatabaseToBook.getGrades(context);
        return grades;
    }

    private void statsGrade(){
        if(grades == null){
            grades = getGrades();
        }
        maxGrade = 0;
        minGrade = 5;
        int S = 0;
        for(int i : grades){
            S += i;
            if(maxGrade < i){
                maxGrade = i;
            }
            if(minGrade > i){
                minGrade = i;
            }
        }
        meanGrade = (float)S / (float) grades.length;
    }


    public String[] getMaxGrade(){
        if(maxGrade == -1){
            statsGrade();
        }
        return FromDatabaseToBook.getBooksByGrades(context,maxGrade);
    }

    public String[] getMinGrade(){
        if(minGrade == -1){
            statsGrade();
        }
        return FromDatabaseToBook.getBooksByGrades(context,minGrade);
    }

    public String getMeanGrade(){
        if(meanGrade == -1){
            statsGrade();
        }
        float ret = 0;
        if (meanGrade != 0) {
            ret = (float) ((int) (meanGrade * 100)) / (float) 100;
        }
        return ret == 0 ? "?" : Float.toString(ret);
    }




}
