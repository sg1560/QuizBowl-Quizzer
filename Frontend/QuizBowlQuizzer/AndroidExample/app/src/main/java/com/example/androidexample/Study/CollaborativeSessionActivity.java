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


public class CollaborativeSessionActivity extends AppCompatActivity implements WebSocketListener {

    private TextView QAtext, flashcardText, topSessionInfo, bottomSessionInfo;
    private Button seeAnswerButton, correctButton, incorrectButton;
    private ArrayList<QuestionObject> flashcardList;
    private int currentQuestionIndex;
    private boolean isQuestion, isStart, isEnd, isSolo;
    private UserObject user;
    private HashMap<Long, UserObject> usersById;
    private BottomNavigationView navBar;
    private ProgressBar progressBar;
    private ConstraintLayout sessionInfo;
    private final String URL_START = MainActivity.wsServerAddress + "/session/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (UserObject) getIntent().getSerializableExtra("USER");
        isSolo = getIntent().getBooleanExtra("Solo", false);
        String type = getIntent().getStringExtra("SessionType");
        String URL = URL_START + type + "/" + user.getId() + "/team/" + user.getGroupID() + "/size/5/category/" + getIntent().getStringExtra("Category");
        Log.i("Websocket", URL);
        WebSocketManager1.getInstance().setWebSocketListener(CollaborativeSessionActivity.this);
        WebSocketManager1.getInstance().connectWebSocket(URL);

        setContentView(R.layout.activity_flashcards);

        QAtext = findViewById(R.id.QAtext);
        flashcardText = findViewById(R.id.flashcard);
        seeAnswerButton = findViewById(R.id.btnSeeAnswer);
        correctButton = findViewById(R.id.btnCorrect);
        incorrectButton = findViewById(R.id.btnIncorrect);
        navBar = findViewById(R.id.bottomNavigationView);
        topSessionInfo = findViewById(R.id.topSessionInfo);
        bottomSessionInfo = findViewById(R.id.bottomSessionInfo);
        progressBar = findViewById(R.id.progressBarUser);
        sessionInfo = findViewById(R.id.sessionWrapper);

        usersById = new HashMap<>();
        if (isSolo) {
            usersById.put(user.getId(), user);
        }
        navBar.setSelectedItemId(R.id.nav_study);
        flashcardList = new ArrayList<QuestionObject>();
        isQuestion = true;
        isStart = false;
        isEnd = false;
        currentQuestionIndex = 0;
        QAtext.setText("");
        flashcardText.setText("Press start to begin!");
        seeAnswerButton.setText("Start");

        progressBar.setVisibility(View.INVISIBLE);
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        if (isSolo) {
            sessionInfo.setVisibility(View.INVISIBLE);
        }
        ArrayList<String> list = new ArrayList<>();
        list.add("Test");
        list.remove("Test");
        seeAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isStart) {
                    WebSocketManager1.getInstance().sendMessage("start");
                    seeAnswerButton.setText("See answer");
                    bottomSessionInfo.setText("");
                }
                if (isEnd) {
                    Intent intent = new Intent(CollaborativeSessionActivity.this, StudyActivity.class);
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
                    intent = new Intent(CollaborativeSessionActivity.this, MainActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    intent = new Intent(CollaborativeSessionActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    intent = new Intent(CollaborativeSessionActivity.this, StudyActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    intent = new Intent(CollaborativeSessionActivity.this, GroupPageActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
                case R.id.nav_chat:
                    intent = new Intent(CollaborativeSessionActivity.this, ChatActivity.class);
                    intent.putExtra("USER", user);
                    WebSocketManager1.getInstance().disconnectWebSocket(false);
                    startActivity(intent);
                    break;
            }
            return true;
        });
    }

    private void endSession() {
        QAtext.setText("Session end");
        isEnd = true;
        flashcardText.setText("You've completed these flashcards! Press back to study another category");
        correctButton.setVisibility(View.INVISIBLE);
        incorrectButton.setVisibility(View.INVISIBLE);
        seeAnswerButton.setText("Leave");
        progressBar.setMax(flashcardList.size());
        progressBar.setProgress(flashcardList.size());
        WebSocketManager1.getInstance().sendMessage("ondisconnect");
        WebSocketManager1.getInstance().disconnectWebSocket(false);
    }

    private void setQuestion(int i, String side, long uid) {
        String userName = "";
        if (uid != -1) {
            userName = usersById.get(uid).getUserName();
        }

        if (side.equals("question")) {
            isQuestion = true;
            if (i != currentQuestionIndex || i == 0) {
                currentQuestionIndex = i;
                incorrectButton.setVisibility(View.INVISIBLE);
                correctButton.setVisibility(View.INVISIBLE);
                seeAnswerButton.setText("See answer");
                progressBar.setMax(flashcardList.size());
                progressBar.setProgress(i);
                if (i != 0) {
                    bottomSessionInfo.setText(userName + " chose an answer");
                }
            } else {
                bottomSessionInfo.setText(userName + " flipped the card");
            }
            QAtext.setText("Question");
            flashcardText.setText(flashcardList.get(currentQuestionIndex).getQuestion());
        } else {
            isQuestion = false;
            QAtext.setText("Answer");
            seeAnswerButton.setText("Flip card");
            bottomSessionInfo.setText(userName + " flipped the card");
            flashcardText.setText(flashcardList.get(currentQuestionIndex).getAnswer());
            incorrectButton.setVisibility(View.VISIBLE);
            correctButton.setVisibility(View.VISIBLE);
        }
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
                    return;
                case "question":
                    flashcardList.add(QuestionObject.getQuestionFromJSON(message));
                    return;
                case "start":
                    isStart = true;
                    topSessionInfo.setText("Activity:");
                    progressBar.setVisibility(View.VISIBLE);
                    setQuestion(0, "question", -1);
                    Log.i("Flashcards", "Flaschard size:" + flashcardList.size());
                    return;
                case "sessionended":
                    endSession();
                    return;
                case "newmember":
                    UserObject userObject = UserObject.getUserFromJSON(message.getJSONObject("user"));
                    usersById.put(userObject.getId(), userObject);
                    if (userObject.getId() != user.getId()) {
                        bottomSessionInfo.setText(bottomSessionInfo.getText() + "\n" + userObject.getUserName());
                    }
                    return;

            }
        } catch (JSONException jsonException) {
            throw new RuntimeException("Error with" + message, jsonException);
        }

    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        Intent intent = new Intent(CollaborativeSessionActivity.this, StudyActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onWebSocketError(Exception ex) {
        Intent intent = new Intent(CollaborativeSessionActivity.this, StudyActivity.class);
        intent.putExtra("USER", user);
        startActivity(intent);
    }
}
