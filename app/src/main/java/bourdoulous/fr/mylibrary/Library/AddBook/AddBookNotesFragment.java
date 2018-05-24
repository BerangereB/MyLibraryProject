package bourdoulous.fr.mylibrary.Library.AddBook;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import bourdoulous.fr.mylibrary.Library.AccountActivity;
import bourdoulous.fr.mylibrary.R;
import bourdoulous.fr.mylibrary.Utilities.DatePickerFragment;

import static bourdoulous.fr.mylibrary.Utilities.SmileyGrade.getGradeWithId;
import static bourdoulous.fr.mylibrary.Utilities.SmileyGrade.getIdWithNote;


public class AddBookNotesFragment extends Fragment {

    private static final String TAG = "AddBookNotesFragment";
    private boolean read = false;
    private int note = 0; // pas de note donné

    private LinearLayout gradeLayout;
    private TextView titleGradeLayout;
    private TextView tv_date;

    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_add_book_notes, container, false);

        gradeLayout = view.findViewById(R.id.smileyLayout);
        titleGradeLayout = view.findViewById(R.id.titleSmileyLayout);
        tv_date = view.findViewById(R.id.date);

        showGradeLayout(false);

        Switch aSwitch = view.findViewById(R.id.switch_);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                changeGradeLayoutState(isChecked);
            }
        });

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getInt("READ") == 1) {
                aSwitch.setChecked(true);
                note = bundle.getInt("GRADE");
                if (note != 0) {
                    gradeLayout.findViewById(getIdWithNote(note)).setBackground(getResources().getDrawable(R.drawable.edit_text_border));
                }
            } else {
                aSwitch.setChecked(false);
            }

            tv_date.setText(bundle.getString("DATE"));

        }

        return view;
    }

    private void changeGradeLayoutState(boolean isChecked) {
        read = isChecked;
        if (read) {
            showGradeLayout(true);
        } else {
            showGradeLayout(false);
        }
    }

    private void showGradeLayout(boolean b) {
        if (b) {
            titleGradeLayout.setVisibility(View.VISIBLE);
            gradeLayout.setVisibility(View.VISIBLE);
        } else {
            titleGradeLayout.setVisibility(View.GONE);
            gradeLayout.setVisibility(View.GONE);
            note = 0;
        }
    }


    public void smileyListener(View v) {
        int id = v.getId(); // notes de 1 à 5 de la moins bonne à la meilleure
        ImageView smileySelected = view.findViewById(id);
        smileySelected.setBackground(getResources().getDrawable(R.drawable.edit_text_border));
        if (note != 0 && note != getGradeWithId(id)) { // on retire l'encadrement du smiley précédent
            ImageView previousSmiley = view.findViewById(getIdWithNote(note));
            previousSmiley.setBackgroundResource(0);
        }
        note = getGradeWithId(id);
    }

    public boolean isRead() {
        return read;
    }

    public int getNote() {
        return note;
    }

    public String getDate() {
        return tv_date.getText().toString();
    }



    public void showDatePickerDialog() {

        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setTxtView(tv_date);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }


    public void addEventInCalendar(final String title) {

        if(getDate().isEmpty()) {
            startActivity(new Intent(getActivity(), AccountActivity.class));
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            final Date strDate = sdf.parse(getDate());
            if (strDate != null && strDate.after(Calendar.getInstance().getTime())) {
                new AlertDialog.Builder(getActivity())
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
                                intent.putExtra("title", getString(R.string.remind_to_read_) + " "  + title);
                                startActivity(new Intent(getActivity(), AccountActivity.class));
                                startActivityForResult(intent,1);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(getActivity(), AccountActivity.class));
                            }
                        })
                        .show();
            }else{
                startActivity(new Intent(getActivity(), AccountActivity.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}


