package com.example.androidexample.Groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.Study.StudyActivity;
import com.example.androidexample.User.UserObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class GroupCreationActivity extends AppCompatActivity implements Serializable {

    private Button create;
    private EditText name;
    private RequestQueue queue;
    private UserObject userObject;

    private BottomNavigationView navBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupcreate_layout);
        queue = Volley.newRequestQueue(this);

        create = findViewById(R.id.createButton);
        name = findViewById(R.id.nameField);
        navBar = findViewById(R.id.bottomNavigationView);
        userObject = (UserObject) getIntent().getSerializableExtra("USER");
        navBar.setSelectedItemId(R.id.nav_groups);

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = MainActivity.httpServerAddress + "/teams/createNewTeam/" + name.getText().toString();
                String parameters = "?coachName=" + userObject.getUserName() + "&coachPassword=" + userObject.getPassword();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url + parameters, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            userObject.joinGroup(response.getInt("tid"));
                            JSONObject coach = new JSONObject();
                            coach.put("userName", userObject.getUserName());
                            coach.put("password", userObject.getPassword());
                            JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.POST, MainActivity.httpServerAddress + "/coach/createCoach", coach, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject jsonObject) {
                                    userObject.makeCoach();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {
                                    volleyError.printStackTrace();
                                }
                            });
                            queue.add(request1);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        Intent intent = new Intent(GroupCreationActivity.this, GroupPageActivity.class);
                        intent.putExtra("USER", userObject);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });
                queue.add(request);
            }
        });

        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()){
                case R.id.nav_home:
                    intent = new Intent(GroupCreationActivity.this, MainActivity.class);
                    intent.putExtra("USER", userObject);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    intent = new Intent(GroupCreationActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", userObject);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    intent = new Intent(GroupCreationActivity.this, StudyActivity.class);
                    intent.putExtra("USER", userObject);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    break;
                case R.id.nav_chat:
                    intent = new Intent(GroupCreationActivity.this, ChatActivity.class);
                    intent.putExtra("USER", userObject);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }
}
