package com.example.androidexample.Groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class GroupJoinActivity extends AppCompatActivity implements Serializable {

    private Button join, create;
    private EditText idField;
    private RequestQueue queue;
    private int userID;
    private UserObject userObj;
    private JSONObject user;

    private BottomNavigationView navBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupjoin_layout);

        join = findViewById(R.id.joinButton);
        idField = findViewById(R.id.teamIDField);
        create = findViewById(R.id.createTeamBUtton);
        queue = Volley.newRequestQueue(this);
        navBar = findViewById(R.id.bottomNavigationView);
        userObj = (UserObject) getIntent().getSerializableExtra("USER");

        navBar.setSelectedItemId(R.id.nav_groups);

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, MainActivity.httpServerAddress + "/teams/getbyid/" + idField.getText().toString() + "/adduser/" + userObj.getId(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        userObj.joinGroup(Integer.parseInt(idField.getText().toString()));
                        Toast.makeText(getApplicationContext(), "Group joined!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(GroupJoinActivity.this, GroupPageActivity.class);
                        intent.putExtra("USER", userObj);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Join failed!", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(request);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupJoinActivity.this, GroupCreationActivity.class);
                intent.putExtra("USER", userObj);
                startActivity(intent);
            }
        });
        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()){
                case R.id.nav_home:
                    intent = new Intent(GroupJoinActivity.this, MainActivity.class);
                    intent.putExtra("USER", userObj);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    intent = new Intent(GroupJoinActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", userObj);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    intent = new Intent(GroupJoinActivity.this, StudyActivity.class);
                    intent.putExtra("USER", userObj);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    break;
                case R.id.nav_chat:
                    intent = new Intent(GroupJoinActivity.this, ChatActivity.class);
                    intent.putExtra("USER", userObj);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }
}

