package com.example.androidexample.EditQuestions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.androidexample.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class FilterQuestionsFragment extends BottomSheetDialogFragment {
    private FilterQuestionsModel filterQuestionsModel;
    private Button saveButton;

    private TextView errorMessage;
    private QuestionObject questionObject;
    private RadioButton scienceBtn, historyBtn, fineArtsBtn, popCultureBtn, geographyBtn;

    public FilterQuestionsFragment() {
        super(R.layout.fragment_question_adder);
    }
    // Constructor with QuestionObject allows me to edit questions

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = requireActivity();
        filterQuestionsModel = new ViewModelProvider(activity).get(FilterQuestionsModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_filter_questions, container, false);

        // Initialize everything
        saveButton = (Button) rootView.findViewById(R.id.btnSave);


        scienceBtn = (RadioButton) rootView.findViewById(R.id.radioScience);
        historyBtn = (RadioButton) rootView.findViewById(R.id.radioHistory);
        fineArtsBtn = (RadioButton) rootView.findViewById(R.id.radioFineArts);
        geographyBtn = (RadioButton) rootView.findViewById(R.id.radioGeography);
        popCultureBtn = (RadioButton) rootView.findViewById(R.id.radioPopCulture);

        errorMessage = (TextView) rootView.findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.INVISIBLE);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

                // If user has selected all the fields
                if (categoryChecked()){
                    filterQuestionsModel.setCategory(getCheckedCategory());
                    dismiss();
                } else{
                    filterQuestionsModel.setCategory(null);
                    dismiss();
//                    errorMessage.setText("Please choose a category");
//                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });
        return rootView;
    }
    private Boolean categoryChecked(){
        return (scienceBtn.isChecked() || geographyBtn.isChecked() || fineArtsBtn.isChecked() || historyBtn.isChecked() || popCultureBtn.isChecked());
    }

    private String getCheckedCategory(){
        if (scienceBtn.isChecked()){
            return "Science";
        }
        if (geographyBtn.isChecked()) {
            return "Geography";
        }
        if (fineArtsBtn.isChecked()){
            return "Fine Arts";
        }
        if (historyBtn.isChecked()){
            return "History";
        }
        return "Pop Culture";
    }
}
