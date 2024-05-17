package com.example.androidexample.Study;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.EditQuestions.QuestionObject;
import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.Groups.GroupPageActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;
import com.example.androidexample.Networking.WebSocketListener;
import com.example.androidexample.Networking.WebSocketManager1;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class CompetitiveSessionActivity extends AppCompatActivity implements WebSocketListener {

    private TextView QAtext, flashcardText, topSessionInfo, bottomSessionInfo;
    private Button seeAnswerButton, correctButton, incorrectButton;
    private ArrayList<QuestionObject> flashcardList;
    private int currentQuestionIndex, progressBarIndex;
    private boolean isQuestion, isStart, isEnd;
    private UserObject user;
    private HashMap<Long, UserObject> usersById;
    private HashMap<Long, ProgressBar> progressBarMap;
    private ConstraintLayout sessionInfo, user1View, user2View, user3View;
    private BottomNavigationView navBar;
    private ProgressBar userProgressBar, progressBar1, progressBar2, progressBar3;
    private final String URL_START = MainActivity.wsServerAddress + "/session/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (UserObject) getIntent().getSerializableExtra("USER");
        String type = getIntent().getStringExtra("SessionType");
        String URL = URL_START + type + "/" + user.getId() + "/team/" + user.getGroupID() + "/size/5/category/" + getIntent().getStringExtra("Category");
        Log.i("Websocket", URL);
        WebSocketManager1.getInstance().setWebSocketListener(CompetitiveSessionActivity.this);
        WebSocketManager1.getInstance().connectWebSocket(URL);

        setContentView(R.layout.activity_competitive);

        QAtext = findViewById(R.id.QAtext);
        flashcardText = findViewById(R.id.flashcard);
        seeAnswerButton = findViewById(R.id.btnSeeAnswer);
        correctButton = findViewById(R.id.btnCorrect);
        incorrectButton = findViewById(R.id.btnIncorrect);
        navBar = findViewById(R.id.bottomNavigationView);
        topSessionInfo = findViewById(R.id.topSessionInfo);
        bottomSessionInfo = findViewById(R.id.bottomSessionInfo);
        userProgressBar = findViewById(R.id.progressBarUser);
        progressBar1 = findViewById(R.id.progressBar1);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar3 = findViewById(R.id.progressBar3);
        sessionInfo = findViewById(R.id.sessionWrapper);
        user1View = findViewById(R.id.wrapperUser1);
        user2View = findViewById(R.id.wrapperUser2);
        user3View = findViewById(R.id.wrapperUser3);

        progressBarIndex = 0;
        usersById = new HashMap<>();
        progressBarMap = new HashMap<>();
        navBar.setSelectedItemId(R.id.nav_study);
        flashcardList = new ArrayList<QuestionObject>();
        isQuestion = true;
        isStart = false;
        isEnd = false;
        currentQuestionIndex = 0;
        QAtext.setText("");
        flashcardText.setText("Press start to begin!");
        seeAnswerButton.setText("Start");

        userProgressBar.setVisibility(View.INVISIBLE);
        user1View.setVisibility(View.INVISIBLE);
        user2View.setVisibility(View.INVISIBLE);
        user3View.setVisibility(View.INVISIBLE);
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        ArrayList<String> list = new ArrayList<>();
        list.add("Test");
        list.remove("Test");
        seeAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStart) {
                    WebSocketManager1.getInstance().sendMessage("start");
                } if (isEnd) {
                    Intent intent = new Intent(CompetitiveSessionActivity.this, StudyActivity.class);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                } else if (flashcardList.size() > currentQuestionIndex) {
                    String side;
                    if (isQuestion) {
                        side = "answer";
                    } else {
                        side = "question";
                    }
                    WebSocketManager1.getInstance().sendMessage("flipCard/" + flashcardList.get(currentQuestionIndex).getId() + "/" + side);
                }

            }
        });
        correctButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebSocketManager1.getInstance().sendMessage("question/" + flashcardList.get(currentQuestionIndex).getId() + "/correct");
                Log.i("StudySession", "Sent: question/" + flashcardList.get(currentQuestionIndex).getId() + "/correct");
            }
        });

        incorrectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WebSocketManager1.getInstance().sendMessage("question/" + flashcardList.get(currentQuestionIndex).getId() + "/incorrect");
                Log.i("StudySession", "Sent: question/" + flashcardList.get(currentQuestionIndex).getId() + "/incorrect");
            }
        });

        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(CompetitiveSessionActivity.this, MainActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    intent = new Intent(CompetitiveSessionActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    intent = new Intent(CompetitiveSessionActivity.this, StudyActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    intent = new Intent(CompetitiveSessionActivity.this, GroupPageActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_chat:
                    intent = new Intent(CompetitiveSessionActivity.this, ChatActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }

    private void finished() {
        QAtext.setText("Finished");
        isEnd = true;
        progressBarMap.get(user.getId()).setProgress(1);
        progressBarMap.get(user.getId()).setMax(1);
        flashcardText.setText("You've completed these flashcards! Watch your teammates' progress, or press back to return to the study page.");
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        seeAnswerButton.setText("Leave");
    }

    private void endSession() {
        QAtext.setText("Session end");
        isEnd = true;
        progressBarMap.get(user.getId()).setProgress(1);
        progressBarMap.get(user.getId()).setMax(1);
        flashcardText.setText("The study session has ended! Good job!");
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        seeAnswerButton.setText("Leave");
        WebSocketManager1.getInstance().disconnectWebSocket(false);
    }

    private void setQuestion(int i, String side, long uid) {
        if (user.getId() == uid) {
            if (side.equals("question")) {
                isQuestion = true;
                QAtext.setText("Question");
                flashcardText.setText(flashcardList.get(currentQuestionIndex).getQuestion());
                if (i != currentQuestionIndex || i == 0) {
                    currentQuestionIndex = i;
                    incorrectButton.setVisibility(View.INVISIBLE);
                    correctButton.setVisibility(View.INVISIBLE);
                    seeAnswerButton.setText("See answer");
                    userProgressBar.setMax(flashcardList.size());
                    userProgressBar.setProgress(i);
                }

            } else {
                isQuestion = false;
                QAtext.setText("Answer");
                seeAnswerButton.setText("Flip card");
                flashcardText.setText(flashcardList.get(currentQuestionIndex).getAnswer());
                incorrectButton.setVisibility(View.VISIBLE);
                correctButton.setVisibility(View.VISIBLE);
            }
        }
    }

    private void updateProgress(int progress, int questions, long uid) {
        progressBarMap.get(uid).setMax(questions);
        progressBarMap.get(uid).setProgress(progress);
    }

    @Override
    public void onWebSocketMessage(String message) {
        /**
         * In Android, all UI-related operations must be performed on the main UI thread
         * to ensure smooth and responsive user interfaces. The 'runOnUiThread' method
         * is used to post a runnable to the UI thread's message queue, allowing UI updates
         * to occur safely from a background or non-UI thread.
         */
        runOnUiThread(() -> {
            try {
                JSONArray jsonArray = new JSONArray(message);
                for (int i = 0; i < jsonArray.length(); i++) {
                    parseMessage(jsonArray.getJSONObject(i));
                }
            } catch (JSONException jsonException) {
                throw new RuntimeException(jsonException);
            }
        });
    }

    private void parseMessage(JSONObject message) {
        try {
            switch (message.getString("type")) {
                case "cardstate":
                    setQuestion(message.getInt("question"), message.getString("side"), message.getLong("user"));
                    updateProgress(message.getInt("progress"), message.getInt("max"), message.getLong("user"));
                    return;
                case "userprogress":
                    updateProgress(message.getInt("progress"), message.getInt("max"), message.getLong("user"));
                    return;
                case "question":
                    flashcardList.add(QuestionObject.getQuestionFromJSON(message));
                    return;
                case "start":
                    isStart = true;
                    topSessionInfo.setText("Activity:");
                    setQuestion(0, "question",  user.getId());
                    Log.i("Flashcards", "Flaschard size:" + flashcardList.size());
                    seeAnswerButton.setText("See answer");
                    sessionInfo.setVisibility(View.INVISIBLE);
                    topSessionInfo.setText("");
                    bottomSessionInfo.setText("");

                    userProgressBar.setVisibility(View.VISIBLE);
                    if (progressBarIndex > 1) {
                        user1View.setVisibility(View.VISIBLE);
                    }
                    if (progressBarIndex > 2) {
                        user2View.setVisibility(View.VISIBLE);
                    }
                    if (progressBarIndex > 3) {
                        user3View.setVisibility(View.VISIBLE);
                    }
                    return;
                case "sessionended":
                    endSession();
                    return;
                case "finished":
                    finished();
                    return;
                case "newmember":
                    UserObject userObject = UserObject.getUserFromJSON(message.getJSONObject("user"));
                    usersById.put(userObject.getId(), userObject);
                    if (userObject.getId() != user.getId()) {
                        bottomSessionInfo.setText(bottomSessionInfo.getText() + "\n" + userObject.getUserName());
                    }
                    switch (progressBarIndex) {
                        case 0:
                            progressBarMap.put(userObject.getId(), userProgressBar);
                            break;
                        case 1:
                            progressBarMap.put(userObject.getId(), progressBar1);
                            break;
                        case 2:
                            progressBarMap.put(userObject.getId(), progressBar2);
                            break;
                        case 3:
                            progressBarMap.put(userObject.getId(), progressBar3);
                            break;
                    }
                    progressBarIndex++;
                    return;
            }
        } catch (JSONException jsonException) {
            throw new RuntimeException("Error with" + message, jsonException);
        }

    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Intent intent = new Intent(CompetitiveSessionActivity.this, StudyActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Intent intent = new Intent(CompetitiveSessionActivity.this, StudyActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }
}
