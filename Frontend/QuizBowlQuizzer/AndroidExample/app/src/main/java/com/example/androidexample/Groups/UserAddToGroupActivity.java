package com.example.androidexample.Groups;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;

import org.json.JSONObject;

public class UserAddToGroupActivity extends AppCompatActivity {

    private UserObject user;
    private EditText field;
    private Button back, add;
    private RequestQueue queue;
    private long teamID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useraddtogroup);
        user = (UserObject) getIntent().getSerializableExtra("USER");
        teamID = (long) getIntent().getSerializableExtra("teamID");
        queue = Volley.newRequestQueue(this);

        field = findViewById(R.id.addUserIDField);
        back = findViewById(R.id.backToCoach);
        add = findViewById(R.id.addToGroup);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserAddToGroupActivity.this, CoachActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, MainActivity.httpServerAddress + "/teams/getbyid/" + teamID + "/adduser/" + field.getText().toString(), null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(request);
            }
        });
    }
}
