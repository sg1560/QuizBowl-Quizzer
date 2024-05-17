package com.example.androidexample.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;

public class ShowAllUsersActivity extends AppCompatActivity implements View.OnClickListener, Serializable {
    private TextView list;
    private Button getIt, back;
    private RequestQueue queue;
    private UserObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showallusers_layout);
        queue = Volley.newRequestQueue(this);
        user = (UserObject) getIntent().getSerializableExtra("USER");

        list = findViewById(R.id.usersList);
        getIt = findViewById(R.id.getAllButton);
        back = findViewById(R.id.backButton);

        getIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = MainActivity.httpServerAddress + "/users/getall";
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                String username = response.getJSONObject(i).getString("userName");
                                int cid = response.getJSONObject(i).getInt("cid");
                                list.append(cid + " - " + username + "\n\n");
                            }
                        } catch (JSONException e) {
                            list.append("No users found!");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.getStackTrace();
                        list.append("Network error!");
                    }
                });
                queue.add(request);
            }
        });
        back.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(ShowAllUsersActivity.this, MainActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }
}
