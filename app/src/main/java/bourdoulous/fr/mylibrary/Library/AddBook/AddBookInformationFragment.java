package bourdoulous.fr.mylibrary.Library.AddBook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import bourdoulous.fr.mylibrary.R;

public class AddBookInformationFragment extends Fragment {


    //private static final String TAG = "InformationFragment";
    private TextInputLayout titleInputLayout;
    private TextInputLayout authorInputLayout;
    private TextInputLayout publisherInputLayout;
    private TextInputLayout categoryInputLayout;
    private TextInputLayout publishedDateInputLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_book_information, container, false);

        titleInputLayout = view.findViewById(R.id.titleLayout);
        authorInputLayout = view.findViewById(R.id.authorLayout);
        publisherInputLayout = view.findViewById(R.id.publisherLayout);
        categoryInputLayout = view.findViewById(R.id.categoryLayout);
        publishedDateInputLayout = view.findViewById(R.id.publishedDateLayout);

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            Objects.requireNonNull(titleInputLayout.getEditText()).setText(bundle.getString("TITLE"));
            Objects.requireNonNull(authorInputLayout.getEditText()).setText(bundle.getString("AUTHOR"));
            Objects.requireNonNull(publisherInputLayout.getEditText()).setText(bundle.getString("PUBLISHER"));
            Objects.requireNonNull(categoryInputLayout.getEditText()).setText(bundle.getString("CATEGORY"));
            Objects.requireNonNull(publishedDateInputLayout.getEditText()).setText(bundle.getString("PUBLISHEDDATE"));
        }

        return view;
    }

    public boolean checkDateFormat() {
        EditText editText = publishedDateInputLayout.getEditText();

        if (editText != null) {
            String date_str = editText.getText().toString();
            if (!date_str.isEmpty()) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault());
                Date date = format.parse(date_str, new ParsePosition(0));
                if(date == null){
                    publishedDateInputLayout.setErrorEnabled(true);
                    publishedDateInputLayout.setError(getString(R.string.wrong_format));
                    return false;
                }
            }
        }
        return true;
    }


    public boolean checkTitleNotEmpty() {
        EditText editText = titleInputLayout.getEditText();
        if (editText != null && editText.getText().toString().isEmpty()) {
            titleInputLayout.setErrorEnabled(true);
            titleInputLayout.setError(getString(R.string.empty_fields));
            return false;
        }
        return true;
    }


    public String getTitleInput() {
        return Objects.requireNonNull(titleInputLayout.getEditText()).getText().toString();
    }

    public String getAuthorInput() {
        return Objects.requireNonNull(authorInputLayout.getEditText()).getText().toString();
    }

    public String getPublisherInput() {
        return Objects.requireNonNull(publisherInputLayout.getEditText()).getText().toString();
    }

    public String getCategoryInput() {
        return Objects.requireNonNull(categoryInputLayout.getEditText()).getText().toString();
    }


    public String getPublishedDateInput() {
        return Objects.requireNonNull(publishedDateInputLayout.getEditText()).getText().toString();
    }
}
