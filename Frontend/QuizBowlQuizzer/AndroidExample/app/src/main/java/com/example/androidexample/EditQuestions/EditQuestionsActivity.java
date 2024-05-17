package com.example.androidexample.EditQuestions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.Groups.GroupPageActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.Study.StudyActivity;
import com.example.androidexample.User.UserObject;
import com.example.androidexample.Networking.VolleySingleton;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// Page to edit and add questions to the app
public class EditQuestionsActivity extends AppCompatActivity implements Serializable {
    private AddQuestionsModel addQuestionsModel;
    private FilterQuestionsModel filterQuestionsModel;
    private Button newQuestion, saveButton, filterButton;

    private BottomNavigationView navBar;
    private TextView postResponse;
    private ListView listView;
    private UserObject user;

    //private static final String URL_POSTMAN_POST = "https://fb2a511b-3578-4818-89b1-de4ab3629707.mock.pstmn.io/post";

    private static final String URL_GET_QUESTIONS = "/questions";

    private final String URL_CREATE_QUESTIONS = "/questions/createQuestion/";

    private final String URL_EDIT_QUESTIONS = "/questions/update/";
    private final String URL_GET_CATEGORY = "/categories/getquestions/";

    private final String URL_DELETE_QUESTIONS = "/questions/delete/";
    private QuestionListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_questions);
        user = (UserObject) getIntent().getSerializableExtra("USER");


        addQuestionsModel = new ViewModelProvider(this).get(AddQuestionsModel.class);
        filterQuestionsModel = new ViewModelProvider(this).get(FilterQuestionsModel.class);

        listView = findViewById(R.id.questionList);
        adapter = new QuestionListAdapter(this, new ArrayList<>());
        postResponse = findViewById(R.id.postResponse);
        postResponse.setVisibility(View.INVISIBLE);

        filterButton = findViewById(R.id.btnFilter);
        saveButton = findViewById(R.id.btnSave);
        newQuestion = findViewById(R.id.btnNewQuestion);
        navBar = findViewById(R.id.bottomNavigationView);
        listView.setAdapter(adapter);
        navBar.setSelectedItemId(R.id.nav_edit);

        getExistingQuestions();


        FragmentManager fragmentManager = getSupportFragmentManager();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                new QuestionAdder(adapter.getItem(i)).show(fragmentManager, "editQuestionTag");

            }
        });

        newQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new QuestionAdder().show(fragmentManager, "newQuestionTag");
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditQuestionsActivity.this, MainActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new FilterQuestionsFragment().show(fragmentManager, "FilterQuestionsTag");
            }
        });

        addQuestionsModel.getObject().observe(this, object -> {
            adapter.add((QuestionObject) addQuestionsModel.getObject().getValue());
            postRequest((QuestionObject) addQuestionsModel.getObject().getValue());
        });

        addQuestionsModel.getDelete().observe(this, delete -> {
            adapter.remove((QuestionObject) addQuestionsModel.getDelete().getValue());
            deleteRequest(((QuestionObject) addQuestionsModel.getDelete().getValue()).getId());
        });

        addQuestionsModel.getEdited().observe(this, edited -> {
            adapter.notifyDataSetChanged();
            putRequest((QuestionObject) addQuestionsModel.getEdited().getValue());
        });

        filterQuestionsModel.getCategory().observe(this, category -> {
            adapter.clear();
            if (category != null) {
                long catid = MainActivity.getCategoryId(filterQuestionsModel.getCategory().getValue());
                if (catid > 0){
                    makeCategoryRequest(MainActivity.getCategoryId(filterQuestionsModel.getCategory().getValue()));
                }
                else {
                    getExistingQuestions();
                }
            } else {
                getExistingQuestions();
            }
        });

        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(EditQuestionsActivity.this, MainActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    break;
                case R.id.nav_study:
                    intent = new Intent(EditQuestionsActivity.this, StudyActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    intent = new Intent(EditQuestionsActivity.this, GroupPageActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_chat:
                    intent = new Intent(EditQuestionsActivity.this, ChatActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
            }
            return true;
        });

    }

    private void getExistingQuestions() {
        String requestUrl = MainActivity.httpServerAddress + URL_GET_QUESTIONS;
        requestUrl += "/" + user.getId();
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                requestUrl,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Parse the JSON array and add data to the adapter
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                adapter.add(QuestionObject.getQuestionFromJSON(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }) {
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    private void postRequest(QuestionObject question) {
        String URL = MainActivity.httpServerAddress + URL_CREATE_QUESTIONS + MainActivity.getCategoryId(question.getCategory()) + "?owner=" + user.getId();
        try {
            JSONObject body = new JSONObject("{\"question\":\"" + question.getQuestion() + "\", \"answer\":\"" + question.getAnswer() + "\", \"points\":10}");

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL,
                    body,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            postResponse.setText(response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            postResponse.setText("Error:" + error.getMessage());
                        }
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                    //                headers.put("Content-Type", "application/json");
                    return headers;
                }

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    //                params.put("param1", "value1");
                    //                params.put("param2", "value2");
                    return params;
                }
            };
            // Adding request to request queue
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void putRequest(QuestionObject question) {
        String URL = MainActivity.httpServerAddress + URL_EDIT_QUESTIONS + question.getId() + "/" + MainActivity.getCategoryId(question.getCategory());
        String json = "{\"question\":\"" + question.getQuestion() + "\", \"answer\":\"" + question.getAnswer() + "\", \"category\":\"" + question.getCategory() + "\", \"points\":10}";

        // Convert input to JSONObject
        JSONObject postBody = null;
        try {
            // etRequest should contain a JSON object string as your POST body
            // similar to what you would have in POSTMAN-body field
            // and the fields should match with the object structure of @RequestBody on sb
            postBody = new JSONObject(json);
        } catch (Exception e) {
            Log.i("ERROR", "postRequest: bad");
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                URL,
                postBody,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        postResponse.setText(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        postResponse.setText("Error:" + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                //                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //                params.put("param1", "value1");
                //                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }

    private void makeCategoryRequest(Long category) {

        String URL = MainActivity.httpServerAddress + URL_GET_CATEGORY + category + "?owner=" + user.getId();
        JsonArrayRequest jsonArrReq = new JsonArrayRequest(
                Request.Method.GET,
                URL,
                null, // Pass null as the request body since it's a GET request
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("Volley Response", response.toString());

                        // Parse the JSON array and add data to the adapter
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                adapter.add(QuestionObject.getQuestionFromJSON(response.getJSONObject(i)));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley Error", error.toString());
                    }
                }) {
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrReq);
    }

    private void deleteRequest(long questionId) {
        String URL = MainActivity.httpServerAddress + URL_DELETE_QUESTIONS + questionId;

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        postResponse.setText(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        postResponse.setText("Error:" + error.getMessage());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
                //                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                //                params.put("param1", "value1");
                //                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
    }
}