package com.example.androidexample.EditQuestions;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class FilterQuestionsModel extends ViewModel {
    private MutableLiveData<String> category = new MutableLiveData<>();

    public void setCategory(String category) {
        this.category.setValue(category);
    }

    public MutableLiveData<String> getCategory() {
        return category;
    }
}
