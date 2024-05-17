package com.example.androidexample.EditQuestions;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

// Model lets us pass data from the fragment to the
public class AddQuestionsModel extends ViewModel {

  private MutableLiveData<QuestionObject> object = new MutableLiveData<QuestionObject>();

  private MutableLiveData<QuestionObject> delete = new MutableLiveData<QuestionObject>();

  private MutableLiveData<QuestionObject> edited = new MutableLiveData<>();

  public void deleteQuestion(QuestionObject deleteQuestion) {delete.setValue(deleteQuestion);}
  public void selectObject(QuestionObject newObject) {object.setValue((newObject));}

  public void setEdited(QuestionObject editedObject) {edited.setValue(editedObject);}

  public LiveData getEdited() {return edited;}


  public LiveData getObject() { return object;}
  public LiveData getDelete() { return delete;}
}

