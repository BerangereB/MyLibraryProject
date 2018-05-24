package bourdoulous.fr.mylibrary.SearchABook;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import bourdoulous.fr.mylibrary.DataBases.MyDatabaseHelper;
import bourdoulous.fr.mylibrary.Books.FavBook;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.Accounts.AccountStateManager;
import bourdoulous.fr.mylibrary.Utilities.DatePickerFragment;

import static bourdoulous.fr.mylibrary.Utilities.SmileyGrade.getGradeWithId;
import static bourdoulous.fr.mylibrary.Utilities.SmileyGrade.getIdWithNote;


public class AddBookManager {

    private AlertDialog dialog;
    private boolean read = false;
    private int note = 0;
    private AppCompatActivity activity;
    private FavBook book;

    AddBookManager(AppCompatActivity a, FavBook b){
        activity = a;
        book = b;
    }

    public void add_book(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity)
                .setView(R.layout.input_dialog_read_grade)
                .setNegativeButton("Annuler", buttonListener)
                .setPositiveButton("Ajouter", buttonListener);

        dialog = dialogBuilder.create();
        dialog.show();
        dateTextView = dialog.findViewById(R.id.date);

        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(Color.BLACK);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(Color.BLACK);

        final LinearLayout gradeLayout = dialog.findViewById(R.id.smileyLayout);
        final TextView titleGradeLayout = dialog.findViewById(R.id.titleSmileyLayout);
        if(titleGradeLayout != null && gradeLayout != null) {
            titleGradeLayout.setVisibility(View.GONE);
            gradeLayout.setVisibility(View.GONE);
        }


        Switch aSwitch = dialog.findViewById(R.id.switch_);

        Objects.requireNonNull(aSwitch).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                read = isChecked;
                if (isChecked && titleGradeLayout != null && gradeLayout != null) {
                    titleGradeLayout.setVisibility(View.VISIBLE);
                    gradeLayout.setVisibility(View.VISIBLE);
                } else if (titleGradeLayout != null && gradeLayout != null) {
                    titleGradeLayout.setVisibility(View.GONE);
                    gradeLayout.setVisibility(View.GONE);
                    note = 0;
                }
            }
        });

    }

    private DialogInterface.OnClickListener buttonListener = new DialogInterface.OnClickListener() {

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case DialogInterface.BUTTON_NEGATIVE:
                    dialogInterface.dismiss();
                    break;
                case DialogInterface.BUTTON_POSITIVE:

                    boolean result = new MyDatabaseHelper(activity).insertBook(AccountStateManager.getSingleton().getAccountConnected(),
                            book.getTitle(), book.getAuthor(), book.getPublisher(), book.getCategory(), book.getPublishedDate(), read, dateTextView.getText().toString(), note);

                    dialogInterface.dismiss();
                    if (result) {
                        Toast.makeText(activity, R.string.book_added_in_your_library, Toast.LENGTH_LONG).show();

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        try {
                            strDate = sdf.parse(dateTextView.getText().toString());
                            if (strDate != null && strDate.after(Calendar.getInstance().getTime())) {
                                addEventInCalendar(book.getTitle());
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, R.string.this_book_already_exists, Toast.LENGTH_LONG).show();
                    }

            }

        }
    };

    /******************************************
     * Change la position du cadre indiquant la
     * note sélectionnée et mise à jour de la
     * valeur de la note donnée au livre
     ******************************************/
    public void smileyListener(View v) {

        int id = v.getId(); // notes de 1 à 5 de la moins bonne à la meilleure

        ImageView smileySelected = dialog.findViewById(id);
        Objects.requireNonNull(smileySelected).setBackground(activity.getResources().getDrawable(R.drawable.edit_text_border));
        if (note != 0 && note != getGradeWithId(id)) { // on retire l'encadrement du smiley précédent
            ImageView previousSmiley = dialog.findViewById(getIdWithNote(note));
            Objects.requireNonNull(previousSmiley).setBackgroundResource(0);
        }
        note = getGradeWithId(id);
    }


    private TextView dateTextView;
    private Date strDate;

    /***********************************************
     * Affichage du sélecteur de date lorsque
     * l'utilisateur clique sur l'icon du calendrier
     ***********************************************/

    public void showDatePickerDialog() {

        DatePickerFragment datePicker = new DatePickerFragment();
        datePicker.setTxtView(dateTextView);
        datePicker.show(activity.getSupportFragmentManager(), "datePicker");
    }


    /********************************************************************
     * Boite de dialogue demandant à l'utilisateur s'il souhaite
     * créer un événement dans son calendrier pour se rappeler
     * de lire le livre actuel.
     * Si oui on lance l'application Calendrier choisie par l'utilisateur
     ********************************************************************/
    private void addEventInCalendar(final String title) {

        new AlertDialog.Builder(activity)
                .setTitle("Reminder")
                .setMessage(R.string.remind_me_to_read)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Calendar beginTime = Calendar.getInstance();
                        beginTime.setTime(strDate);
                        long startMillis = beginTime.getTimeInMillis();

                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra("beginTime", startMillis);
                        intent.putExtra("allDay", true);
                        intent.putExtra("rrule", "FREQ=DAILY;COUNT=1");
                        intent.putExtra("endTime", startMillis);
                        intent.putExtra("title", activity.getString(R.string.remind_to_read_) + " " + title);
                        activity.startActivity(intent);
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }
}
