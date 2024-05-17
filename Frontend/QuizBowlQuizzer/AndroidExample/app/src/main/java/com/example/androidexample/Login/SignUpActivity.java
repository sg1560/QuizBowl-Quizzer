package com.example.androidexample.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;

public class SignUpActivity extends AppCompatActivity {
    private Button signUpButton;
    private EditText emailField, passwordField;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_page);

        emailField = findViewById(R.id.mailField);
        passwordField = findViewById(R.id.passField);
        signUpButton = findViewById(R.id.signupButton);
        queue = Volley.newRequestQueue(this);


        /* button click listeners */
        /* click listener on signup button pressed */
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String username = emailField.getText().toString();
                String password = passwordField.getText().toString();
                if (username.length() != 0 && password.length() != 0) {
                    accountCreate(username, password);
                    Toast.makeText(getApplicationContext(), "Signing up", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this, LoginPageActivity.class));
                } else {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void accountCreate(String user, String pass) {
        String url = MainActivity.httpServerAddress + "/users/post/" + user + "?password=" + pass;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> Toast.makeText(getApplicationContext(), "Success!", Toast.LENGTH_LONG).show(), error -> Toast.makeText(getApplicationContext(), "Error in signing up", Toast.LENGTH_LONG).show());
        queue.add(stringRequest);
    }
}
