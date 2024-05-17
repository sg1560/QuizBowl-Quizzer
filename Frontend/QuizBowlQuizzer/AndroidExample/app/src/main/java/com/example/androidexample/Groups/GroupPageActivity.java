package com.example.androidexample.Groups;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.androidexample.Chat.ChatActivity;
import com.example.androidexample.EditQuestions.EditQuestionsActivity;
import com.example.androidexample.MainActivity;
import com.example.androidexample.R;
import com.example.androidexample.Study.StudyActivity;
import com.example.androidexample.User.UserObject;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

public class GroupPageActivity extends AppCompatActivity implements Serializable {
    private Button createGroup, coach, listAll, deleteGroup;
    private TextView welcome;
    private RequestQueue queue;
    private UserObject user;

    private BottomNavigationView navBar;
    private String username, password, url;
    private long teamID = -1;
    ListView listView;
    ArrayList<String> mTitle = new ArrayList<String>();
    ArrayList<String> mDescription = new ArrayList<String>();
    ArrayList<Bitmap> mImages = new ArrayList<Bitmap>();
    MyAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups_layout);
        user = (UserObject) getIntent().getSerializableExtra("USER");
        username = user.getUserName();
        password = user.getPassword();
        if (user.getGroupID() == 0) {
            Intent intent = new Intent(GroupPageActivity.this, GroupJoinActivity.class);
            intent.putExtra("USER", user);
            startActivity(intent);
        }
        coach = findViewById(R.id.coachButton);
        coach.setVisibility(View.INVISIBLE);
        createGroup = findViewById(R.id.createGroupButton);
        createGroup.setVisibility(View.INVISIBLE);
        welcome = findViewById(R.id.welcome);
        //joinGroup = findViewById(R.id.joinGroupButton);
        listAll = findViewById(R.id.listAllGroupsButton);
        deleteGroup = findViewById(R.id.deleteGroup);
        deleteGroup.setText("Leave group");
        queue = Volley.newRequestQueue(this);
        listView = findViewById(R.id.groupListView);
        navBar = findViewById(R.id.bottomNavigationView);
        // now create an adapter class


        navBar.setSelectedItemId(R.id.nav_groups);
        adapter = new MyAdapter(this, mTitle, mDescription, mImages);
        listView.setAdapter(adapter);
        url = MainActivity.httpServerAddress + "/users/login?username=" + username + "&password=" + password;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("teams");
                    JSONObject teams = jsonArray.getJSONObject(0);
                    String teamName = teams.getString("name");
                    teamID = teams.getInt("tid");
                    welcome.append("Welcome back to team " + teamName + "!");
                    onSuccess(url);
                    JsonObjectRequest request1 = new JsonObjectRequest(Request.Method.GET, MainActivity.httpServerAddress + "/coach/getById/" + teamID, null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject jsonObject) {
                            try {
                                if(jsonObject.getString("username").equals(user.getUserName())){
                                    user.makeCoach();
                                    coach.setVisibility(View.VISIBLE);
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            volleyError.printStackTrace();
                        }
                    });
                    queue.add(request1);
                } catch (JSONException e) {
                    welcome.append("You are not currently in a team");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Toast.makeText(getApplicationContext(), "Connection error!", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(request);

        createGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupPageActivity.this, GroupCreationActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        coach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPageActivity.this, CoachActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        listAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupPageActivity.this, ListAllGroupsActivity.class);
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });
        navBar.setOnItemSelectedListener(item -> {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.nav_home:
                    intent = new Intent(GroupPageActivity.this, MainActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_edit:
                    intent = new Intent(GroupPageActivity.this, EditQuestionsActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_study:
                    intent = new Intent(GroupPageActivity.this, StudyActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
                case R.id.nav_groups:
                    break;
                case R.id.nav_chat:
                    intent = new Intent(GroupPageActivity.this, ChatActivity.class);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    break;
            }
            return true;
        });

        deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url = MainActivity.httpServerAddress + "/teams/getbyid/" + teamID + "/removeuser/" + user.getId();
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Toast.makeText(getApplicationContext(), "Team left", Toast.LENGTH_SHORT).show();
                        user.joinGroup(0);
                        Intent intent = new Intent(GroupPageActivity.this, MainActivity.class);
                        intent.putExtra("USER", user);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                queue.add(request);
            }
        });
    }

    private void onSuccess(String url) {
        url = MainActivity.httpServerAddress + "/teams/getbyid/" + teamID;
        JsonObjectRequest request2 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
//                    if(response.getJSONObject("coach").equals(user.getUserName()) || user.isAdmin()){
//                        Intent intent = new Intent(GroupPageActivity.this, CoachActivity.class);
//                        intent.putExtra("USER", user);
//                        startActivity(intent);
//                    }
//                    memberList.append("The coach of this group is " + response.getJSONObject("coach").getString("userName") + "\n\n");
                    JSONArray jsonArray = response.getJSONArray("members");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject member = jsonArray.getJSONObject(i);
                        //int userNum = i + 1;
                        //memberList.append("Member #" + userNum + " is " + member.getString("userName") + "\n\n");
                        mTitle.add(member.getString("userName"));
                        int id = member.getInt("cid");
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, MainActivity.httpServerAddress + "/users/getbyid/" + id + "/getquestioncorrectpercentage", new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Scanner scan = new Scanner(s);
                                scan.useDelimiter("\"");
                                String test = scan.next();
                                mDescription.add(test);
                                scan.close();
                                adapter.notifyDataSetChanged();
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                volleyError.printStackTrace();
                            }
                        });
                        queue.add(stringRequest);
                        ImageRequest imageRequest = new ImageRequest(MainActivity.httpServerAddress + "/image/getbyid/" + id, new Response.Listener<Bitmap>() {
                            @Override
                            public void onResponse(Bitmap bitmap) {
                                mImages.add(bitmap);
                                adapter.notifyDataSetChanged();
                            }
                        }, 0, // Width, set to 0 to get the original width
                                0, // Height, set to 0 to get the original height
                                ImageView.ScaleType.FIT_XY, // ScaleType
                                Bitmap.Config.RGB_565, // Bitmap config
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError volleyError) {
                                        Bitmap bitmapFromDrawable = getBitmapFromDrawable(GroupPageActivity.this, R.drawable.profilepicturesample);
                                        mImages.add(bitmapFromDrawable);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                        queue.add(imageRequest);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getApplicationContext(), "Failed to get team!", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
               Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT).show();
                volleyError.printStackTrace();
            }
        });
        queue.add(request2);
    }
    private static Bitmap getBitmapFromDrawable(Context context, int drawableId) {
        return BitmapFactory.decodeResource(context.getResources(), drawableId);
    }

    class MyAdapter extends ArrayAdapter<String> {

        Context context;
        ArrayList<String> rTitle;
        ArrayList<String> rDescription;
        ArrayList<Bitmap> rImgs;

        MyAdapter(Context c, ArrayList<String> title, ArrayList<String> description, ArrayList<Bitmap> imgs) {
            super(c, R.layout.row, R.id.usernameGroup, title);
            this.context = c;
            this.rTitle = title;
            this.rDescription = description;
            this.rImgs = imgs;

        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View row = layoutInflater.inflate(R.layout.row, parent, false);
            ImageView images = row.findViewById(R.id.groupPFPImage);
            TextView myTitle = row.findViewById(R.id.usernameGroup);
            TextView myDescription = row.findViewById(R.id.overallGroup);

            // now set our resources on views
            if(rImgs.size()-1>=position&&rImgs.size()!=0) {
                images.setImageBitmap(rImgs.get(position));
            }
            if(rTitle.size()-1>=position&&rTitle.size()!=0) {
                myTitle.setText(rTitle.get(position));
            }
            if(rDescription.size()-1>=position&&rDescription.size()!=0) {
                myDescription.setText(rDescription.get(position)+"% correct overall");
            }
            return row;
        }
    }
}
