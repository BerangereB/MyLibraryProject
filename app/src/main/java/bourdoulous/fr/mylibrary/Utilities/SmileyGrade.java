package bourdoulous.fr.mylibrary.Utilities;

import bourdoulous.fr.mylibrary.R;

public enum SmileyGrade
{
    LOVE(5, R.drawable.love),
    HAPPY(4, R.drawable.happy),
    NEUTRAL(3, R.drawable.neutral),
    CONFUSED(2, R.drawable.confused),
    VOMITED(1, R.drawable.vomited);

    int id, grade;

    SmileyGrade(int grade, int id){
        this.id = id;
        this.grade = grade;
    }


    public int getId() {
        return id;
    }

    public int getGrade(){
        return grade;
    }

    public static int getGradeWithId(int id){
        switch (id){
            case R.id.note_5:
                return 5;

            case R.id.note_4:
                return 4;

            case R.id.note_3:
                return 3;

            case R.id.note_2:
                return 2;

            case R.id.note_1:
                return 1;
        }
        return 0;
    }
    public static int getDrawableWithNote(int id){
        switch (id){
            case 5:
                return R.drawable.love;

            case 4:
                return R.drawable.happy;

            case 3:
                return R.drawable.neutral;

            case 2:
                return R.drawable.confused;

            case 1:
                return R.drawable.vomited;
        }
        return 0;
    }

    public static int getIdWithNote(int id){
        switch (id){
            case 5:
                return R.id.note_5;

            case 4:
                return R.id.note_4;

            case 3:
                return R.id.note_3;

            case 2:
                return R.id.note_2;

            case 1:
                return R.id.note_1;
        }
        return 0;
    }
}
