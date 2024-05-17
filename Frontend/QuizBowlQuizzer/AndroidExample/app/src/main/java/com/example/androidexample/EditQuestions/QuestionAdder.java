package com.example.androidexample.EditQuestions;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.androidexample.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputEditText;

// Fragment where I can add/edit questions
public class QuestionAdder extends BottomSheetDialogFragment {

    private AddQuestionsModel addQuestionsModel;
    private Button saveButton, deleteButton;

    private TextInputEditText question, answer;
    private TextView errorMessage, questionTitle;
    private QuestionObject questionObject;
    private RadioButton scienceBtn, historyBtn, fineArtsBtn, popCultureBtn, geographyBtn;

    public QuestionAdder() {
        super(R.layout.fragment_question_adder);
    }

    // Constructor with QuestionObject allows me to edit questions
    public QuestionAdder(QuestionObject object) {
        super(R.layout.fragment_question_adder);
        questionObject = object;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity activity = requireActivity();
        addQuestionsModel = new ViewModelProvider(activity).get(AddQuestionsModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question_adder, container, false);

        // Initialize everything
        saveButton = (Button) rootView.findViewById(R.id.btnSave);
        question = (TextInputEditText) rootView.findViewById(R.id.question);
        answer = (TextInputEditText) rootView.findViewById(R.id.answer);
        deleteButton = (Button) rootView.findViewById(R.id.btnDelete);

        scienceBtn = (RadioButton) rootView.findViewById(R.id.radioScience);
        historyBtn = (RadioButton) rootView.findViewById(R.id.radioHistory);
        fineArtsBtn = (RadioButton) rootView.findViewById(R.id.radioFineArts);
        geographyBtn = (RadioButton) rootView.findViewById(R.id.radioGeography);
        popCultureBtn = (RadioButton) rootView.findViewById(R.id.radioPopCulture);

        errorMessage = (TextView) rootView.findViewById(R.id.errorMessage);
        errorMessage.setVisibility(View.INVISIBLE);

        // If we're editing a question, change title and load old question/answer text
        questionTitle = (TextView) rootView.findViewById(R.id.questionAdderTitle);
        if (questionObject != null) {
            questionTitle.setText("Edit Question");
            question.setText(questionObject.getQuestion());
            answer.setText(questionObject.getAnswer());
            switch (questionObject.getCategory()) {
                case "History":
                    historyBtn.setChecked(true);
                    break;
                case "Fine Arts":
                    fineArtsBtn.setChecked(true);
                    break;
                case "Geography":
                    geographyBtn.setChecked(true);
                    break;
                case "Pop Culture":
                    popCultureBtn.setChecked(true);
                    break;
                default:
                    scienceBtn.setChecked(true);
                    break;
            }

            deleteButton.setVisibility(View.VISIBLE);
        } else {
            questionTitle.setText("New Question");
            deleteButton.setVisibility(View.INVISIBLE);
        }

        // When we click save
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String q = (String.valueOf(question.getText()));
                String a = (String.valueOf(answer.getText()));
                String c = getCategory();
                // If user has selected all the fields
                if (fieldsFilled() && categoryChecked()) {
                    // If we're editing object, hide alerts and edit the question object, send alert through the model
                    if (questionObject != null) {
                        errorMessage.setVisibility(View.INVISIBLE);
                        questionObject.setQuestion(q, a, c);
                        addQuestionsModel.setEdited(questionObject);
                        dismiss();
                    } else {
                        // Else add new object to the list
                        errorMessage.setVisibility(View.INVISIBLE);
                        addQuestionsModel.selectObject(new QuestionObject(-1, q, a, c, -1));
                        dismiss();
                    }
                } else {
                    if (!categoryChecked() && !fieldsFilled()) {
                        errorMessage.setText("Please enter a question, answer, and select a category");
                    } else if (!fieldsFilled()) {
                        errorMessage.setText("Please fill out both fields");
                    } else {
                        errorMessage.setText("Please choose a category");
                    }
                    errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQuestionsModel.deleteQuestion(questionObject);
                dismiss();
            }
        });

        // Inflate the layout for this fragment
        return rootView;
    }

    public String getCategory() {
        if (scienceBtn.isChecked()) {
            return "Science";
        } else if (geographyBtn.isChecked()) {
            return "Geography";
        } else if (fineArtsBtn.isChecked()) {
            return "Fine Arts";
        } else if (historyBtn.isChecked()) {
            return "History";
        }
        return "Pop Culture";
    }

    private Boolean categoryChecked() {
        return (scienceBtn.isChecked() || geographyBtn.isChecked() || fineArtsBtn.isChecked() || historyBtn.isChecked() || popCultureBtn.isChecked());
    }

    private Boolean fieldsFilled() {
        return !String.valueOf(question.getText()).equals("") && !String.valueOf(answer.getText()).equals("");
    }
}