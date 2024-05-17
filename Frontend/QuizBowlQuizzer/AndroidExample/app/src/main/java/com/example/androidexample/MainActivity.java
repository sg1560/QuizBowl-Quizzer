package com.example.androidexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.Groups.GroupPageActivity;
import com.example.androidexample.Networking.VolleySingleton;
import com.example.androidexample.Networking.VolleySingleton2;
import com.example.androidexample.Study.StudyActivity;
import com.example.androidexample.User.UserObject;
import com.example.androidexample.User.UserProfileActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, Serializable {

    private ProgressBar totalProgress, sciProgress, histProgress, popProgress, fineArtProgress, geoProgress;
    private BottomNavigationView navBar;
    private TextView totalProgressText, hello, sciProgressText, histProgressText, popProgressText, fineArtProgressText, geoProgressText;
    private final String URL_END = "/getquestioncorrectpercentage?category=";

    private final String URL_END_2 = "/getquestioncorrectpercentage";
    /**
     * The address of the target server for http, please only change this one variable
     */
    public static final String httpServerAddress = "http://coms-309-030.class.las.iastate.edu:8080";
    /**
     * The address of the target server for https, please only change this one variable
     */
    public static final String wsServerAddress = "ws://coms-309-030.class.las.iastate.edu:8080";
    //http://coms-309-030.class.las.iastate.edu:8080
    //http://10.0.2.2:8080 for local server not in android VM
    //"ws://10.0.2.2:8080";
    private final String URL_BASE = "/users/getbyid/";


    private UserObject user;

    private ImageButton profilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user = (UserObject) getIntent().getSerializableExtra("USER");
        RequestQueue queue = Volley.newRequestQueue(this);


        hello = findViewById(R.id.greeting);

        sciProgress = findViewById(R.id.sciProgressBar);
        histProgress = findViewById(R.id.historyProgressBar);
        popProgress = findViewById(R.id.popProgressBar);
        fineArtProgress = findViewById(R.id.fineArtProgressBar);
        geoProgress = findViewById(R.id.geoProgressBar);
        totalProgress = findViewById(R.id.totalProgressBar);

        sciProgressText = findViewById(R.id.sciProgressText);
        histProgressText = findViewById(R.id.historyProgressText);
        popProgressText = findViewById(R.id.popProgressText);
        fineArtProgressText = findViewById(R.id.fineArtProgressText);
        geoProgressText = findViewById(R.id.geoProgressText);
        totalProgressText = findViewById(R.id.totalProgressText);

        profilePicture = findViewById(R.id.profilePicture);
        navBar = findViewById(R.id.bottomNavigationView);


        hello.append(user.getUserName() + "!");

        /* button click listeners */
        profilePicture.setOnClickListener(this);
        navBar.setSelectedItemId(R.id.nav_home);

        makeImageRequest();

        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    break;
                case R.id.nav_edit:
                    intent = new Intent(MainActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    intent = new Intent(MainActivity.this, StudyActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    intent = new Intent(MainActivity.this, GroupPageActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_chat:
                    intent = new Intent(MainActivity.this, ChatActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        makeStringReq(getCategoryId("Science"));
        makeStringReq(getCategoryId("Geography"));

        makeStringReq(getCategoryId("Fine Arts"));
        makeStringReq(getCategoryId("Pop Culture"));
        makeStringReq(getCategoryId("History"));
        makeStringReq(0);

    }

    private void handleResponse(String accuracy, long category) {
        float f;
        int acc;
        if (!accuracy.equals("\"NaN\"")) {
            if (category == getCategoryId("Science")) {
                f = Float.parseFloat(accuracy);
                acc = (int) f * 100;
                sciProgressText.setText(acc + "%");
                sciProgress.setProgress(acc);
            } else if (category == getCategoryId("Geography")) {
                f = Float.parseFloat(accuracy);
                acc = (int) (f * 100);
                geoProgressText.setText(acc + "%");
                geoProgress.setProgress(acc);
            } else if (category == getCategoryId("Pop Culture")) {
                f = Float.parseFloat(accuracy);
                acc = (int) (f * 100);
                popProgressText.setText(acc + "%");
                popProgress.setProgress(acc);
            } else if (category == getCategoryId("Fine Arts")) {
                f = Float.parseFloat(accuracy);
                acc = (int) (f * 100);
                fineArtProgressText.setText(acc + "%");
                fineArtProgress.setProgress(acc);
            } else if (category == getCategoryId("History")) {
                f = Float.parseFloat(accuracy);
                acc = (int) (f * 100);
                histProgressText.setText(acc + "%");
                histProgress.setProgress(acc);
            } else {
                f = Float.parseFloat(accuracy);
                acc = (int) (f * 100);
                totalProgressText.setText(acc + "%");
                totalProgress.setProgress(acc);
            }
        }
    }

    private void makeStringReq(long category) {
        String URL;
        if (category > 0) {
            URL = httpServerAddress + URL_BASE + user.getId() + URL_END + category;
        } else {
            URL = httpServerAddress + URL_BASE + user.getId() + URL_END_2;
        }
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Volley Response", response);
                        handleResponse(response, category);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur during the request
                        Log.e("Volley Error", error.toString());
                        String body;
                        //get status code here
                        // final String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        try {
                            body = new String(error.networkResponse.data, "UTF-8");
                            Log.e("Volley Error", body);
                        } catch (UnsupportedEncodingException e) {
                            Log.e("Unsupported", "bad");
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
//                headers.put("Authorization", "Bearer YOUR_ACCESS_TOKEN");
//                headers.put("Content-Type", "application/json");
                return headers;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
//                params.put("param1", "value1");
//                params.put("param2", "value2");
                return params;
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public static long getCategoryId(String category) {
        switch (category) {
            case "History":
                return 1;
            case "Pop Culture":
                return 2;
            case "Science":
                return 3;
            case "Fine Arts":
                return 4;
            case "Geography":
                return 5;
            case "All":
                return 0;
            default:
                return 5;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.profilePicture) {
            Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
            intent.putExtra("USER", user);
            startActivity(intent);
        }
    }

    private void makeImageRequest() {

        ImageRequest imageRequest = new ImageRequest(
                MainActivity.httpServerAddress + "/image/getbyid/" + user.getId(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        // Display the image in the ImageView
                        profilePicture.setImageBitmap(response);
                    }
                },
                0, // Width, set to 0 to get the original width
                0, // Height, set to 0 to get the original height
                ImageView.ScaleType.FIT_XY, // ScaleType
                Bitmap.Config.RGB_565, // Bitmap config

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle errors here
                        Log.e("Volley Error", error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton2.getInstance(getApplicationContext()).addToRequestQueue(imageRequest);
    }

}