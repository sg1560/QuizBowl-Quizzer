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

public class AdminListActivity extends AppCompatActivity {
    private UserObject user;
    private Button back;
    private TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listadmin_layout);
        user = (UserObject) getIntent().getSerializableExtra("USER");

        back = findViewById(R.id.adminListBack);
        text = findViewById(R.id.adminListText);
        String url = MainActivity.httpServerAddress + "/admin/getAllAdmin";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                for(int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject object = jsonArray.getJSONObject(i);
                        text.append(object.getInt("id")+": "+object.getString("name")+"\n");
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                volleyError.printStackTrace();
            }
        });
        queue.add(request);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminListActivity.this, AdminActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
    }
}
