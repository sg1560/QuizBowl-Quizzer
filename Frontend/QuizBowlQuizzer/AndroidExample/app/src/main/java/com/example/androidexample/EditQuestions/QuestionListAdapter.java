package com.example.androidexample.EditQuestions;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.androidexample.R;

import java.util.List;

public class QuestionListAdapter extends ArrayAdapter<QuestionObject> {

    public QuestionListAdapter(Context context, List<QuestionObject> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        QuestionObject item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.question_item, parent, false);
        }

        // Lookup view for data population
        TextView itemQuestion = convertView.findViewById(R.id.itemUserName);
        TextView itemAnswer = convertView.findViewById(R.id.itemText);
        TextView itemCategory = convertView.findViewById(R.id.itemCategory);
        ImageView categoryColor = convertView.findViewById(R.id.categoryColor);

        // Populate the data into the template view using the data object
        itemQuestion.setText(item.getQuestion());
        itemAnswer.setText(item.getAnswer());
        itemCategory.setText(item.getCategory());

        switch(item.getCategory()){
            case "History":
                categoryColor.setColorFilter(Color.RED);
                break;
            case "Geography":
                categoryColor.setColorFilter(Color.YELLOW);
                break;
            case "Pop Culture":
                categoryColor.setColorFilter(Color.MAGENTA);
                break;
            case "Fine Arts":
                categoryColor.setColorFilter(Color.BLUE);
                break;
            default:
                categoryColor.setColorFilter(Color.GREEN);
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

}

