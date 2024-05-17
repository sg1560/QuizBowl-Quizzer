package com.example.androidexample.Study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.Groups.GroupPageActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class StudyActivity extends AppCompatActivity {
    private Button scienceBtn, geographyBtn, popCultureBtn, fineArtsBtn, historyBtn, allBtn;
    private Spinner dropdown;
    private BottomNavigationView navBar;
    private UserObject user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        user = (UserObject) getIntent().getSerializableExtra("USER");

        scienceBtn = findViewById(R.id.btnScience);
        geographyBtn = findViewById(R.id.btnGeography);
        popCultureBtn = findViewById(R.id.btnPopCulture);
        fineArtsBtn = findViewById(R.id.btnFineArts);
        historyBtn = findViewById(R.id.btnHistory);
        allBtn = findViewById(R.id.btnAll);
        navBar = findViewById(R.id.bottomNavigationView);
        dropdown = findViewById(R.id.session_dropdown);

        navBar.setSelectedItemId(R.id.nav_study);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.sessions, R.layout.custom_spinner_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        dropdown.setAdapter(adapter);

        scienceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudyButtonPressed(MainActivity.getCategoryId("Science") + "");

            }
        });
        geographyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudyButtonPressed(MainActivity.getCategoryId("Geography") + "");
            }
        });
        popCultureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudyButtonPressed(MainActivity.getCategoryId("Pop Culture") + "");
            }
        });
        fineArtsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudyButtonPressed(MainActivity.getCategoryId("Fine Arts") + "");
            }
        });
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudyButtonPressed(MainActivity.getCategoryId("History") + "");
            }
        });
        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStudyButtonPressed("0");
            }
        });

        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()){
                case R.id.nav_home:
                    intent = new Intent(StudyActivity.this, MainActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    intent = new Intent(StudyActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    break;
                case R.id.nav_groups:
                    intent = new Intent(StudyActivity.this, GroupPageActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_chat:
                    intent = new Intent(StudyActivity.this, ChatActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }

    private void onStudyButtonPressed(String category) {
        if (dropdown.getSelectedItem().toString().equals("Competitive")) {
            Intent intent = new Intent(StudyActivity.this, CompetitiveSessionActivity.class);
            intent.putExtra("USER", user);
            intent.putExtra("Category", category);
            intent.putExtra("Solo", false);
            intent.putExtra("SessionType", "Competitive");
            startActivity(intent);
        } else {
            Intent intent = new Intent(StudyActivity.this, CollaborativeSessionActivity.class);
            intent.putExtra("USER", user);
            intent.putExtra("Category", category);
            if (dropdown.getSelectedItem().toString().equals("Solo")) {
                intent.putExtra("Solo", true);
                intent.putExtra("SessionType", "Solo");
            }
            else {
                intent.putExtra("Solo", false);
                intent.putExtra("SessionType", "Collaborative");
            }
            startActivity(intent);
        }
    }
}
