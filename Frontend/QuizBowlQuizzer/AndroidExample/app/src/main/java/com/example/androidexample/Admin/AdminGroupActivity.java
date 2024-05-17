package com.example.androidexample.Admin;

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

public class AdminGroupActivity extends AppCompatActivity {

    private UserObject user;
    private Button back, list;
    private TextView text;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admingroup_layout);
        user = (UserObject) getIntent().getSerializableExtra("USER");

        text = findViewById(R.id.adminGroupText);
        back = findViewById(R.id.adminGroupBack);
        list = findViewById(R.id.listGroupsAdmin);

        requestQueue = Volley.newRequestQueue(this);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = MainActivity.httpServerAddress + "/admin/getTeams";
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject teamObject = response.getJSONObject(i);
                                int teamID = teamObject.getInt("tid");
                                String teamName = teamObject.getString("name");
                                //JSONObject coachObject = teamObject.getJSONObject("coach");
                                //String coachUsername = coachObject.getString("userName");
                                JSONArray membersArray = teamObject.getJSONArray("members");

                                text.append("Team ID: "+teamID+", Team name: "+teamName+"\nTeam members:\n");

                                for (int j = 0; j < membersArray.length(); j++) {
                                    JSONObject memberObject = membersArray.getJSONObject(j);
                                    String memberUsername = memberObject.getString("userName");
                                    text.append(memberUsername+"\n");
                                }
                                text.append("\n");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                });
                requestQueue.add(jsonArrayRequest);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminGroupActivity.this, AdminActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
    }
}
