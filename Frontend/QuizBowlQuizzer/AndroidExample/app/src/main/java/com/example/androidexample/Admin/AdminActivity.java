package com.example.androidexample.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;

public class AdminActivity extends AppCompatActivity {

    private UserObject user;
    private Button seeGroups, seeQuestions, list, back;
    private TextView admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_layout);
        user = (UserObject) getIntent().getSerializableExtra("USER");

        seeGroups = findViewById(R.id.adminSeeGroups);
        seeQuestions = findViewById(R.id.adminSeeQuestions);
        admin = findViewById(R.id.adminText);
        list = findViewById(R.id.listAdmin);
        back = findViewById(R.id.adminBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, MainActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        seeGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminGroupActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        seeQuestions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminQuestionsActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, AdminListActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
    }
}
