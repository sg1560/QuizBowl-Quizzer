package com.example.androidexample.Groups;

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
import com.example.androidexample.User.UserObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class ListAllGroupsActivity extends AppCompatActivity implements Serializable {

    private Button listEm, back;
    private TextView list;
    private UserObject user;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listallgroups_layout);
        queue = Volley.newRequestQueue(this);

        back = findViewById(R.id.backFromListAll);
        listEm = findViewById(R.id.listGroupsButton);
        list = findViewById(R.id.listText);
        user = (UserObject) getIntent().getSerializableExtra("USER");

        listEm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = MainActivity.httpServerAddress + "/teams/getall";
                JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject team = response.getJSONObject(i);
                                team = response.getJSONObject(i);
                                list.append("Team: " + team.getString("name") + "\nMembers:\n");
                                JSONArray members = team.getJSONArray("members");
                                for (int j = 0; j < members.length(); j++) {
                                    JSONObject member = members.getJSONObject(j);
                                    String username = member.getString("userName");
                                    list.append("     " + username + "\n");
                                }
                                list.append("\n");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                queue.add(request);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListAllGroupsActivity.this, GroupPageActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
    }
}
