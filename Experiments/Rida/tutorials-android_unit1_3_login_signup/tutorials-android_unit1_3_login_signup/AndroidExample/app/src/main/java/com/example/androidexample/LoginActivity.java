package com.example.androidexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    private String username;

    private String password;
    private EditText usernameEditText;  // define username edittext variable
    private EditText passwordEditText;  // define password edittext variable

    private TextView invalidLogin; // define error message variable
    private Button loginButton;         // define login button variable
    private Button signupButton;        // define signup button variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);            // link to Login activity XML

        /* initialize UI elements */
        usernameEditText = findViewById(R.id.login_username_edt);
        passwordEditText = findViewById(R.id.login_password_edt);
        loginButton = findViewById(R.id.login_login_btn);    // link to login button in the Login activity XML
        signupButton = findViewById(R.id.login_signup_btn);  // link to signup button in the Login activity XML
        invalidLogin = findViewById(R.id.incorrect_login);
        invalidLogin.setVisibility(View.INVISIBLE);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username = extras.getString("USERNAME");
            password = extras.getString("PASSWORD");
        }

        /* click listener on login button pressed */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* grab strings from user inputs */
                String usernameInput = usernameEditText.getText().toString();
                String passwordInput = passwordEditText.getText().toString();

                /* when login button is pressed, use intent to switch to Login Activity */
                if (usernameInput.equals(username) && passwordInput.equals(password)) {
                    invalidLogin.setVisibility(View.INVISIBLE);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("USERNAME", username);  // key-value to pass to the MainActivity
                    intent.putExtra("PASSWORD", password);  // key-value to pass to the MainActivity
                    startActivity(intent);  // go to MainActivity with the key-value data
                } else {
                    invalidLogin.setVisibility(View.VISIBLE);
                }
            }
        });

        /* click listener on signup button pressed */
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /* when signup button is pressed, use intent to switch to Signup Activity */
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);  // go to SignupActivity
            }
        });
    }
}