package com.example.androidexample.User;

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
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;

import org.json.JSONObject;

public class PasswordChangeActivity extends AppCompatActivity {

    private UserObject user;
    private Button back, change;
    private EditText field;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passwordchange);
        user = (UserObject) getIntent().getSerializableExtra("USER");
        queue = Volley.newRequestQueue(this);

        back = findViewById(R.id.backToPPButton);
        change = findViewById(R.id.changePasswordButton);
        field = findViewById(R.id.passwordChangeField);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PasswordChangeActivity.this, UserProfileActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = field.getText().toString();
                String url = MainActivity.httpServerAddress+"/users/changepassword?username="+user.getUserName()+"&password="+user.getPassword()+"&newpassword="+password;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(), "Successfully changed!", Toast.LENGTH_LONG);
                        user.setPassword(password);
                        Intent intent = new Intent(PasswordChangeActivity.this, UserProfileActivity.class);
                        intent.putExtra("USER", user);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(getApplicationContext(), "Failed to change", Toast.LENGTH_SHORT);
                    }
                });
                queue.add(request);
            }
        });
    }
}
