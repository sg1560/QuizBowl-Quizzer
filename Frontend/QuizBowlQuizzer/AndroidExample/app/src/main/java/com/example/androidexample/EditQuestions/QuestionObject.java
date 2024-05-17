package com.example.androidexample.EditQuestions;

import com.example.androidexample.User.UserObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class QuestionObject {
    private String question;
    private String answer;

    private String category;

    private long id;

    private long userOwner;

    public QuestionObject(long id, String question, String answer, String category, long userOwner) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.userOwner = userOwner;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    public String getCategory() { return category;}

    public void setId(long id) {this.id = id;}

    public long getId() {return id;}

    public void setQuestion(String question, String answer, String category) {
        this.question = question;
        this.answer = answer;
        this.category = category;
    }

    public static QuestionObject getQuestionFromJSON(JSONObject jsonObject) throws JSONException {
        QuestionObject questionObject = new QuestionObject(
                jsonObject.getInt("id"),
                jsonObject.getString("question"),
                jsonObject.getString("answer"),
                jsonObject.getJSONObject("category").getString("categoryName"),
                jsonObject.getLong("id")
                );
        return questionObject;
    }
}
