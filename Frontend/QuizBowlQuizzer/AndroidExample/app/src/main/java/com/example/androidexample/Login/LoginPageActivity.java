package com.example.androidexample.Login;

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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class LoginPageActivity extends AppCompatActivity implements Serializable {
    private Button loginButton, signUpButton, quickButton;
    private TextInputEditText emailField, passwordField;
    private TextView tryAgain;
    private RequestQueue queue;
    UserObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        loginButton = findViewById(R.id.logInButton);
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        signUpButton = findViewById(R.id.signUpButton);
        queue = Volley.newRequestQueue(this);
        tryAgain = findViewById(R.id.tryAgain);
        quickButton = findViewById(R.id.quickLoginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String username = emailField.getText().toString();
                String password = passwordField.getText().toString();
                String url = MainActivity.httpServerAddress + "/users/login?username=" + username + "&password=" + password;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            UserObject user = UserObject.getUserFromJSON(response);
                            Intent intent;
                            intent = new Intent(LoginPageActivity.this, MainActivity.class);
                            intent.putExtra("USER", user);
                            startActivity(intent);  // go to MainActivity with the key-value data
                        } catch (JSONException e) {
                            tryAgain.setText("Incorrect username or password.\n");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(request);
            }
        });

        /* click listener on signup button pressed */
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginPageActivity.this, SignUpActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });

        quickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* grab strings from user inputs */
                String username = "SystemUser";
                String password = "system";
                String url = MainActivity.httpServerAddress + "/users/login?username=" + username + "&password=" + password;
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            user = UserObject.getUserFromJSON(response);
                            Intent intent = new Intent(LoginPageActivity.this, MainActivity.class);
                            intent.putExtra("USER", user);
                            startActivity(intent);
                        } catch (JSONException e) {
                            tryAgain.setText("Incorrect username or password.\n");
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
                queue.add(request);
            }
        });
    }
}
