package com.example.androidexample.Chat;

import android.content.Intent;
import android.os.Bundle;
import android.system.ErrnoException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidexample.EditQuestions.QuestionListAdapter;
import com.example.androidexample.EditQuestions.QuestionObject;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.User.UserObject;
import com.example.androidexample.Networking.WebSocketListener;
import com.example.androidexample.Networking.WebSocketManager;

import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ChatActivity extends AppCompatActivity implements WebSocketListener {

    private ImageButton sendBtn, homeBtn;
    private EditText msgEtx;
    private TextView numOnline;
    private ListView listView;
    private ChatListAdapter adapter;
    private UserObject user;
    private HashMap<Long, UserObject> usersById;
    private String BASE_URL = "/chat/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        user = (UserObject) getIntent().getSerializableExtra("USER");

        sendBtn = (ImageButton) findViewById(R.id.bt2);
        msgEtx = (EditText) findViewById(R.id.et2);
        listView = (ListView) findViewById(R.id.chatList);
        adapter = new ChatListAdapter(this, user, new ArrayList<>());
        listView.setAdapter(adapter);
        homeBtn = findViewById(R.id.bt0);
        numOnline = findViewById(R.id.howManyOnlineTxt);
        usersById = new HashMap<>();

        String serverUrl = MainActivity.wsServerAddress + BASE_URL + user.getId() + "/" + user.getGroupID();
        // Establish WebSocket connection and set listener
        WebSocketManager.getInstance().setWebSocketListener(ChatActivity.this);
        WebSocketManager.getInstance().connectWebSocket(serverUrl);
        numOnline.setText(usersById.size() + " online");

        /* send button listener */
        sendBtn.setOnClickListener(v -> {
            try {
                // send message
                WebSocketManager.getInstance().sendMessage(msgEtx.getText().toString());
                msgEtx.setText("");
            } catch (Exception e) {
                Log.d("ExceptionSendMessage:", e.getMessage().toString());
            }
        });

        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, MainActivity.class);
                WebSocketManager.getInstance().disconnectWebSocket(false);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
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
                    try {
                        parseMessage(jsonArray.getJSONObject(i));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }  catch(JSONException e){
                throw new RuntimeException(e);
            }
        });
    }

    private void parseMessage(JSONObject message) throws JSONException {
        if (message.has("type")) {
            String type = message.getString("type");
            if (type.equals("newmessage")) {
                UserObject client = usersById.get(message.getLong("client"));
                String textBody = message.getString("text");
                adapter.add(new ChatObject(client, textBody));
            } else if (type.equals("leftchat")) {
                UserObject client = UserObject.getUserFromJSON(message.getJSONObject("client"));
                usersById.remove(client.getId());
                numOnline.setText(usersById.size() + " online");
            }
        } else if (message.has("cid")) {
            UserObject newClient = UserObject.getUserFromJSON(message);
            usersById.put(newClient.getId(), newClient);
            numOnline.setText(usersById.size() + " online");
        }
    }

    @Override
    public void onWebSocketClose(int code, String reason, boolean remote) {
        String closedBy = remote ? "server" : "local";
        runOnUiThread(() -> {
            Log.d("Chat", "closing chat " + closedBy);
//            String s = msgTv.getText().toString();
//            msgTv.setText(s + "---\nconnection closed by " + closedBy + "\nreason: " + reason);
            if (remote) {
                homeBtn.callOnClick();
            }
        });
    }

    @Override
    public void onWebSocketOpen(ServerHandshake handshakedata) {
    }

    @Override
    public void onWebSocketError(Exception ex) {
        throw new Error(ex);
    }
}
